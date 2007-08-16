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

package org.xwiki.plugins.eclipse.wizards.newspace.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.wizards.XWikiWizardPage;
import org.xwiki.plugins.eclipse.wizards.newspace.ui.SpaceSettingsPageUI;

/**
 * WizardPage responsible for getting user input required
 * to create a new Space.
 */
public class SpaceSettingsPage extends XWikiWizardPage implements ModifyListener
{
    /**
     * UI widget of this page.
     */
    private SpaceSettingsPageUI ui;

    /**
     * Connection into which space will be added.
     */
    private IXWikiConnection connection;

    /**
     * User Input the Space Name.
     */
    private String spaceName;

    /**
     * User Input the Space Key.
     */
    private String spaceKey;

    /**
     * User Input the Space Description.
     */
    private String spaceDescription;

    /**
     * Constructor.
     * 
     * @param connection Connection into which the space will be added.
     */
    public SpaceSettingsPage(IXWikiConnection connection)
    {
        super("SPACE_SETTINGS");
        this.connection = connection;
        setTitle("Space settings");
        setDescription("Please provide initial space settings here.");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
        ui = new SpaceSettingsPageUI(parent, SWT.NULL);
        ui.getSpaceNameText().addModifyListener(this);
        // spaceKey masked.
        // ui.getSpaceKeyText().addModifyListener(this);
        ui.getSpaceDescriptionText().addModifyListener(this);
        setPageComplete(validatePage());
        setControl(ui);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     */
    public void modifyText(ModifyEvent e)
    {
        setPageComplete(validatePage());
    }

    /**
     * Validates content of this page.
     * 
     * @return Validity of the page
     */
    private boolean validatePage()
    {
        spaceName = ui.getSpaceNameText().getText().trim();
        // spaceKey masked.
        // spaceKey = ui.getSpaceKeyText().getText().trim();
        spaceKey = spaceName;
        spaceDescription = ui.getSpaceDescriptionText().getText().trim();
        if (spaceName.equals("")) {
            setErrorMessage(" Space Name Cannot be Empty.");
            return false;
        } else if (connection.getSpaceByName(spaceName) != null) {
            setErrorMessage(" Space Name Must be Unique.");
            return false;
        } else if (spaceKey.equals("")) {
            setErrorMessage(" Space Key Cannot be Empty.");
            return false;
        } else if (connection.getSpaceByKey(spaceKey) != null) {
            setErrorMessage(" Space Key Must be Unique.");
            return false;
        } else if (spaceDescription.equals("")) {
            setErrorMessage(" Space Description Cannot be Empty.");
            return false;
        } else {
            setErrorMessage(null);
            return true;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.wizards.XWikiWizardPage#backPressed()
     */
    public boolean backPressed()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.wizards.XWikiWizardPage#nextPressed()
     */
    public boolean nextPressed()
    {
        return false;
    }

    public String getSpaceDescription()
    {
        return spaceDescription;
    }

    public String getSpaceKey()
    {
        return spaceKey;
    }

    public String getSpaceName()
    {
        return spaceName;
    }

}
