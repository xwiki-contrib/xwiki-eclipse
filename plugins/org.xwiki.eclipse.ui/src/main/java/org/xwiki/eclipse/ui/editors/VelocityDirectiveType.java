package org.xwiki.eclipse.ui.editors;

public enum VelocityDirectiveType
{
    SET, IF, ELSE, ELSEIF, END, FOREACH, INCLUDE, PARSE, MACRO, STOP;

    public String getName()
    {
        return "#" + this.toString().toLowerCase();
    }
}
