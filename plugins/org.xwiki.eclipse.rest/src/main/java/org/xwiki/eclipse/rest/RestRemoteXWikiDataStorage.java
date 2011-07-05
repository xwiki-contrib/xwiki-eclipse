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
package org.xwiki.eclipse.rest;

import java.util.List;

import org.xwiki.rest.model.jaxb.Attachment;
import org.xwiki.rest.model.jaxb.HistorySummary;
import org.xwiki.rest.model.jaxb.ObjectSummary;
import org.xwiki.rest.model.jaxb.Page;
import org.xwiki.rest.model.jaxb.PageSummary;
import org.xwiki.rest.model.jaxb.Space;
import org.xwiki.rest.model.jaxb.Syntaxes;
import org.xwiki.rest.model.jaxb.Tag;
import org.xwiki.rest.model.jaxb.Wiki;
import org.xwiki.rest.model.jaxb.Xwiki;

/**
 * @version $Id$
 */
public class RestRemoteXWikiDataStorage
{
    private XWikiRESTClient restRemoteClient;

    private String endpoint;

    private String username;

    private String password;

    public RestRemoteXWikiDataStorage(String endpoint, String username, String password)
    {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
        this.restRemoteClient = new XWikiRESTClient(endpoint, username, password);
    }

    /**
     * @param username
     * @param password
     * @return
     */
    public List<Wiki> getWikis() throws Exception
    {

        try {
            return restRemoteClient.getWikis(username, password);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
    }

    public Xwiki getServerInfo()
    {
        return restRemoteClient.getServerInfo();
    }

    public List<Space> getSpaces(String spacesUrl)
    {
        try {
            return restRemoteClient.getSpaces(spacesUrl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param pagesUrl
     * @param username
     * @param password
     * @return
     */
    public List<PageSummary> getPages(String pagesUrl)
    {
        List<PageSummary> result = restRemoteClient.getPages(pagesUrl);
        return result;
    }

    /**
     * @param pageSummary
     */
    public List<ObjectSummary> getObjects(String objectsUrl)
    {
        List<ObjectSummary> result = this.restRemoteClient.getObjects(objectsUrl);
        return result;
    }

    /**
     * @param attachmentsUrl
     * @param username
     * @param password
     * @return
     */
    public List<Attachment> getAttachments(String attachmentsUrl)
    {
        if (attachmentsUrl != null) {
            List<Attachment> result = this.restRemoteClient.getAttachments(attachmentsUrl);
            return result;
        }

        return null;
    }

    /**
     * @return
     */
    public Syntaxes getSyntaxes(String syntaxesUrl)
    {
        return this.restRemoteClient.getSyntaxes(syntaxesUrl);
    }

    /**
     * @param pageId
     * @return
     */
    public List<HistorySummary> getPageHistory(String historyUrl)
    {
        if (historyUrl != null) {
            List<HistorySummary> result = this.restRemoteClient.getPageHistory(historyUrl);
            return result;
        }

        return null;
    }

    public Page getPage(String pageUrl)
    {
        try {
            return this.restRemoteClient.getPage(pageUrl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param pageSummary
     * @return
     */
    public org.xwiki.rest.model.jaxb.Class getPageClass(String classUrl)
    {
        try {
            return this.restRemoteClient.getClass(classUrl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param pageSummary
     * @return
     */
    public List<Tag> getTags(String tagsUrl)
    {
        try {
            return this.restRemoteClient.getTags(tagsUrl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
