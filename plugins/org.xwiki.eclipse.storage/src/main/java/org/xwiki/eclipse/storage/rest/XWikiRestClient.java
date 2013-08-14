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
package org.xwiki.eclipse.storage.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.print.URIException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xwiki.eclipse.core.CoreLog;
import org.xwiki.eclipse.storage.utils.StorageUtils;
import org.xwiki.rest.model.jaxb.Attachment;
import org.xwiki.rest.model.jaxb.Attachments;
import org.xwiki.rest.model.jaxb.Class;
import org.xwiki.rest.model.jaxb.Classes;
import org.xwiki.rest.model.jaxb.Comment;
import org.xwiki.rest.model.jaxb.Comments;
import org.xwiki.rest.model.jaxb.History;
import org.xwiki.rest.model.jaxb.HistorySummary;
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

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

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

    public XWikiRestClient(String serverURLAsString, String username, String password) throws Exception
    {

        this.serverURI = new URI(serverURLAsString);
        this.username = username;
        this.password = password;

        JAXBContext context = JAXBContext.newInstance("org.xwiki.rest.model.jaxb");
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();

        objectFactory = new ObjectFactory();
    }

    public boolean login(String username, String password) throws Exception
    {
        try {
            HttpResponse loginResponse = executeGet(serverURI);

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
        DefaultHttpClient httpClient = new DefaultHttpClient();

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

        HttpGet request = new HttpGet(uri);
        request.addHeader(new BasicScheme().authenticate(creds, request));
        HttpResponse response = httpClient.execute(request);

        return response;
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

    protected HttpResponse executePostString(URI uri, String content) throws Exception
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

        HttpPost request = new HttpPost(uri);
        request.addHeader(new BasicScheme().authenticate(creds, request));
        request.addHeader("Content-type", "text/plain; charset=UTF-8");
        request.addHeader("Accept", MediaType.APPLICATION_XML);

        HttpEntity entity = new ByteArrayEntity(content.getBytes());
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

    public Xwiki getServerInfo() throws Exception
    {
        HttpResponse response = executeGet(serverURI);
        Xwiki xwiki = (Xwiki) unmarshaller.unmarshal(response.getEntity().getContent());

        return xwiki;
    }
    
    protected URI getURI(String path, String query, String fragment) throws URISyntaxException {
        return new URI(serverURI.getScheme(), serverURI.getUserInfo(), serverURI.getHost(),  serverURI.getPort(), serverURI.getPath() + path, query, fragment);
    }

    protected URI getURI(String path) throws URISyntaxException {
        return getURI(path, null, null);
    }


    public List<Wiki> getWikis() throws Exception
    {
        URI wikisURI = getURI(String.format("/wikis"));

        HttpResponse response = executeGet(wikisURI);
        Wikis wikis = (Wikis) unmarshaller.unmarshal(response.getEntity().getContent());

        return wikis.getWikis();
    }

    public List<Space> getSpaces(String wiki) throws Exception
    {
        URI spacesURI = getURI(String.format("/wikis/%s/spaces", wiki));

        HttpResponse response = executeGet(spacesURI);
        Spaces spaces = (Spaces) unmarshaller.unmarshal(response.getEntity().getContent());

        return spaces.getSpaces();
    }

    public List<PageSummary> getPages(String wiki, String space) throws Exception
    {
        URI pagesURI = getURI(String.format("/wikis/%s/spaces/%s/pages", wiki, space));

        HttpResponse response = executeGet(pagesURI);
        Pages pages = (Pages) unmarshaller.unmarshal(response.getEntity().getContent());

        return pages.getPageSummaries();
    }

    public List<ObjectSummary> getObjects(String wiki, String space, String page) throws Exception
    {
        URI objectsURI = getURI(String.format("/wikis/%s/spaces/%s/pages/%s/objects", wiki, space, page));

        HttpResponse response = executeGet(objectsURI);
        Objects objects = (Objects) unmarshaller.unmarshal(response.getEntity().getContent());

        return objects.getObjectSummaries();
    }

    public List<Attachment> getAttachments(String wiki, String space, String page) throws Exception
    {
        URI attachmentsURI =
            getURI(String.format("/wikis/%s/spaces/%s/pages/%s/attachments", wiki, space, page));

        HttpResponse response = executeGet(attachmentsURI);
        Attachments attachments = (Attachments) unmarshaller.unmarshal(response.getEntity().getContent());

        return attachments.getAttachments();
    }

    public Syntaxes getSyntaxes() throws Exception
    {
        URI syntaxesURI = getURI(String.format("/syntaxes"));

        HttpResponse response = executeGet(syntaxesURI);
        Syntaxes syntaxes = (Syntaxes) unmarshaller.unmarshal(response.getEntity().getContent());

        return syntaxes;
    }

    public Page getPage(String wiki, String space, String page, String language) throws Exception
    {
        URI pageURI;

        if (language == null || language.equals("")) {
            pageURI = getURI(String.format("/wikis/%s/spaces/%s/pages/%s", wiki, space, page));
        } else {
            pageURI =
                getURI(String.format("/wikis/%s/spaces/%s/pages/%s/translations/%s", wiki, space, page,
                    language));
        }

        HttpResponse response = executeGet(pageURI);
        Page result = (Page) unmarshaller.unmarshal(response.getEntity().getContent());

        return result;
    }

    public Page getPageVersion(String wiki, String space, String page, String language, int majorVersion,
        int minorVersion) throws Exception
    {
        URI pageURI;

        if (language == null || language.equals("")) {
            pageURI =
                getURI(String.format("/wikis/%s/spaces/%s/pages/%s/history/%d.%d", wiki, space, page,
                    majorVersion, minorVersion));
        } else {
            pageURI =
                getURI(String.format("/wikis/%s/spaces/%s/pages/%s/translations/%s/history/%d.%d", wiki,
                    space, page, language, majorVersion, minorVersion));
        }

        HttpResponse response = executeGet(pageURI);
        Page result = (Page) unmarshaller.unmarshal(response.getEntity().getContent());

        return result;
    }

    public List<HistorySummary> getPageHistory(String wiki, String space, String page, String language)
        throws Exception
    {
        URI historyURI;

        if (language == null || language.equals("")) {
            historyURI = getURI(String.format("/wikis/%s/spaces/%s/pages/%s/history", wiki, space, page));
        } else {
            historyURI =
                getURI(String.format("/wikis/%s/spaces/%s/pages/%s/translations/%s/history", wiki, space,
                    page, language));
        }

        HttpResponse response = executeGet(historyURI);
        History history = (History) unmarshaller.unmarshal(response.getEntity().getContent());

        return history.getHistorySummaries();
    }

    public org.xwiki.rest.model.jaxb.Class getClass(String wiki, String className) throws Exception
    {
        URI classURI = getURI(String.format("/wikis/%s/classes/%s", wiki, className));

        HttpResponse response = executeGet(classURI);
        org.xwiki.rest.model.jaxb.Class clazz = (Class) unmarshaller.unmarshal(response.getEntity().getContent());

        return clazz;

    }

    public List<Tag> getTags(String wiki, String space, String page) throws Exception
    {
        URI tagsURI = getURI(String.format("/wikis/%s/spaces/%s/pages/%s/tags", wiki, space, page));

        HttpResponse response = executeGet(tagsURI);
        Tags tags = (Tags) unmarshaller.unmarshal(response.getEntity().getContent());

        return tags.getTags();
    }

    public List<Comment> getComments(String wiki, String space, String page) throws Exception
    {
        URI commentsURI =
            getURI(String.format("/wikis/%s/spaces/%s/pages/%s/comments", wiki, space, page));

        HttpResponse response = executeGet(commentsURI);
        Comments comments = (Comments) unmarshaller.unmarshal(response.getEntity().getContent());

        return comments.getComments();
    }

    public List<Property> getObjectProperties(String wiki, String space, String page, String className, int number)
        throws Exception
    {
        URI propertiesURI =
            getURI(String.format("/wikis/%s/spaces/%s/pages/%s/objects/%s/%d/properties", wiki, space,
                page, className, number));

        HttpResponse response = executeGet(propertiesURI);
        Properties properties = (Properties) unmarshaller.unmarshal(response.getEntity().getContent());

        return properties.getProperties();
    }

    public Object getObject(String wiki, String space, String page, String className, int number) throws Exception
    {
        URI objectURI =
            getURI(String.format("/wikis/%s/spaces/%s/pages/%s/objects/%s/%d", wiki, space, page,
                className, number));

        HttpResponse response = executeGet(objectURI);
        org.xwiki.rest.model.jaxb.Object object =
            (org.xwiki.rest.model.jaxb.Object) unmarshaller.unmarshal(response.getEntity().getContent());

        return object;
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
            response = executeGet(absoluteURI);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                FileOutputStream fos = new java.io.FileOutputStream(dir + File.separator + name);
                entity.writeTo(fos);
                fos.close();
            }
        } catch (Exception e) {
            CoreLog.logError(String.format("Error in download %s to %s/%s", absoluteURI, dir, name), e);
        }

    }

    public Space getSpace(String wiki, String space) throws Exception
    {
        URI spaceURI = getURI(String.format("/wikis/%s/spaces/%s", wiki, space));

        HttpResponse response = executeGet(spaceURI);
        org.xwiki.rest.model.jaxb.Space result =
            (org.xwiki.rest.model.jaxb.Space) unmarshaller.unmarshal(response.getEntity().getContent());

        return result;
    }

    public Comment storeComment(String wiki, String space, String page, Comment comment) throws Exception
    {
        URI commentsURI =
            getURI(String.format("/wikis/%s/spaces/%s/pages/%s/comments", wiki, space, page));

        HttpResponse response = executePostXml(commentsURI, comment);
        Comment result = (Comment) unmarshaller.unmarshal(response.getEntity().getContent());

        return result;
    }

    /**
     * FIXME: need to return different response code so that the UI can respond correspondingly
     */
    public void uploadAttachment(String wiki, String space, String page, String attachmentName, URL fileUrl)
        throws Exception
    {
        URI attachmentURI =
            getURI(String.format("/wikis/%s/spaces/%s/pages/%s/attachments/%s", wiki, space, page,
                attachmentName));

        DefaultHttpClient httpClient = new DefaultHttpClient();

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

        HttpPut request = new HttpPut(attachmentURI);

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
    }

    public List<Tag> getAllTags(String wiki) throws Exception
    {
        URI allTagsURI = getURI(String.format("/wikis/%s/tags"));

        HttpResponse response = executeGet(allTagsURI);
        Tags result = (Tags) unmarshaller.unmarshal(response.getEntity().getContent());

        return result.getTags();
    }

    public List<Tag> addTag(String wiki, String space, String page, String tagName) throws Exception
    {
        URI tagsURI = getURI(String.format("/wikis/%s/spaces/%s/pages/%s/tags", wiki, space, page));

        HttpResponse response;
        Tag tag = new Tag();
        tag.setName(tagName);

        List<Tag> tags = getTags(wiki, space, page);
        tags.add(tag);

        Tags tagsElement = new Tags().withTags(tags);

        response = executePutXml(tagsURI, tagsElement);
        Tags tagsResponse = (Tags) unmarshaller.unmarshal(response.getEntity().getContent());

        return tagsResponse.getTags();
    }

    public List<Class> getClasses(String wiki) throws Exception
    {
        URI classesURI = getURI(String.format("/wikis/%s/classes", wiki));

        HttpResponse response = executeGet(classesURI);
        Classes classes = (Classes) unmarshaller.unmarshal(response.getEntity().getContent());

        return classes.getClazzs();
    }

    public Page storePage(Page page) throws Exception
    {
        // FIXME: REFACTORING: Check that we can PUT a translation directly.
        URI pageURI;

        if (page.getLanguage() == null || page.getLanguage().equals("")) {
            pageURI = getURI(String.format("/wikis/%s/spaces/%s/pages/%s", page.getWiki(), page.getSpace(),
                page.getName()));
        } else {
            pageURI =
                getURI(String.format("/wikis/%s/spaces/%s/pages/%s/translations/%s", page.getWiki(),
                    page.getSpace(), page.getName(), page.getLanguage()));
        }

        HttpResponse response = executePutXml(pageURI, page);
        Page result = (Page) unmarshaller.unmarshal(response.getEntity().getContent());
        return result;
    }

    public Object storeObject(Object o) throws Exception
    {
        // FIXME: REFACTORING: Check conditions
        if (o.getNumber() == -1) {
            URI objectURI =
                getURI(String.format("/wikis/%s/spaces/%s/pages/%s/objects", o.getWiki(), o.getSpace(),
                    o.getPageName()));

            HttpResponse response = executePostXml(objectURI, o);
            Object result = (Object) unmarshaller.unmarshal(response.getEntity().getContent());
            return result;
        } else {
            URI objectURI =
                getURI(String.format("/wikis/%s/spaces/%s/pages/%s/objects/%s/%d", o.getWiki(),
                    o.getSpace(), o.getPageName(), o.getClassName(), o.getNumber()));

            HttpResponse response = executePutXml(objectURI, o);
            Object result = (Object) unmarshaller.unmarshal(response.getEntity().getContent());
            return result;
        }
    }

    public void removeTag(String wiki, String space, String page, String tagName) throws Exception
    {
        URI tagsURI = getURI(String.format("/wikis/%s/spaces/%s/pages/%s/tags", wiki, space, page));

        List<Tag> tags = getTags(wiki, space, page);

        for (Iterator iterator = tags.iterator(); iterator.hasNext();) {
            Tag tag = (Tag) iterator.next();
            if (tag.getName().equals(tagName)) {
                iterator.remove();
            }
        }

        Tags tagsElement = new Tags().withTags(tags);

        // FIXME: REFACTORING: Add error handling
        executePutXml(tagsURI, tagsElement);
    }

    public Page renamePage(Page sourcePageToBeCopied, String wiki, String space, String page, String language)
        throws Exception
    {
        URI pageURI;

        if (language == null || language.equals("")) {
            pageURI = getURI(String.format("/wikis/%s/spaces/%s/pages/%s", wiki, space, page));
        } else {
            pageURI =
                getURI(String.format("/wikis/%s/spaces/%s/pages/%s/translations/%s", wiki, space, page,
                    language));
        }

        HttpResponse response = executePutXml(pageURI, sourcePageToBeCopied);
        Page result = (Page) unmarshaller.unmarshal(response.getEntity().getContent());

        removePage(sourcePageToBeCopied.getWiki(), sourcePageToBeCopied.getSpace(), sourcePageToBeCopied.getName(),
            sourcePageToBeCopied.getLanguage());

        return result;
    }

    public Page copyPage(Page sourcePageToBeCopied, String wiki, String space, String page, String language)
        throws Exception
    {
        URI pageURI;

        if (language == null || language.equals("")) {
            pageURI = getURI(String.format("/wikis/%s/spaces/%s/pages/%s", wiki, space, page));
        } else {
            pageURI =
                getURI(String.format("/wikis/%s/spaces/%s/pages/%s/translations/%s", wiki, space, page,
                    language));
        }

        HttpResponse response = executePutXml(pageURI, sourcePageToBeCopied);
        Page result = (Page) unmarshaller.unmarshal(response.getEntity().getContent());

        return result;
    }

    public void removeObject(String wiki, String space, String page, String className, int number) throws Exception
    {
        URI objectURI =
            getURI(String.format("/wikis/%s/spaces/%s/pages/%s/objects/%s/%d", wiki, space, page,
                className, number));

        // FIXME: REFACTORING: Handle error codes
        executeDelete(objectURI);
    }

    public void removePage(String wiki, String space, String page, String language) throws Exception
    {
        URI pageURI;

        if (language == null || language.equals("")) {
            pageURI = getURI(String.format("/wikis/%s/spaces/%s/pages/%s", wiki, space, page));
        } else {
            pageURI =
                getURI(String.format("/wikis/%s/spaces/%s/pages/%s/translations/%s", wiki, space, page,
                    language));
        }

        // FIXME: REFACTORING: Handle error codes
        executeDelete(pageURI);
    }

    public void removeAttachment(String wiki, String space, String page, String attachmentName) throws Exception
    {
        URI attachmentURI =
            getURI(String.format("/wikis/%s/spaces/%s/pages/%s/attachments/%s", wiki, space, page,
                attachmentName));

        // FIXME: REFACTORING: Handle error codes
        executeDelete(attachmentURI);
    }
    
    /**
     * This method returns possible hints. It will return null if the auto-completion extension is not install on the server
     * @param content
     * @param offset
     * @param syntax
     * @return
     */
    public Hints getAutoCompleteHints(String content, int offset, String syntax)
    {
    	try {
            URI autocompletionURI =
                getURI("/autocomplete", String.format("media=json&syntax=%s&offset=%s", syntax, offset), null);

            HttpResponse response = executePostString(autocompletionURI, content);
            Gson gson = StorageUtils.getGson();
            JsonReader reader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));
            Hints hints = gson.fromJson(reader, Hints.class);
            return hints;
    	} catch (Exception e) {
            e.printStackTrace();
            return null;
    	}
    }
}
