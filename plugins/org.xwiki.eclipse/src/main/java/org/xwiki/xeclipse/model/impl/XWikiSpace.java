package org.xwiki.xeclipse.model.impl;

import java.util.Collection;
import java.util.Map;

import org.codehaus.swizzle.confluence.Space;
import org.xwiki.xeclipse.model.IXWikiPage;
import org.xwiki.xeclipse.model.IXWikiSpace;
import org.xwiki.xeclipse.model.XWikiConnectionException;

/**
 * Implementation of {@link IXWikiSpace}
 */
public class XWikiSpace implements IXWikiSpace
{
    private AbstractXWikiConnection connection;

    private Space space;

    private String key;

    /**
     * Constructor.
     * 
     * @param connection The connection this instance is linked to.
     * @param spaceSummary The raw space object provided by the remote XWiki instance containing the
     *            information.
     */
    public XWikiSpace(AbstractXWikiConnection connection, String key, Map properties)
    {
        this.connection = connection;
        this.key = key;
        space = new Space(properties);
    }

    public String getDescription()
    {
        String result = space.getDescription();
        if(result != null) {
            return result;
        }
        
        getFullSpaceInformation();
        
        return space.getDescription();
    }

    public String getHomePage()
    {
        String result = space.getHomepage();
        if(result != null) {
            return result;
        }
        
        getFullSpaceInformation();
        
        return space.getHomepage();
    }

    public String getKey()
    {
        String result = space.getKey();
        if(result != null) {
            return result;
        }
        
        getFullSpaceInformation();
        
        return space.getKey();
    }

    public String getName()
    {
        String result = space.getName();
        if(result != null) {
            return result;
        }
        
        getFullSpaceInformation();
        
        return space.getName();
    }

    public Collection<IXWikiPage> getPages() throws XWikiConnectionException
    {
        return connection.getPages(space.getKey());
    }

    public String getType()
    {
        String result = space.getType();
        if(result != null) {
            return result;
        }
        
        getFullSpaceInformation();
        
        return space.getType();
    }

    public String getUrl()
    {
        String result = space.getUrl();
        if(result != null) {
            return result;
        }
        
        getFullSpaceInformation();
        
        return space.getUrl();
    }
    
    private void getFullSpaceInformation()
    {
        Space space = connection.getRawSpace(key);
        if(space != null) {
            this.space = space;    
        }
                
    }
}
