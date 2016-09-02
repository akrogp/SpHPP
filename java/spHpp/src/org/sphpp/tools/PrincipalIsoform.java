package org.sphpp.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.io.CsvReader;

public class PrincipalIsoform {
	public static void main(String[] args) throws Exception {
		Set<String> principal = new HashSet<>();
		CsvReader appris = new CsvReader("\t", false, false);
		appris.open(APPRIS_PATH);
		while( appris.readLine()  != null) {
			if( appris.getField(4).equals("PRINCIPAL:1") )
				principal.add(appris.getField(2));
		}
		appris.close();
		
		List<Fasta> input = Fasta.readEntries(INPUT_FASTA, SequenceType.PROTEIN);
		List<Fasta> output = new ArrayList<>();
		for( Fasta fasta : input )
			if( principal.contains(fasta.getHeader().split("\\|")[1].replaceAll("\\..*", "")) )
				output.add(fasta);
		Fasta.writeEntries(OUTPUT_FASTA, output);
		System.out.println(String.format("Saved %d principal isoforms from %d fasta entries", output.size(), input.size()));
	}
	
	private static final String APPRIS_PATH = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/appris_data.principal.Gencode24-Ensembl84.txt";
	private static final String INPUT_FASTA = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/gencode.v24.pc_translations.fasta";
	private static final String OUTPUT_FASTA = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/gencode24principal.fasta";
}
