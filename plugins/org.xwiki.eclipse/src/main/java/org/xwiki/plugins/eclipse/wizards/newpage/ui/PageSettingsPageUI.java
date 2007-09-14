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

package org.xwiki.plugins.eclipse.wizards.newpage.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xwiki.plugins.eclipse.wizards.newpage.pages.PageSettingsPage;

/**
 * UI of {@link PageSettingsPage}.
 */
public class PageSettingsPageUI extends Composite
{

    private Label titleLabel;

    private Text contentText;

    private Label contentLabel;

    private Text titleText;

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
        PageSettingsPageUI inst = new PageSettingsPageUI(shell, SWT.NULL);
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

    public PageSettingsPageUI(org.eclipse.swt.widgets.Composite parent, int style)
    {
        super(parent, style);
        createGUI();
    }

    private void createGUI()
    {
        try {
            this.setLayout(null);
            this.setSize(462, 294);
            createTitleLabel();
            createTitleText();
            createContentLabel();
            createContentText();
            this.layout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Text getTitleText()
    {
        return titleText;
    }

    public Text getContentText()
    {
        return contentText;
    }

    private void createTitleLabel()
    {
        titleLabel = new Label(this, SWT.NONE);
        titleLabel.setText("Title       : ");
        titleLabel.setBounds(24, 39, 63, 21);
    }

    private void createContentText()
    {
        contentText = new Text(this, SWT.MULTI | SWT.WRAP | SWT.BORDER);
        contentText.setText("1.1 New Page");
        contentText.setBounds(106, 105, 329, 168);
    }

    private void createContentLabel()
    {
        contentLabel = new Label(this, SWT.NONE);
        contentLabel.setText("Content :");
        contentLabel.setBounds(24, 108, 63, 28);
    }

    private void createTitleText()
    {
        titleText = new Text(this, SWT.BORDER);
        titleText.setText("Title");
        titleText.setBounds(106, 35, 329, 28);
    }

}
