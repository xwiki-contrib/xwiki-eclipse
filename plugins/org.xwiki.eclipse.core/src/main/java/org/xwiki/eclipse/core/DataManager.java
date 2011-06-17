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
package org.xwiki.eclipse.core;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.xwiki.eclipse.core.notifications.CoreEvent;
import org.xwiki.eclipse.core.notifications.NotificationManager;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.model.XWikiEclipseClassSummary;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.storage.AbstractDataManager;
import org.xwiki.eclipse.storage.BackendType;
import org.xwiki.eclipse.storage.Functionality;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.eclipse.xmlrpc.storage.DataManagerInXmlrpc;


/**
 * A class that implements a controller for handling data and the connection towards an XWiki server. It takes care of
 * synchronizing pages, objects, handling local copies, conflicts, etc.
 * delegate all the functions to backend implementation, xmlrpc or rest
 * fire notification event 
 * 
 * @version $Id$
 */
public class DataManager extends AbstractDataManager
{
	AbstractDataManager dataManager = null;

	public DataManager(IProject project) throws CoreException {
	    super(project);
		try {
		    
			String backend = project.getPersistentProperty(AbstractDataManager.BACKEND);
			BackendType backendType = BackendType.valueOf(backend);
			switch (backendType) {
			case xmlrpc:
				dataManager = new DataManagerInXmlrpc(project);
				break;
			case rest:
			    throw new UnsupportedOperationException();
			    //break;
			default:
				break;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}
	
	public int hashCode() {
		return dataManager.hashCode();
	}

	public boolean equals(Object obj) {
		return dataManager.equals(obj);
	}

	public IProject getProject() {
		return dataManager.getProject();
	}

	public Set<Functionality> getSupportedFunctionalities() {
		return dataManager.getSupportedFunctionalities();
	}

	public String getName() {
		return dataManager.getName();
	}

	public String getEndpoint() throws CoreException {
		return dataManager.getEndpoint();
	}

	public void setEndpoint(String endpoint) throws CoreException {
		dataManager.setEndpoint(endpoint);
	}

	public String getUserName() throws CoreException {
		return dataManager.getUserName();
	}

	public void setUserName(String userName) throws CoreException {
		dataManager.setUserName(userName);
	}

	public String getPassword() throws CoreException {
		return dataManager.getPassword();
	}

	public void setPassword(String password) throws CoreException {
		dataManager.setPassword(password);
	}

	public boolean isAutoConnect() throws CoreException {
		return dataManager.isAutoConnect();
	}

	public void setAutoConnect(boolean autoConnect) throws CoreException {
		dataManager.setAutoConnect(autoConnect);
	}

	public String getXWikiEclipseId() {
		return dataManager.getXWikiEclipseId();
	}

	public boolean isConnected() {
		return dataManager.isConnected();
	}

	public void connect() throws XWikiEclipseStorageException, CoreException {
		dataManager.connect();
		//fire up event
        NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.DATA_MANAGER_CONNECTED, this, this);
	}

	public void disconnect() {
		dataManager.disconnect();
		//fire up event
		NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.DATA_MANAGER_DISCONNECTED, this, this);
	}

	public XWikiEclipseSpaceSummary getSpaceSummary(String spaceKey)
			throws XWikiEclipseStorageException {		
		return dataManager.getSpaceSummary(spaceKey);
	}

	public List<XWikiEclipseSpaceSummary> getSpaces()
			throws XWikiEclipseStorageException {
		return dataManager.getSpaces();
	}

	public List<XWikiEclipsePageSummary> getPages(String spaceKey)
			throws XWikiEclipseStorageException {
		return dataManager.getPages(spaceKey);
	}

