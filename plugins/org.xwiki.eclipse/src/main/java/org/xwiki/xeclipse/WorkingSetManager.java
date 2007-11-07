package org.xwiki.xeclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.xwiki.plugins.eclipse.XWikiEclipsePlugin;

public class WorkingSetManager
{
    private Set<WorkingSet> workingSets;

    private static WorkingSetManager sharedInstance;

    private WorkingSetManager()
    {
        workingSets = new HashSet<WorkingSet>();
    }

    private WorkingSetManager(Set<WorkingSet> workingSets)
    {
        this.workingSets = workingSets;
    }

    @SuppressWarnings("unchecked")
    public static WorkingSetManager getDefault()
    {
        if (sharedInstance == null) {            
                sharedInstance = new WorkingSetManager();            
        }

        return sharedInstance;
    }

    public void add(WorkingSet workingSet)
    {
        workingSets.add(workingSet);
    }

    public void remove(WorkingSet workingSet)
    {
        workingSets.remove(workingSet);
    }

    public Set<WorkingSet> getWorkingSets()
    {
        return workingSets;
    }

    public void restoreWorkingSets(File inputFile) throws Exception
    {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile));
        workingSets = (Set<WorkingSet>) ois.readObject();
        ois.close();
    }

    public void saveWorkingSets(File outputFile) throws Exception
    {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile));
        oos.writeObject(workingSets);
        oos.close();
    }
}
