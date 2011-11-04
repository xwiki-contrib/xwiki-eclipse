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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.xwiki.rest.model.jaxb.Attachment;
import org.xwiki.rest.model.jaxb.Attachments;
import org.xwiki.rest.model.jaxb.Class;
import org.xwiki.rest.model.jaxb.Classes;
import org.xwiki.rest.model.jaxb.Comment;
import org.xwiki.rest.model.jaxb.Comments;
import org.xwiki.rest.model.jaxb.History;
import org.xwiki.rest.model.jaxb.HistorySummary;
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.LinkCollection;
import org.xwiki.rest.model.jaxb.Object;
import org.xwiki.rest.model.jaxb.ObjectFactory;
import org.xwiki.rest.model.jaxb.ObjectSummary;
import org.xwiki.rest.model.jaxb.Objects;
import org.xwiki.rest.model.jaxb.Page;
import org.xwiki.rest.model.jaxb.PageSummary;
import org.xwiki.rest.model.jaxb.Pages;
import org.xwiki.rest.model.jaxb.Properties;
import org.xwiki.rest.model.jaxb.Property;
import org.xwiki.rest.model.jaxb.Space;
import org.xwiki.rest.model.jaxb.Spaces;
import org.xwiki.rest.model.jaxb.Syntaxes;
import org.xwiki.rest.model.jaxb.Tag;
import org.xwiki.rest.model.jaxb.Tags;
import org.xwiki.rest.model.jaxb.Wiki;
import org.xwiki.rest.model.jaxb.Wikis;
import org.xwiki.rest.model.jaxb.Xwiki;

/**
 * @version $Id$
 */
public class XWikiRestClient
{

    private URI serverURI;

    protected Marshaller marshaller;

    protected Unmarshaller unmarshaller;

    protected ObjectFactory objectFactory;

    private String username;

    private String password;

    public XWikiRestClient(String serverURLAsString, String username, String password)
    {
        try {
            this.serverURI = new URI(serverURLAsString);
            this.username = username;
            this.password = password;

            JAXBContext context = JAXBContext.newInstance("org.xwiki.rest.model.jaxb");
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();

        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        objectFactory = new ObjectFactory();
    }

    public boolean login(String username, String password) throws Exception
    {
        try {
            HttpResponse loginResponse = executeGet(serverURI, username, password);

            int statusCode = loginResponse.getStatusLine().getStatusCode();

            if (HttpStatus.SC_OK == statusCode) {
                return true;
            }

            if (HttpStatus.SC_UNAUTHORIZED == statusCode) {
                return false;
            }

        } catch (Exception e) {
            throw e;
        }

        return false;
    }

    public boolean logout()
    {
        // do nothing
        return true;
    }

    protected HttpResponse executeGet(URI uri) throws Exception
    {

        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpContext localContext = new BasicHttpContext();

        HttpGet httpget = new HttpGet(uri);
        httpget.addHeader("Accept", MediaType.APPLICATION_XML);

        HttpResponse response = httpclient.execute(httpget, localContext);

        return response;
    }

    protected HttpResponse executeGet(URI uri, String username, String password) throws Exception
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

        HttpGet request = new HttpGet(uri);
        request.addHeader(new BasicScheme().authenticate(creds, request));
        HttpResponse response = httpClient.execute(request);

        return response;
    }

    public Xwiki getXwikiInfo(String username, String password) throws Exception
    {
        HttpResponse response = executeGet(serverURI, username, password);
        Xwiki xwiki = (Xwiki) unmarshaller.unmarshal(response.getEntity().getContent());
        return xwiki;
    }

    public List<Wiki> getWikis(URI wikisURI, String username, String password) throws Exception
    {
        HttpResponse response = executeGet(wikisURI, username, password);
        Wikis wikis = (Wikis) unmarshaller.unmarshal(response.getEntity().getContent());
        return wikis.getWikis();
    }

    public List<Space> getSpaces(URI spacesURI) throws Exception
    {
        HttpResponse response = executeGet(spacesURI, username, password);
        Spaces spaces = (Spaces) unmarshaller.unmarshal(response.getEntity().getContent());
        return spaces.getSpaces();
    }

    protected HttpResponse executePostXml(URI uri, java.lang.Object object) throws Exception
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

