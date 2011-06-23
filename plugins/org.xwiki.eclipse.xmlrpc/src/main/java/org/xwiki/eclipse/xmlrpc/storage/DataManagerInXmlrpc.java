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
package org.xwiki.eclipse.xmlrpc.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.swizzle.confluence.ServerInfo;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
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
import org.xwiki.eclipse.storage.AbstractDataManager;
import org.xwiki.eclipse.storage.Functionality;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.eclipse.xmlrpc.model.XWikiEclipseClassInXmlrpc;
import org.xwiki.eclipse.xmlrpc.model.XWikiEclipseClassSummaryInXmlrpc;
import org.xwiki.eclipse.xmlrpc.model.XWikiEclipseObjectInXmlrpc;
import org.xwiki.eclipse.xmlrpc.model.XWikiEclipseObjectSummaryInXmlrpc;
import org.xwiki.eclipse.xmlrpc.model.XWikiEclipsePageHistorySummaryInXmlrpc;
import org.xwiki.eclipse.xmlrpc.model.XWikiEclipsePageInXmlrpc;
import org.xwiki.eclipse.xmlrpc.model.XWikiEclipsePageSummaryInXmlrpc;
import org.xwiki.xmlrpc.model.XWikiClass;
import org.xwiki.xmlrpc.model.XWikiClassSummary;
import org.xwiki.xmlrpc.model.XWikiObject;
import org.xwiki.xmlrpc.model.XWikiObjectSummary;
import org.xwiki.xmlrpc.model.XWikiPage;
import org.xwiki.xmlrpc.model.XWikiPageHistorySummary;
import org.xwiki.xmlrpc.model.XWikiPageSummary;

/**
 * A class that implements a controller for handling data and the connection towards an XWiki server. It takes care of
 * synchronizing pages, objects, handling local copies, conflicts, etc.
 */
public class DataManagerInXmlrpc extends AbstractDataManager
{
    /**
     * The remote XWiki
     */
    private RemoteXWikiDataStorage remoteXWikiDataStorage;

    /**
     * A local XWiki data storage for caching XWiki elements.
     */
    private LocalXWikiDataStorage localXWikiDataStorage;

    private LocalXWikiDataStorage lastRetrievedPagesDataStorage;

    private LocalXWikiDataStorage conflictingPagesDataStorage;

    /**
     * Constructor.
     * 
     * @param project The project this data manager is associated with.
     * @throws CoreException
     */
    public DataManagerInXmlrpc(IProject project) throws CoreException
    {
        super(project);

        remoteXWikiDataStorage = null;

        localXWikiDataStorage = new LocalXWikiDataStorage(project.getFolder(LOCAL_STORAGE_DIRECTORY));

        lastRetrievedPagesDataStorage = new LocalXWikiDataStorage(project.getFolder(LAST_RETRIEVED_PAGE_DIRECTORY));

        conflictingPagesDataStorage = new LocalXWikiDataStorage(project.getFolder(CONFLICTING_PAGES_DIRECTORY));
    }

    /*
     * /* Connection management
     */
    public boolean isConnected()
    {
        return remoteXWikiDataStorage != null;
    }

    public void connect() throws XWikiEclipseStorageException, CoreException
    {
        if (isConnected()) {
            return;
        }

        remoteXWikiDataStorage = new RemoteXWikiDataStorage(getEndpoint(), getUserName(), getPassword());
        try {
            ServerInfo serverInfo = remoteXWikiDataStorage.getServerInfo();

            if (serverInfo.getBaseUrl().contains("xwiki")) {
                if (serverInfo.getMajorVersion() == 1) {
                    if (serverInfo.getMinorVersion() < 5) {
                        supportedFunctionalities.remove(Functionality.RENAME);
                    }

                    if (serverInfo.getMinorVersion() < 4) {
                        supportedFunctionalities.remove(Functionality.TRANSLATIONS);
                        supportedFunctionalities.remove(Functionality.ALL_PAGES_RETRIEVAL);
                    }
                }
            } else {
                /* We are talking to a confluence server */
                supportedFunctionalities.remove(Functionality.TRANSLATIONS);
                supportedFunctionalities.remove(Functionality.OBJECTS);
                supportedFunctionalities.remove(Functionality.ALL_PAGES_RETRIEVAL);
            }
        } catch (Exception e) {
            /* Here we are talking to an XWiki < 1.4. In this case we only support basic functionalities. */
            supportedFunctionalities.clear();
        }

        /* When connected synchronize all the pages and objects */
        synchronizePages(new HashSet<String>(pageToStatusMap.keySet()));
        synchronizeObjects(new HashSet<String>(objectToStatusMap.keySet()));
    }

