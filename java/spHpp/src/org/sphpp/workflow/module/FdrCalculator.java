package org.sphpp.workflow.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.Strings;
import es.ehubio.cli.Argument;
import es.ehubio.proteomics.ScoreType;

public class FdrCalculator extends WorkflowModule {
	public enum FdrType { NORMAL, MAYU, PICKED, REFINED };

	protected FdrCalculator() {
		super("Adds columns with different FDR metrics to scores TSV files.");
		
		Argument arg = new Argument(OPT_IN_TARGET, null, "inTarget");
		arg.setParamName("Target.tsv");
		arg.setDescription("Input TSV file with target scores.");
		addOption(arg);
		
		arg = new Argument(OPT_IN_DECOY, null, "inDecoy");
		arg.setParamName("Decoy.tsv");
		arg.setDescription("Input TSV file with decoy scores.");
		addOption(arg);
		
		arg = new Argument(OPT_OUT_TARGET, null, "outTarget");
		arg.setParamName("FdrTarget.tsv");
		arg.setDescription("Output TSV file for target FDR values.");
		arg.setDefaultValue("FdrTarget.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_OUT_DECOY, null, "outDecoy");
		arg.setParamName("FdrDecoy.tsv");
		arg.setDescription("Output TSV file for decoy FDR values.");
		arg.setDefaultValue("FdrDecoy.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_FDR_TYPE, null, "type");
		arg.setChoices(Strings.fromArray(FdrType.values()));
		arg.setDescription("FDR formula to be used.");
		arg.setDefaultValue(FdrType.NORMAL);
		addOption(arg);
		
		arg = new Argument(OPT_MAYU_SIZE, null, "mayuSize");
		arg.setParamName("size");
		arg.setDescription("Number of proteins in the target or decoy database.");
		arg.setDefaultValue("0");
		addOption(arg);
				
		addOption(Arguments.getDecoyPrefix());
	}
	
	public static void main(String[] args) {
		new FdrCalculator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		ScoreFile<ScoreItem> target = ScoreFile.load(getValue(OPT_IN_TARGET));
		ScoreFile<ScoreItem> decoy = ScoreFile.load(getValue(OPT_IN_DECOY));
		FdrType type = FdrType.valueOf(getValue(OPT_FDR_TYPE));		
		run(target.getItems(), decoy.getItems(), ScoreFile.selectScore(target.getItems()),
				type, getIntValue(OPT_MAYU_SIZE), getValue(Arguments.OPT_PREFIX));
		target.save(getValue(OPT_OUT_TARGET));
		decoy.save(getValue(OPT_OUT_DECOY));
	}
	
	public static void run( Set<ScoreItem> targets, Set<ScoreItem> decoys, ScoreType scoreType, FdrType fdrType, int mayuSize, String decoyPrefix ) {
		List<ScoreItem> list = merge(targets, decoys);
		run(list, scoreType, fdrType, mayuSize, decoyPrefix);
	}
	
	private static List<ScoreItem> merge(Set<ScoreItem> targets, Set<ScoreItem> decoys) {
		List<ScoreItem> list = new ArrayList<>(targets.size()+decoys.size());
		for( ScoreItem target : targets ) {
			target.setDecoy(false);
			list.add(target);
		}
		for( ScoreItem decoy : decoys ) {
			decoy.setDecoy(true);
			list.add(decoy);
		}
		return list;
	}
	
	public static void run( Collection<ScoreItem> list, ScoreType scoreType, FdrType fdrType, int mayuSize, String decoyPrefix) {
		es.ehubio.proteomics.pipeline.FdrCalculator fdr;
		switch( fdrType ) {
			case PICKED:
				fdr = es.ehubio.proteomics.pipeline.FdrCalculator.newPickedFdr(decoyPrefix);
				break;
			case NORMAL:
				fdr = es.ehubio.proteomics.pipeline.FdrCalculator.newSeparatedFdr();
				break;
			case MAYU:
				fdr = es.ehubio.proteomics.pipeline.FdrCalculator.newMayuFdr(mayuSize, mayuSize);
				break;
			case REFINED:
				fdr = es.ehubio.proteomics.pipeline.FdrCalculator.newRefinedFdr(decoyPrefix);
				break;
			default:
				throw new UnsupportedOperationException("FDR type not supported");
		}
		fdr.updateDecoyScores(list, scoreType, null, ScoreType.LOCAL_FDR, ScoreType.Q_VALUE, null);//ScoreType.FDR_SCORE);
	}	
	
	//private final static Logger logger = Logger.getLogger(FdrCalculator.class.getName());
	private static final int OPT_IN_TARGET = 1;
	private static final int OPT_IN_DECOY = 2;
	private static final int OPT_OUT_TARGET = 3;
	private static final int OPT_OUT_DECOY = 4;
	private static final int OPT_FDR_TYPE = 5;
	private static final int OPT_MAYU_SIZE = 6;
}
