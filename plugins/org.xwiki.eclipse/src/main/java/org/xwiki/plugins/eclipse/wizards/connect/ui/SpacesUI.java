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

package org.xwiki.plugins.eclipse.wizards.connect.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.xwiki.plugins.eclipse.wizards.connect.ConnectWizard;

/**
 * UI of the import spaces page of {@link ConnectWizard}.
 */
public class SpacesUI extends Composite
{

    private Group selectedListGroup;

    private List availableSpacesList;

    private Group availableListGroup;

    private Button removeAllButton;

    private List selectedSpacesList;

    private Button addAllButton;

    private Button removeButton;

    private Button addButton;

    /**
     * Main method for launching the the GUI alone.
     */
    public static void main(String[] args)
    {
        showGUI();
    }

    /**
     * Test method for creating and displaying the GUI.
     */
    public static void showGUI()
    {
        Display display = Display.getDefault();
        Shell shell = new Shell(display);
        SpacesUI inst = new SpacesUI(shell, SWT.NULL);
        Point size = inst.getSize();
        shell.setLayout(new FillLayout());
        shell.layout();
        if (size.x == 0 && size.y == 0) {
            inst.pack();
            shell.pack();
        } else {
            Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
            shell.setSize(shellBounds.width, shellBounds.height);
        }
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

    public SpacesUI(org.eclipse.swt.widgets.Composite parent, int style)
    {
        super(parent, style);
        initGUI();
    }

    private void initGUI()
    {
        try {
            this.setLayout(null);
            this.setSize(574, 294);
            createAddButton();
            createRemoveButton();
            createAddAllButton();
            createRemoveAllButton();
            createAvailableListGroup();
            createAvailableSpacesList();
            createSelectedListGroup();
            createSelectedSpacesList();
            this.layout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Button getAddAllButton()
    {
        return addAllButton;
    }

    public Button getAddButton()
    {
        return addButton;
    }

    public List getAvailableSpacesList()
    {
        return availableSpacesList;
    }

    public Button getRemoveAllButton()
    {
        return removeAllButton;
    }

    public Button getRemoveButton()
    {
        return removeButton;
    }

    public List getSelectedSpacesList()
    {
        return selectedSpacesList;
    }

    private void createSelectedListGroup()
    {
        selectedListGroup = new Group(this, SWT.NONE);
        selectedListGroup.setLayout(null);
        selectedListGroup.setText("Selected");
        selectedListGroup.setBounds(322, 14, 245, 273);
    }

    private void createAvailableSpacesList()
    {
        availableSpacesList = new List(availableListGroup, SWT.V_SCROLL | SWT.BORDER);
        availableSpacesList.setBounds(14, 21, 210, 238);
    }

    private void createAvailableListGroup()
    {
        availableListGroup = new Group(this, SWT.NONE);
        availableListGroup.setLayout(null);
        availableListGroup.setText("Available");
        availableListGroup.setBounds(7, 14, 238, 273);
    }

    private void createRemoveAllButton()
    {
        removeAllButton = new Button(this, SWT.PUSH | SWT.CENTER);
        removeAllButton.setText("<<");
        removeAllButton.setBounds(251, 199, 63, 28);
    }

    private void createSelectedSpacesList()
    {
        selectedSpacesList = new List(selectedListGroup, SWT.V_SCROLL | SWT.BORDER);
        selectedSpacesList.setBounds(14, 21, 217, 238);
    }

    private void createAddAllButton()
    {
        addAllButton = new Button(this, SWT.PUSH | SWT.CENTER);
        addAllButton.setText(">>");
        addAllButton.setBounds(251, 73, 63, 28);
    }

    private void createRemoveButton()
    {
        removeButton = new Button(this, SWT.PUSH | SWT.CENTER);
        removeButton.setText("<");
        removeButton.setBounds(251, 157, 63, 28);
    }

    private void createAddButton()
    {
        addButton = new Button(this, SWT.PUSH | SWT.CENTER);
        addButton.setText(">");
        addButton.setBounds(251, 115, 63, 28);
    }

}
