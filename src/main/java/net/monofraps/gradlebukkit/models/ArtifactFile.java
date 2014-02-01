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
 * Java representation of artifact/build file object representation of JSON object returned by HTTP API requests.
 *
 * @author monofraps
 */
public class ArtifactFile
{
    private String url;
    private String md5;
    private int    size;

    private ArtifactFile()
    {

    }

    public static ArtifactFile fromJsonObject(final JsonObject json)
    {
        final ArtifactFile file = new ArtifactFile();

        file.setUrl(json.get("url").getAsString());
        file.setMd5(json.get("checksum_md5").getAsString());
        file.setSize(json.get("size").getAsInt());

        return file;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(final String url)
    {
        this.url = url;
    }

    public String getMd5()
    {
        return md5;
    }

    public void setMd5(final String md5)
    {
        this.md5 = md5;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(final int size)
    {
        this.size = size;
    }
}