    public void disconnect()
    {
        remoteXWikiDataStorage.dispose();
        remoteXWikiDataStorage = null;

        /* Set this to true, because the local storage always support extended features */
        supportedFunctionalities.clear();
        supportedFunctionalities.add(Functionality.OBJECTS);
        supportedFunctionalities.add(Functionality.RENAME);
    }

    /*
     * Space retrieval
     */
    public XWikiEclipseSpaceSummary getSpaceSummary(String spaceKey) throws XWikiEclipseStorageException
    {
        SpaceSummary space = null;

        if (isConnected()) {
            space = remoteXWikiDataStorage.getSpaceSumary(spaceKey);
        } else {
            space = localXWikiDataStorage.getSpaceSumary(spaceKey);
        }

        return new XWikiEclipseSpaceSummary(this, space.getKey(), space.getName(), space.getUrl());
    }

    public List<XWikiEclipseSpaceSummary> getSpaces() throws XWikiEclipseStorageException
    {
        List<SpaceSummary> spaceSummaries;

        if (isConnected()) {
            spaceSummaries = remoteXWikiDataStorage.getSpaces();
        } else {
            spaceSummaries = localXWikiDataStorage.getSpaces();
        }

        List<XWikiEclipseSpaceSummary> result = new ArrayList<XWikiEclipseSpaceSummary>();
        for (SpaceSummary spaceSummary : spaceSummaries) {
            result.add(new XWikiEclipseSpaceSummary(this, spaceSummary.getKey(), spaceSummary.getName(), spaceSummary
                .getUrl()));
        }

        return result;
    }

    /*
     * Page retrieval
     */
    public List<XWikiEclipsePageSummary> getPages(final String spaceKey) throws XWikiEclipseStorageException
    {
        List<XWikiPageSummary> pageSummaries;

        if (isConnected()) {
            pageSummaries = remoteXWikiDataStorage.getPages(spaceKey);
        } else {
            pageSummaries = localXWikiDataStorage.getPages(spaceKey);
        }

        List<XWikiEclipsePageSummary> result = new ArrayList<XWikiEclipsePageSummary>();
        for (XWikiPageSummary pageSummary : pageSummaries) {
            result.add(new XWikiEclipsePageSummaryInXmlrpc(this, pageSummary));
        }

        return result;
    }

    public XWikiEclipsePage getPage(String pageId) throws XWikiEclipseStorageException
    {
        XWikiPage page = null;

        page = localXWikiDataStorage.getPage(pageId);
        if (page != null) {
            String pageStatus = pageToStatusMap.get(pageId);
            /* If our local page is either dirty or in conflict then return it */
            if (pageStatus != null) {
                return new XWikiEclipsePageInXmlrpc(this, page);
            }
        }

        /*
         * If we are here either there is no cached page, or the cached page is not dirty and not in conflict, so we can
         * grab the latest version of the page and store it in the local storage.
         */
        if (isConnected()) {
            page = remoteXWikiDataStorage.getPage(pageId);

            localXWikiDataStorage.storePage(page);

            /* Write an additional copy of the page that can be useful for performing 3-way diffs */
            lastRetrievedPagesDataStorage.storePage(page);

            XWikiEclipsePage result = new XWikiEclipsePageInXmlrpc(this, page);

            return result;
        }

        /* If we are here we are not connected so we can return the page that we have retrieved at the beginning */
        return new XWikiEclipsePageInXmlrpc(this, page);
    }

