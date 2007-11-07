package org.xwiki.xeclipse.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;
import org.xwiki.xeclipse.WorkingSet;
import org.xwiki.xeclipse.WorkingSetManager;
import org.xwiki.xeclipse.XWikiEclipseConstants;
import org.xwiki.xeclipse.wizards.NewWorkingSetWizard;

public class ManageWorkingSetsDialog extends Dialog
{
    public ManageWorkingSetsDialog(Shell parentShell)
    {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);

        shell.setText("Manage working sets");
        shell.setSize(600, 300);

    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(
            composite);
        GridLayoutFactory.fillDefaults().margins(10, 10).numColumns(2).applyTo(composite);

        final TableViewer tableViewer = new TableViewer(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(
            tableViewer.getControl());
        tableViewer.setContentProvider(new IStructuredContentProvider()
        {

            public void dispose()
            {
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
            {
            }

            public Object[] getElements(Object inputElement)
            {
                return WorkingSetManager.getDefault().getWorkingSets().toArray();
            }
        });

        tableViewer.setLabelProvider(new LabelProvider()
        {

            @Override
            public String getText(Object element)
            {
                if (element instanceof WorkingSet) {
                    WorkingSet workingSet = (WorkingSet) element;

                    return workingSet.getName();
                }

                return super.getText(element);
            }

            @Override
            public Image getImage(Object element)
            {
                if (element instanceof WorkingSet) {
                    return XWikiEclipsePlugin.getImageDescriptor(
                        XWikiEclipseConstants.WORKING_SET_ICON).createImage();
                }

                return super.getImage(element);
            }

        });
        tableViewer.setInput(WorkingSetManager.getDefault().getWorkingSets());

        Composite buttonBar = new Composite(composite, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(buttonBar);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(false, true).applyTo(
            buttonBar);

        Button button = new Button(buttonBar, SWT.PUSH);
        button.setText("New...");
        button.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent event)
            {
                try {
                    NewWorkingSetWizard wizard = new NewWorkingSetWizard();

                    WizardDialog dialog = new WizardDialog(getShell(), wizard);
                    dialog.create();
                    dialog.open();
                    tableViewer.refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

        button = new Button(buttonBar, SWT.PUSH);
        button.setText("Remove");
        button.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                // TODO Auto-generated method stub

            }

            public void widgetSelected(SelectionEvent e)
            {
                IStructuredSelection selection =
                    (IStructuredSelection) tableViewer.getSelection();
                if (!selection.isEmpty()) {
                    Object selectedObject = selection.getFirstElement();
                    if (selectedObject instanceof WorkingSet) {
                        WorkingSet workingSet = (WorkingSet) selectedObject;

                        MessageBox messageBox =
                            new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
                        messageBox.setMessage(String.format(
                            "Do you really want to remove the working set '%s'?", workingSet
                                .getName()));
                        int result = messageBox.open();
                        if (result == SWT.YES) {
                            WorkingSetManager.getDefault().remove(workingSet);
                            tableViewer.refresh();
                        }
                    }
                }

            }

        });

        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        Button button =
            createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
        button.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                // TODO Auto-generated method stub

            }

            public void widgetSelected(SelectionEvent e)
            {
                close();
            }

        });

    }

}