	public XWikiEclipsePage getPage(String pageId)
			throws XWikiEclipseStorageException {
	    XWikiEclipsePage result = dataManager.getPage(pageId);
	    if (isConnected()) {
	        /* Fire the stored notification to communicate that the page has been stored in the local storage */
            NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_STORED, this, result);
	    }
		return result;
	}

	public XWikiEclipsePage storePage(XWikiEclipsePage page)
			throws XWikiEclipseStorageException {
	    XWikiEclipsePage result = dataManager.storePage(page);
	    NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_STORED, this, page);
		return result;
	}

	public void clearConflictingStatus(String pageId)
			throws XWikiEclipseStorageException {
		dataManager.clearConflictingStatus(pageId);
	}

	public void clearPageStatus(String pageId)
			throws XWikiEclipseStorageException {
		dataManager.clearPageStatus(pageId);
	}

	public boolean isInConflict(String pageId) {
		return dataManager.isInConflict(pageId);
	}

	public XWikiEclipsePage getConflictingPage(String pageId)
			throws XWikiEclipseStorageException {
		return dataManager.getConflictingPage(pageId);
	}

	public XWikiEclipsePage getConflictAncestorPage(String pageId)
			throws XWikiEclipseStorageException {
		return dataManager.getConflictAncestorPage(pageId);
	}

	public List<XWikiEclipseObjectSummary> getObjects(String pageId)
			throws XWikiEclipseStorageException {
		return dataManager.getObjects(pageId);
	}

	public XWikiEclipseObject getObject(String pageId, String className, int id)
			throws XWikiEclipseStorageException {
	    XWikiEclipseObject result = dataManager.getObject(pageId, className, id);
	    if (isConnected()) {
	        /* Fire the stored notification to communicate that the object has been stored in the local storage */
            NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.OBJECT_STORED, this, result);
	    }
	    
		return result;
	}

	public XWikiEclipseClass getClass(String classId)
			throws XWikiEclipseStorageException {
		return dataManager.getClass(classId);
	}

	public XWikiEclipsePageSummary getPageSummary(String pageId)
			throws XWikiEclipseStorageException {
		return dataManager.getPageSummary(pageId);
	}

	public XWikiEclipseObject storeObject(XWikiEclipseObject object)
			throws XWikiEclipseStorageException {
	    XWikiEclipseObject result = dataManager.storeObject(object);
	    
	    //fire core event
	    NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.OBJECT_STORED, this, object);
		return result;
	}

	public boolean isLocallyAvailable(XWikiEclipsePageSummary pageSummary) {
		return dataManager.isLocallyAvailable(pageSummary);
	}

	public boolean isLocallyAvailable(XWikiEclipseObjectSummary objectSummary) {
		return dataManager.isLocallyAvailable(objectSummary);
	}

	public XWikiEclipsePage createPage(String spaceKey, String name,
			String title, String content) throws XWikiEclipseStorageException {
		return dataManager.createPage(spaceKey, name, title, content);
	}

	public XWikiEclipsePage createPage(String spaceKey, String name,
			String title, String language, String content)
			throws XWikiEclipseStorageException {
		return dataManager.createPage(spaceKey, name, title, language, content);
	}

	public XWikiEclipseObject createObject(String pageId, String className)
			throws XWikiEclipseStorageException {
		return dataManager.createObject(pageId, className);
	}

	public List<XWikiEclipseClassSummary> getClasses()
			throws XWikiEclipseStorageException {
		return dataManager.getClasses();
	}

	public XWikiEclipsePage removePage(String pageId) throws XWikiEclipseStorageException {
		XWikiEclipsePage result = dataManager.removePage(pageId);
		NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_REMOVED, this, result);
		return result;
	}

	public XWikiEclipseSpaceSummary removeSpace(String spaceKey)
			throws XWikiEclipseStorageException {
	    XWikiEclipseSpaceSummary result = dataManager.removeSpace(spaceKey);
	    if (result != null) {
	        NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.SPACE_REMOVED, this, result);   
	    }
		
		return result;
		
	}

	public XWikiEclipseObject removeObject(String pageId, String className, int objectId)
			throws XWikiEclipseStorageException {
		XWikiEclipseObject result = dataManager.removeObject(pageId, className, objectId);
		
		NotificationManager.getDefault().fireCoreEvent(
            CoreEvent.Type.OBJECT_REMOVED,
            this, result);
		
		return result;
	}

	/**
	 * 
	 * @param pageId
	 * @param newSpace
	 * @param newPageName
	 * @return array of old page and new page, or null if RENAME function is not supported
	 * @throws XWikiEclipseStorageException
	 */
	public XWikiEclipsePage[] renamePage(String pageId, String newSpace, String newPageName)
			throws XWikiEclipseStorageException {
	    XWikiEclipsePage[] result = dataManager.renamePage(pageId, newSpace, newPageName);
	    
	    if (result != null) {
	        NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_RENAMED, this, result);
	    }
	    
		return result; 
	}

	public List<XWikiEclipsePageHistorySummary> getPageHistory(String pageId)
			throws XWikiEclipseStorageException {
		return dataManager.getPageHistory(pageId);
	}

	public List<XWikiEclipsePageSummary> getAllPageIds()
			throws XWikiEclipseStorageException {
		return dataManager.getAllPageIds();
	}

	public boolean exists(String pageId) {
		return dataManager.exists(pageId);
	}

	public String toString() {
		return dataManager.toString();
	}

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#synchronize(org.xwiki.eclipse.model.XWikiEclipsePage)
     */
    @Override
    protected XWikiEclipsePage synchronize(XWikiEclipsePage page) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#synchronize(org.xwiki.eclipse.model.XWikiEclipseObject)
     */
    @Override
    public XWikiEclipseObject synchronize(XWikiEclipseObject object) throws XWikiEclipseStorageException
    {
        return dataManager.synchronize(object);
    }

    public void synchronizePages(Set<String> pageIds) throws XWikiEclipseStorageException
    {
        dataManager.synchronizePages(pageIds);
        
    }

    public void synchronizeObjects(Set<String> objectCompactIds) throws XWikiEclipseStorageException
    {
        dataManager.synchronizeObjects(objectCompactIds);        
    }
}
