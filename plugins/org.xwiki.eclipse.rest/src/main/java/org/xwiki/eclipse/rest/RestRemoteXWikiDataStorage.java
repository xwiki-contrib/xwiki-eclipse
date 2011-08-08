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

import java.net.URL;
import java.util.List;

import org.xwiki.rest.model.jaxb.Attachment;
import org.xwiki.rest.model.jaxb.Class;
import org.xwiki.rest.model.jaxb.Comment;
import org.xwiki.rest.model.jaxb.HistorySummary;
import org.xwiki.rest.model.jaxb.Object;
import org.xwiki.rest.model.jaxb.ObjectSummary;
import org.xwiki.rest.model.jaxb.Page;
import org.xwiki.rest.model.jaxb.PageSummary;
import org.xwiki.rest.model.jaxb.Property;
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
    private XWikiRestClient restRemoteClient;

    private RestUrlBuilder urlBuilder;

    private String endpoint;

    private String username;

    private String password;

    public RestRemoteXWikiDataStorage(String endpoint, String username, String password)
    {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
        this.restRemoteClient = new XWikiRestClient(endpoint, username, password);
        this.urlBuilder = new RestUrlBuilder(endpoint);
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

    public Xwiki getServerInfo() throws Exception
    {
        return restRemoteClient.getServerInfo();
    }

    public List<Space> getSpaces(String wikiId)
    {
        String spacesUrl = urlBuilder.getSpacesUrl(wikiId);
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
    public synchronized List<PageSummary> getPages(String wiki, String space)
    {
        String pagesUrl = urlBuilder.getPagesUrl(wiki, space);
        List<PageSummary> result = restRemoteClient.getPages(pagesUrl);
        return result;
    }

    /**
     * @param pageSummary
     */
    public List<ObjectSummary> getObjectSummaries(String wiki, String space, String pageName)
    {
        String objectsUrl = urlBuilder.getObjectsUrl(wiki, space, pageName);
        List<ObjectSummary> result = this.restRemoteClient.getObjects(objectsUrl);
        return result;
    }

    /**
     * @param attachmentsUrl
     * @param username
     * @param password
     * @return
     */
    public List<Attachment> getAttachments(String wiki, String space, String pageName)
    {
        String attachmentsUrl = urlBuilder.getAttachmentsUrl(wiki, space, pageName);
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
    public List<HistorySummary> getPageHistories(String wiki, String space, String pageName, String language)
    {
        String historyUrl = urlBuilder.getHistoryUrl(wiki, space, pageName, language);
        if (historyUrl != null) {
            List<HistorySummary> result = this.restRemoteClient.getPageHistory(historyUrl);
            return result;
        }

        return null;
    }

    public Page getPage(String wiki, String space, String pageName, String language) throws Exception
    {
        String pageUrl = urlBuilder.getPageUrl(wiki, space, pageName, language);
        try {
            return this.restRemoteClient.getPage(pageUrl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            throw e;
        }
    }

    public org.xwiki.rest.model.jaxb.Class getClass(String wiki, String className)
    {
        String classUrl = urlBuilder.getclassUrl(wiki, className);
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
    public List<Tag> getTags(String wiki, String space, String pageName)
    {
        String tagsUrl = urlBuilder.getTagsUrl(wiki, space, pageName);
        try {
            return this.restRemoteClient.getTags(tagsUrl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param commentsUrl
     * @return
     */
    public List<Comment> getComments(String wiki, String space, String pageName)
    {
        String commentsUrl = urlBuilder.getCommentsUrl(wiki, space, pageName);
        try {
            return this.restRemoteClient.getComments(commentsUrl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param propertiesUrl
     * @return
     */
    public List<Property> getObjectProperties(String wiki, String space, String pageName, String className, int number)
    {
        String propertiesUrl = urlBuilder.getObjectPropertiesUrl(wiki, space, pageName, className, number);
        try {
            return this.restRemoteClient.getObjectProperties(propertiesUrl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param dir
     * @param absoluteUrl
     */
    public void download(String dir, String absoluteUrl, String name)
    {
        restRemoteClient.download(dir, absoluteUrl, name);
    }

    /**
     * @param objectUrl
     * @return
     */
    public Object getObject(String wiki, String space, String pageName, String className, int number)
    {
        String objectUrl = urlBuilder.getObjectUrl(wiki, space, pageName, className, number);
        try {
            return this.restRemoteClient.getObject(objectUrl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public Space getSpace(String wiki, String space)
    {
        List<Space> spaces = getSpaces(wiki);
        for (Space s : spaces) {
            if (s.getName().equals(space)) {
                return s;
            }
        }

        return null;
    }

    /**
     * @param comment
     * @return
     */
    public Comment storeComment(String commentsUrl, Comment comment)
    {
        try {
            return this.restRemoteClient.storeComment(commentsUrl, comment);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param attachmentUrl
     */
    public void removeAttachment(String attachmentUrl)
    {
        try {
            this.restRemoteClient.executeDelete(attachmentUrl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param attachmentsUrl
     * @param fileUrl
     */
    public void uploadAttachment(String wiki, String space, String pageName, String attachmentName, URL fileUrl)
    {
        String attachmentUrl = urlBuilder.getAttachmentUrl(wiki, space, pageName, attachmentName);
        this.restRemoteClient.uploadAttachment(attachmentUrl, attachmentName, fileUrl);
    }

    /**
     * @param wikiName
     * @return
     */
    public List<Tag> getAllTagsInWiki(String wikiName)
    {
        String allTagsUrl = this.endpoint + "/wikis/" + wikiName + "/tags";
        List<Tag> result;
        try {
            result = restRemoteClient.getAllTags(allTagsUrl);
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param tagsUrl
     * @param tagName
     * @return
     */
    public List<Tag> addTag(String wiki, String space, String pageName, String tagName)
    {
        String tagsUrl = urlBuilder.getTagsUrl(wiki, space, pageName);
        List<Tag> result;
        try {
            result = this.restRemoteClient.addTag(tagsUrl, tagName);
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param wiki
     * @return
     */
    public List<Class> getClasses(String wiki)
    {
        List<Class> result;
        try {
            result = this.restRemoteClient.getClasses(wiki);
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param pageUrl
     */
    public void removePage(String pageUrl)
    {
        try {
            this.restRemoteClient.executeDelete(pageUrl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param url
     */
    public void remove(String url)
    {
        try {
            this.restRemoteClient.executeDelete(url);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class RestUrlBuilder
    {
        private String endpoint;

        public RestUrlBuilder(String endpoint)
        {
            this.endpoint = endpoint;
        }

        /**
         * @param wiki
         * @param className
         * @return
         */
        public String getclassUrl(String wiki, String className)
        {
            return endpoint + "/wikis/" + wiki + "/classes/" + className;
        }

        /**
         * @param wiki
         * @param space
         * @param pageName
         * @return
         */
        public String getTagsUrl(String wiki, String space, String pageName)
        {
            return endpoint + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/tags";
        }

        /**
         * @param wiki
         * @param space
         * @param pageName
         * @param className
         * @param number
         * @return
         */
        public String getObjectUrl(String wiki, String space, String pageName, String className, int number)
        {
            return endpoint + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/objects/" + className
                + "/" + number;
        }

        /**
         * @param wiki
         * @param space
         * @param pageName
         * @param language
         * @return
         */
        public String getPageUrl(String wiki, String space, String pageName, String language)
        {
            if (language != null && !language.equals("")) {
                return endpoint + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/translations/"
                    + language;
            }
            return endpoint + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName;
        }

        /**
         * @param wiki
         * @param space
         * @param pageName
         * @param className
         * @param number
         * @return
         */
        public String getObjectPropertiesUrl(String wiki, String space, String pageName, String className, int number)
        {
            return endpoint + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/objects/" + className
                + "/" + number + "/properties";
        }

        /**
         * @param wiki
         * @param space
         * @param pageName
         * @return
         */
        public String getCommentsUrl(String wiki, String space, String pageName)
        {
            return endpoint + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/comments";
        }

        /**
         * @param wiki
         * @param space
         * @param pageName
         * @param language
         * @return
         */
        public String getHistoryUrl(String wiki, String space, String pageName, String language)
        {
            if (language != null && !language.equals("")) {
                return endpoint + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/translations/"
                    + language + "/history";
            }
            return endpoint + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/history";
        }

        /**
         * @param wiki
         * @param space
         * @param pageName
         * @return
         */
        public String getAttachmentUrl(String wiki, String space, String pageName, String attachmentName)
        {
            return endpoint + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/attachments/"
                + attachmentName;
        }

        public String getAttachmentsUrl(String wiki, String space, String pageName)
        {
            return endpoint + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/attachments";
        }

        /**
         * @param wiki
         * @param space
         * @param pageName
         * @return
         */
        public String getObjectsUrl(String wiki, String space, String pageName)
        {
            return endpoint + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/objects";
        }

        /**
         * @param wiki
         * @param space
         * @return
         */
        public String getPagesUrl(String wiki, String space)
        {
            return endpoint + "/wikis/" + wiki + "/spaces/" + space + "/pages";
        }

        public String getRootUrl()
        {
            return endpoint;
        }

        public String getWikisUrl()
        {
            return endpoint + "/wikis";
        }

        public String getSyntaxesUrl()
        {
            return endpoint + "/syntaxes";
        }

        public String getSpacesUrl(String wikiId)
        {
            return endpoint + "/wikis/" + wikiId + "/spaces";
        }
    }

    /**
     * @param page
     * @return
     */
    public Page storePage(Page page)
    {
        String pageUrl = urlBuilder.getPageUrl(page.getWiki(), page.getSpace(), page.getName(), page.getLanguage());
        Page result;
        try {
            result = restRemoteClient.storePage(pageUrl, page);
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
