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
package org.xwiki.eclipse;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.IXWikiSpace;

public class WorkingSetFilter extends ViewerFilter
{
    private WorkingSet workingSet;

    public WorkingSetFilter(WorkingSet workingSet)
    {
        this.workingSet = workingSet;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
        if (element instanceof IXWikiConnection) {
            IXWikiConnection connection = (IXWikiConnection) element;
            return workingSet.contains(connection);
        }

        if (element instanceof IXWikiSpace) {
            IXWikiSpace space = (IXWikiSpace) element;
            return workingSet.contains(space);
        }

        if (element instanceof IXWikiPage) {
            IXWikiPage page = (IXWikiPage) element;
            return workingSet.contains(page);
        }

        return false;
    }

}
