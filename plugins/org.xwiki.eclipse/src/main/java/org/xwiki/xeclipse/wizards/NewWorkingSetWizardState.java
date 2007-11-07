package org.xwiki.xeclipse.wizards;

import org.xwiki.xeclipse.WorkingSet;

public class NewWorkingSetWizardState
{
    private WorkingSet workingSet;
    
    public NewWorkingSetWizardState()
    {
        workingSet = new WorkingSet(null);
    }
    
    public WorkingSet getWorkingSet()
    {
        return workingSet;
    }
    
    
}
