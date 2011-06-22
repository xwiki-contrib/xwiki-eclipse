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
package org.xwiki.eclipse.storage.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.xwiki.eclipse.storage.BackendType;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * A class containing utility methods.
 * 
 * @version $Id$
 */
public class StorageUtils
{
    private static XStream xstream = null;

    /**
     * Create a folder and all its parents.
     * 
     * @param folder The folder to be created.
     * @return The created folder.
     * @throws CoreException
     */
    public static IFolder createFolder(IFolder folder) throws CoreException
    {
        if (folder.exists()) {
            return folder;
        }

        IProject project = folder.getProject();
        String[] segments = folder.getProjectRelativePath().segments();

        IPath current = new Path("."); //$NON-NLS-1$
        for (String segment : segments) {
            current = current.append(segment);

            IFolder currentFolder = project.getFolder(current);
            if (!currentFolder.exists()) {
                currentFolder.create(true, true, null);
            }
        }

        Assert.isTrue(folder.exists());

        return folder;
    }

    /**
     * Write an XML serialization of the given object to a file. Overwrites the previous file content is the file
     * already exists.
     * 
     * @param file The file where the serialization should be written to.
     * @param data
     * @return
     * @throws CoreException
     */
    public static IFile writeDataToXML(IFile file, Object data) throws CoreException
    {
        XStream xstream = getXStream();

        if (file.getParent() instanceof IFolder) {
            IFolder parentFolder = (IFolder) file.getParent();
            createFolder(parentFolder);
        }

        byte[] bytes = null;

        try {
            // We must use UTF-8 since the reader always assumes UTF-8, but getBytes uses the JVM encoding by default
            bytes = xstream.toXML(data).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // ignore, UTF-8 is always available
        }

        InputStream is = new ByteArrayInputStream(bytes);
        if (!file.exists()) {
            file.create(is, true, null);
        } else {
            file.setContents(is, true, false, null);
        }

        try {
            is.close();
        } catch (IOException e) {
            // Ignore
        }

        return file;
    }

    /**
     * Read the XML serialization from a file.
     * 
     * @param file
     * @return The de-serialized object (client should type-cast to the actual type).
     * @throws CoreException
     */
    public static Object readDataFromXML(IFile file) throws CoreException
    {
        XStream xstream = getXStream();

        file.refreshLocal(1, null);
        return xstream.fromXML(file.getContents());
    }

    private static XStream getXStream()
    {
        if (xstream == null) {
            xstream = new XStream(new DomDriver());
        }

        return xstream;
    }
    
    public static BackendType getBackend(String serverUrl) throws XWikiEclipseStorageException {
        if (serverUrl == null || serverUrl.length() == 0) {
            IllegalArgumentException e = new IllegalArgumentException("serverUrl cannot be null or empty");
            throw new XWikiEclipseStorageException(e);
        }
        
        BackendType result = null;
        if (serverUrl.contains("xmlrpc/confluence")) {
            result = BackendType.XMLRPC;
        } else if (serverUrl.contains("/rest")) {
            result = BackendType.REST;
        } else {
            IllegalArgumentException e = new IllegalArgumentException("serverUrl does not contain valid entry point");
            throw new XWikiEclipseStorageException(e);
        }
        
        return result;
    }
}
