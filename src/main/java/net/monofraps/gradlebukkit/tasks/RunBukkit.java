/*
 * Copyright (C) 2014 Monofraps
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.monofraps.gradlebukkit.tasks;

import com.google.common.base.Stopwatch;
import net.monofraps.gradlebukkit.exxtensions.Bukkit;
import net.monofraps.gradlebukkit.exxtensions.RemoteDebugging;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.tasks.TaskAction;
import org.gradle.mvn3.org.apache.maven.lifecycle.LifecycleExecutionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author monofraps
 */
public class RunBukkit extends DefaultTask
{
    private Integer findBukkitBuildToRun() throws IOException
    {
        final ConfigurableFileTree bukkitFiles = getProject().fileTree(new File(getProject().getBuildDir(), "bukkit"));
        bukkitFiles.include("bukkit-*.jar");

        int currentLatestBuildNum = -1;
        for (final File bukkitFile : bukkitFiles)
        {
            final String fileName = bukkitFile.getName();
            int buildNum = Integer.parseInt(fileName.substring(fileName.indexOf("-") + 1, fileName.indexOf(".")));
            if (buildNum > currentLatestBuildNum)
            {
                currentLatestBuildNum = buildNum;
            }
        }

        return currentLatestBuildNum;
    }

    @TaskAction
    public void doWork() throws IOException, LifecycleExecutionException, InterruptedException
    {
        final int latestDownloadedBuild = findBukkitBuildToRun();
        if (latestDownloadedBuild < 0)
        {
            throw new LifecycleExecutionException("Couldn't find Bukkit jar to run.");
        }

        final String bukkitJarName = "bukkit-" + latestDownloadedBuild + ".jar";

        final RemoteDebugging debugConfiguration = ((Bukkit) getProject().getExtensions().getByName("bukkit")).getDebugSettings();
        final String debuggingArguments = (debugConfiguration == null) ? "" : debugConfiguration.getJvmArguments();

        final ProcessBuilder bukkitProcessBuilder = new ProcessBuilder("java", debuggingArguments, "-jar", bukkitJarName);
        bukkitProcessBuilder.environment().putAll(System.getenv());
        bukkitProcessBuilder.directory(new File(getProject().getBuildDir(), "bukkit"));

        getLogger().lifecycle("Starting Bukkit...");
        final Process bukkitProcess = bukkitProcessBuilder.start();

        final StreamGrabber errorGrabber = new StreamGrabber(bukkitProcess.getErrorStream());
        final StreamGrabber stdoutGrabber = new StreamGrabber(bukkitProcess.getInputStream());
        errorGrabber.start();
        stdoutGrabber.start();

        final PrintWriter stdinWriter = new PrintWriter(bukkitProcess.getOutputStream());
        String line;
        while ((line = System.console().readLine()) != null && !line.equals("gterm"))
        {
            stdinWriter.write(line);
            stdinWriter.write("\n");
            stdinWriter.flush();

            try
            {
                bukkitProcess.exitValue();
                break;
            }
            catch (final IllegalThreadStateException ignored)
            {
            }
        }

        try
        {
            bukkitProcess.exitValue();
        }
        catch (final IllegalThreadStateException ex)
        {
            final Thread joiner = new Thread()
            {
                @Override
                public void run()
                {
                    bukkitProcess.destroy();
                }
            };

            joiner.start();
            final Stopwatch stopwatch = new Stopwatch();
            stopwatch.start();
            while (joiner.isAlive())
            {
                if (stopwatch.elapsed(TimeUnit.MILLISECONDS) > 60)
                {
                    joiner.interrupt();
                    joiner.join(5000);
                }
                Thread.sleep(500);
            }
            stopwatch.stop();
        }

        getLogger().lifecycle("Bukkit process exited with exit code " + bukkitProcess.exitValue());
    }

    private class StreamGrabber extends Thread
    {
        private final InputStream input;

        private StreamGrabber(final InputStream input)
        {
            this.input = input;
        }

        @Override
        public void run()
        {
            try
            {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    getLogger().lifecycle(line);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            getLogger().lifecycle("Closing Bukkit StreamGrabber...");
        }
    }
}
