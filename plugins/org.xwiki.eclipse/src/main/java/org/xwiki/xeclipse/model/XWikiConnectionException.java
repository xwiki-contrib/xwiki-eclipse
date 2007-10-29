package org.xwiki.xeclipse.model;

public class XWikiConnectionException extends Exception
{
    private static final long serialVersionUID = -2193452805890817846L;

    public XWikiConnectionException()
    {
        super();
    }
    
    public XWikiConnectionException(String message) {
        super(message);
    }
    
    public XWikiConnectionException(Throwable throwable) {
        super(throwable);
    }
    
    public XWikiConnectionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
