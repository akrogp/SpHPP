package org.sphpp.workflow.module;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.Utils;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.Strings;
import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.pipeline.ScoreIntegrator;

public class Corrector extends WorkflowModule {
	public enum Mode {
		POISSON,		// Sum{Poisson*(1-Gamma)}
		POISSON_APPROX,	// 0.75*(1-Gamma)
		GAMMA,			// (1-Gamma)
		LOGN			// LPQ-log(M)
	}
	
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
		
		arg = new Argument(OPT_MODE, null, "mode");
		arg.setChoices(Strings.fromArray(Mode.values()));
		arg.setDescription("LP scores correction mode.");
		arg.setDefaultValue(Mode.POISSON);
		addOption(arg);
		
		addOption(Arguments.getScoreName());
	}
	
	public static void main(String[] args) {
		new Corrector().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		ScoreFile<ScoreItem> items = ScoreFile.load(getValue(OPT_INPUT));
		ScoreFile<ScoreItem> mq = ScoreFile.load(getValue(OPT_M));
		Mode mode = Mode.valueOf(getValue(OPT_MODE));
		ScoreType mScore = selectScore(mq, mode);				
		ScoreType lpScore = ScoreFile.selectScore(items.getItems(), ScoreType.LP_SCORE, ScoreType.LPG_SCORE, ScoreType.LPQ_SCORE);
		ScoreType lpcScore = ScoreType.LPCORR_SCORE;
		logger.info(String.format("Using the following scores: LP='%s' M='%s' LPCorr='%s' (%s)", lpScore.getName(), mScore.getName(), lpcScore.getName(), mode));
		Utils.addScores(items.getItems(), mq.getItems());
		run(items.getItems(), mScore, lpScore, lpcScore, mode);
		items.save(getValue(OPT_OUTPUT), lpcScore);
	}
	
	private ScoreType selectScore(ScoreFile<ScoreItem> mq, Mode mode) {
		String scoreName = getValue(Arguments.OPT_SCORE_NAME); 
		if( scoreName != null )
			return ScoreType.getByName(scoreName);
		ScoreType mScore = null;
		switch( mode ) {
			case GAMMA:
			case LOGN:
				mScore = ScoreFile.selectScore(mq.getItems(), ScoreType.M_OVALUE, ScoreType.MG_OVALUE, ScoreType.MQ_OVALUE);
				break;
			case POISSON:
			case POISSON_APPROX:
				mScore = ScoreFile.selectScore(mq.getItems(), ScoreType.M_EVALUE, ScoreType.MG_EVALUE, ScoreType.MQ_EVALUE);
				break;			
		}
		return mScore;
	}

	public static void run( Collection<? extends Decoyable> items, ScoreType mScore, ScoreType lpScore, ScoreType lpcScore, Mode mode ) {
		switch( mode ) {
			case GAMMA:
				ScoreIntegrator.modelGamma(items, mScore, lpScore, lpcScore);
				break;
			case LOGN:
				ScoreIntegrator.modelLogn(items, mScore, lpScore, lpcScore);
				break;
			case POISSON:
				ScoreIntegrator.modelPoisson(items, mScore, lpScore, lpcScore);
				break;
			case POISSON_APPROX:
				ScoreIntegrator.modelPoissonAprox(items, mScore, lpScore, lpcScore);
				break;
		}
	}

	private static final int OPT_INPUT = 1;
	private static final int OPT_M = 2;
	private static final int OPT_OUTPUT = 3;
	private static final int OPT_MODE = 4;
	private static final Logger logger = Logger.getLogger(Corrector.class.getName());
}
