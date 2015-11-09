package org.sphpp.workflow.data;

public abstract class IdItem {
	private final String id;
	
	protected IdItem( String id ) {
		this.id = id;
	}
	
	public boolean equals(Object obj) {
		if( !IdItem.class.isInstance(obj) )
			return false;
		return getId().equals(((IdItem)obj).getId());
	};
	
	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	public String getId() {
		return id;
	}
}
