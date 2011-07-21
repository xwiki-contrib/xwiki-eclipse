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
package org.xwiki.eclipse.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.ui.editors.ObjectEditor;
import org.xwiki.eclipse.ui.editors.ObjectEditorInput;
import org.xwiki.eclipse.ui.utils.XWikiEclipseSafeRunnable;

/**
 * @version $Id$
 */
public class NewObjectWizard extends Wizard implements INewWizard
{
    private NewObjectWizardState newObjectWizardState;

    private DataManager dataManager;

    private XWikiEclipsePageSummary pageSummary;

    public NewObjectWizard(XWikiEclipsePageSummary pageSummary)
    {
        super();
        this.pageSummary = pageSummary;
        newObjectWizardState = new NewObjectWizardState();

        newObjectWizardState.setPageId(pageSummary.getId());
        newObjectWizardState.setSpace(pageSummary.getSpace());
        newObjectWizardState.setWiki(pageSummary.getWiki());
        this.dataManager = pageSummary.getDataManager();
        setNeedsProgressMonitor(true);
    }

    @Override
    public boolean performFinish()
    {
        try {
            getContainer().run(true, false, new IRunnableWithProgress()
            {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
                {
                    try {
                        monitor.beginTask("Creating object...", IProgressMonitor.UNKNOWN);
                        // final XWikiEclipseObject object =
                        // dataManager.createObject(newObjectWizardState.getPageId(),
                        // newObjectWizardState.getClassName());

                        final XWikiEclipseObject object = new XWikiEclipseObject(dataManager);
                        object.setClassName(newObjectWizardState.getClassName());
                        object.setName(String.format("%s[NEW]", newObjectWizardState.getClassName()));
                        object.setPageId(newObjectWizardState.getPageId());
                        object.setSpace(newObjectWizardState.getSpace());
                        object.setWiki(newObjectWizardState.getWiki());
                        XWikiEclipseClass clazz =
                            dataManager.getClass(newObjectWizardState.getWiki(), newObjectWizardState.getClassName());
                        object.setProperties(clazz.getProperties());

                        Display.getDefault().syncExec(new Runnable()
                        {
                            public void run()
                            {
                                SafeRunner.run(new XWikiEclipseSafeRunnable()
                                {
                                    public void run() throws Exception
                                    {
                                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                                            .openEditor(new ObjectEditorInput(object), ObjectEditor.ID);
                                    }
                                });
                            }
                        });

                        monitor.done();
                    } catch (Exception e) {
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
        addPage(new ObjectSettingsPage("Object settings", pageSummary));
    }

    public NewObjectWizardState getNewObjectWizardState()
    {
        return newObjectWizardState;
    }

    @Override
    public boolean canFinish()
    {
        if (!super.canFinish()) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
    }
}
