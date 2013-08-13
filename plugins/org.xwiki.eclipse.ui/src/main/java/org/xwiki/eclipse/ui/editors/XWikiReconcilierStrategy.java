package org.xwiki.eclipse.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.swt.widgets.Display;
import org.xwiki.eclipse.ui.editors.scanners.XWikiAdvancedPartitionScanner;
import org.xwiki.eclipse.ui.parser.Partition;

public class XWikiReconcilierStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {
    private PageEditor editor;

    private IDocument fDocument;

    /** holds the calculated positions for code folding */
    protected final ArrayList fPositions = new ArrayList();

    /** The offset of the next character to be read */
    protected int fOffset;

    /** The end offset of the range to be scanned */
    protected int fRangeEnd;
    
    public XWikiReconcilierStrategy() {
    }
    
    /**
     * @return Returns the editor.
     */
    public PageEditor getEditor() {
            return editor;
    }

    public void setEditor(PageEditor editor) {
            this.editor = editor;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
     */
    public void setDocument(IDocument document) {
            this.fDocument = document;

    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion,
     *      org.eclipse.jface.text.IRegion)
     */
    public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
            initialReconcile();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
     */
    public void reconcile(IRegion partition) {
            initialReconcile();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setProgressMonitor(IProgressMonitor monitor) {
            // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#initialReconcile()
     */
    public void initialReconcile() {
            calculatePositions();
    }

    /*
     * This code is used by the code folding to update it based on the positions
     * The actual positions are calculated in the "parse" function below
     */
    protected void calculatePositions()
    {
        fPositions.clear();
        
        try {
                parse();
        } catch (Exception e) {
                e.printStackTrace();
        }
 
        Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                        if (fPositions!=null)
                         editor.updateFoldingStructure(new ArrayList(fPositions));
                }

        });
    }

    /**
     * This code calculates the positions for code folding
     * The XWiki+Velocity parser is used to calculate these positions which can overlap
     * Currently the positions are calculated for Macros, Velocity if, foreach and macros and XWiki Tables
     */
    protected void parse()
    {
    	XWikiAdvancedPartitionScanner scanner = new XWikiAdvancedPartitionScanner();
        FastPartitioner partitioner = new FastPartitioner(scanner, XWikiAdvancedPartitionScanner.ALL_PARTITIONS);
        partitioner.connect(fDocument);
        partitioner.computePartitioning(0, fDocument.getLength());
        List<Partition> partitions = scanner.getPartitions(); 
       
        for (int i=1;i<partitions.size();i++) {
        	Partition partition = partitions.get(i);
            if (partition.getType().equals(Partition.Type.VELOCITY_FOREACH) 
            		|| partition.getType().equals(Partition.Type.VELOCITY_IF) 
            		|| partition.getType().equals(Partition.Type.VELOCITY_MACRO) 
            		|| partition.getType().equals(Partition.Type.MACRO) 
            		|| partition.getType().equals(Partition.Type.TABLE)) {
        	   Position position = new TypedPosition(partition.getBeginOffset(), partition.getEndOffset()-partition.getBeginOffset(), partition.getType().toString());
               // System.out.println("Adding position " + i + " " + position.toString());
               fPositions.add(position);
            }
        }
    }


}