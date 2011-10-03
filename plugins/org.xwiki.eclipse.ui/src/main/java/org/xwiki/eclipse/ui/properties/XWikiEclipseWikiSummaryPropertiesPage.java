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
package org.xwiki.eclipse.ui.properties;

import java.util.Arrays;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;
import org.xwiki.eclipse.ui.utils.XWikiEclipseSafeRunnable;

/**
 * @version $Id$
 */
public class XWikiEclipseWikiSummaryPropertiesPage extends PropertyPage
{
    private XWikiEclipseWikiSummary wiki;

    public XWikiEclipseWikiSummaryPropertiesPage()
    {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);

        SafeRunner.run(new XWikiEclipseSafeRunnable()
        {
            public void run() throws Exception
            {
                wiki = (XWikiEclipseWikiSummary) getElement().getAdapter(XWikiEclipseWikiSummary.class);

                Label label = new Label(composite, SWT.BORDER);
                label.setText("ID:");
                label = new Label(composite, SWT.BORDER);
                label.setText(wiki.getWikiId());

                label = new Label(composite, SWT.BORDER);
                label.setText("Name:");
                label = new Label(composite, SWT.BORDER);
                label.setText(wiki.getName());

                label = new Label(composite, SWT.BORDER);
                label.setText("Version:");
                label = new Label(composite, SWT.BORDER);
                label.setText(wiki.getVersion());

                label = new Label(composite, SWT.BORDER);
                label.setText("Base URL:");
                label = new Label(composite, SWT.BORDER);
                label.setText(wiki.getBaseUrl());

                label = new Label(composite, SWT.BORDER);
                label.setText("Syntaxes:");
                label = new Label(composite, SWT.BORDER);
                label.setText(Arrays.toString(wiki.getSyntaxes().toArray()));
            }

        });

        return composite;
    }
}
