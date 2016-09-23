package org.sphpp.workflow.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.Utils;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.Strings;
import es.ehubio.cli.Argument;
import es.ehubio.proteomics.ScoreType;

public class FdrCalculator extends WorkflowModule {
	public enum FdrType { NORMAL, MAYU, COMP };

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
		int binSize = getIntValue(OPT_BIN_SIZE);
		if( type == FdrType.MAYU && binSize > 0 ) {
			ScoreFile<ScoreItem> sizes = ScoreFile.load(getValue(OPT_M_TARGET));
			Utils.addScores(target.getItems(), sizes.getItems());
			sizes = ScoreFile.load(getValue(OPT_M_DECOY));
			Utils.addScores(decoy.getItems(), sizes.getItems());
		}
		run(target.getItems(), decoy.getItems(), ScoreFile.selectScore(target.getItems()), type, binSize, getValue(Arguments.OPT_PREFIX));
		target.save(getValue(OPT_OUT_TARGET));
		decoy.save(getValue(OPT_OUT_DECOY));
	}
	
	public static void run( Set<ScoreItem> targets, Set<ScoreItem> decoys, ScoreType scoreType, FdrType fdrType, int binSize, String decoyPrefix ) {
		List<ScoreItem> list = merge(targets, decoys);
		if( fdrType != FdrType.MAYU || binSize == 0 )
			run(list, scoreType, fdrType, binSize, decoyPrefix);
		else {
			final ScoreType sizeType = ScoreFile.selectScore(targets, ScoreType.M_DVALUE, ScoreType.N_DVALUE, ScoreType.M_EVALUE, ScoreType.N_EVALUE);
			if( sizeType == null )
				throw new RuntimeException("Missing protein size information for MAYU database partitioning");
			List<ScoreItem> total = sortBySize(list, sizeType);
			List<ScoreItem> part = new ArrayList<>();
			int off = 0, n;
			double m;
			do {
				part.clear();
				n = 0;
				m = 0.0;
				do {
					ScoreItem item = total.get(off);
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
				run(part, scoreType, fdrType, binSize, decoyPrefix);
			} while( off < total.size() );
		}
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
	
	private static List<ScoreItem> sortBySize( Collection<ScoreItem> set, final ScoreType sizeType ) {
		List<ScoreItem> list = new ArrayList<>(set.size());
		list.addAll(set);
		Collections.sort(list, new Comparator<ScoreItem>() {
			@Override
			public int compare(ScoreItem o1, ScoreItem o2) {
				// largerBetter=false -> ascending order
				return o2.getScoreByType(sizeType).compare(o1.getScoreByType(sizeType).getValue());
			}
		});
		return list;
	}
		
	public static void run( Collection<ScoreItem> list, ScoreType scoreType, FdrType fdrType, int binSize, String decoyPrefix) {
		es.ehubio.proteomics.pipeline.FdrCalculator fdr;
		switch( fdrType ) {
			case COMP:
				list = doCompetition(list, scoreType, decoyPrefix);
			case NORMAL:
				fdr = es.ehubio.proteomics.pipeline.FdrCalculator.newSeparatedFdr();
				break;
			case MAYU:
				fdr = es.ehubio.proteomics.pipeline.FdrCalculator.newMayuFdr(binSize);
				break;
			default:
				throw new UnsupportedOperationException("FDR type not supported");
		}
		fdr.updateDecoyScores(list, scoreType, null, ScoreType.LOCAL_FDR, ScoreType.Q_VALUE, null);//ScoreType.FDR_SCORE);
	}

	private static List<ScoreItem> doCompetition(Collection<ScoreItem> list, ScoreType scoreType, String decoyPrefix) {
		List<ScoreItem> result = new ArrayList<>();
		Map<String, ScoreItem> map = new HashMap<>();
		for( ScoreItem item : list ) {
			String id = item.getId();
			if( Boolean.TRUE.equals(item.getDecoy()) )
				id = id.replaceAll(decoyPrefix, "");
			ScoreItem prev = map.get(id);
			if( prev != null ) {
				int comp = item.getScoreByType(scoreType).compare(prev.getScoreByType(scoreType).getValue());
				if( comp < 0 )
					continue;
				if( comp == 0 )
					result.add(prev);	// target and decoy with the same score
			}
			map.put(id, item);
		}
		result.addAll(map.values());
		logger.info(String.format("%d (target+decoy) entries resulted into %d after competition", list.size(), result.size()));
		return result;
	}

	private final static Logger logger = Logger.getLogger(FdrCalculator.class.getName());
	private static final int OPT_IN_TARGET = 1;
	private static final int OPT_IN_DECOY = 2;
	private static final int OPT_OUT_TARGET = 3;
	private static final int OPT_OUT_DECOY = 4;
	private static final int OPT_FDR_TYPE = 5;
	private static final int OPT_M_TARGET = 6;
	private static final int OPT_M_DECOY = 7;
	private static final int OPT_BIN_SIZE = 8;
}
