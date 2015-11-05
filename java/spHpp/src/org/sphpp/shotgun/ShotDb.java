package org.sphpp.shotgun;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

public class ShotDb {
	private static final String cellDelimiter = "&";
	private static final String listDelimiter = ";";
	private static final String notAvailable = "-";
	
	public static List<ShotProtein> readProteinList( String path ) throws FileNotFoundException, IOException {
		List<ShotProtein> list = new ArrayList<>();
		BufferedReader rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path))));
		String line;
		String[] fields;
		ShotProtein protein;
		rd.readLine();	// Skip header
		while( (line=rd.readLine()) != null ) {
			fields = line.split(cellDelimiter);
			if( fields.length < 20 ) {
				logger.warning("Invalid protein: " + line);
				continue;
			}
			protein = new ShotProtein();
			protein.setUniprotAccession(fields[0]);
			protein.setHpa(fields[7].equals("1"));
			protein.setProteomicsDb(fields[8].equals("1"));
			protein.setNappa(fields[9].equals("1"));
			protein.setJpr2Missing(fields[10].equals("1"));
			protein.setMrmValidated(fields[11].equals("1"));
			protein.addLineCount( "CCD18", Integer.parseInt(fields[12]), Integer.parseInt(fields[13]) );
			protein.addLineCount( "MCF7", Integer.parseInt(fields[14]), Integer.parseInt(fields[15]) );
			protein.addLineCount( "RAMOS", Integer.parseInt(fields[16]), Integer.parseInt(fields[17]));
			protein.addLineCount( "JURKAT", Integer.parseInt(fields[18]), Integer.parseInt(fields[19]));
			list.add(protein);
		}
		rd.close();
		return list;
	}
	
	public static List<ShotPeptide> readPeptideList( String path ) throws FileNotFoundException, IOException {
		List<ShotPeptide> list = new ArrayList<>();
		BufferedReader rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path))));
		String line;
		String[] fields;
		ShotPeptide peptide;
		List<Double> rts;
		Set<Integer> charges;		
		rd.readLine();	// Skip header
		while( (line=rd.readLine()) != null ) {
			fields = line.split(cellDelimiter);
			if( fields.length < 28 ) {
				logger.warning("Invalid peptide: " + line);
				continue;
			}
			peptide = new ShotPeptide();
			peptide.setCode(fields[1]);
			peptide.setExperiments(new HashSet<>(Arrays.asList(fields[3].split(listDelimiter))));
			peptide.setRep(Arrays.asList(fields[4].split(listDelimiter)));
			peptide.setResearcher(fields[6]);
			peptide.setAccessions(new HashSet<>(Arrays.asList(fields[11].split(listDelimiter))));
			peptide.setSequence(fields[13]);
			if( isValidCell(fields[15]) )
				peptide.setMascotScore(Double.parseDouble(fields[15]));
			if( isValidCell(fields[16]) )
				peptide.setMascotExpectation(Double.parseDouble(fields[16]));
			if( isValidCell(fields[18]) )
				peptide.setOcurrence(Integer.parseInt(fields[18]));
			if( isValidCell(fields[20]) )
				peptide.setMzExp(Double.parseDouble(fields[20]));
			if( isValidCell(fields[21]) )
				peptide.setMzCalc(Double.parseDouble(fields[21]));
			if( isValidCell(fields[23]) ) {
				rts = new ArrayList<>();
				for( String str : fields[23].split(listDelimiter) )
					rts.add(Double.parseDouble(str));
				peptide.setRt(rts);
			}
			if( isValidCell(fields[24]) ) {
				charges = new HashSet<>();
				for( String str : fields[24].split(listDelimiter) )
					charges.add(Integer.parseInt(str));
				peptide.setCharges(charges);
			}
			if( isValidCell(fields[26]) )
				peptide.setModifSeq(fields[26]);
			if( isValidCell(fields[27]) )
				peptide.setModifs(Arrays.asList(fields[27].split(listDelimiter)));
			list.add(peptide);
		}
		rd.close();
		return list;
	}
	
	private static boolean isValidCell( String cell ) {
		return !cell.isEmpty() && !cell.equals(notAvailable);
	}
	
	private final static Logger logger = Logger.getLogger(ShotDb.class.getName());
}