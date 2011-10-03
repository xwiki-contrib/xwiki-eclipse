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

    private RestURIBuilder uriBuilder;

    private String endpoint;

    private String username;

    private String password;

    public RestRemoteXWikiDataStorage(String endpoint, String username, String password)
    {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
        this.restRemoteClient = new XWikiRestClient(endpoint, username, password);
        this.uriBuilder = new RestURIBuilder(endpoint);
    }

    /**
     * @param username
     * @param password
     * @return
     */
    public List<Wiki> getWikis() throws Exception
    {
        URI wikisURI = uriBuilder.getWikisURI();
        try {
            return restRemoteClient.getWikis(wikisURI, username, password);
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
        URI spacesURI = uriBuilder.getSpacesURI(wikiId);
        try {
            return restRemoteClient.getSpaces(spacesURI);
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
        URI pagesURI = uriBuilder.getPagesURI(wiki, space);
        List<PageSummary> result = restRemoteClient.getPages(pagesURI);
        return result;
    }

    /**
     * @param pageSummary
     */
    public synchronized List<ObjectSummary> getObjectSummaries(String wiki, String space, String pageName)
    {
        URI objectsURI = uriBuilder.getObjectsURI(wiki, space, pageName);
        List<ObjectSummary> result = this.restRemoteClient.getObjects(objectsURI);
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
        URI attachmentsURI = uriBuilder.getAttachmentsURI(wiki, space, pageName);
        if (attachmentsURI != null) {
            List<Attachment> result = this.restRemoteClient.getAttachments(attachmentsURI);
            return result;
        }

        return null;
    }

    /**
     * @return
     */
    public Syntaxes getSyntaxes(URI syntaxesURI)
    {
        return this.restRemoteClient.getSyntaxes(syntaxesURI);
    }

    /**
     * @param pageId
     * @return
     */
    public List<HistorySummary> getPageHistories(String wiki, String space, String pageName, String language)
    {
        URI historyURI = uriBuilder.getHistoryURI(wiki, space, pageName, language);
        if (historyURI != null) {
            List<HistorySummary> result = this.restRemoteClient.getPageHistory(historyURI);
            return result;
        }

        return null;
    }

    public Page getPage(String wiki, String space, String pageName, String language) throws Exception
    {
        URI pageURI = uriBuilder.getPageURI(wiki, space, pageName, language);
        try {
            return this.restRemoteClient.getPage(pageURI);
        } catch (Exception e) {
            // e.printStackTrace();
            throw e;
        }
    }

    public org.xwiki.rest.model.jaxb.Class getClass(String wiki, String className)
    {
        URI classURI = uriBuilder.getClassURI(wiki, className);
        try {
            return this.restRemoteClient.getClass(classURI);
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
        URI tagsURI = uriBuilder.getTagsURI(wiki, space, pageName);
        try {
            return this.restRemoteClient.getTags(tagsURI);
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
        URI commentsURI = uriBuilder.getCommentsURI(wiki, space, pageName);
        try {
            return this.restRemoteClient.getComments(commentsURI);
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
        URI propertiesURI = uriBuilder.getObjectPropertiesURI(wiki, space, pageName, className, number);
        try {
            return this.restRemoteClient.getObjectProperties(propertiesURI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param dir
     * @param absoluteURI
     * @param name
     */
    public void download(String dir, URI absoluteURI, String name)
    {
        restRemoteClient.download(dir, absoluteURI, name);
    }

    /**
     * @param wiki
     * @param space
     * @param pageName
     * @param className
     * @param number
     * @return
     */
    public Object getObject(String wiki, String space, String pageName, String className, int number)
    {
        URI objectURI = uriBuilder.getObjectURI(wiki, space, pageName, className, number);
        try {
            return this.restRemoteClient.getObject(objectURI);
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
        URI commentsURI = uriBuilder.getCommentsURI(wiki, space, page);
        try {
            return this.restRemoteClient.storeComment(commentsURI, comment);
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
        URI attachmentURI = uriBuilder.getAttachmentURI(wiki, space, pageName, attachmentName);
        this.restRemoteClient.uploadAttachment(attachmentURI, attachmentName, fileUrl);
    }

    /**
     * @param wikiName
     * @return
     */
    public List<Tag> getAllTagsInWiki(String wikiName)
    {
        URI allTagsURI = uriBuilder.getTagsURI(wikiName);
        List<Tag> result;
        try {
            result = restRemoteClient.getAllTags(allTagsURI);
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
        URI tagsURI = uriBuilder.getTagsURI(wiki, space, pageName);
        List<Tag> result;
        try {
            result = this.restRemoteClient.addTag(tagsURI, tagName);
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
        URI classesURI = uriBuilder.getClassesURI(wiki);
        List<Class> result;
        try {
            result = this.restRemoteClient.getClasses(classesURI);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    class RestURIBuilder
    {
        private String endpoint;

        private String host;

        private int port;

        private String scheme;

        private String userInfo;

        private String pathPrefix;

        public RestURIBuilder(String endpoint)
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
         * @param wiki
         * @return
         */
        public URI getClassesURI(String wiki)
        {
            try {
                String path = pathPrefix + "/wikis/" + wiki + "/classes";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @param wikiName
         * @return
         */
        public URI getTagsURI(String wikiName)
        {
            try {
                String path = pathPrefix + "/wikis/" + wikiName + "/tags";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
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
        public URI getClassURI(String wiki, String className)
        {
            try {
                String path = pathPrefix + "/wikis/" + wiki + "/classes/" + className;
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
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
        public URI getTagsURI(String wiki, String space, String pageName)
        {
            try {
                String path = pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/tags";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
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
        public URI getObjectURI(String wiki, String space, String pageName, String className, int number)
        {
            try {
                String path =
                    pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/objects/" + className
                        + "/" + number;
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
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
        public URI getPageURI(String wiki, String space, String pageName, String language)
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
                return uri;
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
        public URI getObjectPropertiesURI(String wiki, String space, String pageName, String className, int number)
        {
            try {
                String path =
                    pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/objects/" + className
                        + "/" + number + "/properties";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
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
        public URI getCommentsURI(String wiki, String space, String pageName)
        {
            try {
                String path = pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/comments";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
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
        public URI getHistoryURI(String wiki, String space, String pageName, String language)
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
                return uri;
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
        public URI getAttachmentURI(String wiki, String space, String pageName, String attachmentName)
        {
            try {
                String path =
                    pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/attachments/"
                        + attachmentName;
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        public URI getAttachmentsURI(String wiki, String space, String pageName)
        {
            try {
                String path =
                    pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/attachments";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
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
        public URI getObjectsURI(String wiki, String space, String pageName)
        {
            try {
                String path = pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages/" + pageName + "/objects";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
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
        public URI getPagesURI(String wiki, String space)
        {
            try {
                String path = pathPrefix + "/wikis/" + wiki + "/spaces/" + space + "/pages";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        public URI getRootURI()
        {
            try {
                URI uri = new URI(scheme, userInfo, host, port, pathPrefix, null, null);
                return uri;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        public URI getWikisURI()
        {
            try {
                String path = pathPrefix + "/wikis";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        public URI getSyntaxesURI()
        {
            try {
                String path = pathPrefix + "/syntaxes";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        public URI getSpacesURI(String wikiId)
        {
            try {
                String path = pathPrefix + "/wikis/" + wikiId + "/spaces";
                URI uri = new URI(scheme, userInfo, host, port, path, null, null);
                return uri;
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
        public URI getPageHistoryURI(String wiki, String space, String name, String language, int majorVersion,
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
                return uri;
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
        public URI getCopyPageURI(String sourcePageId, String newWiki, String newSpace, String newPageName)
        {
            try {
                String path = pathPrefix + "/wikis/" + newWiki + "/spaces/" + newSpace + "/pages/" + newPageName;
                String query = "copyFrom=" + sourcePageId;
                URI uri = new URI(scheme, userInfo, host, port, path, query, null);
                return uri;
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
        public URI getMovePageURI(String sourcePageId, String newWiki, String newSpace, String newPageName)
        {
            try {
                String path = pathPrefix + "/wikis/" + newWiki + "/spaces/" + newSpace + "/pages/" + newPageName;
                String query = "moveFrom=" + sourcePageId;
                URI uri = new URI(scheme, userInfo, host, port, path, query, null);
                return uri;
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
        URI pageURI = uriBuilder.getPageURI(page.getWiki(), page.getSpace(), page.getName(), page.getLanguage());
        Page result;
        try {
            result = restRemoteClient.storePage(pageURI, page);
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
        URI pageURI = uriBuilder.getPageHistoryURI(wiki, space, name, language, majorVersion, minorVersion);
        return this.restRemoteClient.getPage(pageURI);
    }

    /**
     * @param wiki
     * @param space
     * @param name
     * @param language
     */
    public void removePage(String wiki, String space, String name, String language) throws Exception
    {
        URI pageURI = uriBuilder.getPageURI(wiki, space, name, language);
        this.restRemoteClient.executeDelete(pageURI);
    }

    /**
     * @param o
     * @return
     */
    public Object storeObject(Object o)
    {

        URI objectURI = null;
        if (o.getNumber() == -1) {
            /* newly created object */
            objectURI = uriBuilder.getObjectsURI(o.getWiki(), o.getSpace(), o.getPageName());
        } else {
            objectURI =
                uriBuilder.getObjectURI(o.getWiki(), o.getSpace(), o.getPageName(), o.getClassName(), o.getNumber());
        }

        Object result;
        try {
            result = restRemoteClient.storeObject(objectURI, o);
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
        URI objectURI = uriBuilder.getObjectURI(wiki, space, pageName, className, number);
        restRemoteClient.executeDelete(objectURI);
    }

    /**
     * @param wiki
     * @param space
     * @param page
     * @param name
     */
    public void removeAttachment(String wiki, String space, String page, String attachmentName) throws Exception
    {
        URI attachmentURI = uriBuilder.getAttachmentURI(wiki, space, page, attachmentName);
        this.restRemoteClient.executeDelete(attachmentURI);
    }

    /**
     * @param wiki
     * @param space
     * @param page
     * @param tagName
     */
    public void removeTag(String wiki, String space, String page, String tagName) throws Exception
    {
        URI tagsURI = uriBuilder.getTagsURI(wiki, space, page);
        this.restRemoteClient.removeTag(tagsURI, tagName);
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
        URI pageURI = uriBuilder.getCopyPageURI(sourcePageToBeCopied.getId(), newWiki, newSpace, newPageName);
        return restRemoteClient.renamePage(pageURI, sourcePageToBeCopied);
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
        URI pageURI = uriBuilder.getMovePageURI(sourcePageToBeMoved.getId(), newWiki, newSpace, newPageName);
        return restRemoteClient.renamePage(pageURI, sourcePageToBeMoved);
    }
}
