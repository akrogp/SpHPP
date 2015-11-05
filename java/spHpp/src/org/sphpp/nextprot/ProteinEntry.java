package org.sphpp.nextprot;

import java.util.Set;

public final class ProteinEntry {
	private String accession;
	private String evidence;
	private boolean missing;
	private Set<String> categories;
	
	public String getAccession() {
		return accession;
	}
	
	public void setAccession(String accession) {
		this.accession = accession;
	}
	
	public String getEvidence() {
		return evidence;
	}
	
	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}
	
	public boolean isMissing() {
		return missing;
	}
	
	public void setMissing(boolean missing) {
		this.missing = missing;
	}
	
	public Set<String> getCategories() {
		return categories;
	}
	
	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder(getAccession()+":"+getEvidence()+":"+(isMissing()?1:0)+":");
		boolean first = true;
		for( String category : categories )
			if( first ) {
				string.append(category);
				first = false;
			} else
				string.append(";"+category);
		return string.toString();
	}
}