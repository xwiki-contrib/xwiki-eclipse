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

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import org.xwiki.eclipse.model.XWikiEclipsePageTranslationSummary;
import org.xwiki.eclipse.model.XWikiEclipseServerInfo;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseTag;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;
import org.xwiki.eclipse.rest.Relations;
import org.xwiki.eclipse.rest.RestRemoteXWikiDataStorage;
import org.xwiki.eclipse.storage.utils.IdProcessor;
import org.xwiki.rest.model.jaxb.Attachment;
import org.xwiki.rest.model.jaxb.Attribute;
import org.xwiki.rest.model.jaxb.Comment;
import org.xwiki.rest.model.jaxb.HistorySummary;
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.Object;
import org.xwiki.rest.model.jaxb.ObjectSummary;
import org.xwiki.rest.model.jaxb.Page;
import org.xwiki.rest.model.jaxb.PageSummary;
import org.xwiki.rest.model.jaxb.Property;
import org.xwiki.rest.model.jaxb.Space;
import org.xwiki.rest.model.jaxb.Syntaxes;
import org.xwiki.rest.model.jaxb.Tag;
import org.xwiki.rest.model.jaxb.Translation;
import org.xwiki.rest.model.jaxb.Wiki;
import org.xwiki.rest.model.jaxb.Xwiki;

/**
 * @version $Id$
 */
public class RestRemoteXWikiDataStorageAdapter implements IRemoteXWikiDataStorage
{
    RestRemoteXWikiDataStorage restRemoteStorage;

    private DataManager dataManager;

    private String endpoint;

    private String username;

