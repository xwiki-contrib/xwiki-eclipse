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

package org.xwiki.plugins.eclipse.wizards.connect;

import java.util.Collection;

import org.codehaus.swizzle.confluence.SwizzleConfluenceException;
import org.eclipse.jface.wizard.Wizard;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.model.wrappers.XWikiConnectionWrapper;
import org.xwiki.plugins.eclipse.wizards.connect.pages.SettingsPage;
import org.xwiki.plugins.eclipse.wizards.connect.pages.SpacesPage;

/**
 * The wizard responsible for making a new connection.
 */
public class ConnectWizard extends Wizard
{
    /**
     * This is where the user provides parameters for a new connection.
     */
    private SettingsPage connectionSettingsWizardPage;

    /**
     * Let's the user select which spaces to be shown.
     */
    private SpacesPage importSpacesWizardPage;

    /**
     * Used for passing state information between different pages.
     */
    private WizardState wizardState;

    /**
     * Constructor.
     */
    public ConnectWizard()
    {
        super();
        setWindowTitle("Connect...");
        setNeedsProgressMonitor(false);
        wizardState = new WizardState();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        connectionSettingsWizardPage = new SettingsPage(wizardState);
        importSpacesWizardPage = new SpacesPage(wizardState);
        addPage(connectionSettingsWizardPage);
        addPage(importSpacesWizardPage);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        if (!wizardState.isLoggedIn()) {
            connectionSettingsWizardPage.loginAndGetSpaces();
            if (wizardState.isComplete()) {
                Collection<IXWikiSpace> spaces = wizardState.getConnection().getSpaces();
                for (IXWikiSpace s : spaces) {
                    s.setMasked(false);
                }
            } else {
                return true;
            }
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
        return logoutIfNecessary();
    }

    /**
     * Depending on the current wizard state, logs out the user if need be.
     * 
     * @return Whether logging out was successful or not.
     */
    private boolean logoutIfNecessary()
    {
        if (wizardState.isLoggedIn()) {
            IXWikiConnection connection = new XWikiConnectionWrapper(wizardState.getConnection());
            try {
                connection.disconnect();
            } catch (SwizzleConfluenceException e) {
                // Will be logged else where.
            }
            wizardState.setConnection(null);
            wizardState.setComplete(false);
            wizardState.setLoggedIn(false);
        }
        return true;
    }
}