    public XWikiEclipsePage storePage(XWikiEclipsePage p) throws XWikiEclipseStorageException
    {
        Assert.isNotNull(p);

        XWikiEclipsePageInXmlrpc page = (XWikiEclipsePageInXmlrpc) p;

        XWikiPage storedPage = localXWikiDataStorage.storePage(page.getData());

        /*
         * Set the dirty flag only if the page has no status. In fact it might be already dirty (should not be possible
         * though) or in conflict
         */
        if (pageToStatusMap.get(page.getData().getId()) == null) {
            pageToStatusMap.put(page.getData().getId(), DIRTY_STATUS);
        }

        page = new XWikiEclipsePageInXmlrpc(this, synchronize(storedPage));

        return page;
    }

    private XWikiPage synchronize(XWikiPage page) throws XWikiEclipseStorageException
    {
        /* If we are not connected then do nothing */
        if (!isConnected()) {
            return page;
        }

        /*
         * If the page is not dirty (i.e., is in conflict or has no status associated) then do nothing
         */
        if (!DIRTY_STATUS.equals(pageToStatusMap.get(page.getId()))) {
            return page;
        }

        Assert.isTrue(isConnected());
        Assert.isTrue(DIRTY_STATUS.equals(pageToStatusMap.get(page.getId())));

        XWikiPage remotePage = null;
        try {
            remotePage = remoteXWikiDataStorage.getPage(page.getId());

            if (remotePage.getLanguage() != null && !remotePage.getLanguage().equals(page.getLanguage())) {
                /*
                 * The requested translation has not been found, so we are creating a new translation. We set the
                 * remotePage to null in order to force the page creation
                 */
                remotePage = null;
            }
        } catch (XWikiEclipseStorageException e) {
            /*
             * This can fail if the remote page does not yet exist. So ignore the exception here and handle the
             * condition later: remotePage will be null if we are here.
             */
        }

        if (remotePage == null) {
            /* If we are here the page or its translation don't exist. Create it! */
            page = remoteXWikiDataStorage.storePage(page);

            localXWikiDataStorage.storePage(page);

            clearPageStatus(page.getId());
        } else if (page.getVersion() == remotePage.getVersion()) {
            /* This might be a rename */
            if (remotePage.getTitle().equals(page.getTitle())) {
                /* If the local and remote content are equals, no need to re-store remotely the page. */
                if (remotePage.getContent().equals(page.getContent())) {
                    page = remotePage;
                } else {
                    page = remoteXWikiDataStorage.storePage(page);
                }
            } else {
                page = remoteXWikiDataStorage.storePage(page);
            }

            localXWikiDataStorage.storePage(page);

            clearPageStatus(page.getId());
        } else {
            pageToStatusMap.put(page.getId(), CONFLICTING_STATUS);
            conflictingPagesDataStorage.storePage(remotePage);
        }

        return page;
    }

    public void clearConflictingStatus(String pageId) throws XWikiEclipseStorageException
    {
        conflictingPagesDataStorage.removePage(pageId);
        pageToStatusMap.put(pageId, DIRTY_STATUS);
    }

    public void clearPageStatus(String pageId) throws XWikiEclipseStorageException
    {
        conflictingPagesDataStorage.removePage(pageId);
        pageToStatusMap.remove(pageId);
    }

    public boolean isInConflict(String pageId)
    {
        return CONFLICTING_STATUS.equals(pageToStatusMap.get(pageId));
    }

    public XWikiEclipsePage getConflictingPage(String pageId) throws XWikiEclipseStorageException
    {
        return new XWikiEclipsePageInXmlrpc(this, conflictingPagesDataStorage.getPage(pageId));
    }

    public XWikiEclipsePage getConflictAncestorPage(String pageId) throws XWikiEclipseStorageException
    {
        XWikiPage ancestorPage = lastRetrievedPagesDataStorage.getPage(pageId);
        return ancestorPage != null ? new XWikiEclipsePageInXmlrpc(this, ancestorPage) : null;
    }

    /*
     * Objects
     */

