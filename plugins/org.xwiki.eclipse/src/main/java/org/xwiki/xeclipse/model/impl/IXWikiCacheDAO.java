package org.xwiki.xeclipse.model.impl;

import java.util.Set;

import org.codehaus.swizzle.confluence.SpaceSummary;

public interface IXWikiCacheDAO extends IXWikiDAO
{
    public boolean isDirty(String pageId);
    public void setDirty(String pageId, boolean dirty);
    public Set<String> getDirtyPages();
    public boolean isInConflict(String pageId);
    public void setConflict(String pageId, boolean conflict);
    public void storeSpaceSummary(SpaceSummary spaceSummary) throws XWikiDAOException;
    public boolean isCached(String pageId);
}
