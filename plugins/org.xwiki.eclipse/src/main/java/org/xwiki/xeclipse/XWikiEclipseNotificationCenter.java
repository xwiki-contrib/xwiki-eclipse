package org.xwiki.xeclipse;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;

public class XWikiEclipseNotificationCenter
{
    private Map<XWikiEclipseEvent, ListenerList> eventyTypeToListenersMapping;
    private static XWikiEclipseNotificationCenter instance;

    private XWikiEclipseNotificationCenter()
    {                
        eventyTypeToListenersMapping = new HashMap<XWikiEclipseEvent, ListenerList>();
    }
    
    public static XWikiEclipseNotificationCenter getDefault() {
        if(instance == null) {
            instance = new XWikiEclipseNotificationCenter();
        }
        
        return instance;
    }

    public void addListener(XWikiEclipseEvent eventType, Object listener)
    {
        ListenerList listenerList = eventyTypeToListenersMapping.get(eventType);
        if(listenerList == null) {
            listenerList = new ListenerList();
            eventyTypeToListenersMapping.put(eventType, listenerList);
        }
        
        listenerList.add(listener);
    }
    
    public void removeListener(XWikiEclipseEvent eventType, IXWikiEclipseEventListener listener) {
        ListenerList listenerList = eventyTypeToListenersMapping.get(eventType);
        if(listenerList != null) {
            listenerList.remove(listener);
        }
    }
    
    public void fireEvent(Object sender, XWikiEclipseEvent eventType, Object data) {
        ListenerList listenerList = eventyTypeToListenersMapping.get(eventType);
        if(listenerList != null) {
            Object[] listeners = listenerList.getListeners();
            for (int i = 0; i < listeners.length; ++i) {
               ((IXWikiEclipseEventListener) listeners[i]).handleEvent(sender, eventType, data);
            }
        }
    }
}
