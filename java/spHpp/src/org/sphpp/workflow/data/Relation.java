package org.sphpp.workflow.data;

import java.util.LinkedHashSet;
import java.util.Set;

public class Relation extends IdItem {
	private final String upperId;
	private final String lowerId;
	private final Set<String> labels = new LinkedHashSet<String>();
	private Double coeficient;
	public Relation( String upperId, String lowerId ) {
		super(String.format("%s->%s", lowerId, upperId));
		this.upperId = upperId;
		this.lowerId = lowerId;
	}
	public String getUpperId() {
		return upperId;
	}
	public String getLowerId() {
		return lowerId;
	}
	public Set<String> getLabels() {
		return labels;
	}
	public boolean addLabel( String label ) {
		return labels.add(label);
	}
	public Double getCoeficient() {
		return coeficient;
	}
	public void setCoeficient(Double coeficient) {
		this.coeficient = coeficient;
	}
}