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
package org.xwiki.eclipse.rest.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.xwiki.eclipse.model.ModelObject;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.model.XWikiEclipseClassSummary;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;
import org.xwiki.eclipse.rest.model.XWikiEclipseWikiSummaryInRest;
import org.xwiki.eclipse.storage.AbstractDataManager;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.rest.model.jaxb.Wiki;

/**
 * 
 * @version $Id$
 */
public class DataManagerInRest extends AbstractDataManager
{
    private XWikiRESTClient client;
    private String username;
    private String password;

    /**
     * @param project
     * @throws CoreException
     */
    public DataManagerInRest(IProject project) throws CoreException
    {
        super(project);
        this.client = new XWikiRESTClient(project.getPersistentProperty(ENDPOINT));
        this.username = project.getPersistentProperty(USERNAME);
        this.password = project.getPersistentProperty(PASSWORD);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#isConnected()
     */
    @Override
    public boolean isConnected()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#disconnect()
     */
    @Override
    public void disconnect()
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getSpaceSummary(java.lang.String)
     */
    @Override
    public XWikiEclipseSpaceSummary getSpaceSummary(String spaceKey) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getSpaces()
     */
    @Override
    public List<XWikiEclipseSpaceSummary> getSpaces() throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getPages(java.lang.String)
     */
    @Override
    public List<XWikiEclipsePageSummary> getPages(String spaceKey) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getPage(java.lang.String)
     */
    @Override
    public XWikiEclipsePage getPage(String pageId) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#storePage(org.xwiki.eclipse.model.XWikiEclipsePage)
     */
    @Override
    public XWikiEclipsePage storePage(XWikiEclipsePage page) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
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
     * @see org.xwiki.eclipse.storage.AbstractDataManager#clearConflictingStatus(java.lang.String)
     */
    @Override
    public void clearConflictingStatus(String pageId) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#clearPageStatus(java.lang.String)
     */
    @Override
    public void clearPageStatus(String pageId) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#isInConflict(java.lang.String)
     */
    @Override
    public boolean isInConflict(String pageId)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getConflictingPage(java.lang.String)
     */
    @Override
    public XWikiEclipsePage getConflictingPage(String pageId) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getConflictAncestorPage(java.lang.String)
     */
    @Override
    public XWikiEclipsePage getConflictAncestorPage(String pageId) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getObjects(java.lang.String)
     */
    @Override
    public List<XWikiEclipseObjectSummary> getObjects(String pageId) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getObject(java.lang.String, java.lang.String, int)
     */
    @Override
    public XWikiEclipseObject getObject(String pageId, String className, int id) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getClass(java.lang.String)
     */
    @Override
    public XWikiEclipseClass getClass(String classId) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getPageSummary(java.lang.String)
     */
    @Override
    public XWikiEclipsePageSummary getPageSummary(String pageId) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#storeObject(org.xwiki.eclipse.model.XWikiEclipseObject)
     */
    @Override
    public XWikiEclipseObject storeObject(XWikiEclipseObject object) throws XWikiEclipseStorageException
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
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#synchronizePages(java.util.Set)
     */
    @Override
    public void synchronizePages(Set<String> pageIds) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#synchronizeObjects(java.util.Set)
     */
    @Override
    public void synchronizeObjects(Set<String> objectCompactIds) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#isLocallyAvailable(org.xwiki.eclipse.model.XWikiEclipsePageSummary)
     */
    @Override
    public boolean isLocallyAvailable(XWikiEclipsePageSummary pageSummary)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#isLocallyAvailable(org.xwiki.eclipse.model.XWikiEclipseObjectSummary)
     */
    @Override
    public boolean isLocallyAvailable(XWikiEclipseObjectSummary objectSummary)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#createPage(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public XWikiEclipsePage createPage(String spaceKey, String name, String title, String content)
        throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#createPage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public XWikiEclipsePage createPage(String spaceKey, String name, String title, String language, String content)
        throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#createObject(java.lang.String, java.lang.String)
     */
    @Override
    public XWikiEclipseObject createObject(String pageId, String className) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getClasses()
     */
    @Override
    public List<XWikiEclipseClassSummary> getClasses() throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#removePage(java.lang.String)
     */
    @Override
    public XWikiEclipsePage removePage(String pageId) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#removeSpace(java.lang.String)
     */
    @Override
    public XWikiEclipseSpaceSummary removeSpace(String spaceKey) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#removeObject(java.lang.String, java.lang.String, int)
     */
    @Override
    public XWikiEclipseObject removeObject(String pageId, String className, int objectId)
        throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#renamePage(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public XWikiEclipsePage[] renamePage(String pageId, String newSpace, String newPageName)
        throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getPageHistory(java.lang.String)
     */
    @Override
    public List<XWikiEclipsePageHistorySummary> getPageHistory(String pageId) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getAllPageIds()
     */
    @Override
    public List<XWikiEclipsePageSummary> getAllPageIds() throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#exists(java.lang.String)
     */
    @Override
    public boolean exists(String pageId)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#connect()
     */
    @Override
    public void connect() throws XWikiEclipseStorageException, CoreException
    {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getRootResources()
     */
    @Override
    public List<ModelObject> getRootResources() throws XWikiEclipseStorageException
    {
        List<ModelObject> result = new ArrayList<ModelObject>();
        
        List<XWikiEclipseWikiSummary> wikis = getWikis();
        for (XWikiEclipseWikiSummary xWikiEclipseWikiSummary : wikis) {
            result.add(xWikiEclipseWikiSummary);
        }
        
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getWikis()
     */
    @Override
    public List<XWikiEclipseWikiSummary> getWikis() throws XWikiEclipseStorageException
    {
        try {
            List<XWikiEclipseWikiSummary> result = new ArrayList<XWikiEclipseWikiSummary>();
            List<Wiki> wikis = client.getWikis(username, password);
            for (Wiki wiki : wikis) {
                result.add(new XWikiEclipseWikiSummaryInRest(this, wiki));
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new XWikiEclipseStorageException(e);
        }        
    }
}
