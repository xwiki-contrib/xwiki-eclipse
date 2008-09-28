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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.xwiki.eclipse.core.CoreLog;
import org.xwiki.eclipse.core.CorePlugin;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.XWikiEclipseException;
import org.xwiki.eclipse.core.model.XWikiEclipseObject;
import org.xwiki.eclipse.core.model.XWikiEclipsePage;
import org.xwiki.eclipse.core.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.core.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.core.notifications.CoreEvent;
import org.xwiki.eclipse.core.notifications.ICoreEventListener;
import org.xwiki.eclipse.core.notifications.NotificationManager;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;
import org.xwiki.eclipse.ui.dialogs.PageConflictDialog;
import org.xwiki.eclipse.ui.utils.UIUtils;
import org.xwiki.xmlrpc.model.XWikiExtendedId;
import org.xwiki.xmlrpc.model.XWikiPage;

public class PageEditor extends TextEditor implements ICoreEventListener
{
    public static final String ID = "org.xwiki.eclipse.ui.editors.PageEditor";

    private Form form;

    private boolean conflictDialogDisplayed;

    private EditConflictAction editConflictAction;

    private class EditConflictAction extends Action
    {
        public EditConflictAction()
        {
            super("org.xwiki.eclipse.ui.pageEditor.editConflict");
            setText("Edit conflict");
            setImageDescriptor(UIPlugin.getImageDescriptor(UIConstants.CONFLICT_ICON));

        }

        @Override
        public void run()
        {
            handleConflict();
        }
    }

    public PageEditor()
    {
        setDocumentProvider(new PageDocumentProvider(this));
    }

    @Override
    public void dispose()
    {
        NotificationManager.getDefault().removeListener(this);
        super.dispose();
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        super.init(site, input);
        NotificationManager.getDefault().addListener(
            this,
            new CoreEvent.Type[] {CoreEvent.Type.DATA_MANAGER_CONNECTED, CoreEvent.Type.OBJECT_STORED,
            CoreEvent.Type.OBJECT_REMOVED, CoreEvent.Type.REFRESH,
            CoreEvent.Type.PAGE_REMOVED, CoreEvent.Type.SPACE_REMOVED, CoreEvent.Type.DATA_MANAGER_UNREGISTERED});
    }

    @Override
    public void createPartControl(Composite parent)
    {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createForm(parent);
        toolkit.decorateFormHeading(form);

        editConflictAction = new EditConflictAction();

        GridLayoutFactory.fillDefaults().spacing(0, 0).applyTo(form.getBody());

        Composite editorComposite = new Composite(form.getBody(), SWT.NONE);
        editorComposite.setLayout(new FillLayout());
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(editorComposite);
        super.createPartControl(editorComposite);

        PageEditorInput pageEditorInput = (PageEditorInput) getEditorInput();
        if (pageEditorInput.isReadOnly()) {
            getSourceViewer().getTextWidget().setBackground(new Color(Display.getDefault(), 248, 248, 248));
        }

        updateInfo();
    }

    @Override
    protected void doSetInput(IEditorInput input) throws CoreException
    {
        if (!(input instanceof PageEditorInput)) {
            throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Invalid input for editor"));
        }

        PageEditorInput pageEditorInput = (PageEditorInput) input;

        ISourceViewer sourceViewer = getSourceViewer();
        if (sourceViewer != null) {
            int caretOffset = sourceViewer.getTextWidget().getCaretOffset();
            int topPixel = sourceViewer.getTextWidget().getTopPixel();
            super.doSetInput(pageEditorInput);
            sourceViewer.getTextWidget().setCaretOffset(caretOffset);
            sourceViewer.getTextWidget().setTopPixel(topPixel);

            if (!conflictDialogDisplayed) {
                handleConflict();
            }
        } else {
            super.doSetInput(pageEditorInput);

            if (pageEditorInput.getPage().getDataManager().isInConflict(pageEditorInput.getPage().getData().getId())) {
                UIUtils
                    .showMessageDialog(
                        getSite().getShell(),
                        "Page is still in conflict.",
                        "The page is still in conflict. In order to handle the conflict click on icon in the upper left corner of the title bar. You may continue to edit the page, changes will be saved locally until you decide to solve the conflict.");

                conflictDialogDisplayed = true;
            }
        }

        updateInfo();
    }

