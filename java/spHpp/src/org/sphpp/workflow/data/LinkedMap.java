package org.sphpp.workflow.data;

import java.util.HashMap;
import java.util.Map;

public class LinkedMap {
	public void load( Relations relations ) {
		upperMap.clear();
		lowerMap.clear();
		
		for( Relations.Relation rel : relations.getEntries() ) {
			LinkedItem upper = upperMap.get(rel.getUpperId());
			if( upper == null ) {
				upper = new LinkedItem(rel.getUpperId());
				upperMap.put(upper.getId(), upper);
			}
			LinkedItem lower = lowerMap.get(rel.getLowerId());
			if( lower == null ) {
				lower = new LinkedItem(rel.getLowerId());
				lowerMap.put(lower.getId(), lower);
			}
			upper.link(lower);
		}
	}

	public Map<String, LinkedItem> getUpperMap() {
		return upperMap;
	}
	public Map<String, LinkedItem> getLowerMap() {
		return lowerMap;
	}

	private final Map<String,LinkedItem> upperMap = new HashMap<>();
	private final Map<String,LinkedItem> lowerMap = new HashMap<>();
}
