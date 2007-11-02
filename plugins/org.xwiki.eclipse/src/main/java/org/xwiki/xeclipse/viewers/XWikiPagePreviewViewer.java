package org.xwiki.xeclipse.viewers;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.xwiki.xeclipse.model.IXWikiPage;

public class XWikiPagePreviewViewer
{
    private Composite composite;

    private StackLayout stackLayout;

    private Browser browser;

    private Composite notConnectedComposite;

    private Composite noPageSelectedComposite;

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
}
