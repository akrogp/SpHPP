package org.sphpp.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.proteomics.Enzyme;
import es.ehubio.proteomics.pipeline.Digester;

public class UniqueFasta {
	public static void main(String[] args) throws Exception {
		List<Fasta> input = Fasta.readEntries(INPUT, SequenceType.PROTEIN);
		List<Fasta> output = new ArrayList<>();
		Map<String,Set<String>> map = new HashMap<>();
		for( Fasta fasta : input )
			for( String pep : Digester.digestSequence(fasta.getSequence(), ENZYME) ) {
				Set<String> set = map.get(pep);
				if( set == null ) {
					set = new HashSet<>();
					map.put(pep, set);
				}
				set.add(fasta.getEntry());
			}
		long uniqueCount = 0;
		for( Fasta fasta : input ) {
			StringBuilder unique = new StringBuilder();
			for( String pep : Digester.digestSequence(fasta.getSequence(), ENZYME) ) {
				Set<String> set = map.get(pep);
				if( set.size() == 1 ) {
					unique.append(pep);
					uniqueCount++;
				}
			}
			if( unique.length() > 0 )
				output.add(new Fasta(fasta.getHeader(), unique.toString(), SequenceType.PROTEIN));
		}
		Fasta.writeEntries(OUTPUT, output);
		System.out.println(String.format("Using %d unique peptides from %d tryptic peptides", uniqueCount, map.size()));
	}
	
	private static final Enzyme ENZYME = Enzyme.TRYPSIN;
	private static final String INPUT = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/gencode24principal.fasta";
	private static final String OUTPUT = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/kk-gencode24-principal-unique.fasta";
}
