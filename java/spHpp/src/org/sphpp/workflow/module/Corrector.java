package org.sphpp.workflow.module;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.sphpp.workflow.Utils;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.pipeline.ScoreIntegrator;

public class Corrector extends WorkflowModule {
	public Corrector() {
		super("Corrects LP scores according to M values to calculate LPCorr scores.");
		
		Argument arg = new Argument(OPT_INPUT, 'i', "input");
		arg.setParamName("LPScores.tsv");
		arg.setDescription("Input TSV file with LP values.");
		addOption(arg);
		
		arg = new Argument(OPT_M, 'm', null);
		arg.setParamName("M.tsv");
		arg.setDescription("Input TSV file with M values.");
		addOption(arg);
		
		arg = new Argument(OPT_OUTPUT, 'o', "output");
		arg.setParamName("LPCorr.tsv");
		arg.setDescription("Output TSV file for LPCorr values.");
		arg.setDefaultValue("LPCorr.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_APPROX, 'a', "approx");
		arg.setBoolean();
		arg.setDescription("Use approximated calculation (faster).");
		arg.setDefaultValue(false);
		addOption(arg);
	}
	
	public static void main(String[] args) {
		new Corrector().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		ScoreFile<ScoreItem> items = ScoreFile.load(getValue(OPT_INPUT));
		ScoreFile<ScoreItem> mq = ScoreFile.load(getValue(OPT_M));		
		ScoreType mScore = ScoreFile.selectScore(mq.getItems(), ScoreType.M_EVALUE, ScoreType.MG_EVALUE, ScoreType.MQ_EVALUE);
		ScoreType lpScore = ScoreFile.selectScore(items.getItems(), ScoreType.LP_SCORE, ScoreType.LPG_SCORE, ScoreType.LPQ_SCORE);
		ScoreType lpcScore = ScoreType.LPCORR_SCORE;
		logger.info(String.format("Using the following scores: LP='%s' M='%s' LPCorr='%s'", lpScore.getName(), mScore.getName(), lpcScore.getName()));
		Map<String, ScoreItem> map = Utils.getMap(mq.getItems());
		for( ScoreItem item : items.getItems() )
			item.putScore(new Score(mScore, map.get(item.getId()).getScoreByType(mScore).getValue()));
		boolean fast = getBooleanValue(OPT_APPROX);
		run(items.getItems(), mScore, lpScore, lpcScore, fast);
		items.save(getValue(OPT_OUTPUT), lpcScore);
	}
	
	public static void run( Collection<? extends Decoyable> items, ScoreType mScore, ScoreType lpScore, ScoreType lpcScore, boolean fast ) {
		if( fast )
			ScoreIntegrator.modelRandomAprox(items, mScore, lpScore, lpcScore);
		else
			ScoreIntegrator.modelRandom(items, mScore, lpScore, lpcScore);
	}

	private static final int OPT_INPUT = 1;
	private static final int OPT_M = 2;
	private static final int OPT_OUTPUT = 3;
	private static final int OPT_APPROX = 4;
	private static final Logger logger = Logger.getLogger(Corrector.class.getName());
}
