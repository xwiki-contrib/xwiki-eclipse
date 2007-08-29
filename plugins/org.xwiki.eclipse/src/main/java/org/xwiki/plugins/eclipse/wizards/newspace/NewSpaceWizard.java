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

package org.xwiki.plugins.eclipse.wizards.newspace;

import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.eclipse.jface.wizard.Wizard;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.wizards.newspace.pages.SpaceSettingsPage;

/**
 * The wizard is responsible for adding a new space into a connection.
 */
public class NewSpaceWizard extends Wizard
{
    /**
     * This is where the user provides parameters for a new connection.
     */
    private SpaceSettingsPage settingsPage;

    /**
     * Connection into which we'll be adding spaces.
     */
    private IXWikiConnection connection;

    /**
     * Constructor.
     * 
     * @param connection Connection into which space will be added.
     */
    public NewSpaceWizard(IXWikiConnection connection)
    {
        super();
        setWindowTitle("Add New Space...");
        setNeedsProgressMonitor(false);
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        settingsPage = new SpaceSettingsPage(connection);
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
            connection.addSpace(settingsPage.getSpaceName(), settingsPage.getSpaceKey(),
                settingsPage.getSpaceDescription());
        } catch (SwizzleConfluenceException e) {
            // Will be logged elsewhere.
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
