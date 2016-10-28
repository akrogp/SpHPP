package org.sphpp.workflow.data;

public class IdItem implements Identifiable {
	private final String id;
	
	protected IdItem( String id ) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof String )
			return getId().equals(obj);
		if( !(obj instanceof IdItem) )
			return false;
		return getId().equals(((IdItem)obj).getId());
	};
	
	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return getId();
	}
}
