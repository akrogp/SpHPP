package org.sphpp.workflow.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Link<FROM,TO> extends IdItem implements InterMapeable<Link<FROM,TO>,Link<TO,FROM>> {
	public Link( String id ) {
		this(id, null);
	}
	
	public Link( String id, FROM entity ) {
		super(id);
		this.entity = entity;
	}
	
	@Override
	public Set<Link<TO, FROM>> getLinks() {
		if( links == null )
			links = new HashSet<>();
		return links;
	}
	
	@Override
	public boolean link(Link<TO, FROM> item) {
		if( !getLinks().add(item) )
			return false;
		if( item.getEntity() != null )
			getLinkedEntities().add(item.getEntity());
		if( item.getLinks().add(this) && getEntity() != null )
			item.getLinkedEntities().add(getEntity());
		return true;
	}
	
	public boolean link(String id, TO item) {
		return link(new Link<TO,FROM>(id, item));
	}
	
	public FROM getEntity() {
		return entity;
	}
	
	public List<TO> getLinkedEntities() {
		if( list == null )
			list = new ArrayList<>();
		return list;
	}

	private Set<Link<TO, FROM>> links;
	private List<TO> list;
	private final FROM entity;
}