    public List<XWikiEclipseObjectSummary> getObjects(String pageId) throws XWikiEclipseStorageException
    {
        List<XWikiEclipseObjectSummary> result = new ArrayList<XWikiEclipseObjectSummary>();

        if (!supportedFunctionalities.contains(Functionality.OBJECTS)) {
            return result;
        }

        XWikiEclipsePageSummaryInXmlrpc xwikiPageSummary = (XWikiEclipsePageSummaryInXmlrpc) getPageSummary(pageId);

        if (isConnected()) {
            List<XWikiObjectSummary> objects = remoteXWikiDataStorage.getObjects(pageId);

            for (XWikiObjectSummary object : objects) {
                result.add(new XWikiEclipseObjectSummaryInXmlrpc(this, object, xwikiPageSummary.getData()));
            }
        } else {
            List<XWikiObjectSummary> objects = localXWikiDataStorage.getObjects(pageId);

            for (XWikiObjectSummary object : objects) {
                result.add(new XWikiEclipseObjectSummaryInXmlrpc(this, object, xwikiPageSummary.getData()));
            }
        }

        return result;
    }

    public XWikiEclipseObject getObject(String pageId, String className, int id) throws XWikiEclipseStorageException
    {
        if (isConnected()) {
            XWikiClass xwikiClass = remoteXWikiDataStorage.getClass(className);
            XWikiObject xwikiObject = remoteXWikiDataStorage.getObject(pageId, className, id);
            XWikiPageSummary xwikiPageSummary = remoteXWikiDataStorage.getPageSummary(pageId);

            localXWikiDataStorage.storeObject(xwikiObject);
            localXWikiDataStorage.storeClass(xwikiClass);

            XWikiEclipseObject result = new XWikiEclipseObjectInXmlrpc(this, xwikiObject, xwikiClass, xwikiPageSummary);

            return result;
        } else {
            XWikiClass xwikiClass = localXWikiDataStorage.getClass(className);
            XWikiPageSummary xwikiPageSummary = localXWikiDataStorage.getPageSummary(pageId);

            return new XWikiEclipseObjectInXmlrpc(this, localXWikiDataStorage.getObject(pageId, className, id),
                xwikiClass, xwikiPageSummary);
        }
    }

    public XWikiEclipseClass getClass(String classId) throws XWikiEclipseStorageException
    {
        if (isConnected()) {
            return new XWikiEclipseClassInXmlrpc(this, remoteXWikiDataStorage.getClass(classId));

        } else {
            return new XWikiEclipseClassInXmlrpc(this, localXWikiDataStorage.getClass(classId));
        }
    }

    public XWikiEclipsePageSummary getPageSummary(String pageId) throws XWikiEclipseStorageException
    {
        if (isConnected()) {
            return new XWikiEclipsePageSummaryInXmlrpc(this, remoteXWikiDataStorage.getPageSummary(pageId));

        } else {
            return new XWikiEclipsePageSummaryInXmlrpc(this, localXWikiDataStorage.getPageSummary(pageId));
        }
    }

    public XWikiEclipseObject storeObject(XWikiEclipseObject o) throws XWikiEclipseStorageException
    {
        XWikiEclipseObjectInXmlrpc object = (XWikiEclipseObjectInXmlrpc) o;
        localXWikiDataStorage.storeObject(object.getData());

        objectToStatusMap.put(getCompactIdForObject(object.getData()), DIRTY_STATUS);

        object =
            new XWikiEclipseObjectInXmlrpc(this, synchronize(object.getData()), object.getXWikiClassInXmlrpc(),
                object.getPageSummaryInXmlrpc());

        return object;
    }

