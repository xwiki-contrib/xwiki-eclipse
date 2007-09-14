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

package org.xwiki.plugins.eclipse.wizards.connect.pages;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.wizards.XWikiWizardPage;
import org.xwiki.plugins.eclipse.wizards.connect.WizardState;
import org.xwiki.plugins.eclipse.wizards.connect.ui.SpacesUI;

/**
 * This is where the user selects which spaces he wish to work on.
 */
public class SpacesPage extends XWikiWizardPage implements SelectionListener
{
    /**
     * UI widget of this page.
     */
    private SpacesUI importSpacesUI;

    /**
     * For tracking wizard state.
     */
    private WizardState wizardState;

    /**
     * Constructor.
     * 
     * @param state Wizard state.
     */
    public SpacesPage(WizardState state)
    {
        super("IMPORT_SPACES");
        setTitle("Select Spaces");
        setDescription("Please select the spaces you wish to work on.");
        this.wizardState = state;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
        importSpacesUI = new SpacesUI(parent, SWT.NULL);
        importSpacesUI.getAddAllButton().addSelectionListener(this);
        importSpacesUI.getRemoveAllButton().addSelectionListener(this);
        importSpacesUI.getAddButton().addSelectionListener(this);
        importSpacesUI.getRemoveButton().addSelectionListener(this);
        setControl(importSpacesUI);
        setPageComplete(false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    /**
     * {@inheritDoc}
     * <p>
     * Click listener for various buttons on UI
     * </p>
     * 
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected(SelectionEvent e)
    {
        if (importSpacesUI.getAddAllButton().equals(e.getSource())) {
            String[] items = importSpacesUI.getAvailableSpacesList().getItems();
            importSpacesUI.getAvailableSpacesList().removeAll();
            for (String item : items) {
                importSpacesUI.getSelectedSpacesList().add(item);
                wizardState.getConnection().getSpace(item).setMasked(false);
            }
        } else if (importSpacesUI.getRemoveAllButton().equals(e.getSource())) {
            String[] items = importSpacesUI.getSelectedSpacesList().getItems();
            importSpacesUI.getSelectedSpacesList().removeAll();
            for (String item : items) {
                importSpacesUI.getAvailableSpacesList().add(item);
                wizardState.getConnection().getSpace(item).setMasked(true);
            }
        } else if (importSpacesUI.getAddButton().equals(e.getSource())) {
            String[] selections = importSpacesUI.getAvailableSpacesList().getSelection();
            for (String selection : selections) {
                importSpacesUI.getSelectedSpacesList().add(selection);
                wizardState.getConnection().getSpace(selection).setMasked(false);
                importSpacesUI.getAvailableSpacesList().remove(selection);
            }
        } else if (importSpacesUI.getRemoveButton().equals(e.getSource())) {
            String[] selections = importSpacesUI.getSelectedSpacesList().getSelection();
            for (String selection : selections) {
                importSpacesUI.getAvailableSpacesList().add(selection);
                wizardState.getConnection().getSpace(selection).setMasked(true);
                importSpacesUI.getSelectedSpacesList().remove(selection);
            }
        }
        setPageComplete(validatePage());
    }

    /**
     * Validates this page.
     * 
     * @return Whether all required information is present or not.
     */
    private boolean validatePage()
    {
        return (importSpacesUI.getSelectedSpacesList().getItemCount() != 0);
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
        // not possible
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Some preparation code
     * </p>
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    public void setVisible(boolean visible)
    {
        if (visible) {
            importSpacesUI.getAvailableSpacesList().removeAll();
            importSpacesUI.getSelectedSpacesList().removeAll();
        }
        if (wizardState.isComplete()) {
            Collection<IXWikiSpace> spaces = wizardState.getConnection().getSpaces();
            for (IXWikiSpace s : spaces) {
                importSpacesUI.getAvailableSpacesList().add(s.getName());
            }
        }
        super.setVisible(visible);
    }
}
