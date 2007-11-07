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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.xwiki.xeclipse.WorkingSetManager;

public class NewWorkingSetWizard extends Wizard implements INewWizard
{
    private NewWorkingSetWizardState newWorkingSetWizardState;
    
    public NewWorkingSetWizard()
    {
        super();
        newWorkingSetWizardState = new NewWorkingSetWizardState();
    }

    
    
    @Override
    public boolean performFinish()
    {
        WorkingSetManager.getDefault().add(newWorkingSetWizardState.getWorkingSet());
        
        return true;
    }

    @Override
    public void addPages()
    {
        addPage(new WorkingSetSelection("Working set selection"));
    }

    @Override
    public boolean canFinish()
    {
        if(newWorkingSetWizardState.getWorkingSet().getName() == null || newWorkingSetWizardState.getWorkingSet().getName().length() == 0) {
            return false;
        }
        
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
    }

    public NewWorkingSetWizardState getNewWorkingSetState()
    {
        return newWorkingSetWizardState;
    }

}
