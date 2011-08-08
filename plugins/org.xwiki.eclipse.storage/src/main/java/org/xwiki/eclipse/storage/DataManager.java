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
import org.xwiki.eclipse.model.XWikiEclipseServerInfo;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseTag;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;
import org.xwiki.eclipse.storage.notification.CoreEvent;
import org.xwiki.eclipse.storage.notification.NotificationManager;
import org.xwiki.eclipse.storage.utils.PageIdProcessor;
import org.xwiki.eclipse.storage.utils.PersistentMap;
import org.xwiki.eclipse.storage.utils.StorageUtils;
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

    public void connect() throws CoreException, XWikiEclipseStorageException
    {
        if (isConnected()) {
            return;
        }

        remoteXWikiDataStorage =
            RemoteXWikiDataStorageFactory.getRemoteXWikiDataStorage(this, getEndpoint(), getUserName(), getPassword());
        if (remoteXWikiDataStorage == null) {
            /* remote connection error, operate locally */
            disconnect();
        } else {
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
     */
    public List<XWikiEclipseSpaceSummary> getSpaces(String wikiId)
    {
        List<XWikiEclipseSpaceSummary> result = null;

        if (isConnected()) {
            try {
                result = remoteXWikiDataStorage.getSpaceSummaries(wikiId);
            } catch (XWikiEclipseStorageException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            result = new ArrayList<XWikiEclipseSpaceSummary>();
            List<XWikiEclipseSpaceSummary> spaces;
            try {
                spaces = localXWikiDataStorage.getSpaces(wikiId);
                for (XWikiEclipseSpaceSummary spaceSummary : spaces) {
                    XWikiEclipseSpaceSummary space = new XWikiEclipseSpaceSummary(this);
                    space.setId(spaceSummary.getId());
                    space.setName(spaceSummary.getName());
                    space.setUrl(spaceSummary.getUrl());
                    space.setWiki(spaceSummary.getWiki());

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
     * @return
     */
    public List<XWikiEclipseObjectSummary> getObjectSummaries(String wiki, String space, String page)
        throws XWikiEclipseStorageException
    {
        List<XWikiEclipseObjectSummary> result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getObjectSummaries(wiki, space, page);
        }

        return result;
    }

    /**
     * @param pageSummary
     * @return
     */
    public boolean isLocallyAvailable(String pageId, String language)
    {
        PageIdProcessor parser = new PageIdProcessor(pageId);
        return localXWikiDataStorage.pageExists(parser.getWiki(), parser.getSpace(), parser.getPage(), language);
    }

    public boolean isLocallyAvailable(String pageId, String className, int number)
    {
        return localXWikiDataStorage.objectExists(pageId, className, number);
    }

    private String getCompactIdForObject(XWikiObject object)
    {
        return String.format("%s/%s/%d", object.getPageId(), object.getClassName(), object.getId());
    }

    /**
     * @param pageSummary
     * @return
     */
    public List<XWikiEclipseAttachment> getAttachments(String wiki, String space, String pageName)
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
            try {
                result = remoteXWikiDataStorage.getWikiSummaries();
            } catch (XWikiEclipseStorageException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            }
            return result;
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
            return result;
        }
    }

    /**
     * @param pageSummary
     * @return
     */
    public List<XWikiEclipsePageHistorySummary> getPageHistory(String wiki, String space, String pageName,
        String language) throws XWikiEclipseStorageException
    {
        List<XWikiEclipsePageHistorySummary> result = null;

        if (isConnected()) {
            try {
                result = remoteXWikiDataStorage.getPageHistorySummaries(wiki, space, pageName, language);
                return result;
            } catch (XWikiEclipseStorageException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            }
        } else {
            result = localXWikiDataStorage.getPageHistorySummaries(wiki, space, pageName, language);
            return result;
        }
    }

    public XWikiEclipsePage getPage(String wiki, String space, String pageName, String language)
        throws XWikiEclipseStorageException
    {
        XWikiEclipsePage result = null;

        XWikiEclipsePage page = localXWikiDataStorage.getPage(wiki, space, pageName, language);
        if (page != null) {
            String pageStatus = pageToStatusMap.get(StorageUtils.getExtendedPageId(page.getId(), page.getLanguage()));
            /* If our local page is either dirty or in conflict then return it */
            if (pageStatus != null) {
                result = new XWikiEclipsePage(this);
                result.setId(page.getId());
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
                pageSummary = getPageSummary(wiki, space, pageName, language);

                localXWikiDataStorage.storePageSummary(pageSummary);
            }

            /* Write an additional copy of the page that can be useful for performing 3-way diffs */
            lastRetrievedPagesDataStorage.storePage(result);

            /* store wiki, space and page */
            XWikiEclipseSpaceSummary spaceSummary = getSpace(wiki, space);
            localXWikiDataStorage.storeSpace(spaceSummary);

            XWikiEclipseWikiSummary wikiSummary = getWiki(wiki);
            localXWikiDataStorage.storeWiki(wikiSummary);

            localXWikiDataStorage.storePage(result);

            NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_STORED, this, pageSummary);
            return result;
        }

        return page;
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

    /**
     * @param pageSummary
     * @return
     */
    public XWikiEclipseClass getClass(String wiki, String space, String pageName)
    {
        XWikiEclipseClass result = null;
        if (isConnected()) {
            result = remoteXWikiDataStorage.getClass(wiki, space, pageName);
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
    public List<XWikiEclipseComment> getComments(String wiki, String space, String pageName)
    {
        List<XWikiEclipseComment> result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getComments(wiki, space, pageName);
            return result;
        }

        return null;
    }

    public List<XWikiEclipseObjectProperty> getObjectProperties(String wiki, String space, String pageName,
        String className, int number)
    {
        List<XWikiEclipseObjectProperty> result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getObjectProperties(wiki, space, pageName, className, number);
            return result;
        }

        return null;
    }

    /**
     * @param directory
     * @param attachments
     */
    public void download(String directory, XWikiEclipseAttachment attachment)
    {
        remoteXWikiDataStorage.download(directory, attachment);
    }

    public XWikiEclipsePageSummary getPageSummary(String wiki, String space, String pageName, String language)
        throws XWikiEclipseStorageException
    {
        XWikiEclipsePageSummary result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getPageSummary(wiki, space, pageName, language);
        }

        return result;
    }

    /**
     * @param o
     * @return
     */
    public XWikiEclipseObject getObject(String wiki, String space, String pageName, String className, int number)
        throws XWikiEclipseStorageException
    {
        XWikiEclipseObject result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getObject(wiki, space, pageName, className, number);
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

    public void remove(ModelObject o)
    {
        if (isConnected()) {
            remoteXWikiDataStorage.remove(o);
        }
    }

    /**
     * @param c
     */
    public XWikiEclipseComment storeComment(XWikiEclipseComment c)
    {
        XWikiEclipseComment result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.storeComment(c);
        }

        NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.COMMENT_STORED, this, result);
        return result;
    }

    /**
     * @param pageSummary
     * @param fileUrl
     */
    public void uploadAttachment(XWikiEclipsePageSummary pageSummary, URL fileUrl)
    {
        if (isConnected()) {
            remoteXWikiDataStorage.uploadAttachment(pageSummary.getWiki(), pageSummary.getSpace(),
                pageSummary.getName(), fileUrl);
        }

    }

    public XWikiEclipseSpaceSummary getSpace(String wiki, String space)
    {
        XWikiEclipseSpaceSummary result = null;

        if (isConnected()) {
            result = remoteXWikiDataStorage.getSpace(wiki, space);
        }

        return result;
    }

    /**
     * @param attachment
     * @param fileUrl
     */
    public void updateAttachment(XWikiEclipseAttachment attachment, URL fileUrl)
    {
        if (isConnected()) {
            String pageId = attachment.getPageId();
            PageIdProcessor parser = new PageIdProcessor(pageId);
            remoteXWikiDataStorage.updateAttachment(parser.getWiki(), parser.getSpace(), parser.getPage(),
                attachment.getName(), fileUrl);
        }

    }

    public List<XWikiEclipseTag> getAllTagsInWiki(String wiki)
    {
        List<XWikiEclipseTag> result = remoteXWikiDataStorage.getAllTagsInWiki(wiki);
        return result;
    }

    /**
     * @param pageSummary
     * @param tag
     */
    public XWikiEclipseTag addTag(String wiki, String space, String pageName, String tag)
    {
        XWikiEclipseTag result = null;
        if (isConnected()) {
            result = remoteXWikiDataStorage.addTag(wiki, space, pageName, tag);
        }

        return result;
    }

    /**
     * @param wiki
     * @return
     */
    public List<XWikiEclipseClass> getClasses(String wiki)
    {
        List<XWikiEclipseClass> result = null;
        if (isConnected()) {
            result = remoteXWikiDataStorage.getClasses(wiki);
        }

        return result;
    }

    /**
     * @param wiki
     * @param className
     * @return
     */
    public XWikiEclipseClass getClass(String wiki, String className)
    {
        XWikiEclipseClass result = null;
        if (isConnected()) {
            result = remoteXWikiDataStorage.getClass(wiki, className);
        }
        return result;
    }

    /**
     * @param pageSummary
     * @param newSpace
     * @param newPageName
     */
    public void renamePage(XWikiEclipsePageSummary pageSummary, String newSpace, String newPageName)
    {
        // TODO Auto-generated method stub

    }

    /**
     * @param pageId
     * @return
     */
    public boolean exists(String wiki, String space, String pageName, String language)
    {
        if (isConnected()) {
            return remoteXWikiDataStorage.exists(wiki, space, pageName, language);
        }

        return localXWikiDataStorage.pageExists(wiki, space, pageName, language);
    }

    /**
     * @param wiki
     * @param space
     * @param name
     * @param title
     * @param language
     * @param content
     * @return
     */
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
        PageIdProcessor processor = new PageIdProcessor(wiki, space, name, language);
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
        String extendedPageId = StorageUtils.getExtendedPageId(xwikiPage.getId(), xwikiPage.getLanguage());
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
        String extendedPageId = StorageUtils.getExtendedPageId(page.getId(), page.getLanguage());
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
        PageIdProcessor parser = new PageIdProcessor(pageId);
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
        PageIdProcessor parser = new PageIdProcessor(pageId);
        XWikiEclipsePage ancestorPage =
            lastRetrievedPagesDataStorage.getPage(parser.getWiki(), parser.getSpace(), parser.getPage(),
                parser.getLanguage());
        return ancestorPage != null ? ancestorPage : null;

    }
}
