package org.xwiki.eclipse.ui.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;
import org.xwiki.eclipse.ui.NavigatorContentProvider;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.actions.DeleteActionProvider;
import org.xwiki.eclipse.ui.actions.OpenXWikiModelObjectAction;
import org.xwiki.eclipse.ui.actions.PropertyActionProvider;
import org.xwiki.eclipse.ui.actions.RefreshActionProvider;
import org.xwiki.eclipse.ui.actions.WorkingSetActionProvider;
import org.xwiki.eclipse.ui.actions.XWikiEclipseAttachmentActionProvider;
import org.xwiki.eclipse.ui.actions.XWikiEclipseClassActionProvider;
import org.xwiki.eclipse.ui.actions.XWikiEclipseObjectSummaryActionProvider;
import org.xwiki.eclipse.ui.actions.XWikiEclipsePageSummaryActionProvider;
import org.xwiki.eclipse.ui.actions.XWikiEclipseSpaceSummaryActionProvider;
import org.xwiki.eclipse.ui.actions.XWikiEclipseWikiSummaryActionProvider;

public class XWikiNavigator extends ViewPart
{
    private TreeViewer navigatorTreeViewer;

    private OpenXWikiModelObjectAction openAction;

    private PropertyActionProvider propertyActionProvider;

    private RefreshActionProvider refreshActionProvider;

    private XWikiEclipsePageSummaryActionProvider pageSummaryActionProvider;

    private XWikiEclipseSpaceSummaryActionProvider spaceSummaryActionProvider;

    private XWikiEclipseWikiSummaryActionProvider wikiSummaryActionProvider;

    private XWikiEclipseObjectSummaryActionProvider objectSummaryActionProvider;

    private XWikiEclipseClassActionProvider classActionProvider;

    private XWikiEclipseAttachmentActionProvider attachmentActionProvider;

    private DeleteActionProvider deleteActionProvider;

    private WorkingSetActionProvider workingSetActionProvider;

    @Override
    public void init(IViewSite site) throws PartInitException
    {
        super.init(site);

    }

    @Override
    public void createPartControl(Composite parent)
    {
        navigatorTreeViewer = new TreeViewer(parent, SWT.NONE);
        NavigatorContentProvider navigatorContentProvider = new NavigatorContentProvider();
        navigatorTreeViewer.setContentProvider(navigatorContentProvider);
        navigatorTreeViewer.setLabelProvider(new WorkbenchLabelProvider());
        navigatorTreeViewer.addDoubleClickListener(new IDoubleClickListener()
        {
            @Override
            public void doubleClick(DoubleClickEvent event)
            {
                openAction.run();
            }
        });

        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                XWikiNavigator.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(navigatorTreeViewer.getControl());
        navigatorTreeViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, navigatorTreeViewer);

        openAction = new OpenXWikiModelObjectAction(navigatorTreeViewer);
        getSite().setSelectionProvider(navigatorTreeViewer);

        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        navigatorTreeViewer.setInput(projects[0]);

        propertyActionProvider = new PropertyActionProvider(navigatorTreeViewer);
        refreshActionProvider = new RefreshActionProvider();
        pageSummaryActionProvider = new XWikiEclipsePageSummaryActionProvider(navigatorTreeViewer);
        spaceSummaryActionProvider = new XWikiEclipseSpaceSummaryActionProvider();
        wikiSummaryActionProvider = new XWikiEclipseWikiSummaryActionProvider();
        objectSummaryActionProvider = new XWikiEclipseObjectSummaryActionProvider(navigatorTreeViewer);
        classActionProvider = new XWikiEclipseClassActionProvider(navigatorTreeViewer);
        attachmentActionProvider = new XWikiEclipseAttachmentActionProvider();
        deleteActionProvider = new DeleteActionProvider();
        
        workingSetActionProvider = new WorkingSetActionProvider(navigatorTreeViewer);
        
        IActionBars actionBars = getViewSite().getActionBars();
        workingSetActionProvider.fillActionBars(actionBars);
    }

    protected void fillContextMenu(IMenuManager manager)
    {
        IStructuredSelection selection = (IStructuredSelection) navigatorTreeViewer.getSelection();

        if (selection.isEmpty()) {
            return;
        }

        Object selectedElement = selection.getFirstElement();

        if (selectedElement instanceof XWikiEclipseWikiSummary) {
            wikiSummaryActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            refreshActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            propertyActionProvider.fillContextMenu(manager);
        } else if (selectedElement instanceof XWikiEclipseSpaceSummary) {
            spaceSummaryActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            refreshActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            propertyActionProvider.fillContextMenu(manager);
        } else if (selectedElement instanceof XWikiEclipsePageSummary) {
            pageSummaryActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            deleteActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            refreshActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            propertyActionProvider.fillContextMenu(manager);
        } else if (selectedElement instanceof XWikiEclipseObjectSummary) {
            objectSummaryActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            deleteActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            propertyActionProvider.fillContextMenu(manager);
        } else if (selectedElement instanceof XWikiEclipseComment) {
            objectSummaryActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            deleteActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            propertyActionProvider.fillContextMenu(manager);
        } else if (selectedElement instanceof XWikiEclipseClass) {
            classActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            propertyActionProvider.fillContextMenu(manager);
        } else if (selectedElement instanceof XWikiEclipseAttachment) {
            attachmentActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            deleteActionProvider.fillContextMenu(manager);
            manager.add(new Separator());
            propertyActionProvider.fillContextMenu(manager);
        }

    }

    @Override
    public void setFocus()
    {
        // TODO Auto-generated method stub

    }

}
