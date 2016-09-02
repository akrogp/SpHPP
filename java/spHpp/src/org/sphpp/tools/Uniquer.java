package org.sphpp.tools;

import java.io.IOException;

import org.sphpp.workflow.data.Link;
import org.sphpp.workflow.data.LinkMap;
import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.file.RelationFile;

public class Uniquer {
	public static void main(String[] args) throws IOException {
		RelationFile input = RelationFile.load(INPUT);
		RelationFile output = new RelationFile(input.getUpperLabel(), input.getLowerLabel());
		LinkMap<Link<Void,Void>,Link<Void,Void>> map = input.getLinkMap();
		for( Link<Void,Void> link : map.getLowerList() )
			if( link.getLinks().size() == 1 )
				output.addEntry(new Relation(link.getLinks().iterator().next().getId(), link.getId()));
		output.save(OUTPUT);
	}
	
	//private static final String INPUT = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/CNB_CCD_NUC_R1/LegoEqui/Seq2ProtTarget.tsv.gz";
	//private static final String OUTPUT = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/CNB_CCD_NUC_R1/LegoEqui/UniqSeq2ProtTarget.tsv.gz";
	
	private static final String INPUT = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/CNB_CCD_NUC_R1/LegoEqui/Seq2ProtDecoy.tsv.gz";
	private static final String OUTPUT = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/CNB_CCD_NUC_R1/LegoEqui/UniqSeq2ProtDecoy.tsv.gz";
}
