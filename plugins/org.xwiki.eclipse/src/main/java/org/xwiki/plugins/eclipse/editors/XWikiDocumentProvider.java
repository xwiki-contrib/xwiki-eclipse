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

import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.StorageDocumentProvider;
import org.xwiki.plugins.eclipse.model.IXWikiPage;
import org.xwiki.plugins.eclipse.model.wrappers.XWikiPageWrapper;

/**
 * This class handles the mapping between model's document {@link IXWikiPage} and the visual
 * representation of that document within the eclipse editor.
 */
public class XWikiDocumentProvider extends StorageDocumentProvider
{

    /**
     * {@inheritDoc}
     * </p>
     * Creates an IDocument (which is displayed by TextViewer) using an IXWikiPage.
     * </p>
     * 
     * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createDocument(java.lang.Object)
     */
    protected IDocument createDocument(Object element) throws CoreException
    {
        XWikiDocument doc = new XWikiDocument();
        if (element instanceof IXWikiPage) {
            IXWikiPage wikiPage = new XWikiPageWrapper((IXWikiPage) element);
            doc.set(wikiPage.getContent());
        }
        return doc;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is where we get to save the IXWikiPage on server.
     * </p>
     * 
     * @see org.eclipse.ui.editors.text.StorageDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor,
     *      java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
     */
    protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document,
        boolean overwrite) throws CoreException
    {
        IXWikiPage wikiPage = new XWikiPageWrapper((IXWikiPage) element);
        String oldContent = wikiPage.getContent();
        wikiPage.setContent(document.get());
        try {
            wikiPage.save();
        } catch (SwizzleConfluenceException e) {
            wikiPage.setContent(oldContent);
            // TODO might want to throw a Core Exception here.
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Can be used to deny editing if proper permissions are not set
     * </p>
     * 
     * @see org.eclipse.ui.editors.text.StorageDocumentProvider#isModifiable(java.lang.Object)
     */
    public boolean isModifiable(Object element)
    {
        return true;
    }

    /**
     * Internal Document representation. Nothig but a wrapper for text data.
     */
    private class XWikiDocument extends Document
    {
        public XWikiDocument()
        {
            super();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * validates if the document has been modified. If so, returns true. This method is also used to
     * draw the simple asterix(*) mark on the text editor to indicate that the document has been
     * changed.
     * </p>
     * 
     * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#canSaveDocument(java.lang.Object)
     */
    public boolean canSaveDocument(Object element)
    {
        IDocument doc = getDocument(element);
        IXWikiPage wikiPage = (IXWikiPage) element;
        if (!wikiPage.getContent().equals(doc.get())) {
            return true;
        } else {
            return false;
        }
    }

}
