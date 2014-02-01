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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.monofraps.gradlebukkit.exxtensions.Bukkit;
import net.monofraps.gradlebukkit.models.BuildArtifact;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author monofraps
 */
public class ListAvailableArtifacts extends DefaultTask
{
    /**
     * Channel to query for artifacts.
     * Null will inherit from bukkit project extenison object.
     * Empty will query all channels.
     */
    private String channel;

    /**
     * Number of artifacts to display.
     * Set to -1 to show all artifacts returned by the server.
     */
    private int numberOfArtifactsToDisplay = 3;

    @TaskAction
    public void doWork()
    {
        final String responseString;

        try
        {
            final HttpGet httpGet = new HttpGet("http://dl.bukkit.org/api/1.0/downloads/projects/craftbukkit/artifacts/" + getChannel() + (getChannel().isEmpty() ? "" : "/"));

            final HttpClient httpClient = HttpClientBuilder.create().build();
            final HttpResponse httpResponse = httpClient.execute(httpGet);
            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

            final StringBuilder response = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null)
            {
                response.append(line);
            }

            responseString = response.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        final JsonElement response = (new JsonParser()).parse(responseString);
        final JsonArray jsonArtifacts = response.getAsJsonObject().get("results").getAsJsonArray();
        final List<BuildArtifact> artifacts = new ArrayList<>();

        for (final JsonElement versionObject : jsonArtifacts)
        {
            artifacts.add(BuildArtifact.fromJsonObject(versionObject.getAsJsonObject()));
        }
        Collections.sort(artifacts);

        for (final BuildArtifact artifact : (numberOfArtifactsToDisplay == -1) ? artifacts : artifacts.subList(artifacts.size() - numberOfArtifactsToDisplay, artifacts.size()))
        {

            getLogger().lifecycle(String.format("Build %s, version %s, released on %s", artifact.getBuildNumber(), artifact.getVersion(), artifact.getDateCreated()));
            getLogger().lifecycle(String.format("\tReleaseChannel: %s", artifact.getChannel()));
            getLogger().lifecycle(String.format("\tIsBroken: %s", artifact.isBroken() ? artifact.getBrokenReason() : artifact.isBroken()));
            getLogger().lifecycle(String.format("\tDownloadUrl: %s", artifact.getFile().getUrl()));
        }
    }

    public String getChannel()
    {
        if (channel == null)
        {
            return ((Bukkit) getProject().getExtensions().getByName("bukkit")).getChannel();
        }
        return channel;
    }

    public void setChannel(final String channel)
    {
        this.channel = channel;
    }

    public int getNumberOfArtifactsToDisplay()
    {
        return numberOfArtifactsToDisplay;
    }

    public void setNumberOfArtifactsToDisplay(final int numberOfArtifactsToDisplay)
    {
        this.numberOfArtifactsToDisplay = numberOfArtifactsToDisplay;
    }
}
