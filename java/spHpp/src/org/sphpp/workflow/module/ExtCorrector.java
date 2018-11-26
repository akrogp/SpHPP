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
	public static final double FDR_THRESHOLD = 0.01;
	
	public enum Mode {
		LP,		// sum LP
		LPF,	// sum LP < FDR
		LPM,	// best LP
		LPFM,	// best LP < FDR
		LPG,	// gamma distribution for N peptides
		LPGM,	// add peptides until reaching the maximum LPG
		LPGC,	// -log ( (p1 x p2 x…x pn)) * COMBINACION(N,n) * FACTORIAL(n))
		LPG1,	// LPQG1=-log(1-(1-p(best))^N)
		LPGN,	// LPQGn=-log(Gamma(PQF,n)*COMBI(N,n))
		LPGB	// -log[1-(1-best(PQG,PQGn)^2]
	}
	
	public ExtCorrector() {
		super("Calculates extended scores.");
		
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
		arg.setDefaultValue(Mode.LPGN);
		addOption(arg);

		addOption(Arguments.getScoreName());
		addOption(Arguments.getDiscard());
	}
	
	public static void main(String[] args) {
		mainTest(args);
	}
	
	public static int mainTest(String[] args) {
		return new ExtCorrector().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		RelationFile rel = RelationFile.load(getValue(OPT_REL), getValue(Arguments.OPT_DISCARD));
		ScoreFile<ScoreItem> lowerScores = ScoreFile.load(getValue(OPT_INPUT));		
		ScoreType lpScore = ScoreFile.selectScore(lowerScores.getItems(),ScoreType.LP_SCORE,ScoreType.LPP_SCORE,ScoreType.LPQ_SCORE,ScoreType.LPG_SCORE);
		ScoreType lpcScore = ScoreType.LPCORR_SCORE;
		Mode mode = Mode.valueOf(getValue(OPT_MODE));
		ScoreType fdrScore = ScoreFile.selectScore(lowerScores.getItems(),ScoreType.Q_VALUE,ScoreType.PEPTIDE_Q_VALUE,ScoreType.PROTEIN_Q_VALUE,ScoreType.GROUP_Q_VALUE);
		logger.info(String.format("Using the following scores: LP='%s' LPCorr='%s'", lpScore.getName(), lpcScore.getName()));
		LinkMap<ScoreLink, ScoreLink> map = rel.getScoreLinkMap();
		Utils.addScores(map.getLowerList(), lowerScores.getItems());
		run(map, lpScore, fdrScore, lpcScore, mode);
		ScoreFile.save(rel.getUpperLabel(), map.getUpperList(), getValue(OPT_OUTPUT), lpcScore);
	}
	
	public void run(LinkMap<ScoreLink, ScoreLink> map, ScoreType lpScore, ScoreType fdrScore, ScoreType lpcScore, Mode mode) {
		for( ScoreLink item : map.getUpperList() ) {
			double score = 0.0;
			switch( mode ) {
				case LP:
					score = runLp(item, lpScore);
					break;
				case LPF:
					score = runLpf(item, lpScore, fdrScore, FDR_THRESHOLD);
					break;
				case LPM:
					score = runLpm(item, lpScore);
					break;
				case LPFM:
					score = runLpfm(item, lpScore, fdrScore, FDR_THRESHOLD);
					break;
				case LPG:
					score = runLpg(item, lpScore);
					break;
				case LPGM:
					score = runLpgm(item, lpScore);
					break;
				case LPGC:
					score = runLpgc(item, lpScore, fdrScore);
					break;												
				case LPG1:
					score = runLpg1(item, lpScore);
					break;
				case LPGN:
					score = runLpgn(item, lpScore, fdrScore, FDR_THRESHOLD);
					break;
				case LPGB:
					score = runLpgb(item, lpScore, fdrScore, FDR_THRESHOLD);
					break;
			}			
			item.putScore(new Score(lpcScore, score>300?300:score));
		}
	}					

	private double runLp(ScoreLink item, ScoreType lpScore) {
		double score = 0.0;
		for( ScoreLink subItem : item.getLinks() ) {
			double coef = 1.0 / subItem.getLinks().size();
			score += subItem.getScoreByType(lpScore).getValue()*coef;
		}
		return score;
	}

	private double runLpf(ScoreLink item, ScoreType lpScore, ScoreType fdrScore, double fdr) {
		double score = 0.0;
		for( ScoreLink subItem : item.getLinks() ) {
			if( subItem.getScoreByType(fdrScore).getValue() >= fdr )
				continue;
			double coef = 1.0 / subItem.getLinks().size();
			score += subItem.getScoreByType(lpScore).getValue()*coef;
		}
		return score;
	}

	private double runLpm(ScoreLink item, ScoreType lpScore) {
		double score = 0.0;
		for( ScoreLink subItem : item.getLinks() ) {
			double coef = 1.0 / subItem.getLinks().size();
			score = Math.max(score, subItem.getScoreByType(lpScore).getValue()*coef);
		}
		return score;
	}
	
	private double runLpfm(ScoreLink item, ScoreType lpScore, ScoreType fdrScore, double fdr) {
		double score = 0.0;
		for( ScoreLink subItem : item.getLinks() ) {
			if( subItem.getScoreByType(fdrScore).getValue() >= fdr )
				continue;
			double coef = 1.0 / subItem.getLinks().size();
			score = Math.max(score, subItem.getScoreByType(lpScore).getValue()*coef);
		}
		return score;
	}
	
	private double runLpg(ScoreLink item, ScoreType lpScore) {
		double m = 0.0;
		double lp = 0.0;
		for( ScoreLink subItem : item.getLinks() ) {
			double coef = 1.0 / subItem.getLinks().size(); 
			m += coef;
			lp += subItem.getScoreByType(lpScore).getValue()*coef;
		}
		double loge = Math.log(10.0);
		GammaDistribution gamma = new GammaDistribution(m, 1);
		double sum = 1-gamma.cumulativeProbability(lp*loge);
		return getLp(sum);
	}

	private double runLpgc(ScoreLink item, ScoreType lpScore, ScoreType fdrScore) {			
		double m = 0.0;
		double lpf = 0.0;
		int n = 0;
		for( ScoreLink subItem : item.getLinks() ) {
			double coef = 1.0 / subItem.getLinks().size(); 
			m += coef;
			double qValue = subItem.getScoreByType(fdrScore).getValue();
			if( qValue >= 0.01 )
				continue;
			n++;
			lpf += subItem.getScoreByType(lpScore).getValue()*coef;
		}
		int N = (int)Math.round(m);
		while( n > 0 ) {
			lpf -= Math.log10(N-n+1);
			n--;
		}
		return lpf;
	}

	private double runLpgm(ScoreLink item, final ScoreType lpScore) {
		List<ScoreLink> subItems = new ArrayList<>(item.getLinks());
		Collections.sort(subItems, new Comparator<ScoreLink>() {
			@Override
			public int compare(ScoreLink o1, ScoreLink o2) {
				return o2.getScoreByType(lpScore).compare(o1.getScoreByType(lpScore).getValue());
			}
		});
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
			double lpc = getLp(sum);
			if( score != null && lpc < score )
				return score;
			score = lpc;
		}
		return score;
	}
	
	private double runLpg1(ScoreLink item, ScoreType lpScore) {
		//LPQG1=-log(1-(1-p(best))^N)			
		double m = 0.0;
		double lpm = 0.0;
		for( ScoreLink subItem : item.getLinks() ) {
			double coef = 1.0 / subItem.getLinks().size(); 
			m += coef;
			lpm = Math.max(lpm, subItem.getScoreByType(lpScore).getValue()*coef);
		}
		double pbest = Math.pow(10.0, -lpm);
		double score = 1.0-Math.pow(1-pbest, m);
		return getLp(score);
	}
	
	private double runLpgn(ScoreLink item, ScoreType lpScore, ScoreType fdrScore, double fdr) {
		// LPQGn=-log(Gamma(PQF,n)*COMBI(N,n))
		double m = 0.0;
		double lpf = 0.0;
		int n = 0;
		for( ScoreLink subItem : item.getLinks() ) {
			double coef = 1.0 / subItem.getLinks().size(); 
			m += coef;
			if( subItem.getScoreByType(fdrScore).getValue() >= fdr )
				continue;
			n++;
			lpf += subItem.getScoreByType(lpScore).getValue()*coef;
		}
		if( n == 0 )
			return runLpg1(item, lpScore);
		double loge = Math.log(10.0);
		GammaDistribution gamma = new GammaDistribution(n, 1);
		double sum = 1-gamma.cumulativeProbability(lpf*loge);
		double lpgn = -Math.log10(sum);//getLp(sum);
		int N = (int)Math.round(m);
		for( int i = n+1; i <= N; i++ )
			lpgn += -Math.log10(i);
		for( int i = 2; i <= N-n; i++ )
			lpgn -= -Math.log10(i);
		return lpgn;
	}
	
	private double runLpgb(ScoreLink item, ScoreType lpScore, ScoreType fdrScore, double fdr) {
		double lpg = runLpg(item, lpScore);
		double lpgn = runLpgn(item, lpScore, fdrScore, fdr);
		double best = Math.max(lpg, lpgn);
		return getLp(1.0-Math.pow(1.0-Math.pow(10.0,-best),2.0));
	}
	
	private double getLp(double score) {
		return score < 1e-300 ? 300 : -Math.log10(score);
	}

	private static final int OPT_INPUT = 1;
	private static final int OPT_REL = 2;
	private static final int OPT_OUTPUT = 3;
	private static final int OPT_MODE = 4;
	private static final Logger logger = Logger.getLogger(ExtCorrector.class.getName());
}
