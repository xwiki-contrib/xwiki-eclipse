package org.xwiki.eclipse.storage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;
import org.xwiki.eclipse.storage.utils.IdProcessor;
import org.xwiki.eclipse.storage.utils.StorageUtils;
import org.xwiki.xmlrpc.model.XWikiPageHistorySummary;

/**
 * This class implements a local data storage for XWiki elements that uses the Eclipse resource component. The local
 * storage is rooted at an IFolder passed to the constructor. The structure of the local storage is the following:
 * 
 * <pre>
 * Root 
 * + wikis
 *   |- wiki1.xews (wiki summary)
 *   |- wiki2.xews
 * + spaces
 *   |- space1.xess (space summary)
 *   |- space2.xess  
 * + pages
 *   |- Page1.xeps (containing the page summary)
 *   |- Page1.xep (the actual page information)
 *   |- ...
 * + objects
 *   |- Object1.xeos (object summaries)
 *   |- Object1.xeo (the actual object information)
 *   |- ...
 * + classes
 *   |- Class1.xec (the actual class information)
 *   |- ...
 * </pre>
 * 
 * All xe* files contains an JSON serialization of the corresponding XWiki Eclipse elements.
 * 
 * @version $Id$
 */
public class LocalXWikiDataStorage
{
    private static final String WIKI_SUMMARY_FILE_EXTENSION = "xews"; //$NON-NLS-1$

    private static final String SPACE_SUMMARY_FILE_EXTENSION = "xess"; //$NON-NLS-1$

    private static final String PAGE_SUMMARY_FILE_EXTENSION = "xeps"; //$NON-NLS-1$

    private static final String PAGE_FILE_EXTENSION = "xep"; //$NON-NLS-1$

    protected static final Object OBJECT_SUMMARY_FILE_EXTENSION = "xeos"; //$NON-NLS-1$

    protected static final Object OBJECT_FILE_EXTENSION = "xeo"; //$NON-NLS-1$

    protected static final Object CLASS_FILE_EXTENSION = "xec"; //$NON-NLS-1$

    private IPath WIKIS_DIRECTORY = new Path("wikis"); //$NON-NLS-1$

    private IPath SPACES_DIRECTORY = new Path("spaces"); //$NON-NLS-1$

    private IPath PAGES_DIRECTORY = new Path("pages"); //$NON-NLS-1$

    private IPath OBJECTS_DIRECTORY = new Path("objects"); //$NON-NLS-1$

    private IPath CLASSES_DIRECTORY = new Path("classes"); //$NON-NLS-1$

    private IContainer baseFolder;

    public LocalXWikiDataStorage(IContainer baseFolder)
    {
        this.baseFolder = baseFolder;
    }

    public void dispose()
    {
        // Do nothing.
    }

    public XWikiEclipsePage getPage(String wiki, String space, String pageName, String language)
        throws XWikiEclipseStorageException
    {
        try {
            IFolder pageFolder = StorageUtils.createFolder(baseFolder.getFolder(PAGES_DIRECTORY));

            IFile pageFile = pageFolder.getFile(getFileNameForPage(wiki, space, pageName, language)); //$NON-NLS-1$
            if (pageFile.exists()) {
                XWikiEclipsePage result =
                    (XWikiEclipsePage) StorageUtils.readFromJSON(pageFile, XWikiEclipsePage.class.getCanonicalName());
                return result;
            }
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }

        return null;
    }

    private List<IResource> getChildResources(final IContainer parent, int depth) throws CoreException
    {
        final List<IResource> result = new ArrayList<IResource>();

        parent.accept(new IResourceVisitor()
        {
            public boolean visit(IResource resource) throws CoreException
            {
                if (!resource.equals(parent)) {
                    result.add(resource);
                }

                return true;
            }

        }, depth, IResource.NONE);

        return result;
    }

