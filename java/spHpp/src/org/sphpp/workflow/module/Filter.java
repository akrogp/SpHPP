package org.sphpp.workflow.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sphpp.workflow.file.PsmFile;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class Filter extends WorkflowModule {
	public Filter() {
		super("Filters entries from PSMS SpHPP TSV file.");
		
		Argument arg = new Argument(OPT_INPUT, 'i', "input");
		arg.setParamName("Psms.tsv");
		arg.setDescription("Input TSV file with PSM entries.");
		addOption(arg);
		
		arg = new Argument(OPT_OUTPUT, 'o', "output");
		arg.setParamName("Filter.tsv");
		arg.setDescription("Output TSV file.");
		arg.setDefaultValue("Filter.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_RANK, 'r', "rank");
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
		new Filter().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		MsMsData data = PsmFile.load(getValue(OPT_INPUT));
		Set<Psm> psms = data.getPsms();
		psms = run(psms, getIntValue(OPT_RANK), getBooleanValue(OPT_FEATURE), getBooleanValue(OPT_BEST_PSM), ScoreFile.selectScore(psms));
		PsmFile.save(psms, getValue(OPT_OUTPUT));
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

	private static final int OPT_INPUT = 1;
	private static final int OPT_OUTPUT = 2;
	private static final int OPT_RANK = 3;
	private static final int OPT_FEATURE = 4;
	private static final int OPT_BEST_PSM = 5;
}
