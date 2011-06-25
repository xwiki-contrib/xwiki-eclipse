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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.LinkCollection;
import org.xwiki.rest.model.jaxb.ObjectFactory;
import org.xwiki.rest.model.jaxb.Space;
import org.xwiki.rest.model.jaxb.Spaces;
import org.xwiki.rest.model.jaxb.Wiki;
import org.xwiki.rest.model.jaxb.Wikis;
import org.xwiki.rest.model.jaxb.Xwiki;

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

    public boolean login(String username, String password)
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
            e.printStackTrace();
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

    // protected String getFullUri(Class< ? > resourceClass)
    // {
    // return String.format("%s%s", getBaseUrl(), UriBuilder.fromResource(resourceClass).build());
    // }

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

        DefaultHttpClient httpclient = new DefaultHttpClient();

        httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(username, password));

        HttpContext localContext = new BasicHttpContext();

        HttpGet httpget = new HttpGet(uri);
        httpget.addHeader("Accept", MediaType.APPLICATION_XML.toString());

        HttpResponse response = httpclient.execute(httpget, localContext);

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

    public List<Space> getSpaces(String wikiId, String username, String password) throws Exception
    {
        String spacesUrl = getServerUrl() + "/" + Relations.WIKIS_PREFIX + "/" + wikiId + "/" + Relations.SPACES_PREFIX;
        HttpResponse response = executeGet(spacesUrl, username, password);
        Spaces spaces = (Spaces) unmarshaller.unmarshal(response.getEntity().getContent());
        return spaces.getSpaces();
    }

    // protected HttpPost executePostXml(String uri, Object object) throws Exception
    // {
    // HttpClient httpClient = new HttpClient();
    //
    // PostMethod postMethod = new PostMethod(uri);
    // postMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());
    //
    // StringWriter writer = new StringWriter();
    // marshaller.marshal(object, writer);
    //
    // RequestEntity entity =
    // new StringRequestEntity(writer.toString(), MediaType.APPLICATION_XML.toString(), "UTF-8");
    // postMethod.setRequestEntity(entity);
    //
    // httpClient.executeMethod(postMethod);
    //
    // return postMethod;
    // }
    //
    // protected HttpPost executePostXml(String uri, Object object, String userName, String password) throws Exception
    // {
    // HttpClient httpClient = new HttpClient();
    // httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
    // httpClient.getParams().setAuthenticationPreemptive(true);
    //
    // PostMethod postMethod = new PostMethod(uri);
    // postMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());
    //
    // StringWriter writer = new StringWriter();
    // marshaller.marshal(object, writer);
    //
    // RequestEntity entity =
    // new StringRequestEntity(writer.toString(), MediaType.APPLICATION_XML.toString(), "UTF-8");
    // postMethod.setRequestEntity(entity);
    //
    // httpClient.executeMethod(postMethod);
    //
    // return postMethod;
    // }
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
    //
    // protected HttpDelete executeDelete(String uri) throws Exception
    // {
    // HttpClient httpClient = new HttpClient();
    // DeleteMethod deleteMethod = new DeleteMethod(uri);
    // httpClient.executeMethod(deleteMethod);
    //
    // return deleteMethod;
    // }
    //
    // protected HttpDelete executeDelete(String uri, String userName, String password) throws Exception
    // {
    // HttpClient httpClient = new HttpClient();
    // httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
    // httpClient.getParams().setAuthenticationPreemptive(true);
    //
    // DeleteMethod deleteMethod = new DeleteMethod(uri);
    // httpClient.executeMethod(deleteMethod);
    //
    // return deleteMethod;
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

    // private Page getPage(String wikiName, String spaceName, String pageName) throws Exception
    // {
    // String uri = getUriBuilder(PageResource.class).build(wikiName, spaceName, pageName).toString();
    //
    // GetMethod getMethod = executeGet(uri);
    //
    // return (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
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

    public static void main(String[] args)
    {
        String serverUrl = "http://localhost:8080/xwiki/rest";
        String username = "Admin";
        String password = "admin";
        System.out.println("begin testing");
        XWikiRESTClient client = new XWikiRESTClient(serverUrl, username, password);
        try {
            List<Wiki> wikis = client.getWikis(username, password);
            System.out.println(wikis.get(0).getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
}
