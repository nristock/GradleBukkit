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

package net.monofraps.gradlebukkit.models;

import com.google.gson.JsonObject;

/**
 * Java representation of JSON object returned by HTTP API requests.
 *
 * @author monofraps
 */
public class BuildArtifact implements Comparable<BuildArtifact>
{
    private String       brokenReason;
    private int          buildNumber;
    private String       dateCreated;
    private String       url;
    private boolean      isBroken;
    private String       htmlUrl;
    private String       version;
    private ArtifactFile file;
    private String       gitCommitHash;
    private String       channel;

    private BuildArtifact()
    {

    }

    public static BuildArtifact fromJsonObject(final JsonObject json)
    {
        final BuildArtifact buildArtifact = new BuildArtifact();

        buildArtifact.setBrokenReason(json.get("broken_reason").getAsString());
        buildArtifact.setBuildNumber(json.get("build_number").getAsInt());
        buildArtifact.setDateCreated(json.get("created").getAsString());
        buildArtifact.setUrl(json.get("url").getAsString());
        buildArtifact.setBroken(json.get("is_broken").getAsBoolean());
        buildArtifact.setHtmlUrl(json.get("html_url").getAsString());
        buildArtifact.setVersion(json.get("version").getAsString());
        buildArtifact.setFile(ArtifactFile.fromJsonObject(json.get("file").getAsJsonObject()));
        buildArtifact.setGitCommitHash(json.get("commit").getAsJsonObject().get("ref").getAsString());
        buildArtifact.setChannel(json.get("channel").getAsJsonObject().get("slug").getAsString());

        return buildArtifact;
    }

    public String getBrokenReason()
    {
        return brokenReason;
    }

    public void setBrokenReason(final String brokenReason)
    {
        this.brokenReason = brokenReason;
    }

    public int getBuildNumber()
    {
        return buildNumber;
    }

    public void setBuildNumber(final int buildNumber)
    {
        this.buildNumber = buildNumber;
    }

    public String getDateCreated()
    {
        return dateCreated;
    }

    public void setDateCreated(final String dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(final String url)
    {
        this.url = url;
    }

    public boolean isBroken()
    {
        return isBroken;
    }

    public void setBroken(final boolean isBroken)
    {
        this.isBroken = isBroken;
    }

    public String getHtmlUrl()
    {
        return htmlUrl;
    }

    public void setHtmlUrl(final String htmlUrl)
    {
        this.htmlUrl = htmlUrl;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(final String version)
    {
        this.version = version;
    }

    public String getGitCommitHash()
    {
        return gitCommitHash;
    }

    public void setGitCommitHash(final String gitCommitHash)
    {
        this.gitCommitHash = gitCommitHash;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(final String channel)
    {
        this.channel = channel;
    }

    public ArtifactFile getFile()
    {
        return file;
    }

    public void setFile(final ArtifactFile file)
    {
        this.file = file;
    }

    @Override
    public int compareTo(final BuildArtifact o)
    {
        if (o.getBuildNumber() > getBuildNumber())
        {
            return -1;
        }
        else if (o.getBuildNumber() < getBuildNumber())
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}
