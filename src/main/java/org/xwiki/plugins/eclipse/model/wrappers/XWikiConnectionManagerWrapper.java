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

package org.xwiki.plugins.eclipse.model.wrappers;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiConnectionManager;
import org.xwiki.plugins.eclipse.rpc.exceptions.CommunicationException;
import org.xwiki.plugins.eclipse.util.GuiUtils;
import org.xwiki.plugins.eclipse.util.XWikiProgressRunner;

/**
 * Implentation of Decorator Pattern for adding GUI icing for
 * underlying {@link IXWikiConnectionManager}.
 */
public class XWikiConnectionManagerWrapper implements IXWikiConnectionManager
{
    /**
     * Actual {@link IXWikiConnectionManager} being wrapped.
     */
    private IXWikiConnectionManager manager;

    /**
     * Constructs a wrapper.
     * 
     * @param manager Actual {@link IXWikiConnectionManager} instance.
     */
    public XWikiConnectionManagerWrapper(IXWikiConnectionManager manager)
    {
        this.manager = manager;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnectionManager#connect(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public IXWikiConnection connect(final String serverUrl, final String userName,
        final String password, final String proxy) throws CommunicationException
    {
        XWikiProgressRunner operation = new XWikiProgressRunner()
        {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
             */
            public void run(IProgressMonitor monitor) throws InvocationTargetException,
                InterruptedException
            {
                monitor.beginTask("Logging in...", IProgressMonitor.UNKNOWN);
                try {
                    IXWikiConnection con = manager.connect(serverUrl, userName, password, proxy);
                    setArtifact(con);
                    monitor.done();
                } catch (CommunicationException e) {
                    monitor.done();
                    setComEx(e);
                    throw new InvocationTargetException(e);
                }
            }
        };
        GuiUtils.runOperationWithProgress(operation, null);
        if (operation.getComEx() != null) {
            throw operation.getComEx();
        } else {
            return (IXWikiConnection) operation.getArtifact();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnectionManager#getAllConnections()
     */
    public Collection<IXWikiConnection> getAllConnections()
    {
        return manager.getAllConnections();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnectionManager#removeConnection(java.lang.String)
     */
    public void removeConnection(String loginToken)
    {
        manager.removeConnection(loginToken);
    }

}
