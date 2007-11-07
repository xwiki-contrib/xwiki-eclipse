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
package org.xwiki.xeclipse.model.impl;

import java.util.Collection;
import java.util.Map;

import org.codehaus.swizzle.confluence.Space;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.model.IXWikiSpace;
import org.xwiki.xeclipse.model.XWikiConnectionException;

/**
 * Implementation of {@link IXWikiSpace}
 */
public class XWikiSpace implements IXWikiSpace
{
    private AbstractXWikiConnection connection;

    private Space space;

    private String key;

    /**
     * Constructor.
     * 
     * @param connection The connection this instance is linked to.
     * @param spaceSummary The raw space object provided by the remote XWiki instance containing the
     *            information.
     */
    @SuppressWarnings("unchecked")
    public XWikiSpace(AbstractXWikiConnection connection, String key, Map properties)
    {
        this.connection = connection;
        this.key = key;
        space = new Space(properties);
    }

    public String getDescription()
    {
        String result = space.getDescription();
        if (result != null) {
            return result;
        }

        getFullSpaceInformation();

        return space.getDescription();
    }

    public String getHomePage()
    {
        String result = space.getHomepage();
        if (result != null) {
            return result;
        }

        getFullSpaceInformation();

        return space.getHomepage();
    }

    public String getKey()
    {
        String result = space.getKey();
        if (result != null) {
            return result;
        }

        getFullSpaceInformation();

        return space.getKey();
    }

    public String getName()
    {
        String result = space.getName();
        if (result != null) {
            return result;
        }

        getFullSpaceInformation();

        return space.getName();
    }

    public Collection<IXWikiPage> getPages() throws XWikiConnectionException
    {
        return connection.getPages(this);
    }

    public String getType()
    {
        String result = space.getType();
        if (result != null) {
            return result;
        }

        getFullSpaceInformation();

        return space.getType();
    }

    public String getUrl()
    {
        String result = space.getUrl();
        if (result != null) {
            return result;
        }

        getFullSpaceInformation();

        return space.getUrl();
    }

    private void getFullSpaceInformation()
    {
        try {
            Space space = connection.getRawSpace(key);
            if (space != null) {
                this.space = space;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public IXWikiPage createPage(String name, String content) throws XWikiConnectionException
    {
        return connection.createPage(this, name, content);
    }

    public IXWikiConnection getConnection()
    {
        return connection;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
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
        final XWikiSpace other = (XWikiSpace) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

    public void remove() throws XWikiConnectionException
    {
        connection.removeSpace(this);
        
    }

}
