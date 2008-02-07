package org.xwiki.eclipse.model.impl;

import java.io.Serializable;

public class ConflictData implements Serializable {
	private static final long serialVersionUID = -7999756414069939507L;
	
	private String localContent;
	private String remoteContent;

	public ConflictData(String localContent, String remoteContent) {
		this.localContent = localContent;
		this.remoteContent = remoteContent;
	}

	public String getLocalContent() {
		return localContent;
	}

	public String getRemoteContent() {
		return remoteContent;
	}
	
	
	
}
