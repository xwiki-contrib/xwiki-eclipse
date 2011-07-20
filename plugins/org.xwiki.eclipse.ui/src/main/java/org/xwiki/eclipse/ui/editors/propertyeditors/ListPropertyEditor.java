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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ListPropertyEditor extends BasePropertyEditor
{
    private String allowedValues;

    private Map<String, String> valueTextMap;

    private Tree tree;

    private final static String listSeparator = "\\|";

    private boolean multiSelect;

    private boolean unmodifiable;

    public ListPropertyEditor(FormToolkit toolkit, Composite parent, XWikiEclipseObjectProperty property)
    {
        super(toolkit, parent, property);
    }

    @Override
    public Composite createControl(Composite parent)
    {
        /* multiSelect */
        String multiSelectAttributeString = property.getAttribute("multiSelect").toString();
        if (multiSelectAttributeString == null || multiSelectAttributeString.equals("0")) {
            multiSelect = false;
        } else {
            multiSelect = true;
        }

        String unModifiableAttributeString = property.getAttribute("unmodifiable");
        if (unModifiableAttributeString == null || unModifiableAttributeString.equals("0")) {
            unmodifiable = true;
        } else {
            unmodifiable = false;
        }

        Section section = toolkit.createSection(parent, Section.TITLE_BAR | Section.EXPANDED);
        section.setText(property.getPrettyName() + " " + (unmodifiable ? "[unmodifiable]" : "") + " "
            + (multiSelect ? "[multiSelect]" : ""));

        Composite composite = toolkit.createComposite(section, SWT.NONE);
        GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 10).applyTo(composite);

        tree = toolkit.createTree(composite, SWT.BORDER | SWT.V_SCROLL | (multiSelect ? SWT.MULTI : SWT.NONE));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(tree);
        toolkit.paintBordersFor(tree);

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

        populateTree();

        section.setClient(composite);

        return section;
    }

    /**
     * populate the tree based on the values or allowedValues attribute of this property
     */
    private void populateTree()
    {
        /* values attribute, separated by "|", e.g., value1=text for value1|value2=text for value2 */
        String valuesAttributeString = property.getAttribute("values");

        if (valueTextMap == null) {
            valueTextMap = new HashMap<String, String>();
        }

        if (valuesAttributeString != null && valuesAttributeString.length() > 0) {
            String[] v = valuesAttributeString.split("\\|");
            for (int i = 0; i < v.length; i++) {
                /* parse value and text pair, if no text is specified, use the value */
                String[] vv = v[i].split("=");
                if (vv.length > 1) {
                    TreeItem item = new TreeItem(tree, SWT.NONE);
                    item.setText(vv[1]);
                    /* set the value in the data field of tree item */
                    item.setData(vv[0]);

                    valueTextMap.put(vv[0], vv[1]);
                } else {
                    TreeItem item = new TreeItem(tree, SWT.NONE);
                    item.setText(vv[0]);
                    item.setData(vv[0]);

                    valueTextMap.put(vv[0], vv[0]);
                }
            }
        } else {
            /* if values attribute is empty, then search for allowedValues, separated by "," */
            this.allowedValues = property.getAttribute("allowedValues");
            if (allowedValues != null && allowedValues.length() > 0) {
                String[] values = this.allowedValues.split(",");
                for (String s : values) {

                    TreeItem item = new TreeItem(tree, SWT.NONE);
                    item.setText(s);
                    item.setData(s);

                    valueTextMap.put(s, s);
                }
            }

        }

        /* always add a guest user for user list */
        if (property.getType().equals("com.xpn.xwiki.objects.classes.UsersClass")) {
            TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setText("XWiki.XWikiGuest");
            item.setData("XWiki.XWikiGuest");
        }

    }

    @Override
    public void setValue(Object value)
    {
        if (value instanceof String) {
            String[] v = null;
            if (property.getType().equals("com.xpn.xwiki.objects.classes.UsersClass")
                || property.getType().equals("com.xpn.xwiki.objects.classes.GroupsClass")) {
                /* separated by "," */
                v = ((String) value).split(",");
            } else {
                /* separated by "|" */
                v = ((String) value).split("\\|");
            }

            if (v.length > 1) {
                TreeItem[] items = tree.getItems();
                List<TreeItem> selectedItems = new ArrayList<TreeItem>();
                for (TreeItem item : items) {
                    for (int i = 0; i < v.length; i++) {
                        /* compare the value instead of text */
                        if (item.getData().equals(v[i])) {
                            selectedItems.add(item);
                            break;
                        }
                    }
                }

                TreeItem[] selected = new TreeItem[selectedItems.size()];
                selectedItems.toArray(selected);
                tree.setSelection(selected);
            } else if (v.length == 1) {
                TreeItem[] items = tree.getItems();
                for (TreeItem item : items) {
                    if (item.getData().equals(v[0])) {
                        tree.setSelection(item);
                        break;
                    }
                }
            }
        }
    }
}
