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
package org.xwiki.xeclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

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
