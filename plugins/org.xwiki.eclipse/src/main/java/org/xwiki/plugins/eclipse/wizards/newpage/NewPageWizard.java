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

package org.xwiki.plugins.eclipse.wizards.newpage;

import org.eclipse.jface.wizard.Wizard;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.rpc.exceptions.CommunicationException;
import org.xwiki.plugins.eclipse.wizards.newpage.pages.PageSettingsPage;

/**
 * Wizard for adding a new page to a space.
 */
public class NewPageWizard extends Wizard
{

    /**
     * Page settings wizard page.
     */
    private PageSettingsPage settingsPage;

    /**
     * Space into which we'll be adding pages.
     */
    private IXWikiSpace space;

    /**
     * Constructs and initializes a new wizard.
     * 
     * @param space Space into which pages are added.
     */
    public NewPageWizard(IXWikiSpace sapce)
    {
        super();
        setWindowTitle("Add New Page...");
        setNeedsProgressMonitor(false);
        this.space = sapce;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        settingsPage = new PageSettingsPage(space);
        addPage(settingsPage);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        try {
            space.addPage(settingsPage.getPageTitle(), settingsPage.getPageContent());
        } catch (CommunicationException e) {
            // Will be logged elsewhere
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#performCancel()
     */
    public boolean performCancel()
    {
        return super.performCancel();
    }

}
