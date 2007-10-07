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

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.xwiki.plugins.eclipse.model.IXWikiPage;

/**
 * This class represents the Page editor. It has two tabs (pages in eclipse terminology), one for
 * editing the markup (Simple Text Editor) and another for displaying the browser output (which is
 * an actual browser widget). For each page opened for editing, one instance of this class will be
 * created with corresponding IXWikiPage as the input.
 */
public class XWikiEditor extends MultiPageEditorPart implements IResourceChangeListener
{

    /**
     * XWiki Markup Editor (page 0)
     */
    private XWikiMarkupEditorPart xwikiMarkupEditor;

    /**
     * XWiki Wiki page browser (page 1)
     */
    private XWikiBrowserPart xwikiBrowser;

    /**
     * Constructor
     */
    public XWikiEditor()
    {
        super();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    private void createXwikiMarkupEditor()
    {
        try {
            xwikiMarkupEditor = new XWikiMarkupEditorPart();
            int index = addPage(xwikiMarkupEditor, getEditorInput());
            setPartName(xwikiMarkupEditor.getTitle());
            setPageText(index, "Edit");
        } catch (PartInitException e) {
            e.printStackTrace();
        }
    }

    private void createXwikiBrowser()
    {
        try {
            xwikiBrowser = new XWikiBrowserPart();
            int index = addPage(xwikiBrowser, getEditorInput());
            setPageText(index, "view");
        } catch (PartInitException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc} Creates the pages of the multi-page editor.
     * 
     * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
     */
    protected void createPages()
    {
        createXwikiMarkupEditor();
        IXWikiPage page = (IXWikiPage) getEditorInput();
        if (!page.isOffline()) {
            createXwikiBrowser();
        }
    }

    /**
     * The <code>MultiPageEditorPart</code> implementation of this <code>IWorkbenchPart</code>
     * method disposes all nested editors. Subclasses may extend.
     */
    public void dispose()
    {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        super.dispose();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Saves the multi-page editor's document.
     * </p>
     * 
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor)
    {
        getEditor(0).doSave(monitor);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Saves the multi-page editor's document as another file. Also updates the text for page 0's
     * tab, and updates this multi-page editor's input to correspond to the nested editor's.
     * </p>
     * 
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    public void doSaveAs()
    {
        getEditor(0).doSaveAs();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.MultiPageEditorPart#init(org.eclipse.ui.IEditorSite,
     *      org.eclipse.ui.IEditorInput)
     */
    public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException
    {
        super.init(site, editorInput);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed()
    {
        // TODO for now...
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
     */
    protected void pageChange(int newPageIndex)
    {
        super.pageChange(newPageIndex);
        // TODO Refresh the browser...
    }

    /**
     * {@inheritDoc}
     * </p>
     * Closes all project files on project close.
     * </p>
     * 
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(final IResourceChangeEvent event)
    {
        // TODO leaving out for now..
    }
}
