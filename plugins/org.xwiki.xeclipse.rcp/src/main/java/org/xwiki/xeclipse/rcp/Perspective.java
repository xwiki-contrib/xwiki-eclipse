package org.xwiki.xeclipse.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
	    layout.addView("org.xwiki.xeclipse.views.XWikiExplorer", IPageLayout.LEFT, 0.4f,
            layout.getEditorArea());
	}
}
