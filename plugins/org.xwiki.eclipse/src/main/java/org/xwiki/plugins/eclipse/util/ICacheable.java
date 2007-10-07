package org.xwiki.plugins.eclipse.util;

import java.io.Serializable;

import org.eclipse.core.runtime.IPath;

/**
 * All model objects extend this class (XWikiPage, XWikiSpace and XWikiConnection)
 * Handles local-cache management.
 */
public interface ICacheable extends Serializable
{
    /**
     * @return The location where this object is cached.
     * 
     */
    public IPath getCachePath();
    
    /**
     * @return Whether we're online or not
     */
    public boolean isOffline();
        
}
