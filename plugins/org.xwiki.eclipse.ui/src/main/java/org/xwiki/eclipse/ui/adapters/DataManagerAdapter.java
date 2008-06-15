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
package org.xwiki.eclipse.ui.adapters;

import java.util.List;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;
import org.xwiki.eclipse.ui.utils.XWikiEclipseSafeRunnableWithResult;

public class DataManagerAdapter extends WorkbenchAdapter
{
    @Override
    public Object[] getChildren(Object object)
    {
        if (object instanceof DataManager) {
            final DataManager dataManager = (DataManager) object;

            XWikiEclipseSafeRunnableWithResult<List<XWikiEclipseSpaceSummary>> runnable =
                new XWikiEclipseSafeRunnableWithResult<List<XWikiEclipseSpaceSummary>>()
                {
                    public void run() throws Exception
                    {
                        setResult(dataManager.getSpaces());
                    }

                };
            SafeRunner.run(runnable);

            return runnable.getResult() != null ? runnable.getResult().toArray() : NO_CHILDREN;
        }

        return super.getChildren(object);
    }

    @Override
    public String getLabel(Object object)
    {
        if (object instanceof DataManager) {
            DataManager dataManager = (DataManager) object;
            return dataManager.getName();
        }

        return super.getLabel(object);
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object)
    {
        return UIPlugin.getImageDescriptor(UIConstants.XWIKI_ICON);
    }

    @Override
    public FontData getFont(Object object)
    {
        if (object instanceof DataManager) {
            DataManager dataManager = (DataManager) object;

            if (dataManager.isConnected()) {
                return JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT).getFontData()[0];
            }
        }

        return super.getFont(object);
    }
}
