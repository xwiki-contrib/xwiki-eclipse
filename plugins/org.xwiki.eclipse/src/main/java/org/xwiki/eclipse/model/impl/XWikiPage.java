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
package org.xwiki.eclipse.model.impl;

import java.util.Date;
import java.util.Map;

import org.codehaus.swizzle.confluence.Page;
import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.IXWikiSpace;
import org.xwiki.eclipse.model.XWikiConnectionException;

public class XWikiPage implements IXWikiPage
{
    private AbstractXWikiConnection connection;

    private String id;

    private Page page;

    private IXWikiSpace space;

    @SuppressWarnings("unchecked")
    public XWikiPage(AbstractXWikiConnection connection, String id, IXWikiSpace space,
        Map properties)
    {
        this.connection = connection;
        this.id = id;
        page = new Page(properties);
        this.space = space;
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
        Page updatedPage = connection.savePage(page);
        this.page = updatedPage;
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

    public String getSpaceKey()
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IXWikiConnection getConnection()
    {
        return connection;
    }

    public boolean isCached()
    {
        return connection.isPageCached(page.getId());
    }

//    @Override
//    public int hashCode()
//    {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((id == null) ? 0 : id.hashCode());
//        return result;
//    }
//
//    @Override
//    public boolean equals(Object obj)
//    {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
//        final XWikiPage other = (XWikiPage) obj;
//        if (id == null) {
//            if (other.id != null)
//                return false;
//        } else if (!id.equals(other.id))
//            return false;
//        return true;
//    }

    
    
    public IXWikiSpace getSpace() throws XWikiConnectionException
    {
        return space;
    }

    public void remove() throws XWikiConnectionException
    {
        connection.removePage(this);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connection == null) ? 0 : connection.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final XWikiPage other = (XWikiPage) obj;
        if (connection == null) {
            if (other.connection != null)
                return false;
        } else if (!connection.equals(other.connection))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public String getExtendedId()
    {     
        return String.format("%s@%s#%s", connection.getUserName(), connection.getServerUrl(), id);
    }
}
