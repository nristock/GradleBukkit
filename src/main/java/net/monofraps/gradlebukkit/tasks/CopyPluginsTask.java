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

import net.monofraps.gradlebukkit.extensions.Bukkit;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.mvn3.org.apache.maven.lifecycle.LifecycleExecutionException;

import java.io.File;
import java.io.IOException;

/**
 * @author monofraps
 */
public class CopyPluginsTask extends DefaultTask
{
    @TaskAction
    public void doWork() throws LifecycleExecutionException, IOException
    {
        final File bukkitTargetDir = new File(getProject().getBuildDir(), "bukkit");
        final File bukkitPluginsDir = new File(bukkitTargetDir, "plugins");

        if (!bukkitTargetDir.exists())
        {
            if (!bukkitTargetDir.mkdir())
                throw new LifecycleExecutionException("Failed to create bukkit target directory " + bukkitTargetDir);
        }
        if (!bukkitPluginsDir.exists())
        {
            if (!bukkitPluginsDir.mkdir())
                throw new LifecycleExecutionException("Failed to create bukkit plugin directory " + bukkitPluginsDir);
        }

        for (final Object fileObject : ((Bukkit) getProject().getExtensions().getByName("bukkit")).getPluginsToCopy())
        {
            final File plugin = getProject().file(fileObject);
            if (!plugin.exists())
            {
                throw new LifecycleExecutionException("Plugin copy failed: Plugin file " + plugin + " does not exist.");
            }

            final File target = new File(bukkitPluginsDir, plugin.getName());
            if (target.exists()) continue;

            getLogger().lifecycle("Copying " + plugin + " to " + target);

            if (plugin.isDirectory())
            {
                FileUtils.copyDirectory(plugin, target);
            }
            else
            {
                FileUtils.copyFile(plugin, target);
            }
        }
    }
}
