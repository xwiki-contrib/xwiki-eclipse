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
package org.xwiki.eclipse.core;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.xwiki.eclipse.core.notifications.CoreEvent;
import org.xwiki.eclipse.core.notifications.NotificationManager;
import org.xwiki.eclipse.model.XWikiEclipseClass;
import org.xwiki.eclipse.model.XWikiEclipseClassSummary;
import org.xwiki.eclipse.model.XWikiEclipseObject;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.model.XWikiEclipsePage;
import org.xwiki.eclipse.model.XWikiEclipsePageHistorySummary;
import org.xwiki.eclipse.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.storage.AbstractDataManager;
import org.xwiki.eclipse.storage.BackendType;
import org.xwiki.eclipse.storage.Functionality;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.eclipse.xmlrpc.storage.DataManagerInXmlrpc;


/**
 * A class that implements a controller for handling data and the connection towards an XWiki server. It takes care of
 * synchronizing pages, objects, handling local copies, conflicts, etc.
 * delegate all the functions to backend implementation, xmlrpc or rest
 * fire notification event 
 * 
 * @version $Id$
 */
public class DataManagerFactory
{
	AbstractDataManager dataManager = null;

	public static AbstractDataManager createDataManager(IProject project) throws CoreException {
		try {
			String backend = project.getPersistentProperty(AbstractDataManager.BACKEND);
			BackendType backendType = BackendType.valueOf(backend);
			switch (backendType) {
			case xmlrpc:
			    AbstractDataManager dataManager = new DataManagerInXmlrpc(project);
			    return dataManager;
			case rest:
			    throw new UnsupportedOperationException();
			    //break;
			default:
				break;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
