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
import java.net.URI;
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
import org.xwiki.eclipse.storage.rest.XWikiRestClient;
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
public class RestRemoteXWikiDataStorage implements IRemoteXWikiDataStorage
{
    XWikiRestClient restClient;

    private DataManager dataManager;

    private String endpoint;

    private String username;
    
    public RestRemoteXWikiDataStorage(DataManager dataManager, String endpoint, String userName, String password)
        throws XWikiEclipseStorageException
    {
        this.username = userName;
        this.dataManager = dataManager;
        this.restClient = new XWikiRestClient(endpoint, userName, password);
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

            List<Wiki> wikis = restClient.getWikis();
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
            throw new XWikiEclipseStorageException(e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#getSpaces()
     */
    @Override
    public List<XWikiEclipseSpaceSummary> getSpaceSummaries(String wikiId) throws XWikiEclipseStorageException
    {
        List<XWikiEclipseSpaceSummary> result = new ArrayList<XWikiEclipseSpaceSummary>();

        try {
            List<Space> spaces = this.restClient.getSpaces(wikiId);
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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
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
            xwiki = this.restClient.getServerInfo();
            Syntaxes syntaxes = this.restClient.getSyntaxes();
            List<String> syntaxeList = syntaxes.getSyntaxes();
            serverInfo.setSyntaxes(syntaxeList);

            // TODO: Improve this
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
        throws XWikiEclipseStorageException
    {
        try {
            List<XWikiEclipsePageSummary> result = new ArrayList<XWikiEclipsePageSummary>();

            List<PageSummary> pages = restClient.getPages(wiki, space);
            if (pages != null) {

                for (PageSummary pageSummary : pages) {
                    List<XWikiEclipsePageSummary.Data> data = new ArrayList<XWikiEclipsePageSummary.Data>();
                    for (Link link : pageSummary.getLinks()) {
                        if ("http://www.xwiki.org/rel/objects".equals(link.getRel())) {
                            data.add(XWikiEclipsePageSummary.Data.OBJECTS);
                        } else if ("http://www.xwiki.org/rel/comments".equals(link.getRel())) {
                            data.add(XWikiEclipsePageSummary.Data.COMMENTS);
                        } else if ("http://www.xwiki.org/rel/attachments".equals(link.getRel())) {
                            data.add(XWikiEclipsePageSummary.Data.ATTACHMENTS);
                        } else if ("http://www.xwiki.org/rel/tags".equals(link.getRel())) {
                            data.add(XWikiEclipsePageSummary.Data.TAGS);
                        }
                    }

                    XWikiEclipsePageSummary page =
                        new XWikiEclipsePageSummary(dataManager, data.toArray(new XWikiEclipsePageSummary.Data[0]));
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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public List<XWikiEclipseObjectSummary> getObjectSummaries(String wiki, String space, String pageName)
        throws XWikiEclipseStorageException
    {
        try {
            List<XWikiEclipseObjectSummary> result = new ArrayList<XWikiEclipseObjectSummary>();

            List<ObjectSummary> objects = restClient.getObjects(wiki, space, pageName);

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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public List<XWikiEclipseAttachment> getAttachments(String wiki, String space, String pageName)
        throws XWikiEclipseStorageException
    {
        try {
            List<XWikiEclipseAttachment> result = new ArrayList<XWikiEclipseAttachment>();

            List<Attachment> attachments = restClient.getAttachments(wiki, space, pageName);
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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public List<XWikiEclipsePageHistorySummary> getPageHistorySummaries(String wiki, String space, String pageName,
        String language) throws XWikiEclipseStorageException
    {
        try {
            List<XWikiEclipsePageHistorySummary> result = new ArrayList<XWikiEclipsePageHistorySummary>();

            List<HistorySummary> history = restClient.getPageHistory(wiki, space, pageName, language);
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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }

    }

    @Override
    public XWikiEclipseClass getClass(String wiki, String space, String pageName) throws XWikiEclipseStorageException
    {
        try {
            String className = String.format("%s.%s", space, pageName);
            org.xwiki.rest.model.jaxb.Class classSummary = restClient.getClass(wiki, className);

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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public List<XWikiEclipseTag> getTags(String wiki, String space, String page) throws XWikiEclipseStorageException
    {
        try {
            List<XWikiEclipseTag> result = new ArrayList<XWikiEclipseTag>();

            List<Tag> tags = this.restClient.getTags(wiki, space, page);

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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public List<XWikiEclipseComment> getComments(String wiki, String space, String pageName)
        throws XWikiEclipseStorageException
    {
        try {
            List<XWikiEclipseComment> result = new ArrayList<XWikiEclipseComment>();
            List<Comment> comments = restClient.getComments(wiki, space, pageName);

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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public List<XWikiEclipseObjectProperty> getObjectProperties(String wiki, String space, String pageName,
        String className, int number) throws XWikiEclipseStorageException
    {
        try {
            List<XWikiEclipseObjectProperty> result = new ArrayList<XWikiEclipseObjectProperty>();

            List<Property> properties =
                this.restClient.getObjectProperties(wiki, space, pageName, className, number);
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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public void download(String dir, XWikiEclipseAttachment attachment) throws XWikiEclipseStorageException
    {
        try {
            if (attachment != null) {
                URI absoluteURI = new URI(attachment.getAbsoluteUrl());
                restClient.download(dir, absoluteURI, attachment.getName());
            }
        } catch (URISyntaxException e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public synchronized XWikiEclipsePage getPage(String wiki, String space, String pageName, String language)
        throws XWikiEclipseStorageException
    {
        try {
            Page page = restClient.getPage(wiki, space, pageName, language);
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
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public XWikiEclipseObject getObject(String wiki, String space, String pageName, String className, int number)
        throws XWikiEclipseStorageException
    {
        try {
            XWikiEclipseObject result = new XWikiEclipseObject(dataManager);

            org.xwiki.rest.model.jaxb.Object object =
                restClient.getObject(wiki, space, pageName, className, number);
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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public XWikiEclipseComment storeComment(XWikiEclipseComment c) throws XWikiEclipseStorageException
    {
        try {
            Comment comment = new Comment();
            comment.setAuthor(c.getAuthor());
            comment.setDate(c.getDate());
            comment.setHighlight(c.getHighlight());
            comment.setPageId(c.getPageId());
            comment.setReplyTo(c.getReplyTo());
            comment.setText(c.getText());

            IdProcessor parser = new IdProcessor(c.getPageId());
            Comment stored =
                restClient.storeComment(parser.getWiki(), parser.getSpace(), parser.getPage(), comment);

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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public XWikiEclipsePageSummary getPageSummary(String wiki, String space, String pageName, String language)
        throws XWikiEclipseStorageException
    {
        try {
            Page page = restClient.getPage(wiki, space, pageName, language);

            List<XWikiEclipsePageSummary.Data> data = new ArrayList<XWikiEclipsePageSummary.Data>();
            for (Link link : page.getLinks()) {
                if ("http://www.xwiki.org/rel/objects".equals(link.getRel())) {
                    data.add(XWikiEclipsePageSummary.Data.OBJECTS);
                } else if ("http://www.xwiki.org/rel/comments".equals(link.getRel())) {
                    data.add(XWikiEclipsePageSummary.Data.COMMENTS);
                } else if ("http://www.xwiki.org/rel/attachments".equals(link.getRel())) {
                    data.add(XWikiEclipsePageSummary.Data.ATTACHMENTS);
                } else if ("http://www.xwiki.org/rel/tags".equals(link.getRel())) {
                    data.add(XWikiEclipsePageSummary.Data.TAGS);
                }
            }

            XWikiEclipsePageSummary pageSummary =
                new XWikiEclipsePageSummary(dataManager, data.toArray(new XWikiEclipsePageSummary.Data[0]));
            pageSummary.setFullName(page.getFullName());
            pageSummary.setId(page.getId());
            pageSummary.setLanguage(page.getLanguage());
            pageSummary.setName(page.getName());
            pageSummary.setParentId(page.getParentId());
            pageSummary.setSpace(page.getSpace());
            pageSummary.setSyntax(page.getSyntax());
            pageSummary.setTitle(page.getTitle());
            pageSummary.setUrl(page.getXwikiAbsoluteUrl());
            pageSummary.setWiki(page.getWiki());

            return pageSummary;
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public void uploadAttachment(String wiki, String space, String pageName, URL fileUrl)
        throws XWikiEclipseStorageException
    {
        try {
            File f = new File(fileUrl.toURI());
            restClient.uploadAttachment(wiki, space, pageName, f.getName(), fileUrl);
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public XWikiEclipseSpaceSummary getSpace(String wiki, String space) throws XWikiEclipseStorageException
    {
        try {
            Space s = restClient.getSpace(wiki, space);
            XWikiEclipseSpaceSummary result = new XWikiEclipseSpaceSummary(dataManager);

            result.setId(s.getId());
            result.setName(s.getName());
            result.setUrl(s.getXwikiAbsoluteUrl());
            result.setWiki(s.getWiki());

            return result;
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public void updateAttachment(String wiki, String space, String pageName, String attachmentName, URL fileUrl)
        throws XWikiEclipseStorageException
    {
        try {
            restClient.uploadAttachment(wiki, space, pageName, attachmentName, fileUrl);
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public List<XWikiEclipseTag> getAllTagsInWiki(String wiki) throws XWikiEclipseStorageException
    {
        // FIXME: REFACTORING: Change the name of this method to getAllTags
        try {
            List<XWikiEclipseTag> result = new ArrayList<XWikiEclipseTag>();

            List<Tag> tags = restClient.getAllTags(wiki);
            for (Tag tag : tags) {
                XWikiEclipseTag t = new XWikiEclipseTag(dataManager);

                t.setName(tag.getName());
                t.setWiki(wiki);

                result.add(t);
            }

            return result;
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public XWikiEclipseTag addTag(String wiki, String space, String pageName, String tagName)
        throws XWikiEclipseStorageException
    {
        try {
            List<Tag> tags = this.restClient.addTag(wiki, space, pageName, tagName);

            XWikiEclipseTag result = new XWikiEclipseTag(dataManager);
            for (Tag t : tags) {
                if (t.getName().equals(tagName)) {
                    result.setName(t.getName());
                    result.setWiki(wiki);

                    break;
                }
            }

            return result;
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public List<XWikiEclipseClass> getClasses(String wiki) throws XWikiEclipseStorageException
    {
        try {
            List<org.xwiki.rest.model.jaxb.Class> classes = this.restClient.getClasses(wiki);
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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public XWikiEclipseClass getClass(String wiki, String className) throws XWikiEclipseStorageException
    {
        try {
            org.xwiki.rest.model.jaxb.Class clazz = restClient.getClass(wiki, className);

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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
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
                restClient.removeObject(objectSummary.getWiki(), objectSummary.getSpace(),
                    objectSummary.getPageName(), objectSummary.getClassName(), objectSummary.getNumber());
            } catch (Exception e) {
                throw new XWikiEclipseStorageException(e);
            }
        }

        if (o instanceof XWikiEclipsePageSummary) {
            XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) o;
            try {
                restClient.removePage(pageSummary.getWiki(), pageSummary.getSpace(), pageSummary.getName(),
                    pageSummary.getLanguage());
            } catch (Exception e) {
                throw new XWikiEclipseStorageException(e);
            }
        }

        if (o instanceof XWikiEclipseComment) {
            /*
             * current REST API does not provide a Url for comment deletion, use the object Url instead
             */
            XWikiEclipseComment comment = (XWikiEclipseComment) o;
            String commentClassName = "XWiki.XWikiComments";
            IdProcessor parser = new IdProcessor(comment.getPageId());
            try {
                restClient.removeObject(parser.getWiki(), parser.getSpace(), parser.getPage(), commentClassName,
                    comment.getId());
            } catch (Exception e) {
                throw new XWikiEclipseStorageException(e);
            }
        }

        if (o instanceof XWikiEclipseTag) {
            /*
             * current REST API does not provide a Url for tag deletion
             */
            XWikiEclipseTag tag = (XWikiEclipseTag) o;
            try {
                restClient.removeTag(tag.getWiki(), tag.getSpace(), tag.getPage(), tag.getName());
            } catch (Exception e) {
                throw new XWikiEclipseStorageException(e);
            }
        }

        if (o instanceof XWikiEclipseAttachment) {
            XWikiEclipseAttachment attachment = (XWikiEclipseAttachment) o;
            IdProcessor parser = new IdProcessor(attachment.getPageId());
            try {
                restClient.removeAttachment(parser.getWiki(), parser.getSpace(), parser.getPage(),
                    attachment.getName());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new XWikiEclipseStorageException(e);
            }
        }
    }

    @Override
    public boolean pageExists(String wiki, String space, String pageName, String language)
        throws XWikiEclipseStorageException
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
     * @throws XWikiEclipseStorageException
     * @see org.xwiki.eclipse.storage.IRemoteXWikiDataStorage#storePage(org.xwiki.eclipse.model.XWikiEclipsePage)
     */
    @Override
    public XWikiEclipsePage storePage(XWikiEclipsePage page) throws XWikiEclipseStorageException
    {
        try {
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

            Page storedPage = restClient.storePage(pageToBeStored);

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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public XWikiEclipsePage getPageHistory(String wiki, String space, String name, String language, int majorVersion,
        int minorVersion) throws XWikiEclipseStorageException
    {
        try {
            Page page = restClient.getPageVersion(wiki, space, name, language, majorVersion, minorVersion);
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
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public XWikiEclipseObject storeObject(XWikiEclipseObject o) throws XWikiEclipseStorageException
    {
        try {
            org.xwiki.rest.model.jaxb.Object object = new Object();
            object.setClassName(o.getClassName());
            object.setWiki(o.getWiki());
            object.setSpace(o.getSpace());
            object.setPageName(o.getPageName());
            object.setPageId(o.getPageId());
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

            object = restClient.storeObject(object);

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
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public XWikiEclipsePage copyPage(XWikiEclipsePage sourcePage, String newWiki, String newSpace, String newPageName)
        throws XWikiEclipseStorageException
    {
        try {
            Page sourcePageToBeCopied = new Page();
            sourcePageToBeCopied.setCreated(sourcePage.getCreated());
            sourcePageToBeCopied.setCreator(sourcePage.getCreator());
            sourcePageToBeCopied.setFullName(sourcePage.getFullName());
            sourcePageToBeCopied.setId(sourcePage.getId());
            sourcePageToBeCopied.setLanguage(sourcePage.getLanguage());
            sourcePageToBeCopied.setMajorVersion(sourcePage.getMajorVersion());
            sourcePageToBeCopied.setMinorVersion(sourcePage.getMinorVersion());
            sourcePageToBeCopied.setModified(sourcePage.getModified());
            sourcePageToBeCopied.setModifier(sourcePage.getModifier());
            sourcePageToBeCopied.setName(sourcePage.getName());
            sourcePageToBeCopied.setParentId(sourcePage.getParentId());
            sourcePageToBeCopied.setSpace(sourcePage.getSpace());
            sourcePageToBeCopied.setSyntax(sourcePage.getSyntax());
            sourcePageToBeCopied.setTitle(sourcePage.getTitle());
            sourcePageToBeCopied.setVersion(sourcePage.getVersion());
            sourcePageToBeCopied.setWiki(sourcePage.getWiki());

            Page page =
                restClient.copyPage(sourcePageToBeCopied, newWiki, newSpace, newPageName,
                    sourcePage.getLanguage());

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
            throw new XWikiEclipseStorageException(e);
        }
    }

    @Override
    public XWikiEclipsePage movePage(XWikiEclipsePage sourcePage, String newWiki, String newSpace, String newPageName)
        throws XWikiEclipseStorageException
    {
        try {
            Page sourcePageToBeMoved = new Page();
            sourcePageToBeMoved.setCreated(sourcePage.getCreated());
            sourcePageToBeMoved.setCreator(sourcePage.getCreator());
            sourcePageToBeMoved.setFullName(sourcePage.getFullName());
            sourcePageToBeMoved.setId(sourcePage.getId());
            sourcePageToBeMoved.setLanguage(sourcePage.getLanguage());
            sourcePageToBeMoved.setMajorVersion(sourcePage.getMajorVersion());
            sourcePageToBeMoved.setMinorVersion(sourcePage.getMinorVersion());
            sourcePageToBeMoved.setModified(sourcePage.getModified());
            sourcePageToBeMoved.setModifier(sourcePage.getModifier());
            sourcePageToBeMoved.setName(sourcePage.getName());
            sourcePageToBeMoved.setParentId(sourcePage.getParentId());
            sourcePageToBeMoved.setSpace(sourcePage.getSpace());
            sourcePageToBeMoved.setSyntax(sourcePage.getSyntax());
            sourcePageToBeMoved.setTitle(sourcePage.getTitle());
            sourcePageToBeMoved.setVersion(sourcePage.getVersion());
            sourcePageToBeMoved.setWiki(sourcePage.getWiki());

            // FIXME: REFACTORING: Check language handling.
            Page page = restClient.renamePage(sourcePageToBeMoved, newWiki, newSpace, newPageName, null);
            if (page != null) {
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
            }

            return null;
        } catch (Exception e) {
            throw new XWikiEclipseStorageException(e);
        }
    }
}
