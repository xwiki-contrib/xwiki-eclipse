package org.xwiki.xeclipse.rcp;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
        
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
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
        
        IContributionItem item = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
        
    }

    protected void fillMenuBar(IMenuManager menuBar) {          
    }
    
}
