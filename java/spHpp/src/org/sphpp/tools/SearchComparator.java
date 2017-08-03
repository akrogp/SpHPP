package org.sphpp.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

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
		
		//saveData(list, String.format("%s/SearchComp.tsv", DATA));
		plotData(list, String.format("%s/SearchComp.png", DATA));
		
		LOGGER.info("finished!!");
	}
	
	private static void plotData(List<Psm> list, String path) throws IOException {		
		BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setPaint(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		Font font = new Font("TimesRoman", Font.BOLD, 20);
	    g.setFont(font);
	    
	    double maxX = getMax(list, ENGINES[0]);
	    double maxY = getMax(list, ENGINES[1]);
	    
	    double scaleX = 1.0/maxX*(WIDTH-X_OFFSET);
	    double scaleY = 1.0/maxY*(HEIGHT-Y_OFFSET);
	    
	    plotSingle(g, scaleX, scaleY, list, false);
	    plotSingle(g, scaleX, scaleY, list, true);
	    drawFrame(g, maxX, maxY, scaleX, scaleY);
	    
	    ImageIO.write(bi, "PNG", new File(path));
	}

	private static void drawFrame(Graphics2D g, double maxX, double maxY, double scaleX, double scaleY) {
		//FontMetrics fm = g.getFontMetrics();
		g.setPaint(Color.BLACK);
		int x1, x2, y1, y2;
		
		y1 = 0;
		y2 = HEIGHT;
		for( double x = -5; x <= 20; x += 1 ) {
			x1 = getX(x, scaleX);
			g.drawLine(x1, y1, x1, y2);
		}
		g.fillRect(0, HEIGHT-Y_OFFSET-1, WIDTH, 3);
		
		x1 = 0;
		x2 = WIDTH;
		for( double y = -5; y <= 20; y += 1 ) {
			y1 = getY(y, scaleY);
			g.drawLine(x1, y1, x2, y1);
		}
		g.fillRect(X_OFFSET-1, 0, 3, HEIGHT);		
	}

	private static void plotSingle(Graphics2D g, double scaleX, double scaleY, List<Psm> list, boolean decoy) {
		g.setPaint(decoy?Color.RED:Color.BLUE);
	    Map<String, Double> map = new HashMap<>();
	    for( Psm psm : list ) {
	    	if( psm.isDecoy() != decoy )
	    		continue;
	    	String key = psm.getScan();
	    	Double otherScore = map.get(key);
	    	if( otherScore == null )
	    		map.put(key, psm.getScore());
	    	else {
	    		if( otherScore == psm.getScore() )	// Psms with same rank
	    			continue;
	    		map.remove(key);
	    		double x, y;
	    		if( psm.getEngine().equals(ENGINES[0]) ) {
	    			x = psm.getScore(); y = otherScore;
	    		} else {
	    			x = otherScore; y = psm.getScore();
	    		}
	    		int xi = getX(x, scaleX);
	    		int yi = getY(y, scaleY);	    		
	    		g.fillRect(xi-1, yi-1, 2, 2);
	    	}
	    }
	}
	
	private static int getX(double x, double scaleX) {
		return X_OFFSET+(int)Math.round(x*scaleX);
	}
	
	private static int getY(double y, double scaleY) {
		return HEIGHT-(int)Math.round(y*scaleY)-Y_OFFSET;
	}

	private static double getMax(List<Psm> list, String engine) {
		double max = -30.0;
		for( Psm psm : list )
			if( psm.getEngine().equals(engine) )
				if( psm.getScore() > max )
					max = psm.getScore();
		return max;
	}

	@SuppressWarnings("unused")
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
			int rank = csv.getIntField(2);
			if( rank > 1 )
				continue;
			
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
			scan = scan.replaceAll(".*\\/","");
			scan = scan.replaceAll("\\.mgf","");
			
			Psm psm = new Psm();
			psm.setScan(scan);
			psm.setEngine(engine);
			psm.setPeptide(csv.getField(6));
			psm.setDecoy(decoy);
			psm.setCharge(csv.getIntField(4));
			psm.setMass(Numbers.parseDouble(csv.getField(3)));			
			psm.setRank(rank);
			double score = Numbers.parseDouble(csv.getField(7));
			if( engine.equalsIgnoreCase("XTandem") )
				score = -Math.log10(score);
			psm.setScore(score);
			list.add(psm);
		}
		csv.close();
		return list;
	}

	private static final Logger LOGGER = Logger.getLogger(SearchComparator.class.getName());
	private static final String DATA = "/media/gorka/EhuBio/Lego/Adult_Heart/ScanTest";
	//private static final String[] ENGINES = {"XTandem", "Comet"};
	private static final String[] ENGINES = {"Comet", "XTandem"};
	private static final int WIDTH = 1024, HEIGHT = 1024, X_OFFSET = 150, Y_OFFSET = 150;
}
