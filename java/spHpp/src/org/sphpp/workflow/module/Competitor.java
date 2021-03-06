package org.sphpp.workflow.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.file.PsmFile;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class Competitor extends WorkflowModule {
	public Competitor() {
		super("Makes competition between target and decoy PSMs.");
		
		Argument arg = new Argument(OPT_IN_TARGET, null, "inTarget");
		arg.setParamName("Target.tsv");
		arg.setDescription("Input TSV file with target PSMs.");
		addOption(arg);
		
		arg = new Argument(OPT_IN_DECOY, null, "inDecoy");
		arg.setParamName("Decoy.tsv");
		arg.setDescription("Input TSV file with decoy PSMs.");
		addOption(arg);
		
		arg = new Argument(OPT_OUT_TARGET, null, "outTarget");
		arg.setParamName("TargetComp.tsv");
		arg.setDescription("Output TSV file for target PSMs after competition.");
		arg.setDefaultValue("TargetComp.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_OUT_DECOY, null, "outDecoy");
		arg.setParamName("DecoyComp.tsv");
		arg.setDescription("Output TSV file for decoy PSMs after competition.");
		arg.setDefaultValue("DecoyComp.tsv.gz");
		addOption(arg);
	}
	
	public static void main( String[] args ) {
		new Competitor().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		Set<Psm> targets = PsmFile.load(getValue(OPT_IN_TARGET)).getPsms();
		Set<Psm> decoys = PsmFile.load(getValue(OPT_IN_DECOY)).getPsms();
		
		logger.info("Searching for shared sequences between target and decoy ...");
		Set<String> dups = findDuplicates(targets, decoys);
		targets = removeDups(targets, dups);
		decoys = removeDups(decoys, dups);
		
		ScoreType type = ScoreFile.selectScore(targets);
		logger.info(String.format("Using '%s' for competition ...", type.getName()));
		run(targets, decoys, type);
		PsmFile.save(targets, getValue(OPT_OUT_TARGET), type);
		PsmFile.save(decoys, getValue(OPT_OUT_DECOY), type);
	}
	
	private Set<Psm> removeDups(Set<Psm> psms, Set<String> dups) {
		Set<Psm> result = new HashSet<>();
		for( Psm psm : psms )
			if( !dups.contains(psm.getPeptide().getSequence().toLowerCase()) )
				result.add(psm);
		if( psms.size() == result.size() )
			logger.info("No shared target/decoy sequences found");
		else
			logger.warning(String.format("Removed %d PSMs with same peptide sequence in target and decoy", psms.size()-result.size()));
		return result;
	}

	private Set<String> findDuplicates(Set<Psm> targets, Set<Psm> decoys) {
		Set<String> dups = new HashSet<>();
		Set<String> decoySeqs = new HashSet<>();
		for( Psm decoy : decoys )
			decoySeqs.add(decoy.getPeptide().getSequence().toLowerCase());
		String targetSeq;
		for( Psm target : targets ) {
			targetSeq = target.getPeptide().getSequence().toLowerCase();
			if( decoySeqs.contains(targetSeq) )
				dups.add(targetSeq);
		}
		return dups;
	}

	public static void run( Set<Psm> targets, Set<Psm> decoys, ScoreType type ) {		
		Map<String, Double> mapScores = new HashMap<>();
		addScores(targets, mapScores, type);
		addScores(decoys, mapScores, type);
		getBest(targets, mapScores, type);
		getBest(decoys, mapScores, type);
	}	

	private static void addScores(Set<Psm> psms, Map<String, Double> mapScores, ScoreType type) {
		for( Psm psm : psms ) {
			Score score = psm.getScoreByType(type);
			Double oldScore = mapScores.get(psm.getSpectrum().getUniqueString());
			if( oldScore == null || score.compare(oldScore) > 0 )
				mapScores.put(psm.getSpectrum().getUniqueString(), score.getValue());
		}
	}
	
	private static void getBest(Set<Psm> psms, Map<String, Double> mapScores, ScoreType type) {
		Set<Psm> best = new HashSet<>();
		for( Psm psm : psms ) {
			Score score = psm.getScoreByType(type);
			Double bestScore = mapScores.get(psm.getSpectrum().getUniqueString());
			if( score.compare(bestScore) == 0 )
				best.add(psm);
		}
		psms.clear();
		psms.addAll(best);
	}
	
	private static final Logger logger = Logger.getLogger(Competitor.class.getName());
	private static final int OPT_IN_TARGET = 1;
	private static final int OPT_IN_DECOY = 2;
	private static final int OPT_OUT_TARGET = 3;
	private static final int OPT_OUT_DECOY = 4;
}
