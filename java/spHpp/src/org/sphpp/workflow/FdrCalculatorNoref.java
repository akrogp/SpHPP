package org.sphpp.workflow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import es.ehubio.tools.Command.Interface;

public class FdrCalculatorNoref implements Interface {
	private static final Logger logger = Logger.getLogger(FdrCalculatorNoref.class.getName());
	
	private static class Item implements Comparable<Item> {
		public String id;
		public double score;
		public double fdr = -1.0f;
		boolean decoy = false;
		@Override
		public int compareTo(Item o) {
			return (int)Math.signum(score-o.score);
		}
	}

	@Override
	public String getUsage() {
		return "<input_target.tsv> <input_decoy.tsv> <fdr> <output_target.tsv> <output_decoy.tsv>";
	}

	@Override
	public int getMinArgs() {
		return 5;
	}

	@Override
	public int getMaxArgs() {
		return 5;
	}

	@Override
	public void run(String[] args) throws Exception {
		String inputTarget = args[0];
		String inputDecoy = args[1];
		double fdr = Double.parseDouble(args[2]);
		String outputTarget = args[3];
		String outputDecoy = args[4];
		
		List<Item> list = loadFiles(inputTarget, inputDecoy);
		calculateFdr(list);
		list = filter(list, fdr);
		saveFiles(list, outputTarget, outputDecoy);
	}
	
	private List<Item> filter( List<Item> list, double fdr ) {
		List<Item> result = new ArrayList<>();
		for( Item item : list )
			if( item.fdr <= fdr )
				result.add(item);
		return result;
	}
	
	private void calculateFdr( List<Item> list ) {
		Collections.sort(list);
		
		Map<Double,Double> mapFdr = new HashMap<>();
		
		int decoy = 0;
		int target = 0;
		for( int i = list.size()-1; i >= 0; i-- ) {
			Item item = list.get(i);
			if( item.decoy )
				decoy++;
			else
				target++;
			mapFdr.put(item.score, ((double)decoy)/target);
		}
		
		for( Item item : list )
			item.fdr = mapFdr.get(item.score);
	}
	
	private List<Item> loadFiles( String tsvTarget, String tsvDecoy ) throws IOException {
		List<Item> listDecoy = loadFile(tsvDecoy,true);
		List<Item> listTarget = loadFile(tsvTarget,false);		
		listTarget.addAll(listDecoy);
		return listTarget;
	}
	
	private List<Item> loadFile( String tsv, boolean decoy ) throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader(tsv));
		String line;
		String[] fields;
		List<Item> list = new ArrayList<>();
		Item item = null;
		boolean header = true;
		while( (line=rd.readLine()) != null ) {			
			fields = line.split("\\t");
			if( header ) {
				idName = fields[0];
				scoreName = fields[1];
				header = false;
			} else {
				item = new Item();
				item.id = fields[0];
				item.score = Double.parseDouble(fields[1]);
				item.decoy = decoy;
				list.add(item);
			}
		}
		rd.close();
		logger.info(String.format("Loaded %s %s from %s", list.size(), decoy?"decoys":"targets", tsv));
		return list;
	}
	
	private void saveFiles( List<Item> list, String tsvTarget, String tsvDecoy ) throws IOException {
		List<Item> listTarget = new ArrayList<>();
		List<Item> listDecoy = new ArrayList<>();
		for( Item item : list )
			if( item.decoy )
				listDecoy.add(item);
			else
				listTarget.add(item);
		saveFile(listTarget, tsvTarget);
		saveFile(listDecoy, tsvDecoy);
	}
	
	private void saveFile( List<Item> list, String tsv ) throws IOException {
		PrintWriter wr = new PrintWriter(tsv);
		wr.println(String.format("%s\t%s\t%s", idName, scoreName, "localFdr"));
		int targets = 0;
		int decoys = 0;
		for( Item item : list ) {
			wr.println(String.format("%s\t%s\t%s", item.id, item.score, item.fdr));
			if( item.decoy )
				decoys++;
			else
				targets++;
		}
		logger.info(String.format("Saved %s targets and %s decoys into %s", targets, decoys, tsv));
		wr.close();
	}
	
	private String idName, scoreName;
}