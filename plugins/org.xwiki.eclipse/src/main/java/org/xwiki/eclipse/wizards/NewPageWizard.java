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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.xwiki.eclipse.editors.XWikiPageEditor;
import org.xwiki.eclipse.editors.XWikiPageEditorInput;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.IXWikiSpace;

public class NewPageWizard extends Wizard implements INewWizard
{
    private NewPageWizardState newPageWizardState;

    private IXWikiSpace xwikiSpace;

    private IWorkbenchPage workbenchPage;

    public NewPageWizard(IXWikiSpace xwikiSpace, IWorkbenchPage workbenchPage)
    {
        super();
        newPageWizardState = new NewPageWizardState();
        this.xwikiSpace = xwikiSpace;
        this.workbenchPage = workbenchPage;
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
                        monitor.beginTask("Creating page...", IProgressMonitor.UNKNOWN);
                        final IXWikiPage xwikiPage =
                            xwikiSpace.createPage(newPageWizardState.getTitle(),
                                "Write here content");

                        Display.getDefault().asyncExec(new Runnable()
                        {
                            public void run()
                            {
                                XWikiPageEditorInput editorInput =
                                    new XWikiPageEditorInput(xwikiPage);
                                try {
                                    workbenchPage.openEditor(editorInput, XWikiPageEditor.ID);
                                } catch (PartInitException e) {
                                    e.printStackTrace();
                                }
                            }

                        });

                        monitor.done();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new InvocationTargetException(e);
                    }
                }
            });
        } catch (Exception e) {
            WizardPage currentPage = (WizardPage) getContainer().getCurrentPage();
            currentPage.setErrorMessage("Error creating remote page.");

            return false;
        }

        return true;
    }

    @Override
    public void addPages()
    {
        addPage(new PageSettingsPage("Page settings"));
    }

    public NewPageWizardState getNewPageWizardState()
    {
        return newPageWizardState;
    }

    @Override
    public boolean canFinish()
    {
        if (newPageWizardState.getTitle() == null || newPageWizardState.getTitle().length() == 0) {
            return false;
        }

        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
    }

}
