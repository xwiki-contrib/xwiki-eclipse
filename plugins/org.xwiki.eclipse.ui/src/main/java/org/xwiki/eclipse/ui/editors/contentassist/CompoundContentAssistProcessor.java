package org.xwiki.eclipse.ui.editors.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * A content assist processor that re-groups other assist processor and queries them for getting proposals.
 * 
 * @author fmancinelli (based on work of malaka, venkatesh)
 */
public class CompoundContentAssistProcessor implements IContentAssistProcessor
{
    private List<IContentAssistProcessor> contentAssistProcessors;

    public CompoundContentAssistProcessor()
    {
        contentAssistProcessors = new ArrayList<IContentAssistProcessor>();
    }

    public void addContentAssistProcessor(IContentAssistProcessor contentAssistProcessor)
    {
        contentAssistProcessors.add(contentAssistProcessor);
    }

    public void removeContentAssistProcessor(IContentAssistProcessor contentAssistProcessor)
    {
        contentAssistProcessors.remove(contentAssistProcessor);
    }

    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
    {
        /* Query all the registered assist processors and collect all the returned proposals. */
        List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
        for (IContentAssistProcessor contentAssistProcessor : contentAssistProcessors) {
            ICompletionProposal[] proposals = contentAssistProcessor.computeCompletionProposals(viewer, offset);
            if (proposals != null && proposals.length > 0) {
                for (ICompletionProposal proposal : proposals) {
                    result.add(proposal);
                }
            }
        }

        return result.toArray(new ICompletionProposal[result.size()]);
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
