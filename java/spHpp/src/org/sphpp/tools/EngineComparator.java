package org.sphpp.tools;

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

public class EngineComparator {
	public static void main( String[] args ) throws Exception {
		run( DATA, TISSUE, ENGINES );
		LOGGER.info("finished!");
	}

	public static void run(String data, String tissue, String[] engines) throws IOException, ParseException {
		Map<String, List<Double>> mapPep = new HashMap<>();
		ScoreType lpScore = ScoreType.LP_SCORE;
		ScoreType qScore = ScoreType.Q_VALUE;
		int iEngine = 0;		
		for( String engine : engines ) {
			long fdrCount = 0;
			//ScoreFile<ScoreItem> file = ScoreFile.load(String.format("%s/%s/%s/FdrPepTarget.tsv.gz",data,tissue,engine), lpScore, qScore);
			ScoreFile<ScoreItem> file = ScoreFile.load(String.format("%s/%s/%s/FdrPepDecoy.tsv.gz",data,tissue,engine), lpScore, qScore);
			for( ScoreItem item : file.getItems() ) {
				List<Double> scores = mapPep.get(item.getId());
				if( scores == null ) {
					scores = new ArrayList<>();
					mapPep.put(item.getId(), scores);
				}
				while( scores.size()/2 < iEngine ) {
					scores.add(null);
					scores.add(null);
				}
				scores.add(item.getScoreByType(lpScore).getValue());
				double fdr = item.getScoreByType(qScore).getValue();
				scores.add(fdr);
				if( fdr < 0.01 )
					fdrCount++;
			}
			iEngine++;
			LOGGER.info(String.format("%s: %d of %d peptides", engine, fdrCount, file.getItems().size()));
		}
		LOGGER.info(String.format("Combination: %d peptides", mapPep.size()));
		
		//PrintWriter pw = new PrintWriter(Streams.getTextWriter(String.format("%s/%s/LPPepTargetEngines.tsv",data,tissue)));
		PrintWriter pw = new PrintWriter(Streams.getTextWriter(String.format("%s/%s/LPPepDecoyEngines.tsv",data,tissue)));
		pw.print("peptide");
		for( String engine : engines )
			pw.print(String.format("\t%s (%s)\t%s (%s)",
					lpScore.getName(), engine, qScore.getName(), engine));
		pw.println();
		for( Entry<String, List<Double>> entry : mapPep.entrySet() ) {
			pw.print(entry.getKey());
			for( Double score : entry.getValue() ) {
				pw.print('\t');
				if( score != null )
					pw.print(score);
			}
			pw.println();
		}
		pw.close();		
	}

	private final static Logger LOGGER = Logger.getLogger(EngineComparator.class.getName());
	private static final String DATA = "/media/gorka/EhuBio/Lego";
	private static final String TISSUE = "Adult_Heart";
	private static final String[] ENGINES = {"XTandem", "Comet", "XTandem+Comet-NoCal"};
}
