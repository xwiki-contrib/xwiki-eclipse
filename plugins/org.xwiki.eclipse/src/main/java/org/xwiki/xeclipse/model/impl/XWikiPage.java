package org.xwiki.xeclipse.model.impl;

import java.util.Date;
import java.util.Map;

import org.codehaus.swizzle.confluence.Page;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.model.XWikiConnectionException;

public class XWikiPage implements IXWikiPage
{
    private AbstractXWikiConnection connection;

    private String id;

    private Page page;

    public XWikiPage(AbstractXWikiConnection connection, String id, Map properties)
    {
        this.connection = connection;
        this.id = id;
        page = new Page(properties);
    }

    public String getContent()
    {
        String result = page.getContent();
        if (result != null) {
            return result;
        }

        getFullPageInformation();

        return page.getContent();
    }

    public String getContentStatus()
    {
        String result = page.getContentStatus();
        if (result != null) {
            return result;
        }

        getFullPageInformation();

        return page.getContentStatus();
    }

    public String getCreator()
    {
        String result = page.getCreator();
        if (result != null) {
            return result;
        }

        getFullPageInformation();

        return page.getCreator();
    }

    public Date getModified()
    {
        Date result = page.getModified();
        if (result != null) {
            return result;
        }

        getFullPageInformation();

        return page.getModified();
    }

    public String getModifier()
    {
        String result = page.getModifier();
        if (result != null) {
            return result;
        }

        getFullPageInformation();

        return page.getModifier();
    }

    public int getVersion()
    {
        try {
            page.getVersion();
        } catch (Exception e) {
            getFullPageInformation();
        }

        return page.getVersion();
    }

    public void save() throws XWikiConnectionException
    {
        connection.savePage(page);
    }

    public void setContent(String content)
    {
        page.setContent(content);
    }

    public String getId()
    {
        String result = page.getId();
        if (result != null) {
            return result;
        }

        getFullPageInformation();

        return page.getId();
    }

    public int getLocks()
    {
        try {
            page.getLocks();
        } catch (Exception e) {
            getFullPageInformation();
        }

        return page.getLocks();
    }

    public String getParentId()
    {
        String result = page.getParentId();
        if (result != null) {
            return result;
        }

        getFullPageInformation();

        return page.getParentId();
    }

    public String getSpace()
    {
        String result = page.getSpace();
        if (result != null) {
            return result;
        }

        getFullPageInformation();

        return page.getSpace();
    }

    public String getTitle()
    {
        String result = page.getTitle();
        if (result != null) {
            return result;
        }

        getFullPageInformation();

        return page.getTitle();
    }

    public String getUrl()
    {
        String result = page.getUrl();
        if (result != null) {
            return result;
        }

        getFullPageInformation();

        return page.getUrl();
    }

    public boolean isConflict()
    {
        return connection.isPageConflict(page.getId());
    }

    public boolean isDirty()
    {
        return connection.isPageDirty(page.getId());
    }

    private void getFullPageInformation()
    {
        try {
        Page page = connection.getRawPage(id);
        if (page != null) {
            this.page = page;
        }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
