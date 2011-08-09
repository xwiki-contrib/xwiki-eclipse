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
 */
package org.xwiki.eclipse.storage.utils;

/**
 * @version $Id$
 */
public class IdProcessor
{
    private String pageId;

    private String wiki;

    private String space;

    private String page;

    private String language = "";

    private String className;

    private int number;

    private String objectId;

    public IdProcessor(String id)
    {
        this.pageId = id;

        this.wiki = id.split(":")[0];
        String fullname = id.split(":")[1];
        String[] extendedIdParts = fullname.split("\\.");

        if (extendedIdParts.length == 3) {
            /* extended page id */
            this.space = extendedIdParts[0];
            this.page = extendedIdParts[1];
            this.language = extendedIdParts[2];
        } else if (extendedIdParts.length == 2) {
            /* page id */
            this.space = extendedIdParts[0];
            this.page = extendedIdParts[1];
        } else if (extendedIdParts.length == 4) {
            /* object id */
            this.space = extendedIdParts[0];
            this.page = extendedIdParts[1];
            this.className = extendedIdParts[2];
            this.number = Integer.parseInt(extendedIdParts[3]);
        }
    }

    public IdProcessor(String wiki, String space, String page, String className, int number)
    {
        super();
        this.wiki = wiki;
        this.space = space;
        this.page = page;
        this.className = className;
        this.number = number;
        this.objectId = wiki + ":" + space + "." + page + "." + className + "." + number;
    }

    public IdProcessor(String wiki, String space, String page, String language)
    {
        super();
        this.wiki = wiki;
        this.space = space;
        this.page = page;
        this.language = language;
        this.pageId = wiki + ":" + space + "." + page + "." + language;
    }

    public IdProcessor(String wiki, String space, String page)
    {
        super();
        this.wiki = wiki;
        this.space = space;
        this.page = page;
        this.pageId = wiki + ":" + space + "." + page;
    }

    public String getPageId()
    {
        return pageId;
    }

    public String getWiki()
    {
        return wiki;
    }

    public void setWiki(String wiki)
    {
        this.wiki = wiki;
    }

    public String getSpace()
    {
        return space;
    }

    public void setSpace(String space)
    {
        this.space = space;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public String getObjectId()
    {
        return objectId;
    }

    /**
     * @param pageId original page id
     * @param language
     * @return
     */
    public static String getExtendedPageId(String pageId, String language)
    {
        return pageId + (language.equals("") ? "" : "." + language);
    }

    public static String getExtendedObjectId(String pageId, String className, int number)
    {
        IdProcessor parser = new IdProcessor(pageId);
        return new IdProcessor(parser.getWiki(), parser.getSpace(), parser.getPage(), className, number).getObjectId();
    }
}
