package org.xwiki.xeclipse.wizards;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;
import org.xwiki.xeclipse.WorkingSet;
import org.xwiki.xeclipse.WorkingSetFilter;
import org.xwiki.xeclipse.XWikiConnectionManager;
import org.xwiki.xeclipse.XWikiEclipseConstants;
import org.xwiki.xeclipse.model.IXWikiConnection;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.model.IXWikiSpace;
import org.xwiki.xeclipse.views.XWikiExplorerContentProvider;

public class WorkingSetSelection extends WizardPage
{
    private NewWorkingSetWizardState newWorkingSetWizardState;

    private TreeViewer previewTreeViewer;

    private WorkingSetFilter workingSetFilter;

    protected WorkingSetSelection(String pageName)
    {
        super(pageName);
        setTitle("New working set");
        setImageDescriptor(XWikiEclipsePlugin
            .getImageDescriptor(XWikiEclipseConstants.WORKING_SET_BANNER));
    }

    
    
    private void checkPath(TreeItem item, boolean checked, boolean grayed)
    {
        if (item == null) {
            return;
        }

        if (grayed) {
            checked = true;
        } else {
            int index = 0;
            TreeItem[] items = item.getItems();
            while (index < items.length) {
                TreeItem child = items[index];
                if (child.getGrayed() || checked != child.getChecked()) {
                    checked = grayed = true;
                    break;
                }
                index++;
            }
        }

        item.setChecked(checked);
        item.setGrayed(grayed);
        updateWorkingSet(newWorkingSetWizardState.getWorkingSet(), item.getData(), checked);
        checkPath(item.getParentItem(), checked, grayed);
    }

    private void checkItems(TreeItem item, boolean checked)
    {
        item.setGrayed(false);
        item.setChecked(checked);
        TreeItem[] items = item.getItems();
        for (int i = 0; i < items.length; i++) {
            checkItems(items[i], checked);
            updateWorkingSet(newWorkingSetWizardState.getWorkingSet(), items[i].getData(),
                checked);
        }
    }

    public void createControl(Composite parent)
    {
        newWorkingSetWizardState = ((NewWorkingSetWizard) getWizard()).getNewWorkingSetState();
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(composite);

        Group group = new Group(composite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(group);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(group);

        Label label = new Label(group, SWT.NONE);
        label.setText("Working set name:");

        final Text workingSetNameText = new Text(group, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(
            workingSetNameText);
        workingSetNameText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                newWorkingSetWizardState.getWorkingSet().setName(workingSetNameText.getText());
                getContainer().updateButtons();
            }
        });

        SashForm sashForm = new SashForm(composite, SWT.HORIZONTAL);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(sashForm);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(
            sashForm);

        group = new Group(sashForm, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(group);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(group);
        group.setText("Working set content selection");

        final TreeViewer treeViewer = new TreeViewer(group, SWT.CHECK);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(
            treeViewer.getControl());
        treeViewer.setComparator(new ViewerComparator());
        treeViewer.setContentProvider(new XWikiExplorerContentProvider(treeViewer));
        treeViewer.setLabelProvider(new WorkbenchLabelProvider());
        treeViewer.setInput(XWikiConnectionManager.getDefault());
        treeViewer.getTree().addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event event)
            {
                if (event.detail == SWT.CHECK) {
                    TreeItem item = (TreeItem) event.item;
                    boolean checked = item.getChecked();
                    checkItems(item, checked);
                    checkPath(item.getParentItem(), checked, false);

                    updateWorkingSet(newWorkingSetWizardState.getWorkingSet(), item.getData(),
                        checked);

                    if (workingSetFilter != null) {
                        previewTreeViewer.removeFilter(workingSetFilter);
                    }

                    workingSetFilter =
                        new WorkingSetFilter(newWorkingSetWizardState.getWorkingSet());
                    previewTreeViewer.addFilter(workingSetFilter);
                }
            }
        });

        group = new Group(sashForm, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(group);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(group);
        group.setText("Selected working set preview");

        previewTreeViewer = new TreeViewer(group, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(
            previewTreeViewer.getControl());
        previewTreeViewer.setComparator(new ViewerComparator());
        previewTreeViewer.setContentProvider(new XWikiExplorerContentProvider(previewTreeViewer));
        previewTreeViewer.setLabelProvider(new WorkbenchLabelProvider());
        previewTreeViewer.setInput(XWikiConnectionManager.getDefault());

        workingSetFilter = new WorkingSetFilter(newWorkingSetWizardState.getWorkingSet());
        previewTreeViewer.addFilter(workingSetFilter);

        sashForm.setWeights(new int[] {50, 50});

        setControl(composite);
    }

    private void updateWorkingSet(WorkingSet workingSet, Object object, boolean add)
    {
        if (object instanceof IXWikiConnection) {
            IXWikiConnection connection = (IXWikiConnection) object;
            if (add) {
                workingSet.add(connection);
            } else {
                workingSet.remove(connection);
            }
        }

        if (object instanceof IXWikiSpace) {
            IXWikiSpace space = (IXWikiSpace) object;
            if (add) {
                workingSet.add(space);
            } else {
                workingSet.remove(space);
            }
        }

        if (object instanceof IXWikiPage) {
            IXWikiPage page = (IXWikiPage) object;
            if (add) {
                workingSet.add(page);
            } else {
                workingSet.remove(page);
            }
        }

    }

}
