package org.sphpp.workflow.module;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.file.PepFile;
import org.sphpp.workflow.file.PsmFile;
import org.sphpp.workflow.file.RelationFile;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class PsmFilter extends WorkflowModule {
	public PsmFilter() {
		super("Filters entries from PSMS SpHPP TSV file.");
		
		Argument arg = new Argument(OPT_INPUT, 'i', "input");
		arg.setParamName("Psms.tsv");
		arg.setDescription("Input TSV file with PSM entries.");
		addOption(arg);
		
		arg = new Argument(OPT_OUTPUT_PSM, null, "outputPsm");
		arg.setParamName("FilterPsm.tsv");
		arg.setDescription("Output TSV file.");
		arg.setDefaultValue("FilterPsm.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_OUTPUT_PEP, null, "outputPep");
		arg.setParamName("FilterPep.tsv");
		arg.setDescription("Output peptide TSV file.");
		arg.setDefaultValue("FilterPep.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_PSM2PEP, 'r', "relations");
		arg.setParamName("Psm2Pep.tsv");
		arg.setDescription("Output relations file between filtered PSMs and peptides.");
		arg.setDefaultValue("Psm2Pep.tsv.gz");
		addOption(arg);				
		
		arg = new Argument(OPT_RANK, null, "rank");
		arg.setParamName("rank");
		arg.setDescription("PSM rank threshold.");
		arg.setDefaultValue(1);
		addOption(arg);
		
		arg = new Argument(OPT_FEATURE, 'f', "features");
		arg.setBoolean();
		arg.setDescription("Selects only the best PSM per precursor.");
		arg.setDefaultValue(true);
		addOption(arg);
		
		arg = new Argument(OPT_BEST_PSM, 'b', "bestPsm");
		arg.setBoolean();
		arg.setDescription("Selects only the best PSM per peptide.");
		arg.setDefaultValue(false);
		addOption(arg);
	}
	
	public static void main( String[] args ) {
		new PsmFilter().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		MsMsData data = PsmFile.load(getValue(OPT_INPUT));
		Set<Psm> psms = data.getPsms();
		psms = run(psms, getIntValue(OPT_RANK), getBooleanValue(OPT_FEATURE), getBooleanValue(OPT_BEST_PSM), ScoreFile.selectScore(psms));
		logger.info(String.format("Saving %s PSMs ...", psms.size()));
		PsmFile.save(psms, getValue(OPT_OUTPUT_PSM));
		Set<Peptide> peptides = getPeptides(psms);
		logger.info(String.format("Saving %s peptides ...", peptides.size()));
		PepFile.save(peptides, getValue(OPT_OUTPUT_PEP));
		logger.info("Saving peptide to protein relations ...");
		saveRelations(psms, getValue(OPT_PSM2PEP));		
	}	

	public static Set<Psm> run( Set<Psm> psms, int rank, boolean feature, boolean best, ScoreType type ) {
		if( rank >= 0 )
			psms = filterRank(psms, rank);
		if( type != null )
			if( best )
				psms = filterBest(psms, type);
			else if( feature )
				psms = filterFeature(psms, type);
		return psms;
	}
	
	public static Set<Psm> filterRank( Set<Psm> inputPsms, int rank ) {
		if( rank <= 0 )
			return inputPsms;
		Set<Psm> outputPsms = new HashSet<>();
		for( Psm psm : inputPsms )
			if( psm.getRank() <= rank )
				outputPsms.add(psm);
		return outputPsms;
	}
	
	public static Set<Psm> filterFeature( Collection<Psm> inputPsms, ScoreType type ) {
		Set<Psm> result = new HashSet<>();
		Map<Integer,Double> bestScores = new HashMap<>();
		Map<Integer,Psm> bestPsms = new HashMap<>();
		Double prev, cur;
		for( Peptide peptide : getPeptides(inputPsms) ) {
			bestScores.clear();
			bestPsms.clear();
			for( Psm psm : peptide.getPsms() ) {
				Score score = psm.getScoreByType(type);
				if( score == null )
					continue;
				prev = bestScores.get(psm.getCharge());
				cur = score.getValue();
				if( prev == null || type.compare(cur,prev) > 0 ) {
					bestScores.put(psm.getCharge(), cur);
					bestPsms.put(psm.getCharge(), psm);
				}
			}
			for( Psm psm : peptide.getPsms() )
				if( psm.equals(bestPsms.get(psm.getCharge())) )
					result.add(psm);
		}
		return result;
	}
	
	public static Set<Psm> filterBest( Collection<Psm> inputPsms, ScoreType type ) {
		Set<Psm> result = new HashSet<>();
		for( Peptide peptide : getPeptides(inputPsms) ) {
			Psm best = peptide.getBestPsm(type);
			for( Psm psm : peptide.getPsms() )
				if( psm == best )
					result.add(psm);
		}
		return result;
	}
	
	private static Set<Peptide> getPeptides( Collection<Psm> inputPsms ) {
		Set<Peptide> peptides = new HashSet<Peptide>();
		for( Psm psm : inputPsms )
			peptides.add(psm.getPeptide());
		return peptides;
	}
	
	private void saveRelations(Set<Psm> psms, String path) throws IOException {
		RelationFile file = new RelationFile("peptide", "psm");
		for( Psm psm : psms )
			file.addEntry(new Relation(psm.getPeptide().getUniqueString(), psm.getUniqueString()));
		file.save(path);
	}

	private static final int OPT_INPUT = 1;
	private static final int OPT_OUTPUT_PSM = 2;
	private static final int OPT_RANK = 3;
	private static final int OPT_FEATURE = 4;
	private static final int OPT_BEST_PSM = 5;
	private static final int OPT_PSM2PEP = 6;
	private static final int OPT_OUTPUT_PEP = 7;
	private static final Logger logger = Logger.getLogger(PsmFilter.class.getName());
}