    private XWikiObject synchronize(XWikiObject object) throws XWikiEclipseStorageException
    {
        /* If we are not connected then do nothing */
        if (!isConnected()) {
            return object;
        }

        /*
         * If the page is not dirty (i.e., is in conflict or has no status associated) then do nothing
         */
        if (!DIRTY_STATUS.equals(objectToStatusMap.get(getCompactIdForObject(object)))) {
            return object;
        }

        Assert.isTrue(isConnected());
        Assert.isTrue(DIRTY_STATUS.equals(objectToStatusMap.get(getCompactIdForObject(object))));

        if (object.getId() == -1) {
            /*
             * If we are here we are synchronizing an object that has been created locally and does not exist remotely.
             */

            /*
             * We save the current object because its id will be assigned when the object is stored remotely. In this
             * way, we will be able to cleanup all the references to the locally created object with the -1 id from the
             * status map, index and local storage.
             */
            XWikiObject previousObject = object;

            object = remoteXWikiDataStorage.storeObject(object);
            localXWikiDataStorage.storeObject(object);
            objectToStatusMap.remove(getCompactIdForObject(object));

            /* Cleanup */
            localXWikiDataStorage.removeObject(previousObject.getPageId(), previousObject.getClassName(),
                previousObject.getId());
            objectToStatusMap.remove(getCompactIdForObject(previousObject));
        } else {
            object = remoteXWikiDataStorage.storeObject(object);
            localXWikiDataStorage.storeObject(object);

            objectToStatusMap.remove(getCompactIdForObject(object));
        }

        return object;
    }

