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

package org.xwiki.plugins.eclipse.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Handles all GUI related work (services used by all other modules).
 */
public class GuiUtils
{
    /**
     * Runs a time consuming operation with a progress indicator.
     * 
     * @param operation Operation which consumes time
     * @param shell Shell associated with
     */
    public static void runOperationWithProgress(IRunnableWithProgress operation, Shell shell)
    {
        if (XWikiConstants.PROGRESS_ON) {
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
            try {
                dialog.run(true, false, operation);
            } catch (InvocationTargetException e) {
                reportError(false, "Error", e.getTargetException().getMessage());
                e.printStackTrace();
            } catch (InterruptedException e) {
                reportError(false, "Internel Error",
                    "An error occured while servicing your request.");
            } catch (Exception e) {
                reportError(false, "Internel Error",
                    "An error occured while servicing your request.");
            }
        } else {
            try {
                operation.run(new DummyProgressMonitor());
            } catch (InvocationTargetException e) {
                reportError(false, "Error", e.getTargetException().getMessage());
                e.printStackTrace();
            } catch (InterruptedException e) {
                reportError(false, "Internel Error",
                    "An error occured while servicing your request.");
            } catch (Exception e) {
                reportError(false, "Internel Error",
                    "An error occured while servicing your request.");
            }
        }
    }

    /**
     * A utility method for reporting erros.
     * 
     * @param sync The mode of report (syncronous or asynchronous)
     * @param title Title of the error message.
     * @param message Error message to be displayed.
     */
    public static void reportError(boolean sync, final String title, final String message)
    {
        Runnable runner = new Runnable()
        {
            public void run()
            {
                MessageDialog.openError(null, title, message);
            }
        };

        if (sync) {
            Display.getDefault().syncExec(runner);
        } else {
            Display.getDefault().asyncExec(runner);
        }
    }
    
    /**
     * A utility method for reporting Warnings.
     * 
     * @param sync The mode of report (syncronous or asynchronous)
     * @param title Title of the warning message.
     * @param message Warning message to be displayed.
     */
    public static void reportWarning(boolean sync, final String title, final String message)
    {
        Runnable runner = new Runnable()
        {
            public void run()
            {
                MessageDialog.openWarning(null, title, message);
            }
        };

        if (sync) {
            Display.getDefault().syncExec(runner);
        } else {
            Display.getDefault().asyncExec(runner);
        }
    }

}
