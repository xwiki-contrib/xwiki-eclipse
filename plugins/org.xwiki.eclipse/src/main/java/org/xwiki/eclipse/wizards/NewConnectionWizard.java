/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
package org.xwiki.eclipse.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.xwiki.eclipse.WorkingSet;
import org.xwiki.eclipse.WorkingSetManager;
import org.xwiki.eclipse.XWikiConnectionManager;
import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.XWikiConnectionFactory;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;

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
                        e.printStackTrace();
                        throw new InvocationTargetException(e);
                    }
                }
            });
        } catch (Exception e) {
            WizardPage currentPage = (WizardPage) getContainer().getCurrentPage();
            currentPage
                .setErrorMessage("Error connecting to remote XWiki. Please check your settings.");

            return false;
        }

        try {
            IXWikiConnection connection =
                XWikiConnectionFactory
                    .createCachedConnection(newConnectionWizardState.getServerUrl(),
                        newConnectionWizardState.getUserName(), new File(XWikiEclipsePlugin
                            .getDefault().getStateLocation().toFile(), "cache"));
            XWikiConnectionManager.getDefault().addConnection(connection,
                newConnectionWizardState.getPassword());
            
            WorkingSet currentWorkingSet = WorkingSetManager.getDefault().getActiveWorkingSet();
            if(currentWorkingSet != null) {
                currentWorkingSet.add(connection);
            }
            
            connection.connect(newConnectionWizardState.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
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
        if (newConnectionWizardState.getServerUrl() == null
            || !(newConnectionWizardState.getServerUrl().startsWith("http://")
            || newConnectionWizardState.getServerUrl().startsWith("https://"))) {
            return false;
        }

        if (newConnectionWizardState.getUserName() == null
            || newConnectionWizardState.getUserName().length() == 0) {
            return false;
        }

        if (newConnectionWizardState.getPassword() == null
            || newConnectionWizardState.getPassword().length() == 0) {
            return false;
        }

        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
    }

}
