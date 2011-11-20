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
package org.xwiki.eclipse.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xwiki.eclipse.core.CoreLog;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.model.XWikiEclipseWikiSummary;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.eclipse.ui.utils.XWikiEclipseSafeRunnableWithResult;

/**
 * @version $Id$
 */
public class RenamePageDialog extends TitleAreaDialog
{
    private XWikiEclipsePageSummary pageSummary;

    private String action;

    private String newWiki;

    private String newSpace;

    private String newPageName;

    public RenamePageDialog(Shell parentShell, XWikiEclipsePageSummary pageSummary, String action)
    {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
        this.pageSummary = pageSummary;
        this.action = action;
    }

    protected void configureShell(Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText(action.equals("copyFrom") ? "Copy Page" : "Rename page");
    }

    @Override
    protected Point getInitialSize()
    {
        // TODO Auto-generated method stub
        return new Point(800, 600);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        Button OKButton =
            createButton(parent, IDialogConstants.OK_ID, action.equals("copyFrom") ? "Copy" : "Rename", true);
        OKButton.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                // Do nothing.
            }

            public void widgetSelected(SelectionEvent e)
            {
            }
        });

        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Control contents = super.createContents(parent);

        setTitle(action.equals("copyFrom") ? "Copy Page" : "Rename page");

        return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite mainComposite = (Composite) super.createDialogArea(parent);
        GridLayoutFactory.fillDefaults().applyTo(mainComposite);

        Composite composite = new Composite(mainComposite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(composite);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);

        Label label = new Label(composite, SWT.NONE);
        label.setText("Wiki:");

        final Combo combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
        List<XWikiEclipseWikiSummary> wikis = new ArrayList<XWikiEclipseWikiSummary>();
        try {
            wikis = pageSummary.getDataManager().getWikis();
        } catch (XWikiEclipseStorageException e) {
            CoreLog.logError(String.format("Error getting wikis in rename page dialog"), e);
        }        
        final String[] items = new String[wikis.size()];
        for (int i = 0; i < items.length; i++) {
            String wiki = wikis.get(i).getName();
            items[i] = wiki;
            if (wiki.equals(pageSummary.getWiki())) {
                combo.select(i);
            }
        }
        combo.setItems(items);

        for (int i = 0; i < items.length; i++) {
            String wiki = wikis.get(i).getName();
            if (wiki.equals(pageSummary.getWiki())) {
                combo.select(i);
                newWiki = pageSummary.getWiki();
            }
        }

        combo.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                newWiki = items[((Combo) e.getSource()).getSelectionIndex()];
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                // TODO Auto-generated method stub
            }
        });

        label = new Label(composite, SWT.NONE);
        label.setText("New space:");

        final ComboViewer comboViewer = new ComboViewer(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(comboViewer.getControl());
        comboViewer.setContentProvider(new IStructuredContentProvider()
        {
            public Object[] getElements(Object inputElement)
            {
                XWikiEclipseSafeRunnableWithResult<List<XWikiEclipseSpaceSummary>> runnable =
                    new XWikiEclipseSafeRunnableWithResult<List<XWikiEclipseSpaceSummary>>()
                    {

                        public void run() throws Exception
                        {
                            setResult(pageSummary.getDataManager().getSpaces(pageSummary.getWiki()));
                        }

                    };
                SafeRunner.run(runnable);

                if (runnable.getResult() != null) {
                    String[] elements = new String[runnable.getResult().size()];
                    int i = 0;
                    for (XWikiEclipseSpaceSummary spaceSummary : runnable.getResult()) {
                        elements[i] = spaceSummary.getName();
                        i++;
                    }

                    return elements;
                }

                return new Object[0];
            }

            public void dispose()
            {
                // TODO Auto-generated method stub

            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
            {
                // TODO Auto-generated method stub
            }
        });
        comboViewer.setLabelProvider(new LabelProvider());
        comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (selection != null) {
                    newSpace = (String) selection.getFirstElement();
                }
            }
        });
        comboViewer.getCombo().addModifyListener(new ModifyListener()
        {

            public void modifyText(ModifyEvent e)
            {
                newSpace = comboViewer.getCombo().getText();
            }

        });

        comboViewer.setInput(new Object());
        comboViewer.setSelection(new StructuredSelection(pageSummary.getSpace()));

        label = new Label(composite, SWT.NONE);
        label.setText("New name:");

        final Text name = new Text(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(name);

        if (pageSummary.getId().indexOf('.') != -1) {
            String[] components = pageSummary.getId().split("\\.");

            name.setText(components[1]);
        } else {
            name.setText(pageSummary.getTitle());
        }

        name.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                newPageName = name.getText();
            }

        });

        return composite;
    }

    public String getNewSpace()
    {
        return newSpace;
    }

    public String getNewPageName()
    {
        return newPageName;
    }

    public String getNewWiki()
    {
        return newWiki;
    }

    public String getAction()
    {
        return action;
    }
}
