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
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;

public class XWikiEclipsePageIndexer implements IXWikiEclipseEventListener
{
    private static XWikiEclipsePageIndexer instance;

    private Map<IXWikiConnection, Job> connectionToIndexerMapping;

    private class IndexerJob extends Job
    {
        private IXWikiConnection connection;

        public IndexerJob(IXWikiConnection connection)
        {
            super("Connection page indexer");
            this.connection = connection;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
            try {
                XWikiEclipsePageIndex pageIndex = XWikiEclipsePageIndex.getDefault();

                monitor.beginTask(String.format("Indexing connection..."),
                    IProgressMonitor.UNKNOWN);

                if (monitor.isCanceled()) {                   
                    return Status.CANCEL_STATUS;
                }

                Collection<IXWikiSpace> spaces = connection.getSpaces();

                for (IXWikiSpace space : spaces) {
                    if (monitor.isCanceled()) {              
                        return Status.CANCEL_STATUS;
                    }

                    Collection<IXWikiPage> pages = space.getPages();
                    for (IXWikiPage page : pages) {
                        pageIndex.addPage(page);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return new Status(IStatus.ERROR, XWikiEclipsePlugin.PLUGIN_ID, String.format(
                    "Error while indexing\n%s", e.getMessage()));
            } finally {
                monitor.done();
                schedule(600000);
            }
                  
            return Status.OK_STATUS;
        }

    }

    private XWikiEclipsePageIndexer()
    {
        connectionToIndexerMapping = new HashMap<IXWikiConnection, Job>();
    }

    public static XWikiEclipsePageIndexer getDefault()
    {
        if (instance == null) {
            instance = new XWikiEclipsePageIndexer();
        }

        return instance;
    }

    public void start()
    {
        XWikiEclipseNotificationCenter.getDefault().addListener(
            XWikiEclipseEvent.CONNECTION_ESTABLISHED, this);
        XWikiEclipseNotificationCenter.getDefault().addListener(
            XWikiEclipseEvent.CONNECTION_CLOSED, this);
    }

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

                    IndexerJob indexerJob = new IndexerJob(connection);
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
