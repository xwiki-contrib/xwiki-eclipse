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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.xwiki.rest.model.jaxb.PageSummary;
import org.xwiki.rest.model.jaxb.Space;
import org.xwiki.rest.model.jaxb.Tag;
import org.xwiki.rest.model.jaxb.Wiki;

/**
 * @version $Id$
 */
public class XWikiRESTClientTest
{
    Bundle bundle = Platform.getBundle("org.xwiki.eclipse.test");

    @Test
    public void testGetClassProperties()
    {
        String serverUrl = "http://localhost:8080/xwiki/rest";

        String username = "XWiki.Admin";
        String password = "admin";
        String className = "AnnotationCode.AnnotationClass";

        XWikiRestClient client = new XWikiRestClient(serverUrl, username, password);
        String classURL = serverUrl + "/wikis/xwiki/classes/" + className;

        try {
            URI classURI = new URI(classURL);
            org.xwiki.rest.model.jaxb.Class clazz = client.getClass(classURI);
            Assert.assertNotNull(clazz);
            Assert.assertTrue(clazz.getProperties().size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testAddTag()
    {
        String serverUrl = "http://localhost:8080/xwiki/rest";

        String tagsURL = "http://localhost:8080/xwiki/rest/wikis/xwiki/spaces/myspace/pages/page3/tags";
        String username = "XWiki.Admin";
        String password = "admin";

        XWikiRestClient client = new XWikiRestClient(serverUrl, username, password);

        try {
            URI tagsURI = new URI(tagsURL);
            String tagName = "TestTagInJunit" + System.currentTimeMillis();
            List<Tag> tags = client.addTag(tagsURI, tagName);
            Assert.assertNotNull(tags);
            boolean found = false;
            for (Tag t : tags) {
                if (t.getName().equals(tagName)) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testUploadAttachment()
    {
        String serverUrl = "http://localhost:8080/xwiki/rest";
        String attachmentName = "notice.html";
        String attachmentURL =
            "http://localhost:8080/xwiki/rest/wikis/xwiki/spaces/myspace/pages/WebHome/attachments/" + attachmentName;
        String username = "XWiki.Admin";
        String password = "admin";

        XWikiRestClient client = new XWikiRestClient(serverUrl, username, password);
        try {
            URL url = FileLocator.find(bundle, new Path("src/main/resources/notice.html"), null);
            System.out.println("url = " + url.toString());

            URL fileUrl = FileLocator.toFileURL(url);
            URI fileUri = FileLocator.toFileURL(url).toURI();

            System.out.println("uri = " + fileUri);
            URI attachmentURI = new URI(attachmentURL);
            client.uploadAttachment(attachmentURI, attachmentName, fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetPages()
    {
        String serverURL = "http://localhost:8080/xwiki/rest";
        String pagesURL = "http://localhost:8080/xwiki/rest/wikis/wiki1/spaces/myspace/pages";
        String username = "Admin";
        String password = "admin";
        XWikiRestClient client = new XWikiRestClient(serverURL, username, password);
        try {
            URI pagesURI = new URI(pagesURL);
            List<PageSummary> pages = client.getPages(pagesURI);
            Assert.assertNotNull(pages);
            Assert.assertTrue(pages.size() > 0);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void testGetSpaces()
    {
        String serverURL = "http://localhost:8080/xwiki/rest";
        String spacesURL = "http://localhost:8080/xwiki/rest/wikis/wiki1/spaces";
        String username = "Admin";
        String password = "admin";
        XWikiRestClient client = new XWikiRestClient(serverURL, username, password);
        try {
            URI spacesURI = new URI(spacesURL);
            List<Space> spaces = client.getSpaces(spacesURI);
            Assert.assertNotNull(spaces);
            Assert.assertTrue(spaces.size() > 0);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void testLogin()
    {
        String serverUrl = "http://localhost:8080/xwiki/rest";
        String username = "Admin";
        String password = "admin";
        XWikiRestClient client = new XWikiRestClient(serverUrl, username, password);
        try {
            Assert.assertTrue(client.login(username, password));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetWikis()
    {
        String serverURL = "http://localhost:8080/xwiki/rest";
        String username = "Admin";
        String password = "admin";
        XWikiRestClient client = new XWikiRestClient(serverURL, username, password);
        try {
            URI wikisURI = new URI("http://localhost:8080/xwiki/rest/wikis");
            List<Wiki> wikis = client.getWikis(wikisURI, username, password);
            Assert.assertNotNull(wikis);
            Assert.assertTrue(wikis.size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
