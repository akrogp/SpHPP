package org.sphpp.workflow.module;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.Utils;
import org.sphpp.workflow.data.Identifiable;
import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.data.Relations;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.RelationFile;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class FdrTagger extends WorkflowModule {

	public FdrTagger() {
		super("Marks relations with low FDR in lower items.");
		
		Argument arg = new Argument(OPT_IN_RELS, null, "inputRelations");
		arg.setParamName("Lower2Upper.tsv");
		arg.setDescription("Input TSV file with lower to upper relations.");
		addOption(arg);
		
		arg = new Argument(OPT_IN_SCORES, null, "scores");
		arg.setParamName("Scores.tsv");
		arg.setDescription("Input TSV file with lower scores.");
		addOption(arg);
		
		arg = new Argument(OPT_OUT_RELS, null, "outputRelations");
		arg.setParamName("Lower2UpperTagged.tsv");
		arg.setDescription("Output TSV file with tagged lower to upper relations.");
		addOption(arg);
		
		arg = new Argument(OPT_TH, 'f', "fdr");
		arg.setParamName("threshold");
		arg.setDescription("FDR threshold.");
		arg.setDefaultValue(0.01);
		addOption(arg);
		
		addOption(Arguments.getDiscard());
	}
	
	public static void main(String[] args) {
		new FdrTagger().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		RelationFile file = RelationFile.load(getValue(OPT_IN_RELS), getValue(Arguments.OPT_DISCARD));
		ScoreFile<ScoreItem> items = ScoreFile.load(getValue(OPT_IN_SCORES));
		ScoreType type = ScoreFile.selectScore(items.getItems(), ScoreType.FDR_SCORE, ScoreType.Q_VALUE, ScoreType.LOCAL_FDR);		
		Score threshold = new Score(type, getDoubleValue(OPT_TH));
		logger.info(String.format("Using '%s' as FDR threshold ...", threshold.toString()));
		run(file,items.getItems(),threshold);
		file.save(getValue(OPT_OUT_RELS));
	}
	
	public static <T extends Identifiable & Decoyable>
	void run(Relations rels, Set<T> items, Score threshold) {
		Map<String,T> map = Utils.getMap(items);
		for( Relation rel : rels.getEntries() )
			if( map.get(rel.getLowerId()).getScoreByType(threshold.getType()).compare(threshold.getValue()) < 0 )
				rel.addLabel("LowFdr");
	}
	
	private static final Logger logger = Logger.getLogger(FdrTagger.class.getName());
	private static final int OPT_IN_RELS = 1;
	private static final int OPT_IN_SCORES = 2;
	private static final int OPT_OUT_RELS = 3;
	private static final int OPT_TH = 4;
}
