/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
package org.xwiki.eclipse;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;

/**
 * This class implements a publish-subscribe notification hub for dispatching events.
 */
public class XWikiEclipseNotificationCenter
{
    private Map<XWikiEclipseEvent, ListenerList> eventyTypeToListenersMapping;

    private static XWikiEclipseNotificationCenter instance;

    private XWikiEclipseNotificationCenter()
    {
        eventyTypeToListenersMapping = new HashMap<XWikiEclipseEvent, ListenerList>();
    }

    /**
     * @return The shared instance.
     */
    public static XWikiEclipseNotificationCenter getDefault()
    {
        if (instance == null) {
            instance = new XWikiEclipseNotificationCenter();
        }

        return instance;
    }

    /**
     * Register a listener for a given event type.
     * 
     * @param eventType The event type.
     * @param listener The associated listener.
     */
    public void addListener(XWikiEclipseEvent eventType, IXWikiEclipseEventListener listener)
    {
        ListenerList listenerList = eventyTypeToListenersMapping.get(eventType);
        if (listenerList == null) {
            listenerList = new ListenerList();
            eventyTypeToListenersMapping.put(eventType, listenerList);
        }

        listenerList.add(listener);
    }

    /**
     * Unregister a listener for a given event type.
     * 
     * @param eventType The event type.
     * @param listener The associated listener.
     */
    public void removeListener(XWikiEclipseEvent eventType, IXWikiEclipseEventListener listener)
    {
        ListenerList listenerList = eventyTypeToListenersMapping.get(eventType);
        if (listenerList != null) {
            listenerList.remove(listener);
        }
    }

    /**
     * Dispatch an event to all the listener registered for it.
     * 
     * @param sender The object who sends the event.
     * @param eventType The event type.
     * @param data Additional data to be provided to the listener (depends on the event type. See {@link XWikiEclipseEvent}
     */
    public void fireEvent(Object sender, XWikiEclipseEvent eventType, Object data)
    {
        ListenerList listenerList = eventyTypeToListenersMapping.get(eventType);
        if (listenerList != null) {
            Object[] listeners = listenerList.getListeners();
            for (int i = 0; i < listeners.length; ++i) {
                ((IXWikiEclipseEventListener) listeners[i]).handleEvent(sender, eventType, data);
            }
        }
    }
}
