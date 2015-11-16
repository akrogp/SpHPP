package org.sphpp.workflow.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.Constants;

import es.ehubio.io.CsvReader;
import es.ehubio.io.Streams;
import es.ehubio.proteomics.Peptide;

public class PepFile {
	public static void save( Set<Peptide> peptides, String path ) throws FileNotFoundException, IOException {
		//logger.info(String.format("Saving '%d' peptides ...", peptides.size()));
		try( PrintWriter pw = new PrintWriter(Streams.getTextWriter(path))) {
			pw.print("peptide");
			pw.print(Constants.SEP);
			pw.println("sequence");
			for( Peptide peptide : peptides ) {
				pw.print(peptide.getMassSequence());
				pw.print(Constants.SEP);
				pw.println(peptide.getSequence());
			}
		}
	}
	
	public static Set<Peptide> load( String path ) throws IOException {
		try( CsvReader rd = new CsvReader(Constants.SEP, true, true)) {
			rd.open(path);
			Set<Peptide> peptides = new HashSet<>();
			while( rd.readLine() != null ) {
				Peptide peptide = new Peptide();
				peptide.setUniqueString(rd.getField(0));
				peptide.setSequence(rd.getField(1));
				peptides.add(peptide);
			}
			logger.info(String.format("Loaded %d peptides", peptides.size()));
			return peptides;
		}
	}
	
	private final static Logger logger = Logger.getLogger(PepFile.class.getName());
}