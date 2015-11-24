package org.sphpp.workflow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.sphpp.workflow.data.Identifiable;

import es.ehubio.proteomics.Decoyable;

public class Utils {
	public static <T extends Identifiable> Map<String, T> getMap(Collection<T> items) {
		Map<String, T> map = new HashMap<>(items.size());
		for( T item : items )
			map.put(item.getId(), item);
		return map;
	}
	
	public static <T1 extends Identifiable & Decoyable, T2 extends Identifiable & Decoyable>
	void addScores(Collection<T1> items, Collection<T2> scores) {
		Map<String, T2> mapScores = getMap(scores);
		for( T1 item : items )
			item.putScores(mapScores.get(item.getId()).getScores());
	}
}
