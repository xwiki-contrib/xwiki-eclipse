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

package org.xwiki.plugins.eclipse;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.xwiki.plugins.eclipse.model.IXWikiConnection;
import org.xwiki.plugins.eclipse.model.impl.XWikiConnectionManager;

/**
 * The activator class controls the plug-in life cycle, this is a mandatory class and is used by the
 * eclipse plugin framework.
 */
public class XWikiEclipsePlugin extends AbstractUIPlugin
{
    /**
     * The plugin ID.
     */
    public static final String PLUGIN_ID = "org.xwiki.eclipse";

    /**
     * The shared instance.
     */
    private static XWikiEclipsePlugin plugin;

    /**
     * The constructor.
     */
    public XWikiEclipsePlugin()
    {
        plugin = this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception
    {        
        super.start(context);
        File connections = new File(getStateLocation().toFile(), "connections.data");
        if(connections.exists()) {
            org.xwiki.xeclipse.XWikiConnectionManager.getDefault().restoreConnections(connections);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception
    {
        for (IXWikiConnection con : XWikiConnectionManager.getInstance().getAllConnections()) {
            try {
                con.disconnect();
            } catch (Exception e) {
                // Nothing to do.
            }
        }
        
        File connections = new File(getStateLocation().toFile(), "connections.data");
        org.xwiki.xeclipse.XWikiConnectionManager.getDefault().saveConnections(connections);        
        
        plugin = null;
        super.stop(context);
    }

    /**
     * @return The shared instance of this plugin.
     */
    public static XWikiEclipsePlugin getDefault()
    {
        return plugin;
    }

    /**
     * @param path The path to image file.
     * @return An image descriptor for the image file at the given plug-in relative path.
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {        
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
