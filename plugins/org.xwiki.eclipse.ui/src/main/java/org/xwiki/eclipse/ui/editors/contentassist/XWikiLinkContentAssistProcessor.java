package org.xwiki.eclipse.ui.editors.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.ui.UIPlugin;
import org.xwiki.eclipse.ui.editors.utils.Utils;

/**
 * @author fmancinelli, venkatesh, malaka
 */
public class XWikiLinkContentAssistProcessor implements IContentAssistProcessor
{
    private DataManager dataManager;

    public XWikiLinkContentAssistProcessor(DataManager dataManager)
    {
        this.dataManager = dataManager;
    }

    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
    {
        List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

        IDocument document = viewer.getDocument();

        String linkPrefix = Utils.getPrefix(document, offset, '[', "]");

        if (linkPrefix != null) {
            List<XWikiEclipsePageSummary> pageSummaries =
                UIPlugin.getDefault().getAllPageSummariesForDataManager(dataManager);

            for (XWikiEclipsePageSummary pageSummary : pageSummaries) {
                String pageId = pageSummary.getData().getId();
                if (pageId.startsWith(linkPrefix)) {
                    result.add(new CompletionProposal(pageId, offset - linkPrefix.length(), linkPrefix.length(), pageId
                        .length(), null, pageId, null, null));
                }
            }
        }

        if (result.size() > 0) {
            return result.toArray(new ICompletionProposal[result.size()]);
        }

        return null;
    }

    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
    {
        return null;
    }

    public char[] getCompletionProposalAutoActivationCharacters()
    {
        return null;
    }

    public char[] getContextInformationAutoActivationCharacters()
    {
        return null;
    }

    public IContextInformationValidator getContextInformationValidator()
    {
        return null;
    }

    public String getErrorMessage()
    {
        return null;
    }

}
