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

package net.monofraps.gradlebukkit.exxtensions;

import com.google.common.base.Strings;
import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.List;

/**
 * @author monofraps
 */
public class Bukkit
{
    /**
     * The channel to use when looking for CraftBukkit artifacts. Defaults to beta. Set to null or empty to use all
     * available channels.
     */
    private String channel = "beta";

    /**
     * The artifact slug to use when looking for CraftBukkit artifacts.
     * <ul>
     * <li>"latest" - either absolute latest artifact or latest of channel if channel is neither null nor empty</li>
     * <li>"git-[commit_ref]" - use the artifact with the given commit reference</li>
     * <li>"build-[build_number]" - use the artifact with the given build number</li>
     * </ul>
     * Defaults to latest.
     */
    private String artifactSlug = "";

    /**
     * Allows to pass additional arguments to Bukkit's JVM.
     */
    private String additionalJvmArgs = "";

    /**
     * Allows to automatically configure remote debugging.
     */
    private RemoteDebugging debugSettings;

    /**
     * A list of files to copy to Bukkit's plugin directory. Also works with directories.
     */
    private List<Object> pluginsToCopy = new ArrayList<>();

    /**
     * Adds a plugin file to copy.
     *
     * @param fileObject the file to copy (gets passed to Project.file)
     *
     * @return this
     */
    public Bukkit plugin(final Object fileObject)
    {
        pluginsToCopy.add(fileObject);
        return this;
    }

    public Bukkit remoteDebugging(final Closure closure)
    {
        debugSettings = new RemoteDebugging();
        if (closure != null)
        {
            closure.setDelegate(debugSettings);
            closure.setResolveStrategy(Closure.DELEGATE_ONLY);
            closure.run();
        }

        return this;
    }

    public String getChannel()
    {
        return Strings.nullToEmpty(channel);
    }

    public void setChannel(final String channel)
    {
        this.channel = channel;
    }

    public String getArtifactSlug()
    {
        String artSlug = Strings.nullToEmpty(artifactSlug);
        if (artSlug.isEmpty())
        {
            artSlug = "latest";
        }

        if (!getChannel().isEmpty())
        {
            artSlug += "-" + getChannel();
        }

        return artSlug;
    }

    public void setArtifactSlug(final String artifactSlug)
    {
        this.artifactSlug = artifactSlug;
    }

    public List<Object> getPluginsToCopy()
    {
        return pluginsToCopy;
    }

    public String getAdditionalJvmArgs()
    {
        return additionalJvmArgs;
    }

    public void setAdditionalJvmArgs(final String additionalJvmArgs)
    {
        this.additionalJvmArgs = additionalJvmArgs;
    }

    public RemoteDebugging getDebugSettings()
    {
        return debugSettings;
    }

    public void setDebugSettings(final RemoteDebugging debugSettings)
    {
        this.debugSettings = debugSettings;
    }
}
