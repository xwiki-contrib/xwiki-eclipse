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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.xwiki.eclipse.WorkingSet;

public class EditWorkingSetWizard extends Wizard implements INewWizard
{	
	private WorkingSet workingSet;

	public EditWorkingSetWizard(WorkingSet workingSet)
	{
		super();		
		this.workingSet = workingSet;
	}

	@Override
	public boolean performFinish()
	{
		return true;
	}

	@Override
	public void addPages()
	{
		addPage(new EditWorkingSetSelection("Working set selection", workingSet));
	}

	@Override
	public boolean canFinish()
	{
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
	}	
}
