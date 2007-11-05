package org.xwiki.xeclipse.rcp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.views.IViewDescriptor;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{

    private IContributionItem views;

    private List<Action> showViewActions;

    class ShowViewAction extends Action
    {
        public final String ID = "org.xwiki.xeclipse.actions.ShowView";

        private IWorkbenchWindow window;

        private IViewDescriptor viewDescriptor;

        public ShowViewAction(IWorkbenchWindow window, IViewDescriptor viewDescriptor)
        {
            this.window = window;
            this.viewDescriptor = viewDescriptor;

            setId(ID);
            setText(viewDescriptor.getLabel());
            setImageDescriptor(viewDescriptor.getImageDescriptor());
        }

        @Override
        public void run()
        {
            try {
                window.getActivePage().showView(viewDescriptor.getId());
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }

    }

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
    {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window)
    {
        IWorkbenchAction saveAction = ActionFactory.SAVE.create(window);
        register(saveAction);

        IWorkbenchAction cutAction = ActionFactory.CUT.create(window);
        register(cutAction);

        IWorkbenchAction copyAction = ActionFactory.COPY.create(window);
        register(copyAction);

        IWorkbenchAction pasteAction = ActionFactory.PASTE.create(window);
        register(pasteAction);

        IWorkbenchAction exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);

        IWorkbenchAction showViewMenuAction = ActionFactory.SHOW_VIEW_MENU.create(window);
        register(showViewMenuAction);

        showViewActions = new ArrayList<Action>();

        for (IViewDescriptor v : window.getWorkbench().getViewRegistry().getViews()) {
            String[] categoryPath = v.getCategoryPath();

            boolean addView = false;
            if (categoryPath != null) {
                for (String category : categoryPath) {
                    if (category.equalsIgnoreCase("XWIKI")) {
                        addView = true;
                        break;
                    }
                }
            }

            if (addView) {
                ShowViewAction action = new ShowViewAction(window, v);
                showViewActions.add(action);
                register(action);
            }
        }

    }

    protected void fillMenuBar(IMenuManager menuBar)
    {
        MenuManager fileMenu = new MenuManager("File", "org.xwiki.xeclipse.menu.File");
        menuBar.add(fileMenu);

        MenuManager editMenu = new MenuManager("Edit", "org.xwiki.xeclipse.menu.Edit");
        menuBar.add(editMenu);

        MenuManager windowMenu = new MenuManager("Window", "org.xwiki.xeclipse.menu.Window");
        MenuManager showViewMenu =
            new MenuManager("Show view", "org.xwiki.xeclipse.menu.ShowView");
        for (Action action : showViewActions) {
            showViewMenu.add(action);
        }
        windowMenu.add(showViewMenu);
        menuBar.add(windowMenu);

        MenuManager helpMenu = new MenuManager("Help", "org.xwiki.xeclipse.menu.Help");
        menuBar.add(helpMenu);

    }

}
