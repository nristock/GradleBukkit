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

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import groovy.lang.Closure;
import net.monofraps.gradlebukkit.exxtensions.Bukkit;
import net.monofraps.gradlebukkit.models.BuildArtifact;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.mvn3.org.apache.maven.lifecycle.LifecycleExecutionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author monofraps
 */
public class DownloadCraftBukkit extends DefaultTask
{
    /**
     * Artifact slug specifying the version of CraftBukkit to download.
     * Leave null or empty to inherit from bukkit project properties extension.
     */
    private String artifactSlug;

    private File bukkitServerJar;
    private File bukkitTargetDir;

    public DownloadCraftBukkit()
    {
        super();

        getOutputs().upToDateWhen(new UpToDateWhen(this));
        bukkitTargetDir = new File(getProject().getBuildDir(), "bukkit");

        getOutputs().dir(bukkitTargetDir);
    }

    private class UpToDateWhen extends Closure<Boolean>
    {

        private DownloadCraftBukkit owner;

        public UpToDateWhen(DownloadCraftBukkit owner)
        {
            super(owner);
            this.owner = owner;
        }

        @Override
        public Boolean call(Object params)
        {
            try
            {
                return !owner.needsUpdating();
            }
            catch (IOException | LifecycleExecutionException e)
            {
                e.printStackTrace();
            }

            return false;
        }
    }

    public boolean needsUpdating() throws IOException, LifecycleExecutionException
    {
        final BuildArtifact artifact = prepareExecution();
        return artifact != null;

    }

    private BuildArtifact prepareExecution() throws IOException, LifecycleExecutionException
    {
        final BuildArtifact artifact = getArtifactToDownload();

        bukkitServerJar = new File(bukkitTargetDir, "bukkit-" + artifact.getBuildNumber() + ".jar");
        final File bukkitPluginDir = new File(bukkitTargetDir, "plugins");

        if (bukkitServerJar.exists())
        {
            final String localMd5 = DigestUtils.md5Hex(new FileInputStream(bukkitServerJar));
            if (!localMd5.equals(artifact.getFile().getMd5()))
            {
                getLogger().lifecycle("CraftBukkit MD5 sum mismatch. Going to update local copy.");
            }
            else
            {
                getLogger().lifecycle("File exists.");
                return null;
            }
        }

        if (!bukkitTargetDir.exists())
        {
            if (!bukkitTargetDir.mkdir())
                throw new LifecycleExecutionException("Failed to create bukkit server directory " + bukkitTargetDir);
        }

        if (!bukkitPluginDir.exists())
        {
            if (!bukkitPluginDir.mkdir())
                throw new LifecycleExecutionException("Failed to create bukkit plugin directory " + bukkitPluginDir);
        }

        return artifact;
    }


    @TaskAction
    public void doWork() throws IOException, LifecycleExecutionException
    {
        final BuildArtifact artifact = prepareExecution();

        getLogger().lifecycle("I chose CraftBukkit " + artifact.getVersion() + " build " + artifact.getBuildNumber());

        if (artifact == null)
        {
            getLogger().lifecycle("Nothing to be done.");
            return;
        }

        downloadArtifact("http://dl.bukkit.org/" + artifact.getFile().getUrl(), artifact.getFile().getMd5());
    }

    private void downloadArtifact(final String url, final String referenceMd5) throws IOException, LifecycleExecutionException
    {
        getLogger().lifecycle("Going to download CraftBukkit from " + url);

        final HttpGet httpGet = new HttpGet(url);
        final HttpClient httpClient = HttpClientBuilder.create().build();
        final HttpResponse httpResponse = httpClient.execute(httpGet);

        if (!bukkitServerJar.exists())
        {
            if (!bukkitServerJar.createNewFile())
                throw new LifecycleExecutionException("Failed to create bukkit server jar " + bukkitServerJar);
        }

        final HttpEntity httpEntity = httpResponse.getEntity();
        getLogger().lifecycle("Writing " + httpEntity.getContentLength() + " bytes to bukkit.jar");

        final byte[] data = EntityUtils.toByteArray(httpEntity);
        FileUtils.writeByteArrayToFile(bukkitServerJar, data);

        final String localMd5 = DigestUtils.md5Hex(new FileInputStream(bukkitServerJar));
        if (!localMd5.equals(referenceMd5))
        {
            throw new LifecycleExecutionException("bukkit.jar MD5 sum mismatch. - Download failed.");
        }
    }

    private BuildArtifact getArtifactToDownload() throws IOException, LifecycleExecutionException
    {
        final String responseString;

        final String url = "http://dl.bukkit.org/api/1.0/downloads/projects/craftbukkit/view/" + getArtifactSlug() + "/";
        final HttpGet httpGet = new HttpGet(url);

        final HttpClient httpClient = HttpClientBuilder.create().build();
        final HttpResponse httpResponse = httpClient.execute(httpGet);

        if (httpResponse.getStatusLine().getStatusCode() != 200)
        {
            throw new LifecycleExecutionException("Failed to download CraftBukkit: Failed to download version information (HTTP " + httpResponse.getStatusLine().getStatusCode() + ") from " + url);
        }

        final BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

        final StringBuilder response = new StringBuilder();
        String line;
        while ((line = responseReader.readLine()) != null)
        {
            response.append(line);
        }

        responseString = response.toString();

        final JsonElement jsonResponse = (new JsonParser()).parse(responseString);
        return BuildArtifact.fromJsonObject(jsonResponse.getAsJsonObject());
    }

    public String getArtifactSlug()
    {
        return Strings.nullToEmpty(artifactSlug).isEmpty() ? ((Bukkit) getProject().getExtensions().getByName("bukkit")).getArtifactSlug() : artifactSlug;
    }

    public void setArtifactSlug(final String artifactSlug)
    {
        this.artifactSlug = artifactSlug;
    }
}
