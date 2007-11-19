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

package org.xwiki.plugins.eclipse.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Extended WizardDialog to receive button clicks from within internal Wizard Pages.
 */
public class XWikiWizardDialog extends WizardDialog
{
    public XWikiWizardDialog(Shell parentShell, IWizard wizard)
    {
        super(parentShell, wizard);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.WizardDialog#nextPressed()
     */
    protected void nextPressed()
    {
        if (getCurrentPage() instanceof XWikiWizardPage) {
            if (((XWikiWizardPage) getCurrentPage()).nextPressed()) {
                super.nextPressed();
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.WizardDialog#backPressed()
     */
    protected void backPressed()
    {
        if (getCurrentPage() instanceof XWikiWizardPage) {
            if (((XWikiWizardPage) getCurrentPage()).backPressed()) {
                super.backPressed();
            }
        }
    }

}
