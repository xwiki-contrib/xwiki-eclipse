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
package org.xwiki.eclipse.storage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.codehaus.swizzle.confluence.ServerInfo;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.xwiki.eclipse.model.ModelObject;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipseObjectProperty;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseServerInfo;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseTag;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;
import org.xwiki.eclipse.xmlrpc.XWikiEclipseXmlrpcException;
import org.xwiki.eclipse.xmlrpc.XmlrpcRemoteXWikiDataStorage;
import org.xwiki.xmlrpc.model.XWikiObjectSummary;
import org.xwiki.xmlrpc.model.XWikiPageHistorySummary;
import org.xwiki.xmlrpc.model.XWikiPageSummary;

/**
 * @version $Id$
 */
public class XmlrpcRemoteXWikiDataStorageAdapter implements IRemoteXWikiDataStorage
{
    private DataManager dataManager;

    private XmlrpcRemoteXWikiDataStorage xmlrpcRemoteDataStorage;

    /**
     * @param dataManager
     * @param endpoint
     * @param userName
     * @param password
     */
    public XmlrpcRemoteXWikiDataStorageAdapter(DataManager dataManager, String endpoint, String userName,
        String password)
    {
        this.dataManager = dataManager;

        try {
            xmlrpcRemoteDataStorage = new XmlrpcRemoteXWikiDataStorage(endpoint, userName, password);
        } catch (XWikiEclipseXmlrpcException e) {
            e.printStackTrace();
        }
    }

