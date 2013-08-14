/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.eclipse.storage.rest;

/**
 * Represents hint data (name of hint and description of hint).
 *
 * @version $Id$
 */
public class HintData
{
    /**
     * @see #getName()
     */
    private String name;

    /**
     * @see #getDescription()
     */
    private String description;

    /**
     * @param name see {@link #getName()}
     * @param description see {@link #getDescription()}
     */
    public HintData(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    /**
     * @return the hint description
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * @return the hint name (ie what will get inserted if the user picks it)
     */
    public String getName()
    {
        return this.name;
    }

    @Override
    public String toString()
    {
        return String.format("name = [%s], description = [%s]", getName(), getDescription());
    }
}