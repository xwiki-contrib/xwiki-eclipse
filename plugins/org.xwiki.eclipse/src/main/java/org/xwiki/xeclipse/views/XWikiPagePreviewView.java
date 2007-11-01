package org.xwiki.xeclipse.views;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.utils.XWikiEclipseUtil;

public class XWikiPagePreviewView extends ViewPart implements ISelectionListener
{
    public static final String ID = "org.xwiki.xeclipse.views.XWikiPagePreview";
    private Composite composite;
    private StackLayout stackLayout;
    private Browser browser;
    private Composite notConnectedComposite;
    private Composite noPageSelectedComposite;
    
    @Override
    public void createPartControl(Composite parent)
    {
        composite = new Composite(parent, SWT.NONE);
        stackLayout = new StackLayout();
        composite.setLayout(stackLayout);
        
        browser = new Browser(composite, SWT.NONE);
        
        notConnectedComposite = new Composite(composite, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(notConnectedComposite);
        notConnectedComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));        
        Label label = new Label(notConnectedComposite, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).applyTo(label);        
        label.setText("No preview available if not connected.");        
        label.setFont(JFaceResources.getHeaderFont());
        label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        label.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
        
        noPageSelectedComposite = new Composite(composite, SWT.NONE);
        noPageSelectedComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        GridLayoutFactory.fillDefaults().margins(0,0).applyTo(noPageSelectedComposite);
        label = new Label(noPageSelectedComposite, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).applyTo(label);
        label.setText("No page selected");        
        label.setFont(JFaceResources.getHeaderFont());
        label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        label.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

        stackLayout.topControl = noPageSelectedComposite;
        composite.layout();
        
        getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(this);        
    }

    @Override
    public void setFocus()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void dispose()
    {
        getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(this);
        super.dispose();
    }

    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {        
        Object selectedObject = XWikiEclipseUtil.getSingleSelectedObjectInStructuredSelection(selection);
        if(selectedObject instanceof IXWikiPage) {
            IXWikiPage xwikiPage = (IXWikiPage) selectedObject;
            
            if(xwikiPage.getConnection().isConnected()) {
                browser.setUrl(xwikiPage.getUrl());
                stackLayout.topControl = browser;
                composite.layout();
            }
            else {
                stackLayout.topControl = notConnectedComposite;
                composite.layout();
            }
        }
        else {
            stackLayout.topControl = noPageSelectedComposite;
            composite.layout();
        }
    }



}
