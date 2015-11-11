package org.sphpp.workflow.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.pipeline.FdrCalculator;

public class LPCalculator extends WorkflowModule {
	public LPCalculator() {
		super("Calculates LP values from target and decoy scores.");
		
		Argument arg = new Argument(OPT_IN_TARGET, null, "inTarget");
		arg.setParamName("Target.tsv");
		arg.setDescription("Input TSV file with target scores.");
		addOption(arg);
		
		arg = new Argument(OPT_IN_DECOY, null, "inDecoy");
		arg.setParamName("Decoy.tsv");
		arg.setDescription("Input TSV file with decoy scores.");
		addOption(arg);
		
		arg = new Argument(OPT_OUT_TARGET, null, "outTarget");
		arg.setParamName("LPTarget.tsv");
		arg.setDescription("Output TSV file for target LP values.");
		arg.setDefaultValue("LPTarget.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_OUT_DECOY, null, "outDecoy");
		arg.setParamName("LPDecoy.tsv");
		arg.setDescription("Output TSV file for decoy LP values.");
		arg.setDefaultValue("LPDecoy.tsv.gz");
		addOption(arg);
	}
	
	public static void main( String[] args ) {
		new LPCalculator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		ScoreFile target = ScoreFile.load(getValue(OPT_IN_TARGET));
		ScoreFile decoy = ScoreFile.load(getValue(OPT_IN_DECOY));
		ScoreType type = ScoreFile.selectScore(target.getItems());
		logger.info(String.format("Using '%s' for calculating LP values ...", type.getName()));
		run(target.getItems(), decoy.getItems(), type, ScoreType.LP_SCORE);
		target.save(getValue(OPT_OUT_TARGET), ScoreType.LP_SCORE);
		decoy.save(getValue(OPT_OUT_DECOY), ScoreType.LP_SCORE);
	}

	public static void run( Collection<? extends Decoyable> targetPsms, Collection<? extends Decoyable> decoyPsms, ScoreType type, ScoreType lpScore ) {
		List<Decoyable> list = new ArrayList<>(targetPsms.size()+decoyPsms.size());
		addPsms(list, targetPsms, false);
		addPsms(list, decoyPsms, true);
		FdrCalculator fdr = new FdrCalculator();
		fdr.updateDecoyScores(list, type, lpScore, null, null, null);
		for( Decoyable psm : list ) {			
			Score score = psm.getScoreByType(lpScore);
			score.setValue(-Math.log10(score.getValue()));
		}
	}

	private static void addPsms(List<Decoyable> list, Collection<? extends Decoyable> psms, boolean decoy) {
		for( Decoyable psm : psms ) {
			psm.setDecoy(decoy);
			list.add(psm);
		}
	}
	
	private final static Logger logger = Logger.getLogger(LPCalculator.class.getName());
	private static final int OPT_IN_TARGET = 1;
	private static final int OPT_IN_DECOY = 2;
	private static final int OPT_OUT_TARGET = 3;
	private static final int OPT_OUT_DECOY = 4;
}
