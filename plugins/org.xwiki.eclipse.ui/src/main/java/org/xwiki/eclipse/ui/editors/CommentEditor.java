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
package org.xwiki.eclipse.ui.editors;

import java.util.Calendar;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.ui.utils.UIUtils;

/**
 * @version $Id$
 */
public class CommentEditor extends EditorPart
{
    public static final String ID = "org.xwiki.eclipse.ui.editors.Comment";

    private ScrolledForm scrolledForm;

    private boolean dirty;

    @Override
    public void doSave(IProgressMonitor monitor)
    {
        UIUtils.showMessageDialog(Display.getDefault().getActiveShell(), SWT.ICON_ERROR, "Save is not implemented",
            "Update comment is not supported yet in REST API yet");

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs()
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        setSite(site);
        setInput(input);
        setPartName(input.getName());

    }

    @Override
    protected void setInput(IEditorInput input)
    {
        if (!(input instanceof CommentEditorInput)) {
            throw new IllegalArgumentException("Invalid input for editor");
        }

        super.setInput(input);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty()
    {
        return dirty;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent)
    {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        scrolledForm = toolkit.createScrolledForm(parent);
        toolkit.decorateFormHeading(scrolledForm.getForm());

        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = 2;
        scrolledForm.getBody().setLayout(layout);

        CommentEditorInput input = (CommentEditorInput) getEditorInput();
        XWikiEclipseComment c = input.getComment();

        /* author prettyName: Author */
        Section authorSection =
            toolkit.createSection(scrolledForm.getBody(), Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED
                | Section.CLIENT_INDENT);
        authorSection.setText("Author");
        authorSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        Composite compositeClient = toolkit.createComposite(authorSection, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(compositeClient);
        Text author = toolkit.createText(compositeClient, c.getAuthor(), SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(author);

        author.addModifyListener(new ModifyListener()
        {

            @Override
            public void modifyText(ModifyEvent e)
            {
                dirty = true;
                firePropertyChange(PROP_DIRTY);
            }
        });

        authorSection.setClient(compositeClient);

        /* highlight, Highlighted Text */
        Section highlightSection =
            toolkit.createSection(scrolledForm.getBody(), Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED
                | Section.CLIENT_INDENT);
        highlightSection.setText("Highlighted Text");
        highlightSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        compositeClient = toolkit.createComposite(highlightSection, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(compositeClient);
        Text highlight =
            toolkit.createText(compositeClient, c.getHighlight() == null ? "" : c.getHighlight(), SWT.BORDER | SWT.WRAP
                | SWT.MULTI | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false)
            .hint(0, highlight.getLineHeight() * 3).applyTo(highlight);

        highlight.addModifyListener(new ModifyListener()
        {

            @Override
            public void modifyText(ModifyEvent e)
            {
                dirty = true;
                firePropertyChange(PROP_DIRTY);
            }
        });

        highlightSection.setClient(compositeClient);

        /* text, Comment */
        Section commentTextSection =
            toolkit.createSection(scrolledForm.getBody(), Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED
                | Section.CLIENT_INDENT);
        commentTextSection.setText("Comment");
        commentTextSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        compositeClient = toolkit.createComposite(commentTextSection, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(compositeClient);
        Text commentText =
            toolkit.createText(compositeClient, c.getText() == null ? "" : c.getText(), SWT.BORDER | SWT.WRAP
                | SWT.MULTI | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false)
            .hint(0, commentText.getLineHeight() * 5).applyTo(commentText);

        commentText.addModifyListener(new ModifyListener()
        {

            @Override
            public void modifyText(ModifyEvent e)
            {
                dirty = true;
                firePropertyChange(PROP_DIRTY);
            }
        });

        commentTextSection.setClient(compositeClient);

        /* reply to, Reply To */
        Section replyToSection =
            toolkit.createSection(scrolledForm.getBody(), Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED
                | Section.CLIENT_INDENT);
        replyToSection.setText("Reply To");
        replyToSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        compositeClient = toolkit.createComposite(replyToSection, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(compositeClient);
        Text replyTo =
            toolkit.createText(compositeClient, c.getReplyTo() == null ? "" : c.getReplyTo().toString(), SWT.BORDER
                | SWT.SINGLE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(replyTo);

        replyTo.addModifyListener(new ModifyListener()
        {

            @Override
            public void modifyText(ModifyEvent e)
            {
                dirty = true;
                firePropertyChange(PROP_DIRTY);
            }
        });

        replyToSection.setClient(compositeClient);

        /* date, Date and time */
        Section dateSection =
            toolkit.createSection(scrolledForm.getBody(), Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED
                | Section.CLIENT_INDENT);
        dateSection.setText("Date and Time");

        dateSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 1, 2));

        compositeClient = toolkit.createComposite(dateSection, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compositeClient);

        Label dateLabel = toolkit.createLabel(compositeClient, "Date");
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(dateLabel);
        Label timeLabel = toolkit.createLabel(compositeClient, "Time");
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(timeLabel);

        final DateTime date = new DateTime(compositeClient, SWT.CALENDAR);
        GridDataFactory.fillDefaults().applyTo(date);
        toolkit.adapt(date);

        final DateTime time = new DateTime(compositeClient, SWT.TIME);
        toolkit.adapt(time);
        time.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
                // TODO Auto-generated method stub
            }

            public void widgetSelected(SelectionEvent e)
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, date.getDay());
                calendar.set(Calendar.MONTH, date.getMonth());
                calendar.set(Calendar.YEAR, date.getYear());
                calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
                calendar.set(Calendar.MINUTE, time.getMinutes());
                calendar.set(Calendar.SECOND, time.getSeconds());
                System.out.println(calendar.toString());
            }
        });

        date.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
                // TODO Auto-generated method stub
            }

            public void widgetSelected(SelectionEvent e)
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, date.getDay());
                calendar.set(Calendar.MONTH, date.getMonth());
                calendar.set(Calendar.YEAR, date.getYear());
                calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
                calendar.set(Calendar.MINUTE, time.getMinutes());
                calendar.set(Calendar.SECOND, time.getSeconds());
                System.out.println(calendar.toString());
            }
        });

        dateSection.setClient(compositeClient);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus()
    {
        scrolledForm.setFocus();
    }
}
