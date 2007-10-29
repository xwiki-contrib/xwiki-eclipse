package org.xwiki.xeclipse.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.XWikiConnectionFactory;

public class NewConnectionWizard extends Wizard implements INewWizard
{
    private NewConnectionWizardState newConnectionWizardState;

    public NewConnectionWizard()
    {
        super();
        newConnectionWizardState = new NewConnectionWizardState();
        setNeedsProgressMonitor(true);
    }

    @Override
    public boolean performFinish()
    {
        try {
            getContainer().run(true, false, new IRunnableWithProgress()
            {
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                    InterruptedException
                {
                    try {
                        monitor.beginTask("Validating connection...", IProgressMonitor.UNKNOWN);
                        IXWikiConnection connection =
                            XWikiConnectionFactory.createPlainConnection(newConnectionWizardState
                                .getServerUrl(), newConnectionWizardState.getUserName());
                        connection.connect(newConnectionWizardState.getPassword());
                        connection.disconnect();
                        connection.dispose();
                        monitor.done();
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    }
                }
            });
        } catch (Exception e) {
            WizardPage currentPage = (WizardPage) getContainer().getCurrentPage();
            currentPage
                .setErrorMessage("Error connecting to remote XWiki. Please check out your settings.");
            
            return false;
        }

        return true;
    }

    @Override
    public void addPages()
    {
        addPage(new ConnectionSettingsPage("Connection settings"));
    }

    public NewConnectionWizardState getNewConnectionWizardState()
    {
        return newConnectionWizardState;
    }

    @Override
    public boolean canFinish()
    {
        if(newConnectionWizardState.getServerUrl() == null || !newConnectionWizardState.getServerUrl().startsWith("http://")) {
            return false;
        }
        
        if(newConnectionWizardState.getUserName() == null || newConnectionWizardState.getUserName().length() == 0) {
            return false;
        }
        
        if(newConnectionWizardState.getPassword() == null || newConnectionWizardState.getPassword().length() == 0) {
            return false;
        }
            
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
    }

}
