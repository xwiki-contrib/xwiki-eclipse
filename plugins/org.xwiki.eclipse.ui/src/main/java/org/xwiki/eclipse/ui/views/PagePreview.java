package org.xwiki.eclipse.ui.views;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.part.ViewPart;
import org.xwiki.eclipse.core.model.XWikiEclipseObject;
import org.xwiki.eclipse.core.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.core.model.XWikiEclipsePage;
import org.xwiki.eclipse.core.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.core.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.core.notifications.CoreEvent;
import org.xwiki.eclipse.core.notifications.ICoreEventListener;
import org.xwiki.eclipse.core.notifications.NotificationManager;
import org.xwiki.eclipse.ui.utils.UIUtils;

public class PagePreview extends ViewPart implements ISelectionListener, ICoreEventListener
{
    private Composite mainComposite;

    private Composite browserComposite;

    private StackLayout stackLayout;

    private Browser browser;

    private Composite notConnectedComposite;

    private Composite noPageSelectedComposite;

    private Composite addressBarComposite;

    private Text urlText;

    private class ShowAddressBarAction extends Action
    {
        public ShowAddressBarAction()
        {
            super("Address bar", AS_CHECK_BOX);
        }

        @Override
        public void run()
        {
            if (isChecked()) {
                addressBarComposite.setVisible(true);
                GridData gridData = (GridData) addressBarComposite.getLayoutData();
                gridData.exclude = false;
                addressBarComposite.setLayoutData(gridData);
                mainComposite.layout();
            } else {
                addressBarComposite.setVisible(false);
                GridData gridData = (GridData) addressBarComposite.getLayoutData();
                gridData.exclude = true;
                addressBarComposite.setLayoutData(gridData);
                mainComposite.layout();
            }
        }

    }

    @Override
    public void init(IViewSite site) throws PartInitException
    {
        super.init(site);
        getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(this);
        NotificationManager.getDefault().addListener(
            this,
            new CoreEvent.Type[] {CoreEvent.Type.DATA_MANAGER_CONNECTED, CoreEvent.Type.DATA_MANAGER_DISCONNECTED,
            CoreEvent.Type.DATA_MANAGER_REGISTERED, CoreEvent.Type.DATA_MANAGER_UNREGISTERED,
            CoreEvent.Type.OBJECT_REMOVED, CoreEvent.Type.OBJECT_STORED, CoreEvent.Type.PAGE_REMOVED,
            CoreEvent.Type.PAGE_STORED, CoreEvent.Type.PAGE_SELECTED, CoreEvent.Type.OBJECT_SELECTED,
            CoreEvent.Type.SPACE_REMOVED});
    }

    @Override
    public void dispose()
    {
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
        super.dispose();
    }

    @Override
    public void createPartControl(Composite parent)
    {
        mainComposite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(0, 0).spacing(0, 0).applyTo(mainComposite);

        addressBarComposite = new Composite(mainComposite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).margins(0, 5).spacing(0, 0).applyTo(addressBarComposite);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).exclude(true).applyTo(
            addressBarComposite);

        Label label = new Label(addressBarComposite, SWT.NONE);
        label.setText("URL:");
        label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        urlText = new Text(addressBarComposite, SWT.BORDER);
        urlText.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                browser.setUrl(urlText.getText());

            }

            public void widgetSelected(SelectionEvent e)
            {

            }

        });
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(urlText);
        addressBarComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        addressBarComposite.setVisible(false);
        Button button = new Button(addressBarComposite, SWT.PUSH);
        button.setText("Open in external browser");
        button.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                // TODO Auto-generated method stub

            }

            public void widgetSelected(SelectionEvent e)
            {
                IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();

                IWebBrowser browser;
                try {
                    browser = browserSupport.createBrowser("xeclipse");
                    browser.openURL(new URL(urlText.getText()));
                } catch (Exception e1) {
                    MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", String.format(
                        "Unable to open external browser\n%s", e1));
                }

            }

        });

        browserComposite = new Composite(mainComposite, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(browserComposite);

        stackLayout = new StackLayout();
        browserComposite.setLayout(stackLayout);

        browser = new Browser(browserComposite, SWT.NONE);

        notConnectedComposite = new Composite(browserComposite, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(notConnectedComposite);
        notConnectedComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        label = new Label(notConnectedComposite, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).applyTo(label);
        label.setText("No preview available if not connected.");
        label.setFont(JFaceResources.getHeaderFont());
        label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        label.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

        noPageSelectedComposite = new Composite(browserComposite, SWT.NONE);
        noPageSelectedComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(noPageSelectedComposite);
        label = new Label(noPageSelectedComposite, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).applyTo(label);
        label.setText("No page selected");
        label.setFont(JFaceResources.getHeaderFont());
        label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        label.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

        stackLayout.topControl = noPageSelectedComposite;
        browserComposite.layout();

        IActionBars actionBars = getViewSite().getActionBars();
        IToolBarManager toolBarManager = actionBars.getToolBarManager();
        toolBarManager.add(new ShowAddressBarAction());
    }

    @Override
    public void setFocus()
    {
        // TODO Auto-generated method stub

    }

    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
        Object selectedObject = UIUtils.getFirstSelectedObjectsFromSelection(selection);
        if (selectedObject instanceof XWikiEclipsePageSummary) {
            XWikiEclipsePageSummary pageSummary = (XWikiEclipsePageSummary) selectedObject;

            update(pageSummary.getData().getUrl(), pageSummary.getDataManager().isConnected());
        }

        if (selectedObject instanceof XWikiEclipseObjectSummary) {
            XWikiEclipseObjectSummary objectSummary = (XWikiEclipseObjectSummary) selectedObject;

            update(objectSummary.getPageSummary() != null ? objectSummary.getPageSummary().getUrl() : null,
                objectSummary.getDataManager().isConnected());
        }

    }

    private void update(final String url, final boolean isConnected)
    {
        Display.getDefault().asyncExec(new Runnable()
        {

            public void run()
            {
                if (url != null && !url.equals("")) {
                    if (isConnected) {

                        stackLayout.topControl = browser;
                        if (!browser.isDisposed()) {
                            browser.setUrl(url);
                            urlText.setText(url);
                            browserComposite.layout();
                        }

                    } else {
                        stackLayout.topControl = notConnectedComposite;
                        if (!browser.isDisposed()) {
                            urlText.setText("");
                            browserComposite.layout();
                        }
                    }
                } else {
                    stackLayout.topControl = noPageSelectedComposite;
                    if (!browser.isDisposed()) {
                        urlText.setText("");
                        browserComposite.layout();
                    }
                }

            }
        });
    }

    public void handleCoreEvent(CoreEvent event)
    {
        Object object = event.getData();

        if (object instanceof XWikiEclipsePage) {
            XWikiEclipsePage page = (XWikiEclipsePage) object;

            update(page.getData().getUrl(), page.getDataManager().isConnected());
        }

        else
            
        if (object instanceof XWikiEclipseObject) {
            XWikiEclipseObject xwikiObject = (XWikiEclipseObject) object;

            update(xwikiObject.getPageSummary() != null ? xwikiObject.getPageSummary().getUrl() : null, xwikiObject
                .getDataManager().isConnected());
        }
        
        else
        
        if (object instanceof XWikiEclipseSpaceSummary) {
            XWikiEclipseSpaceSummary xwikiSpaceSummary = (XWikiEclipseSpaceSummary) object;

            update(xwikiSpaceSummary.getData().getUrl(), xwikiSpaceSummary.getDataManager().isConnected());
        }
    }

}
