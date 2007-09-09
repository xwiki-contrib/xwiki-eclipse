/*
 * Copyright 2006-2007, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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

package org.xwiki.plugins.eclipse.wizards.newspace.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xwiki.plugins.eclipse.wizards.newspace.pages.SpaceSettingsPage;

/**
 * UI of {@link SpaceSettingsPage}.
 */
public class SpaceSettingsPageUI extends Composite
{

    private Text spaceNameText;

    private Label spaceDescriptionLabel;

    private Text spaceDescriptionText;

    private Label spaceNameLabel;

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
        FillLayout shellLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
        shell.setLayout(shellLayout);
        SpaceSettingsPageUI inst = new SpaceSettingsPageUI(shell, SWT.NULL);
        Point size = inst.getSize();
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

    public SpaceSettingsPageUI(org.eclipse.swt.widgets.Composite parent, int style)
    {
        super(parent, style);
        createGUI();
    }

    private void createGUI()
    {
        try {
            this.setLayout(null);
            this.setSize(483, 266);
            createSpaceNameLabel();
            createSpaceNameText();
            createSpaceDescriptionLabel();
            createSpaceDescriptionText();
            this.layout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Text getSpaceNameText()
    {
        return spaceNameText;
    }

    public Text getSpaceDescriptionText()
    {
        return spaceDescriptionText;
    }

    private void createSpaceNameText()
    {
        spaceNameText = new Text(this, SWT.BORDER);
        spaceNameText.setBounds(135, 28, 315, 28);
        spaceNameText.setText("NewSpace");
    }

    private void createSpaceDescriptionLabel()
    {
        spaceDescriptionLabel = new Label(this, SWT.NONE);
        spaceDescriptionLabel.setText("Description :");
        spaceDescriptionLabel.setBounds(30, 77, 91, 28);
    }

    private void createSpaceDescriptionText()
    {
        spaceDescriptionText = new Text(this, SWT.MULTI | SWT.WRAP | SWT.BORDER);
        spaceDescriptionText.setBounds(135, 77, 315, 154);
        spaceDescriptionText.setText("<p>This is a new space</p>");
    }

    private void createSpaceNameLabel()
    {
        spaceNameLabel = new Label(this, SWT.NONE);
        spaceNameLabel.setText("Name         :");
        spaceNameLabel.setBounds(30, 28, 98, 21);
    }

}
