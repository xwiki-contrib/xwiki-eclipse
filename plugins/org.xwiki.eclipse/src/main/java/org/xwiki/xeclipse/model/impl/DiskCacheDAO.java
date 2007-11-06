package org.xwiki.xeclipse.model.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.Space;
import org.codehaus.swizzle.confluence.SpaceSummary;

/**
 * A Data Access Object for accessing to the local cache.
 */
public class DiskCacheDAO implements IXWikiCacheDAO
{

    /**
     * A class that contains all the indexes for handling the cache. Objects of this class will be
     * serialized in order to provide persistence to the indexes. This is a convenience class for
     * storing all the indexes in a single step.
     */
    private static class IndexAggregate implements Serializable
    {
        private static final long serialVersionUID = 7034420710276064016L;

        /**
         * A mapping that associates a page id to the file name under which page information have
         * been stored in the cache.
         */
        private Map<String, String> pageToDataFileNameIndex;

        /**
         * A mapping that associates a page to the space where it belongs to.
         */
        private HashMap<String, String> pageToSpaceIndex;

        /**
         * A mapping that associates a space id to the list of the pages id contained in that space.
         */
        private Map<String, Set<String>> spaceToPagesIndex;

        /**
         * A mapping that associates a space id to the file name under which space information have
         * been stored in the cache.
         */
        private Map<String, String> spaceToDataFileNameIndex;

        /**
         * A list of page ids that are marked as dirty.
         */
        private Set<String> dirtyPagesIndex;

        /**
         * A list of page ids that are marked as in conflict.
         */
        private Set<String> conflictPagesIndex;

        public IndexAggregate()
        {
            pageToDataFileNameIndex = new HashMap<String, String>();
            pageToSpaceIndex = new HashMap<String, String>();
            spaceToPagesIndex = new HashMap<String, Set<String>>();
            spaceToDataFileNameIndex = new HashMap<String, String>();
            dirtyPagesIndex = new HashSet<String>();
            conflictPagesIndex = new HashSet<String>();
        }

        public Map<String, String> getPageToDataFileNameIndex()
        {
            return pageToDataFileNameIndex;
        }

        public Map<String, Set<String>> getSpaceToPagesIndex()
        {
            return spaceToPagesIndex;
        }

        public Map<String, String> getSpaceToDataFileNameIndex()
        {
            return spaceToDataFileNameIndex;
        }

        public Set<String> getDirtyPagesIndex()
        {
            return dirtyPagesIndex;
        }

        public Set<String> getConflictPagesIndex()
        {
            return conflictPagesIndex;
        }

        public static long getSerialVersionUID()
        {
            return serialVersionUID;
        }

        public HashMap<String, String> getPageToSpaceIndex()
        {
            return pageToSpaceIndex;
        }

    }

    private static final String INDEXES_FILENAME = "indexes.data";

    /**
     * The directory where the cache will store its data.
     */
    private File cacheDir;

    /**
     * Cache indexes.
     */
    private IndexAggregate indexAggregate;

