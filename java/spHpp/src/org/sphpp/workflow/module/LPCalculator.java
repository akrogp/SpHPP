package org.sphpp.workflow.module;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import org.sphpp.workflow.data.ScoreItem;
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
		ScoreFile<ScoreItem> target = ScoreFile.load(getValue(OPT_IN_TARGET));
		ScoreFile<ScoreItem> decoy = ScoreFile.load(getValue(OPT_IN_DECOY));
		ScoreType type = ScoreFile.selectScore(target.getItems());
		logger.info(String.format("Using '%s' for calculating LP values ...", type.getName()));
		run(target.getItems(), decoy.getItems(), type, ScoreType.LP_SCORE);
		save(target, getValue(OPT_OUT_TARGET));
		save(decoy, getValue(OPT_OUT_DECOY));
	}

	private void save(ScoreFile<ScoreItem> file, String path) throws FileNotFoundException, IOException {
		final ScoreType type = ScoreType.LP_SCORE;
		List<ScoreItem> list = new ArrayList<>(file.getItems());
		list.sort(new Comparator<ScoreItem>() {
			@Override
			public int compare(ScoreItem o1, ScoreItem o2) {
				return o1.getScoreByType(type).compare(o2.getScoreByType(type).getValue());
			}
		});
		ScoreFile.save(file.getId(), list, path, type);
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
			
			// Test using a parametric Gamma distribution
			/*Score xcorr = psm.getScoreByType(ScoreType.SEQUEST_XCORR);
			score.setValue(
				-Math.log10(
					GAMMA_TEST.cumulativeProbability(
						-Math.log10(xcorr.getValue()) + 4
					)
				)
			);*/
		}
	}

	private static void addPsms(List<Decoyable> list, Collection<? extends Decoyable> psms, boolean decoy) {
		for( Decoyable psm : psms ) {
			psm.setDecoy(decoy);
			list.add(psm);
		}
	}
	
	private final static Logger logger = Logger.getLogger(LPCalculator.class.getName());
	//private final static GammaDistribution GAMMA_TEST = new GammaDistribution(830.679455239117, 0.004929571847063);
	private static final int OPT_IN_TARGET = 1;
	private static final int OPT_IN_DECOY = 2;
	private static final int OPT_OUT_TARGET = 3;
	private static final int OPT_OUT_DECOY = 4;
}
