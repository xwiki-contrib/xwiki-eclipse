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
package org.xwiki.eclipse.dialogs;

import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.xwiki.eclipse.XWikiEclipsePageIndex;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;

public class OpenPageDialog extends FilteredItemsSelectionDialog
{
    private static class SelectionLabelDecorator extends LabelDecorator
    {
        @Override
        public Image decorateImage(Image image, Object element, IDecorationContext context)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String decorateText(String text, Object element, IDecorationContext context)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean prepareDecoration(Object element, String originalText,
            IDecorationContext context)
        {
            // TODO Auto-generated method stub
            return false;
        }

        public Image decorateImage(Image image, Object element)
        {
            // TODO Auto-generated method stub
            return null;
        }

        public String decorateText(String text, Object element)
        {
            if (element instanceof IXWikiPage) {
                IXWikiPage xwikiPage = (IXWikiPage) element;
                return String.format("%s - %s@%s", text, xwikiPage.getConnection().getUserName(),
                    xwikiPage.getConnection().getServerUrl());
            }
            return text;
        }

        public void addListener(ILabelProviderListener listener)
        {
            // TODO Auto-generated method stub

        }

        public void dispose()
        {
            // TODO Auto-generated method stub

        }

        public boolean isLabelProperty(Object element, String property)
        {
            // TODO Auto-generated method stub
            return false;
        }

        public void removeListener(ILabelProviderListener listener)
        {
            // TODO Auto-generated method stub

        }

    }

    private static class OpenPageLabelProvider extends LabelProvider
    {
        WorkbenchLabelProvider workbenchLabelProvider;

        public OpenPageLabelProvider(WorkbenchLabelProvider workbenchLabelProvider)
        {
            this.workbenchLabelProvider = workbenchLabelProvider;
        }

        @Override
        public String getText(Object element)
        {
            if (element instanceof IXWikiPage) {
                IXWikiPage xwikiPage = (IXWikiPage) element;
                return String.format("%s (%s)", xwikiPage.getTitle(), xwikiPage.getSpaceKey());
            }

            return super.getText(element);
        }

        @Override
        public Image getImage(Object element)
        {
            return workbenchLabelProvider.getImage(element);
        }
    }

    public OpenPageDialog(Shell shell)
    {
        super(shell);
        setTitle("Open page");
        setListLabelProvider(new OpenPageLabelProvider(new WorkbenchLabelProvider()));        
        setListSelectionLabelDecorator(new SelectionLabelDecorator());
        setDetailsLabelProvider(new WorkbenchLabelProvider());
    }

    @Override
    protected Control createExtendedContentArea(Composite parent)
    {
        return null;
    }

    @Override
    protected ItemsFilter createFilter()
    {
        return new ItemsFilter()
        {
            @Override
            public boolean isConsistentItem(Object item)
            {
                return true;
            }

            @Override
            public boolean matchItem(Object item)
            {
                if (item instanceof IXWikiPage) {
                    IXWikiPage xwikiPage = (IXWikiPage) item;
                    return matches(xwikiPage.getTitle());
                }

                return false;
            }

        };
    }

    @Override
    protected void fillContentProvider(AbstractContentProvider contentProvider,
        ItemsFilter itemsFilter, IProgressMonitor progressMonitor) throws CoreException
    {
        progressMonitor.beginTask("Searching...", IProgressMonitor.UNKNOWN);
        for (IXWikiPage page : XWikiEclipsePageIndex.getDefault().getPages()) {
            if (page.isCached() || page.getConnection().isConnected()) {
                contentProvider.add(page, itemsFilter);
            }
        }

        progressMonitor.done();

    }

    @Override
    protected IDialogSettings getDialogSettings()
    {
        return XWikiEclipsePlugin.getDefault().getDialogSettings();
    }

    @Override
    public String getElementName(Object item)
    {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Comparator getItemsComparator()
    {
        return new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                IXWikiPage page1 = (IXWikiPage) o1;
                IXWikiPage page2 = (IXWikiPage) o2;
                
                return page1.getTitle().compareTo(page2.getTitle());
            }
        };
    }

    @Override
    protected IStatus validateItem(Object item)
    {
        // TODO Auto-generated method stub
        return Status.OK_STATUS;
    }
}
