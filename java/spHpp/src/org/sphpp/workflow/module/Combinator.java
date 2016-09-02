package org.sphpp.workflow.module;

import java.util.List;
import java.util.Map;

import org.sphpp.workflow.Utils;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class Combinator extends WorkflowModule {
	public Combinator() {
		super("Selects one score between two score files.");
		
		Argument arg = new Argument(OPT_IN1, null, "input1");
		arg.setParamName("Scores1.tsv");
		arg.setDescription("Input TSV file with scores.");
		addOption(arg);
		
		arg = new Argument(OPT_IN2, null, "input2");
		arg.setParamName("Scores2.tsv");
		arg.setDescription("Input TSV file with scores.");
		addOption(arg);
		
		arg = new Argument(OPT_OUT, null, "output");
		arg.setParamName("Scores.tsv");
		arg.setDescription("Output TSV file with best scores.");
		addOption(arg);
	}
	
	public static void main(String[] args) {
		new Combinator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		//ScoreType lpcScore = ScoreType.LPCORR_SCORE;
		ScoreFile<ScoreItem> items1 = ScoreFile.load(getValue(OPT_IN1));
		ScoreFile<ScoreItem> items2 = ScoreFile.load(getValue(OPT_IN2));
		ScoreType lpcScore1 = items1.selectScore(ScoreType.LPCORR_SCORE, ScoreType.LP_SCORE);
		ScoreType lpcScore2 = items2.selectScore(ScoreType.LPCORR_SCORE, ScoreType.LP_SCORE);
		Map<String,ScoreItem> map = Utils.getMap(items2.getItems());
		for( ScoreItem item : items1.getItems() ) {
			Score score = item.getScoreByType(lpcScore1);
			double value = map.get(item.getId()).getScoreByType(lpcScore2).getValue();
			if( score.compare(value) < 0 )
				score.setValue(value);
		}
		items1.save(getValue(OPT_OUT), lpcScore1);
	}
	
	private static final int OPT_IN1 = 1;
	private static final int OPT_IN2 = 2;
	private static final int OPT_OUT = 3;
}
