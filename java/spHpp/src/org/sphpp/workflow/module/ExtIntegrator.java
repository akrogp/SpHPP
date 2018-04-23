package org.sphpp.workflow.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.Strings;
import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class ExtIntegrator extends WorkflowModule {
	public enum Mode {
		LPG1,	// 1-(1-min(p))^r
		LPG,	// gamma(sum(LPQ),t)
		LPGN,	// -log(Gamma(LPQF,n)*COMBI(N,n))
		HK0,	// House Keeping: FDR<1% in all tissues
		HK1,	// House Keeping test1: 1-gamma(prod(1-p),t)
		HK2,	// House Keeping test2: P(housekeeping, N tejidos)=p(peor)^count(N)
		BEST,	// min(p)
		FILTER,	// FDRi < 1%
		MERGE	// BEST if score > 0
	}
	
	public ExtIntegrator() {
		super("Integrates individual results to experiment level results.");
		
		Argument arg = new Argument(OPT_INPUT, 'i', "input");
		arg.setParamName("ScoresInput1.tsv,ScoresInput2.tsv,...");
		arg.setDescription("Inputs TSV score files.");
		addOption(arg);
		
		arg = new Argument(OPT_MODE, 'm', "mode");
		arg.setChoices(Strings.fromArray(Mode.values()));
		arg.setDescription("Probabilities integration mode.");
		addOption(arg);
		
		arg = new Argument(OPT_OUTPUT, 'o', "output");
		arg.setParamName("ScoresOutput.tsv");
		arg.setDescription("Output TSV score file.");
		addOption(arg);
	}
	
	public static void main(String[] args) {
		new ExtIntegrator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		String[] inputs = getValue(OPT_INPUT).split(","); 
		ScoreFile<ScoreItem> file = ScoreFile.load(inputs[0]);
		ScoreType scoreType = file.selectScore(ScoreType.LPCORR_SCORE, ScoreType.LP_SCORE);
		Mode mode = Mode.valueOf(getValue(OPT_MODE));
		List<Set<ScoreItem>> list = new ArrayList<>();
		list.add(file.getItems());		
		for( int i = 1; i < inputs.length; i++ )
			if( mode == Mode.FILTER || mode == Mode.LPGN || mode == Mode.HK0 )
				list.add(ScoreFile.load(inputs[i], scoreType, ScoreType.Q_VALUE).getItems());
			else
				list.add(ScoreFile.load(inputs[i], scoreType).getItems());
		Collection<ScoreItem> output = run(list, scoreType, mode);
		ScoreFile.save(file.getId(), output, getValue(OPT_OUTPUT), scoreType);
	}
	
	private Collection<ScoreItem> run(List<Set<ScoreItem>> list, ScoreType scoreType, Mode mode) {
		Collection<ScoreItem> items = integrate(list, scoreType, mode);
		if( mode == Mode.HK0 )
			return filter(items, list.size());
		finalize(items, scoreType, list.size(), mode);
		return items;
	}	

	private Collection<ScoreItem> integrate(List<Set<ScoreItem>> list, ScoreType scoreType, Mode mode) {
		Map<String, ScoreItem> map = new HashMap<>();
		for( Set<ScoreItem> input : list ) {
			for( ScoreItem item : input ) {
				if( (mode == Mode.FILTER || mode == Mode.LPGN || mode == Mode.HK0)
						&& item.getScoreByType(ScoreType.Q_VALUE).getValue() >= 0.01 )
					continue;
				Score score = item.getScoreByType(scoreType);
				if( mode == Mode.HK1 ) {						
					double p = Math.pow(10.0, -score.getValue());
					score.setValue(getLp(1.0-p));
				}
				ScoreItem prev = map.get(item.getId());
				if( prev == null ) {
					if( mode == Mode.MERGE && score.getValue() == 0 )
						continue;
					map.put(item.getId(), item);
					if( mode == Mode.FILTER || mode == Mode.LPGN || mode == Mode.HK2 || mode == Mode.HK0 )
						item.putScore(new Score(ScoreType.ID_COUNT, 1));
				} else {
					Score prevScore = prev.getScoreByType(scoreType);
					if( mode == Mode.LPG1 || mode == Mode.BEST || mode == Mode.FILTER || mode == Mode.HK0 || mode == Mode.MERGE )
						prevScore.setValue(Math.max(prevScore.getValue(), score.getValue()));
					else if( mode == Mode.HK2 )
						prevScore.setValue(Math.min(prevScore.getValue(), score.getValue()));
					else
						prevScore.setValue(prevScore.getValue()+score.getValue());
					if( mode == Mode.FILTER || mode == Mode.LPGN || mode == Mode.HK2 || mode == Mode.HK0 ) {
						Score countScore = prev.getScoreByType(ScoreType.ID_COUNT);
						countScore.setValue(countScore.getValue()+1);
					}
				}
			}
		}
		return map.values();
	}
	
	private void finalize(Collection<ScoreItem> items, ScoreType scoreType, int n, Mode mode) {
		if( mode == Mode.BEST || mode == Mode.FILTER )
			return;
		for( ScoreItem item : items ) {
			Score score = item.getScoreByType(scoreType);
			switch( mode ) {			
				case LPG1:
					double p = Math.pow(10.0, -score.getValue());
					score.setValue(getLp(1.0-Math.pow(1.0-p,n)));
					break;
				case LPG:
					score.setValue(getLp(gamma(score.getValue(),n)));
					break;
				case LPGN:
					finalizeLpgn(score, item.getScoreByType(ScoreType.ID_COUNT), n);
					break;
				case HK1:
					score.setValue(getLp(1.0-gamma(score.getValue(),n)));
					break;
				case HK2:
					/*if( item.getScoreByType(ScoreType.ID_COUNT).getValue() < n )
						score.setValue(0.0);
					else
						score.setValue(n*score.getValue());*/
					score.setValue(item.getScoreByType(ScoreType.ID_COUNT).getValue()*score.getValue());
					break;
				default:
					return;
			}
		}		
	}
	
	private Collection<ScoreItem> filter(Collection<ScoreItem> items, int n) {
		List<ScoreItem> list = new ArrayList<>();
		for( ScoreItem item : items )
			if( item.getScoreByType(ScoreType.ID_COUNT).getValue() == n )
				list.add(item);
		return list;
	}
	
	private void finalizeLpgn(Score score, Score count, int N) {
		if( count == null || count.getValue() == 0 ) {
			double p = Math.pow(10.0, -score.getValue());
			score.setValue(getLp(1.0-Math.pow(1.0-p,N)));
			return;
		}
		int n = (int)count.getValue();
		double lpgn = -Math.log10(gamma(score.getValue(), n));
		for( int i = n+1; i <= N; i++ )
			lpgn += -Math.log10(i);
		for( int i = 2; i <= N-n; i++ )
			lpgn -= -Math.log10(i);
		score.setValue(lpgn>300?300:lpgn);
	}

	private static double gamma(double lp, int n) {
		double loge = Math.log(10.0);
		GammaDistribution gamma = new GammaDistribution(n, 1);
		double sum = 1-gamma.cumulativeProbability(lp*loge);
		return sum;
	}	

	private static double getLp(double score) {
		return score < 1e-300 ? 300 : -Math.log10(score);
	}

	private static final int OPT_INPUT = 1;
	private static final int OPT_MODE = 2;
	private static final int OPT_OUTPUT = 3;
}
