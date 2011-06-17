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
package org.xwiki.eclipse.core.notifications;

import org.eclipse.core.runtime.Assert;

/**
 * A class representing an event generated by the XWiki Eclipse Core component.
 * 
 * @version $Id$
 */
public class CoreEvent
{
    /**
     * An enumeration containing the possible events.
     */
    public static enum Type
    {
        DATA_MANAGER_REGISTERED, DATA_MANAGER_UNREGISTERED, DATA_MANAGER_CONNECTED, DATA_MANAGER_DISCONNECTED, PAGE_STORED, OBJECT_STORED, PAGE_REMOVED, OBJECT_REMOVED, PAGE_SELECTED, OBJECT_SELECTED, REFRESH, PAGE_RENAMED, SPACE_REMOVED;
    }

    /**
     * The event type.
     */
    private Type type;

    /**
     * The object that generated the event.
     */
    private Object source;

    /**
     * Additional data associated to this event.
     */
    private Object data;

    /**
     * Constructor.
     * 
     * @param type The event type.
     * @param source The object that generated the event.
     * @param data Additional data associated to this event.
     */
    public CoreEvent(Type type, Object source, Object data)
    {
        Assert.isNotNull(type);
        this.type = type;

        Assert.isNotNull(source);
        this.source = source;

        this.data = data;
    }

    public Type getType()
    {
        return type;
    }

    public Object getSource()
    {
        return source;
    }

    public Object getData()
    {
        return data;
    }
}
