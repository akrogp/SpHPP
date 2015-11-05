package org.sphpp.workflow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import es.ehubio.proteomics.AmbiguityGroup;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Spectrum;
import es.ehubio.proteomics.pipeline.PAnalyzer;
import es.ehubio.tools.Command.Interface;

public class ProteinPeptideGrouper implements Interface {
	private static final Logger logger = Logger.getLogger(ProteinPeptideGrouper.class.getName());	

	@Override
	public String getUsage() {
		return "<input_relations.tsv> <output_relations.tsv>";
	}

	@Override
	public int getMinArgs() {
		return 2;
	}

	@Override
	public int getMaxArgs() {
		return 2;
	}

	@Override
	public void run(String[] args) throws Exception {
		String tsvInput = args[0];
		String tsvOutput = args[1];
		
		MsMsData data = loadFile(tsvInput);
		PAnalyzer pAnalyzer = new PAnalyzer(data);
		pAnalyzer.run();
		logger.info(String.format("PAnalyzer: %s", pAnalyzer.getCounts().toString()));
		saveFile(data, tsvOutput);
	}

	private void saveFile(MsMsData data, String tsv) throws IOException {
		PrintWriter wr = new PrintWriter(tsv);
		wr.println(String.format("%s\t%s\t%s\t%s", "protein group id", pepName, "peptide type", "protein group type"));
		for( AmbiguityGroup group : data.getGroups() )
			for( Peptide peptide : group.getPeptides() )
				wr.println(String.format("%s\t%s\t%s\t%s", group.getId(), peptide.getName(), peptide.getConfidence(), group.getConfidence()));
		wr.close();
		logger.info(String.format("Saved %s groups into %s", data.getGroups().size(), tsv));
	}

	private MsMsData loadFile(String tsv) throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader(tsv));
				
		Set<Spectrum> spectra = new HashSet<>();
		Map<String,Peptide> peptides = new HashMap<>();
		Map<String,Protein> proteins = new HashMap<>();
		
		String line;
		String[] fields;
		boolean header = true;
		while( (line=rd.readLine()) != null ) {			
			fields = line.split("\\t");
			if( header ) {
				pepName = fields[1];
				protName = fields[0];
				header = false;
				logger.info(String.format("Using '%s' as peptides, and '%s' as proteins ...",pepName,protName));
			} else {
				Peptide peptide = peptides.get(fields[1]);
				Protein protein = proteins.get(fields[0]);
				if( peptide == null ) {
					Spectrum spectrum = new Spectrum();
					spectra.add(spectrum);
					Psm psm = new Psm();
					peptide = new Peptide();
					peptide.setName(fields[1]);
					psm.linkPeptide(peptide);
					psm.linkSpectrum(spectrum);
					peptides.put(fields[1], peptide);
				}
				if( protein == null ) {
					protein = new Protein();
					protein.setName(fields[0]);
					proteins.put(fields[0], protein);
				}
				protein.linkAmbiguityPart(peptide);
			}
		}
		rd.close();
		logger.info(String.format("Loaded %s peptides and %s proteins from %s", peptides.size(), proteins.size(), tsv));
		
		MsMsData data = new MsMsData();
		data.loadFromSpectra(spectra);
		return data;
	}
	
	private String pepName;
	private String protName;
}
