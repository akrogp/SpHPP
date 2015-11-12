package org.sphpp.workflow.data;

import java.util.Set;

public interface Linkable<FROM extends Linkable<FROM,TO>, TO extends Linkable<TO,FROM>> {	
	
	public Set<TO> getLinks();
	
	public boolean link( TO item );
}
