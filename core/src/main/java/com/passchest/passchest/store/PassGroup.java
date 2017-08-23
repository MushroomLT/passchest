package com.passchest.passchest.store;

import java.util.List;

public class PassGroup {
	public String groupName;
	public List<String> groupDomainAlias;
	public List<PassEntry> groupEntries;
	
	public PassGroup() {
		
	}

	public PassGroup(String groupName, List<String> groupDomainAlias, List<PassEntry> groupEntries) {
		this.groupName = groupName;
		this.groupDomainAlias = groupDomainAlias;
		this.groupEntries = groupEntries;
	}
}
