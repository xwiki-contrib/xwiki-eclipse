package org.xwiki.eclipse.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class XWikiContentOutlinePage extends ContentOutlinePage implements IDocumentListener
{
    private IDocument document;

    private static class HeadingInfo {
        private int offset;
        private String title;

        public HeadingInfo(int offset, String title) {
            this.offset = offset;
            this.title = title;
        }

        public int getOffset()
        {
            return offset;
        }

        public String getTitle()
        {
            return title;
        }
    }
    
    private static class HeadingInfoLabelProvider extends LabelProvider {

        @Override
        public String getText(Object element)
        {
            if(element instanceof HeadingInfo) {
                HeadingInfo headingInfo = (HeadingInfo) element;
                return headingInfo.getTitle();
            }
            
            return super.getText(element);
        }
        
    }
    
    private static class HeadingInfoContentProvider implements ITreeContentProvider
    {
        private String[] headingsStart = {"1 ", "1.1 ", "1.1.1 ", "1.1.1.1 ", "1.1.1.1.1 ", "1.1.1.1.1.1 " };
        private Object[] NO_OBJECTS = new Object[0];

        public Object[] getChildren(Object parentElement)
        {
            return NO_OBJECTS;
        }

        public Object getParent(Object element)
        {
            return null;
        }

        public boolean hasChildren(Object element)
        {         
            return false;
        }

        public Object[] getElements(Object inputElement)
        {
            if(!(inputElement instanceof IDocument)) {
                return NO_OBJECTS;
            }
            
            IDocument document = (IDocument) inputElement;
            List<HeadingInfo> headings = new ArrayList<HeadingInfo>();
            
            for(int i = 0; i < document.getNumberOfLines(); i++) {
                try {
                    int start = document.getLineOffset(i);
                    int length = document.getLineLength(i);
                    String line = document.get(start, length);
                    if(line.endsWith("\n")) {
                        line = line.substring(0, line.length() - 1);
                    }
                    
                    if(isHeading(line)) {
                        headings.add(new HeadingInfo(start, line));
                    }
                } catch (BadLocationException e) {
                    /* Should never happen */
                    e.printStackTrace();
                }
                
            }
            
            return headings.toArray();
        }
        
        private boolean isHeading(String line) {
            for(int i = 0; i < headingsStart.length; i++) {
                if(line.startsWith(headingsStart[i])) {
                    return true;
                }
            }
            
            return false;
        }

        public void dispose()
        {
            // TODO Auto-generated method stub

        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
            // TODO Auto-generated method stub

        }

    }

    public XWikiContentOutlinePage(IDocument document)
    {
        this.document = document;
    }

    @Override
    public void createControl(Composite parent)
    {

        super.createControl(parent);

        TreeViewer viewer = getTreeViewer();
        viewer.setContentProvider(new HeadingInfoContentProvider());
        viewer.setLabelProvider(new HeadingInfoLabelProvider());
        viewer.addSelectionChangedListener(this);
        viewer.setInput(document);  
        document.addDocumentListener(this);
    }

    public void documentAboutToBeChanged(DocumentEvent event)
    {
        // TODO Auto-generated method stub
        
    }

    public void documentChanged(DocumentEvent event)
    {
        getTreeViewer().refresh();        
    }

    @Override
    public void dispose()
    {
        document.removeDocumentListener(this);
        super.dispose();
    }
}
