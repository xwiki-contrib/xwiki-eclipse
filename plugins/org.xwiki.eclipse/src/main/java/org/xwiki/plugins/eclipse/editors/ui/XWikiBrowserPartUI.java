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

package org.xwiki.plugins.eclipse.editors.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;
import org.xwiki.plugins.eclipse.editors.XWikiEditor;
import org.xwiki.plugins.eclipse.util.GuiUtils;
import org.xwiki.plugins.eclipse.util.XWikiConstants;

/**
 * The UI of the browser of main {@link XWikiEditor}.
 */
public class XWikiBrowserPartUI extends Composite
{

    private Button toggleViewButton;

    private Browser browser;

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
        XWikiBrowserPartUI inst = new XWikiBrowserPartUI(shell, SWT.NULL);
        Point size = inst.getSize();
        shell.setLayout(new FillLayout());
        shell.layout();
        Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
        shell.setSize(shellBounds.width, shellBounds.height);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

    public XWikiBrowserPartUI(org.eclipse.swt.widgets.Composite parent, int style)
    {
        super(parent, style);
        createGUI();
    }

    private void createGUI()
    {
        try {
            GridLayout gridLayout = new GridLayout();
            gridLayout.makeColumnsEqualWidth = true;
            this.setLayout(gridLayout);
            this.setSize(547, 357);
            createToggleButton();
            createBrowser();
            this.layout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createToggleButton()
    {
        toggleViewButton = new Button(this, SWT.TOGGLE | SWT.CENTER);
        toggleViewButton.setImage(XWikiEclipsePlugin.getImageDescriptor(XWikiConstants.TOGGLE_BUTTON_ICON)
            .createImage());
        GridData toggleViewButtonLData = new GridData();
        toggleViewButtonLData.widthHint = 30;
        toggleViewButtonLData.heightHint = 30;
        toggleViewButton.setLayoutData(toggleViewButtonLData);
        toggleViewButton.setSize(30, 30);
        toggleViewButton.setToolTipText("switch between browser / print views");
    }

    public void createBrowser()
    {
        GridData browserLData = new GridData();
        browserLData.grabExcessVerticalSpace = true;
        browserLData.grabExcessHorizontalSpace = true;
        browserLData.verticalAlignment = GridData.FILL;
        browserLData.horizontalAlignment = GridData.FILL;
        browser = new Browser(this, SWT.BORDER);
        browser.setLayoutData(browserLData);
    }

    public Browser getBrowser()
    {
        return browser;
    }

    public Button getToggleViewButton()
    {
        return toggleViewButton;
    }

}
