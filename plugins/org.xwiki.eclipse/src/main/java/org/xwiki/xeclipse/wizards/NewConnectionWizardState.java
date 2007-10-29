package org.xwiki.xeclipse.wizards;

public class NewConnectionWizardState
{
    private String serverUrl;
    private String userName;
    private String password;
    private boolean useProxy;
    private String proxyHost;
    private String proxyPort;
    
    public String getServerUrl()
    {
        return serverUrl;
    }
    public void setServerUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }
    public String getUserName()
    {
        return userName;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public boolean isUseProxy()
    {
        return useProxy;
    }
    public void setUseProxy(boolean useProxy)
    {
        this.useProxy = useProxy;
    }
    public String getProxyHost()
    {
        return proxyHost;
    }
    public void setProxyHost(String proxyHost)
    {
        this.proxyHost = proxyHost;
    }
    public String getProxyPort()
    {
        return proxyPort;
    }
    public void setProxyPort(String proxyPort)
    {
        this.proxyPort = proxyPort;
    }
    
    

}
