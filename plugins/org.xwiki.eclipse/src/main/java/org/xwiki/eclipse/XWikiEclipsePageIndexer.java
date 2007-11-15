package org.xwiki.eclipse;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.xwiki.eclipse.model.IXWikiConnection;
import org.xwiki.eclipse.model.IXWikiPage;
import org.xwiki.eclipse.model.IXWikiSpace;
import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;

public class XWikiEclipsePageIndexer
{
    private static XWikiEclipsePageIndexer instance;
    private Job indexer;
    
    private class IndexerJob extends Job
    {

        public IndexerJob(String name)
        {
            super(name);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
            try {
                XWikiEclipsePageIndex pageIndex = XWikiEclipsePageIndex.getDefault();
                
                monitor.beginTask(String.format("Indexing connections..."),
                    XWikiConnectionManager.getDefault().getConnections().size());

                for (IXWikiConnection xwikiConnection : XWikiConnectionManager.getDefault()
                    .getConnections()) {
                    
                    if(monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }
                    
                    Collection<IXWikiSpace> spaces = xwikiConnection.getSpaces();
                    
                    for(IXWikiSpace space : spaces) {
                        if(monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                        
                        Collection<IXWikiPage> pages = space.getPages();
                        for(IXWikiPage page : pages) {
                            pageIndex.addPage(page);
                        }
                    }
                    monitor.worked(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new Status(IStatus.ERROR, XWikiEclipsePlugin.PLUGIN_ID, String.format("Error while indexing\n%s", e.getMessage()));
            } finally {
                monitor.done();
                schedule(600000);
            }

            return Status.OK_STATUS;
        }

    }

    private XWikiEclipsePageIndexer()
    {
        // TODO Auto-generated constructor stub
    }
    
    public static XWikiEclipsePageIndexer getDefault()
    {
        if (instance == null) {
            instance = new XWikiEclipsePageIndexer();
        }

        return instance;
    }
    
    public void start() {
        if(indexer != null) {
            return;
        }
        
        indexer = new IndexerJob("Page indexer");
        indexer.setSystem(true);
        indexer.setPriority(Job.DECORATE);        
        indexer.schedule();
    }
    
    public void stop() {
        if(indexer == null) {
            return;
        }
        
        indexer.cancel();
        indexer = null;
    }
}