    private void updateInfo()
    {
        if (form != null) {
            PageEditorInput input = (PageEditorInput) getEditorInput();
            if (input != null) {
                XWikiEclipsePage page = input.getPage();

                int version = page.getData().getVersion();
                int minorVersion = page.getData().getMinorVersion();

                /* Compatibility with XWiki 1.3 */
                if (version > 65536) {
                    int temp = version;
                    version = temp >> 16;
                    minorVersion = temp & 0xFFFF;
                }

                /* If the page id does not have a '.' then we are dealing with confluence ids */
                if (page.getData().getId().indexOf('.') != -1) {
                    XWikiExtendedId extendedId = new XWikiExtendedId(page.getData().getId());

                    String language = "Default";
                    if (page.getData().getLanguage() != null) {
                        if (!page.getData().getLanguage().equals("")) {
                            language = page.getData().getLanguage();
                        }
                    }

                    form.setText(String.format("%s version %s.%s [Language: %s] %s", extendedId.getBasePageId(),
                        version, minorVersion, language, input.isReadOnly() ? "[READONLY]" : ""));
                } else {
                    form.setText(String.format("%s version %s.%s %s", page.getData().getTitle(), version, minorVersion,
                        input.isReadOnly() ? "[READONLY]" : ""));
                }
            }

            if (input.getPage().getDataManager().isInConflict(input.getPage().getData().getId())) {
                boolean editConlictActionFound = false;
                for (IContributionItem contributionItem : form.getToolBarManager().getItems()) {
                    if (contributionItem instanceof ActionContributionItem) {
                        ActionContributionItem actionContributionItem = (ActionContributionItem) contributionItem;
                        if (actionContributionItem.getAction().equals(editConflictAction)) {
                            editConlictActionFound = true;
                        }
                    }
                }

                if (!editConlictActionFound) {
                    form.getToolBarManager().add(editConflictAction);
                    form.updateToolBar();
                }

                form.setMessage("Page is in conflict.", IMessageProvider.WARNING);
            } else {
                form.getToolBarManager().removeAll();
                form.updateToolBar();
                form.setMessage(input.getPage().getDataManager().getName());
            }

        }
    }

    private void handleConflict()
    {
        PageEditorInput input = (PageEditorInput) getEditorInput();
        XWikiEclipsePage currentPage = input.getPage();
        DataManager dataManager = currentPage.getDataManager();

        if (dataManager.isInConflict(currentPage.getData().getId())) {
            try {
                XWikiEclipsePage conflictingPage = dataManager.getConflictingPage(currentPage.getData().getId());

                XWikiEclipsePage conflictAncestorPage =
                    dataManager.getConflictAncestorPage(currentPage.getData().getId());

                PageConflictDialog compareDialog =
                    new PageConflictDialog(Display.getDefault().getActiveShell(), currentPage, conflictingPage,
                        conflictAncestorPage);
                int result = compareDialog.open();

                conflictDialogDisplayed = true;

                switch (result) {
                    case PageConflictDialog.ID_USE_LOCAL:
                        XWikiPage newPage = new XWikiPage(conflictingPage.getData().toRawMap());
                        newPage.setContent(currentPage.getData().getContent());
                        dataManager.clearConflictingStatus(newPage.getId());
                        setInput(new PageEditorInput(new XWikiEclipsePage(dataManager, newPage), input.isReadOnly()));

                        /* Force the editor to be dirty */
                        getDocumentProvider().getDocument(getEditorInput()).set(
                            getDocumentProvider().getDocument(getEditorInput()).get());

                        conflictDialogDisplayed = false;
                        break;
                    case PageConflictDialog.ID_USE_REMOTE:
                        dataManager.clearConflictingStatus(conflictingPage.getData().getId());
                        setInput(new PageEditorInput(conflictingPage, input.isReadOnly()));

                        /* Force the editor to be dirty */
                        getDocumentProvider().getDocument(getEditorInput()).set(
                            getDocumentProvider().getDocument(getEditorInput()).get());

                        conflictDialogDisplayed = false;
                        break;
                    case PageConflictDialog.ID_MERGE:
                        newPage = new XWikiPage(conflictingPage.getData().toRawMap());
                        newPage.setContent(String.format(">>>>>>>LOCAL>>>>>>>>%s\n\n\n>>>>>>>REMOTE>>>>>>>>\n%s",
                            currentPage.getData().getContent(), conflictingPage.getData().getContent()));
                        dataManager.clearConflictingStatus(newPage.getId());
                        setInput(new PageEditorInput(new XWikiEclipsePage(dataManager, newPage), input.isReadOnly()));

                        /* Force the editor to be dirty */
                        getDocumentProvider().getDocument(getEditorInput()).set(
                            getDocumentProvider().getDocument(getEditorInput()).get());

                        conflictDialogDisplayed = false;
                        break;
                    default:
                        break;
                }
            } catch (XWikiEclipseException e) {
                CoreLog.logError("Error while handling conflict", e);
            }
        }
    }

    @Override
    public void setFocus()
    {
        PageEditorInput input = (PageEditorInput) getEditorInput();
        XWikiEclipsePage page = input.getPage();

        NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_SELECTED, this, page);

