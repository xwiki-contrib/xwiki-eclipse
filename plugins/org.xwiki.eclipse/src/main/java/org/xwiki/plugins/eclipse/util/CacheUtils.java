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
 *
 */

package org.xwiki.plugins.eclipse.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;

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
        return XWikiEclipsePlugin.getDefault().getStateLocation();
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
     * Reads the cache.
     * 
     * @param dir Directory under which cache files should be searched.
     * @return A Map of cacheables found along with their cache locations.
     * @throws IOException I/O Problems
     * @throws ClassNotFoundException Class versionning problems
     */
    public static Map<IPath, ICacheable> readCache(File dir) throws IOException,
        ClassNotFoundException
    {
        HashMap<IPath, ICacheable> result = new HashMap<IPath, ICacheable>();
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File f : files) {
                if (f.getName().endsWith(".cache")) {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                    ICacheable cacheable = (ICacheable) ois.readObject();
                    String path = f.getAbsolutePath();
                    path = path.substring(0, path.length() - 6);                    
                    result.put(new Path(path), cacheable);
                }
            }
        }
        return result;
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
     * 
     * @param dir Directory to be removed
     * @return Success or otherwise
     */
    private static boolean removeDirectory(File dir)
    {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = removeDirectory(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
