package org.sphpp.workflow.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.Strings;
import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Score;
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
		run(target.getItems(), decoy.getItems(), ScoreFile.selectScore(target.getItems()), type, getValue(Arguments.OPT_PREFIX));
		target.save(getValue(OPT_OUT_TARGET));
		decoy.save(getValue(OPT_OUT_DECOY));
	}
	
	public static void run( Set<ScoreItem> targets, Set<ScoreItem> decoys, ScoreType scoreType, FdrType fdrType, String decoyPrefix ) {
		/*if( fdrType == FdrType.REFINED )
			runRefined(targets, decoys, scoreType, decoyPrefix);
		else {*/
			List<ScoreItem> list = merge(targets, decoys);
			run(list, scoreType, fdrType, decoyPrefix);
		//}
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
	
	public static void run( Collection<ScoreItem> list, ScoreType scoreType, FdrType fdrType, String decoyPrefix) {
		es.ehubio.proteomics.pipeline.FdrCalculator fdr;
		switch( fdrType ) {
			case PICKED:
				list = doCompetition(list, scoreType, decoyPrefix);
				fdr = es.ehubio.proteomics.pipeline.FdrCalculator.newSeparatedFdr();
				break;
			case NORMAL:
				fdr = es.ehubio.proteomics.pipeline.FdrCalculator.newSeparatedFdr();
				break;
			case MAYU:
				fdr = es.ehubio.proteomics.pipeline.FdrCalculator.newMayuFdr(0);
				break;
			case REFINED:
				fdr = es.ehubio.proteomics.pipeline.FdrCalculator.newRefinedFdr(decoyPrefix);
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
	
	public static void runRefined(Collection<ScoreItem> targets, Collection<ScoreItem> decoys, ScoreType scoreType, String decoyPrefix) {
		List<ScoreItem> sortedTargets = createSortedList(targets, scoreType);
		List<ScoreItem> sortedDecoys = createSortedList(decoys, scoreType);
		double th = getMaxScore(sortedTargets.get(0), sortedDecoys.get(0), scoreType), fdr;
		int d0, db=0, to, tb=0;
		int t1 = 0, t2 = 0, d1 = 0, d2 = 0;
		while( t2 < targets.size() || d2 < decoys.size() ) {
			while( t2 < targets.size() && sortedTargets.get(t2).getScoreByType(scoreType).compare(th) >= 0 )
				t2++;
			while( d2 < decoys.size() && sortedDecoys.get(d2).getScoreByType(scoreType).compare(th) >= 0 )
				d2++;			
			for( int i = t1; i < t2; i++ ) {
				ScoreItem target = sortedTargets.get(i);				
				ScoreItem decoy = findItem(sortedDecoys, d2, decoyPrefix+target.getId());
				if( decoy != null )
					if( decoy.getScoreByType(scoreType).compare(target.getScoreByType(scoreType).getValue()) > 0 )
						db++;
					else if( target.getScoreByType(scoreType).compare(decoy.getScoreByType(scoreType).getValue()) > 0 )
						tb++;
			}
			for( int i = d1; i < d2; i++ ) {
				ScoreItem decoy = sortedDecoys.get(i);
				ScoreItem target = findItem(sortedTargets, t2, decoy.getId().replaceFirst(decoyPrefix, ""));
				if( target != null )
					if( target.getScoreByType(scoreType).compare(decoy.getScoreByType(scoreType).getValue()) > 0 )
						tb++;
					else if( decoy.getScoreByType(scoreType).compare(target.getScoreByType(scoreType).getValue()) > 0 )
						db++;
			}
			to = t2-tb-db;
			d0 = d2-db-tb;
			fdr = ((double)d0+2*db)/((double)db+tb+to);
			for( int i = t1; i < t2; i++ )
				sortedTargets.get(i).putScore(new Score(ScoreType.LOCAL_FDR, fdr));
			for( int i = d1; i < d2; i++ )
				sortedDecoys.get(i).putScore(new Score(ScoreType.LOCAL_FDR, fdr));
			System.out.println(String.format("t2=%d/%d d2=%d/%d to=%d do=%d tb=%d db=%d th=%f",t2,targets.size(),d2,decoys.size(),to,d0,tb,db,th));
			t1 = t2; d1 = d2;
			if( t1 == sortedTargets.size() && d1 < sortedDecoys.size() )
				th = sortedDecoys.get(d1).getScoreByType(scoreType).getValue();
			else if( d1 == sortedDecoys.size() && t1 < sortedTargets.size() )
				th = sortedTargets.get(t1).getScoreByType(scoreType).getValue();
			else if( t1 < sortedTargets.size() && d1 < sortedDecoys.size() )
				th = getMaxScore(sortedTargets.get(t1), sortedDecoys.get(d1), scoreType);
		}
	}
	
	private static ScoreItem findItem(Collection<ScoreItem> items, int limit, String id) {
		for( ScoreItem item : items ) {
			if( --limit < 0 )
				return null;
			if( item.getId().equals(id) )
				return item;
		}
		return null;
	}
	
	private static double getMaxScore( ScoreItem i1, ScoreItem i2, ScoreType scoreType ) {
		double score1 = i1.getScoreByType(scoreType).getValue();
		double score2 = i2.getScoreByType(scoreType).getValue();
		return scoreType.compare(score1, score2) >= 0 ? score1 : score2; 
	}
	
	private static List<ScoreItem> createSortedList(Collection<ScoreItem> items, final ScoreType scoreType) {
		List<ScoreItem> list = new ArrayList<>(items);
		list.sort(new Comparator<ScoreItem>() {
			@Override
			public int compare(ScoreItem o1, ScoreItem o2) {
				return o2.getScoreByType(scoreType).compare(o1.getScoreByType(scoreType).getValue());
			}
		});
		return list;
	}

	private final static Logger logger = Logger.getLogger(FdrCalculator.class.getName());
	private static final int OPT_IN_TARGET = 1;
	private static final int OPT_IN_DECOY = 2;
	private static final int OPT_OUT_TARGET = 3;
	private static final int OPT_OUT_DECOY = 4;
	private static final int OPT_FDR_TYPE = 5;
}