    /**
     * @param dataManager
     * @param endpoint
     * @param userName
     * @param password
     */
    public RestRemoteXWikiDataStorageAdapter(DataManager dataManager, String endpoint, String userName, String password)
        throws XWikiEclipseStorageException
    {
        this.username = userName;
        this.dataManager = dataManager;
        this.restRemoteStorage = new RestRemoteXWikiDataStorage(endpoint, userName, password);
        this.endpoint = endpoint;
        try {
            XWikiEclipseServerInfo serverInfo = getServerInfo();
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    public List<XWikiEclipseWikiSummary> getWikiSummaries() throws XWikiEclipseStorageException
    {
        try {
            XWikiEclipseServerInfo serverInfo = getServerInfo();

            List<XWikiEclipseWikiSummary> result = new ArrayList<XWikiEclipseWikiSummary>();

            List<Wiki> wikis = restRemoteStorage.getWikis();
            for (Wiki wiki : wikis) {
                XWikiEclipseWikiSummary wikiSummary = new XWikiEclipseWikiSummary(dataManager);
                wikiSummary.setWikiId(wiki.getId());
                wikiSummary.setName(wiki.getName());
                wikiSummary.setVersion(serverInfo.getVersion());
                wikiSummary.setBaseUrl(serverInfo.getBaseUrl());
                wikiSummary.setSyntaxes(serverInfo.getSyntaxes());

                result.add(wikiSummary);
            }

            return result;
        } catch (Exception e) {
            // e.printStackTrace();
            throw new XWikiEclipseStorageException(e);
        }
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

        List<Space> spaces = this.restRemoteStorage.getSpaces(wikiId);
        if (spaces != null) {
            for (Space space : spaces) {
                XWikiEclipseSpaceSummary summary = new XWikiEclipseSpaceSummary(dataManager);
                summary.setId(space.getId());
                summary.setName(space.getName());
                summary.setUrl(space.getXwikiAbsoluteUrl());
                summary.setWiki(space.getWiki());

                result.add(summary);
            }
        }

        return result;
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
     * @throws XWikiEclipseStorageException
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getServerInfo()
     */
    @Override
    public XWikiEclipseServerInfo getServerInfo() throws XWikiEclipseStorageException
    {
        XWikiEclipseServerInfo serverInfo = new XWikiEclipseServerInfo();

        Xwiki xwiki;
        try {
            xwiki = this.restRemoteStorage.getServerInfo();
            List<Link> links = xwiki.getLinks();
            String syntaxesUrl = null;
            for (Link link : links) {
                if (link.getRel().equals(Relations.SYNTAXES)) {
                    syntaxesUrl = link.getHref();
                    break;
                }
            }

            Syntaxes syntaxes = this.restRemoteStorage.getSyntaxes(syntaxesUrl);

            List<String> syntaxeList = syntaxes.getSyntaxes();
            serverInfo.setSyntaxes(syntaxeList);

            String versionStr = xwiki.getVersion(); // e.g., 3.1-rc-1
            serverInfo.setVersion(versionStr);
            StringTokenizer tokenizer = new StringTokenizer(versionStr, "-");

            String v = tokenizer.nextToken();

            tokenizer = new StringTokenizer(v, ".");
            serverInfo.setMajorVersion(Integer.parseInt(tokenizer.nextToken()));
            serverInfo.setMinorVersion(Integer.parseInt(tokenizer.nextToken()));

            serverInfo.setBaseUrl(endpoint);

            return serverInfo;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            throw new XWikiEclipseStorageException(e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getPageSummaries(org.xwiki.eclipse.model.XWikiEclipseSpaceSummary)
     */
    @Override
    public List<XWikiEclipsePageSummary> getPageSummaries(String wiki, String space)
    {
        List<XWikiEclipsePageSummary> result = new ArrayList<XWikiEclipsePageSummary>();

        List<PageSummary> pages = this.restRemoteStorage.getPages(wiki, space);
        if (pages != null) {

            for (PageSummary pageSummary : pages) {
                XWikiEclipsePageSummary page = new XWikiEclipsePageSummary(dataManager);
                page.setId(pageSummary.getId());
                page.setName(pageSummary.getName());
                page.setFullName(pageSummary.getFullName());

                page.setParentId(pageSummary.getParentId());
                page.setSpace(pageSummary.getSpace());
                page.setTitle(pageSummary.getTitle());
                page.setUrl(pageSummary.getXwikiAbsoluteUrl());
                page.setWiki(pageSummary.getWiki());
                page.setSyntax(pageSummary.getSyntax());

                String defaultLanguage = pageSummary.getTranslations().getDefault();
                List<Translation> translations = pageSummary.getTranslations().getTranslations();
                if (translations != null && translations.size() > 0) {
                    for (Translation translation : translations) {
                        XWikiEclipsePageTranslationSummary t = new XWikiEclipsePageTranslationSummary(dataManager);
                        t.setLanguage(translation.getLanguage());
                        t.setDefaultLanguage(defaultLanguage);
                        page.getTranslations().add(t);
                    }
                }

                result.add(page);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getObjectSummaries(org.xwiki.eclipse.model.XWikiEclipsePageSummary)
     */
    @Override
    public List<XWikiEclipseObjectSummary> getObjectSummaries(String wiki, String space, String pageName)
    {
        List<XWikiEclipseObjectSummary> result = new ArrayList<XWikiEclipseObjectSummary>();

        List<ObjectSummary> objects = this.restRemoteStorage.getObjectSummaries(wiki, space, pageName);

        if (objects != null) {
            for (ObjectSummary objectSummary : objects) {
                XWikiEclipseObjectSummary o = new XWikiEclipseObjectSummary(dataManager);
                o.setClassName(objectSummary.getClassName());
                o.setId(objectSummary.getId());
                o.setPageId(objectSummary.getPageId());
                o.setPageName(objectSummary.getPageName());
                o.setSpace(space);
                o.setWiki(wiki);
                o.setPageName(objectSummary.getPageName());
                o.setNumber(objectSummary.getNumber());

                result.add(o);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getAttachments(org.xwiki.eclipse.model.XWikiEclipsePageSummary)
     */
    @Override
    public List<XWikiEclipseAttachment> getAttachments(String wiki, String space, String pageName)
    {
        List<XWikiEclipseAttachment> result = new ArrayList<XWikiEclipseAttachment>();

        List<Attachment> attachments = this.restRemoteStorage.getAttachments(wiki, space, pageName);
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                XWikiEclipseAttachment a = new XWikiEclipseAttachment(dataManager);
                a.setAbsoluteUrl(attachment.getXwikiAbsoluteUrl());
                a.setAuthor(attachment.getAuthor());
                a.setDate(attachment.getDate());
                a.setId(attachment.getId());
                a.setMimeType(attachment.getMimeType());
                a.setName(attachment.getName());
                a.setPageId(attachment.getPageId());
                a.setPageVersion(attachment.getPageVersion());
                a.setVersion(attachment.getVersion());

                result.add(a);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getPageHistorySummaries(org.xwiki.eclipse.model.XWikiEclipsePageSummary)
     */
    @Override
    public List<XWikiEclipsePageHistorySummary> getPageHistorySummaries(String wiki, String space, String pageName,
        String language) throws XWikiEclipseStorageException
    {
        List<XWikiEclipsePageHistorySummary> result = new ArrayList<XWikiEclipsePageHistorySummary>();

        List<HistorySummary> history = this.restRemoteStorage.getPageHistories(wiki, space, pageName, language);
        if (history != null) {
            for (HistorySummary historySummary : history) {
                XWikiEclipsePageHistorySummary h = new XWikiEclipsePageHistorySummary(dataManager);
                h.setPageId(historySummary.getPageId());
                h.setLanguage(language);
                h.setMajorVersion(historySummary.getMajorVersion());
                h.setMinorVersion(historySummary.getMinorVersion());
                h.setModified(historySummary.getModified());
                h.setModifier(historySummary.getModifier());
                h.setName(historySummary.getName());
                h.setSpace(historySummary.getSpace());
                h.setVersion(historySummary.getVersion());
                h.setWiki(historySummary.getWiki());

                result.add(h);
            }
        }

        return result;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getClasses(org.xwiki.eclipse.model.XWikiEclipsePageSummary)
     */
    @Override
    public XWikiEclipseClass getClass(String wiki, String space, String pageName)
    {
        String className = space + "." + pageName;
        org.xwiki.rest.model.jaxb.Class classSummary = this.restRemoteStorage.getClass(wiki, className);

        XWikiEclipseClass result = new XWikiEclipseClass(dataManager);
        result.setId(classSummary.getId());
        result.setName(classSummary.getName());
        result.setWiki(wiki);

        /* populate the properties for page class */
        List<Property> properties = classSummary.getProperties();

        for (Property property : properties) {
            XWikiEclipseObjectProperty p = new XWikiEclipseObjectProperty(dataManager);
            p.setName(property.getName());
            p.setType(property.getType());
            p.setValue(property.getValue());
            Map<String, String> attributeMap = new HashMap<String, String>();

            List<Attribute> attributes = property.getAttributes();
            for (Attribute attribute : attributes) {
                attributeMap.put(attribute.getName(), attribute.getValue());
            }

            p.setAttributes(attributeMap);

            result.getProperties().add(p);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getTags(org.xwiki.eclipse.model.XWikiEclipsePageSummary)
     */
    @Override
    public List<XWikiEclipseTag> getTags(String wiki, String space, String page)
    {
        List<XWikiEclipseTag> result = new ArrayList<XWikiEclipseTag>();

        List<Tag> tags = this.restRemoteStorage.getTags(wiki, space, page);

        if (tags != null) {
            for (Tag tag : tags) {
                XWikiEclipseTag t = new XWikiEclipseTag(dataManager);
                t.setName(tag.getName());
                t.setWiki(wiki);
                t.setSpace(space);
                t.setPage(page);

                result.add(t);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getComments(org.xwiki.eclipse.model.XWikiEclipsePageSummary)
     */
    @Override
    public List<XWikiEclipseComment> getComments(String wiki, String space, String pageName)
    {
        List<XWikiEclipseComment> result = new ArrayList<XWikiEclipseComment>();
        List<Comment> comments = this.restRemoteStorage.getComments(wiki, space, pageName);

        if (comments != null && comments.size() > 0) {
            for (Comment comment : comments) {
                XWikiEclipseComment c = new XWikiEclipseComment(dataManager);
                c.setAuthor(comment.getAuthor());
                c.setDate(comment.getDate());
                c.setHighlight(comment.getHighlight());
                c.setId(comment.getId());
                c.setText(comment.getText());

                /* current implementation of REST API does not return pageid */
                // c.setPageId(comment.getPageId());
                IdProcessor idBuilder = new IdProcessor(wiki, space, pageName);
                c.setPageId(idBuilder.getPageId());

                c.setReplyTo(comment.getReplyTo());

                result.add(c);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getObjectProperties(org.xwiki.eclipse.model.XWikiEclipseObjectSummary)
     */
    @Override
    public List<XWikiEclipseObjectProperty> getObjectProperties(String wiki, String space, String pageName,
        String className, int number)
    {
        List<XWikiEclipseObjectProperty> result = new ArrayList<XWikiEclipseObjectProperty>();

        List<Property> properties =
            this.restRemoteStorage.getObjectProperties(wiki, space, pageName, className, number);
        if (properties != null) {

            for (Property property : properties) {
                XWikiEclipseObjectProperty p = new XWikiEclipseObjectProperty(dataManager);
                p.setName(property.getName());
                p.setType(property.getType());
                p.setValue(property.getValue());
                p.setWiki(wiki);
                p.setSpace(space);
                p.setPage(pageName);
                p.setClassName(className);
                p.setNumber(number);

                Map<String, String> attributeMap = new HashMap<String, String>();

                List<Attribute> attributes = property.getAttributes();
                for (Attribute attribute : attributes) {
                    attributeMap.put(attribute.getName(), attribute.getValue());
                }

                p.setAttributes(attributeMap);

                result.add(p);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#download(java.lang.String, java.util.List)
     */
    @Override
    public void download(String dir, XWikiEclipseAttachment attachment)
    {
        if (attachment != null) {
            restRemoteStorage.download(dir, attachment.getAbsoluteUrl(), attachment.getName());
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getPage(org.xwiki.eclipse.model.ModelObject)
     */
    @Override
    public XWikiEclipsePage getPage(String wiki, String space, String pageName, String language)
        throws XWikiEclipseStorageException
    {
        Page page;
        try {
            page = restRemoteStorage.getPage(wiki, space, pageName, language);
            XWikiEclipsePage result = new XWikiEclipsePage(dataManager);
            result.setFullName(page.getFullName());
            result.setId(page.getId());
            result.setName(page.getName());
            result.setSpace(page.getSpace());
            result.setSyntax(page.getSyntax());
            result.setTitle(page.getTitle());
            result.setWiki(page.getWiki());
            result.setContent(page.getContent());
            result.setCreated(page.getCreated());
            result.setCreator(page.getCreator());
            result.setLanguage(page.getLanguage());
            result.setMajorVersion(page.getMajorVersion());
            result.setMinorVersion(page.getMinorVersion());
            result.setModified(page.getModified());
            result.setModifier(page.getModifier());
            result.setParentId(page.getParentId());
            result.setVersion(page.getVersion());

            if (language != null && !language.equals("")) {
                result.setUrl(page.getXwikiAbsoluteUrl() + "?language=" + language);
            } else {
                result.setUrl(page.getXwikiAbsoluteUrl() + "?language=default");
            }

            return result;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            throw new XWikiEclipseStorageException(e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getObject(org.xwiki.eclipse.model.XWikiEclipseObjectSummary)
     */
    @Override
    public XWikiEclipseObject getObject(String wiki, String space, String pageName, String className, int number)
    {
        XWikiEclipseObject result = new XWikiEclipseObject(dataManager);

        org.xwiki.rest.model.jaxb.Object object = restRemoteStorage.getObject(wiki, space, pageName, className, number);
        result.setName(object.getId());
        result.setClassName(object.getClassName());
        result.setId(object.getId());
        result.setPageId(object.getPageId());
        result.setSpace(object.getSpace());
        result.setWiki(object.getWiki());
        result.setPageName(object.getPageName());
        result.setNumber(object.getNumber());

        List<Property> props = object.getProperties();

        for (Property property : props) {
            XWikiEclipseObjectProperty p = new XWikiEclipseObjectProperty(dataManager);
            p.setName(property.getName());
            p.setType(property.getType());
            p.setValue(property.getValue());
            Map<String, String> attributeMap = new HashMap<String, String>();

            List<Attribute> attributes = property.getAttributes();
            for (Attribute attribute : attributes) {
                attributeMap.put(attribute.getName(), attribute.getValue());
            }

            p.setAttributes(attributeMap);

            result.getProperties().add(p);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#storeComment(org.xwiki.eclipse.model.XWikiEclipseComment)
     */
    @Override
    public XWikiEclipseComment storeComment(XWikiEclipseComment c)
    {
        Comment comment = new Comment();
        comment.setAuthor(c.getAuthor());
        comment.setDate(c.getDate());
        comment.setHighlight(c.getHighlight());
        comment.setPageId(c.getPageId());
        comment.setReplyTo(c.getReplyTo());
        comment.setText(c.getText());

        IdProcessor parser = new IdProcessor(c.getPageId());
        Comment stored = restRemoteStorage.storeComment(parser.getWiki(), parser.getSpace(), parser.getPage(), comment);

        XWikiEclipseComment result = new XWikiEclipseComment(dataManager);
        result.setAuthor(stored.getAuthor());
        result.setDate(stored.getDate());
        result.setHighlight(stored.getHighlight());
        result.setId(stored.getId());
        result.setText(stored.getText());
        /* current implementation of rest API does not return pageid */
        // result.setPageId(stored.getPageId());
        result.setPageId(c.getPageId());
        result.setReplyTo(stored.getReplyTo());

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getPageSummary(org.xwiki.eclipse.model.ModelObject)
     */
    @Override
    public XWikiEclipsePageSummary getPageSummary(String wiki, String space, String pageName, String language)
    {
        List<XWikiEclipsePageSummary> pageSummaries = getPageSummaries(wiki, space);
        for (XWikiEclipsePageSummary pageSummary : pageSummaries) {
            if (pageSummary.getName().equals(pageName) && pageSummary.getLanguage().equals(language)) {
                return pageSummary;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#uploadAttachment(org.xwiki.eclipse.model.XWikiEclipsePageSummary,
     *      java.lang.String)
     */
    @Override
    public void uploadAttachment(String wiki, String space, String pageName, URL fileUrl)
    {
        File f;
        try {
            f = new File(fileUrl.toURI());

            restRemoteStorage.uploadAttachment(wiki, space, pageName, f.getName(), fileUrl);

        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getSpace(org.xwiki.eclipse.model.XWikiEclipsePageSummary)
     */
    @Override
    public XWikiEclipseSpaceSummary getSpace(String wiki, String space)
    {
        Space s = restRemoteStorage.getSpace(wiki, space);
        XWikiEclipseSpaceSummary result = new XWikiEclipseSpaceSummary(dataManager);

        result.setId(s.getId());
        result.setName(s.getName());
        result.setUrl(s.getXwikiAbsoluteUrl());
        result.setWiki(s.getWiki());

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#updateAttachment(org.xwiki.eclipse.model.XWikiEclipseAttachment,
     *      java.lang.String)
     */
    @Override
    public void updateAttachment(String wiki, String space, String pageName, String attachmentName, URL fileUrl)
    {
        restRemoteStorage.uploadAttachment(wiki, space, pageName, attachmentName, fileUrl);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getAllTagsInWiki(org.xwiki.eclipse.model.ModelObject)
     */
    @Override
    public List<XWikiEclipseTag> getAllTagsInWiki(String wiki)
    {
        List<XWikiEclipseTag> result = new ArrayList<XWikiEclipseTag>();

        List<Tag> tags = restRemoteStorage.getAllTagsInWiki(wiki);
        for (Tag tag : tags) {
            XWikiEclipseTag t = new XWikiEclipseTag(dataManager);

            t.setName(tag.getName());
            t.setWiki(wiki);

            result.add(t);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#addTag(org.xwiki.eclipse.model.XWikiEclipsePageSummary,
     *      java.lang.String)
     */
    @Override
    public XWikiEclipseTag addTag(String wiki, String space, String pageName, String tagName)
    {
        List<Tag> tags = this.restRemoteStorage.addTag(wiki, space, pageName, tagName);

        XWikiEclipseTag result = new XWikiEclipseTag(dataManager);
        for (Tag t : tags) {
            if (t.getName().equals(tagName)) {
                result.setName(t.getName());
                result.setWiki(wiki);

                break;
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getClasses(java.lang.String)
     */
    @Override
    public List<XWikiEclipseClass> getClasses(String wiki)
    {
        List<org.xwiki.rest.model.jaxb.Class> classes = this.restRemoteStorage.getClasses(wiki);
        List<XWikiEclipseClass> result = new ArrayList<XWikiEclipseClass>();

        for (org.xwiki.rest.model.jaxb.Class clazz : classes) {
            XWikiEclipseClass c = new XWikiEclipseClass(dataManager);
            c.setId(clazz.getId());
            c.setName(clazz.getName());
            c.setWiki(wiki);

            /* populate the properties for page class */
            List<Property> properties = clazz.getProperties();

            for (Property property : properties) {
                XWikiEclipseObjectProperty p = new XWikiEclipseObjectProperty(dataManager);
                p.setName(property.getName());
                p.setType(property.getType());
                p.setValue(property.getValue());
                Map<String, String> attributeMap = new HashMap<String, String>();

                List<Attribute> attributes = property.getAttributes();
                for (Attribute attribute : attributes) {
                    attributeMap.put(attribute.getName(), attribute.getValue());
                }

                p.setAttributes(attributeMap);

                c.getProperties().add(p);
            }

            result.add(c);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getClass(java.lang.String, java.lang.String)
     */
    @Override
    public XWikiEclipseClass getClass(String wiki, String className)
    {
        org.xwiki.rest.model.jaxb.Class clazz = restRemoteStorage.getClass(wiki, className);

        XWikiEclipseClass result = new XWikiEclipseClass(dataManager);
        result.setId(clazz.getId());
        result.setName(clazz.getName());
        result.setWiki(wiki);

        /* populate the properties for page class */
        List<Property> properties = clazz.getProperties();

        for (Property property : properties) {
            XWikiEclipseObjectProperty p = new XWikiEclipseObjectProperty(dataManager);
            p.setName(property.getName());
            p.setType(property.getType());
            p.setValue(property.getValue());
            Map<String, String> attributeMap = new HashMap<String, String>();

            List<Attribute> attributes = property.getAttributes();
            for (Attribute attribute : attributes) {
                attributeMap.put(attribute.getName(), attribute.getValue());
            }

            p.setAttributes(attributeMap);

            result.getProperties().add(p);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#remove(org.xwiki.eclipse.model.ModelObject)
     */
    @Override
    public void remove(ModelObject o) throws XWikiEclipseStorageException
    {
        if (o instanceof XWikiEclipseObjectSummary) {
            XWikiEclipseObjectSummary objectSummary = (XWikiEclipseObjectSummary) o;
            try {
                restRemoteStorage.removeObject(objectSummary.getWiki(), objectSummary.getSpace(),
                    objectSummary.getPageName(), objectSummary.getClassName(), objectSummary.getNumber());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (o instanceof XWikiEclipsePageSummary) {
            XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) o;
            try {
                restRemoteStorage.removePage(pageSummary.getWiki(), pageSummary.getSpace(), pageSummary.getName(),
                    pageSummary.getLanguage());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new XWikiEclipseStorageException(e);
            }
        }

        if (o instanceof XWikiEclipseComment) {
            /* current REST API does not provide a Url for comment deletion, use the object Url instead */
            XWikiEclipseComment comment = (XWikiEclipseComment) o;
            String commentClassName = "XWiki.XWikiComments";
            IdProcessor parser = new IdProcessor(comment.getPageId());
            try {
                restRemoteStorage.removeObject(parser.getWiki(), parser.getSpace(), parser.getPage(), commentClassName,
                    comment.getId());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new XWikiEclipseStorageException(e);
            }
        }

        if (o instanceof XWikiEclipseTag) {
            /*
             * current REST API does not provide a Url for tag deletion
             */
            XWikiEclipseTag tag = (XWikiEclipseTag) o;
            try {
                restRemoteStorage.removeTag(tag.getWiki(), tag.getSpace(), tag.getPage(), tag.getName());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new XWikiEclipseStorageException(e);
            }
        }

        if (o instanceof XWikiEclipseAttachment) {
            XWikiEclipseAttachment attachment = (XWikiEclipseAttachment) o;
            IdProcessor parser = new IdProcessor(attachment.getPageId());
            try {
                restRemoteStorage.removeAttachment(parser.getWiki(), parser.getSpace(), parser.getPage(),
                    attachment.getName());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new XWikiEclipseStorageException(e);
            }
        }

        // if (url != null) {
        // restRemoteStorage.remove(url);
        // }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#exists(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public boolean pageExists(String wiki, String space, String pageName, String language)
    {
        XWikiEclipsePageSummary pageSummary = getPageSummary(wiki, space, pageName, language);
        if (pageSummary != null) {
            return true;
        }

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
        Page pageToBeStored = new Page();
        pageToBeStored.setContent(page.getContent());
        pageToBeStored.setCreated(page.getCreated());
        pageToBeStored.setCreator(page.getCreator());
        pageToBeStored.setFullName(page.getFullName());
        pageToBeStored.setId(page.getId());
        pageToBeStored.setLanguage(page.getLanguage());
        pageToBeStored.setMajorVersion(page.getMajorVersion());
        pageToBeStored.setMinorVersion(page.getMinorVersion());
        pageToBeStored.setModified(page.getModified());
        pageToBeStored.setModifier(page.getModifier());
        pageToBeStored.setName(page.getName());
        pageToBeStored.setParentId(page.getParentId());
        pageToBeStored.setSpace(page.getSpace());
        pageToBeStored.setSyntax(page.getSyntax());
        pageToBeStored.setTitle(page.getTitle());
        pageToBeStored.setVersion(page.getVersion());
        pageToBeStored.setWiki(page.getWiki());

        Page storedPage = restRemoteStorage.storePage(pageToBeStored);

        XWikiEclipsePage result = new XWikiEclipsePage(dataManager);
        result.setContent(storedPage.getContent());
        result.setCreated(storedPage.getCreated());
        result.setCreator(storedPage.getCreator());
        result.setFullName(storedPage.getFullName());
        result.setId(storedPage.getId());
        result.setLanguage(storedPage.getLanguage());
        result.setMajorVersion(storedPage.getMajorVersion());
        result.setMinorVersion(storedPage.getMinorVersion());
        result.setModified(storedPage.getModified());
        result.setModifier(storedPage.getModifier());
        result.setName(storedPage.getName());
        result.setParentId(storedPage.getParentId());
        result.setSpace(storedPage.getSpace());
        result.setSyntax(storedPage.getSyntax());
        result.setTitle(storedPage.getTitle());
        result.setVersion(storedPage.getVersion());
        result.setWiki(storedPage.getWiki());
        result.setUrl(storedPage.getXwikiAbsoluteUrl());

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getPageHistory(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, int, int)
     */
    @Override
    public XWikiEclipsePage getPageHistory(String wiki, String space, String name, String language, int majorVersion,
        int minorVersion) throws XWikiEclipseStorageException
    {
        Page page;
        try {
            page = restRemoteStorage.getPageHistory(wiki, space, name, language, majorVersion, minorVersion);
            XWikiEclipsePage result = new XWikiEclipsePage(dataManager);
            result.setContent(page.getContent());
            result.setCreated(page.getCreated());
            result.setCreator(page.getCreator());
            result.setFullName(page.getFullName());
            result.setId(page.getId());
            result.setLanguage(page.getLanguage());
            result.setMajorVersion(page.getMajorVersion());
            result.setMinorVersion(page.getMinorVersion());
            result.setModified(page.getModified());
            result.setModifier(page.getModifier());
            result.setName(page.getName());
            result.setParentId(page.getParentId());
            result.setSpace(page.getSpace());
            result.setSyntax(page.getSyntax());
            result.setTitle(page.getTitle());
            result.setVersion(page.getVersion());
            result.setWiki(page.getWiki());
            result.setUrl(page.getXwikiAbsoluteUrl());

            return result;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new XWikiEclipseStorageException(e);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#storeObject(org.xwiki.eclipse.model.XWikiEclipseObject)
     */
    @Override
    public XWikiEclipseObject storeObject(XWikiEclipseObject o)
    {
        org.xwiki.rest.model.jaxb.Object object = new Object();
        object.setClassName(o.getClassName());
        object.setWiki(o.getWiki());
        object.setSpace(o.getSpace());
        object.setPageName(o.getPageName());
        object.setPageId(o.getPageId());
        /* if the number is -1, use the /objects url. otherwise, use the url of /objects/classname/number */
        object.setNumber(o.getNumber());

        List<XWikiEclipseObjectProperty> properties = o.getProperties();
        for (XWikiEclipseObjectProperty prop : properties) {
            Property p = new Property();
            Map<String, String> attributes = prop.getAttributes();
            for (String s : attributes.keySet()) {
                Attribute a = new Attribute();
                a.setName(s);
                a.setValue(attributes.get(s));

                p.getAttributes().add(a);
            }

            p.setName(prop.getName());
            p.setType(prop.getType());
            p.setValue(prop.getValue());

            object.getProperties().add(p);
        }

        object = restRemoteStorage.storeObject(object);

        XWikiEclipseObject result = new XWikiEclipseObject(dataManager);

        result.setName(object.getId());
        result.setClassName(object.getClassName());
        result.setId(object.getId());
        result.setPageId(object.getPageId());
        result.setSpace(object.getSpace());
        result.setWiki(object.getWiki());
        result.setPageName(object.getPageName());
        result.setNumber(object.getNumber());

        List<Property> props = object.getProperties();

        for (Property property : props) {
            XWikiEclipseObjectProperty p = new XWikiEclipseObjectProperty(dataManager);
            p.setName(property.getName());
            p.setType(property.getType());
            p.setValue(property.getValue());

            List<Attribute> attributes = property.getAttributes();
            for (Attribute attribute : attributes) {
                p.getAttributes().put(attribute.getName(), attribute.getValue());
            }

            result.getProperties().add(p);
        }

        return result;
    }
}
