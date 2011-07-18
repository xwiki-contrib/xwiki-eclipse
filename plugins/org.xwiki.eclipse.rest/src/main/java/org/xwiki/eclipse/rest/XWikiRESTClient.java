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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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
public class XWikiRESTClient
{

    private String serverUrl;

    protected Marshaller marshaller;

    protected Unmarshaller unmarshaller;

    protected ObjectFactory objectFactory;

    private String username;

    private String password;

    public XWikiRESTClient(String serverUrl, String username, String password)
    {
        this.serverUrl = serverUrl;
        this.username = username;
        this.password = password;

        JAXBContext context;
        try {
            context = JAXBContext.newInstance("org.xwiki.rest.model.jaxb");
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        objectFactory = new ObjectFactory();
    }

    public boolean login(String username, String password) throws Exception
    {
        try {
            HttpResponse loginResponse = executeGet(getServerUrl(), username, password);

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

    public static Link getFirstLinkByRelation(LinkCollection linkCollection, String relation)
    {
        if (linkCollection.getLinks() == null) {
            return null;
        }

        for (Link link : linkCollection.getLinks()) {
            if (link.getRel().equals(relation)) {
                return link;
            }
        }

        return null;
    }

    public static List<Link> getLinksByRelation(LinkCollection linkCollection, String relation)
    {
        List<Link> result = new ArrayList<Link>();

        if (linkCollection.getLinks() == null) {
            return result;
        }

        for (Link link : linkCollection.getLinks()) {
            if (link.getRel().equals(relation)) {
                result.add(link);
            }
        }

        return result;
    }

    protected String getBaseUrl()
    {
        return getServerUrl();
    }

    protected HttpResponse executeGet(String uri) throws Exception
    {

        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpContext localContext = new BasicHttpContext();

        HttpGet httpget = new HttpGet(uri);
        httpget.addHeader("Accept", MediaType.APPLICATION_XML);

        HttpResponse response = httpclient.execute(httpget, localContext);

        return response;
    }

    protected HttpResponse executeGet(String uri, String username, String password) throws Exception
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
        HttpResponse response = executeGet(getServerUrl(), username, password);
        Xwiki xwiki = (Xwiki) unmarshaller.unmarshal(response.getEntity().getContent());
        return xwiki;
    }

    public List<Wiki> getWikis(String username, String password) throws Exception
    {
        String wikisUrl = getServerUrl() + "/" + Relations.WIKIS_PREFIX;
        HttpResponse response = executeGet(wikisUrl, username, password);
        Wikis wikis = (Wikis) unmarshaller.unmarshal(response.getEntity().getContent());
        return wikis.getWikis();
    }

    public List<Space> getSpaces(String spacesUrl) throws Exception
    {
        HttpResponse response = executeGet(spacesUrl, username, password);
        Spaces spaces = (Spaces) unmarshaller.unmarshal(response.getEntity().getContent());
        return spaces.getSpaces();
    }

    protected HttpResponse executePostXml(String uri, java.lang.Object object, String userName, String password)
        throws Exception
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

    protected HttpResponse executePutXml(String uri, java.lang.Object object) throws Exception
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

    protected HttpResponse executeDelete(String uri) throws Exception
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

        HttpDelete request = new HttpDelete(uri);
        request.addHeader(new BasicScheme().authenticate(creds, request));
        HttpResponse response = httpClient.execute(request);

        return response;
    }

    //
    // protected HttpPost executePost(String uri, String string, String mediaType, String userName, String password)
    // throws Exception
    // {
    // HttpClient httpClient = new HttpClient();
    // httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
    // httpClient.getParams().setAuthenticationPreemptive(true);
    //
    // PostMethod postMethod = new PostMethod(uri);
    // postMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());
    //
    // RequestEntity entity = new StringRequestEntity(string, mediaType, "UTF-8");
    // postMethod.setRequestEntity(entity);
    //
    // httpClient.executeMethod(postMethod);
    //
    // return postMethod;
    // }

    // protected HttpPost executePostForm(String uri, NameValuePair[] nameValuePairs, String userName, String password)
    // throws Exception
    // {
    // HttpClient httpClient = new HttpClient();
    // httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
    // httpClient.getParams().setAuthenticationPreemptive(true);
    //
    // PostMethod postMethod = new PostMethod(uri);
    // postMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());
    // postMethod.addRequestHeader("Content-type", MediaType.APPLICATION_WWW_FORM.toString());
    //
    // postMethod.setRequestBody(nameValuePairs);
    //
    // httpClient.executeMethod(postMethod);
    //
    // return postMethod;
    // }

    // protected HttpPut executePutXml(String uri, Object object) throws Exception
    // {
    // HttpClient httpClient = new HttpClient();
    //
    // PutMethod putMethod = new PutMethod(uri);
    // putMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());
    //
    // StringWriter writer = new StringWriter();
    // marshaller.marshal(object, writer);
    //
    // RequestEntity entity =
    // new StringRequestEntity(writer.toString(), MediaType.APPLICATION_XML.toString(), "UTF-8");
    // putMethod.setRequestEntity(entity);
    //
    // httpClient.executeMethod(putMethod);
    //
    // return putMethod;
    // }
    //
    // protected HttpPut executePutXml(String uri, Object object, String userName, String password) throws Exception
    // {
    // HttpClient httpClient = new HttpClient();
    // httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
    // httpClient.getParams().setAuthenticationPreemptive(true);
    //
    // PutMethod putMethod = new PutMethod(uri);
    // putMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());
    //
    // StringWriter writer = new StringWriter();
    // marshaller.marshal(object, writer);
    //
    // RequestEntity entity =
    // new StringRequestEntity(writer.toString(), MediaType.APPLICATION_XML.toString(), "UTF-8");
    // putMethod.setRequestEntity(entity);
    //
    // httpClient.executeMethod(putMethod);
    //
    // return putMethod;
    // }
    //
    // protected HttpPut executePut(String uri, String string, String mediaType) throws Exception
    // {
    // HttpClient httpClient = new HttpClient();
    //
    // PutMethod putMethod = new PutMethod(uri);
    // RequestEntity entity = new StringRequestEntity(string, mediaType, "UTF-8");
    // putMethod.setRequestEntity(entity);
    //
    // httpClient.executeMethod(putMethod);
    //
    // return putMethod;
    // }
    //
    // protected HttpPut executePut(String uri, String string, String mediaType, String userName, String password)
    // throws Exception
    // {
    // HttpClient httpClient = new HttpClient();
    // httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
    // httpClient.getParams().setAuthenticationPreemptive(true);
    //
    // PutMethod putMethod = new PutMethod(uri);
    // RequestEntity entity = new StringRequestEntity(string, mediaType, "UTF-8");
    // putMethod.setRequestEntity(entity);
    //
    // httpClient.executeMethod(putMethod);
    //
    // return putMethod;
    // }
    // protected String getWiki() throws Exception
    // {
    // GetMethod getMethod = executeGet(getFullUri(WikisResource.class));
    // Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());
    //
    // Wikis wikis = (Wikis) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
    // Assert.assertTrue(wikis.getWikis().size() > 0);
    //
    // return wikis.getWikis().get(0).getName();
    // }

    // protected void checkLinks(LinkCollection linkCollection) throws Exception
    // {
    // if (linkCollection.getLinks() != null) {
    // for (Link link : linkCollection.getLinks()) {
    // GetMethod getMethod = executeGet(link.getHref());
    // if (getMethod.getStatusCode() != HttpStatus.SC_UNAUTHORIZED) {
    // Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());
    // }
    // }
    // }
    // }

    // protected UriBuilder getUriBuilder(Class< ? > resource)
    // {
    // return UriBuilder.fromUri(getBaseUrl()).path(resource);
    // }

    // protected String getPageContent(String wikiName, String spaceName, String pageName) throws Exception
    // {
    // Page page = getPage(wikiName, spaceName, pageName);
    //
    // return page.getContent();
    // }
    //
    // protected int setPageContent(String wikiName, String spaceName, String pageName, String content) throws Exception
    // {
    // String uri = getUriBuilder(PageResource.class).build(wikiName, spaceName, pageName).toString();
    //
    // PutMethod putMethod = executePut(uri, content, javax.ws.rs.core.MediaType.TEXT_PLAIN, "Admin", "admin");
    //
    // int code = putMethod.getStatusCode();
    // Assert.assertTrue(String.format("Failed to set page content, %s", getHttpMethodInfo(putMethod)),
    // code == HttpStatus.SC_ACCEPTED || code == HttpStatus.SC_CREATED);
    //
    // return code;
    // }

    // protected String getHttpMethodInfo(HttpMethod method) throws Exception
    // {
    // return String.format("\nName: %s\nURI: %s\nStatus code: %d\nStatus text: %s", method.getName(),
    // method.getURI(), method.getStatusCode(), method.getStatusText());
    // }

    // protected String getAttachmentsInfo(Attachments attachments)
    // {
    // StringBuffer sb = new StringBuffer();
    // sb.append(String.format("Attachments: %d\n", attachments.getAttachments().size()));
    // for (Attachment attachment : attachments.getAttachments()) {
    // sb.append(String.format("* %s\n", attachment.getName()));
    // }
    //
    // return sb.toString();
    // }
    //
    // protected String getPagesInfo(Pages pages)
    // {
    // StringBuffer sb = new StringBuffer();
    // sb.append(String.format("Pages: %d\n", pages.getPageSummaries().size()));
    // for (PageSummary pageSummary : pages.getPageSummaries()) {
    // sb.append(String.format("* %s\n", pageSummary.getFullName()));
    // }
    //
    // return sb.toString();
    // }

    public String getServerUrl()
    {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }

    /**
     * @return
     */
    public Xwiki getServerInfo()
    {
        String wikisUrl = getServerUrl();
        HttpResponse response;
        try {
            response = executeGet(wikisUrl, username, password);
            Xwiki xwiki = (Xwiki) unmarshaller.unmarshal(response.getEntity().getContent());
            return xwiki;
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
        HttpResponse response;
        try {
            response = executeGet(pagesUrl, username, password);
            Pages pages = (Pages) unmarshaller.unmarshal(response.getEntity().getContent());
            return pages.getPageSummaries();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param objectsUrl
     * @param username
     * @param password
     * @return
     */
    public List<ObjectSummary> getObjects(String objectsUrl)
    {
        HttpResponse response;
        if (objectsUrl != null) {
            try {
                response = executeGet(objectsUrl, username, password);
                Objects objects = (Objects) unmarshaller.unmarshal(response.getEntity().getContent());
                return objects.getObjectSummaries();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @param attachmentsUrl
     * @param username
     * @param password
     * @return
     */
    public List<Attachment> getAttachments(String attachmentsUrl)
    {
        HttpResponse response;
        try {
            response = executeGet(attachmentsUrl, username, password);
            Attachments attachments = (Attachments) unmarshaller.unmarshal(response.getEntity().getContent());
            return attachments.getAttachments();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param syntaxesUrl
     * @return
     */
    public Syntaxes getSyntaxes(String syntaxesUrl)
    {
        HttpResponse response;
        try {
            response = executeGet(syntaxesUrl, username, password);
            Syntaxes synataxes = (Syntaxes) unmarshaller.unmarshal(response.getEntity().getContent());
            return synataxes;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param historyUrl
     * @return
     */
    public List<HistorySummary> getPageHistory(String historyUrl)
    {
        HttpResponse response;
        try {
            response = executeGet(historyUrl, username, password);
            History history = (History) unmarshaller.unmarshal(response.getEntity().getContent());
            return history.getHistorySummaries();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public Page getPage(String pageUrl) throws Exception
    {
        HttpResponse response;
        try {
            response = executeGet(pageUrl, username, password);
            Page result = (Page) unmarshaller.unmarshal(response.getEntity().getContent());
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param classUrl
     * @return
     */
    public Class getClass(String classUrl) throws Exception
    {
        HttpResponse response;
        try {
            response = executeGet(classUrl, username, password);
            Class clazz = (Class) unmarshaller.unmarshal(response.getEntity().getContent());
            return clazz;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param tagsUrl
     * @return
     */
    public List<Tag> getTags(String tagsUrl) throws Exception
    {
        if (tagsUrl != null) {
            HttpResponse response;
            try {
                response = executeGet(tagsUrl, username, password);
                Tags tags = (Tags) unmarshaller.unmarshal(response.getEntity().getContent());
                return tags.getTags();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            }
        }

        return null;
    }

    /**
     * @param commentsUrl
     * @return
     */
    public List<Comment> getComments(String commentsUrl) throws Exception
    {
        if (commentsUrl != null) {
            HttpResponse response;
            try {
                response = executeGet(commentsUrl, username, password);
                Comments comments = (Comments) unmarshaller.unmarshal(response.getEntity().getContent());
                return comments.getComments();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            }
        }

        return null;
    }

    /**
     * @param propertiesUrl
     * @return
     */
    public List<Property> getObjectProperties(String propertiesUrl) throws Exception
    {
        if (propertiesUrl != null) {
            HttpResponse response;
            try {
                response = executeGet(propertiesUrl, username, password);
                Properties properties = (Properties) unmarshaller.unmarshal(response.getEntity().getContent());
                return properties.getProperties();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            }
        }

        return null;
    }

    /**
     * @param dir
     * @param absoluteUrl
     * @param name
     */
    public void download(String dir, String absoluteUrl, String name)
    {
        HttpResponse response;
        try {
            response = executeGet(absoluteUrl, username, password);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                FileOutputStream fos = new java.io.FileOutputStream(dir + File.separator + name);
                entity.writeTo(fos);
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param objectUrl
     * @return
     */
    public Object getObject(String objectUrl) throws Exception
    {
        if (objectUrl != null) {
            HttpResponse response;
            try {
                response = executeGet(objectUrl, username, password);
                org.xwiki.rest.model.jaxb.Object object =
                    (org.xwiki.rest.model.jaxb.Object) unmarshaller.unmarshal(response.getEntity().getContent());
                return object;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            }
        }

        return null;
    }

    /**
     * @param spaceUrl
     * @return
     */
    public Space getSpace(String spaceUrl) throws Exception
    {
        if (spaceUrl != null) {
            HttpResponse response;
            try {
                response = executeGet(spaceUrl, username, password);
                org.xwiki.rest.model.jaxb.Space result =
                    (org.xwiki.rest.model.jaxb.Space) unmarshaller.unmarshal(response.getEntity().getContent());
                return result;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            }
        }

        return null;
    }

    /**
     * @param comment
     * @return
     */
    public Comment storeComment(String commentsUrl, Comment comment) throws Exception
    {
        HttpResponse response;
        try {
            response = executePostXml(commentsUrl, comment, username, password);
            Comment result = (Comment) unmarshaller.unmarshal(response.getEntity().getContent());
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * FIXME: need to return different response code so that the UI can respond correspondingly
     * 
     * @param attachmentUrl
     * @param attachmentName
     * @param fileUrl
     */
    public void uploadAttachment(String attachmentUrl, String attachmentName, URL fileUrl)
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

        HttpPut request = new HttpPut(attachmentUrl);
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param allTagsUrl
     * @return
     */
    public List<Tag> getAllTags(String allTagsUrl) throws Exception
    {
        HttpResponse response;
        try {
            response = executeGet(allTagsUrl, username, password);
            Tags result = (Tags) unmarshaller.unmarshal(response.getEntity().getContent());
            return result.getTags();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * FIXME: it only takes the <tags> element
     * 
     * @param tagsUrl
     * @param tagName
     * @return
     */
    public List<Tag> addTag(String tagsUrl, String tagName) throws Exception
    {
        HttpResponse response;
        try {
            Tag tag = new Tag();
            tag.setName(tagName);

            List<Tag> tags = getAllTags(tagsUrl);
            tags.add(tag);

            Tags tagsElement = new Tags();
            tagsElement.withTags(tags);

            response = executePutXml(tagsUrl, tagsElement);
            Tags tagsResponse = (Tags) unmarshaller.unmarshal(response.getEntity().getContent());
            return tagsResponse.getTags();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
    }
}
