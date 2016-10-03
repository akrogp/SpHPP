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

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class ExtCorrector extends WorkflowModule {
	
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
		logger.info(String.format("Using the following scores: LP='%s' LPCorr='%s'", lpScore.getName(), lpcScore.getName()));
		LinkMap<ScoreLink, ScoreLink> map = rel.getScoreLinkMap();
		Utils.addScores(map.getLowerList(), lowerScores.getItems());
		run(map, lpScore, lpcScore);
		ScoreFile.save(rel.getUpperLabel(), map.getUpperList(), getValue(OPT_OUTPUT), lpcScore);
	}

	public void run(LinkMap<ScoreLink, ScoreLink> map, final ScoreType lpScore, ScoreType lpcScore) {
		for( ScoreLink item : map.getUpperList() ) {
			List<ScoreLink> subItems = new ArrayList<>(item.getLinks());
			Collections.sort(subItems, new Comparator<ScoreLink>() {
				@Override
				public int compare(ScoreLink o1, ScoreLink o2) {
					return o2.getScoreByType(lpScore).compare(o1.getScoreByType(lpScore).getValue());
				}
			});
			double score = computeScore(subItems, lpScore);
			item.putScore(new Score(lpcScore, score));
		}
	}

	private double computeScore(List<ScoreLink> subItems, ScoreType lpScore) {
		double loge = Math.log(10.0);
		Double score = null;
		double lp = 0.0;
		double m = 0.0;
		for( ScoreLink subItem : subItems ) {
			m += 1.0 / subItem.getLinks().size();
			lp += subItem.getScoreByType(lpScore).getValue();
			GammaDistribution gamma = new GammaDistribution(m, 1);
			double sum = 1-gamma.cumulativeProbability(lp*loge);
			double lpc = sum < 1e-30 ? 30 : -Math.log10(sum);
			if( score == null )
				score = lpc;
			else if( lpc < score )
				return score;
			score = lpc;
		}
		return score;
	}

	private static final int OPT_INPUT = 1;
	private static final int OPT_REL = 2;
	private static final int OPT_OUTPUT = 3;
	private static final Logger logger = Logger.getLogger(ExtCorrector.class.getName());
}
