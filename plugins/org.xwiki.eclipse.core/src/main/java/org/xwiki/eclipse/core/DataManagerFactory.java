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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.xwiki.eclipse.rest.storage.DataManagerInRest;
import org.xwiki.eclipse.storage.AbstractDataManager;
import org.xwiki.eclipse.storage.BackendType;
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
	public static AbstractDataManager createDataManager(IProject project) throws CoreException {
		try {		    
			String backend = project.getPersistentProperty(AbstractDataManager.BACKEND);
			BackendType backendType = BackendType.valueOf(backend);

			AbstractDataManager dataManager = null;
			switch (backendType) {
			case xmlrpc:
			    dataManager = new DataManagerInXmlrpc(project);
			    return dataManager;
			case rest:
			    dataManager = new DataManagerInRest(project);
			    return dataManager;
			default:
				break;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
