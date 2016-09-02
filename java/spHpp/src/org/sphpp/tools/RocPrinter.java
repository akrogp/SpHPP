package org.sphpp.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.io.Streams;
import es.ehubio.proteomics.ScoreType;

public class RocPrinter {
	public static void main(String[] args) throws Exception {		
		List<ScoreItem> items = loadItems();
		Map<Integer,Integer> roc = getRoc(items);
		printRoc(roc);
	}
	
	private static void printRoc(Map<Integer, Integer> roc) {
		for( Map.Entry<Integer, Integer> entry : roc.entrySet() ) {
			System.out.print(entry.getKey());
			System.out.print('\t');
			System.out.println(entry.getValue());
		}
	}

	private static Map<Integer, Integer> getRoc(List<ScoreItem> items) {
		Map<Integer, Integer> result = new TreeMap<>();
		int offset = 0;
		result.put(0, 0);
		while( offset < items.size() ) {			
			int tp = 0, fp = 0;
			for( int i = offset; i < items.size(); i++ )
				if( Boolean.TRUE.equals(items.get(i).getDecoy()) )
					fp++;
				else
					tp++;
			result.put(fp, tp);
			double score = items.get(offset).getScoreByType(SCORE).getValue();
			offset++;
			while( offset < items.size() && items.get(offset).getScoreByType(SCORE).getValue() == score )
				offset++;
		}
		return result;
	}

	private static List<ScoreItem> loadItems() throws Exception {
		ScoreFile<ScoreItem> data = ScoreFile.load(DATA, SCORE);
		List<String> positives = Streams.readLines(POSITIVES);
		List<ScoreItem> items = new ArrayList<>();
		for( ScoreItem item : data.getItems() ) {
			item.setDecoy(!positives.contains(item.getId()));			
			items.add(item);
			if( item.getScoreByType(SCORE).getValue() == 0.0 && Boolean.TRUE.equals(item.getDecoy()) )
				System.out.println(item.getId());
		}
		Collections.sort(items, new Comparator<ScoreItem>() {
			@Override
			public int compare(ScoreItem o1, ScoreItem o2) {
				return o1.getScoreByType(SCORE).compare(o2.getScoreByType(SCORE).getValue());
			}
		});
		return items;
	}

	private static final ScoreType SCORE= ScoreType.Q_VALUE;
	//private static final String DATA = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/Scerevisiae/ROC/LegoEqui.csv";
	//private static final String DATA = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/Scerevisiae/ROC/LegoOccam.csv";
	//private static final String DATA = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/Scerevisiae/ROC/MaxLP.csv";
	//private static final String DATA = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/Scerevisiae/ROC/LegoEqui2.csv";
	//private static final String DATA = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/Scerevisiae/ROC/MaxLP2.csv";
	private static final String DATA = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/Scerevisiae/ROC/Comb.csv";
	private static final String POSITIVES = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/Scerevisiae/yeast_3nonMSdatasets_4MSdatasets_in2ormore_union.dat";
}
