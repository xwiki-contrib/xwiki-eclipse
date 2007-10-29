package org.xwiki.xeclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.xeclipse.wizards.NewConnectionWizard;

public class NewConnectionHandler extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        try {
            NewConnectionWizard wizard = new NewConnectionWizard();

            WizardDialog dialog =
                new WizardDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), wizard);
            dialog.create();
            dialog.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
