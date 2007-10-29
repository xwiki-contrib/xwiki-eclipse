package org.xwiki.xeclipse.model.impl;

public class XWikiDAOException extends Exception
{  
    private static final long serialVersionUID = -1232028292621663409L;

    public XWikiDAOException()
    {
        super();
    }
    
    public XWikiDAOException(String message) {
        super(message);
    }
    
    public XWikiDAOException(Throwable throwable) {
        super(throwable);
    }
    
    public XWikiDAOException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
