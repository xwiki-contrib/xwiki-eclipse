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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.xwiki.eclipse.model.XWikiEclipseAttachment;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.storage.notification.CoreEvent;
import org.xwiki.eclipse.storage.notification.NotificationManager;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;
import org.xwiki.eclipse.ui.utils.XWikiEclipseSafeRunnable;

/**
 * @version $Id$
 */
public class XWikiEclipseAttachmentWizard extends Wizard implements INewWizard
{

    private Set selectedObjects;

    private final List<Button> selectedButtons = new ArrayList<Button>();

    private DownloadAttachmentState downloadState;

    private XWikiEclipsePageSummary pageSummary;

    private Command command;

    private List<String> filesToBeUploaded = new ArrayList<String>();

    /**
     * @param selectedObjects
     */
    public XWikiEclipseAttachmentWizard(Set selectedObjects, Command command)
    {
        super();
        this.command = command;
        this.selectedObjects = selectedObjects;
        setNeedsProgressMonitor(true);
        if (command.getId().equals(UIConstants.DOWNLOAD_ATTACHMENT_COMMAND)) {
            downloadState = new DownloadAttachmentState();
            for (Object selectedObject : selectedObjects) {
                if (selectedObject instanceof XWikiEclipseAttachment) {
                    XWikiEclipseAttachment attachment = (XWikiEclipseAttachment) selectedObject;
                    downloadState.getNameAttachmentMap().put(attachment.getName(), attachment);
                }
            }
        }

        if (command.getId().equals(UIConstants.UPLOAD_ATTACHMENT_COMMAND)) {
            /* already make sure that only one page is selected */
            pageSummary = (XWikiEclipsePageSummary) selectedObjects.iterator().next();
        }
    }

