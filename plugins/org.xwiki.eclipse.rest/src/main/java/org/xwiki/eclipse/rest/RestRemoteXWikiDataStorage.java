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

import java.net.URI;
import java.net.URISyntaxException;
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
    public synchronized List<ObjectSummary> getObjectSummaries(String wiki, String space, String pageName)
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
     * @param wiki
     * @param space
     * @param page
     * @param comment
     * @return
     */
    public Comment storeComment(String wiki, String space, String page, Comment comment)
    {
        String commentsUrl = urlBuilder.getCommentsUrl(wiki, space, page);
        try {
            return this.restRemoteClient.storeComment(commentsUrl, comment);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
        String allTagsUrl = urlBuilder.getTagsUrl(wikiName);
        List<Tag> result;
        try {
            result = restRemoteClient.getAllTags(allTagsUrl);
            return result;
        } catch (Exception e) {
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
            e.printStackTrace();
        }

        return null;
    }

    class RestUrlBuilder
    {
        private String endpoint;

        private String host;

        private int port;

        private String scheme;

        private String userInfo;

        private String pathPrefix;

        public RestUrlBuilder(String endpoint)
        {
            this.endpoint = endpoint;
            try {
                URI uri = new URI(endpoint);
                this.host = uri.getHost();
                this.port = uri.getPort();
                this.scheme = uri.getScheme();
                this.userInfo = uri.getUserInfo();
                this.pathPrefix = uri.getPath();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }

        /**
         * @param wikiName
         * @return
         */
        public String getTagsUrl(String wikiName)
        {
            try {
                String path = pathPrefix + "/wikis/" + wikiName + "/tags";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @param wiki
         * @param className
         * @return
         */
        public String getclassUrl(String wiki, String className)
        {
            try {
                String path = pathPrefix + "/wikis/" + wiki + "/classes/" + className;
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @param wiki
         * @param space
         * @param pageName
         * @return
         */
        public String getTagsUrl(String wiki, String space, String pageName)
        {
            try {
                String path = pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/tags";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
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
            try {
                String path =
                    pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/objects/" + className
                        + "/" + number;
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
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
            try {
                String path = null;
                if (language != null && !language.equals("")) {
                    path =
                        pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/translations/"
                            + language;
                } else {
                    path = pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName;

                }
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
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
            try {
                String path =
                    pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/objects/" + className
                        + "/" + number + "/properties";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @param wiki
         * @param space
         * @param pageName
         * @return
         */
        public String getCommentsUrl(String wiki, String space, String pageName)
        {
            try {
                String path = pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/comments";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
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
            try {
                String path = null;
                if (language != null && !language.equals("")) {
                    path =
                        pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/translations/"
                            + language + "/history";
                } else {
                    path = pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/history";
                }

                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @param wiki
         * @param space
         * @param pageName
         * @return
         */
        public String getAttachmentUrl(String wiki, String space, String pageName, String attachmentName)
        {
            try {
                String path =
                    pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/attachments/"
                        + attachmentName;
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getAttachmentsUrl(String wiki, String space, String pageName)
        {
            try {
                String path =
                    pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/attachments";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @param wiki
         * @param space
         * @param pageName
         * @return
         */
        public String getObjectsUrl(String wiki, String space, String pageName)
        {
            try {
                String path = pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/objects";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @param wiki
         * @param space
         * @return
         */
        public String getPagesUrl(String wiki, String space)
        {
            try {
                String path = pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getRootUrl()
        {
            try {
                URI uri = new URI(scheme, userInfo, host, port, pathPrefix, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getWikisUrl()
        {
            try {
                String path = pathPrefix + "/wikis";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getSyntaxesUrl()
        {
            try {
                String path = pathPrefix + "/syntaxes";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getSpacesUrl(String wikiId)
        {
            try {
                String path = pathPrefix + "/wikis/" + wikiId + "/spaces";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @param wiki
         * @param space
         * @param name
         * @param language
         * @param majorVersion
         * @param minorVersion
         * @return
         */
        public String getPageHistoryUrl(String wiki, String space, String name, String language, int majorVersion,
            int minorVersion)
        {
            try {
                String path = null;

                if (!language.equals("")) {
                    path =
                        pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + name + "/translations/"
                            + language + "/history/" + majorVersion + "." + minorVersion;
                } else {
                    path =
                        pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + name + "/history/"
                            + majorVersion + "." + minorVersion;
                }
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @param sourcePageId
         * @param newWiki
         * @param newSpace
         * @param newPageName
         * @return
         */
        public String getCopyPageUrl(String sourcePageId, String newWiki, String newSpace, String newPageName)
        {
            try {
                String path = pathPrefix + "/wikis/" + newWiki + "/spaces/" + newSpace + "/pages/" + newPageName;
                String query = "copyFrom=" + sourcePageId;
                URI uri = new URI(scheme, userInfo, host, port, path, query, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @param sourcePageId
         * @param newWiki
         * @param newSpace
         * @param newPageName
         * @return
         */
        public String getMovePageUrl(String sourcePageId, String newWiki, String newSpace, String newPageName)
        {
            try {
                String path = pathPrefix + "/wikis/" + newWiki + "/spaces/" + newSpace + "/pages/" + newPageName;
                String query = "moveFrom=" + sourcePageId;
                URI uri = new URI(scheme, userInfo, host, port, path, query, null);
                return uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
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
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param wiki
     * @param space
     * @param name
     * @param language
     * @param majorVersion
     * @param minorVersion
     * @return
     * @throws Exception
     */
    public Page getPageHistory(String wiki, String space, String name, String language, int majorVersion,
        int minorVersion) throws Exception
    {
        String pageUrl = urlBuilder.getPageHistoryUrl(wiki, space, name, language, majorVersion, minorVersion);
        return this.restRemoteClient.getPage(pageUrl);
    }

    /**
     * @param wiki
     * @param space
     * @param name
     * @param language
     */
    public void removePage(String wiki, String space, String name, String language) throws Exception
    {
        String pageUrl = urlBuilder.getPageUrl(wiki, space, name, language);
        this.restRemoteClient.executeDelete(pageUrl);
    }

    /**
     * @param o
     * @return
     */
    public Object storeObject(Object o)
    {

        String objectUrl = null;
        if (o.getNumber() == -1) {
            /* newly created object */
            objectUrl = urlBuilder.getObjectsUrl(o.getWiki(), o.getSpace(), o.getPageName());
        } else {
            objectUrl =
                urlBuilder.getObjectUrl(o.getWiki(), o.getSpace(), o.getPageName(), o.getClassName(), o.getNumber());
        }

        Object result;
        try {
            result = restRemoteClient.storeObject(objectUrl, o);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param wiki
     * @param space
     * @param pageName
     * @param className
     * @param number
     * @throws Exception
     */
    public void removeObject(String wiki, String space, String pageName, String className, int number) throws Exception
    {
        String objectUrl = urlBuilder.getObjectUrl(wiki, space, pageName, className, number);
        restRemoteClient.executeDelete(objectUrl);
    }

    /**
     * @param wiki
     * @param space
     * @param page
     * @param name
     */
    public void removeAttachment(String wiki, String space, String page, String attachmentName) throws Exception
    {
        String attachmentUrl = urlBuilder.getAttachmentUrl(wiki, space, page, attachmentName);
        this.restRemoteClient.executeDelete(attachmentUrl);
    }

    /**
     * @param wiki
     * @param space
     * @param page
     * @param tagName
     */
    public void removeTag(String wiki, String space, String page, String tagName) throws Exception
    {
        String tagsUrl = urlBuilder.getTagsUrl(wiki, space, page);
        this.restRemoteClient.removeTag(tagsUrl, tagName);
    }

    /**
     * @param sourcePageId
     * @param newWiki
     * @param newSpace
     * @param newPageName
     * @return
     * @throws Exception
     */
    public Page copyPage(Page sourcePageToBeCopied, String newWiki, String newSpace, String newPageName)
        throws Exception
    {
        String pageUrl = urlBuilder.getCopyPageUrl(sourcePageToBeCopied.getId(), newWiki, newSpace, newPageName);
        return restRemoteClient.renamePage(pageUrl, sourcePageToBeCopied);
    }

    /**
     * @param sourcePageToBeMoved
     * @param newWiki
     * @param newSpace
     * @param newPageName
     * @return
     */
    public Page movePage(Page sourcePageToBeMoved, String newWiki, String newSpace, String newPageName)
        throws Exception
    {
        String pageUrl = urlBuilder.getMovePageUrl(sourcePageToBeMoved.getId(), newWiki, newSpace, newPageName);
        return restRemoteClient.renamePage(pageUrl, sourcePageToBeMoved);
    }
}
