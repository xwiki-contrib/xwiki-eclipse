package org.xwiki.eclipse.ui.editors.contentassist;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.xwiki.eclipse.ui.editors.XWikiApiType;
import org.xwiki.eclipse.ui.editors.utils.XWikiApiTemplateManager;

public class XWikiApiCompletionProcessor extends TemplateCompletionProcessor
{
    XWikiApiType xwikiApiType;

    public XWikiApiCompletionProcessor(XWikiApiType xwikiApiType)
    {
        super();
        this.xwikiApiType = xwikiApiType;
    }

    @Override
    protected TemplateContextType getContextType(ITextViewer arg0, IRegion arg1)
    {
        return new TemplateContextType("org.xwiki.eclipse.ui.editors.velocity.xwikiapi");
    }

    @Override
    protected Image getImage(Template arg0)
    {
        return null;
    }

    @Override
    protected Template[] getTemplates(String arg0)
    {
        Template[] result = XWikiApiTemplateManager.getDefault().getXWikiCompletionTemplates(xwikiApiType);
        if (result != null) {
            return result;
        }

        return new Template[0];
    }
}
