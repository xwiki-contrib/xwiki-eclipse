package org.xwiki.eclipse.storage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.xwiki.eclipse.core.notification.CoreEvent;
import org.xwiki.eclipse.core.notification.NotificationManager;
import org.xwiki.eclipse.model.ModelObject;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipseObjectProperty;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseTag;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;
import org.xwiki.eclipse.storage.rest.Hints;
import org.xwiki.eclipse.storage.utils.IdProcessor;
import org.xwiki.eclipse.storage.utils.PersistentMap;

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

    /* Properties for projects associated to data managers */
    public static final QualifiedName AUTO_CONNECT = new QualifiedName("xwiki.eclipse", "auto_connect"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final QualifiedName PASSWORD = new QualifiedName("xwiki.eclipse", "password"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final QualifiedName USERNAME = new QualifiedName("xwiki.eclipse", "username"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final QualifiedName ENDPOINT = new QualifiedName("xwiki.eclipse", "endpoint"); //$NON-NLS-1$ //$NON-NLS-2$

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

        /* at beginning, does not connect to remote */
        remoteXWikiDataStorage = null;

        /*
         * initialize LocalDataStorage
         */
        localXWikiDataStorage = new LocalXWikiDataStorage(project.getFolder(LOCAL_STORAGE_DIRECTORY));

        lastRetrievedPagesDataStorage = new LocalXWikiDataStorage(project.getFolder(LAST_RETRIEVED_PAGE_DIRECTORY));

        conflictingPagesDataStorage = new LocalXWikiDataStorage(project.getFolder(CONFLICTING_PAGES_DIRECTORY));
        pageToStatusMap = new PersistentMap(project.getFolder(DATA_MANAGER_DIRECTORY).getFile(PAGES_STATUS));

        objectToStatusMap = new PersistentMap(project.getFolder(DATA_MANAGER_DIRECTORY).getFile(OBJECTS_STATUS));
    }

    /*
     * Property getters and setters
     */
    public IProject getProject()
    {
        return project;
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

    public String getXWikiEclipseId()
    {
        return String.format("xwikieclipse://%s", getName()); //$NON-NLS-1$
    }

    /*
     * Connection management
     */
    public boolean isConnected()
    {
        return remoteXWikiDataStorage != null;
    }

    public void connect() throws CoreException, XWikiEclipseStorageException
    {
        if (isConnected()) {
            return;
        }

        remoteXWikiDataStorage = new RestRemoteXWikiDataStorage(this, getEndpoint(), getUserName(), getPassword());

        /* When connected synchronize all the pages and objects */
        synchronizePages(new HashSet<String>(pageToStatusMap.keySet()));
        synchronizeObjects(new HashSet<String>(objectToStatusMap.keySet()));

        NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.DATA_MANAGER_CONNECTED, this, null);
    }

    private void synchronizeObjects(Set<String> objectIds) throws XWikiEclipseStorageException
    {
        for (String objectId : objectIds) {
            IdProcessor parser = new IdProcessor(objectId);
            XWikiEclipseObject object =
                localXWikiDataStorage.getObject(parser.getWiki(), parser.getSpace(), parser.getPage(),
                    parser.getClassName(), parser.getNumber());
            if (object != null) {
                synchronize(object);
            }
        }
    }

    /**
     * @param hashSet
     * @throws XWikiEclipseStorageException
     * @throws CoreException
     */
    private void synchronizePages(HashSet<String> pageIds) throws XWikiEclipseStorageException, CoreException
    {
        for (String pageId : pageIds) {
            IdProcessor parser = new IdProcessor(pageId);
            XWikiEclipsePage page =
                localXWikiDataStorage.getPage(parser.getWiki(), parser.getSpace(), parser.getPage(),
                    parser.getLanguage());
            if (page != null) {
                synchronize(page);
            }
        }

    }

    public void disconnect()
    {
        if (remoteXWikiDataStorage != null) {
            remoteXWikiDataStorage.dispose();
        }
        remoteXWikiDataStorage = null;

        NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.DATA_MANAGER_DISCONNECTED, this, null);
    }

    /**
     * @return
     * @throws XWikiEclipseStorageException
     */
    public List<XWikiEclipsePageSummary> getAllPageIds() throws XWikiEclipseStorageException
    {
        List<XWikiEclipsePageSummary> result = new ArrayList<XWikiEclipsePageSummary>();

        List<XWikiEclipseWikiSummary> wikis = getWikis();
        for (XWikiEclipseWikiSummary wiki : wikis) {
            List<XWikiEclipseSpaceSummary> spaces = getSpaces(wiki.getWikiId());
            for (XWikiEclipseSpaceSummary space : spaces) {
                List<XWikiEclipsePageSummary> pageSummaries = getPageSummaries(space.getWiki(), space.getName());
                result.addAll(pageSummaries);
            }
        }

        return result;
    }

    /**
     * @param wiki
     * @param space
     * @return
     */
    public List<XWikiEclipsePageSummary> getPageSummaries(String wiki, String space)
        throws XWikiEclipseStorageException
    {
        List<XWikiEclipsePageSummary> result = null;
        if (isConnected()) {
            result = remoteXWikiDataStorage.getPageSummaries(wiki, space);

            /* add translated page, which are cached in the local storage */
            List<XWikiEclipsePageSummary> pageSummaries = localXWikiDataStorage.getPageSummaries(wiki, space);
            for (XWikiEclipsePageSummary pageSummary : pageSummaries) {
                if (!pageSummary.getLanguage().equals("")) {

                    XWikiEclipsePageSummary p = new XWikiEclipsePageSummary(this);
                    p.setLanguage(pageSummary.getLanguage());
                    p.setUrl(pageSummary.getUrl());
                    p.setName(pageSummary.getName());
                    p.setWiki(pageSummary.getWiki());
                    p.setSpace(pageSummary.getSpace());
                    p.setId(pageSummary.getId());
                    p.setParentId(pageSummary.getParentId());
                    p.setTitle(pageSummary.getTitle());
                    p.setSyntax(pageSummary.getSyntax());

                    result.add(p);
                }
            }
        } else {
            result = new ArrayList<XWikiEclipsePageSummary>();

            List<XWikiEclipsePageSummary> pageSummaries = localXWikiDataStorage.getPageSummaries(wiki, space);
            for (XWikiEclipsePageSummary pageSummary : pageSummaries) {

                XWikiEclipsePageSummary p = new XWikiEclipsePageSummary(this);
                p.setLanguage(pageSummary.getLanguage());
                p.setUrl(pageSummary.getUrl());
                p.setName(pageSummary.getName());
                p.setWiki(pageSummary.getWiki());
                p.setSpace(pageSummary.getSpace());
                p.setId(pageSummary.getId());
                p.setParentId(pageSummary.getParentId());
                p.setTitle(pageSummary.getTitle());
                p.setSyntax(pageSummary.getSyntax());

                result.add(p);
            }

        }
        return result;
    }

    /*
     * space retrieval
     */

    /**
     * @param wiki
     * @return
     * @throws XWikiEclipseStorageException
     */
    public List<XWikiEclipseSpaceSummary> getSpaces(String wikiId) throws XWikiEclipseStorageException
    {
        List<XWikiEclipseSpaceSummary> result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getSpaceSummaries(wikiId);
        } else {
            result = new ArrayList<XWikiEclipseSpaceSummary>();
            List<XWikiEclipseSpaceSummary> spaces;

            spaces = localXWikiDataStorage.getSpaces(wikiId);
            for (XWikiEclipseSpaceSummary spaceSummary : spaces) {
                XWikiEclipseSpaceSummary space = new XWikiEclipseSpaceSummary(this);
                space.setId(spaceSummary.getId());
                space.setName(spaceSummary.getName());
                space.setUrl(spaceSummary.getUrl());
                space.setWiki(spaceSummary.getWiki());

                result.add(space);
            }
        }

        return result;
    }

    public List<XWikiEclipseObjectSummary> getObjectSummaries(String wiki, String space, String page)
        throws XWikiEclipseStorageException
    {
        List<XWikiEclipseObjectSummary> result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getObjectSummaries(wiki, space, page);
        } else {
            try {
                result = new ArrayList<XWikiEclipseObjectSummary>();
                List<XWikiEclipseObjectSummary> objectSummaries =
                    localXWikiDataStorage.getObjectSummaries(wiki, space, page);

                for (XWikiEclipseObjectSummary objectSummary : objectSummaries) {
                    XWikiEclipseObjectSummary o = new XWikiEclipseObjectSummary(this);
                    o.setClassName(objectSummary.getClassName());
                    o.setId(objectSummary.getId());
                    o.setNumber(objectSummary.getNumber());
                    o.setPageId(objectSummary.getPageId());
                    o.setPageName(objectSummary.getPageName());
                    o.setSpace(objectSummary.getSpace());
                    o.setWiki(objectSummary.getWiki());

                    result.add(o);
                }

            } catch (Exception e) {
                throw new XWikiEclipseStorageException(e);
            }
        }

        return result;
    }

    public boolean isPageLocallyAvailable(String pageId, String language)
    {
        IdProcessor parser = new IdProcessor(pageId);
        return localXWikiDataStorage.pageExists(parser.getWiki(), parser.getSpace(), parser.getPage(), language);
    }

    public boolean isObjectLocallyAvailable(String pageId, String className, int number)
    {
        IdProcessor parser = new IdProcessor(pageId);
        return localXWikiDataStorage.objectExists(parser.getWiki(), parser.getSpace(), parser.getPage(), className,
            number);
    }

    public List<XWikiEclipseAttachment> getAttachments(String wiki, String space, String pageName)
        throws XWikiEclipseStorageException
    {
        Assert.isNotNull(wiki);
        Assert.isNotNull(space);
        Assert.isNotNull(pageName);

        List<XWikiEclipseAttachment> result = null;
        if (isConnected()) {
            result = remoteXWikiDataStorage.getAttachments(wiki, space, pageName);
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
            result = remoteXWikiDataStorage.getWikiSummaries();
        } else {
            result = new ArrayList<XWikiEclipseWikiSummary>();

            List<XWikiEclipseWikiSummary> wikiSummaries = localXWikiDataStorage.getWikiSummaries();

            if (wikiSummaries != null) {
                for (XWikiEclipseWikiSummary wiki : wikiSummaries) {
                    XWikiEclipseWikiSummary wikiSummary = new XWikiEclipseWikiSummary(this);
                    wikiSummary.setWikiId(wiki.getWikiId());
                    wikiSummary.setName(wiki.getName());
                    wikiSummary.setVersion(wiki.getVersion());
                    wikiSummary.setBaseUrl(wiki.getBaseUrl());
                    wikiSummary.setSyntaxes(wiki.getSyntaxes());

                    result.add(wikiSummary);
                }
            }
        }

        return result;
    }

    /**
     * @param pageSummary
     * @return
     */
    public List<XWikiEclipsePageHistorySummary> getPageHistories(String wiki, String space, String pageName,
        String language) throws XWikiEclipseStorageException
    {
        List<XWikiEclipsePageHistorySummary> result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getPageHistorySummaries(wiki, space, pageName, language);
        } else {
            result = localXWikiDataStorage.getPageHistorySummaries(wiki, space, pageName, language);
        }

        return result;
    }

    public XWikiEclipsePage getPage(String wiki, String space, String pageName, String language)
        throws XWikiEclipseStorageException
    {
        XWikiEclipsePage result = null;

        XWikiEclipsePage page = localXWikiDataStorage.getPage(wiki, space, pageName, language);
        if (page != null) {
            String pageStatus = pageToStatusMap.get(IdProcessor.getExtendedPageId(page.getId(), page.getLanguage()));
            /* If our local page is either dirty or in conflict then return it */
            if (pageStatus != null) {
                result = new XWikiEclipsePage(this);
                result.setId(page.getId());
                result.setFullName(page.getFullName());
                result.setParentId(page.getParentId());
                result.setTitle(page.getTitle());
                result.setUrl(page.getUrl());
                result.setContent(page.getContent());
                result.setSpace(page.getSpace());
                result.setWiki(page.getWiki());
                result.setMajorVersion(page.getMajorVersion());
                result.setMinorVersion(page.getMinorVersion());
                result.setVersion(page.getVersion());
                result.setName(page.getName());
                result.setLanguage(page.getLanguage());

                return result;
            }
        }

        /*
         * If we are here either there is no cached page, or the cached page is not dirty and not in conflict, so we can
         * grab the latest version of the page and store it in the local storage.
         */
        if (isConnected()) {
            result = remoteXWikiDataStorage.getPage(wiki, space, pageName, language);
            XWikiEclipsePageSummary pageSummary = null;

            /* store pageSummary in local storage */
            if (language != null && !language.equals("")) {
                /* translation page */
                /* create a pageSummary instance, as well as space and wiki */
                pageSummary = new XWikiEclipsePageSummary(this);
                /* set the translation language */
                pageSummary.setLanguage(language);
                pageSummary.setUrl(result.getUrl());
                pageSummary.setName(result.getName());
                pageSummary.setWiki(result.getWiki());
                pageSummary.setSpace(result.getSpace());
                pageSummary.setId(result.getId());
                pageSummary.setParentId(result.getParentId());
                pageSummary.setTitle(result.getTitle());
                pageSummary.setSyntax(result.getSyntax());
                pageSummary.setFullName(result.getFullName());

                localXWikiDataStorage.storePageSummary(pageSummary);

            } else {
                /* default language */
                pageSummary = new XWikiEclipsePageSummary(this);
                pageSummary.setLanguage("");
                pageSummary.setUrl(result.getUrl());
                pageSummary.setName(result.getName());
                pageSummary.setWiki(result.getWiki());
                pageSummary.setSpace(result.getSpace());
                pageSummary.setId(result.getId());
                pageSummary.setParentId(result.getParentId());
                pageSummary.setTitle(result.getTitle());
                pageSummary.setSyntax(result.getSyntax());
                pageSummary.setFullName(result.getFullName());

                localXWikiDataStorage.storePageSummary(pageSummary);
            }

            /* Write an additional copy of the page that can be useful for performing 3-way diffs */
            lastRetrievedPagesDataStorage.storePage(result);

            /* store wiki, space and page */
            XWikiEclipseSpaceSummary spaceSummary = new XWikiEclipseSpaceSummary(this);
            spaceSummary.setId(String.format("%s:%s", result.getWiki(), result.getSpace()));
            spaceSummary.setName(result.getSpace());
            spaceSummary.setUrl("");
            spaceSummary.setWiki(result.getWiki());
            localXWikiDataStorage.storeSpace(spaceSummary);

            XWikiEclipseWikiSummary wikiSummary = new XWikiEclipseWikiSummary(this);
            wikiSummary.setWikiId(result.getWiki());
            wikiSummary.setName(result.getWiki());
            localXWikiDataStorage.storeWiki(wikiSummary);

            localXWikiDataStorage.storePage(result);

            NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_STORED, this, pageSummary);

            return result;
        }

        /* must set the DataManager field */
        if (page != null) {
            result = new XWikiEclipsePage(this);
            result.setId(page.getId());
            result.setFullName(page.getFullName());
            result.setParentId(page.getParentId());
            result.setTitle(page.getTitle());
            result.setUrl(page.getUrl());
            result.setContent(page.getContent());
            result.setSpace(page.getSpace());
            result.setWiki(page.getWiki());
            result.setMajorVersion(page.getMajorVersion());
            result.setMinorVersion(page.getMinorVersion());
            result.setVersion(page.getVersion());
            result.setName(page.getName());
            result.setLanguage(page.getLanguage());

            return result;

        }

        return null;
    }

    /**
     * @param spaceSummary
     * @return
     */
    public XWikiEclipseWikiSummary getWiki(String wiki) throws XWikiEclipseStorageException
    {
        List<XWikiEclipseWikiSummary> wikis = getWikis();
        for (XWikiEclipseWikiSummary wikiSummary : wikis) {
            if (wikiSummary.getName().equals(wiki)) {
                return wikiSummary;
            }
        }

        return null;
    }

    public XWikiEclipseClass getClass(String wiki, String space, String pageName) throws XWikiEclipseStorageException
    {
        XWikiEclipseClass result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getClass(wiki, space, pageName);

        }

        return result;
    }

    public List<XWikiEclipseTag> getTags(XWikiEclipsePageSummary pageSummary) throws XWikiEclipseStorageException
    {
        List<XWikiEclipseTag> result = null;

        if (isConnected()) {
            result =
                remoteXWikiDataStorage.getTags(pageSummary.getWiki(), pageSummary.getSpace(), pageSummary.getName());
        }

        return result;
    }

    public List<XWikiEclipseComment> getComments(String wiki, String space, String pageName)
        throws XWikiEclipseStorageException
    {
        List<XWikiEclipseComment> result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getComments(wiki, space, pageName);
        }

        return result;
    }

    public List<XWikiEclipseObjectProperty> getObjectProperties(String wiki, String space, String pageName,
        String className, int number) throws XWikiEclipseStorageException
    {
        List<XWikiEclipseObjectProperty> result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getObjectProperties(wiki, space, pageName, className, number);
        }

        return result;
    }

    public void download(String directory, XWikiEclipseAttachment attachment) throws XWikiEclipseStorageException
    {
        remoteXWikiDataStorage.download(directory, attachment);
    }

    public XWikiEclipsePageSummary getPageSummary(String wiki, String space, String pageName, String language)
        throws XWikiEclipseStorageException
    {
        XWikiEclipsePageSummary result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getPageSummary(wiki, space, pageName, language);
        } else {
            XWikiEclipsePageSummary pageSummary;
            try {
                pageSummary = localXWikiDataStorage.getPageSummary(wiki, space, pageName, language);
                result = new XWikiEclipsePageSummary(this);
                result.setUrl(pageSummary.getUrl());
                result.setLanguage(pageSummary.getLanguage());
                result.setName(pageSummary.getName());
                result.setWiki(pageSummary.getWiki());
                result.setSpace(pageSummary.getSpace());
                result.setId(pageSummary.getId());
                result.setParentId(pageSummary.getParentId());
                result.setTitle(pageSummary.getTitle());
                result.setSyntax(pageSummary.getSyntax());
            } catch (Exception e) {
                throw new XWikiEclipseStorageException(e);
            }
        }

        return result;
    }

    public XWikiEclipseObject getObject(String wiki, String space, String pageName, String className, int number)
        throws XWikiEclipseStorageException
    {
        XWikiEclipseObject result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getObject(wiki, space, pageName, className, number);
            XWikiEclipseClass clazz = remoteXWikiDataStorage.getClass(wiki, className);

            localXWikiDataStorage.storeClass(clazz);
            localXWikiDataStorage.storeObject(result);

            /* store wiki, space and page */
            XWikiEclipseSpaceSummary spaceSummary = new XWikiEclipseSpaceSummary(this);
            spaceSummary.setId(String.format("%s:%s", result.getWiki(), result.getSpace()));
            spaceSummary.setName(result.getSpace());
            spaceSummary.setUrl("");
            spaceSummary.setWiki(result.getWiki());
            localXWikiDataStorage.storeSpace(spaceSummary);

            XWikiEclipseWikiSummary wikiSummary = new XWikiEclipseWikiSummary(this);
            wikiSummary.setName(result.getWiki());
            wikiSummary.setWikiId(result.getWiki());
            localXWikiDataStorage.storeWiki(wikiSummary);

            XWikiEclipsePageSummary pageSummary = new XWikiEclipsePageSummary(this);
            pageSummary.setFullName(result.getPageId());
            pageSummary.setId(result.getPageId());
            pageSummary.setLanguage("");
            pageSummary.setName(result.getPageName());
            pageSummary.setSpace(result.getSpace());
            pageSummary.setWiki(result.getWiki());
            localXWikiDataStorage.storePageSummary(pageSummary);

            /* Fire the stored notification to communicate that the object has been stored in the local storage */
            NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.OBJECT_STORED, this, result);
        } else {
            XWikiEclipseObject object = localXWikiDataStorage.getObject(wiki, space, pageName, className, number);

            result = new XWikiEclipseObject(this);

            result.setName(object.getId());
            result.setClassName(object.getClassName());
            result.setId(object.getId());
            result.setPageId(object.getPageId());
            result.setSpace(object.getSpace());
            result.setWiki(object.getWiki());
            result.setPageName(object.getPageName());
            result.setNumber(object.getNumber());

            List<XWikiEclipseObjectProperty> props = object.getProperties();
            for (XWikiEclipseObjectProperty p : props) {
                XWikiEclipseObjectProperty prop = new XWikiEclipseObjectProperty(this);
                prop.setClassName(prop.getClassName());
                prop.setName(p.getName());
                prop.setNumber(prop.getNumber());
                prop.setPage(p.getPage());
                prop.setSpace(p.getSpace());
                prop.setType(p.getType());
                prop.setValue(p.getValue());
                prop.setWiki(p.getWiki());

                prop.getAttributes().putAll(p.getAttributes());

                result.getProperties().add(prop);
            }
        }

        return result;
    }

    /**
     * @param spaceSummary
     */
    public void removeSpace(XWikiEclipseSpaceSummary spaceSummary)
    {
        // TODO Auto-generated method stub

    }

    public void removePage(XWikiEclipsePageSummary pageSummary) throws XWikiEclipseStorageException
    {
        if (isConnected()) {
            remoteXWikiDataStorage.remove(pageSummary);
        }

        String pageId = IdProcessor.getExtendedPageId(pageSummary.getId(), pageSummary.getLanguage());
        try {
            localXWikiDataStorage.removePage(pageId);
        } catch (CoreException e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    public void remove(ModelObject o) throws XWikiEclipseStorageException
    {
        if (isConnected()) {
            remoteXWikiDataStorage.remove(o);
        }
    }

    public XWikiEclipseComment storeComment(XWikiEclipseComment c) throws XWikiEclipseStorageException
    {
        XWikiEclipseComment result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.storeComment(c);
        }

        NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.COMMENT_STORED, this, result);

        return result;
    }

    public void uploadAttachment(XWikiEclipsePageSummary pageSummary, URL fileUrl) throws XWikiEclipseStorageException
    {
        if (isConnected()) {
            remoteXWikiDataStorage.uploadAttachment(pageSummary.getWiki(), pageSummary.getSpace(),
                pageSummary.getName(), fileUrl);
        }

    }

    public XWikiEclipseSpaceSummary getSpace(String wiki, String space) throws XWikiEclipseStorageException
    {
        XWikiEclipseSpaceSummary result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getSpace(wiki, space);
        } else {
            XWikiEclipseSpaceSummary spaceSummary = localXWikiDataStorage.getSpace(wiki, space);

            result = new XWikiEclipseSpaceSummary(this);
            result.setId(spaceSummary.getId());
            result.setName(spaceSummary.getName());
            result.setUrl(spaceSummary.getUrl());
            result.setWiki(spaceSummary.getWiki());
        }

        return result;
    }

    public void updateAttachment(XWikiEclipseAttachment attachment, URL fileUrl) throws XWikiEclipseStorageException
    {
        if (isConnected()) {
            String pageId = attachment.getPageId();
            IdProcessor parser = new IdProcessor(pageId);
            remoteXWikiDataStorage.updateAttachment(parser.getWiki(), parser.getSpace(), parser.getPage(),
                attachment.getName(), fileUrl);
        }

    }

    public List<XWikiEclipseTag> getAllTagsInWiki(String wiki) throws XWikiEclipseStorageException
    {
        List<XWikiEclipseTag> result = remoteXWikiDataStorage.getAllTagsInWiki(wiki);

        return result;
    }

    public XWikiEclipseTag addTag(String wiki, String space, String pageName, String tag)
        throws XWikiEclipseStorageException
    {
        XWikiEclipseTag result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.addTag(wiki, space, pageName, tag);
        }

        return result;
    }

    public List<XWikiEclipseClass> getClasses(String wiki) throws XWikiEclipseStorageException
    {
        List<XWikiEclipseClass> result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getClasses(wiki);
        }

        return result;
    }

    public XWikiEclipseClass getClass(String wiki, String className) throws XWikiEclipseStorageException
    {
        XWikiEclipseClass result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getClass(wiki, className);
        } else {
            try {
                result = localXWikiDataStorage.getClass(wiki, className);
            } catch (Exception e) {
                throw new XWikiEclipseStorageException(e);
            }
        }
        return result;
    }

    public void renamePage(XWikiEclipsePageSummary pageSummary, String newSpace, String newPageName)
    {
        // TODO Auto-generated method stub
    }

    public boolean pageExists(String wiki, String space, String pageName, String language)
        throws XWikiEclipseStorageException
    {
        if (isConnected()) {
            return remoteXWikiDataStorage.pageExists(wiki, space, pageName, language);
        }

        return localXWikiDataStorage.pageExists(wiki, space, pageName, language);
    }

    public XWikiEclipsePage createPage(String wiki, String space, String name, String title, String language,
        String content) throws CoreException, XWikiEclipseStorageException
    {
        XWikiEclipsePage xwikiPage = new XWikiEclipsePage(this);
        xwikiPage.setWiki(wiki);
        xwikiPage.setSpace(space);
        xwikiPage.setName(name);
        xwikiPage.setTitle(title);
        xwikiPage.setLanguage(language);
        xwikiPage.setContent(content);
        IdProcessor processor = new IdProcessor(wiki, space, name);
        xwikiPage.setId(processor.getPageId());

        xwikiPage.setMajorVersion(1);
        xwikiPage.setMinorVersion(1);
        Calendar date = Calendar.getInstance();
        xwikiPage.setCreated(date);
        xwikiPage.setCreator(getUserName());
        xwikiPage.setModified(date);
        xwikiPage.setModifier(getUserName());
        xwikiPage.setParentId("");
        xwikiPage.setUrl("");

        return storePage(xwikiPage);
    }

    /**
     * @param xwikiPage
     * @return
     * @throws CoreException
     */
    public XWikiEclipsePage storePage(XWikiEclipsePage xwikiPage) throws XWikiEclipseStorageException, CoreException
    {
        Assert.isNotNull(xwikiPage);

        XWikiEclipsePage storedPage = localXWikiDataStorage.storePage(xwikiPage);

        /*
         * check for local files of wiki and summary are created or not
         */
        if (!localXWikiDataStorage.wikiExists(xwikiPage.getWiki())) {
            XWikiEclipseWikiSummary wiki = getWiki(xwikiPage.getWiki());
            if (wiki != null) {
                localXWikiDataStorage.storeWiki(wiki);
            }
        }

        if (!localXWikiDataStorage.spaceExists(xwikiPage.getWiki(), xwikiPage.getSpace())) {
            XWikiEclipseSpaceSummary space = getSpace(xwikiPage.getWiki(), xwikiPage.getSpace());
            if (space != null) {
                localXWikiDataStorage.storeSpace(space);
            }
        }

        /*
         * Set the dirty flag only if the page has no status. In fact it might be already dirty (should not be possible
         * though) or in conflict
         */
        String extendedPageId = IdProcessor.getExtendedPageId(xwikiPage.getId(), xwikiPage.getLanguage());
        if (pageToStatusMap.get(extendedPageId) == null) {
            pageToStatusMap.put(extendedPageId, DIRTY_STATUS);
        }

        xwikiPage = synchronize(storedPage);

        NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_STORED, this, xwikiPage);

        return xwikiPage;
    }

    private XWikiEclipsePage synchronize(XWikiEclipsePage page) throws XWikiEclipseStorageException, CoreException
    {
        /* If we are not connected then do nothing */
        if (!isConnected()) {
            return page;
        }

        /*
         * If the page is not dirty (i.e., is in conflict or has no status associated) then do nothing
         */
        String extendedPageId = IdProcessor.getExtendedPageId(page.getId(), page.getLanguage());
        if (!DIRTY_STATUS.equals(pageToStatusMap.get(extendedPageId))) {
            return page;
        }

        Assert.isTrue(isConnected());
        Assert.isTrue(DIRTY_STATUS.equals(pageToStatusMap.get(extendedPageId)));

        XWikiEclipsePage remotePage = null;
        try {
            remotePage =
                remoteXWikiDataStorage.getPage(page.getWiki(), page.getSpace(), page.getName(), page.getLanguage());

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

            clearPageStatus(extendedPageId);
        } else if (page.getMajorVersion() == remotePage.getMajorVersion()) {
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

            clearPageStatus(extendedPageId);
        } else {
            pageToStatusMap.put(extendedPageId, CONFLICTING_STATUS);
            conflictingPagesDataStorage.storePage(remotePage);
        }

        return page;
    }

    public void clearConflictingStatus(String pageId) throws XWikiEclipseStorageException, CoreException
    {
        conflictingPagesDataStorage.removePage(pageId);
        pageToStatusMap.put(pageId, DIRTY_STATUS);
    }

    public void clearPageStatus(String pageId) throws XWikiEclipseStorageException, CoreException
    {
        conflictingPagesDataStorage.removePage(pageId);
        pageToStatusMap.remove(pageId);
    }

    /**
     * @param id
     * @return
     */
    public boolean isInConflict(String id)
    {
        return CONFLICTING_STATUS.equals(pageToStatusMap.get(id));
    }

    /**
     * @param pageId page id or extended page id
     * @return
     * @throws XWikiEclipseStorageException
     */
    public XWikiEclipsePage getConflictingPage(String pageId) throws XWikiEclipseStorageException
    {
        IdProcessor parser = new IdProcessor(pageId);
        XWikiEclipsePage page =
            conflictingPagesDataStorage.getPage(parser.getWiki(), parser.getSpace(), parser.getPage(),
                parser.getLanguage());
        XWikiEclipsePage result = new XWikiEclipsePage(this);
        result.setContent(page.getContent());
        result.setCreated(page.getCreated());
        result.setCreator(page.getCreator());
        result.setFullName(page.getFullName());
        result.setId(page.getId());
        result.setLanguage(page.getLanguage());
        result.setMajorVersion(page.getMajorVersion());
        result.setMinorVersion(page.getMinorVersion());
        result.setModified(page.getModified());
        result.setModifier(page.getModifier());
        result.setName(page.getName());
        result.setParentId(page.getParentId());
        result.setSpace(page.getSpace());
        result.setSyntax(page.getSyntax());
        result.setTitle(page.getTitle());
        result.setUrl(page.getUrl());
        result.setVersion(page.getVersion());
        result.setWiki(page.getWiki());

        return result;
    }

    /**
     * @param pageId page id or extended page id
     * @return
     * @throws XWikiEclipseStorageException
     */
    public XWikiEclipsePage getConflictAncestorPage(String pageId) throws XWikiEclipseStorageException
    {
        IdProcessor parser = new IdProcessor(pageId);
        XWikiEclipsePage ancestorPage =
            lastRetrievedPagesDataStorage.getPage(parser.getWiki(), parser.getSpace(), parser.getPage(),
                parser.getLanguage());

        return ancestorPage != null ? ancestorPage : null;
    }

    public XWikiEclipsePage getPageHistory(XWikiEclipsePageHistorySummary pageHistory)
        throws XWikiEclipseStorageException
    {
        if (isConnected()) {
            remoteXWikiDataStorage.getPageHistory(pageHistory.getWiki(), pageHistory.getSpace(), pageHistory.getName(),
                pageHistory.getLanguage(), pageHistory.getMajorVersion(), pageHistory.getMinorVersion());
        }

        return null;
    }

    /**
     * @param object
     * @throws XWikiEclipseStorageException
     */
    public void storeObject(ModelObject object) throws XWikiEclipseStorageException
    {
        if (object instanceof XWikiEclipseObject) {
            XWikiEclipseObject o = (XWikiEclipseObject) object;

            localXWikiDataStorage.storeObject(o);

            String objectId = IdProcessor.getExtendedObjectId(o.getPageId(), o.getClassName(), o.getNumber());

            try {
                objectToStatusMap.put(objectId, DIRTY_STATUS);
            } catch (CoreException e) {
                throw new XWikiEclipseStorageException(e);
            }

            /* store wiki, space and page */
            IdProcessor parser = new IdProcessor(o.getPageId());
            XWikiEclipseSpaceSummary spaceSummary = getSpace(parser.getWiki(), parser.getSpace());
            localXWikiDataStorage.storeSpace(spaceSummary);

            XWikiEclipseWikiSummary wikiSummary = getWiki(parser.getWiki());
            localXWikiDataStorage.storeWiki(wikiSummary);

            XWikiEclipsePageSummary pageSummary =
                getPageSummary(parser.getWiki(), parser.getSpace(), parser.getPage(), "");
            localXWikiDataStorage.storePageSummary(pageSummary);

            o = synchronize(o);

            NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.OBJECT_STORED, this, o);
        }

    }

    /**
     * @param object
     * @return
     * @throws XWikiEclipseStorageException
     */
    private XWikiEclipseObject synchronize(XWikiEclipseObject object) throws XWikiEclipseStorageException
    {
        /* If we are not connected then do nothing */
        if (!isConnected()) {
            return object;
        }

        /*
         * If the page is not dirty (i.e., is in conflict or has no status associated) then do nothing
         */
        String objectId =
            IdProcessor.getExtendedObjectId(object.getPageId(), object.getClassName(), object.getNumber());
        if (!DIRTY_STATUS.equals(objectToStatusMap.get(objectId))) {
            return object;
        }

        Assert.isTrue(isConnected());
        Assert.isTrue(DIRTY_STATUS.equals(objectToStatusMap.get(objectId)));

        if (object.getNumber() == -1) {
            /*
             * If we are here we are synchronizing an object that has been created locally and does not exist remotely.
             */

            /*
             * We save the current object because its id will be assigned when the object is stored remotely. In this
             * way, we will be able to cleanup all the references to the locally created object with the -1 id from the
             * status map, index and local storage.
             */
            XWikiEclipseObject previousObject = object;

            object = remoteXWikiDataStorage.storeObject(object);
            localXWikiDataStorage.storeObject(object);
            try {
                objectToStatusMap.remove(objectId);
            } catch (CoreException e) {
                throw new XWikiEclipseStorageException(e);
            }

            /* Cleanup */
            String previousObjectId =
                IdProcessor.getExtendedObjectId(previousObject.getPageId(), previousObject.getClassName(),
                    previousObject.getNumber());
            IdProcessor parser = new IdProcessor(previousObject.getPageId());
            localXWikiDataStorage.removeObject(parser.getWiki(), parser.getSpace(), parser.getPage(),
                previousObject.getClassName(), previousObject.getNumber());
            try {
                objectToStatusMap.remove(previousObjectId);
            } catch (CoreException e) {
                throw new XWikiEclipseStorageException(e);
            }
        } else {
            object = remoteXWikiDataStorage.storeObject(object);
            localXWikiDataStorage.storeObject(object);

            try {
                objectToStatusMap.remove(objectId);
            } catch (CoreException e) {
                throw new XWikiEclipseStorageException(e);
            }
        }

        return object;
    }

    /**
     * @param pageId
     * @param className
     * @return
     */
    public XWikiEclipseObject createObject(String pageId, String className)
    {
        final XWikiEclipseObject result = new XWikiEclipseObject(this);
        result.setClassName(className);
        result.setName(String.format("%s[NEW]", className));
        result.setPageId(pageId);
        IdProcessor parser = new IdProcessor(pageId);
        result.setSpace(parser.getSpace());
        result.setWiki(parser.getWiki());
        result.setPageName(parser.getPage());
        /* set the default number to -1 */
        result.setNumber(-1);

        return result;
    }

    /**
     * @param pageSummary
     * @param newWiki
     * @param newSpace
     * @param newPageName
     * @throws CoreException
     * @throws XWikiEclipseStorageException
     */
    public void copyPage(XWikiEclipsePageSummary pageSummary, String newWiki, String newSpace, String newPageName)
        throws XWikiEclipseStorageException, CoreException
    {
        XWikiEclipsePage sourcePage =
            getPage(pageSummary.getWiki(), pageSummary.getSpace(), pageSummary.getName(), pageSummary.getLanguage());
        if (isConnected()) {
            XWikiEclipsePage page = remoteXWikiDataStorage.copyPage(sourcePage, newWiki, newSpace, newPageName);
            storePage(page);
        }

    }

    /**
     * @param pageSummary
     * @param newWiki
     * @param newSpace
     * @param newPageName
     * @throws CoreException
     * @throws XWikiEclipseStorageException
     */
    public void movePage(XWikiEclipsePageSummary pageSummary, String newWiki, String newSpace, String newPageName)
        throws XWikiEclipseStorageException, CoreException
    {
        XWikiEclipsePage sourcePage =
            getPage(pageSummary.getWiki(), pageSummary.getSpace(), pageSummary.getName(), pageSummary.getLanguage());

        if (isConnected()) {
            XWikiEclipsePage page = remoteXWikiDataStorage.movePage(sourcePage, newWiki, newSpace, newPageName);
            storePage(page);
            /* the source page has been deleted in remote server, so remove the local copy as well */
            localXWikiDataStorage.removePage(pageSummary.getId());
        }

    }
    
    public Hints getAutoCompleteHints(String content, int offset, String syntax)
    {
    	if (isConnected()) {
    		return remoteXWikiDataStorage.getAutoCompleteHints(content, offset, syntax);
    	} else {
    		return null;
    	}
    }
    
}
