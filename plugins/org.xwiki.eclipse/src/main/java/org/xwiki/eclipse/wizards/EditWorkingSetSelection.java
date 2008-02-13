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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.xwiki.eclipse.WorkingSet;
import org.xwiki.eclipse.WorkingSetFilter;
import org.xwiki.eclipse.XWikiConnectionManager;
import org.xwiki.eclipse.XWikiEclipseConstants;
import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.IXWikiSpace;
import org.xwiki.eclipse.views.XWikiExplorerContentProvider;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;


public class EditWorkingSetSelection extends WizardPage
{
	private WorkingSet workingSet;
	private TreeViewer treeViewer;
	private TreeViewer previewTreeViewer;
	private WorkingSetFilter workingSetFilter;
	
	class TV extends TreeViewer {
		private WorkingSet workingSet;

		public TV(Composite parent, int style, WorkingSet workingset)
		{
			super(parent, style);
			this.workingSet = workingset;
		}
				
		@Override
		public void add(Object parentElementOrTreePath, Object[] childElements)
		{
			TreeItem item;
			
			super.add(parentElementOrTreePath, childElements);
			
			for(Object c : childElements) {
				item = (TreeItem) findItem(c);
				setChecked(item, c);
			}								
		}
		
		@Override
		protected Item newItem(Widget parent, int flags, int ix)
		{
			if(parent instanceof TreeItem) {
				setChecked((TreeItem)parent, parent.getData());
			}
			return super.newItem(parent, flags, ix);
		}
		
		private void setChecked(TreeItem item, Object o) {
			if(o instanceof IXWikiSpace) {
				IXWikiSpace xwikiSpace = (IXWikiSpace) o;
				item.setChecked(workingSet.contains(xwikiSpace));								
			}
			else if(o instanceof IXWikiPage) {
				IXWikiPage xwikiPage = (IXWikiPage) o;
				item.setChecked(workingSet.contains(xwikiPage));
			}
			else if(o instanceof IXWikiConnection) {
				IXWikiConnection xwikiConnection = (IXWikiConnection) o;
				item.setChecked(workingSet.contains(xwikiConnection));
			}
		}
		
		
	}


	protected EditWorkingSetSelection(String pageName, WorkingSet workingSet)
	{
		super(pageName);
		setTitle(String.format("Edit working set %s", workingSet.getName()));
		setImageDescriptor(XWikiEclipsePlugin
				.getImageDescriptor(XWikiEclipseConstants.WORKING_SET_BANNER));

		this.workingSet = workingSet;
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
        updateWorkingSet(workingSet, item.getData(), checked);
        checkPath(item.getParentItem(), checked, grayed);
    }

    private void checkItems(TreeItem item, boolean checked)
    {
        item.setGrayed(false);
        item.setChecked(checked);
        TreeItem[] items = item.getItems();
        for (int i = 0; i < items.length; i++) {
            checkItems(items[i], checked);
            updateWorkingSet(workingSet, items[i].getData(),
                checked);
        }
    }

	public void createControl(Composite parent) {		
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
                workingSet.setName(workingSetNameText.getText());
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

        final TV treeViewer = new TV(group, SWT.CHECK, workingSet);
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

                    updateWorkingSet(workingSet, item.getData(),
                        checked);
                    
                    workingSetFilter =
                        new WorkingSetFilter(workingSet);
                    previewTreeViewer.setFilters(new ViewerFilter[] {workingSetFilter});
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

        workingSetFilter = new WorkingSetFilter(workingSet);
        previewTreeViewer.addFilter(workingSetFilter);

        sashForm.setWeights(new int[] {50, 50});

        label = new Label(composite, SWT.BORDER | SWT.WRAP);
        label
            .setText("To select all pages in a space, first expand the space node and then click on the checkbox next to it.");
        label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));

        setControl(composite);

	}
	
	public void createControl2(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(composite);

		Composite labelComposite = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(
				labelComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				false).applyTo(labelComposite);

		Label label = new Label(labelComposite, SWT.NONE);
		label.setText("Working set name:");

		final Text workingSetNameText = new Text(labelComposite, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				false).applyTo(workingSetNameText);
		workingSetNameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				workingSet.setName(workingSetNameText.getText());
				getContainer().updateButtons();
			}
		});

		/** ****************************************************************** */

		Composite mainComposite = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).margins(5, 5).applyTo(
				mainComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				true).applyTo(mainComposite);

		Group group = new Group(mainComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(group);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				true).applyTo(group);
		group.setText("Working set content selection");

		treeViewer = new TreeViewer(group, SWT.MULTI);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				true).applyTo(treeViewer.getControl());
		treeViewer.setComparator(new ViewerComparator());
		treeViewer.setContentProvider(new XWikiExplorerContentProvider(
				treeViewer));
		treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		treeViewer.setInput(XWikiConnectionManager.getDefault());
		

		Composite buttonComposite = new Composite(mainComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(buttonComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(false,
				true).applyTo(buttonComposite);

		Button button = new Button(buttonComposite, SWT.PUSH);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				false).applyTo(button);
		button.setText("Add ->");
		button.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub
			}

			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection selection = (IStructuredSelection) treeViewer
						.getSelection();
				if (!selection.isEmpty())
				{
					for (Object element : selection.toArray())
					{
						if (element instanceof IXWikiPage)
						{
							IXWikiPage page = (IXWikiPage) element;							
							workingSet.add(page);
						}
					}
				}
			}
		});

		button = new Button(buttonComposite, SWT.PUSH);
		button.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub
			}

			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection selection = (IStructuredSelection) treeViewer
						.getSelection();
				if (!selection.isEmpty())
				{
					for (Object element : selection.toArray())
					{
						System.out.format("Removing %s\n", element);
					}
				}
			}
		});

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				false).applyTo(button);
		button.setText("<- Remove");

		group = new Group(mainComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(group);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				true).applyTo(group);
		group.setText("Working set preview");

		previewTreeViewer = new TreeViewer(group, SWT.MULTI);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				true).applyTo(previewTreeViewer.getControl());
		previewTreeViewer.setComparator(new ViewerComparator());
		previewTreeViewer.setContentProvider(new XWikiExplorerContentProvider(
				previewTreeViewer));
		previewTreeViewer.setLabelProvider(new WorkbenchLabelProvider());
		previewTreeViewer.setInput(XWikiConnectionManager.getDefault());

		
		
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
