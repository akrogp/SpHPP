package org.sphpp.workflow.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class ScoreLink extends IdItem implements InterMapeable<ScoreLink,ScoreLink>, Decoyable {
	public ScoreLink( String id ) {
		this(id, new ScoreItem(id));
	}
	
	public ScoreLink( String id, Decoyable decoyable ) {
		super(id);
		this.decoyable = decoyable;
	}

	@Override
	public Set<ScoreLink> getLinks() {
		if( links == null )
			links = new HashSet<>();
		return links;
	}

	@Override
	public boolean link(ScoreLink item) {
		if( !getLinks().add(item) )
			return false;
		item.getLinks().add(this);
		return true;
	}
	
	public Boolean getDecoy() {
		return decoyable.getDecoy();
	}

	public void setDecoy(Boolean decoy) {
		decoyable.setDecoy(decoy);
	}

	public Score putScore(Score score) {
		return decoyable.putScore(score);
	}

	public Collection<Score> getScores() {
		return decoyable.getScores();
	}

	public void putScores(Collection<Score> scores) {
		decoyable.putScores(scores);
	}

	public Score getScoreByType(ScoreType type) {
		return decoyable.getScoreByType(type);
	}

	public void clearScores() {
		decoyable.clearScores();
	}

	public void setPassThreshold(boolean passThreshold) {
		decoyable.setPassThreshold(passThreshold);
	}

	public boolean isPassThreshold() {
		return decoyable.isPassThreshold();
	}

	public boolean skipFdr() {
		return decoyable.skipFdr();
	}

	private final Decoyable decoyable;
	private Set<ScoreLink> links;
}