        HttpPost request = new HttpPost(uri);
        request.addHeader(new BasicScheme().authenticate(creds, request));
        request.addHeader("Content-type", "text/xml; charset=UTF-8");
        request.addHeader("Accept", MediaType.APPLICATION_XML);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        marshaller.marshal(object, os);
        HttpEntity entity = new ByteArrayEntity(os.toByteArray());
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);

        return response;
    }

    protected HttpResponse executePutXml(URI uri, java.lang.Object object) throws Exception
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

        HttpPut request = new HttpPut(uri);
        request.addHeader(new BasicScheme().authenticate(creds, request));
        request.addHeader("Content-type", "text/xml; charset=UTF-8");
        request.addHeader("Accept", MediaType.APPLICATION_XML);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // StringWriter writer = new StringWriter();
        // marshaller.marshal(object, writer);
        // System.out.println("content = " + writer.toString());
        marshaller.marshal(object, os);
        HttpEntity entity = new ByteArrayEntity(os.toByteArray());
        request.setEntity(entity);

        HttpResponse response = httpClient.execute(request);

        return response;
    }

    protected HttpResponse executeDelete(URI uri) throws Exception
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

        HttpDelete request = new HttpDelete(uri);
        request.addHeader(new BasicScheme().authenticate(creds, request));
        HttpResponse response = httpClient.execute(request);

        return response;
    }

    /**
     * @return
     */
    public Xwiki getServerInfo() throws Exception
    {
        HttpResponse response;
        try {
            response = executeGet(serverURI, username, password);
            Xwiki xwiki = (Xwiki) unmarshaller.unmarshal(response.getEntity().getContent());
            return xwiki;
        } catch (Exception e) {
            // e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param pagesURI
     * @param username
     * @param password
     * @return
     */
    public List<PageSummary> getPages(URI pagesURI)
    {
        HttpResponse response;
        try {
            response = executeGet(pagesURI, username, password);
            Pages pages = (Pages) unmarshaller.unmarshal(response.getEntity().getContent());
            return pages.getPageSummaries();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param objectsURI
     * @param username
     * @param password
     * @return
     */
    public List<ObjectSummary> getObjects(URI objectsURI)
    {
        HttpResponse response;
        if (objectsURI != null) {
            try {
                response = executeGet(objectsURI, username, password);
                Objects objects = (Objects) unmarshaller.unmarshal(response.getEntity().getContent());
                return objects.getObjectSummaries();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @param attachmentsURI
     * @param username
     * @param password
     * @return
     */
    public List<Attachment> getAttachments(URI attachmentsURI)
    {
        HttpResponse response;
        try {
            response = executeGet(attachmentsURI, username, password);
            Attachments attachments = (Attachments) unmarshaller.unmarshal(response.getEntity().getContent());
            return attachments.getAttachments();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param syntaxesURI
     * @return
     */
    public Syntaxes getSyntaxes(URI syntaxesURI)
    {
        HttpResponse response;
        try {
            response = executeGet(syntaxesURI, username, password);
            Syntaxes synataxes = (Syntaxes) unmarshaller.unmarshal(response.getEntity().getContent());
            return synataxes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param historyURI
     * @return
     */
    public List<HistorySummary> getPageHistory(URI historyURI)
    {
        HttpResponse response;
        try {
            response = executeGet(historyURI, username, password);
            History history = (History) unmarshaller.unmarshal(response.getEntity().getContent());
            return history.getHistorySummaries();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Page getPage(URI pageURI) throws Exception
    {
        HttpResponse response;
        try {
            response = executeGet(pageURI, username, password);
            Page result = (Page) unmarshaller.unmarshal(response.getEntity().getContent());
            return result;
        } catch (Exception e) {
            // e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param classURI
     * @return
     */
    public Class getClass(URI classURI) throws Exception
    {
        HttpResponse response;
        try {
            response = executeGet(classURI, username, password);
            Class clazz = (Class) unmarshaller.unmarshal(response.getEntity().getContent());
            return clazz;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param tagsURI
     * @return
     */
    public List<Tag> getTags(URI tagsURI) throws Exception
    {
        if (tagsURI != null) {
            HttpResponse response;
            try {
                response = executeGet(tagsURI, username, password);
                Tags tags = (Tags) unmarshaller.unmarshal(response.getEntity().getContent());
                return tags.getTags();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        return null;
    }

    /**
     * @param commentsURI
     * @return
     */
    public List<Comment> getComments(URI commentsURI) throws Exception
    {
        if (commentsURI != null) {
            HttpResponse response;
            try {
                response = executeGet(commentsURI, username, password);
                Comments comments = (Comments) unmarshaller.unmarshal(response.getEntity().getContent());
                return comments.getComments();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        return null;
    }

    /**
     * @param propertiesURI
     * @return
     */
    public List<Property> getObjectProperties(URI propertiesURI) throws Exception
    {
        if (propertiesURI != null) {
            HttpResponse response;
            try {
                response = executeGet(propertiesURI, username, password);
                Properties properties = (Properties) unmarshaller.unmarshal(response.getEntity().getContent());
                return properties.getProperties();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
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
        HttpResponse response;
        try {
            response = executeGet(absoluteURI, username, password);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                FileOutputStream fos = new java.io.FileOutputStream(dir + File.separator + name);
                entity.writeTo(fos);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param objectURI
     * @return
     */
    public Object getObject(URI objectURI) throws Exception
    {
        if (objectURI != null) {
            HttpResponse response;
            try {
                response = executeGet(objectURI, username, password);
                org.xwiki.rest.model.jaxb.Object object =
                    (org.xwiki.rest.model.jaxb.Object) unmarshaller.unmarshal(response.getEntity().getContent());
                return object;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        return null;
    }

    /**
     * @param spaceURI
     * @return
     */
    public Space getSpace(URI spaceURI) throws Exception
    {
        if (spaceURI != null) {
            HttpResponse response;
            try {
                response = executeGet(spaceURI, username, password);
                org.xwiki.rest.model.jaxb.Space result =
                    (org.xwiki.rest.model.jaxb.Space) unmarshaller.unmarshal(response.getEntity().getContent());
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        return null;
    }

    /**
     * @param commentsURI
     * @param comment
     * @return
     */
    public Comment storeComment(URI commentsURI, Comment comment) throws Exception
    {
        HttpResponse response;
        try {
            response = executePostXml(commentsURI, comment);
            Comment result = (Comment) unmarshaller.unmarshal(response.getEntity().getContent());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * FIXME: need to return different response code so that the UI can respond correspondingly
     */
    public void uploadAttachment(URI attachmentURI, String attachmentName, URL fileUrl)
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

        HttpPut request = new HttpPut(attachmentURI);
        try {
            request.addHeader(new BasicScheme().authenticate(creds, request));
            request.addHeader("Accept", MediaType.APPLICATION_XML);

            File file = new File(fileUrl.toURI());
            byte[] bytes = FileUtils.readFileToByteArray(file);
            ByteArrayEntity bin = new ByteArrayEntity(bytes);

            request.setEntity(bin);

            HttpResponse response = httpClient.execute(request);

            /* file created */
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                // System.out.println("SC_CREATED");
            }
            /* file updated */
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                // System.out.println("SC_ACCEPTED");
            }

            /* user UNAUTHORIZED */
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                // System.out.println("SC_UNAUTHORIZED");
            }

        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param allTagsURI
     * @return
     */
    public List<Tag> getAllTags(URI allTagsURI) throws Exception
    {
        HttpResponse response;
        try {
            response = executeGet(allTagsURI, username, password);
            Tags result = (Tags) unmarshaller.unmarshal(response.getEntity().getContent());
            return result.getTags();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * FIXME: it only takes the <tags> element
     * 
     * @param tagsURI
     * @param tagName
     * @return
     */
    public List<Tag> addTag(URI tagsURI, String tagName) throws Exception
    {
        HttpResponse response;
        try {
            Tag tag = new Tag();
            tag.setName(tagName);

            List<Tag> tags = getAllTags(tagsURI);
            tags.add(tag);

            Tags tagsElement = new Tags();
            tagsElement.withTags(tags);

            response = executePutXml(tagsURI, tagsElement);
            Tags tagsResponse = (Tags) unmarshaller.unmarshal(response.getEntity().getContent());
            return tagsResponse.getTags();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param classesURI
     * @return
     */
    public List<Class> getClasses(URI classesURI) throws Exception
    {
        HttpResponse response;
        try {
            response = executeGet(classesURI, username, password);
            Classes clazzes = (Classes) unmarshaller.unmarshal(response.getEntity().getContent());
            return clazzes.getClazzs();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param pageURI
     * @param page
     * @return
     */
    public Page storePage(URI pageURI, Page page) throws Exception
    {
        HttpResponse response;
        try {

            response = executePutXml(pageURI, page);
            Page result = (Page) unmarshaller.unmarshal(response.getEntity().getContent());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param objectURI
     * @param o
     * @return
     */
    public Object storeObject(URI objectURI, Object o) throws Exception
    {
        HttpResponse response;

        if (o.getNumber() == -1) {
            try {
                response = executePostXml(objectURI, o);
                Object result = (Object) unmarshaller.unmarshal(response.getEntity().getContent());
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            try {
                response = executePutXml(objectURI, o);
                Object result = (Object) unmarshaller.unmarshal(response.getEntity().getContent());
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * @param tagsURI
     * @param tagName
     */
    public void removeTag(URI tagsURI, String tagName) throws Exception
    {
        HttpResponse response;
        try {

            List<Tag> tags = getAllTags(tagsURI);

            for (Iterator iterator = tags.iterator(); iterator.hasNext();) {
                Tag tag = (Tag) iterator.next();
                if (tag.getName().equals(tagName)) {
                    iterator.remove();
                }
            }

            Tags tagsElement = new Tags();
            tagsElement.withTags(tags);

            response = executePutXml(tagsURI, tagsElement);
            // Tags tagsResponse = (Tags) unmarshaller.unmarshal(response.getEntity().getContent());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * @param pageURI
     * @param sourcePageToBeCopied
     * @return
     * @throws Exception
     */
    public Page renamePage(URI pageURI, Page sourcePageToBeCopied) throws Exception
    {
        HttpResponse response = executePutXml(pageURI, sourcePageToBeCopied);
        Page result = (Page) unmarshaller.unmarshal(response.getEntity().getContent());

        return result;
    }
}
