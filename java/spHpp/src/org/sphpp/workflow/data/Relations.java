package org.sphpp.workflow.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.sphpp.workflow.Utils;

import es.ehubio.proteomics.Decoyable;

public class Relations {
	public Collection<Relation> getEntries() {
		return rels.values();
	}
	
	public boolean addEntry( Relation rel ) {
		return rels.put(rel.getId(), rel) == null;
	}
	
	public LinkMap<Link<Void,Void>,Link<Void,Void>> getLinkMap() {
		return getLinkMap(Void.class);
	}
		
	public <T> LinkMap<Link<T,T>,Link<T,T>> getLinkMap( Class<T> cls ) {
		return getLinkMap(cls, this);
	}
	
	public static <T> LinkMap<Link<T,T>,Link<T,T>> getLinkMap( Class<T> cls, Relations rels ) {		
		LinkMap<Link<T,T>,Link<T,T>> map = new LinkMap<>();
		for( Relation rel : rels.getEntries() ) {
			Link<T,T> upper = new Link<>(rel.getUpperId());
			Link<T,T> lower = new Link<>(rel.getLowerId());
			map.addLink(upper, lower);
		}
		return map;
	}
	
	public <S extends Decoyable & Identifiable>
	LinkMap<ScoreLink,ScoreLink> getScoreLinkMap() {
		return getScoreLinkMap(this);
	}
	
	public <S extends Decoyable & Identifiable>
	LinkMap<ScoreLink,ScoreLink> getScoreLinkMap( Set<S> scoreItems ) {
		return getScoreLinkMap(scoreItems, this);
	}
	
	public static <S extends Decoyable & Identifiable>
	LinkMap<ScoreLink,ScoreLink> getScoreLinkMap( Set<S> upperScores, Relations rels ) {
		LinkMap<ScoreLink,ScoreLink> result = new LinkMap<>();
		Map<String,S> scoreMap = Utils.getMap(upperScores);
		for( Relation rel : rels.getEntries() ) {
			ScoreLink upper = new ScoreLink(rel.getUpperId(),scoreMap.get(rel.getUpperId()));
			ScoreLink lower = new ScoreLink(rel.getLowerId());
			result.addLink(upper, lower);
		}
		return result;
	}
	
	public static <S extends Decoyable & Identifiable>
	LinkMap<ScoreLink,ScoreLink> getScoreLinkMap( Relations rels ) {
		LinkMap<ScoreLink,ScoreLink> result = new LinkMap<>();
		for( Relation rel : rels.getEntries() ) {
			ScoreLink upper = new ScoreLink(rel.getUpperId());
			ScoreLink lower = new ScoreLink(rel.getLowerId());
			result.addLink(upper, lower);
		}
		return result;
	}
	
	public boolean hasCoeficients() {
		return getEntries().iterator().next().getCoeficient() != null;
	}
	
	public boolean hasLabels() {
		Set<String> labels = getEntries().iterator().next().getLabels();
		return labels != null && !labels.isEmpty();
	}
	
	public void setEquitative() {
		LinkMap<Link<Void,Void>,Link<Void,Void>> map = getLinkMap();
		for( Relation rel : getEntries() )
			rel.setCoeficient(1.0/map.getLower(rel.getLowerId()).getLinks().size());
	}
	
	public Relation get( String upper, String lower ) {
		return rels.get(new Relation(upper, lower).getId());
	}
	
	private final Map<String, Relation> rels = new LinkedHashMap<>();
}