        super.setFocus();
    }

    public void handleCoreEvent(CoreEvent event)
    {
        final PageEditorInput input = (PageEditorInput) getEditorInput();
        XWikiEclipsePage page = input.getPage();
        String targetPageId = null;
        DataManager dataManager = null;
        if (event.getSource() instanceof DataManager){
            dataManager = (DataManager) event.getSource();
        }
        
        boolean updatePage = false;

        switch (event.getType()) {
            case OBJECT_STORED:
                /*
                 * When objects are modified, the version number of the page is incremented. Here, we retrieve the
                 * current page. If the version numbers are not equal and the editor is not dirty then it means that an
                 * object of the page has been modified, but not the page content. So we basically update the page
                 * content. If the editor is dirty then we do not do nothing.
                 */
                XWikiEclipseObject object = (XWikiEclipseObject) event.getData();
                targetPageId = object.getData().getPageId();

                updatePage = page.getDataManager().equals(dataManager) && page.getData().getId().equals(targetPageId);

                break;
                
            case OBJECT_REMOVED:
                targetPageId = ((XWikiEclipseObject)event.getData()).getPageSummary().getId();

                updatePage = page.getDataManager().equals(dataManager) && page.getData().getId().equals(targetPageId);

                break;
                
            case DATA_MANAGER_CONNECTED:

                updatePage = page.getDataManager().equals(dataManager);

                break;
                
            case REFRESH:
                Object data = event.getData();
                if (data instanceof DataManager || data instanceof XWikiEclipsePageSummary || 
                    data instanceof XWikiEclipseSpaceSummary){
                    
                    // Check if this refresh event was triggered for the page managed by this editor
                    if (data instanceof XWikiEclipsePageSummary){
                        XWikiEclipsePageSummary refreshedPageSummary = (XWikiEclipsePageSummary) data;
                        if (!refreshedPageSummary.getXWikiEclipseId().equals(page.getSummary().getXWikiEclipseId()))
                            return;
                    } else  
                        
                    if (data instanceof DataManager){
                        DataManager aDataManager = (DataManager) data;
                        
                        if (!aDataManager.equals(page.getDataManager()))
                            return;
                    } else
                        
                    if (data instanceof XWikiEclipseSpaceSummary){
                        XWikiEclipseSpaceSummary space = (XWikiEclipseSpaceSummary) data;
                        
                        if (!space.getDataManager().equals(page.getDataManager()) ||
                            !space.getData().getKey().equals(page.getData().getSpace()))
                            return;
                    }
                    
                    // Check if the user was in the middle of something.
                    if (isDirty()){
                        MessageBox messageBox = new MessageBox(Display.getCurrent().getShells()[0], SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);
                        messageBox.setMessage(String.format("Refreshing the page %s will overwrite your current work on it. Do you wish to save it?", page.getData().getId()));
                        messageBox.setText("Save work");
                        this.setFocus();
                        
                        int result = messageBox.open();
                        if (result == SWT.YES) {
                            this.doSave(null);
                            
                            // let the conflict resolution solve any conflict if it is the case.
                            return;
                        }else if(result == SWT.CANCEL){
                            return;
                        }
                        
                        try{
                            page.getDataManager().clearPageStatus(page.getData().getId());
                        }catch(Exception ex){
                            // ignore
                        }
                        
                        // we are here if the user said no
                    }
                    
                    doRevertToSaved();
                    updatePage = true;
                }
                break;
                
            case PAGE_REMOVED:
                XWikiEclipsePage aPage = (XWikiEclipsePage) event.getData();
                
                if (aPage.getXWikiEclipseId().equals(page.getXWikiEclipseId())){
                    // The page being edited has been deleted.
                    this.close(false);
                }
                
                break;
                
            case SPACE_REMOVED:
                XWikiEclipseSpaceSummary aSpace = (XWikiEclipseSpaceSummary) event.getData();
                
                if (aSpace.getDataManager().equals(page.getDataManager()) && 
                    aSpace.getData().getKey().equals(page.getData().getSpace())){
                    // The space that the page being edited belonged to has been deleted.
                    this.close(false);
                }
                
                break;
                
            case DATA_MANAGER_UNREGISTERED:

                DataManager aDataManager = (DataManager) event.getData();
                if (aDataManager.equals(page.getDataManager())){
                    // The connection that the page being edited belonged to has been deleted.
                    this.close(false);
                }
                
                break;
        }

        try {
            if (updatePage) {
                if (!isDirty()) {
                    final XWikiEclipsePage newPage = page.getDataManager().getPage(page.getData().getId());

                    if (page.getData().getVersion() != newPage.getData().getVersion()) {
                        /*
                         * If we are here then the editor is not dirty and the page versions differ. So we update the
                         * page being edited. This may happen when an object associated to a page is stored or when
                         * pages are synchronized when a data manager is connected.
                         */
                        final ISourceViewer sourceViewer = getSourceViewer();
                        if (sourceViewer != null) {
                            Display.getDefault().syncExec(new Runnable()
                            {
                                public void run()
                                {
                                    int caretOffset = sourceViewer.getTextWidget().getCaretOffset();
                                    int topPixel = sourceViewer.getTextWidget().getTopPixel();
                                    try {
                                        doSetInput(new PageEditorInput(newPage, input.isReadOnly()));
                                    } catch (CoreException e) {
                                        CoreLog.logError("Error while handling XWiki Eclipse event", e);
                                    }
                                    sourceViewer.getTextWidget().setCaretOffset(caretOffset);
                                    sourceViewer.getTextWidget().setTopPixel(topPixel);
                                    updateInfo();
                                }

                            });

                        }

                    }
                }
            }
        } catch (XWikiEclipseException e) {
            CoreLog.logError("Error while handling XWiki Eclipse event", e);
        }

    }
}
