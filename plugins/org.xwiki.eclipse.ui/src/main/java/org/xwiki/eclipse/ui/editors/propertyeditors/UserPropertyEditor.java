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
package org.xwiki.eclipse.ui.editors.propertyeditors;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.xwiki.eclipse.model.XWikiEclipseObjectProperty;

/**
 * @version $Id$
 */
public class UserPropertyEditor extends BasePropertyEditor
{
    String allowedValues;

    private Tree tree;

    private boolean multiSelect;

    public UserPropertyEditor(FormToolkit toolkit, Composite parent, XWikiEclipseObjectProperty property)
    {
        super(toolkit, parent, property);
    }

    @Override
    public Composite createControl(Composite parent)
    {
        Section section = toolkit.createSection(parent, Section.TITLE_BAR | Section.EXPANDED);
        section.setText(property.getPrettyName());

        Composite composite = toolkit.createComposite(section, SWT.NONE);
        GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 10).applyTo(composite);

        String multiSelectAttributeString = property.getAttribute("multiSelect").toString();
        if (multiSelectAttributeString == null || multiSelectAttributeString.equals("0")) {
            multiSelect = false;
        } else {
            multiSelect = true;
        }

        tree = toolkit.createTree(composite, SWT.BORDER | SWT.V_SCROLL | (multiSelect ? SWT.MULTI : SWT.NONE));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(tree);
        toolkit.paintBordersFor(tree);

        this.allowedValues = property.getAttribute("allowedValues");
        String[] values = this.allowedValues.split(",");
        for (String s : values) {
            TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setText(s);
        }

        tree.addSelectionListener(new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                TreeItem[] items = tree.getSelection();

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < items.length; i++) {
                    TreeItem item = items[i];
                    if (i == items.length - 1) {
                        sb.append(item.getText());
                    } else {
                        sb.append(item.getText() + ",");
                    }
                }
                property.setValue(sb.toString());
                firePropertyModifyListener();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                // TODO Auto-generated method stub

            }
        });

        section.setClient(composite);

        return section;
    }

    @Override
    public void setValue(Object value)
    {
        if (value instanceof String) {
            TreeItem[] items = tree.getItems();
            for (TreeItem item : items) {
                if (item.getText().equals((String) value)) {
                    tree.setSelection(item);
                    break;
                }
            }
        }
    }

}
