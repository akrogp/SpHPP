package org.sphpp.workflow.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class ScoreItem extends IdItem {
	private final Map<ScoreType, Score> scores = new LinkedHashMap<>();
	
	public ScoreItem( String id ) {
		super(id);		
	}

	public Score putScore(Score score) {
		return scores.put(score.getType(), score);
	}

	public Score getScoreByType(ScoreType type) {
		return scores.get(type);
	}
	
	public Collection<Score> getScores() {
		return scores.values();
	}
}
