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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xwiki.plugins.eclipse.wizards.connect.ConnectWizard;

/**
 * UI of the settings page of {@link ConnectWizard}.
 */
public class SettingsUI extends Composite
{

    private Text serverUrlTextBox;

    private Label ServerUrlLabel;

    private Button proxyCheckBox;

    private Label proxyPortLabel;

    private Text proxyHostTextBox;

    private Label proxyHostLabel;

    private Text passwordTextBox;

    private Button selectSpacesCheckBox;

    private Text proxyPortTextBox;

    private Label passwordLabel;

    private Text usernameTextBox;

    private Label usernameLabel;

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
        SettingsUI inst = new SettingsUI(shell, SWT.NULL);
        Point size = inst.getSize();
        shell.setLayout(new FillLayout());
        shell.setText("Connect...");
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

    public SettingsUI(org.eclipse.swt.widgets.Composite parent, int style)
    {
        super(parent, style);
        initGUI();
    }

    private void initGUI()
    {
        try {
            this.setLayout(null);
            this.setSize(539, 266);
            createSelectSpacesCheckBox();
            createProxyPortTextBox();
            createProxyPortLabel();
            createProxyHostTextBox();
            createProxyHostLabel();
            createProxyCheckBox();
            createPasswordTextBox();
            createPasswordLabel();
            createUsernameTextBox();
            createUsernameLabel();
            createServerUrlTextBox();
            createServerUrlLabel();
            this.layout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Text getServerUrlTextBox()
    {
        return serverUrlTextBox;
    }

    public Text getUsernameTextBox()
    {
        return usernameTextBox;
    }

    public Text getProxyHostTextBox()
    {
        return proxyHostTextBox;
    }

    public Text getPasswordTextBox()
    {
        return passwordTextBox;
    }

    public Button getProxyCheckBox()
    {
        return proxyCheckBox;
    }

    public Button getSelectSpacesCheckBox()
    {
        return selectSpacesCheckBox;
    }

    public Text getProxyPortTextBox()
    {
        return proxyPortTextBox;
    }

    private void createServerUrlTextBox()
    {
        serverUrlTextBox = new Text(this, SWT.BORDER);
        serverUrlTextBox.setBounds(123, 21, 364, 30);
        serverUrlTextBox.setText("http://localhost:8080/xwiki/xmlrpc/confluence");
    }

    private void createServerUrlLabel()
    {
        ServerUrlLabel = new Label(this, SWT.NONE);
        ServerUrlLabel.setText("Server        : ");
        ServerUrlLabel.setBounds(32, 27, 91, 30);
    }

    private void createProxyCheckBox()
    {
        proxyCheckBox = new Button(this, SWT.CHECK | SWT.LEFT);
        proxyCheckBox.setText("Use Proxy");
        proxyCheckBox.setBounds(32, 126, 91, 30);
    }

    private void createProxyPortLabel()
    {
        proxyPortLabel = new Label(this, SWT.NONE);
        proxyPortLabel.setText("Port  :");
        proxyPortLabel.setBounds(123, 200, 42, 30);
    }

    private void createProxyHostTextBox()
    {
        proxyHostTextBox = new Text(this, SWT.BORDER);
        proxyHostTextBox.setBounds(172, 161, 315, 30);
        proxyHostTextBox.setEnabled(false);
        proxyHostTextBox.setText("somehost");
    }

    private void createProxyHostLabel()
    {
        proxyHostLabel = new Label(this, SWT.NONE);
        proxyHostLabel.setText("Host :");
        proxyHostLabel.setBounds(123, 165, 42, 30);
    }

    private void createPasswordTextBox()
    {
        passwordTextBox = new Text(this, SWT.BORDER);
        passwordTextBox.setBounds(123, 91, 364, 30);
        passwordTextBox.setEchoChar('*');
        passwordTextBox.setText("admin");
    }

    private void createSelectSpacesCheckBox()
    {
        selectSpacesCheckBox = new Button(this, SWT.CHECK | SWT.LEFT);
        selectSpacesCheckBox.setText("Select Spaces");
        selectSpacesCheckBox.setBounds(32, 231, 119, 30);
        selectSpacesCheckBox.setSelection(true);
    }

    private void createProxyPortTextBox()
    {
        proxyPortTextBox = new Text(this, SWT.BORDER);
        proxyPortTextBox.setBounds(172, 196, 63, 30);
        proxyPortTextBox.setEnabled(false);
        proxyPortTextBox.setText("3128");
    }

    private void createPasswordLabel()
    {
        passwordLabel = new Label(this, SWT.NONE);
        passwordLabel.setText("Password    :");
        passwordLabel.setBounds(33, 97, 91, 28);
    }

    private void createUsernameTextBox()
    {
        usernameTextBox = new Text(this, SWT.BORDER);
        usernameTextBox.setBounds(123, 56, 364, 30);
        usernameTextBox.setText("Admin");
    }

    private void createUsernameLabel()
    {
        usernameLabel = new Label(this, SWT.NONE);
        usernameLabel.setText("User Name :");
        usernameLabel.setBounds(32, 62, 91, 30);
    }

}
