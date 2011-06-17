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
package org.xwiki.eclipse.storage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.model.XWikiEclipseClassSummary;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.storage.utils.PersistentMap;

/**
 * 
 * @version $Id$
 */
public abstract class AbstractDataManager
{

    protected static final IPath DATA_MANAGER_DIRECTORY = new Path(".xwikieclipse"); //$NON-NLS-1$

    protected static final IPath LOCAL_STORAGE_DIRECTORY = DATA_MANAGER_DIRECTORY.append("local_storage"); //$NON-NLS-1$

    protected static final IPath LAST_RETRIEVED_PAGE_DIRECTORY = DATA_MANAGER_DIRECTORY.append("last_retrieved_pages"); //$NON-NLS-1$

    protected static final IPath CONFLICTING_PAGES_DIRECTORY = DATA_MANAGER_DIRECTORY.append("conflicting_pages"); //$NON-NLS-1$

    protected static final String PAGES_STATUS = "pagesStatus.index"; //$NON-NLS-1$

    protected static final String OBJECTS_STATUS = "objectsStatus.index"; //$NON-NLS-1$

    /**
     * The project associated to this data manager.
     */
    private IProject project;

    protected static final String DIRTY_STATUS = "dirty"; //$NON-NLS-1$

    protected static final String CONFLICTING_STATUS = "conflicting"; //$NON-NLS-1$

    protected PersistentMap pageToStatusMap;

    protected PersistentMap objectToStatusMap;

    protected Set<Functionality> supportedFunctionalities;

