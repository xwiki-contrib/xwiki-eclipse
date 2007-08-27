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

package org.xwiki.plugins.eclipse.wizards.connect.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiConnectionManager;
import org.xwiki.plugins.eclipse.model.impl.XWikiConnectionManager;
import org.xwiki.plugins.eclipse.model.wrappers.XWikiConnectionManagerWrapper;
import org.xwiki.plugins.eclipse.model.wrappers.XWikiConnectionWrapper;
import org.xwiki.plugins.eclipse.rpc.exceptions.CommunicationException;
import org.xwiki.plugins.eclipse.wizards.XWikiWizardPage;
import org.xwiki.plugins.eclipse.wizards.connect.WizardState;
import org.xwiki.plugins.eclipse.wizards.connect.ui.SettingsUI;

/**
 * The WizardPage responsible for getting user input,
 * validating and making the initial connection.
 */
public class SettingsPage extends XWikiWizardPage implements ModifyListener, Listener
{
    /**
     * UI widget of this page.
     */
    private SettingsUI connectionSettingsUI;

    /**
     * current wizard state.
     */
    private WizardState wizardState;

    /**
     * User input - server url.
     */
    private String serverUrl;

    /**
     * User input - username.
     */
    private String userName;

    /**
     * User input - password.
     */
    private String password;

    /**
     * User input - whether the proxy options are selected or not.
     */
    private boolean proxySelected;

    /**
     * User input - proxy host.
     */
    private String proxyHost;

    /**
     * User input - proxy port.
     */
    private String proxyPort;

    /**
     * Constructor.
     * 
     * @param state Used for tracking wizard state.
     */
    public SettingsPage(WizardState state)
    {
        super("CONNECTION_SETTINGS");
        setTitle("Connection settings");
        setDescription("Please provide necessary login details here.");
        this.wizardState = state;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
        connectionSettingsUI = new SettingsUI(parent, SWT.NULL);
        connectionSettingsUI.getServerUrlTextBox().addModifyListener(this);
        connectionSettingsUI.getUsernameTextBox().addModifyListener(this);
        connectionSettingsUI.getPasswordTextBox().addModifyListener(this);
        connectionSettingsUI.getProxyCheckBox().addListener(SWT.Selection, this);
        connectionSettingsUI.getProxyHostTextBox().addModifyListener(this);
        connectionSettingsUI.getProxyPortTextBox().addModifyListener(this);
        connectionSettingsUI.getSelectSpacesCheckBox().addListener(SWT.Selection, this);
        setControl(connectionSettingsUI);
        setPageComplete(validatePage());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Listener for receiving text field changes, we validate the content of the page here.
     * </p>
     * 
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     */
    public void modifyText(ModifyEvent e)
    {
        setPageComplete(validatePage());
    }

    /**
     * {@inheritDoc}
     * <p>
     * For check-boxes in the UI.
     * </p>
     * 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
        if (event.widget.equals(connectionSettingsUI.getSelectSpacesCheckBox())) {
            if (connectionSettingsUI.getSelectSpacesCheckBox().getSelection()) {
                if (isPageComplete()) {
                    setPageComplete(false);
                    ((WizardPage) getNextPage()).setPageComplete(false);
                    setPageComplete(true);
                } else {
                    ((WizardPage) getNextPage()).setPageComplete(false);
                }
            } else {
                if (isPageComplete()) {
                    setPageComplete(false);
                    ((WizardPage) getNextPage()).setPageComplete(true);
                    setPageComplete(true);
                } else {
                    ((WizardPage) getNextPage()).setPageComplete(true);
                }
            }
        } else if (event.widget.equals(connectionSettingsUI.getProxyCheckBox())) {
            if (connectionSettingsUI.getProxyCheckBox().getSelection()) {
                connectionSettingsUI.getProxyHostTextBox().setEnabled(true);
                connectionSettingsUI.getProxyPortTextBox().setEnabled(true);
            } else {
                connectionSettingsUI.getProxyHostTextBox().setEnabled(false);
                connectionSettingsUI.getProxyPortTextBox().setEnabled(false);
            }
        }
    }

    /**
     * Validates the content of the page (a.k.a user input).
     * 
     * @return Whether enough information has been provided to make a connection.
     */
    private boolean validatePage()
    {
        boolean validity = false;
        copyInput();
        validity = !(serverUrl.equals("") || userName.equals("") || password.equals(""));
        if (proxySelected) {
            validity = validity && !(proxyHost.equals("") || proxyPort.equals(""));
        }
        return validity;
    }

    /**
     * Copy user's input to local variables (so that we don't need to do this all over the place.
     */
    private void copyInput()
    {
        serverUrl = connectionSettingsUI.getServerUrlTextBox().getText().trim();
        userName = connectionSettingsUI.getUsernameTextBox().getText().trim();
        password = connectionSettingsUI.getPasswordTextBox().getText().trim();
        proxySelected = connectionSettingsUI.getProxyCheckBox().getSelection();
        proxyHost = connectionSettingsUI.getProxyHostTextBox().getText().trim();
        proxyPort = connectionSettingsUI.getProxyPortTextBox().getText().trim();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.wizards.XWikiWizardPage#nextPressed()
     */
    public boolean nextPressed()
    {
        loginAndGetSpaces();
        if (wizardState.isComplete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Make the connection and retrieve space summaries.
     */
    public void loginAndGetSpaces()
    {
        String proxy = (proxySelected) ? (proxyHost + ":" + proxyPort) : null;
        IXWikiConnectionManager manager =
            new XWikiConnectionManagerWrapper(XWikiConnectionManager.getInstance());
        IXWikiConnection connection = null;
        if (!wizardState.isLoggedIn()) {
            try {
                connection = manager.connect(serverUrl, userName, password, proxy);
                wizardState.setConnection(connection);
                wizardState.setLoggedIn(true);
            } catch (CommunicationException e) {
                wizardState.setLoggedIn(false);
                return;
            }
        }
        if (!wizardState.isComplete()) {
            IXWikiConnection wrapper = new XWikiConnectionWrapper(connection);
            wrapper.getSpaces();
            wizardState.setComplete(true);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.wizards.XWikiWizardPage#backPressed()
     */
    public boolean backPressed()
    {
        // not interested
        return true;
    }
}