    @Override
    public void addPages()
    {
        if (command.getId().equals(UIConstants.DOWNLOAD_ATTACHMENT_COMMAND)) {
            addPage(new DownloadAttachmentPage("Select Attachments to be downloaded"));
        }

        if (command.getId().equals(UIConstants.UPLOAD_ATTACHMENT_COMMAND)) {
            addPage(new UploadAttachmentPage("Upload Attachments to a page"));
        }

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
        if (command.getId().equals(UIConstants.DOWNLOAD_ATTACHMENT_COMMAND)) {
            Set<String> names = downloadState.getNameAttachmentMap().keySet();
            final Collection<XWikiEclipseAttachment> attachments = downloadState.getNameAttachmentMap().values();
            final String dir = downloadState.getDirPath();

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

        if (command.getId().equals(UIConstants.UPLOAD_ATTACHMENT_COMMAND)) {
            File[] files = new File[filesToBeUploaded.size()];
            final String[] fileNames = new String[filesToBeUploaded.size()];
            for (int i = 0; i < files.length; i++) {
                files[i] = new File(filesToBeUploaded.get(i));
                fileNames[i] = files[i].getName();
            }

            Job uploadJob = new Job(String.format("Uploading %s", Arrays.toString(fileNames)))
            {

                @Override
                protected IStatus run(IProgressMonitor monitor)
                {

                    monitor.beginTask("Uploading", 100);
                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }

                    int work = 100 / filesToBeUploaded.size();

                    for (int i = 0; i < filesToBeUploaded.size(); i++) {

                        final int idx = i;
                        final DataManager dataManager = pageSummary.getDataManager();
                        monitor.setTaskName("Uploading Attachment: " + fileNames[i]);
                        SafeRunner.run(new XWikiEclipseSafeRunnable()
                        {

                            @Override
                            public void run() throws Exception
                            {
                                dataManager.uploadAttachment(pageSummary, filesToBeUploaded.get(idx));
                            }
                        });

                        monitor.worked(work);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                    monitor.done();
                    NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.ATTACHMENT_UPLOADED, this,
                        pageSummary);

                    return Status.OK_STATUS;
                }
            };
            uploadJob.setUser(true);
            uploadJob.schedule();

            return true;
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
        // TODO Auto-generated method stub

    }

    class UploadAttachmentPage extends WizardPage
    {

        private Group pageSummaryGroup;

        private Group attachmentGroup;

        private ScrolledComposite sc;

        private Composite addAttachmentComposite;

        /**
         * @param pageName
         */
        protected UploadAttachmentPage(String pageName)
        {
            super(pageName);
            setTitle("Upload Attachments");
            setImageDescriptor(UIPlugin.getImageDescriptor(UIConstants.CONNECTION_SETTINGS_BANNER));
            setPageComplete(false);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
         */
        @Override
        public void createControl(final Composite parent)
        {
            final Composite composite = new Composite(parent, SWT.NONE);
            GridLayoutFactory.fillDefaults().applyTo(composite);

            pageSummaryGroup = new Group(composite, SWT.NONE);
            pageSummaryGroup.setText("Page Summary Information");
            GridDataFactory.fillDefaults().grab(true, false).applyTo(pageSummaryGroup);
            GridLayoutFactory.fillDefaults().numColumns(2).applyTo(pageSummaryGroup);

            Label property = new Label(pageSummaryGroup, SWT.NONE);
            property.setText("Name:");
            Label value = new Label(pageSummaryGroup, SWT.NONE);
            value.setText(pageSummary.getName() == null ? "" : pageSummary.getName());

            property = new Label(pageSummaryGroup, SWT.NONE);
            property.setText("Wiki:");
            value = new Label(pageSummaryGroup, SWT.NONE);
            value.setText(pageSummary.getWiki() == null ? "" : pageSummary.getWiki());

            property = new Label(pageSummaryGroup, SWT.NONE);
            property.setText("Space:");
            value = new Label(pageSummaryGroup, SWT.NONE);
            value.setText(pageSummary.getSpace() == null ? "" : pageSummary.getSpace());

            attachmentGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
            attachmentGroup.setText("Attachment to be uploaded");

            Point size = attachmentGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 3 * size.y).applyTo(attachmentGroup);

            GridLayoutFactory.fillDefaults().applyTo(attachmentGroup);

            sc = new ScrolledComposite(attachmentGroup, SWT.V_SCROLL);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(sc);
            GridLayoutFactory.fillDefaults().applyTo(sc);

            addAttachmentComposite = new Composite(sc, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(addAttachmentComposite);
            GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(addAttachmentComposite);

            sc.setContent(addAttachmentComposite);
            sc.setMinSize(addAttachmentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            sc.setExpandVertical(true);
            sc.setExpandHorizontal(true);
            sc.setShowFocusedControl(true);

            /* button to add an attachment */
            Button addAttachmentButton = new Button(addAttachmentComposite, SWT.PUSH);
            addAttachmentButton.setText("add another attachment");
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false).span(2, 1)
                .applyTo(addAttachmentButton);

            addAttachmentComposite.setSize(addAttachmentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

            attachmentGroup.layout();

            addAttachmentButton.addSelectionListener(new SelectionListener()
            {

                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    Button browseButton = new Button(addAttachmentComposite, SWT.PUSH);
                    GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false)
                        .applyTo(browseButton);
                    browseButton.setText("Browse...");

                    final Text file = new Text(addAttachmentComposite, SWT.BORDER | SWT.SINGLE);
                    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(file);

                    browseButton.addSelectionListener(new SelectionListener()
                    {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                            FileDialog fileDialog = new FileDialog(attachmentGroup.getShell());

                            /* Set the initial filter path according to user directory */
                            fileDialog.setFilterPath(System.getProperty("user.dir"));

                            /* Change the title bar text */
                            fileDialog.setText("Select a file to be uploaded");

                            /*
                             * Calling open() will open and run the dialog. It will return the selected file, or null if
                             * user cancels
                             */
                            String filePath = fileDialog.open();
                            if (filePath != null) {
                                file.setText(filePath);
                                getContainer().updateButtons();
                            }

                        }

                        @Override
                        public void widgetDefaultSelected(SelectionEvent e)
                        {
                            // TODO Auto-generated method stub
                        }
                    });

                    /* refresh the layout */
                    addAttachmentComposite.setSize(addAttachmentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                    sc.setMinSize(addAttachmentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                    attachmentGroup.layout();

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
            filesToBeUploaded.clear();

            Control[] constrols = addAttachmentComposite.getChildren();
            for (Control control : constrols) {
                if (control instanceof Text) {
                    String path = ((Text) control).getText();
                    if (path.length() > 0 && new File(path).exists()) {
                        filesToBeUploaded.add(path);
                    }

                }
            }

            if (filesToBeUploaded.size() > 0) {
                return true;
            }

            return false;
        }
    }

    class DownloadAttachmentPage extends WizardPage
    {
        /**
         * @param pageName
         */
        protected DownloadAttachmentPage(String pageName)
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

            for (String name : downloadState.getNameAttachmentMap().keySet()) {
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
                        downloadState.setDirPath(dir);
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
            if (downloadState.getDirPath() == null) {
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
