package org.sphpp.workflow.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class ScoreItem extends IdItem implements Decoyable {
	private final Map<ScoreType, Score> scores = new LinkedHashMap<>();
	private Boolean decoy;
	private boolean passThreshold;
	
	public ScoreItem( String id ) {
		super(id);
	}
	
	@Override
	public Boolean getDecoy() {
		return decoy;
	}

	@Override
	public void setDecoy(Boolean decoy) {
		this.decoy = decoy;
	}

	@Override
	public Score putScore(Score score) {		
		return scores.put(score.getType(), score);
	}

	@Override
	public Collection<Score> getScores() {
		return scores.values();
	}

	@Override
	public Score getScoreByType(ScoreType type) {
		return scores.get(type);
	}

	@Override
	public void clearScores() {
		scores.clear();
	}

	@Override
	public void setPassThreshold(boolean passThreshold) {
		this.passThreshold = passThreshold;
	}

	@Override
	public boolean isPassThreshold() {		
		return passThreshold;
	}

	@Override
	public boolean skipFdr() {
		return false;
	}
}
