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
package org.xwiki.eclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * A singleton for managing working sets.
 */
public class WorkingSetManager
{
    private Set<WorkingSet> workingSets;

    private static WorkingSetManager sharedInstance;

    /**
     * The currently selected working set.
     */
    private WorkingSet activeWorkingSet;

    private WorkingSetManager()
    {
        workingSets = new HashSet<WorkingSet>();
        activeWorkingSet = null;
    }

    /**
     * Constructor for restoring a previously saved working set list
     * 
     * @param workingSets A set containing working sets.
     */
    private WorkingSetManager(Set<WorkingSet> workingSets)
    {
        this.workingSets = workingSets;
    }

    /**
     * @return The shared instance.
     */
    @SuppressWarnings("unchecked")
    public static WorkingSetManager getDefault()
    {
        if (sharedInstance == null) {
            sharedInstance = new WorkingSetManager();
        }

        return sharedInstance;
    }

    /**
     * Add a working set to the manager.
     * 
     * @param workingSet The working set to be added.
     */
    public void add(WorkingSet workingSet)
    {
        workingSets.add(workingSet);
    }

    /**
     * Remove a working set from the manager.
     * 
     * @param workingSet The working set to be removed.
     */
    public void remove(WorkingSet workingSet)
    {
        workingSets.remove(workingSet);
    }

    /**
     * @return All the currently registered working set.
     */
    public Set<WorkingSet> getWorkingSets()
    {
        return workingSets;
    }

    /**
     * Restore a previously saved working set list from a file.
     * 
     * @param inputFile The file with the serialized stream containing the working sets.
     * @throws Exception
     */
    public void restoreWorkingSets(File inputFile) throws Exception
    {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile));
        workingSets = (Set<WorkingSet>) ois.readObject();
        ois.close();
    }

    /**
     * Store the currently registered working set on a file.
     * 
     * @param outputFile The file where the serialized stream containing the working sets will be
     *            stored.
     * @throws Exception
     */
    public void saveWorkingSets(File outputFile) throws Exception
    {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile));
        oos.writeObject(workingSets);
        oos.close();
    }

    /**
     * @return The active working set. null if no working is active.
     */
    public WorkingSet getActiveWorkingSet()
    {
        return activeWorkingSet;
    }

    /**
     * Set the active working set.
     * 
     * @param workingSet A working set to be activated.
     */
    public void setActiveWorkingSet(WorkingSet workingSet)
    {
        this.activeWorkingSet = workingSet;
    }

}
