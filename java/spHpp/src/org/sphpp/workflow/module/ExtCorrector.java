package org.sphpp.workflow.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.Utils;
import org.sphpp.workflow.data.LinkMap;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.data.ScoreLink;
import org.sphpp.workflow.file.RelationFile;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.Strings;
import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class ExtCorrector extends WorkflowModule {
	public enum Mode {
		LPC_MAX,	// lpc3: add peptides until reaching the maximum lpc2
		LPC_CFG		// -log ( (p1 x p2 x…x pn)) * COMBINACION(N,n) * FACTORIAL(n))
	}
	
	public ExtCorrector() {
		super("Calculates LPCorr(max).");
		
		Argument arg = new Argument(OPT_INPUT, 'i', "input");
		arg.setParamName("LPScores.tsv");
		arg.setDescription("Input TSV file with lower level LP values.");
		addOption(arg);
		
		arg = new Argument(OPT_REL, 'r', "relations");
		arg.setParamName("Lower2Upper.tsv");
		arg.setDescription("Input TSV file with lower to upper relations (upper id in the first column).");
		addOption(arg);
		
		arg = new Argument(OPT_OUTPUT, 'o', "output");
		arg.setParamName("LPCorr.tsv");
		arg.setDescription("Output TSV file for LPCorr values.");
		arg.setDefaultValue("LPCorr.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_MODE, null, "mode");
		arg.setChoices(Strings.fromArray(Mode.values()));
		arg.setDescription("LP scores correction mode.");
		arg.setDefaultValue(Mode.LPC_CFG);
		addOption(arg);

		addOption(Arguments.getScoreName());
		addOption(Arguments.getDiscard());
	}
	
	public static void main(String[] args) {
		new ExtCorrector().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		RelationFile rel = RelationFile.load(getValue(OPT_REL), getValue(Arguments.OPT_DISCARD));
		ScoreFile<ScoreItem> lowerScores = ScoreFile.load(getValue(OPT_INPUT));		
		ScoreType lpScore = ScoreFile.selectScore(lowerScores.getItems(),ScoreType.LP_SCORE,ScoreType.LPP_SCORE,ScoreType.LPQ_SCORE,ScoreType.LPG_SCORE);
		ScoreType lpcScore = ScoreType.LPCORR_SCORE;
		Mode mode = Mode.valueOf(getValue(OPT_MODE));
		ScoreType fdrScore = mode != Mode.LPC_CFG ? null : ScoreFile.selectScore(lowerScores.getItems(),ScoreType.Q_VALUE,ScoreType.PEPTIDE_Q_VALUE,ScoreType.PROTEIN_Q_VALUE,ScoreType.GROUP_Q_VALUE);
		logger.info(String.format("Using the following scores: LP='%s' LPCorr='%s'", lpScore.getName(), lpcScore.getName()));
		LinkMap<ScoreLink, ScoreLink> map = rel.getScoreLinkMap();
		Utils.addScores(map.getLowerList(), lowerScores.getItems());
		run(map, lpScore, fdrScore, lpcScore, mode);
		ScoreFile.save(rel.getUpperLabel(), map.getUpperList(), getValue(OPT_OUTPUT), lpcScore);
	}
	
	public void run(LinkMap<ScoreLink, ScoreLink> map, ScoreType lpScore, ScoreType fdrScore, ScoreType lpcScore, Mode mode) {
		switch( mode ) {
			case LPC_MAX:
				runMax(map, lpScore, lpcScore);
				break;
			case LPC_CFG:
				runCfg(map, lpScore, fdrScore, lpcScore);
				break;
		}
	}

	private void runCfg(LinkMap<ScoreLink, ScoreLink> map, ScoreType lpScore, ScoreType fdrScore, ScoreType lpcScore) {
		for( ScoreLink item : map.getUpperList() ) {			
			double m = 0.0;
			double lp = 0.0;
			int n = 0;
			for( ScoreLink subItem : item.getLinks() ) {
				double coef = 1.0 / subItem.getLinks().size(); 
				m += coef;
				double qValue = subItem.getScoreByType(fdrScore).getValue();
				if( qValue >= 0.01 )
					continue;
				n++;
				lp += subItem.getScoreByType(lpScore).getValue()*coef;
			}
			int N = (int)Math.round(m);
			while( n > 0 ) {
				lp -= Math.log10(N-n+1);
				n--;
			}
			item.putScore(new Score(lpcScore, lp>30?30:lp));
		}
	}

	private void runMax(LinkMap<ScoreLink, ScoreLink> map, final ScoreType lpScore, ScoreType lpcScore) {
		for( ScoreLink item : map.getUpperList() ) {
			List<ScoreLink> subItems = new ArrayList<>(item.getLinks());
			Collections.sort(subItems, new Comparator<ScoreLink>() {
				@Override
				public int compare(ScoreLink o1, ScoreLink o2) {
					return o2.getScoreByType(lpScore).compare(o1.getScoreByType(lpScore).getValue());
				}
			});
			double score = computeScoreMax(subItems, lpScore);
			item.putScore(new Score(lpcScore, score));
		}
	}

	private double computeScoreMax(List<ScoreLink> subItems, ScoreType lpScore) {
		double loge = Math.log(10.0);
		Double score = null;
		double lp = 0.0;
		double m = 0.0;		
		for( ScoreLink subItem : subItems ) {
			double coef = 1.0 / subItem.getLinks().size(); 
			m += coef;
			lp += subItem.getScoreByType(lpScore).getValue()*coef;
			GammaDistribution gamma = new GammaDistribution(m, 1);
			double sum = 1-gamma.cumulativeProbability(lp*loge);
			double lpc = sum < 1e-30 ? 30 : -Math.log10(sum);
			if( score != null && lpc < score )
				return score;
			score = lpc;
		}
		return score;
	}

	private static final int OPT_INPUT = 1;
	private static final int OPT_REL = 2;
	private static final int OPT_OUTPUT = 3;
	private static final int OPT_MODE = 4;
	private static final Logger logger = Logger.getLogger(ExtCorrector.class.getName());
}