    public void synchronizePages(Set<String> pageIds) throws XWikiEclipseStorageException
    {
        for (String pageId : pageIds) {
            XWikiPage p = localXWikiDataStorage.getPage(pageId);
            XWikiEclipsePage page = new XWikiEclipsePageInXmlrpc(this, p);
            if (page != null) {
                synchronize(page);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#synchronize(org.xwiki.eclipse.model.XWikiEclipsePage)
     */
    @Override
    public XWikiEclipsePage synchronize(XWikiEclipsePage page) throws XWikiEclipseStorageException
    {
        /* If we are not connected then do nothing */
        if (!isConnected()) {
            return page;
        }

        /*
         * If the page is not dirty (i.e., is in conflict or has no status associated) then do nothing
         */
        if (!DIRTY_STATUS.equals(pageToStatusMap.get(page.getId()))) {
            return page;
        }

        Assert.isTrue(isConnected());
        Assert.isTrue(DIRTY_STATUS.equals(pageToStatusMap.get(page.getId())));

        XWikiPage pageData = ((XWikiEclipsePageInXmlrpc) page).getData();
        XWikiPage remotePage = null;
        try {
            remotePage = remoteXWikiDataStorage.getPage(page.getId());

            if (remotePage.getLanguage() != null && !remotePage.getLanguage().equals(page.getLanguage())) {
                /*
                 * The requested translation has not been found, so we are creating a new translation. We set the
                 * remotePage to null in order to force the page creation
                 */
                remotePage = null;
            }
        } catch (XWikiEclipseStorageException e) {
            /*
             * This can fail if the remote page does not yet exist. So ignore the exception here and handle the
             * condition later: remotePage will be null if we are here.
             */
        }

        if (remotePage == null) {
            /* If we are here the page or its translation don't exist. Create it! */
            pageData = remoteXWikiDataStorage.storePage(pageData);

            localXWikiDataStorage.storePage(pageData);

            clearPageStatus(page.getId());
        } else if (page.getVersion() == remotePage.getVersion()) {
            /* This might be a rename */
            if (remotePage.getTitle().equals(page.getTitle())) {
                /* If the local and remote content are equals, no need to re-store remotely the page. */
                if (remotePage.getContent().equals(page.getContent())) {
                    pageData = remotePage;
                } else {
                    pageData = remoteXWikiDataStorage.storePage(pageData);
                }
            } else {
                pageData = remoteXWikiDataStorage.storePage(pageData);
            }

            localXWikiDataStorage.storePage(pageData);

            clearPageStatus(page.getId());
        } else {
            pageToStatusMap.put(page.getId(), CONFLICTING_STATUS);
            conflictingPagesDataStorage.storePage(remotePage);
        }

        page = new XWikiEclipsePageInXmlrpc(this, pageData);
        return page;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#synchronize(org.xwiki.eclipse.model.XWikiEclipseObject)
     */
    @Override
    public XWikiEclipseObject synchronize(XWikiEclipseObject object) throws XWikiEclipseStorageException
    {
        /* If we are not connected then do nothing */
        if (!isConnected()) {
            return object;
        }

        /*
         * If the page is not dirty (i.e., is in conflict or has no status associated) then do nothing
         */
        if (!DIRTY_STATUS.equals(objectToStatusMap.get(getCompactIdForObject(object)))) {
            return object;
        }

        Assert.isTrue(isConnected());
        Assert.isTrue(DIRTY_STATUS.equals(objectToStatusMap.get(getCompactIdForObject(object))));

        XWikiObject objectData = ((XWikiEclipseObjectInXmlrpc) object).getData();
        if (object.getId() == -1) {
            /*
             * If we are here we are synchronizing an object that has been created locally and does not exist remotely.
             */

            /*
             * We save the current object because its id will be assigned when the object is stored remotely. In this
             * way, we will be able to cleanup all the references to the locally created object with the -1 id from the
             * status map, index and local storage.
             */

            XWikiObject previousObject = objectData;

            objectData = remoteXWikiDataStorage.storeObject(objectData);
            localXWikiDataStorage.storeObject(objectData);
            objectToStatusMap.remove(getCompactIdForObject(object));

            /* Cleanup */
            localXWikiDataStorage.removeObject(previousObject.getPageId(), previousObject.getClassName(),
                previousObject.getId());
            objectToStatusMap.remove(getCompactIdForObject(previousObject));
        } else {
            objectData = remoteXWikiDataStorage.storeObject(objectData);
            localXWikiDataStorage.storeObject(objectData);

            objectToStatusMap.remove(getCompactIdForObject(object));
        }

        object =
            new XWikiEclipseObjectInXmlrpc(this, objectData,
                ((XWikiEclipseObjectInXmlrpc) object).getXWikiClassInXmlrpc(),
                ((XWikiEclipseObjectInXmlrpc) object).getPageSummaryInXmlrpc());
        return object;
    }

    public void synchronizeObjects(Set<String> objectCompactIds) throws XWikiEclipseStorageException
    {
        for (String objectCompactId : objectCompactIds) {
            XWikiObject object = getObjectByCompactId(localXWikiDataStorage, objectCompactId);
            if (object != null) {
                synchronize(object);
            }
        }
    }

    public boolean isLocallyAvailable(XWikiEclipsePageSummary pageSummary)
    {
        return localXWikiDataStorage.exists(pageSummary.getId());
    }

    public boolean isLocallyAvailable(XWikiEclipseObjectSummary objectSummary)
    {
        return localXWikiDataStorage.exists(objectSummary.getPageId(), objectSummary.getClassName(),
            objectSummary.getId());
    }

    private String getCompactIdForObject(XWikiObject object)
    {
        return String.format("%s/%s/%d", object.getPageId(), object.getClassName(), object.getId());
    }

    private XWikiObject getObjectByCompactId(IDataStorage storage, String compactId) throws NumberFormatException,
        XWikiEclipseStorageException
    {
        String[] components = compactId.split("/");
        return storage.getObject(components[0], components[1], Integer.parseInt(components[2]));
    }

    public XWikiEclipsePage createPage(String spaceKey, String name, String title, String content)
        throws XWikiEclipseStorageException
    {
        return createPage(spaceKey, name, title, null, content);
    }

    public XWikiEclipsePage createPage(String spaceKey, String name, String title, String language, String content)
        throws XWikiEclipseStorageException
    {
        XWikiPage xwikiPage = new XWikiPage();
        xwikiPage.setSpace(spaceKey);
        xwikiPage.setTitle(title);
        if (language != null) {
            xwikiPage.setId(String.format("%s.%s?language=%s", spaceKey, name, language));
        } else {
            xwikiPage.setId(String.format("%s.%s", spaceKey, name));
        }
        xwikiPage.setContent(content);
        xwikiPage.setVersion(1);
        xwikiPage.setMinorVersion(1);
        xwikiPage.setContentStatus("");
        xwikiPage.setCreated(new Date());
        xwikiPage.setCreator("");
        if (language != null) {
            xwikiPage.setLanguage(language);
        } else {
            xwikiPage.setLanguage("");
        }
        xwikiPage.setModified(new Date());
        xwikiPage.setModifier("");
        xwikiPage.setParentId("");
        xwikiPage.setTranslations(new ArrayList<String>());
        xwikiPage.setUrl("");

        XWikiEclipsePage page = new XWikiEclipsePageInXmlrpc(this, xwikiPage);

        return storePage(page);
    }

    public XWikiEclipseObject createObject(String pageId, String className) throws XWikiEclipseStorageException
    {
        XWikiObject xwikiObject = new XWikiObject();
        xwikiObject.setClassName(className);
        xwikiObject.setPageId(pageId);
        xwikiObject.setId(-1);
        xwikiObject.setPrettyName(String.format("%s[NEW]", className));

        XWikiEclipseClass xwikiClass = getClass(className);
        XWikiEclipsePageSummary xwikiPageSummary = getPageSummary(pageId);

        XWikiEclipseObject object =
            new XWikiEclipseObjectInXmlrpc(this, xwikiObject, ((XWikiEclipseClassInXmlrpc) xwikiClass).getData(),
                ((XWikiEclipsePageSummaryInXmlrpc) xwikiPageSummary).getData());

        object = storeObject(object);

        return object;
    }

    public List<XWikiEclipseClassSummary> getClasses() throws XWikiEclipseStorageException
    {
        List<XWikiClassSummary> classSummaries;

        if (isConnected()) {
            classSummaries = remoteXWikiDataStorage.getClasses();
        } else {
            classSummaries = localXWikiDataStorage.getClasses();
        }

        List<XWikiEclipseClassSummary> result = new ArrayList<XWikiEclipseClassSummary>();
        for (XWikiClassSummary classSummary : classSummaries) {
            result.add(new XWikiEclipseClassSummaryInXmlrpc(this, classSummary));
        }

        return result;
    }

    public XWikiEclipsePage removePage(String pageId) throws XWikiEclipseStorageException
    {
        XWikiPage page = null;

        if (isConnected()) {
            page = remoteXWikiDataStorage.getPage(pageId);
            remoteXWikiDataStorage.removePage(pageId);
        } else {
            page = localXWikiDataStorage.getPage(pageId);
        }

        localXWikiDataStorage.removePage(pageId);

        String spaceKey = page.getSpace();

        List<XWikiEclipsePageSummary> pages = null;
        try {
            pages = getPages(spaceKey);
        } catch (XWikiEclipseStorageException e) {
            e.printStackTrace();
            throw new XWikiEclipseStorageException(e);
            // CoreLog.logError("Unable to get space pages: " + e.getMessage());
        }

        if (pages != null && pages.size() == 0) {
            // The space is left with no pages so it has to be removed.
            localXWikiDataStorage.removeSpace(spaceKey);
        }

        XWikiEclipsePage result = new XWikiEclipsePageInXmlrpc(this, page);
        return result;
    }

    public XWikiEclipseSpaceSummary removeSpace(String spaceKey) throws XWikiEclipseStorageException
    {
        XWikiEclipseSpaceSummary space = getSpaceSummary(spaceKey);

        if (space != null) {
            remoteXWikiDataStorage.removeSpace(spaceKey);
            localXWikiDataStorage.removeSpace(spaceKey);
        }

        return space;
    }

    public XWikiEclipseObject removeObject(String pageId, String className, int objectId)
        throws XWikiEclipseStorageException
    {
        XWikiObject object = null;

        if (isConnected()) {
            object = remoteXWikiDataStorage.getObject(pageId, className, objectId);
            remoteXWikiDataStorage.removeObject(pageId, className, objectId);
        } else {
            object = localXWikiDataStorage.getObject(pageId, className, objectId);
        }

        localXWikiDataStorage.removeObject(pageId, className, objectId);

        XWikiEclipseObject result =
            new XWikiEclipseObjectInXmlrpc(this, object,
                ((XWikiEclipseClassInXmlrpc) getClass(object.getClassName())).getData(),
                ((XWikiEclipsePageSummaryInXmlrpc) getPageSummary(object.getPageId())).getData());

        return result;
    }

    public XWikiEclipsePage[] renamePage(String pageId, String newSpace, String newPageName)
        throws XWikiEclipseStorageException
    {
        if (!supportedFunctionalities.contains(Functionality.RENAME)) {
            return null;

        }
        XWikiEclipsePageInXmlrpc page = (XWikiEclipsePageInXmlrpc) getPage(pageId);
        page.getData().setSpace(newSpace);
        page.getData().setTitle(newPageName);
        storePage(page);

        /* Remove the old page from the cache */
        clearPageStatus(pageId);
        localXWikiDataStorage.removePage(pageId);

        /* Retrieve the new page for caching it */
        XWikiEclipsePage newPage;
        if (pageId.indexOf('.') != -1) {
            newPage = getPage(String.format("%s.%s", newSpace, newPageName));
        } else {
            newPage = getPage(pageId);
        }

        XWikiEclipsePage pages[] = new XWikiEclipsePage[] {page, newPage};

        return pages;
    }

    public List<XWikiEclipsePageHistorySummary> getPageHistory(String pageId) throws XWikiEclipseStorageException
    {
        List<XWikiEclipsePageHistorySummary> result = new ArrayList<XWikiEclipsePageHistorySummary>();
        List<XWikiPageHistorySummary> pageHistory = null;

        if (isConnected()) {
            pageHistory = remoteXWikiDataStorage.getPageHistory(pageId);

        } else {
            pageHistory = localXWikiDataStorage.getPageHistory(pageId);
        }

        for (XWikiPageHistorySummary pageHistorySummary : pageHistory) {
            result.add(new XWikiEclipsePageHistorySummaryInXmlrpc(this, pageHistorySummary));
        }

        return result;
    }

    public List<XWikiEclipsePageSummary> getAllPageIds() throws XWikiEclipseStorageException
    {
        List<XWikiEclipsePageSummary> result = new ArrayList<XWikiEclipsePageSummary>();

        if (isConnected()) {
            if (supportedFunctionalities.contains(Functionality.ALL_PAGES_RETRIEVAL)) {
                List<XWikiPageSummary> pageSummaries = remoteXWikiDataStorage.getAllPageIds();
                for (XWikiPageSummary pageSummary : pageSummaries) {
                    result.add(new XWikiEclipsePageSummaryInXmlrpc(this, pageSummary));
                }
            } else {
                List<SpaceSummary> spaces = remoteXWikiDataStorage.getSpaces();
                for (SpaceSummary spaceSummary : spaces) {
                    List<XWikiPageSummary> pages = remoteXWikiDataStorage.getPages(spaceSummary.getKey());
                    for (XWikiPageSummary pageSummary : pages) {
                        result.add(new XWikiEclipsePageSummaryInXmlrpc(this, pageSummary));
                    }
                }
            }
        } else {
            List<XWikiPageSummary> pageSummaries = localXWikiDataStorage.getAllPageIds();
            for (XWikiPageSummary pageSummary : pageSummaries) {
                result.add(new XWikiEclipsePageSummaryInXmlrpc(this, pageSummary));
            }
        }

        return result;
    }

    public boolean exists(String pageId)
    {
        if (isConnected()) {
            return remoteXWikiDataStorage.exists(pageId);
        }

        return localXWikiDataStorage.exists(pageId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.AbstractDataManager#getRootResources()
     */
    @Override
    public List<ModelObject> getRootResources() throws XWikiEclipseStorageException
    {
        List<XWikiEclipseSpaceSummary> spaces = getSpaces();
        List<ModelObject> result = new ArrayList<ModelObject>();
        for (XWikiEclipseSpaceSummary xWikiEclipseSpaceSummary : spaces) {
            result.add(xWikiEclipseSpaceSummary);
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
        // return nothing
        return null;
    }
}
