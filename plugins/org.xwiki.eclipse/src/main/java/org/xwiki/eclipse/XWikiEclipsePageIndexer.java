package org.xwiki.eclipse;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.IXWikiSpace;
import org.xwiki.eclipse.model.XWikiConnectionFactory;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;

/**
 * A singleton that handles the indexing of available connections.
 */
public class XWikiEclipsePageIndexer implements IXWikiEclipseEventListener
{
    private static XWikiEclipsePageIndexer instance;

    private Map<IXWikiConnection, Job> connectionToIndexerMapping;

    /**
     * The Eclipse job that performs the actual indexing for a given connection
     */
    private class IndexerJob extends Job
    {
        private IXWikiConnection connection;
        private boolean reschedule;

        /**
         * Constructor.
         * 
         * @param connection The connection to be indexed by this job.
         */
        public IndexerJob(IXWikiConnection connection)
        {
            this(connection, false);
        }
        
        public IndexerJob(IXWikiConnection connection, boolean reschedule)
        {
            super("Connection page indexer");
            this.connection = connection;
            this.reschedule = reschedule;
        }


        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
            try {
                XWikiEclipsePageIndex pageIndex = XWikiEclipsePageIndex.getDefault();

                monitor.beginTask(String.format("Indexing connection..."),
                    IProgressMonitor.UNKNOWN);

                if (monitor.isCanceled() || !connection.isConnected()) {                   
                    return Status.CANCEL_STATUS;
                }

                /* Use a working connection in order to not interfere with the "master" one */
                IXWikiConnection workingConnection = XWikiConnectionFactory.createPlainConnection(connection.getServerUrl(), connection.getUserName());
                workingConnection.connect(XWikiConnectionManager.getDefault().getPasswordForConnection(connection));                
                
                /* Check that the master connection is still "connected", otherwise cancel indexing */
                if (monitor.isCanceled() || !connection.isConnected()) {                   
                    return Status.CANCEL_STATUS;
                }
                
                Collection<IXWikiSpace> spaces = workingConnection.getSpaces();
                
                for (IXWikiSpace space : spaces) {
                    if (monitor.isCanceled() || !connection.isConnected()) {              
                        return Status.CANCEL_STATUS;
                    }

                    Collection<IXWikiPage> pages = space.getPages();
                    for (IXWikiPage page : pages) {                 
                        pageIndex.addPage(page);
                    }
                }
                
                workingConnection.disconnect();
                workingConnection.dispose();
                workingConnection = null;
            } catch (Exception e) {
                e.printStackTrace();
                return new Status(IStatus.ERROR, XWikiEclipsePlugin.PLUGIN_ID, String.format(
                    "Error while indexing\n%s", e.getMessage()));
            } finally {
                monitor.done();
                if(reschedule) {
                    schedule(1200000);
                }
            }
                  
            return Status.OK_STATUS;
        }
    }

    private XWikiEclipsePageIndexer()
    {
        connectionToIndexerMapping = new HashMap<IXWikiConnection, Job>();
    }

    /**
     * @return The shared instance.
     */
    public static XWikiEclipsePageIndexer getDefault()
    {
        if (instance == null) {
            instance = new XWikiEclipsePageIndexer();
        }

        return instance;
    }

    /**
     * Start the indexer.
     */
    public void start()
    {
        XWikiEclipseNotificationCenter.getDefault().addListener(
            XWikiEclipseEvent.CONNECTION_ESTABLISHED, this);
        XWikiEclipseNotificationCenter.getDefault().addListener(
            XWikiEclipseEvent.CONNECTION_CLOSED, this);
    }

    /**
     * Stop the indexer.
     */
    public void stop()
    {
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.CONNECTION_ESTABLISHED, this);
        XWikiEclipseNotificationCenter.getDefault().removeListener(
            XWikiEclipseEvent.CONNECTION_CLOSED, this);
        
        for(Job indexerJob : connectionToIndexerMapping.values()) {
            indexerJob.cancel();            
        }
    }
    
    /**
     * Event handling.
     * 
     * When a connection is established, then an indexing job is created on that connection.
     * The job is re-scheduled at a fixed interval.
     * 
     * When a connection is closed, the indexing job for that connection is canceled.
     *
     * So only active connections are indexed.
     */
    public void handleEvent(Object sender, XWikiEclipseEvent event, Object data)
    {
        IXWikiConnection connection;

        switch (event) {
            case CONNECTION_ESTABLISHED:
                connection = (IXWikiConnection) data;
                if (XWikiConnectionManager.getDefault().getConnections().contains(connection)) {
                    if (connectionToIndexerMapping.get(connection) != null) {
                        connectionToIndexerMapping.get(connection).cancel();
                        connectionToIndexerMapping.remove(connection);
                    }

                    IndexerJob indexerJob = new IndexerJob(connection, true);
                    connectionToIndexerMapping.put(connection, indexerJob);
                    indexerJob.setSystem(true);
                    indexerJob.setPriority(Job.DECORATE);
                    indexerJob.schedule();
                }
                break;
            case CONNECTION_CLOSED:
                connection = (IXWikiConnection) data;
                if (XWikiConnectionManager.getDefault().getConnections().contains(connection)) {
                    if (connectionToIndexerMapping.get(connection) != null) {
                        connectionToIndexerMapping.get(connection).cancel();
                        connectionToIndexerMapping.remove(connection);
                    }
                }
                break;
        }
    }
}
