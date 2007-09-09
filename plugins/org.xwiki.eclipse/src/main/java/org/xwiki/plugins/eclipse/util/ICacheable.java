package org.xwiki.plugins.eclipse.util;

import java.io.Serializable;

import org.eclipse.core.runtime.IPath;

public interface ICacheable extends Serializable
{
    public IPath getCachePath();
}
