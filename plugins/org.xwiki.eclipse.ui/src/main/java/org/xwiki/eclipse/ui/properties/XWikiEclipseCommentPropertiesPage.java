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
 *
 */
package org.xwiki.eclipse.ui.properties;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;

/**
 * @version $Id$
 */
public class XWikiEclipseCommentPropertiesPage extends PropertyPage
{

    public XWikiEclipseCommentPropertiesPage()
    {
        super();
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);

        XWikiEclipseComment comment = (XWikiEclipseComment) getElement().getAdapter(XWikiEclipseComment.class);
        XWikiEclipsePage page = null;
        try {
            page = comment.getDataManager().getPage(comment);
        } catch (XWikiEclipseStorageException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Label property = new Label(composite, SWT.NONE);
        property.setText("ID:");
        Label value = new Label(composite, SWT.NONE);
        value.setText(Integer.toString(comment.getId()));

        property = new Label(composite, SWT.NONE);
        property.setText("Page:");
        value = new Label(composite, SWT.NONE);
        value.setText(page.getName() == null ? "" : page.getName());

        property = new Label(composite, SWT.NONE);
        property.setText("Space:");
        value = new Label(composite, SWT.NONE);
        value.setText(page.getSpace() == null ? "" : page.getSpace());

        property = new Label(composite, SWT.NONE);
        property.setText("Wiki:");
        value = new Label(composite, SWT.NONE);
        value.setText(page.getWiki() == null ? "" : page.getWiki());

        property = new Label(composite, SWT.NONE);
        property.setText("Author:");
        value = new Label(composite, SWT.NONE);
        value.setText(comment.getAuthor() == null ? "" : comment.getAuthor());

        return composite;
    }

}
