package org.sphpp.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;

public class ProteinLength {
	
	public static void main(String[] args) throws Exception {
		List<Fasta> list = Fasta.readEntries("/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/cima/HPP2014/ensemblCrap.fasta", SequenceType.PROTEIN);
		Map<String, Fasta> map = new HashMap<>();
		for( Fasta fasta : list )
			map.put(fasta.getAccession(), fasta);
		//List<String> accs = Streams.readLines("/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/CNB_CCD_NUC_R1/kk.txt");
		//PrintWriter pw = new PrintWriter("/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/CNB_CCD_NUC_R1/kk2.txt");
		String[] accs = {"ENSP00000464265","ENSP00000383851","ENSP00000225964","ENSP00000259569","ENSP00000328062","ENSP00000261708"};
		for( String acc : accs )
			System.out.println(map.get(acc).getSequence().length());
		//pw.close();
	}
}
