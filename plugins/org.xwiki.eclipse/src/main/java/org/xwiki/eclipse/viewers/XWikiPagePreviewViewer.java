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
package org.xwiki.eclipse.viewers;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.xwiki.eclipse.model.IXWikiPage;

public class XWikiPagePreviewViewer
{
    private Composite composite;

    private StackLayout stackLayout;

    private Browser browser;

    private Composite notConnectedComposite;

    private Composite noPageSelectedComposite;
    
    private IXWikiPage xwikiPage;

    public XWikiPagePreviewViewer(Composite parent)
    {
        composite = new Composite(parent, SWT.NONE);
        stackLayout = new StackLayout();
        composite.setLayout(stackLayout);

        browser = new Browser(composite, SWT.NONE);

        notConnectedComposite = new Composite(composite, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(notConnectedComposite);
        notConnectedComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        Label label = new Label(notConnectedComposite, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).applyTo(
            label);
        label.setText("No preview available if not connected.");
        label.setFont(JFaceResources.getHeaderFont());
        label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        label.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

        noPageSelectedComposite = new Composite(composite, SWT.NONE);
        noPageSelectedComposite
            .setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(noPageSelectedComposite);
        label = new Label(noPageSelectedComposite, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).applyTo(
            label);
        label.setText("No page selected");
        label.setFont(JFaceResources.getHeaderFont());
        label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        label.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

        stackLayout.topControl = noPageSelectedComposite;
        composite.layout();
    }

    public void showPreview(IXWikiPage xwikiPage)
    {
    	this.xwikiPage = xwikiPage;
    	
        if (xwikiPage == null) {
            stackLayout.topControl = noPageSelectedComposite;
            composite.layout();
        } else {
            if (xwikiPage.getConnection().isConnected()) {
                browser.setUrl(xwikiPage.getUrl());
                stackLayout.topControl = browser;
                composite.layout();
            } else {
                stackLayout.topControl = notConnectedComposite;
                composite.layout();
            }
        }
    }
    
    public void showURL(String url) {
    	browser.setUrl(url);
        stackLayout.topControl = browser;
        composite.layout();
    }

	public IXWikiPage getXwikiPage()
	{
		return xwikiPage;
	}

	public Browser getBrowser()
	{
		return browser;
	}
	
	public Control getControl() {
		return composite;
	}
    
}