    /**
     * Constructor.
     * 
     * @param cacheDir The directory to be used for storing cache files.
     * @throws XWikiDAOException
     */
    public DiskCacheDAO(File cacheDir) throws XWikiDAOException
    {
        this.cacheDir = cacheDir;
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                throw new XWikiDAOException("Cannot create cache dir");
            }
        }

        /* Read cache indexes or create a new one if it doesn't exist or read/error */
        try {
            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(new File(cacheDir, INDEXES_FILENAME)));
            indexAggregate = (IndexAggregate) ois.readObject();
            ois.close();
        } catch (Exception e) {
            indexAggregate = new IndexAggregate();
        }
    }

    /**
     * Write cache indexes to disk.
     * 
     * @throws XWikiDAOException
     */
    synchronized void flushIndexes() throws XWikiDAOException
    {
        try {
            ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(new File(cacheDir, INDEXES_FILENAME)));
            oos.writeObject(indexAggregate);
            oos.close();
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }
    }

    /**
     * Release all the resources allocated for the cache.
     */
    public void close()
    {
        try {
            flushIndexes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @return A list of the available spaces in the cache that have at least one page cached.
     * @throws XWikiDAOException
     */
    @SuppressWarnings("unchecked")
    public List<SpaceSummary> getSpaces() throws XWikiDAOException
    {
        List<SpaceSummary> result = new ArrayList<SpaceSummary>();

        try {
            for (String spaceKey : indexAggregate.getSpaceToPagesIndex().keySet()) {
                if (!indexAggregate.getSpaceToPagesIndex().get(spaceKey).isEmpty()) {
                    String dataFileName =
                        indexAggregate.getSpaceToDataFileNameIndex().get(spaceKey);
                    ObjectInputStream ois =
                        new ObjectInputStream(new FileInputStream(new File(cacheDir, dataFileName)));
                    Map map = (Map) ois.readObject();
                    ois.close();
                    result.add(new SpaceSummary(map));
                }
            }
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }

        return result;
    }

    /**
     * Stores space summary information in the cache.
     * 
     * @param spaceSummary The space information to be stored.
     * @throws XWikiDAOException
     */
    public void storeSpaceSummary(SpaceSummary spaceSummary) throws XWikiDAOException
    {
        try {
            String dataFileName = String.format("%s.space", spaceSummary.getKey());
            ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(new File(cacheDir, dataFileName)));
            oos.writeObject(spaceSummary.toMap());
            oos.close();

            indexAggregate.getSpaceToDataFileNameIndex().put(spaceSummary.getKey(), dataFileName);
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }
    }

    /**
     * @param spaceKey The space key.
     * @return The page summaries for all the pages available in the given space.
     * @throws XWikiDAOException
     */
    @SuppressWarnings("unchecked")
    public List<PageSummary> getPages(String spaceKey) throws XWikiDAOException
    {
        List<PageSummary> result = new ArrayList<PageSummary>();

        try {
            Set<String> pageIds = indexAggregate.getSpaceToPagesIndex().get(spaceKey);
            if (pageIds != null) {
                for (String pageId : pageIds) {
                    String dataFileName = indexAggregate.pageToDataFileNameIndex.get(pageId);
                    ObjectInputStream ois =
                        new ObjectInputStream(new FileInputStream(new File(cacheDir, dataFileName)));
                    Map map = (Map) ois.readObject();
                    ois.close();

                    result.add(new PageSummary(map));
                }
            }
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }

        return result;
    }

    /**
     * @param id The page id.
     * @return All the page information for the page identified by the given id.
     * @throws XWikiDAOException
     */
    @SuppressWarnings("unchecked")
    public Page getPage(String pageId) throws XWikiDAOException
    {
        Page result = null;
        try {
            String dataFileName = indexAggregate.pageToDataFileNameIndex.get(pageId);
            if (dataFileName == null) {
                return null;
            }

            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(new File(cacheDir, dataFileName)));
            Map map = (Map) ois.readObject();
            ois.close();

            result = new Page(map);
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }

        return result;
    }

    /**
     * Stores a page in the cache.
     * 
     * @param page The page information to be stored.
     * @throws XWikiDAOException
     */
    public void storePage(Page page) throws XWikiDAOException
    {
        try {
            String dataFileName = String.format("%s.page", page.getId());
            ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(new File(cacheDir, dataFileName)));
            oos.writeObject(page.toMap());
            oos.close();

            indexAggregate.getPageToDataFileNameIndex().put(page.getId(), dataFileName);
            Set<String> pagesInSpace = indexAggregate.getSpaceToPagesIndex().get(page.getSpace());
            if (pagesInSpace == null) {
                pagesInSpace = new HashSet<String>();
                indexAggregate.getSpaceToPagesIndex().put(page.getSpace(), pagesInSpace);
            }
            pagesInSpace.add(page.getId());
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }
    }

    /* Since we store only space summaries, return a space with only space summary content */
    @SuppressWarnings("unchecked")
    public Space getSpace(String key) throws XWikiDAOException
    {
        Space result = null;

        try {
            String dataFileName = indexAggregate.getSpaceToDataFileNameIndex().get(key);
            if (dataFileName != null) {
                ObjectInputStream ois =
                    new ObjectInputStream(new FileInputStream(new File(cacheDir, dataFileName)));
                Map map = (Map) ois.readObject();
                ois.close();
                result = new Space(map);
            }
        } catch (Exception e) {
            throw new XWikiDAOException(e);
        }

        return result;
    }

    /**
     * @param pageId The page id.
     * @return true if the page identified by the id is marked as dirty.
     */
    public boolean isDirty(String pageId)
    {
        return indexAggregate.dirtyPagesIndex.contains(pageId);
    }

    /**
     * Sets the dirty state of a page.
     * 
     * @param pageId The page id.
     * @param dirty The new dirty state.
     */
    public void setDirty(String pageId, boolean dirty)
    {
        if (dirty) {
            indexAggregate.dirtyPagesIndex.add(pageId);
        } else {
            indexAggregate.dirtyPagesIndex.remove(pageId);
        }
    }

    /**
     * @param pageId The page id.
     * @return true if the page identified by the id is marked as in conflict.
     */
    public boolean isInConflict(String pageId)
    {
        return indexAggregate.conflictPagesIndex.contains(pageId);
    }

    /**
     * Sets the "in conflict" state of a page.
     * 
     * @param pageId The page id.
     * @param conflict The new "in conflict" state.
     */
    public void setConflict(String pageId, boolean conflict)
    {
        if (conflict) {
            indexAggregate.conflictPagesIndex.add(pageId);
        } else {
            indexAggregate.conflictPagesIndex.remove(pageId);
        }
    }

    /**
     * @return A collection of the id of all the pages marked dirty.
     */
    public Set<String> getDirtyPages()
    {
        return indexAggregate.getDirtyPagesIndex();
    }

    public boolean isCached(String pageId)
    {
        return indexAggregate.getPageToDataFileNameIndex().get(pageId) != null ? true : false;
    }

    public Space createSpace(String key, String name, String description)
        throws XWikiDAOException
    {
        throw new XWikiDAOException("Cannot create spaces on a local cache");
    }

    public Page createPage(String spaceKey, String title, String content)
        throws XWikiDAOException
    {
        throw new XWikiDAOException("Cannot create pages on a local cache");
    }

    public void removePage(String id) throws XWikiDAOException
    {
        String space = indexAggregate.getPageToSpaceIndex().get(id);
        indexAggregate.getPageToDataFileNameIndex().remove(id);
        indexAggregate.getDirtyPagesIndex().remove(id);
        indexAggregate.getConflictPagesIndex().remove(id);
        Set<String> pages = indexAggregate.getSpaceToPagesIndex().get(space);
        if(pages != null) {
            pages.remove(id);
        }
        indexAggregate.getPageToSpaceIndex().remove(id);

        /* TODO: Remove also files from the filesystem */
    }

    public void removeSpace(String key) throws XWikiDAOException
    {
        Set<String> pages = indexAggregate.getSpaceToPagesIndex().get(key);
        indexAggregate.getSpaceToDataFileNameIndex().remove(key);
        indexAggregate.getSpaceToPagesIndex().remove(key);
        if (pages != null) {
            for (String id : pages) {
                removePage(id);
            }
        }

        /* TODO: Remove space also from fileSystem */
    }
}
