package org.xwiki.eclipse.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.swizzle.confluence.SpaceSummary;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipseClassSummary;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipseObjectProperty;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseServerInfo;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseTag;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;
import org.xwiki.eclipse.storage.utils.PersistentMap;
import org.xwiki.xmlrpc.model.XWikiObject;

/**
 * DataManager is a class that manages remote and local storages and handles their initialization. Basically, it's the
 * entry point of the org.xwiki.eclipse.storage system/plugin
 * 
 * @version $Id$
 */
public class DataManager
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
     * The remote XWiki
     */
    private IRemoteXWikiDataStorage remoteXWikiDataStorage;

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
    public DataManager(IProject project) throws CoreException
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

        /*
         * initialize LocalDataStorage
         */
        localXWikiDataStorage = new LocalXWikiDataStorage(project.getFolder(LOCAL_STORAGE_DIRECTORY));

        lastRetrievedPagesDataStorage = new LocalXWikiDataStorage(project.getFolder(LAST_RETRIEVED_PAGE_DIRECTORY));

        conflictingPagesDataStorage = new LocalXWikiDataStorage(project.getFolder(CONFLICTING_PAGES_DIRECTORY));
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

    protected String getCompactIdForObject(XWikiEclipseObject object)
    {
        return String.format("%s/%s/%d", object.getPageId(), object.getClassName(), object.getId());
    }

    /*
     * Connection management
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

        remoteXWikiDataStorage =
            RemoteXWikiDataStorageFactory.getRemoteXWikiDataStorage(this, getEndpoint(), getUserName(), getPassword());
        try {
            XWikiEclipseServerInfo serverInfo = remoteXWikiDataStorage.getServerInfo();

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

        // FIXME: need to work on the sync in the future
        /* When connected synchronize all the pages and objects */
        // synchronizePages(new HashSet<String>(pageToStatusMap.keySet()));
        // synchronizeObjects(new HashSet<String>(objectToStatusMap.keySet()));
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

    /**
     * @return
     */
    public List<XWikiEclipsePageSummary> getAllPageIds()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param spaceKey
     * @return
     */
    public List<XWikiEclipsePageSummary> getPages(XWikiEclipseSpaceSummary spaceSummary)
        throws XWikiEclipseStorageException
    {
        List<XWikiEclipsePageSummary> result = null;
        if (isConnected()) {
            result = remoteXWikiDataStorage.getPages(spaceSummary);
        } else {
            // FIXME: unimplemented yet, not sure which level to return
        }
        return result;
    }

    /**
     * @param attachment
     * @return
     */
    public XWikiEclipsePage getPage(XWikiEclipseAttachment attachment) throws XWikiEclipseStorageException
    {
        XWikiEclipsePage result = null;
        if (isConnected()) {
            result = remoteXWikiDataStorage.getPage(attachment);
            return result;
        }

        return null;
    }

    /*
     * space retrieval
     */

    /**
     * @param wiki
     * @return
     */
    public List<XWikiEclipseSpaceSummary> getSpaces(XWikiEclipseWikiSummary wiki)
    {
        List<XWikiEclipseSpaceSummary> result = null;

        if (isConnected()) {
            try {
                result = remoteXWikiDataStorage.getSpaces(wiki);
            } catch (XWikiEclipseStorageException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            result = new ArrayList<XWikiEclipseSpaceSummary>();
            List<SpaceSummary> spaces;
            try {
                spaces = localXWikiDataStorage.getSpaces();
                for (SpaceSummary spaceSummary : spaces) {
                    XWikiEclipseSpaceSummary space = new XWikiEclipseSpaceSummary(this);
                    space.setKey(spaceSummary.getKey());
                    space.setName(spaceSummary.getName());
                    space.setUrl(spaceSummary.getUrl());

                    result.add(space);
                }
                return result;

            } catch (XWikiEclipseStorageException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * FXIME: implement xmlrpc and rest getObjects() method
     * 
     * @param pageSummary
     * @return
     */
    public List<XWikiEclipseObjectSummary> getObjects(XWikiEclipsePageSummary pageSummary)
        throws XWikiEclipseStorageException
    {
        List<XWikiEclipseObjectSummary> result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getObjects(pageSummary);
        }

        return result;
    }

    /**
     * @param id
     * @return
     */
    public boolean isInConflict(String id)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @param pageSummary
     * @return
     */
    public boolean isLocallyAvailable(XWikiEclipsePageSummary pageSummary)
    {
        return localXWikiDataStorage.exists(pageSummary.getId());
    }

    /**
     * @param spaceKey
     * @return
     */
    public List<XWikiEclipsePageSummary> getPages(String spaceKey) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param objectSummary
     * @return
     */
    public boolean isLocallyAvailable(XWikiEclipseObjectSummary objectSummary)
    {
        return localXWikiDataStorage.exists(objectSummary.getPageId(), objectSummary.getClassName(),
            objectSummary.getId());
    }

    private String getCompactIdForObject(XWikiObject object)
    {
        return String.format("%s/%s/%d", object.getPageId(), object.getClassName(), object.getId());
    }

    //
    // /*
    // * Page retrieval
    // */
    // public List<XWikiEclipsePageSummary> getPages(final String spaceKey) throws XWikiEclipseException
    // {
    // List<XWikiPageSummary> pageSummaries;
    //
    // if (isConnected()) {
    // pageSummaries = remoteXWikiDataStorage.getPages(spaceKey);
    // } else {
    // pageSummaries = localXWikiDataStorage.getPages(spaceKey);
    // }
    //
    // List<XWikiEclipsePageSummary> result = new ArrayList<XWikiEclipsePageSummary>();
    // for (XWikiPageSummary pageSummary : pageSummaries) {
    // result.add(new XWikiEclipsePageSummary(this, pageSummary));
    // }
    //
    // return result;
    // }
    //
    // public XWikiEclipsePage getPage(String pageId) throws XWikiEclipseException
    // {
    // XWikiPage page = null;
    //
    // page = localXWikiDataStorage.getPage(pageId);
    // if (page != null) {
    // String pageStatus = pageToStatusMap.get(pageId);
    // /* If our local page is either dirty or in conflict then return it */
    // if (pageStatus != null) {
    // return new XWikiEclipsePage(this, page);
    // }
    // }
    //
    // /*
    // * If we are here either there is no cached page, or the cached page is not dirty and not in conflict, so we can
    // * grab the latest version of the page and store it in the local storage.
    // */
    // if (isConnected()) {
    // page = remoteXWikiDataStorage.getPage(pageId);
    //
    // localXWikiDataStorage.storePage(page);
    //
    // /* Write an additional copy of the page that can be useful for performing 3-way diffs */
    // lastRetrievedPagesDataStorage.storePage(page);
    //
    // XWikiEclipsePage result = new XWikiEclipsePage(this, page);
    //
    // /* Fire the stored notification to communicate that the page has been stored in the local storage */
    // NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_STORED, this, result);
    //
    // return result;
    // }
    //
    // /* If we are here we are not connected so we can return the page that we have retrieved at the beginning */
    // return new XWikiEclipsePage(this, page);
    // }
    //
    // public XWikiEclipsePage storePage(XWikiEclipsePage page) throws XWikiEclipseException
    // {
    // Assert.isNotNull(page);
    //
    // XWikiPage storedPage = localXWikiDataStorage.storePage(page.getData());
    //
    // /*
    // * Set the dirty flag only if the page has no status. In fact it might be already dirty (should not be possible
    // * though) or in conflict
    // */
    // if (pageToStatusMap.get(page.getData().getId()) == null) {
    // pageToStatusMap.put(page.getData().getId(), DIRTY_STATUS);
    // }
    //
    // page = new XWikiEclipsePage(this, synchronize(storedPage));
    //
    // NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_STORED, this, page);
    //
    // return page;
    // }
    //
    // private XWikiPage synchronize(XWikiPage page) throws XWikiEclipseException
    // {
    // /* If we are not connected then do nothing */
    // if (!isConnected()) {
    // return page;
    // }
    //
    // /*
    // * If the page is not dirty (i.e., is in conflict or has no status associated) then do nothing
    // */
    // if (!DIRTY_STATUS.equals(pageToStatusMap.get(page.getId()))) {
    // return page;
    // }
    //
    // Assert.isTrue(isConnected());
    // Assert.isTrue(DIRTY_STATUS.equals(pageToStatusMap.get(page.getId())));
    //
    // XWikiPage remotePage = null;
    // try {
    // remotePage = remoteXWikiDataStorage.getPage(page.getId());
    //
    // if (remotePage.getLanguage() != null && !remotePage.getLanguage().equals(page.getLanguage())) {
    // /*
    // * The requested translation has not been found, so we are creating a new translation. We set the
    // * remotePage to null in order to force the page creation
    // */
    // remotePage = null;
    // }
    // } catch (XWikiEclipseException e) {
    // /*
    // * This can fail if the remote page does not yet exist. So ignore the exception here and handle the
    // * condition later: remotePage will be null if we are here.
    // */
    // }
    //
    // if (remotePage == null) {
    // /* If we are here the page or its translation don't exist. Create it! */
    // page = remoteXWikiDataStorage.storePage(page);
    //
    // localXWikiDataStorage.storePage(page);
    //
    // clearPageStatus(page.getId());
    // } else if (page.getVersion() == remotePage.getVersion()) {
    // /* This might be a rename */
    // if (remotePage.getTitle().equals(page.getTitle())) {
    // /* If the local and remote content are equals, no need to re-store remotely the page. */
    // if (remotePage.getContent().equals(page.getContent())) {
    // page = remotePage;
    // } else {
    // page = remoteXWikiDataStorage.storePage(page);
    // }
    // } else {
    // page = remoteXWikiDataStorage.storePage(page);
    // }
    //
    // localXWikiDataStorage.storePage(page);
    //
    // clearPageStatus(page.getId());
    // } else {
    // pageToStatusMap.put(page.getId(), CONFLICTING_STATUS);
    // conflictingPagesDataStorage.storePage(remotePage);
    // }
    //
    // return page;
    // }
    //
    // public void clearConflictingStatus(String pageId) throws XWikiEclipseException
    // {
    // conflictingPagesDataStorage.removePage(pageId);
    // pageToStatusMap.put(pageId, DIRTY_STATUS);
    // }
    //
    // public void clearPageStatus(String pageId) throws XWikiEclipseException
    // {
    // conflictingPagesDataStorage.removePage(pageId);
    // pageToStatusMap.remove(pageId);
    // }
    //
    // public boolean isInConflict(String pageId)
    // {
    // return CONFLICTING_STATUS.equals(pageToStatusMap.get(pageId));
    // }
    //
    // public XWikiEclipsePage getConflictingPage(String pageId) throws XWikiEclipseException
    // {
    // return new XWikiEclipsePage(this, conflictingPagesDataStorage.getPage(pageId));
    // }
    //
    // public XWikiEclipsePage getConflictAncestorPage(String pageId) throws XWikiEclipseException
    // {
    // XWikiPage ancestorPage = lastRetrievedPagesDataStorage.getPage(pageId);
    // return ancestorPage != null ? new XWikiEclipsePage(this, ancestorPage) : null;
    // }
    //
    // /*
    // * Objects
    // */
    //
    // public List<XWikiEclipseObjectSummary> getObjects(String pageId) throws XWikiEclipseException
    // {
    // List<XWikiEclipseObjectSummary> result = new ArrayList<XWikiEclipseObjectSummary>();
    //
    // if (!supportedFunctionalities.contains(Functionality.OBJECTS)) {
    // return result;
    // }
    //
    // XWikiEclipsePageSummary xwikiPageSummary = getPageSummary(pageId);
    //
    // if (isConnected()) {
    // List<XWikiObjectSummary> objects = remoteXWikiDataStorage.getObjects(pageId);
    //
    // for (XWikiObjectSummary object : objects) {
    // result.add(new XWikiEclipseObjectSummary(this, object, xwikiPageSummary.getData()));
    // }
    // } else {
    // List<XWikiObjectSummary> objects = localXWikiDataStorage.getObjects(pageId);
    //
    // for (XWikiObjectSummary object : objects) {
    // result.add(new XWikiEclipseObjectSummary(this, object, xwikiPageSummary.getData()));
    // }
    // }
    //
    // return result;
    // }
    //
    // public XWikiEclipseObject getObject(String pageId, String className, int id) throws XWikiEclipseException
    // {
    // if (isConnected()) {
    // XWikiClass xwikiClass = remoteXWikiDataStorage.getClass(className);
    // XWikiObject xwikiObject = remoteXWikiDataStorage.getObject(pageId, className, id);
    // XWikiPageSummary xwikiPageSummary = remoteXWikiDataStorage.getPageSummary(pageId);
    //
    // localXWikiDataStorage.storeObject(xwikiObject);
    // localXWikiDataStorage.storeClass(xwikiClass);
    //
    // XWikiEclipseObject result = new XWikiEclipseObject(this, xwikiObject, xwikiClass, xwikiPageSummary);
    //
    // /* Fire the stored notification to communicate that the object has been stored in the local storage */
    // NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.OBJECT_STORED, this, result);
    //
    // return result;
    // } else {
    // XWikiClass xwikiClass = localXWikiDataStorage.getClass(className);
    // XWikiPageSummary xwikiPageSummary = localXWikiDataStorage.getPageSummary(pageId);
    //
    // return new XWikiEclipseObject(this, localXWikiDataStorage.getObject(pageId, className, id), xwikiClass,
    // xwikiPageSummary);
    // }
    // }
    //
    // public XWikiEclipseClass getClass(String classId) throws XWikiEclipseException
    // {
    // if (isConnected()) {
    // return new XWikiEclipseClass(this, remoteXWikiDataStorage.getClass(classId));
    //
    // } else {
    // return new XWikiEclipseClass(this, localXWikiDataStorage.getClass(classId));
    // }
    // }
    //
    // public XWikiEclipsePageSummary getPageSummary(String pageId) throws XWikiEclipseException
    // {
    // if (isConnected()) {
    // return new XWikiEclipsePageSummary(this, remoteXWikiDataStorage.getPageSummary(pageId));
    //
    // } else {
    // return new XWikiEclipsePageSummary(this, localXWikiDataStorage.getPageSummary(pageId));
    // }
    // }
    //
    // public XWikiEclipseObject storeObject(XWikiEclipseObject object) throws XWikiEclipseException
    // {
    // localXWikiDataStorage.storeObject(object.getData());
    //
    // objectToStatusMap.put(getCompactIdForObject(object.getData()), DIRTY_STATUS);
    //
    // object =
    // new XWikiEclipseObject(this, synchronize(object.getData()), object.getXWikiClass(), object.getPageSummary());
    //
    // NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.OBJECT_STORED, this, object);
    //
    // return object;
    // }
    //
    // private XWikiObject synchronize(XWikiObject object) throws XWikiEclipseException
    // {
    // /* If we are not connected then do nothing */
    // if (!isConnected()) {
    // return object;
    // }
    //
    // /*
    // * If the page is not dirty (i.e., is in conflict or has no status associated) then do nothing
    // */
    // if (!DIRTY_STATUS.equals(objectToStatusMap.get(getCompactIdForObject(object)))) {
    // return object;
    // }
    //
    // Assert.isTrue(isConnected());
    // Assert.isTrue(DIRTY_STATUS.equals(objectToStatusMap.get(getCompactIdForObject(object))));
    //
    // if (object.getId() == -1) {
    // /*
    // * If we are here we are synchronizing an object that has been created locally and does not exist remotely.
    // */
    //
    // /*
    // * We save the current object because its id will be assigned when the object is stored remotely. In this
    // * way, we will be able to cleanup all the references to the locally created object with the -1 id from the
    // * status map, index and local storage.
    // */
    // XWikiObject previousObject = object;
    //
    // object = remoteXWikiDataStorage.storeObject(object);
    // localXWikiDataStorage.storeObject(object);
    // objectToStatusMap.remove(getCompactIdForObject(object));
    //
    // /* Cleanup */
    // localXWikiDataStorage.removeObject(previousObject.getPageId(), previousObject.getClassName(),
    // previousObject.getId());
    // objectToStatusMap.remove(getCompactIdForObject(previousObject));
    // } else {
    // object = remoteXWikiDataStorage.storeObject(object);
    // localXWikiDataStorage.storeObject(object);
    //
    // objectToStatusMap.remove(getCompactIdForObject(object));
    // }
    //
    // return object;
    // }
    //
    // private void synchronizePages(Set<String> pageIds) throws XWikiEclipseException
    // {
    // for (String pageId : pageIds) {
    // XWikiPage page = localXWikiDataStorage.getPage(pageId);
    // if (page != null) {
    // synchronize(page);
    // }
    // }
    // }
    //
    // private void synchronizeObjects(Set<String> objectCompactIds) throws XWikiEclipseException
    // {
    // for (String objectCompactId : objectCompactIds) {
    // XWikiObject object = getObjectByCompactId(localXWikiDataStorage, objectCompactId);
    // if (object != null) {
    // synchronize(object);
    // }
    // }
    // }
    //
    //
    // private XWikiObject getObjectByCompactId(IDataStorage storage, String compactId) throws NumberFormatException,
    // XWikiEclipseException
    // {
    // String[] components = compactId.split("/");
    // return storage.getObject(components[0], components[1], Integer.parseInt(components[2]));
    // }
    //
    // public XWikiEclipsePage createPage(String spaceKey, String name, String title, String content)
    // throws XWikiEclipseException
    // {
    // return createPage(spaceKey, name, title, null, content);
    // }
    //
    // public XWikiEclipsePage createPage(String spaceKey, String name, String title, String language, String content)
    // throws XWikiEclipseException
    // {
    // XWikiPage xwikiPage = new XWikiPage();
    // xwikiPage.setSpace(spaceKey);
    // xwikiPage.setTitle(title);
    // if (language != null) {
    // xwikiPage.setId(String.format("%s.%s?language=%s", spaceKey, name, language));
    // } else {
    // xwikiPage.setId(String.format("%s.%s", spaceKey, name));
    // }
    // xwikiPage.setContent(content);
    // xwikiPage.setVersion(1);
    // xwikiPage.setMinorVersion(1);
    // xwikiPage.setContentStatus("");
    // xwikiPage.setCreated(new Date());
    // xwikiPage.setCreator("");
    // if (language != null) {
    // xwikiPage.setLanguage(language);
    // } else {
    // xwikiPage.setLanguage("");
    // }
    // xwikiPage.setModified(new Date());
    // xwikiPage.setModifier("");
    // xwikiPage.setParentId("");
    // xwikiPage.setTranslations(new ArrayList<String>());
    // xwikiPage.setUrl("");
    //
    // XWikiEclipsePage page = new XWikiEclipsePage(this, xwikiPage);
    //
    // return storePage(page);
    // }
    //
    // public XWikiEclipseObject createObject(String pageId, String className) throws XWikiEclipseException
    // {
    // XWikiObject xwikiObject = new XWikiObject();
    // xwikiObject.setClassName(className);
    // xwikiObject.setPageId(pageId);
    // xwikiObject.setId(-1);
    // xwikiObject.setPrettyName(String.format("%s[NEW]", className));
    //
    // XWikiEclipseClass xwikiClass = getClass(className);
    // XWikiEclipsePageSummary xwikiPageSummary = getPageSummary(pageId);
    //
    // XWikiEclipseObject object =
    // new XWikiEclipseObject(this, xwikiObject, xwikiClass.getData(), xwikiPageSummary.getData());
    //
    // object = storeObject(object);
    //
    // return object;
    // }
    //
    // public List<XWikiEclipseClassSummary> getClasses() throws XWikiEclipseException
    // {
    // List<XWikiClassSummary> classSummaries;
    //
    // if (isConnected()) {
    // classSummaries = remoteXWikiDataStorage.getClasses();
    // } else {
    // classSummaries = localXWikiDataStorage.getClasses();
    // }
    //
    // List<XWikiEclipseClassSummary> result = new ArrayList<XWikiEclipseClassSummary>();
    // for (XWikiClassSummary classSummary : classSummaries) {
    // result.add(new XWikiEclipseClassSummary(this, classSummary));
    // }
    //
    // return result;
    // }
    //
    // public void removePage(String pageId) throws XWikiEclipseException
    // {
    // XWikiPage page = null;
    //
    // if (isConnected()) {
    // page = remoteXWikiDataStorage.getPage(pageId);
    // remoteXWikiDataStorage.removePage(pageId);
    // } else {
    // page = localXWikiDataStorage.getPage(pageId);
    // }
    //
    // localXWikiDataStorage.removePage(pageId);
    //
    // String spaceKey = page.getSpace();
    //
    // List<XWikiEclipsePageSummary> pages = null;
    // try {
    // pages = getPages(spaceKey);
    // } catch (XWikiEclipseException e) {
    // CoreLog.logError("Unable to get space pages: " + e.getMessage());
    // }
    //
    // if (pages != null && pages.size() == 0) {
    // // The space is left with no pages so it has to be removed.
    // localXWikiDataStorage.removeSpace(spaceKey);
    // }
    //
    // NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_REMOVED, this,
    // new XWikiEclipsePage(this, page));
    // }
    //
    // public void removeSpace(String spaceKey) throws XWikiEclipseException
    // {
    // XWikiEclipseSpaceSummary space = getSpaceSummary(spaceKey);
    //
    // if (space != null) {
    // remoteXWikiDataStorage.removeSpace(spaceKey);
    // localXWikiDataStorage.removeSpace(spaceKey);
    //
    // NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.SPACE_REMOVED, this, space);
    // }
    // }
    //
    // public void removeObject(String pageId, String className, int objectId) throws XWikiEclipseException
    // {
    // XWikiObject object = null;
    //
    // if (isConnected()) {
    // object = remoteXWikiDataStorage.getObject(pageId, className, objectId);
    // remoteXWikiDataStorage.removeObject(pageId, className, objectId);
    // } else {
    // object = localXWikiDataStorage.getObject(pageId, className, objectId);
    // }
    //
    // localXWikiDataStorage.removeObject(pageId, className, objectId);
    //
    // NotificationManager.getDefault().fireCoreEvent(
    // CoreEvent.Type.OBJECT_REMOVED,
    // this,
    // new XWikiEclipseObject(this, object, getClass(object.getClassName()).getData(), getPageSummary(
    // object.getPageId()).getData()));
    // }
    //
    // public boolean renamePage(String pageId, String newSpace, String newPageName) throws XWikiEclipseException
    // {
    // if (!supportedFunctionalities.contains(Functionality.RENAME)) {
    // return false;
    //
    // }
    // XWikiEclipsePage page = getPage(pageId);
    // page.getData().setSpace(newSpace);
    // page.getData().setTitle(newPageName);
    // storePage(page);
    //
    // /* Remove the old page from the cache */
    // clearPageStatus(pageId);
    // localXWikiDataStorage.removePage(pageId);
    //
    // /* Retrieve the new page for caching it */
    // XWikiEclipsePage newPage;
    // if (pageId.indexOf('.') != -1) {
    // newPage = getPage(String.format("%s.%s", newSpace, newPageName));
    // } else {
    // newPage = getPage(pageId);
    // }
    //
    // XWikiEclipsePage pages[] = new XWikiEclipsePage[] {page, newPage};
    // NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_RENAMED, this, pages);
    //
    // return true;
    // }
    //
    // public List<XWikiEclipsePageHistorySummary> getPageHistory(String pageId) throws XWikiEclipseException
    // {
    // List<XWikiEclipsePageHistorySummary> result = new ArrayList<XWikiEclipsePageHistorySummary>();
    // List<XWikiPageHistorySummary> pageHistory = null;
    //
    // if (isConnected()) {
    // pageHistory = remoteXWikiDataStorage.getPageHistory(pageId);
    //
    // } else {
    // pageHistory = localXWikiDataStorage.getPageHistory(pageId);
    // }
    //
    // for (XWikiPageHistorySummary pageHistorySummary : pageHistory) {
    // result.add(new XWikiEclipsePageHistorySummary(this, pageHistorySummary));
    // }
    //
    // return result;
    // }
    //
    // public String getXWikiEclipseId()
    // {
    //        return String.format("xwikieclipse://%s", getName()); //$NON-NLS-1$
    // }
    //
    // public List<XWikiEclipsePageSummary> getAllPageIds() throws XWikiEclipseException
    // {
    // List<XWikiEclipsePageSummary> result = new ArrayList<XWikiEclipsePageSummary>();
    //
    // if (isConnected()) {
    // if (supportedFunctionalities.contains(Functionality.ALL_PAGES_RETRIEVAL)) {
    // List<XWikiPageSummary> pageSummaries = remoteXWikiDataStorage.getAllPageIds();
    // for (XWikiPageSummary pageSummary : pageSummaries) {
    // result.add(new XWikiEclipsePageSummary(this, pageSummary));
    // }
    // } else {
    // List<SpaceSummary> spaces = remoteXWikiDataStorage.getSpaces();
    // for (SpaceSummary spaceSummary : spaces) {
    // List<XWikiPageSummary> pages = remoteXWikiDataStorage.getPages(spaceSummary.getKey());
    // for (XWikiPageSummary pageSummary : pages) {
    // result.add(new XWikiEclipsePageSummary(this, pageSummary));
    // }
    // }
    // }
    // } else {
    // List<XWikiPageSummary> pageSummaries = localXWikiDataStorage.getAllPageIds();
    // for (XWikiPageSummary pageSummary : pageSummaries) {
    // result.add(new XWikiEclipsePageSummary(this, pageSummary));
    // }
    // }
    //
    // return result;
    // }
    //
    // public boolean exists(String pageId)
    // {
    // if (isConnected()) {
    // return remoteXWikiDataStorage.exists(pageId);
    // }
    //
    // return localXWikiDataStorage.exists(pageId);
    // }

    /**
     * @param pageSummary
     * @return
     */
    public List<XWikiEclipseAttachment> getAttachments(XWikiEclipsePageSummary pageSummary)
    {
        List<XWikiEclipseAttachment> result = null;
        if (isConnected()) {
            result = remoteXWikiDataStorage.getAttachments(pageSummary);
            return result;
        }

        return null;
    }

    /**
     * @return
     */
    public List<XWikiEclipseWikiSummary> getWikis() throws XWikiEclipseStorageException
    {
        List<XWikiEclipseWikiSummary> result = null;

        if (isConnected()) {
            try {
                result = remoteXWikiDataStorage.getWikis();
            } catch (XWikiEclipseStorageException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            }
            return result;
        }

        return null;
    }

    /**
     * @param pageSummary
     * @return
     */
    public List<XWikiEclipsePageHistorySummary> getPageHistory(XWikiEclipsePageSummary pageSummary)
        throws XWikiEclipseStorageException
    {
        List<XWikiEclipsePageHistorySummary> result = null;

        if (isConnected()) {
            try {
                result = remoteXWikiDataStorage.getPageHistory(pageSummary);
                return result;
            } catch (XWikiEclipseStorageException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            }
        }

        return null;
    }

    /**
     * @param pageHistorySummary
     * @return
     */
    public XWikiEclipsePage getPage(XWikiEclipsePageHistorySummary pageHistorySummary)
        throws XWikiEclipseStorageException
    {
        XWikiEclipsePage result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getPage(pageHistorySummary);
            return result;
        }
        return null;
    }

    /**
     * @param pageSummary
     * @return
     */
    public XWikiEclipseClassSummary getPageClass(XWikiEclipsePageSummary pageSummary)
    {
        XWikiEclipseClassSummary result = null;
        if (isConnected()) {
            result = remoteXWikiDataStorage.getPageClass(pageSummary);
            return result;
        }

        return null;
    }

    /**
     * @param pageSummary
     * @return
     */
    public List<XWikiEclipseTag> getTags(XWikiEclipsePageSummary pageSummary) throws XWikiEclipseStorageException
    {
        List<XWikiEclipseTag> result = null;
        if (isConnected()) {
            result = remoteXWikiDataStorage.getTags(pageSummary);
            return result;
        }

        return null;
    }

    /**
     * @param pageSummary
     * @return
     */
    public List<XWikiEclipseComment> getComments(XWikiEclipsePageSummary pageSummary)
    {
        List<XWikiEclipseComment> result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getComments(pageSummary);
            return result;
        }

        return null;
    }

    /**
     * @param objectSummary
     * @return
     */
    public List<XWikiEclipseObjectProperty> getObjectProperties(XWikiEclipseObjectSummary objectSummary)
    {
        List<XWikiEclipseObjectProperty> result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getObjectProperties(objectSummary);
            return result;
        }

        return null;
    }

    /**
     * @param pageId
     * @return
     */
    public XWikiEclipsePageSummary getPage(String pageId) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
