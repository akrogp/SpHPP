package org.sphpp.tools;

import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class Counter {
	public static void main( String[] args ) throws Exception {
		if( args.length != 1 ) {
			System.out.println(String.format("Usage:\n\t%s <FdrFile.tsv.gz>", Counter.class.getSimpleName()));
			return;
		}
		ScoreFile<ScoreItem> file = ScoreFile.load(args[0], ScoreType.Q_VALUE);
		int count = 0;
		for( ScoreItem item : file.getItems() ) {
			Score score = item.getScoreByType(ScoreType.Q_VALUE);
			if( score != null && score.getValue() < 0.01 )
				count++;
		}
		String[] dirs = args[0].split("/");
		System.out.println(String.format("%s %d", dirs[dirs.length-2], count));
	}
}
