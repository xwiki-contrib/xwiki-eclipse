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

public class XWikiEclipseNotificationCenter
{
    private Map<XWikiEclipseEvent, ListenerList> eventyTypeToListenersMapping;

    private static XWikiEclipseNotificationCenter instance;

    private XWikiEclipseNotificationCenter()
    {
        eventyTypeToListenersMapping = new HashMap<XWikiEclipseEvent, ListenerList>();
    }

    public static XWikiEclipseNotificationCenter getDefault()
    {
        if (instance == null) {
            instance = new XWikiEclipseNotificationCenter();
        }

        return instance;
    }

    public void addListener(XWikiEclipseEvent eventType, Object listener)
    {
        ListenerList listenerList = eventyTypeToListenersMapping.get(eventType);
        if (listenerList == null) {
            listenerList = new ListenerList();
            eventyTypeToListenersMapping.put(eventType, listenerList);
        }

        listenerList.add(listener);
    }

    public void removeListener(XWikiEclipseEvent eventType, IXWikiEclipseEventListener listener)
    {
        ListenerList listenerList = eventyTypeToListenersMapping.get(eventType);
        if (listenerList != null) {
            listenerList.remove(listener);
        }
    }

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
