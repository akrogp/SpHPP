package org.sphpp.workflow.data;

import java.util.Collection;
import java.util.Set;

import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class Wrapper<S extends Decoyable, FROM extends InterMapeable<FROM, TO>, TO extends InterMapeable<TO,FROM>>
extends IdItem implements Decoyable, InterMapeable<FROM,TO> {
	public Wrapper( String id, S entity, FROM link ) {
		super(id);
		this.entity = entity;
		this.link = link;		
	}

	@Override
	public Boolean getDecoy() {
		return getScoreEntity().getDecoy();
	}

	@Override
	public void setDecoy(Boolean decoy) {
		getScoreEntity().setDecoy(decoy);
	}

	@Override
	public Score putScore(Score score) {
		return getScoreEntity().putScore(score);
	}

	@Override
	public Collection<Score> getScores() {
		return getScoreEntity().getScores();
	}

	@Override
	public Score getScoreByType(ScoreType type) {
		return getScoreEntity().getScoreByType(type);
	}

	@Override
	public void clearScores() {
		getScoreEntity().clearScores();
	}

	@Override
	public void setPassThreshold(boolean passThreshold) {
		getScoreEntity().setPassThreshold(passThreshold);
	}

	@Override
	public boolean isPassThreshold() {
		return getScoreEntity().isPassThreshold();
	}

	@Override
	public boolean skipFdr() {
		return getScoreEntity().skipFdr();
	}
	
	public S getScoreEntity() {
		return entity;
	}	

	@Override
	public Set<TO> getLinks() {
		return link.getLinks();
	}

	@Override
	public boolean link(TO item) {
		return link.link(item);
	}
	
	private final S entity;
	private final FROM link;
}
