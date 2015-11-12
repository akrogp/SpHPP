package org.sphpp.workflow.data;

import java.util.Collection;

import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class Wrapper<FROM extends Decoyable,TO> extends Link<FROM,TO> implements Decoyable {
	public Wrapper( String id, FROM entity ) {
		super(id, entity);
	}

	@Override
	public Boolean getDecoy() {
		return getEntity().getDecoy();
	}

	@Override
	public void setDecoy(Boolean decoy) {
		getEntity().setDecoy(decoy);
	}

	@Override
	public Score putScore(Score score) {
		return getEntity().putScore(score);
	}

	@Override
	public Collection<Score> getScores() {
		return getEntity().getScores();
	}

	@Override
	public Score getScoreByType(ScoreType type) {
		return getEntity().getScoreByType(type);
	}

	@Override
	public void clearScores() {
		getEntity().clearScores();
	}

	@Override
	public void setPassThreshold(boolean passThreshold) {
		getEntity().setPassThreshold(passThreshold);
	}

	@Override
	public boolean isPassThreshold() {
		return getEntity().isPassThreshold();
	}

	@Override
	public boolean skipFdr() {
		return getEntity().skipFdr();
	}
}
