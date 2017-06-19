package org.sphpp.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import es.ehubio.Numbers;
import es.ehubio.io.CsvReader;
import es.ehubio.io.Streams;

public class SearchComparator {
	public static class Psm {
		public String getEngine() {
			return engine;
		}
		public void setEngine(String engine) {
			this.engine = engine;
		}
		public String getPeptide() {
			return peptide;
		}
		public void setPeptide(String peptide) {
			this.peptide = peptide;
		}
		public boolean isDecoy() {
			return decoy;
		}
		public void setDecoy(boolean decoy) {
			this.decoy = decoy;
		}
		public int getCharge() {
			return charge;
		}
		public void setCharge(int charge) {
			this.charge = charge;
		}
		public double getScore() {
			return score;
		}
		public void setScore(double score) {
			this.score = score;
		}
		public String getScan() {
			return scan;
		}
		public void setScan(String scan) {
			this.scan = scan;
		}
		public int getRank() {
			return rank;
		}
		public void setRank(int rank) {
			this.rank = rank;
		}
		public double getMass() {
			return mass;
		}
		public void setMass(double mass) {
			this.mass = mass;
		}
		private String scan, engine, peptide;
		private boolean decoy;
		private int charge, rank;		
		private double score, mass;
	}
	
	public static void main(String[] args) throws Exception {
		List<Psm> list= new ArrayList<>();
		
		for( int i = 0; i < ENGINES.length; i++ ) {
			list.addAll(loadData(String.format("%s/%s/PsmTarget.tsv.gz", DATA, ENGINES[i]), ENGINES[i], false));
			list.addAll(loadData(String.format("%s/%s/PsmDecoy.tsv.gz", DATA, ENGINES[i]), ENGINES[i], true));
		}
		
		saveData(list, String.format("%s/SearchComp.tsv", DATA));
		
		LOGGER.info("finished!!");
	}
	
	private static void saveData(List<Psm> list, String path) throws FileNotFoundException, IOException {
		LOGGER.info(String.format("Saving '%s' ...", path));
		
		PrintWriter pw = new PrintWriter(Streams.getTextWriter(path));
		pw.println("scan\tengine\tpeptide\tdecoy\tcharge\tmass\trank\tscore");
		for( Psm psm : list ) {
			pw.print(psm.getScan());
			pw.print('\t');
			pw.print(psm.getEngine());
			pw.print('\t');
			pw.print(psm.getPeptide());
			pw.print('\t');
			pw.print(psm.isDecoy());
			pw.print('\t');
			pw.print(psm.getCharge());
			pw.print('\t');
			pw.print(Numbers.toString(psm.getMass()));
			pw.print('\t');
			pw.print(psm.getRank());
			pw.print('\t');
			pw.print(Numbers.toString(psm.getScore()));
			pw.println();
		}
		pw.close();
	}

	private static List<Psm> loadData(String path, String engine, boolean decoy) throws Exception {
		LOGGER.info(String.format("Loading '%s' ...", path));
		CsvReader csv = new CsvReader("\t", true);
		csv.open(path);
		List<Psm> list = new ArrayList<>();
		while( csv.readLine() != null ) {			
			/*String spectrum = csv.getField(1);
			String scan, file, fields[];
			if( spectrum.startsWith("Adult") ) {
				fields = spectrum.split("\\.");
				file = fields[0];
				scan = fields[1];
			} else {
				fields = spectrum.split("@");
				scan = fields[0];
				file = fields[1].split("/")[3].split("\\.")[0];
			}*/			
			String scan = csv.getField(1);
			if( Integer.parseInt(scan) > 1000 )
				continue;
			Psm psm = new Psm();
			//psm.setScan(String.format("%s.%s", file, scan));
			psm.setScan(scan);
			psm.setEngine(engine);
			psm.setPeptide(csv.getField(6));
			psm.setDecoy(decoy);
			psm.setCharge(csv.getIntField(4));
			psm.setMass(Numbers.parseDouble(csv.getField(3)));			
			psm.setRank(csv.getIntField(2));
			psm.setScore(Numbers.parseDouble(csv.getField(7)));
			list.add(psm);
		}
		csv.close();
		return list;
	}

	private static final Logger LOGGER = Logger.getLogger(SearchComparator.class.getName());
	private static final String DATA = "/media/gorka/EhuBio/Lego/Adult_Heart/ScanTest";
	private static final String[] ENGINES = {"XTandem", "Comet"};
}
