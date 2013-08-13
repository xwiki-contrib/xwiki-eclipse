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

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.storage.DataManager;
import org.xwiki.eclipse.ui.editors.contentassist.CompoundContentAssistProcessor;
import org.xwiki.eclipse.ui.editors.contentassist.MacroContentAssistProcessor;
import org.xwiki.eclipse.ui.editors.contentassist.VelocityVariableContentAssistProcessor;
import org.xwiki.eclipse.ui.editors.contentassist.XWikiHeadingContentAssistProcessor;
import org.xwiki.eclipse.ui.editors.contentassist.XWikiLinkContentAssistProcessor;
import org.xwiki.eclipse.ui.editors.contentassist.XWikiStyleContentAssistProcessor;
import org.xwiki.eclipse.ui.editors.contentassist.strategies.TableAutoEditStrategy;
import org.xwiki.eclipse.ui.editors.contentassist.strategies.VelocityAutoEditStrategy;
import org.xwiki.eclipse.ui.editors.contentassist.strategies.XWikiMarkupAutoEditStrategy;
import org.xwiki.eclipse.ui.editors.format.DefaultFormattingStrategy;
import org.xwiki.eclipse.ui.editors.format.XWikiFormattingStrategy;
import org.xwiki.eclipse.ui.editors.scanners.GroovyPartitionScanner;
import org.xwiki.eclipse.ui.editors.scanners.GroovyScanner;
import org.xwiki.eclipse.ui.editors.scanners.VelocityScanner;
import org.xwiki.eclipse.ui.editors.scanners.XWikiMarkupScanner;
import org.xwiki.eclipse.ui.editors.scanners.XWikiPartitionScanner;
import org.xwiki.eclipse.ui.editors.scanners.XWikiAdvancedPartitionScanner;

/**
 * @version $Id$
 */
public class XWikiSourceViewerConfiguration extends TextSourceViewerConfiguration
{
    private PageEditor pageEditor = null;

    public XWikiSourceViewerConfiguration()
    {
        super();
    }

    public XWikiSourceViewerConfiguration(PageEditor pageEditor)
    {
        super();
        this.pageEditor = pageEditor;
    }

