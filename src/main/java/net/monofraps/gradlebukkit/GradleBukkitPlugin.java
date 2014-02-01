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

package net.monofraps.gradlebukkit;

import net.monofraps.gradlebukkit.exxtensions.Bukkit;
import net.monofraps.gradlebukkit.tasks.CopyPluginsTask;
import net.monofraps.gradlebukkit.tasks.DownloadCraftBukkit;
import net.monofraps.gradlebukkit.tasks.ListAvailableArtifacts;
import net.monofraps.gradlebukkit.tasks.RunBukkit;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

/**
 * @author monofraps
 */
public class GradleBukkitPlugin implements Plugin<Project>
{
    @Override
    public void apply(final Project target)
    {
        target.getExtensions().create("bukkit", Bukkit.class);
        target.getTasks().create("listAvailableBukkitVersions", ListAvailableArtifacts.class);
        final Task downloadTask = target.getTasks().create("downloadCraftBukkit", DownloadCraftBukkit.class);
        final Task copyPluginsTask = target.getTasks().create("copyBukkitPlugins", CopyPluginsTask.class);
        target.getTasks().create("runBukkit", RunBukkit.class).dependsOn(downloadTask, copyPluginsTask);

    }
}
