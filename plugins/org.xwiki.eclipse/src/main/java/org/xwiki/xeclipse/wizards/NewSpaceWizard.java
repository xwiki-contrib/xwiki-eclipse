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

public class NewSpaceWizard extends Wizard implements INewWizard
{    
    private NewSpaceWizardState newSpaceWizardState;
    private IXWikiConnection xwikiConnection;

    public NewSpaceWizard(IXWikiConnection xwikiConnection)
    {
        super();
        newSpaceWizardState = new NewSpaceWizardState();
        this.xwikiConnection = xwikiConnection;
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
                        monitor.beginTask("Creating space...", IProgressMonitor.UNKNOWN);
                        String spaceKey = newSpaceWizardState.getName().trim().replace(' ', '_');
                        xwikiConnection.createSpace(spaceKey, newSpaceWizardState.getName(), newSpaceWizardState.getDescription());                        
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
                .setErrorMessage("Error creating remote space.");
            
            return false;
        }
                
        return true;
    }

    @Override
    public void addPages()
    {
        addPage(new SpaceSettingsPage("Space settings"));
    }

    public NewSpaceWizardState getNewSpaceWizardState()
    {
        return newSpaceWizardState;
    }

    @Override
    public boolean canFinish()
    {                
        if(newSpaceWizardState.getName() == null || newSpaceWizardState.getName().length() == 0) {
            return false;
        }
        
        if(newSpaceWizardState.getDescription() == null || newSpaceWizardState.getDescription().length() == 0) {
            return false;
        }
            
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
    }

}