    /* Properties for projects associated to data managers */
    public static final QualifiedName AUTO_CONNECT = new QualifiedName("xwiki.eclipse", "auto_connect"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final QualifiedName PASSWORD = new QualifiedName("xwiki.eclipse", "password"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final QualifiedName USERNAME = new QualifiedName("xwiki.eclipse", "username"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final QualifiedName ENDPOINT = new QualifiedName("xwiki.eclipse", "endpoint"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final QualifiedName BACKEND = new QualifiedName("xwiki.eclipse", "backend"); //$NON-NLS-1$ //$NON-NLS-2$;

    /**
     * Constructor.
     * 
     * @param project The project this data manager is associated with.
     * @throws CoreException
     */
    public AbstractDataManager(IProject project) throws CoreException
    {
        Assert.isNotNull(project); //$NON-NLS-1$
        this.project = project;

        pageToStatusMap = new PersistentMap(project.getFolder(DATA_MANAGER_DIRECTORY).getFile(PAGES_STATUS));

        objectToStatusMap = new PersistentMap(project.getFolder(DATA_MANAGER_DIRECTORY).getFile(OBJECTS_STATUS));

        /*
         * At the beginning we operate locally, and the local storage always support all extended functionalities, i.e.,
         * objects, etc.
         */
        supportedFunctionalities = new HashSet<Functionality>();
        supportedFunctionalities.add(Functionality.OBJECTS);
        supportedFunctionalities.add(Functionality.RENAME);
        supportedFunctionalities.add(Functionality.TRANSLATIONS);
        supportedFunctionalities.add(Functionality.ALL_PAGES_RETRIEVAL);
    }

    /*
     * Property getters and setters
     */
    public IProject getProject()
    {
        return project;
    }

    public Set<Functionality> getSupportedFunctionalities()
    {
        return supportedFunctionalities;
    }

    public String getName()
    {
        return project.getName();
    }

    public String getEndpoint() throws CoreException
    {
        return project.getPersistentProperty(ENDPOINT);
    }

    public void setEndpoint(final String endpoint) throws CoreException
    {
        project.setPersistentProperty(ENDPOINT, endpoint);
    }

    public String getUserName() throws CoreException
    {
        return project.getPersistentProperty(USERNAME);
    }

    public void setUserName(final String userName) throws CoreException
    {
        project.setPersistentProperty(USERNAME, userName);
    }

    public String getPassword() throws CoreException
    {
        return project.getPersistentProperty(PASSWORD);
    }

    public void setPassword(final String password) throws CoreException
    {
        project.setPersistentProperty(PASSWORD, password);
    }

    public boolean isAutoConnect() throws CoreException
    {
        return project.getPersistentProperty(AUTO_CONNECT) != null;
    }

    public void setAutoConnect(final boolean autoConnect) throws CoreException
    {
        project.setPersistentProperty(AUTO_CONNECT, autoConnect ? "true" : null); //$NON-NLS-1$
    }

    public String getBackend() throws CoreException
    {
        return project.getPersistentProperty(BACKEND);
    }

    public void setBackend(final String backend) throws CoreException
    {
        project.setPersistentProperty(BACKEND, backend);
    }    
    
    public String getXWikiEclipseId()
    {
        return String.format("xwikieclipse://%s", getName()); //$NON-NLS-1$
    }

    // abstract methods
    /*
     * Connection management
     */
    public abstract boolean isConnected();

    public abstract void connect() throws XWikiEclipseStorageException, CoreException;

    public abstract void disconnect();

    /*
     * Space retrieval
     */
    public abstract XWikiEclipseSpaceSummary getSpaceSummary(String spaceKey) throws XWikiEclipseStorageException;

    public abstract List<XWikiEclipseSpaceSummary> getSpaces() throws XWikiEclipseStorageException;

    /*
     * Page retrieval
     */
    public abstract List<XWikiEclipsePageSummary> getPages(final String spaceKey) throws XWikiEclipseStorageException;

    public abstract XWikiEclipsePage getPage(String pageId) throws XWikiEclipseStorageException;

    public abstract XWikiEclipsePage storePage(XWikiEclipsePage page) throws XWikiEclipseStorageException;

    protected abstract XWikiEclipsePage synchronize(XWikiEclipsePage page) throws XWikiEclipseStorageException;

    public abstract void clearConflictingStatus(String pageId) throws XWikiEclipseStorageException;

    public abstract void clearPageStatus(String pageId) throws XWikiEclipseStorageException;

    public abstract boolean isInConflict(String pageId);

    public abstract XWikiEclipsePage getConflictingPage(String pageId) throws XWikiEclipseStorageException;

    public abstract XWikiEclipsePage getConflictAncestorPage(String pageId) throws XWikiEclipseStorageException;

    /*
     * Objects
     */

    public abstract List<XWikiEclipseObjectSummary> getObjects(String pageId) throws XWikiEclipseStorageException;

    public abstract XWikiEclipseObject getObject(String pageId, String className, int id)
        throws XWikiEclipseStorageException;

    public abstract XWikiEclipseClass getClass(String classId) throws XWikiEclipseStorageException;

    public abstract XWikiEclipsePageSummary getPageSummary(String pageId) throws XWikiEclipseStorageException;

    public abstract XWikiEclipseObject storeObject(XWikiEclipseObject object) throws XWikiEclipseStorageException;

    public abstract XWikiEclipseObject synchronize(XWikiEclipseObject object) throws XWikiEclipseStorageException;

    public abstract void synchronizePages(Set<String> pageIds) throws XWikiEclipseStorageException;

    public abstract void synchronizeObjects(Set<String> objectCompactIds) throws XWikiEclipseStorageException;

    public abstract boolean isLocallyAvailable(XWikiEclipsePageSummary pageSummary);

    public abstract boolean isLocallyAvailable(XWikiEclipseObjectSummary objectSummary);

    protected String getCompactIdForObject(XWikiEclipseObject object)
    {
        return String.format("%s/%s/%d", object.getPageId(), object.getClassName(), object.getId());
    }

    public abstract XWikiEclipsePage createPage(String spaceKey, String name, String title, String content)
        throws XWikiEclipseStorageException;

    public abstract XWikiEclipsePage createPage(String spaceKey, String name, String title, String language,
        String content) throws XWikiEclipseStorageException;

    public abstract XWikiEclipseObject createObject(String pageId, String className)
        throws XWikiEclipseStorageException;

    public abstract List<XWikiEclipseClassSummary> getClasses() throws XWikiEclipseStorageException;

    public abstract XWikiEclipsePage removePage(String pageId) throws XWikiEclipseStorageException;

    public abstract XWikiEclipseSpaceSummary removeSpace(String spaceKey) throws XWikiEclipseStorageException;

    public abstract XWikiEclipseObject removeObject(String pageId, String className, int objectId)
        throws XWikiEclipseStorageException;

    public abstract XWikiEclipsePage[] renamePage(String pageId, String newSpace, String newPageName)
        throws XWikiEclipseStorageException;

    public abstract List<XWikiEclipsePageHistorySummary> getPageHistory(String pageId)
        throws XWikiEclipseStorageException;

    public abstract List<XWikiEclipsePageSummary> getAllPageIds() throws XWikiEclipseStorageException;

    public abstract boolean exists(String pageId);
}
