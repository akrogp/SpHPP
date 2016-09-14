package org.sphpp.workflow.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.sphpp.workflow.Utils;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.pipeline.FdrCalculator.FdrFormula;

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
		
		arg = new Argument(OPT_USE_MAYU, null, "useMayu");
		arg.setBoolean();
		arg.setDescription("Use Mayu correction for FDR calculation.");
		arg.setDefaultValue(false);
		addOption(arg);
		
		arg = new Argument(OPT_M_TARGET, null, "mTarget");
		arg.setParamName("MTarget.tsv");
		arg.setDescription("Input TSV file with target proteins sizes to be used by Mayu.");
		arg.setDefaultValue("MdbProtTarget.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_M_DECOY, null, "mDecoy");
		arg.setParamName("MDecoy.tsv");
		arg.setDescription("Input TSV file with decoy proteins sizes to be used by Mayu.");
		arg.setDefaultValue("MdbProtDecoy.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_BIN_SIZE, null, "binSize");
		arg.setParamName("size");
		arg.setDescription("Bin size used for Mayu database partitioning.");
		arg.setDefaultValue("0");
		addOption(arg);
	}
	
	public static void main(String[] args) {
		new FdrCalculator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		ScoreFile<ScoreItem> target = ScoreFile.load(getValue(OPT_IN_TARGET));
		ScoreFile<ScoreItem> decoy = ScoreFile.load(getValue(OPT_IN_DECOY));
		boolean mayu = getBooleanValue(OPT_USE_MAYU);
		int binSize = getIntValue(OPT_BIN_SIZE);
		if( mayu && binSize > 0 ) {
			ScoreFile<ScoreItem> sizes = ScoreFile.load(getValue(OPT_M_TARGET));
			Utils.addScores(target.getItems(), sizes.getItems());
			sizes = ScoreFile.load(getValue(OPT_M_DECOY));
			Utils.addScores(decoy.getItems(), sizes.getItems());
		}
		run(target.getItems(), decoy.getItems(), ScoreFile.selectScore(target.getItems()), mayu, binSize);
		target.save(getValue(OPT_OUT_TARGET));
		decoy.save(getValue(OPT_OUT_DECOY));
	}
	
	public static void run( Set<? extends Decoyable> targets, Set<? extends Decoyable> decoys, ScoreType type, boolean mayu, int binSize ) {
		if( !mayu || binSize == 0 )
			run(targets, decoys, type, mayu);
		else {
			final ScoreType sizeType = ScoreFile.selectScore(targets, ScoreType.M_DVALUE, ScoreType.N_DVALUE, ScoreType.M_EVALUE, ScoreType.N_EVALUE);
			if( sizeType == null )
				throw new RuntimeException("Missing protein size information for MAYU database partitioning");
			List<Decoyable> total = sortBySize(merge(targets, decoys), sizeType);
			List<Decoyable> part = new ArrayList<>();
			int off = 0, n;
			double m;
			do {
				part.clear();
				n = 0;
				m = 0.0;
				do {
					Decoyable item = total.get(off);
					off++;
					part.add(item);
					if( !Boolean.TRUE.equals(item.getDecoy()) ) {
						n++;
						m = item.getScoreByType(sizeType).getValue();
					} else if( n == binSize && item.getScoreByType(sizeType).getValue() == m )
						n--;
					// Group two last bins if necessary
					if( n == binSize && total.size() - off < binSize )
						n = 0;
				} while( off < total.size() && n < binSize );
				run(part, type, mayu);
			} while( off < total.size() );
		}
	}
	
	private static List<Decoyable> merge(Set<? extends Decoyable> targets, Set<? extends Decoyable> decoys) {
		List<Decoyable> list = new ArrayList<>(targets.size()+decoys.size());
		for( Decoyable target : targets ) {
			target.setDecoy(false);
			list.add(target);
		}
		for( Decoyable decoy : decoys ) {
			decoy.setDecoy(true);
			list.add(decoy);
		}
		return list;
	}
	
	private static List<Decoyable> sortBySize( Collection<? extends Decoyable> set, final ScoreType sizeType ) {
		List<Decoyable> list = new ArrayList<>(set.size());
		list.addAll(set);
		Collections.sort(list, new Comparator<Decoyable>() {
			@Override
			public int compare(Decoyable o1, Decoyable o2) {
				// largerBetter=false -> ascending order
				return o2.getScoreByType(sizeType).compare(o1.getScoreByType(sizeType).getValue());
			}
		});
		return list;
	}
	
	public static void run( Set<? extends Decoyable> targets, Set<? extends Decoyable> decoys, ScoreType type, boolean mayu) {
		List<Decoyable> list = merge(targets, decoys);
		run(list, type, mayu);
	}
	
	public static void run( Collection<? extends Decoyable> set, ScoreType type, boolean mayu) {
		es.ehubio.proteomics.pipeline.FdrCalculator fdr = new es.ehubio.proteomics.pipeline.FdrCalculator(mayu?FdrFormula.MAYU:FdrFormula.DT);
		fdr.updateDecoyScores(set, type, null, ScoreType.LOCAL_FDR, ScoreType.Q_VALUE, null);//ScoreType.FDR_SCORE);
	}

	//private final static Logger logger = Logger.getLogger(FdrCalculator.class.getName());
	private static final int OPT_IN_TARGET = 1;
	private static final int OPT_IN_DECOY = 2;
	private static final int OPT_OUT_TARGET = 3;
	private static final int OPT_OUT_DECOY = 4;
	private static final int OPT_USE_MAYU = 5;
	private static final int OPT_M_TARGET = 6;
	private static final int OPT_M_DECOY = 7;
	private static final int OPT_BIN_SIZE = 8;
}
