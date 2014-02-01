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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import net.monofraps.gradlebukkit.DebuggingTransport;

/**
 * @author monofraps
 */
public class RemoteDebugging
{
    /**
     * Template for JVM arguments.
     * $transport$ will be replaced by the transport name.
     * $address$ will be replaced by the address (can either be a port [SOCKET transport] or a memory name [SHARED_MEM transport]).
     */
    private String jvmArguments = "-agentlib:jdwp=transport=$transport$,server=y,suspend=n,address=$address$";

    private String address;

    private DebuggingTransport debuggingTransport;

    public String getJvmArguments()
    {
        jvmArguments = Strings.nullToEmpty(jvmArguments);
        Preconditions.checkArgument(!jvmArguments.isEmpty(), "Parameter jvmArguments must not be null nor empty.");

        return jvmArguments.replace("$transport$", getDebuggingTransport().toString()).replace("$address$", address);
    }

    public void setJvmArguments(final String jvmArguments)
    {
        this.jvmArguments = jvmArguments;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(final String address)
    {
        this.address = address;
    }

    public DebuggingTransport getDebuggingTransport()
    {
        return debuggingTransport;
    }

    public void setDebuggingTransport(final DebuggingTransport debuggingTransport)
    {
        this.debuggingTransport = debuggingTransport;
    }
}
