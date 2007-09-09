/*
 * Copyright 2006-2007, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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
 *
 */

package org.xwiki.plugins.eclipse.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.eclipse.core.runtime.IPath;
import org.xwiki.plugins.eclipse.Activator;
import org.xwiki.plugins.eclipse.model.impl.XWikiConnection;
import org.xwiki.plugins.eclipse.model.impl.XWikiSpace;

/**
 * All utility functions related to local cache management should go here.
 */
public class CacheUtils
{

    /**
     * @return The direcotory into which local cache should be stuffed in.
     */
    public static IPath getMasterCacheDirectory()
    {
        // Return the plugin state location for now.
        return Activator.getDefault().getStateLocation();
    }

    /**
     * Saves the given xwiki connection into local cache.
     * 
     * @param connection Connection to be cached.
     */
    public static void saveConnection(XWikiConnection connection)
    {
        try {
            ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(connection.getCachePath()
                    .addFileExtension("cache").toFile()));
            oos.writeObject(connection);
            oos.close();
        } catch (IOException e) {
            // TODO What should happen here ?
        }
    }

    /**
     * Saves the given xwiki space into local cache.
     * 
     * @param space Space to be cached.
     */
    public static void saveSpace(XWikiSpace space)
    {
        try {
            ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(space.getCachePath()
                    .addFileExtension("cache").toFile()));
            oos.writeObject(space);
            oos.close();
        } catch (IOException e) {
            // TODO What should happen here ?
        }
    }
}
