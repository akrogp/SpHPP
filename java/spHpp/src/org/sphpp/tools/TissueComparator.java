package org.sphpp.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.io.Streams;
import es.ehubio.proteomics.ScoreType;

public class TissueComparator {
	public static void main(String[] args) throws Exception {
		run(DATA, "XTandem", SCORE);
		run(DATA, "Comet", SCORE);
	}
	
	public static void run(String data, String engine, String score) throws IOException, ParseException {
		File dir = new File("/media/gorka/EhuBio/Lego");
		File[] tissues = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains("Adult_") || name.contains("Fetal_");
			}
		});
		
		ScoreType lpScore = ScoreType.LPCORR_SCORE;
		ScoreType qScore = ScoreType.Q_VALUE;
		Map<String, List<Double>> map = new HashMap<>();
		
		int iTissue = 0;
		for( File tissue : tissues ) {
			LOGGER.info(String.format("Loading tissue %s ...", tissue.getName()));
			ScoreFile<ScoreItem> file = ScoreFile.load(String.format("%s/%s/%s/%s/FdrGenTarget.tsv.gz",data,tissue.getName(),engine,score), lpScore, qScore);
			for( ScoreItem item : file.getItems() ) {
				double fdr = item.getScoreByType(qScore).getValue();
				if( fdr >= 0.01 )
					continue;
				List<Double> scores = map.get(item.getId());
				if( scores == null ) {
					scores = new ArrayList<>();
					map.put(item.getId(), scores);
				}
				while( scores.size() < iTissue )
					scores.add(null);				
				scores.add(item.getScoreByType(lpScore).getValue());
			}
			iTissue++;
		}
		
		LOGGER.info("Loading proteome ...");
		ScoreFile<ScoreItem> file = ScoreFile.load(String.format("%s/Proteome/%s/LPG1-%s/FdrGenTarget.tsv.gz",data,engine,score), lpScore, qScore);
		for( ScoreItem item : file.getItems() ) {
			List<Double> scores = map.get(item.getId());
			if( scores == null || scores.isEmpty() )
				continue;
			while( scores.size() < tissues.length )
				scores.add(null);			
			scores.add(item.getScoreByType(lpScore).getValue());
			scores.add(item.getScoreByType(qScore).getValue());
		}
		
		LOGGER.info("Writing results ...");
		PrintWriter pw = new PrintWriter(Streams.getTextWriter(String.format("%s/Summary/LPGenTargetTissues-%s.tsv",data,engine)));
		pw.print("gene");
		for( File tissue : tissues )
			pw.print(String.format("\t%s",tissue.getName()));
		pw.println(String.format("\tProteome (%s)\tProteome (%s)", lpScore.getName(), qScore.getName()));
		for( Entry<String, List<Double>> entry : map.entrySet() ) {
			if( entry.getValue().isEmpty() )
				continue;
			pw.print(entry.getKey());
			for( Double s : entry.getValue() ) {
				pw.print('\t');
				if( s!= null )
					pw.print(s);
			}
			pw.println();
		}
		pw.close();
		
		LOGGER.info("Finished!!");
	}

	private final static Logger LOGGER = Logger.getLogger(TissueComparator.class.getName());
	private static final String DATA = "/media/gorka/EhuBio/Lego";
	private static final String SCORE = "LPGN-FDRr";
}
