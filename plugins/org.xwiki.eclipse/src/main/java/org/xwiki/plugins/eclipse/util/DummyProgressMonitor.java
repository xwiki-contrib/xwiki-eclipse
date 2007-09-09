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

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is used to hide progress information in case the user wants them to be suppressed.
 */
class DummyProgressMonitor implements IProgressMonitor
{

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String, int)
     */
    public void beginTask(String name, int totalWork)
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IProgressMonitor#done()
     */
    public void done()
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
     */
    public void internalWorked(double work)
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
     */
    public boolean isCanceled()
    {

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
     */
    public void setCanceled(boolean value)
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
     */
    public void setTaskName(String name)
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
     */
    public void subTask(String name)
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
     */
    public void worked(int work)
    {

    }

}
