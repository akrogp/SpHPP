package org.sphpp.workflow.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.Identifiable;
import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.RelationFile;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;

public class Integrator extends WorkflowModule {
	public Integrator() {
		super("Integrates upper level scores by summing lower level scores.");
		
		Argument arg = new Argument(OPT_LOWER, 'i', "input");
		arg.setParamName("ScoreLower.tsv");
		arg.setDescription("Input TSV file with lower level scores.");
		addOption(arg);
		
		arg = new Argument(OPT_REL, 'r', "relations");
		arg.setParamName("Lower2Upper.tsv");
		arg.setDescription("Input TSV file with lower to upper relations (upper id in the first column).");
		addOption(arg);
		
		arg = new Argument(OPT_UPPER, 'o', "output");
		arg.setParamName("ScoreUpper.tsv");
		arg.setDescription("Output TSV file with upper level scores.");
		addOption(arg);
		
		addOption(Arguments.getDiscard());
	}
	
	public static void main( String[] args ) {
		new Integrator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		RelationFile rel = RelationFile.load(getValue(OPT_REL),getValue(Arguments.OPT_DISCARD));
		if( !rel.hasCoeficients() )
			rel.setEquitative();
		ScoreFile<ScoreItem> lower = ScoreFile.load(getValue(OPT_LOWER));
		ScoreFile<ScoreItem> upper = new ScoreFile<ScoreItem>(rel.getUpperLabel());
		upper.setItems(run(lower.getItems(),rel));
		upper.save(getValue(OPT_UPPER));
	}
	
	public static <T extends Identifiable & Decoyable> Set<ScoreItem> run(Set<T> lowerSet, RelationFile relations) throws Exception {		
		Map<String,T> lowerMap = new HashMap<>();		
		for( T lower : lowerSet )
			lowerMap.put(lower.getId(), lower);
		Map<String,ScoreItem> upperMap = new HashMap<>();
		for( Relation rel : relations.getEntries() ) {
			T lower = lowerMap.get(rel.getLowerId());
			if( lower == null )
				throw new Exception(String.format("Referenced lower item '%s' not found", rel.getLowerId()));
			ScoreItem upper = upperMap.get(rel.getUpperId());
			if( upper == null ) {
				upper = new ScoreItem(rel.getUpperId());
				upperMap.put(upper.getId(), upper);
				for( Score score : lower.getScores() )
					upper.putScore(new Score(score.getType(), score.getName(), 0.0));
			}
			for( Score lowerScore : lower.getScores() ) {
				Score upperScore = upper.getScoreByType(lowerScore.getType());
				double coef = rel.getCoeficient() == null ? 1.0 : rel.getCoeficient();
				upperScore.setValue(upperScore.getValue()+lowerScore.getValue()*coef);
			}
		}
		return new HashSet<ScoreItem>(upperMap.values());
	}

	private static final int OPT_LOWER = 1;
	private static final int OPT_REL = 2;
	private static final int OPT_UPPER = 3;
}
