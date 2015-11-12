package org.sphpp.workflow.data;

public class IdItem implements Identifiable {
	private final String id;
	
	protected IdItem( String id ) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( !IdItem.class.isInstance(obj) )
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
}
