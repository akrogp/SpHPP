package org.sphpp.workflow.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Psm;
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
	}

	public static void run( Collection<Psm> targetPsms, Collection<Psm> decoyPsms, ScoreType psmScore, ScoreType lpScore ) {
		List<Psm> list = new ArrayList<>(targetPsms.size()+decoyPsms.size());
		addPsms(list, targetPsms, false);
		addPsms(list, decoyPsms, true);
		FdrCalculator fdr = new FdrCalculator();
		fdr.updateDecoyScores(list, psmScore, lpScore, null, null, null);
		for( Psm psm : list ) {			
			Score score = psm.getScoreByType(lpScore);
			score.setValue(-Math.log10(score.getValue()));
		}
	}

	private static void addPsms(List<Psm> list, Collection<Psm> psms, boolean decoy) {
		for( Psm psm : psms ) {
			psm.setDecoy(decoy);
			list.add(psm);
		}
	}
	
	private static final int OPT_IN_TARGET = 1;
	private static final int OPT_IN_DECOY = 2;
	private static final int OPT_OUT_TARGET = 3;
	private static final int OPT_OUT_DECOY = 4;
}
