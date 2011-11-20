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
package org.xwiki.eclipse.ui.editors;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.xwiki.eclipse.core.CoreLog;
import org.xwiki.eclipse.model.XWikiEclipseComment;
import org.xwiki.eclipse.ui.UIConstants;

/**
 * @version $Id$
 */
public class CommentEditorInput implements IEditorInput
{
    private XWikiEclipseComment comment;

    private Command command = null;

    private Action action = null;

    public CommentEditorInput(XWikiEclipseComment comment, Object o)
    {
        if (o instanceof Action) {
            this.action = (Action) o;
        }

        if (o instanceof Command) {
            this.command = (Command) o;
        }

        this.comment = comment;
    }

    public boolean exists()
    {
        return false;
    }

    public ImageDescriptor getImageDescriptor()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPersistableElement getPersistable()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getToolTipText()
    {
        return getName();
    }

    public Object getAdapter(Class adapter)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public XWikiEclipseComment getComment()
    {
        return comment;
    }

    @Override
    public int hashCode()
    {
        return comment.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CommentEditorInput other = (CommentEditorInput) obj;
        if (comment == null) {
            if (other.comment != null)
                return false;
        } else if (!comment.getXWikiEclipseId().equals(other.comment.getXWikiEclipseId()))
            return false;
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    @Override
    public String getName()
    {
        String name = null;
        try {
            if (command != null && command.getId().equals(UIConstants.REPLYTO_COMMENT_COMMAND)) {
                name =
                    command.getName() + " " + (comment.getReplyTo() == null ? "" : comment.getReplyTo()) + ": "
                        + comment.getAuthor();
            } else {
                if (command != null) {
                    name =
                        command.getName() + " " + (comment.getId() == null ? "" : comment.getId()) + ": "
                            + comment.getAuthor();
                }

                if (action != null) {
                    name =
                        action.getText() + " " + (comment.getId() == null ? "" : comment.getId()) + ": "
                            + comment.getAuthor();
                }

            }
        } catch (NotDefinedException e) {
            CoreLog.logError("Error in comment editor", e);
        }

        return name;
    }

    public String getCommandText()
    {
        if (command != null) {
            try {
                return command.getName();
            } catch (NotDefinedException e) {
                CoreLog.logError("Error in comment editor", e);
            }
        }

        if (action != null) {
            return action.getText();
        }

        return null;
    }
}
