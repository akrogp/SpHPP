package org.sphpp.workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sphpp.workflow.data.Identifiable;

public class Utils {
	public static <T extends Identifiable> Map<String, T> getMap(Set<T> items) {
		Map<String, T> map = new HashMap<>(items.size());
		for( T item : items )
			map.put(item.getId(), item);
		return map;
	}
}
