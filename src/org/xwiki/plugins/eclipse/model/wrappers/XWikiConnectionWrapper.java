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
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.rpc.exceptions.CommunicationException;
import org.xwiki.plugins.eclipse.util.GuiUtils;
import org.xwiki.plugins.eclipse.util.XWikiProgressRunner;

/**
 * Implentation of Decorator Pattern for adding GUI icing for
 * underlying {@link IXWikiConnection}.
 */
public class XWikiConnectionWrapper implements IXWikiConnection
{
    /**
     * Actual {@link IXWikiConnection} being wrapped.
     */
    private IXWikiConnection connection;

    /**
     * Constructs a wrapper.
     * 
     * @param connection Actual {@link IXWikiConnection} instance.
     */
    public XWikiConnectionWrapper(IXWikiConnection connection)
    {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#addSpace(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void addSpace(final String name, final String key, final String description)
        throws CommunicationException
    {
        // It is assumed that at this point it has been verfied that spaceName
        // and spaceKey are unique.
        XWikiProgressRunner operation = new XWikiProgressRunner()
        {
            public void run(IProgressMonitor monitor) throws InvocationTargetException,
                InterruptedException
            {
                monitor.beginTask("Storing space...", IProgressMonitor.UNKNOWN);
                try {
                    connection.addSpace(name, key, description);
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
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#disconnect()
     */
    public void disconnect() throws CommunicationException
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
                monitor.beginTask("Logging out...", IProgressMonitor.UNKNOWN);
                try {
                    connection.disconnect();
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
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getLoginToken()
     */
    public String getLoginToken()
    {
        return connection.getLoginToken();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getServerUrl()
     */
    public String getServerUrl()
    {
        return connection.getServerUrl();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getSpaceByName(java.lang.String)
     */
    public IXWikiSpace getSpaceByName(String spaceName)
    {
        return connection.getSpaceByName(spaceName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getSpaceByKey(java.lang.String)
     */
    public IXWikiSpace getSpaceByKey(String spaceKey)
    {
        return connection.getSpaceByKey(spaceKey);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getSpaces()
     */
    public Collection<IXWikiSpace> getSpaces()
    {
        try {
            init();
            return connection.getSpaces();
        } catch (CommunicationException e) {
            // TODO log this exception
            return new ArrayList<IXWikiSpace>();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#getUserName()
     */
    public String getUserName()
    {
        return connection.getUserName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#init()
     */
    public void init() throws CommunicationException
    {
        if (!isSpacesReady()) {
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
                    monitor.beginTask("Retrieving pages...", IProgressMonitor.UNKNOWN);
                    try {
                        connection.init();
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
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#isSpacesReady()
     */
    public boolean isSpacesReady()
    {
        return connection.isSpacesReady();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.plugins.eclipse.model.IXWikiConnection#removeSpace(java.lang.String)
     */
    public void removeSpace(final String key) throws CommunicationException
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
                monitor.beginTask("Removing space...", IProgressMonitor.UNKNOWN);
                try {
                    connection.removeSpace(key);
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
        }
    }
}
