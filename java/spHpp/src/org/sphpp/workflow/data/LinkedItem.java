package org.sphpp.workflow.data;

import java.util.HashSet;
import java.util.Set;

public class LinkedItem extends IdItem {
	private final Set<LinkedItem> links = new HashSet<>();
	
	public LinkedItem( String id ) {
		super(id);
	}
	
	public Set<LinkedItem> getLinks() {
		return links;
	}
	
	public boolean link( LinkedItem item ) {
		if( !links.add(item) )
			return false;
		item.links.add(this);
		return true;
	}	
}
