package org.sphpp.workflow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import es.ehubio.tools.Command.Interface;

public class Integrator implements Interface {
	private static final Logger logger = Logger.getLogger(Integrator.class.getName());
	
	private static class Item {
		public String id;
		public double score;
	}

	@Override
	public String getUsage() {
		return "<input_score.tsv> <relations.tsv> <output_score.tsv>";
	}

	@Override
	public int getMinArgs() {
		return 3;
	}

	@Override
	public int getMaxArgs() {
		return 3;
	}

	@Override
	public void run(String[] args) throws Exception {
		String tsvInput = args[0];
		String tsvRelations = args[1];
		String tsvOutput = args[2];
		
		List<Item> inputList = loadFile(tsvInput);
		Map<String,String> relations = loadRelations(tsvRelations);
		List<Item> outputList = integrate(inputList, relations);
		saveFile(outputList, tsvOutput);
	}
	
	private void saveFile(List<Item> list, String tsv) throws IOException {
		PrintWriter wr = new PrintWriter(tsv);
		wr.println(String.format("%s\t%s", highIdName, scoreName));
		for( Item item : list )
			wr.println(String.format("%s\t%s", item.id, item.score));
		wr.close();
		logger.info(String.format("%s results saved into %s", list.size(), tsv));
	}

	private List<Item> integrate(List<Item> inputList, Map<String, String> relations) {
		Map<String,Item> result = new HashMap<>();
		for( Item item : inputList ) {
			String highId = relations.get(item.id);
			if( highId == null ) {
				logger.severe(String.format("No mapping for %s", item.id));
				continue;
			}
			Item res = result.get(highId);
			if( res == null ) {
				res = new Item();
				res.id = highId;
				res.score = item.score;
				result.put(highId, res);
			} else
				res.score += item.score;
		}
		return new ArrayList<>(result.values());
	}

	private List<Item> loadFile( String tsv ) throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader(tsv));
		String line, lowIdName = null;
		String[] fields;
		List<Item> list = new ArrayList<>();
		Item item = null;
		boolean header = true;
		while( (line=rd.readLine()) != null ) {			
			fields = line.split("\\t");
			if( header ) {
				lowIdName = fields[0];
				scoreName = fields[1];				
				header = false;
			} else {
				item = new Item();
				item.id = fields[0];
				item.score = Double.parseDouble(fields[1]);
				list.add(item);
			}
		}
		rd.close();
		logger.info(String.format("Loaded %s scores (%s) for %s from %s", list.size(), scoreName, lowIdName, tsv));
		return list;
	}
	
	private Map<String,String> loadRelations( String tsv ) throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader(tsv));
		String line;
		String[] fields;
		Map<String,String> map = new HashMap<>();
		boolean header = true;
		while( (line=rd.readLine()) != null ) {			
			fields = line.split("\\t");
			if( header ) {
				highIdName = fields[0];
				header = false;
			} else if( map.put(fields[1],fields[0]) != null )
				logger.severe(String.format("Duplicated %s key", fields[1]));
		}
		rd.close();
		logger.info(String.format("Loaded %s mappings from %s", map.size(), tsv));
		return map;
	}
	
	private String highIdName;
	private String scoreName;
}
