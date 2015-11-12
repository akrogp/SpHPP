package org.sphpp.workflow.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LinkList<U extends InterMapeable<U,L>, L extends InterMapeable<L,U>> {
	public void addLink( U upper, L lower) {
		U uqUpper = upperMap.get(upper.getId());
		if( uqUpper == null ) {
			upperMap.put(upper.getId(), upper);
			uqUpper = upper;
		}
		L uqLower = lowerMap.get(lower.getId());
		if( uqLower == null ) {
			lowerMap.put(lower.getId(), lower);
			uqLower = lower;
		}
		uqUpper.link(uqLower);
	}

	public Map<String, U> getUpperMap() {
		return upperMap;
	}
	public Map<String, L> getLowerMap() {
		return lowerMap;
	}
	
	public Collection<U> getUpperList() {
		return getUpperMap().values();
	}
	
	public Collection<L> getLowerList() {
		return getLowerMap().values();
	}

	private final Map<String,U> upperMap = new HashMap<>();
	private final Map<String,L> lowerMap = new HashMap<>();
}
