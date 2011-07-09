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
 */
package org.xwiki.eclipse.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;

/**
 * @version $Id$
 */
public class DownloadAttachmentWizard extends Wizard implements INewWizard
{

    private Set selectedObjects;

    private final List<Button> selectedButtons = new ArrayList<Button>();

    private DownloadAttachmentState state;

    /**
     * @param selectedObjects
     */
    public DownloadAttachmentWizard(Set selectedObjects)
    {
        super();
        this.selectedObjects = selectedObjects;
        setNeedsProgressMonitor(true);
        state = new DownloadAttachmentState();
        for (Object selectedObject : selectedObjects) {
            if (selectedObject instanceof XWikiEclipseAttachment) {
                XWikiEclipseAttachment attachment = (XWikiEclipseAttachment) selectedObject;
                state.getNameAttachmentMap().put(attachment.getName(), attachment);
            }

        }
    }

    @Override
    public void addPages()
    {
        addPage(new AttachmentSelectionPage("Select Attachments to be downloaded"));
    }

    @Override
    public boolean canFinish()
    {
        return super.canFinish();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish()
    {
        Set<String> names = state.getNameAttachmentMap().keySet();
        final Collection<XWikiEclipseAttachment> attachments = state.getNameAttachmentMap().values();
        final String dir = state.getDirPath();

        Job downloadJob = new Job(String.format("Downloading %s", Arrays.toString(names.toArray())))
        {

            @Override
            protected IStatus run(IProgressMonitor monitor)
            {

                monitor.beginTask("Downloading", 100);
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }
                int work = 100 / attachments.size();

                for (XWikiEclipseAttachment attachment : attachments) {
                    DataManager dataManager = attachment.getDataManager();
                    monitor.setTaskName("Downloading " + attachment.getName());
                    dataManager.download(dir, attachment);
                    monitor.worked(work);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                monitor.done();
                return Status.OK_STATUS;
            }
        };
        downloadJob.setUser(true);
        downloadJob.schedule();

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
        // TODO Auto-generated method stub

    }

    class AttachmentSelectionPage extends WizardPage
    {
        /**
         * @param pageName
         */
        protected AttachmentSelectionPage(String pageName)
        {
            super(pageName);
            setTitle("Download Attachments");
            setImageDescriptor(UIPlugin.getImageDescriptor(UIConstants.CONNECTION_SETTINGS_BANNER));
            setPageComplete(false);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
         */
        @Override
        public void createControl(Composite parent)
        {
            Composite composite = new Composite(parent, SWT.None);
            GridLayoutFactory.fillDefaults().applyTo(composite);

            Group attachmentGroup = new Group(composite, SWT.NONE);
            GridLayoutFactory.fillDefaults().numColumns(1).applyTo(attachmentGroup);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(attachmentGroup);
            attachmentGroup.setText("Selected Attachments");

            for (String name : state.getNameAttachmentMap().keySet()) {
                Button checkbox = new Button(attachmentGroup, SWT.CHECK);
                checkbox.addSelectionListener(new SelectionListener()
                {

                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        getContainer().updateButtons();
                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent e)
                    {
                        // TODO Auto-generated method stub

                    }
                });
                checkbox.setText(name);
                checkbox.setSelection(true);
                selectedButtons.add(checkbox);
            }

            final Group directoryGroup = new Group(composite, SWT.NONE);
            GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(directoryGroup);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(directoryGroup);
            directoryGroup.setText("Choose folder to save the selected attachments");

            Label label = new Label(directoryGroup, SWT.NONE);
            label.setText("Directory Path: ");

            final Text dirPath = new Text(directoryGroup, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(dirPath);

            Button browseButton = new Button(directoryGroup, SWT.PUSH);
            browseButton.setText("Browse...");
            browseButton.addSelectionListener(new SelectionListener()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    DirectoryDialog dirDialog = new DirectoryDialog(directoryGroup.getShell());

                    /* Set the initial filter path according to user directory */
                    dirDialog.setFilterPath(System.getProperty("user.dir"));

                    /* Change the title bar text */
                    dirDialog.setText("Download the attachments");

                    /* Customizable message displayed in the dialog */
                    dirDialog.setMessage("Select a directory");

                    /*
                     * Calling open() will open and run the dialog. It will return the selected directory, or null if
                     * user cancels
                     */
                    /* construct file name = dir + attachment name */
                    String dir = dirDialog.open();
                    if (dir != null) {
                        dirPath.setText(dir);
                        state.setDirPath(dir);
                        getContainer().updateButtons();
                    }

                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e)
                {
                    // TODO Auto-generated method stub
                }
            });

            setControl(composite);

        }

        @Override
        public boolean isPageComplete()
        {
            if (state.getDirPath() == null) {
                setErrorMessage("A folder to save the attachments must be specified");
                return false;
            }

            int selected = 0;
            for (Button check : selectedButtons) {
                if (check.getSelection()) {
                    selected++;
                }
            }

            if (selected == 0) {
                setErrorMessage("At lease one attachment must be selected");
                return false;
            }

            setErrorMessage(null);
            return true;
        }
    }

    class DownloadAttachmentState
    {
        private String dirPath;

        private Map<String, XWikiEclipseAttachment> nameAttachmentMap = new HashMap<String, XWikiEclipseAttachment>();

        public String getDirPath()
        {
            return dirPath;
        }

        public void setDirPath(String dirPath)
        {
            this.dirPath = dirPath;
        }

        public Map<String, XWikiEclipseAttachment> getNameAttachmentMap()
        {
            return nameAttachmentMap;
        }

        public void setNameAttachmentMap(Map<String, XWikiEclipseAttachment> nameAttachmentMap)
        {
            this.nameAttachmentMap = nameAttachmentMap;
        }
    }
}
