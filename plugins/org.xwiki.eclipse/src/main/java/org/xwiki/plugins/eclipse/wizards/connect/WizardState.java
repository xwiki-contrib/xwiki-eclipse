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

import org.xwiki.plugins.eclipse.model.IXWikiConnection;

/**
 * Tracks the state of the ConnectWizard.
 */
public class WizardState
{
    /**
     * XWikiConnection (if present).
     */
    private IXWikiConnection connection = null;

    /**
     * Whether login phase has succeeded.
     */
    private boolean loggedIn = false;

    /**
     * Whether the wizard can be completed or not.
     */
    private boolean complete = false;

    public IXWikiConnection getConnection()
    {
        return connection;
    }

    public void setConnection(IXWikiConnection connection)
    {
        this.connection = connection;
    }

    public boolean isComplete()
    {
        return complete;
    }

    public void setComplete(boolean complete)
    {
        this.complete = complete;
    }

    public boolean isLoggedIn()
    {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn)
    {
        this.loggedIn = loggedIn;
    }

}
