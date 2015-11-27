package org.sphpp.workflow.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LinkMap<U extends InterMapeable<U,L>, L extends InterMapeable<L,U>> {
	public void addLink( U upper, L lower) {
		addLink(upper, lower, false);
	}
	
	public void addLink( U upper, L lower, boolean toLowerCase) {
		U uqUpper = upperMap.get(getId(upper, toLowerCase));
		if( uqUpper == null ) {
			upperMap.put(getId(upper, toLowerCase), upper);
			uqUpper = upper;
		}
		L uqLower = lowerMap.get(getId(lower, toLowerCase));
		if( uqLower == null ) {
			lowerMap.put(getId(lower, toLowerCase), lower);
			uqLower = lower;
		}
		uqUpper.link(uqLower);
	}
	
	private String getId(Identifiable item, boolean toLowerCase) {
		return toLowerCase?item.getId().toLowerCase():item.getId();
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
	
	public U getUpper( String id ) {
		return getUpperMap().get(id);
	}
	
	public L getLower( String id ) {
		return getLowerMap().get(id);
	}

	private final Map<String,U> upperMap = new HashMap<>();
	private final Map<String,L> lowerMap = new HashMap<>();
}
