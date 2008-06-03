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
package org.xwiki.eclipse.rcp;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory
{
    public void createInitialLayout(IPageLayout layout)
    {
        IFolderLayout leftFolder =
            layout.createFolder("leftFolder", IPageLayout.LEFT, 0.20f, layout.getEditorArea());

        leftFolder.addView("org.xwiki.eclipse.ui.views.Navigator");
        leftFolder.addView("org.eclipse.ui.navigator.ProjectExplorer");

        IFolderLayout bottomFolder =
            layout
                .createFolder("bottomFolder", IPageLayout.BOTTOM, 0.60f, layout.getEditorArea());
        bottomFolder.addView("org.xwiki.eclipse.ui.views.PagePreview");
        bottomFolder.addView("org.eclipse.pde.runtime.LogView");

        layout.addView("org.eclipse.ui.views.ContentOutline", IPageLayout.RIGHT, 0.80f, layout
            .getEditorArea());
    }
}