    /**
     * This code is used to activate syntax coloring for each section detected by the XWikiPartitionner
     * The partitioner is activated in createDocument in PageDocumentProvider.java
     * @see PageDocumentProvider#createDocument(Object)
     */
    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
    {
        PresentationReconciler reconciler = new PresentationReconciler();
        reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

        /* Use the XWiki markup for tables and default content. */
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new XWikiMarkupScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        reconciler.setDamager(dr, XWikiPartitionScanner.XWIKI_TABLE);
        reconciler.setRepairer(dr, XWikiPartitionScanner.XWIKI_TABLE);

        /* Use the Velocity Scanner for velocity blocks */
        dr = new DefaultDamagerRepairer(new VelocityScanner());
        reconciler.setDamager(dr, XWikiPartitionScanner.VELOCITY);
        reconciler.setRepairer(dr, XWikiPartitionScanner.VELOCITY);

        /* Use the Groovy Scanner for groovy blocks */
        dr = new DefaultDamagerRepairer(new GroovyScanner());
        reconciler.setDamager(dr, XWikiPartitionScanner.GROOVY);
        reconciler.setRepairer(dr, XWikiPartitionScanner.GROOVY);

        /* Use a uniform style for html blocks. */
        RuleBasedScanner codeScanner = new RuleBasedScanner();
        codeScanner.setDefaultReturnToken(new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.HTML)));
        dr = new DefaultDamagerRepairer(codeScanner);
        reconciler.setDamager(dr, XWikiPartitionScanner.XWIKI_HTML);
        reconciler.setRepairer(dr, XWikiPartitionScanner.XWIKI_HTML);

        /* Use a uniform style for code blocks. */
        codeScanner = new RuleBasedScanner();
        codeScanner.setDefaultReturnToken(new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.CODE)));
        dr = new DefaultDamagerRepairer(codeScanner);
        reconciler.setDamager(dr, XWikiPartitionScanner.XWIKI_CODE);
        reconciler.setRepairer(dr, XWikiPartitionScanner.XWIKI_CODE);

        /* Use a uniform style for pre blocks. */
        RuleBasedScanner preScanner = new RuleBasedScanner();
        preScanner.setDefaultReturnToken(new Token(Preferences.getDefault().getTextAttribute(Preferences.Style.CODE)));
        dr = new DefaultDamagerRepairer(preScanner);
        reconciler.setDamager(dr, XWikiPartitionScanner.XWIKI_PRE);
        reconciler.setRepairer(dr, XWikiPartitionScanner.XWIKI_PRE);

        RuleBasedScanner javadocScanner = new GroovyScanner();
        dr = new DefaultDamagerRepairer(javadocScanner);
        reconciler.setDamager(dr, GroovyPartitionScanner.GROOVY_DEFAULT);
        reconciler.setRepairer(dr, GroovyPartitionScanner.GROOVY_DEFAULT);

        return reconciler;
    }

    /**
     * Code to handle auto-completion
     */
    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer)
    {
        ContentAssistant contentAssistant = new ContentAssistant();

        CompoundContentAssistProcessor compoundContentAssistProcessor;

        if (pageEditor == null) {
            return contentAssistant;
        }

        PageEditorInput input = (PageEditorInput) pageEditor.getEditorInput();
        XWikiEclipsePage currentPage = input.getPage();
        DataManager dataManager = currentPage.getDataManager();

        /* Content assist for standard paragraphs and tables */
        compoundContentAssistProcessor = new CompoundContentAssistProcessor();
        compoundContentAssistProcessor.addContentAssistProcessor(new XWikiLinkContentAssistProcessor(dataManager));
        compoundContentAssistProcessor
            .addContentAssistProcessor(new VelocityVariableContentAssistProcessor(pageEditor));
        compoundContentAssistProcessor.addContentAssistProcessor(new MacroContentAssistProcessor(pageEditor));
        compoundContentAssistProcessor.addContentAssistProcessor(new XWikiHeadingContentAssistProcessor());
        compoundContentAssistProcessor.addContentAssistProcessor(new XWikiStyleContentAssistProcessor());
        contentAssistant.setContentAssistProcessor(compoundContentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
        contentAssistant.setContentAssistProcessor(compoundContentAssistProcessor, XWikiPartitionScanner.XWIKI_TABLE);

        /* Content assist for Velocity partitions */
        compoundContentAssistProcessor = new CompoundContentAssistProcessor();
        compoundContentAssistProcessor
            .addContentAssistProcessor(new VelocityVariableContentAssistProcessor(pageEditor));
        compoundContentAssistProcessor.addContentAssistProcessor(new MacroContentAssistProcessor(pageEditor));
        contentAssistant.setContentAssistProcessor(compoundContentAssistProcessor, XWikiPartitionScanner.VELOCITY);

        contentAssistant.enableAutoActivation(true);

        return contentAssistant;
    }

    @Override
    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
    {
        if (contentType.equals(IDocument.DEFAULT_CONTENT_TYPE)) {
            return new IAutoEditStrategy[] {new XWikiMarkupAutoEditStrategy()};
        } else if (contentType.equals(XWikiPartitionScanner.XWIKI_TABLE)) {
            return new IAutoEditStrategy[] {new TableAutoEditStrategy(), new XWikiMarkupAutoEditStrategy()};
        } else if (contentType.equals(XWikiPartitionScanner.VELOCITY)) {
            return new IAutoEditStrategy[] {new VelocityAutoEditStrategy()};
        }

        return super.getAutoEditStrategies(sourceViewer, contentType);
    }

    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
    {
        return new String[] {IDocument.DEFAULT_CONTENT_TYPE, XWikiPartitionScanner.XWIKI_TABLE,
        XWikiPartitionScanner.VELOCITY, XWikiPartitionScanner.XWIKI_CODE, XWikiPartitionScanner.XWIKI_PRE};
    }
    
    /** 
     * Activated the reconcilier using XWikiReconcilier
     * This is necessary for Code Folding
     * 
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IReconciler getReconciler(ISourceViewer sourceViewer)
    {
        XWikiReconcilierStrategy strategy = new XWikiReconcilierStrategy();
        strategy.setEditor(pageEditor);
        
        MonoReconciler reconciler = new MonoReconciler(strategy,false);
        
        return reconciler;
    }
    
    /**
     * Activated  the XWikiFormattingStrategy to implement Code Indentation
     * Code Indentation uses XWikiAdvancedPartitionScanner
     */
    public IContentFormatter getContentFormatter(ISourceViewer sourceViewer)
    {
    	System.out.println("In getContentFormatter");
        ContentFormatter formatter = new ContentFormatter();
        XWikiFormattingStrategy xwikiFormattingStrategy = new XWikiFormattingStrategy(false);
        XWikiFormattingStrategy velocityStrategy1 = new XWikiFormattingStrategy(true);
        
        formatter.setFormattingStrategy(xwikiFormattingStrategy, IDocument.DEFAULT_CONTENT_TYPE);
        formatter.setFormattingStrategy(velocityStrategy1, XWikiAdvancedPartitionScanner.VELOCITY);
        formatter.setFormattingStrategy(velocityStrategy1, XWikiAdvancedPartitionScanner.VELOCITY_FOREACH);
        formatter.setFormattingStrategy(velocityStrategy1, XWikiAdvancedPartitionScanner.VELOCITY_IF);
        formatter.setFormattingStrategy(velocityStrategy1, XWikiAdvancedPartitionScanner.VELOCITY_MACRO);
        return formatter;
    }
}
