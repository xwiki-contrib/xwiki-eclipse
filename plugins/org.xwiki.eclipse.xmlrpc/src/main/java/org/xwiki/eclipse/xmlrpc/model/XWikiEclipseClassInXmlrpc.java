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
package org.xwiki.eclipse.xmlrpc.model;

import org.eclipse.core.runtime.Assert;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.storage.AbstractDataManager;
import org.xwiki.xmlrpc.model.XWikiClass;

/**
 * A class representing an XWiki class.
 */
public class XWikiEclipseClassInXmlrpc extends XWikiEclipseClass
{
    private XWikiClass data;

    public XWikiEclipseClassInXmlrpc(AbstractDataManager dataManager, XWikiClass data)
    {
        super(dataManager);

        Assert.isNotNull(data);
        this.data = data;
    }

    public XWikiClass getData()
    {
        return data;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.model.XWikiEclipseClass#getId()
     */
    @Override
    public String getId()
    {
        return data.getId();
    }
}
