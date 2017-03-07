package org.sphpp.tools;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class Venn {
	public static void main( String[] args ) throws Exception {
		if( args.length != 3 ) {
			System.out.println(String.format("Usage:\n\t%s <FdrFile1.tsv.gz> <FdrFile2.tsv.gz> <FdrFile3.tsv.gz>", Venn.class.getSimpleName()));
			return;
		}
		Set<String> set1 = loadItems(args[0]);
		Set<String> set2 = loadItems(args[1]);
		Set<String> set3 = loadItems(args[2]);
		Set<String> total = new HashSet<>();
		total.addAll(set1);
		total.addAll(set2);
		total.addAll(set3);
		int a = 0, b = 0, c = 0, ab = 0, ac = 0, bc = 0, abc = 0;
		boolean ina, inb, inc;
		int count;
		for( String str : total ) {
			ina = set1.contains(str);
			inb = set2.contains(str);
			inc = set3.contains(str);
			count = 0;
			count += ina ? 1 : 0;
			count += inb ? 1 : 0;
			count += inc ? 1 : 0;
			if( count == 3 )
				abc++;
			else if( count == 1 ) {
				if( ina )
					a++;
				else if( inb )
					b++;
				else if( inc )
					c++;
			} else if( count == 2 ) {
				if( ina && inb )
					ab++;
				else if( ina && inc )
					ac++;
				else if( inb && inc )
					bc++;
			}			
		}
		System.out.println(String.format("%d %d %d %d %d %d %d", a, b, c, ab, ac, bc, abc));
	}
	
	private static Set<String> loadItems( String path ) throws IOException, ParseException {
		ScoreFile<ScoreItem> file = ScoreFile.load(path, ScoreType.Q_VALUE);
		Set<String> set = new HashSet<>();
		for( ScoreItem item : file.getItems() ) {
			Score score = item.getScoreByType(ScoreType.Q_VALUE);			
			if( score != null && score.getValue() < 0.01 )
				set.add(item.getId());
		}
		return set;
	}
}
