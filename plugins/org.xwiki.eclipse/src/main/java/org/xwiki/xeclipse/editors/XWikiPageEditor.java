package org.xwiki.xeclipse.editors;

import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class XWikiPageEditor extends AbstractTextEditor
{
    public static final String ID = "org.xwiki.xeclipse.editors.XWikiPage";    

    public XWikiPageEditor()
    {
        super();        
        setDocumentProvider(new XWikiPageDocumentProvider());
        setSourceViewerConfiguration(new SourceViewerConfiguration());
    }
}