    /**
     * as xmlrpc can only process one return a single xwiki instance {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getWikiSummaries()
     */
    @Override
    public List<XWikiEclipseWikiSummary> getWikiSummaries()
    {
        List<XWikiEclipseWikiSummary> result = new ArrayList<XWikiEclipseWikiSummary>();
        XWikiEclipseWikiSummary xwiki = new XWikiEclipseWikiSummary(dataManager);

        XWikiEclipseServerInfo serverInfo = getServerInfo();

        xwiki.setBaseUrl(serverInfo.getBaseUrl());
        xwiki.setName("xwiki"); // by default, it is xwiki
        xwiki.setSyntaxes(serverInfo.getSyntaxes());
        xwiki.setVersion(serverInfo.getVersion());
        xwiki.setWikiId("xwiki"); // by default, it is xwiki

        result.add(xwiki);

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getSpaces()
     */
    @Override
    public List<XWikiEclipseSpaceSummary> getSpaceSummaries(String wikiId)
    {
        List<XWikiEclipseSpaceSummary> result = new ArrayList<XWikiEclipseSpaceSummary>();

        try {
            List<SpaceSummary> spaces = this.xmlrpcRemoteDataStorage.getSpaces();
            for (SpaceSummary spaceSummary : spaces) {
                XWikiEclipseSpaceSummary space = new XWikiEclipseSpaceSummary(dataManager);
                space.setId(spaceSummary.getKey());
                space.setName(spaceSummary.getName());
                space.setUrl(spaceSummary.getUrl());

                result.add(space);
            }
            return result;
        } catch (XWikiEclipseXmlrpcException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#dispose()
     */
    @Override
    public void dispose()
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getServerInfo()
     */
    @Override
    public XWikiEclipseServerInfo getServerInfo()
    {
        XWikiEclipseServerInfo serverInfo = new XWikiEclipseServerInfo();

        try {
            ServerInfo info = xmlrpcRemoteDataStorage.getServerInfo();
            serverInfo.setBaseUrl(info.getBaseUrl());
            serverInfo.setMajorVersion(info.getMajorVersion());
            serverInfo.setMinorVersion(info.getMinorVersion());
            /* set up the version string */
            String version = info.getMajorVersion() + "." + info.getMinorVersion();
            serverInfo.setVersion(version);

            /* by default, use xwiki/1.0 as the syntaxes */
            List<String> syntaxes = new ArrayList<String>();
            syntaxes.add("xwiki/1.0");
            serverInfo.setSyntaxes(syntaxes);

            return serverInfo;
        } catch (XWikiEclipseXmlrpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getPageSummaries(org.xwiki.eclipse.model.XWikiEclipseSpaceSummary)
     */
    @Override
    public List<XWikiEclipsePageSummary> getPageSummaries(String wiki, String space)
    {
        try {
            List<XWikiEclipsePageSummary> result = new ArrayList<XWikiEclipsePageSummary>();

            List<XWikiPageSummary> pages = xmlrpcRemoteDataStorage.getPages(space);
            for (XWikiPageSummary pageSummary : pages) {
                XWikiEclipsePageSummary page = new XWikiEclipsePageSummary(dataManager);
                page.setId(pageSummary.getId());
                page.setName(pageSummary.getId());
                String fullName = pageSummary.getSpace() + "." + pageSummary.getId();
                page.setFullName(fullName);
                page.setParentId(pageSummary.getParentId());
                page.setSpace(pageSummary.getSpace());
                page.setTitle(pageSummary.getTitle());
                page.setUrl(pageSummary.getUrl());
                /* default value is "xwiki" for all xmlrpc implementation */
                page.setWiki("xwiki");
                // page.setTranslations(pageSummary.getTranslations());
                page.setSyntax("xwiki/1.0");

                result.add(page);
            }

            return result;
        } catch (XWikiEclipseXmlrpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getObjectSummaries(org.xwiki.eclipse.model.XWikiEclipsePageSummary)
     */
    public List<XWikiEclipseObjectSummary> getObjectSummaries(XWikiEclipsePageSummary pageSummary)
    {
        List<XWikiEclipseObjectSummary> result = new ArrayList<XWikiEclipseObjectSummary>();

        try {
            List<XWikiObjectSummary> objectSummaries = xmlrpcRemoteDataStorage.getObjects(pageSummary.getId());
            for (XWikiObjectSummary objectSummary : objectSummaries) {
                XWikiEclipseObjectSummary o = new XWikiEclipseObjectSummary(dataManager);
                o.setClassName(objectSummary.getClassName());
                o.setId(Integer.toString(objectSummary.getId()));
                o.setPageId(pageSummary.getParentId());
                o.setPrettyName(objectSummary.getPrettyName());

                result.add(o);
            }
        } catch (XWikiEclipseXmlrpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getPageHistorySummaries(org.xwiki.eclipse.model.XWikiEclipsePageSummary)
     */
    public List<XWikiEclipsePageHistorySummary> getPageHistorySummaries(XWikiEclipsePageSummary pageSummary)
        throws XWikiEclipseStorageException
    {
        List<XWikiEclipsePageHistorySummary> result = new ArrayList<XWikiEclipsePageHistorySummary>();

        try {
            List<XWikiPageHistorySummary> history = xmlrpcRemoteDataStorage.getPageHistory(pageSummary.getId());
            for (XWikiPageHistorySummary historySummary : history) {
                XWikiEclipsePageHistorySummary h = new XWikiEclipsePageHistorySummary(dataManager);
                h.setLanguage("en");
                h.setMajorVersion(historySummary.getVersion());
                h.setMinorVersion(historySummary.getMinorVersion());

                Calendar cal = Calendar.getInstance();
                cal.setTime(historySummary.getModified());
                h.setModified(cal);

                h.setModifier(historySummary.getModifier());
                h.setPageId(historySummary.getBasePageId());

                result.add(h);
                return result;
            }
        } catch (XWikiEclipseXmlrpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new XWikiEclipseStorageException(e);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getTags(org.xwiki.eclipse.model.XWikiEclipsePageSummary)
     */
    @Override
    public List<XWikiEclipseTag> getTags(XWikiEclipsePageSummary pageSummary)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getObjectProperties(org.xwiki.eclipse.model.XWikiEclipseObjectSummary)
     */
    public List<XWikiEclipseObjectProperty> getObjectProperties(XWikiEclipseObjectSummary objectSummary)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#download(java.lang.String,
     *      org.xwiki.eclipse.model.XWikiEclipseAttachment)
     */
    @Override
    public void download(String dir, XWikiEclipseAttachment attachment)
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getObject(org.xwiki.eclipse.model.ModelObject)
     */
    public XWikiEclipseObject getObject(ModelObject o)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#storeComment(org.xwiki.eclipse.model.XWikiEclipseComment)
     */
    @Override
    public XWikiEclipseComment storeComment(XWikiEclipseComment c)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getClasses(java.lang.String)
     */
    @Override
    public List<XWikiEclipseClass> getClasses(String wiki)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getClass(java.lang.String, java.lang.String)
     */
    @Override
    public XWikiEclipseClass getClass(String wiki, String className)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#remove(org.xwiki.eclipse.model.ModelObject)
     */
    @Override
    public void remove(ModelObject o)
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getObjectSummaries(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public List<XWikiEclipseObjectSummary> getObjectSummaries(String wiki, String space, String pageName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getAttachments(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public List<XWikiEclipseAttachment> getAttachments(String wiki, String space, String pageName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getPageHistorySummaries(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<XWikiEclipsePageHistorySummary> getPageHistorySummaries(String wiki, String space, String page,
        String language) throws XWikiEclipseStorageException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getClass(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public XWikiEclipseClass getClass(String wiki, String space, String pageName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getComments(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public List<XWikiEclipseComment> getComments(String wiki, String space, String pageName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getObjectProperties(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, int)
     */
    @Override
    public List<XWikiEclipseObjectProperty> getObjectProperties(String wiki, String space, String pageName,
        String className, int number)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getPage(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public XWikiEclipsePage getPage(String wiki, String space, String pageName, String language)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getObject(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, int)
     */
    @Override
    public XWikiEclipseObject getObject(String wiki, String space, String pageName, String className, int number)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getPageSummary(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public XWikiEclipsePageSummary getPageSummary(String wiki, String space, String pageName, String language)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getSpace(java.lang.String, java.lang.String)
     */
    @Override
    public XWikiEclipseSpaceSummary getSpace(String wiki, String space)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getAllTagsInWiki(java.lang.String)
     */
    @Override
    public List<XWikiEclipseTag> getAllTagsInWiki(String wiki)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#addTag(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public XWikiEclipseTag addTag(String wiki, String space, String pageName, String tagName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#uploadAttachment(java.lang.String, java.lang.String,
     *      java.lang.String, java.net.URL)
     */
    @Override
    public void uploadAttachment(String wiki, String space, String pageName, URL fileUrl)
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#updateAttachment(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.net.URL)
     */
    @Override
    public void updateAttachment(String wiki, String space, String pageName, String attachmentName, URL fileUrl)
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#exists(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public boolean exists(String wiki, String space, String pageName, String language)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#storePage(org.xwiki.eclipse.model.XWikiEclipsePage)
     */
    @Override
    public XWikiEclipsePage storePage(XWikiEclipsePage page)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
