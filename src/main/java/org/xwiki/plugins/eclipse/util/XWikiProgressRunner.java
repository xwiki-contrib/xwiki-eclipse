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

package org.xwiki.plugins.eclipse.util;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.xwiki.plugins.eclipse.rpc.exceptions.CommunicationException;

/**
 * Customized {@link IRunnableWithProgress} to accomodate
 * XWiki specific tasks.
 */
abstract public class XWikiProgressRunner implements IRunnableWithProgress
{
    /**
     * Place holder for any exceptions thrown while executing the task (RPC).
     */
    private CommunicationException comEx;

    /**
     * Place holder for any objects returned while executing the task (RPC).
     */
    private Object artifact;

    /**
     * @return Any {@link CommunicationException} s thrown or null.
     */
    public CommunicationException getComEx()
    {
        return comEx;
    }

    /**
     * @param communicationException Used by the executing task to set any
     *            {@link CommunicationException} s encountered.
     */
    public void setComEx(CommunicationException communicationException)
    {
        this.comEx = communicationException;
    }

    /**
     * @return Return object as returned from relevant RPC.
     */
    public Object getArtifact()
    {
        return artifact;
    }

    /**
     * @param artifact Used by the executng task to set any Objects returned by RPCs.
     */
    public void setArtifact(Object artifact)
    {
        this.artifact = artifact;
    }

}
