package org.xwiki.eclipse.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
	    layout.addView("org.xwiki.eclipse.views.XWikiExplorer", IPageLayout.LEFT, 0.30f,
            layout.getEditorArea());
	    
	    layout.addView("org.xwiki.eclipse.views.XWikiPagePreview", IPageLayout.BOTTOM, 0.60f,
            layout.getEditorArea());	    	    
	}
}
