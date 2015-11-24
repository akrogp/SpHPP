package org.sphpp.workflow.module;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.Utils;
import org.sphpp.workflow.data.Identifiable;
import org.sphpp.workflow.data.LinkMap;
import org.sphpp.workflow.data.Relations;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.data.ScoreLink;
import org.sphpp.workflow.file.RelationFile;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class OccamIntegrator extends WorkflowModule {
	public static class Result {
		public Result(Collection<ScoreLink> upper, Relations relations, double diff, int iters) {
			this.upper = upper;
			this.relations = relations;
			this.diff = diff;
			this.iters = iters;
		}
		public Collection<ScoreLink> getUpper() {
			return upper;
		}
		public Relations getRelations() {
			return relations;
		}
		public double getDiff() {
			return diff;
		}
		public int getIters() {
			return iters;
		}
		private final Collection<ScoreLink> upper;
		private final Relations relations;
		private final double diff;
		private final int iters;
	}

	public OccamIntegrator() {
		super("Loops Integrator, Corrector and Occam modules.");
		
		Argument arg = new Argument(OPT_IN_LP, null, "inputScores");
		arg.setParamName("LPPep.tsv");
		arg.setDescription("Input lower level LP values in TSV format.");
		addOption(arg);
		
		arg = new Argument(OPT_IN_REL, null, "inputRelations");
		arg.setParamName("Pep2Prot.tsv");
		arg.setDescription("Input relations in TSV format.");
		addOption(arg);
		
		arg = new Argument(OPT_IN_M, 'm', null);
		arg.setParamName("MProt.tsv");
		arg.setDescription("Expected M-values in TSV format.");
		addOption(arg);
		
		arg = new Argument(OPT_OUT_LP, null, "outputScores");
		arg.setParamName("LPProt.tsv");
		arg.setDescription("Output upper level LP values in TSV format.");
		addOption(arg);
		
		arg = new Argument(OPT_OUT_LPCORR, null, "outputCorrScores");
		arg.setParamName("LPCorrProt.tsv");
		arg.setDescription("Output upper level LPCorr values in TSV format.");
		addOption(arg);
		
		arg = new Argument(OPT_OUT_REL, null, "outputRelations");
		arg.setParamName("Pep2ProtOccam.tsv");
		arg.setDescription("Output relations in TSV format.");
		arg.setDefaultValue("Pep2ProtOccam.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_DIFF, null, "maxDiff");
		arg.setParamName("diff");
		arg.setDescription("Maximum difference in LP values between iterations.");
		arg.setDefaultValue(0.1);
		addOption(arg);
		
		arg = new Argument(OPT_ITERS, null, "maxIters");
		arg.setParamName("iters");
		arg.setDescription("Maximum number of iterations.");
		arg.setDefaultValue(300);
		addOption(arg);
		
		addOption(Arguments.getDiscard());
	}
	
	public static void main(String[] args) {
		new OccamIntegrator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {		
		ScoreType lpScore = ScoreType.LP_SCORE;
		ScoreType lpcScore = ScoreType.LPCORR_SCORE;
		
		RelationFile rels = RelationFile.load(getValue(OPT_IN_REL), getValue(Arguments.OPT_DISCARD));
		ScoreFile<ScoreItem> lppep = ScoreFile.load(getValue(OPT_IN_LP));
		ScoreType lowerType = ScoreFile.selectScore(lppep.getItems(), ScoreType.LP_SCORE, ScoreType.LPP_SCORE);
		
		ScoreFile<ScoreItem> mq = ScoreFile.load(getValue(OPT_IN_M));
		ScoreType mScore = ScoreFile.selectScore(mq.getItems(), ScoreType.M_EVALUE, ScoreType.MG_EVALUE, ScoreType.MQ_EVALUE);
		
		Result result = run(
			getDoubleValue(OPT_DIFF), getIntValue(OPT_ITERS),
			rels, lppep.getItems(), mq.getItems(),
			lowerType, lpScore, mScore, lpcScore);
		
		ScoreFile.save(rels.getUpperLabel(), result.getUpper(), getValue(OPT_OUT_LP), lpScore);
		ScoreFile.save(rels.getUpperLabel(), result.getUpper(), getValue(OPT_OUT_LPCORR), lpcScore);
		rels.save(result.getRelations(), getValue(OPT_OUT_REL));
	}
	
	public <T extends Identifiable & Decoyable>
	Result run(double maxDiff, int maxIters, Relations rels, Collection<T> lowerItems, Collection<T> mValues, ScoreType lowerType, ScoreType lpScore, ScoreType mScore, ScoreType lpcScore ) {
		if( !rels.hasCoeficients() ) {
			logger.info("Starting with equitative sharing ...");
			rels.setEquitative();			
		}
		logger.info("Integrating lower LP values ...");
		LinkMap<ScoreLink,ScoreLink> linkmap = rels.getLinkMap(Integrator.run(lowerItems,rels));
		logger.info("Combining scores ...");
		Utils.addScores(linkmap.getUpperList(), mValues);
		Utils.addScores(linkmap.getLowerList(), lowerItems);
		
		double diff;
		int iter = 0;		
		do {
			iter++;
			logger.info(String.format("Iteration %d", iter));			
			logger.info("Calculating LPCorr values using fast approximation ...");
			Corrector.run(linkmap.getUpperList(), mScore, lpScore, lpcScore, true);	
			logger.info("Updating relations ...");
			rels = Occam.next(linkmap.getUpperList(), lpcScore);
			logger.info("Updating LP values ...");
			diff = updateLpq(rels, linkmap.getLowerList(), lowerType, linkmap.getUpperList(), lpScore);
			logger.info(String.format(Locale.ENGLISH, "Diff=%f", diff));
		} while( diff > maxDiff && iter < maxIters );
		
		logger.info("Calculating LPCorr values using exact formulation ...");
		Corrector.run(linkmap.getUpperList(), mScore, lpScore, lpcScore, false);
		
		return new Result(linkmap.getUpperList(), rels, diff, iter);
	}
	
	private double updateLpq( Relations rels, Collection<ScoreLink> lowerItems, ScoreType lowerType, Collection<ScoreLink> upperItems, ScoreType upperType ) {
		double dif = 0.0;		
		for( ScoreLink upper : upperItems ) {
			double newScore = 0.0;
			for( ScoreLink lower : upper.getLinks() )
				newScore += rels.get(upper.getId(), lower.getId()).getCoeficient()*lower.getScoreByType(lowerType).getValue();
			Score score = upper.getScoreByType(upperType);
			dif = Math.max(dif, Math.abs(newScore-score.getValue()));			
			score.setValue(newScore);
		}		
		return dif;
	}

	private static final Logger logger = Logger.getLogger(OccamIntegrator.class.getName());
	private static final int OPT_IN_LP = 1;
	private static final int OPT_IN_REL = 2;
	private static final int OPT_IN_M = 3;
	private static final int OPT_OUT_LP = 4;
	private static final int OPT_OUT_LPCORR = 5;
	private static final int OPT_OUT_REL = 6;
	private static final int OPT_DIFF = 7;
	private static final int OPT_ITERS = 8;
}
