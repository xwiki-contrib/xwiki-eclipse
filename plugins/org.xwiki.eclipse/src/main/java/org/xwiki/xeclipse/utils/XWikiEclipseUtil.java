package org.xwiki.xeclipse.utils;

import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Utility methods for different tasks. 
 */
public class XWikiEclipseUtil
{
    /**
     * @param object The object that should represent an IStructuredSelection
     * @return the only selected object in the passed selection or null if the passed object is not
     *         an IStructuredSelection of if it contains more than one selected item.
     */
    public static Object getSingleSelectedObjectInStructuredSelection(Object object)
    {
        if (object instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) object;
            if (selection.size() == 1) {
                return selection.getFirstElement();
            }
        }

        return null;
    }
}
