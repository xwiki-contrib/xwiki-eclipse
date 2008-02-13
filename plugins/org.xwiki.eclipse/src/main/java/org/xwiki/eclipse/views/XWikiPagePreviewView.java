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
package org.xwiki.eclipse.views;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.part.ViewPart;
import org.xwiki.eclipse.IXWikiEclipseEventListener;
import org.xwiki.eclipse.XWikiEclipseEvent;
import org.xwiki.eclipse.XWikiEclipseNotificationCenter;
import org.xwiki.eclipse.editors.XWikiPageEditor;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.utils.XWikiEclipseUtil;
import org.xwiki.eclipse.viewers.XWikiPagePreviewViewer;
import org.xwiki.plugins.eclipse.views.navigator.XWikiNavigator;

public class XWikiPagePreviewView extends ViewPart implements
		ISelectionListener, IXWikiEclipseEventListener
{
	public static final String ID = "org.xwiki.eclipse.views.XWikiPagePreview";
	private Text url;

	private XWikiPagePreviewViewer previewViewer;

	@Override
	public void createPartControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(composite);

		Composite bar = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(bar);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				false).applyTo(bar);

		url = new Text(bar, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				true).applyTo(url);
		url.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e)
			{
				previewViewer.showURL(url.getText());
				
			}

			public void widgetSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
		});
		
		Button openInExternalBrowser = new Button(bar, SWT.PUSH);
		openInExternalBrowser.setText("Open external browser");
		openInExternalBrowser.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e)
			{
				IWorkbenchBrowserSupport browserSupport = PlatformUI
						.getWorkbench().getBrowserSupport();

				IWebBrowser browser;
				try
				{
					browser = browserSupport.createBrowser("xeclipse");
					System.out.format("Opening browser on: %s\n", url.getText());
					browser.openURL(new URL(url.getText()));
				} catch (Exception e1)
				{
					MessageDialog.openWarning(Display.getDefault()
							.getActiveShell(), "Warning", String.format(
							"Unable to open external browser\n%s", e1));
					e1.printStackTrace();
				}
			}

		});

		previewViewer = new XWikiPagePreviewViewer(composite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				true).applyTo(previewViewer.getControl());

		getSite().getWorkbenchWindow().getSelectionService()
				.addPostSelectionListener(this);
		XWikiEclipseNotificationCenter.getDefault().addListener(
				XWikiEclipseEvent.PAGE_UPDATED, this);
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose()
	{
		getSite().getWorkbenchWindow().getSelectionService()
				.removePostSelectionListener(this);
		XWikiEclipseNotificationCenter.getDefault().removeListener(
				XWikiEclipseEvent.PAGE_UPDATED, this);
		super.dispose();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		if (part instanceof XWikiPageEditor)
		{
			return;
		}

		Object selectedObject = XWikiEclipseUtil
				.getSingleSelectedObjectInStructuredSelection(selection);
		if (selectedObject instanceof IXWikiPage)
		{
			IXWikiPage xwikiPage = (IXWikiPage) selectedObject;
			previewViewer.showPreview(xwikiPage);
			url.setText(xwikiPage.getUrl());
		} else
		{
			previewViewer.showPreview(null);
			url.setText("");
		}
	}

	public void handleEvent(Object sender, XWikiEclipseEvent event, Object data)
	{
		switch (event)
		{
		case PAGE_UPDATED:
			IXWikiPage xwikiPage = (IXWikiPage) data;
			previewViewer.showPreview(xwikiPage);
			url.setText(xwikiPage.getUrl());

			break;
		}

	}
}
