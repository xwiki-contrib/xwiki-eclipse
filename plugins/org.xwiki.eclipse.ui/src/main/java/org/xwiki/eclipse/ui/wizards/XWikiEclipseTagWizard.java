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
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.xwiki.eclipse.core.notification.CoreEvent;
import org.xwiki.eclipse.core.notification.NotificationManager;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseTag;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;
import org.xwiki.eclipse.ui.utils.XWikiEclipseSafeRunnable;

/**
 * @version $Id$
 */
public class XWikiEclipseTagWizard extends Wizard implements INewWizard
{
    private XWikiEclipsePageSummary pageSummary;

    private Command command;

    private String[] tagsProposals = null;

    private List<String> tagsToAdd = new ArrayList<String>();

    /**
     * @param selectedObjects
     * @param command
     */
    public XWikiEclipseTagWizard(Set selectedObjects, Command command)
    {
        super();
        setNeedsProgressMonitor(true);
        this.command = command;

        /* already make sure that the selected is pageSummary and there is only one */
        this.pageSummary = (XWikiEclipsePageSummary) selectedObjects.iterator().next();

        /* fetch all the possible tags in this wiki, populate the proposals */
        List<XWikiEclipseTag> tagsInWiki = new ArrayList<XWikiEclipseTag>();
        try {
            tagsInWiki = pageSummary.getDataManager().getAllTagsInWiki(pageSummary.getWiki());
        } catch (XWikiEclipseStorageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (tagsProposals == null) {
            tagsProposals = new String[tagsInWiki.size()];

            for (int i = 0; i < tagsProposals.length; i++) {
                tagsProposals[i] = tagsInWiki.get(i).getName();
            }
        }
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

    @Override
    public void addPages()
    {
        if (command.getId().equals(UIConstants.NEW_TAG_COMMAND)) {
            addPage(new AddTagsPage("Add tags to page"));
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
        if (command.getId().equals(UIConstants.NEW_TAG_COMMAND)) {

            Job addTagsJob = new Job(String.format("Adding Tags %s", Arrays.toString(tagsToAdd.toArray())))
            {

                @Override
                protected IStatus run(IProgressMonitor monitor)
                {

                    monitor.beginTask("Adding", tagsToAdd.size());
                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }

                    for (int i = 0; i < tagsToAdd.size(); i++) {
                        final String tagStr = tagsToAdd.get(i);
                        final int idx = i;
                        final DataManager dataManager = pageSummary.getDataManager();
                        monitor.setTaskName("Adding Tag: " + tagStr);
                        SafeRunner.run(new XWikiEclipseSafeRunnable()
                        {

                            @Override
                            public void run() throws Exception
                            {
                                XWikiEclipseTag tag =
                                    dataManager.addTag(pageSummary.getWiki(), pageSummary.getSpace(),
                                        pageSummary.getName(), tagStr);

                            }
                        });

                        monitor.worked(1);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                    monitor.done();
                    NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.TAG_STORED, this, pageSummary);

                    return Status.OK_STATUS;
                }
            };
            addTagsJob.setUser(true);
            addTagsJob.schedule();

            return true;
        }

        return true;
    }

    class AddTagsPage extends WizardPage
    {

        private Group tagGroup;

        private ScrolledComposite sc;

        private Composite addTagComposite;

        /**
         * @param pageName
         */
        protected AddTagsPage(String pageName)
        {
            super(pageName);
            setTitle(pageName);
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
            final Composite composite = new Composite(parent, SWT.NONE);
            GridLayoutFactory.fillDefaults().applyTo(composite);

            tagGroup = new Group(composite, SWT.NONE);
            tagGroup.setText("Add Tags");

            GridDataFactory.fillDefaults().grab(true, true).applyTo(tagGroup);
            GridLayoutFactory.fillDefaults().applyTo(tagGroup);

            sc = new ScrolledComposite(tagGroup, SWT.V_SCROLL);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(sc);
            GridLayoutFactory.fillDefaults().applyTo(sc);

            addTagComposite = new Composite(sc, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(addTagComposite);
            GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(addTagComposite);

            sc.setContent(addTagComposite);
            sc.setMinSize(addTagComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            sc.setExpandVertical(true);
            sc.setExpandHorizontal(true);
            sc.setShowFocusedControl(true);

            /* button to add an attachment */
            Button addTagButton = new Button(addTagComposite, SWT.PUSH);
            addTagButton.setText("add another tag");
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false).span(2, 1)
                .applyTo(addTagButton);

            addTagComposite.setSize(addTagComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

            addTagButton.addSelectionListener(new SelectionListener()
            {

                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    Label label = new Label(addTagComposite, SWT.NONE);
                    label.setText("tag name: ");

                    final Text tag = new Text(addTagComposite, SWT.BORDER | SWT.SINGLE);
                    new AutoCompleteField(tag, new TextContentAdapter(), tagsProposals);
                    tag.addModifyListener(new ModifyListener()
                    {

                        @Override
                        public void modifyText(ModifyEvent e)
                        {
                            getContainer().updateButtons();
                        }
                    });

                    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(tag);

                    /* refresh the layout */
                    addTagComposite.setSize(addTagComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                    sc.setMinSize(addTagComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                    tagGroup.layout();
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e)
                {
                    // TODO Auto-generated method stub

                }
            });

            /* refresh the layout */
            addTagComposite.setSize(addTagComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            sc.setMinSize(addTagComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            tagGroup.layout();

            setControl(composite);
        }

        @Override
        public boolean isPageComplete()
        {
            tagsToAdd.clear();

            if (addTagComposite == null)
                return false;

            Control[] controls = addTagComposite.getChildren();
            if (controls.length == 1) {
                // only contains the initial button
                return false;
            }

            for (Control control : controls) {
                if (control instanceof Text) {
                    String tag = ((Text) control).getText();
                    if (tag == null || tag.trim().length() == 0) {
                        return false;
                    } else {
                        tagsToAdd.add(tag.trim());
                    }
                }
            }

            return true;
        }

    }
}
