package org.sphpp.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.Numbers;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class FdrPlotter {
	public static void main(String[] args) throws Exception {
		if( args.length != 1 ) {
			System.out.println(String.format("Usage:\n\t%s <FdrFile.tsv.gz>", FdrPlotter.class.getSimpleName()));
			return;
		}
		final ScoreType scoreType = ScoreType.Q_VALUE;
		ScoreFile<ScoreItem> file = ScoreFile.load(args[0], scoreType);
		List<Double> list = new ArrayList<>();
		Score score;
		for( ScoreItem item : file.getItems() )
			if( (score=item.getScoreByType(scoreType)) != null && score.getValue() <= 0.02 )
				list.add(score.getValue());
		Collections.sort(list, new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return (int)Math.signum(o2-o1);
			}
		});
		int count = list.size();
		Double prev = null;
		for( Double fdr : list ) {
			if( prev == null || fdr.compareTo(prev) != 0 )				
				System.out.println(String.format("%s %d", Numbers.toString(fdr), count));
			prev = fdr;
			count--;
		}		
	}
}
