package org.sphpp.workflow.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.ScoreType;

public class FdrCalculator extends WorkflowModule {

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
	}
	
	public static void main(String[] args) {
		new FdrCalculator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		ScoreFile<ScoreItem> target = ScoreFile.load(getValue(OPT_IN_TARGET));
		ScoreFile<ScoreItem> decoy = ScoreFile.load(getValue(OPT_IN_DECOY));
		run(target.getItems(), decoy.getItems(), ScoreFile.selectScore(target.getItems()));
		target.save(getValue(OPT_OUT_TARGET));
		decoy.save(getValue(OPT_OUT_DECOY));
	}
	
	public static void run( Set<? extends Decoyable> targets, Set<? extends Decoyable> decoys, ScoreType type ) {
		List<Decoyable> list = new ArrayList<>(targets.size()+decoys.size());
		for( Decoyable target : targets ) {
			target.setDecoy(false);
			list.add(target);
		}
		for( Decoyable decoy : decoys ) {
			decoy.setDecoy(true);
			list.add(decoy);
		}
		es.ehubio.proteomics.pipeline.FdrCalculator fdr = new es.ehubio.proteomics.pipeline.FdrCalculator();
		fdr.updateDecoyScores(list, type, null, ScoreType.LOCAL_FDR, ScoreType.Q_VALUE, null );//ScoreType.FDR_SCORE);
	}

	//private final static Logger logger = Logger.getLogger(FdrCalculator.class.getName());
	private static final int OPT_IN_TARGET = 1;
	private static final int OPT_IN_DECOY = 2;
	private static final int OPT_OUT_TARGET = 3;
	private static final int OPT_OUT_DECOY = 4;
}
