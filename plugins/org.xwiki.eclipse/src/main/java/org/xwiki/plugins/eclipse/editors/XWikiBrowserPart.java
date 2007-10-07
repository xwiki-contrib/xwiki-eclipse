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

package org.xwiki.plugins.eclipse.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.xwiki.plugins.eclipse.editors.ui.XWikiBrowserPartUI;
import org.xwiki.plugins.eclipse.model.IXWikiPage;
import org.xwiki.plugins.eclipse.util.GuiUtils;
import org.xwiki.plugins.eclipse.util.XWikiConstants;

/**
 * The browser component of the XWikiEditor.
 */
public class XWikiBrowserPart implements IEditorPart, Listener
{

    /**
     * Eclipse built-in Browser widget.
     */
    private Browser browser;

    /**
     * @see org.eclipse.ui.IEditorSite
     */
    private IEditorSite editorSite;

    /**
     * The ui of this editor tab.
     * 
     * @see org.xwiki.plugins.eclipse.editors.ui.XWikiBrowserPartUI
     */
    private XWikiBrowserPartUI xwikiBrowserUi;

    /**
     * The XWikiPage which is the current IEditorInput for this editor.
     */
    private IXWikiPage wikiPage;

    /**
     * Holds the state of the current view (print view or browser output view).
     */
    private boolean printView = true;

    /**
     * Constructor
     */
    public XWikiBrowserPart()
    {

    }

    /**
     * @return The internal browser widget used to render web pages.
     */
    public Browser getBrowser()
    {
        return browser;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorPart#getEditorInput()
     */
    public IEditorInput getEditorInput()
    {
        return (IEditorInput) wikiPage;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorPart#getEditorSite()
     */
    public IEditorSite getEditorSite()
    {
        return this.editorSite;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        if (input instanceof IXWikiPage) {
            this.wikiPage = (IXWikiPage) input;
            this.editorSite = site;
        } else {
            throw new PartInitException("Input should be an instance of XWikiPage");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor)
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public void doSaveAs()
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    public boolean isDirty()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.ISaveablePart#isSaveOnCloseNeeded()
     */
    public boolean isSaveOnCloseNeeded()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchPart#addPropertyListener(org.eclipse.ui.IPropertyListener)
     */
    public void addPropertyListener(IPropertyListener listener)
    {

    }

    /**
     * {@inheritDoc}
     * <p>
     * This is where we draw our ui
     * </p>
     * 
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent)
    {
        // xwikiBrowserUi = new XWikiBrowserPartUI(parent, SWT.NONE);
        xwikiBrowserUi = new XWikiBrowserPartUI(parent, SWT.NONE);
        xwikiBrowserUi.getToggleViewButton().addListener(SWT.Selection, this);
        browser = xwikiBrowserUi.getBrowser();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose()
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchPart#getSite()
     */
    public IWorkbenchPartSite getSite()
    {
        return this.editorSite;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchPart#getTitle()
     */
    public String getTitle()
    {
        return wikiPage.getTitle();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchPart#getTitleImage()
     */
    public Image getTitleImage()
    {
        ImageDescriptor desc = GuiUtils.loadIconImage(XWikiConstants.NAV_PAGE_ONLINE_NOT_CACHED_ICON);
        return desc.createImage();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchPart#getTitleToolTip()
     */
    public String getTitleToolTip()
    {
        return "Some tooltip";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchPart#removePropertyListener(org.eclipse.ui.IPropertyListener)
     */
    public void removePropertyListener(IPropertyListener listener)
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        browser.setFocus();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Handles events from the toggle view button in XWikiBrowserPartUI
     * </p>
     * 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
        if (event.widget.equals(xwikiBrowserUi.getToggleViewButton())) {
            if (xwikiBrowserUi.getToggleViewButton().getSelection()) {
                xwikiBrowserUi.getToggleViewButton().setImage(
                    GuiUtils.loadIconImage(XWikiConstants.TOGGLE_BUTTON_ICON).createImage());
                toggleUrl();
            } else {
                xwikiBrowserUi.getToggleViewButton().setImage(
                    GuiUtils.loadIconImage(XWikiConstants.TOGGLE_BUTTON_ICON).createImage());
                printView = true;
                toggleUrl();
            }
        }
    }

    /**
     * Toggles between different views. This is achieved by simply appending or removing the text
     * "?xpage=print&" into/form the browser url.
     */
    private void toggleUrl()
    {
        String url = browser.getUrl();
        if (url.endsWith("?xpage=print&")) {
            url = url.replace("?xpage=print&", "");
        } else {
            url = url + "?xpage=print&";
        }
        browser.setUrl(url);

    }

    /**
     * @return Whether the current view is print view or not
     */
    public boolean isPrintView()
    {
        return printView;
    }

    /**
     * @see org.xwiki.plugins.eclipse.editors.ui.XWikiBrowserPartUI
     * @return The UI component of this editor part
     */
    public XWikiBrowserPartUI getXwikiBrowserUi()
    {
        return xwikiBrowserUi;
    }
}
