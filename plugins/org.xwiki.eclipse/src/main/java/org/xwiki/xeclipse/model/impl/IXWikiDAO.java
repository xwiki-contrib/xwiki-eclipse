package org.xwiki.xeclipse.model.impl;

import java.util.List;

import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.Space;
import org.codehaus.swizzle.confluence.SpaceSummary;

public interface IXWikiDAO
{
    public List<SpaceSummary> getSpaces() throws XWikiDAOException;
    public Space getSpace(String key) throws XWikiDAOException;
    public List<PageSummary> getPages(String spaceKey) throws XWikiDAOException;
    public Page getPage(String id) throws XWikiDAOException;
    public void storePage(Page page) throws XWikiDAOException;
    public void close() throws XWikiDAOException;
    public Space createSpace(String key, String name, String description) throws XWikiDAOException;
    public Page createPage(String spaceKey, String title, String content) throws XWikiDAOException;    
}