    public List<XWikiEclipseWikiSummary> getWikiSummaries() throws XWikiEclipseStorageException
    {
        final List<XWikiEclipseWikiSummary> result = new ArrayList<XWikiEclipseWikiSummary>();

        try {
            final IFolder wikiFolder = StorageUtils.createFolder(baseFolder.getFolder(WIKIS_DIRECTORY));

            List<IResource> wikiFolderResources = getChildResources(wikiFolder, IResource.DEPTH_ONE);
            for (IResource wikiFolderResource : wikiFolderResources) {
                if (wikiFolderResource instanceof IFile) {
                    IFile wikiFile = (IFile) wikiFolderResource;
                    XWikiEclipseWikiSummary wikiSummary;
                    try {
                        wikiSummary =
                            (XWikiEclipseWikiSummary) StorageUtils.readFromJSON(wikiFile,
                                XWikiEclipseWikiSummary.class.getCanonicalName());
                        result.add(wikiSummary);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        } catch (CoreException e) {
            throw new XWikiEclipseStorageException(e);
        }

        return result;

    }

    public XWikiEclipseWikiSummary storeWiki(final XWikiEclipseWikiSummary wiki) throws XWikiEclipseStorageException
    {
        try {
            ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
            {
                public void run(IProgressMonitor monitor) throws CoreException
                {
                    /* Write the wiki summary */
                    // XWikiEclipseWikiSummary wikiSummary = new XWikiEclipseWikiSummary(wiki.getDataManager());
                    // wikiSummary.setBaseUrl("local");
                    // wikiSummary.setName(wiki.getName());
                    // wikiSummary.setSyntaxes(wiki.getSyntaxes());
                    // wikiSummary.setVersion(wiki.getVersion());
                    // wikiSummary.setWikiId(wiki.getWikiId());

                    StorageUtils.writeToJson(
                        baseFolder.getFolder(WIKIS_DIRECTORY).getFile(getFileNameForWikiSummary(wiki.getWikiId())),
                        wiki);
                }
            }, null);
        } catch (CoreException e) {
            throw new XWikiEclipseStorageException(e);
        }

        return wiki;
    }

    public List<XWikiEclipseSpaceSummary> getSpaces(String wikiId) throws XWikiEclipseStorageException
    {

        final List<XWikiEclipseSpaceSummary> result = new ArrayList<XWikiEclipseSpaceSummary>();

        try {
            final IFolder spaceFolder = StorageUtils.createFolder(baseFolder.getFolder(SPACES_DIRECTORY));

            List<IResource> spaceFolderResources = getChildResources(spaceFolder, IResource.DEPTH_ONE);
            for (IResource spaceFolderResource : spaceFolderResources) {
                if (spaceFolderResource instanceof IFile) {
                    IFile wikiFile = (IFile) spaceFolderResource;
                    XWikiEclipseSpaceSummary spaceSummary;
                    try {
                        spaceSummary =
                            (XWikiEclipseSpaceSummary) StorageUtils.readFromJSON(wikiFile,
                                XWikiEclipseSpaceSummary.class.getCanonicalName());
                        if (spaceSummary.getWiki().equals(wikiId)) {
                            result.add(spaceSummary);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        } catch (CoreException e) {
            throw new XWikiEclipseStorageException(e);
        }

        return result;
    }

    public void removeSpace(String wiki, String space) throws XWikiEclipseStorageException
    {
        // Delete space summary file
        try {
            final IFolder spaceFolder = StorageUtils.createFolder(baseFolder.getFolder(SPACES_DIRECTORY));

            List<IResource> spacesFolderResources = getChildResources(spaceFolder, IResource.DEPTH_ONE);
            for (IResource spacesFolderResource : spacesFolderResources) {
                if (spacesFolderResource instanceof IFile) {
                    IFile spaceFile = (IFile) spacesFolderResource;
                    if (spaceFile.getName().equals(getFileNameForSpaceSummary(wiki, space))) {
                        spaceFile.delete(true, null);
                        break;
                    }
                }
            }
        } catch (CoreException e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    public XWikiEclipsePage storePage(final XWikiEclipsePage page) throws XWikiEclipseStorageException
    {
        try {
            ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
            {
                public void run(IProgressMonitor monitor) throws CoreException
                {
                    // XWikiEclipsePage p = new XWikiEclipsePage(page.getDataManager());
                    //
                    // p.setId(page.getId());
                    // p.setParentId(page.getParentId());
                    // p.setTitle(page.getTitle());
                    // p.setUrl(page.getUrl());
                    // p.setContent(page.getContent());
                    // p.setSpace(page.getSpace());
                    // p.setWiki(page.getWiki());
                    // p.setSpace(page.getSpace());
                    // p.setMajorVersion(page.getMajorVersion());
                    // p.setMinorVersion(page.getMinorVersion());
                    // p.setVersion(page.getVersion());
                    // p.setName(page.getName());
                    // p.setLanguage(page.getLanguage());

                    /* Write the page, considering the translation language */
                    String fileName =
                        getFileNameForPage(page.getWiki(), page.getSpace(), page.getName(), page.getLanguage());
                    StorageUtils.writeToJson(baseFolder.getFolder(PAGES_DIRECTORY).getFile(fileName), page);
                }
            }, null);
        } catch (CoreException e) {
            throw new XWikiEclipseStorageException(e);
        }

        return page;
    }

    /**
     * @param wiki
     * @param space
     * @return
     * @throws XWikiEclipseStorageException
     */
    public XWikiEclipseSpaceSummary getSpace(String wiki, String space) throws XWikiEclipseStorageException
    {
        List<XWikiEclipseSpaceSummary> spaces = getSpaces(wiki);
        for (XWikiEclipseSpaceSummary s : spaces) {
            if (s.getName().equals(space)) {
                return s;
            }
        }

        return null;
    }

    public XWikiEclipseObject getObject(String wiki, String space, String page, String className, int number)
        throws XWikiEclipseStorageException
    {
        try {
            IFolder objectsFolder = StorageUtils.createFolder(baseFolder.getFolder(OBJECTS_DIRECTORY));

            IFile objectFile = objectsFolder.getFile(getFileNameForObject(wiki, space, page, className, number)); //$NON-NLS-1$
            if (objectFile.exists()) {
                XWikiEclipseObject result =
                    (XWikiEclipseObject) StorageUtils.readFromJSON(objectFile,
                        XWikiEclipseObject.class.getCanonicalName());
                return result;
            }
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }

        return null;
    }

    public XWikiEclipseObject storeObject(final XWikiEclipseObject object) throws XWikiEclipseStorageException
    {
        try {
            ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
            {
                public void run(IProgressMonitor monitor) throws CoreException
                {
                    /* Write the objectSummary */
                    XWikiEclipseObjectSummary objectSummary = new XWikiEclipseObjectSummary(object.getDataManager());
                    objectSummary.setClassName(object.getClassName());
                    objectSummary.setWiki(object.getWiki());
                    objectSummary.setSpace(object.getSpace());
                    objectSummary.setPageName(object.getPageName());
                    objectSummary.setPageId(object.getPageId());

                    objectSummary.setId(object.getId());
                    objectSummary.setNumber(object.getNumber());

                    String fileName =
                        getFileNameForObjectSummary(objectSummary.getWiki(), objectSummary.getSpace(),
                            objectSummary.getPageName(), objectSummary.getClassName(), objectSummary.getNumber());
                    StorageUtils.writeToJson(baseFolder.getFolder(OBJECTS_DIRECTORY).getFile(fileName), objectSummary);

                    /* Write the object */
                    fileName =
                        getFileNameForObject(objectSummary.getWiki(), objectSummary.getSpace(),
                            objectSummary.getPageName(), objectSummary.getClassName(), objectSummary.getNumber());
                    StorageUtils.writeToJson(baseFolder.getFolder(OBJECTS_DIRECTORY).getFile(fileName), object);
                }
            }, null);
        } catch (CoreException e) {
            throw new XWikiEclipseStorageException(e);
        }

        return object;
    }

    /**
     * @param wiki
     * @return
     */
    public boolean wikiExists(String wiki)
    {
        IFile wikiFile = baseFolder.getFolder(WIKIS_DIRECTORY).getFile(getFileNameForWikiSummary(wiki));
        return wikiFile.exists();
    }

    public boolean spaceExists(String wiki, String space)
    {
        IFile spaceFile = baseFolder.getFolder(SPACES_DIRECTORY).getFile(getFileNameForSpaceSummary(wiki, space));
        return spaceFile.exists();
    }

    public boolean pageExists(String wiki, String space, String page, String language)
    {
        IFile pageFile = baseFolder.getFolder(PAGES_DIRECTORY).getFile(getFileNameForPage(wiki, space, page, language));
        return pageFile.exists();
    }

    public boolean objectExists(String wiki, String space, String page, String className, int number)
    {
        IFile objectFile =
            baseFolder.getFolder(OBJECTS_DIRECTORY).getFile(getFileNameForObject(wiki, space, page, className, number));
        return objectFile.exists();
    }

    private String getFileNameForSpaceSummary(String wiki, String space)
    {
        return String.format("%s.%s.%s", wiki, space, SPACE_SUMMARY_FILE_EXTENSION); //$NON-NLS-1$
    }

    private String getFileNameForWikiSummary(String wikiId)
    {
        return String.format("%s.%s", wikiId, WIKI_SUMMARY_FILE_EXTENSION); //$NON-NLS-1$
    }

    private String getFileNameForPageSummary(String wiki, String space, String page, String language)
    {
        if (language != null && !language.equals("")) {
            return String.format("%s.%s.%s.%s.%s", wiki, space, page, language, PAGE_SUMMARY_FILE_EXTENSION); //$NON-NLS-1$
        } else {
            return String.format("%s.%s.%s.%s", wiki, space, page, PAGE_SUMMARY_FILE_EXTENSION); //$NON-NLS-1$    
        }

    }

    private String getFileNameForPage(String wiki, String space, String page, String language)
    {
        if (language != null && !language.equals("")) {
            return String.format("%s.%s.%s.%s.%s", wiki, space, page, language, PAGE_FILE_EXTENSION); //$NON-NLS-1$
        } else {
            return String.format("%s.%s.%s.%s", wiki, space, page, PAGE_FILE_EXTENSION); //$NON-NLS-1$    
        }
    }

    private String getFileNameForObjectSummary(String wiki, String space, String page, String className, int number)
    {
        return String.format("%s.%s.%s.%s.%d.%s", wiki, space, page, className, number, OBJECT_SUMMARY_FILE_EXTENSION); //$NON-NLS-1$
    }

    private String getFileNameForObject(String wiki, String space, String page, String className, int number)
    {
        return String.format("%s.%s.%s.%s.%d.%s", wiki, space, page, className, number, OBJECT_FILE_EXTENSION); //$NON-NLS-1$
    }

    private String getFileNameForClass(String wiki, String classId)
    {
        return String.format("%s.%s.%s", wiki, classId, CLASS_FILE_EXTENSION); //$NON-NLS-1$
    }

    public List<XWikiEclipseClass> getClasses() throws Exception
    {
        List<XWikiEclipseClass> result = new ArrayList<XWikiEclipseClass>();

        List<IResource> classFolderResources =
            getChildResources(baseFolder.getFolder(CLASSES_DIRECTORY), IResource.DEPTH_ONE);
        for (IResource classFolderResource : classFolderResources) {
            if (classFolderResource instanceof IFile) {
                IFile file = (IFile) classFolderResource;
                if (file.getFileExtension().equals(CLASS_FILE_EXTENSION)) {
                    XWikiEclipseClass clazz =
                        (XWikiEclipseClass) StorageUtils.readFromJSON(file, XWikiEclipseClass.class.getCanonicalName());
                    result.add(clazz);
                }
            }
        }

        return result;
    }

    public boolean removeObject(final String wiki, final String space, final String page, final String className,
        final int number) throws XWikiEclipseStorageException
    {
        try {
            ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
            {
                public void run(IProgressMonitor monitor) throws CoreException
                {
                    IFile file =
                        baseFolder.getFolder(OBJECTS_DIRECTORY).getFile(
                            getFileNameForObjectSummary(wiki, space, page, className, number));
                    if (file.exists()) {
                        file.delete(true, null);
                    }

                    file =
                        baseFolder.getFolder(OBJECTS_DIRECTORY).getFile(
                            getFileNameForObject(wiki, space, page, className, number));
                    if (file.exists()) {
                        file.delete(true, null);
                    }
                }
            }, null);
        } catch (CoreException e) {
            new XWikiEclipseStorageException(e);
        }

        return true;
    }

    public List<XWikiPageHistorySummary> getPageHistory(String pageId) throws XWikiEclipseStorageException
    {
        // Currently not supported in local storage.
        return new ArrayList<XWikiPageHistorySummary>();
    }

    /**
     * @param spaceSummary
     */
    public XWikiEclipseSpaceSummary storeSpace(final XWikiEclipseSpaceSummary spaceSummary)
        throws XWikiEclipseStorageException
    {
        try {
            ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
            {
                public void run(IProgressMonitor monitor) throws CoreException
                {
                    /* Write the space summary */
                    // XWikiEclipseSpaceSummary space = new XWikiEclipseSpaceSummary(spaceSummary.getDataManager());
                    // space.setUrl("local");
                    // space.setName(spaceSummary.getName());
                    // space.setWiki(spaceSummary.getWiki());
                    // space.setId(spaceSummary.getId());

                    String fileName = getFileNameForSpaceSummary(spaceSummary.getWiki(), spaceSummary.getName());
                    StorageUtils.writeToJson(baseFolder.getFolder(SPACES_DIRECTORY).getFile(fileName), spaceSummary);
                }
            }, null);
        } catch (CoreException e) {
            throw new XWikiEclipseStorageException(e);
        }

        return spaceSummary;

    }

    /**
     * @param pageSummary
     */
    public XWikiEclipsePageSummary storePageSummary(final XWikiEclipsePageSummary pageSummary)
        throws XWikiEclipseStorageException
    {
        try {
            ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
            {
                public void run(IProgressMonitor monitor) throws CoreException
                {
                    /* Write the page summary */
                    // XWikiEclipsePageSummary p = new XWikiEclipsePageSummary(pageSummary.getDataManager());
                    // p.setUrl(pageSummary.getUrl());
                    // p.setLanguage(pageSummary.getLanguage());
                    // p.setName(pageSummary.getName());
                    // p.setWiki(pageSummary.getWiki());
                    // p.setSpace(pageSummary.getSpace());
                    // p.setId(pageSummary.getId());
                    // p.setParentId(pageSummary.getParentId());
                    // p.setTitle(pageSummary.getTitle());
                    // p.setSyntax(pageSummary.getSyntax());

                    String fileName =
                        getFileNameForPageSummary(pageSummary.getWiki(), pageSummary.getSpace(), pageSummary.getName(),
                            pageSummary.getLanguage());

                    StorageUtils.writeToJson(baseFolder.getFolder(PAGES_DIRECTORY).getFile(fileName), pageSummary);
                }
            }, null);
        } catch (CoreException e) {
            throw new XWikiEclipseStorageException(e);
        }

        return pageSummary;

    }

    /**
     * @param spaceSummary
     * @return
     */
    public List<XWikiEclipsePageSummary> getPageSummaries(String wiki, String space)
        throws XWikiEclipseStorageException
    {
        final List<XWikiEclipsePageSummary> result = new ArrayList<XWikiEclipsePageSummary>();

        try {
            final IFolder pagesFolder = StorageUtils.createFolder(baseFolder.getFolder(PAGES_DIRECTORY));

            List<IResource> pageFolderResources = getChildResources(pagesFolder, IResource.DEPTH_ONE);
            for (IResource pageFolderResource : pageFolderResources) {
                if (pageFolderResource instanceof IFile) {
                    IFile file = (IFile) pageFolderResource;
                    if (file.getFileExtension().equals(PAGE_SUMMARY_FILE_EXTENSION)) {
                        IFile pageSummaryFile = (IFile) pageFolderResource;
                        XWikiEclipsePageSummary pageSummary;
                        try {
                            pageSummary =
                                (XWikiEclipsePageSummary) StorageUtils.readFromJSON(pageSummaryFile,
                                    XWikiEclipsePageSummary.class.getCanonicalName());
                            if (pageSummary.getWiki().equals(wiki) && pageSummary.getSpace().equals(space)) {
                                result.add(pageSummary);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (CoreException e) {
            throw new XWikiEclipseStorageException(e);
        }

        return result;
    }

    /**
     * @param pageSummary
     * @return
     */
    public List<XWikiEclipsePageHistorySummary> getPageHistorySummaries(String wiki, String space, String pageName,
        String language)
    {
        /* Currently not supported in local storage. */
        return new ArrayList<XWikiEclipsePageHistorySummary>();
    }

    /**
     * @param pageId
     * @throws CoreException
     */
    public void removePage(final String pageId) throws CoreException
    {

        ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
        {

            @Override
            public void run(IProgressMonitor monitor) throws CoreException
            {
                IdProcessor parser = new IdProcessor(pageId);
                IFolder pageFolder;
                pageFolder = StorageUtils.createFolder(baseFolder.getFolder(PAGES_DIRECTORY));

                IFile pageFile =
                    pageFolder.getFile(getFileNameForPage(parser.getWiki(), parser.getSpace(), parser.getPage(),
                        parser.getLanguage())); //$NON-NLS-1$
                if (pageFile.exists()) {
                    pageFile.delete(true, null);
                }

                IFile pageSummaryFile =
                    pageFolder.getFile(getFileNameForPageSummary(parser.getWiki(), parser.getSpace(), parser.getPage(),
                        parser.getLanguage())); //$NON-NLS-1$
                if (pageSummaryFile.exists()) {
                    pageSummaryFile.delete(true, null);
                }

                try {
                    // remove the objects of this page as well
                    List<XWikiEclipseObjectSummary> objects =
                        getObjectSummaries(parser.getWiki(), parser.getSpace(), parser.getPage());
                    for (XWikiEclipseObjectSummary o : objects) {
                        removeObject(parser.getWiki(), parser.getSpace(), parser.getPage(), o.getClassName(),
                            o.getNumber());
                    }

                    // if the space does not contain any page, remove the space as well
                    List<XWikiEclipsePageSummary> remainingPages =
                        getPageSummaries(parser.getWiki(), parser.getSpace());
                    if (remainingPages != null && remainingPages.size() == 0) {
                        removeSpace(parser.getWiki(), parser.getSpace());
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, null);

    }

    /**
     * @param wiki
     * @param className
     * @return
     * @throws Exception
     */
    public XWikiEclipseClass getClass(String wiki, String className) throws Exception
    {
        IFolder classesFolder = StorageUtils.createFolder(baseFolder.getFolder(CLASSES_DIRECTORY));
        IFile classFile = classesFolder.getFile(getFileNameForClass(wiki, className));
        if (classFile.exists()) {
            XWikiEclipseClass result =
                (XWikiEclipseClass) StorageUtils.readFromJSON(classFile, XWikiEclipseClass.class.getCanonicalName());

            return result;
        }
        // TODO Auto-generated method stub
        return null;
    }

    public void storeClass(final XWikiEclipseClass xwikiClass) throws XWikiEclipseStorageException
    {
        try {
            ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
            {
                public void run(IProgressMonitor monitor) throws CoreException
                {
                    StorageUtils.writeToJson(
                        baseFolder.getFolder(CLASSES_DIRECTORY).getFile(
                            getFileNameForClass(xwikiClass.getWiki(), xwikiClass.getId())), xwikiClass);
                }
            }, null);
        } catch (CoreException e) {
            new XWikiEclipseStorageException(e);
        }
    }

    /**
     * @param wiki
     * @param space
     * @param page
     * @return
     * @throws Exception
     */
    public List<XWikiEclipseObjectSummary> getObjectSummaries(String wiki, String space, String page) throws Exception
    {
        List<XWikiEclipseObjectSummary> result = new ArrayList<XWikiEclipseObjectSummary>();
        final IFolder objectsFolder = StorageUtils.createFolder(baseFolder.getFolder(OBJECTS_DIRECTORY));

        List<IResource> objectsFolderResources = getChildResources(objectsFolder, IResource.DEPTH_ONE);
        for (IResource objectsFolderResource : objectsFolderResources) {
            if (objectsFolderResource instanceof IFile
                && ((IFile) objectsFolderResource).getFileExtension().equals(OBJECT_SUMMARY_FILE_EXTENSION)) {
                IFile objectSummaryFile = (IFile) objectsFolderResource;
                XWikiEclipseObjectSummary objectSummary =
                    (XWikiEclipseObjectSummary) StorageUtils.readFromJSON(objectSummaryFile,
                        XWikiEclipseObjectSummary.class.getCanonicalName());
                if (objectSummary.getWiki().equals(wiki) && objectSummary.getSpace().equals(space)
                    && objectSummary.getPageName().equals(page)) {
                    result.add(objectSummary);
                }
            }
        }

        return result;
    }

    /**
     * @param wiki
     * @param space
     * @param pageName
     * @param language
     * @return
     * @throws Exception
     */
    public XWikiEclipsePageSummary getPageSummary(String wiki, String space, String pageName, String language)
        throws Exception
    {
        String fileName = getFileNameForPageSummary(wiki, space, pageName, language);
        IFile pageSummaryFile = baseFolder.getFolder(PAGES_DIRECTORY).getFile(fileName);
        if (pageSummaryFile.exists()) {
            return (XWikiEclipsePageSummary) StorageUtils.readFromJSON(pageSummaryFile,
                XWikiEclipsePageSummary.class.getCanonicalName());
        }
        return null;
    }
}
