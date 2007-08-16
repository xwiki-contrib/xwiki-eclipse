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

package org.xwiki.plugins.eclipse.wizards.newpage.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.wizards.XWikiWizardPage;
import org.xwiki.plugins.eclipse.wizards.newpage.ui.PageSettingsPageUI;

/**
 * WizardPage responsible for getting user 
 * input required to add a new page.
 */
public class PageSettingsPage extends XWikiWizardPage implements ModifyListener
{

    /**
     * UI of this page.
     */
    private PageSettingsPageUI ui;

    /**
     * User Input the Page title.
     */
    private String pageTitle;

    /**
     * User Input the Page Content.
     */
    private String pageContent;

    /**
     * Space into which we're gonna add a page.
     */
    private IXWikiSpace space;

    /**
     * Constructs and initializes the page.
     * 
     * @param space Into which we're gonna add a page.
     */
    public PageSettingsPage(IXWikiSpace space)
    {
        super("PAGE_SETTINGS");
        this.space = space;
        setTitle("Page settings");
        setDescription("Please provide initial page settings here.");
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

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
        ui = new PageSettingsPageUI(parent, SWT.NULL);
        ui.getTitleText().addModifyListener(this);
        ui.getContentText().addModifyListener(this);
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
     * Validates the input of this page.
     * 
     * @return Whether enough info has been provided or not.
     */
    private boolean validatePage()
    {
        this.pageTitle = ui.getTitleText().getText().trim();
        this.pageContent = ui.getContentText().getText();
        if (pageTitle.equals("")) {
            setErrorMessage(" Page Title Cannot be Empty.");
            return false;
        }        
        // check whether page already exists.
        if (space.getPageByTitle(pageTitle) != null) {
            setErrorMessage(" Page Title Must be Unique : " + pageTitle);
            return false;
        } else if (pageContent.trim().equals("")) {
            setErrorMessage(" Initial Page Content Cannot be Empty.");
            return false;
        } else {
            setErrorMessage(null);
            return true;
        }
    }

    public String getPageContent()
    {
        return pageContent;
    }

    public String getPageTitle()
    {
        return pageTitle;
    }

}
