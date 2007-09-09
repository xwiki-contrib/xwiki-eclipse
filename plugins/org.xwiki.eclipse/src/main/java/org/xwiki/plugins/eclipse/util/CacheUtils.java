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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.eclipse.core.runtime.IPath;
import org.xwiki.plugins.eclipse.Activator;

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
     * Caches the given object into local repository.
     * 
     * @param cacheable Object to be cached.
     */
    public static void updateCache(ICacheable cacheable)
    {
        try {
            ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(cacheable.getCachePath()
                    .addFileExtension("cache").toFile()));
            oos.writeObject(cacheable);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO What should happen here ?
        }
    }

    /**
     * Removes all cached objects relevant to this Cacheable.
     * 
     * @param cacheable Cacheable to be wiped.
     */
    public static void clearCache(ICacheable cacheable)
    {
        File cacheFile = cacheable.getCachePath().addFileExtension("cache").toFile();
        File dataCacheDirectory = cacheable.getCachePath().toFile();
        if (cacheFile.exists()) {
            cacheFile.delete();
        }
        if (dataCacheDirectory.exists() && dataCacheDirectory.isDirectory()) {
            removeDirectory(dataCacheDirectory);
        }
    }
    
    /**
     * Utility method for removing a non-empty directory.
     * @param dir Directory to be removed
     * @return Success or otherwise
     */
    private static boolean removeDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = removeDirectory(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }            
        return dir.delete();
    }
}
