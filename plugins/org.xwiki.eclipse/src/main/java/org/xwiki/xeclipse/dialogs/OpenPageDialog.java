package org.xwiki.xeclipse.dialogs;

import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;
import org.xwiki.xeclipse.XWikiEclipsePageIndex;
import org.xwiki.xeclipse.model.IXWikiPage;

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

    public OpenPageDialog(Shell shell)
    {
        super(shell);
        setTitle("Open page");
        setListLabelProvider(new WorkbenchLabelProvider());
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

    @Override
    protected Comparator getItemsComparator()
    {
        return new Comparator()
        {

            public int compare(Object o1, Object o2)
            {
                return o1.toString().compareTo(o2.toString());
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
