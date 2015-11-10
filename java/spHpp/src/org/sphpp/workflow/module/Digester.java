package org.sphpp.workflow.module;

import java.io.IOException;
import java.util.List;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.file.RelationFile;

import es.ehubio.cli.Argument;
import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.proteomics.Enzyme;

public class Digester extends WorkflowModule {
	private static final int OPT_FASTA = 1;
	private static final int OPT_REL = 2;
	
	public Digester() {
		super("Models peptide to protein relations from a FASTA file.");
		
		Argument arg = new Argument(OPT_FASTA, 'i', "fasta");
		arg.setParamName("input.fasta");
		arg.setDescription("Input fasta file path.");
		addOption(arg);
		
		arg = new Argument(OPT_REL, 'o', "output");
		arg.setParamName("Pep2Prot.tsv");
		arg.setDescription("Output TSV file with peptide to protein relations.");
		arg.setDefaultValue("Pep2Prot.tsv.gz");
		addOption(arg);
		
		addOption(Arguments.getEnzyme());
		addOption(Arguments.getMissedCleavages());
		addOption(Arguments.getAspPro());
		addOption(Arguments.getCutNterm());
		addOption(Arguments.getMinPepLen());
		addOption(Arguments.getMaxPepLen());
	}
	
	public static void main( String[] args ) throws Exception {
		new Digester().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		Enzyme enzyme = Enzyme.valueOf(getValue(Arguments.OPT_ENZYME));
		int missedCleavages = getIntValue(Arguments.OPT_CLEAVAGES);
		boolean usingDP = hasArgument(Arguments.OPT_ASP_PRO);
		int cutNterm = getIntValue(Arguments.OPT_CUT_NTERM);
		es.ehubio.proteomics.pipeline.Digester.Config digestion =
			new es.ehubio.proteomics.pipeline.Digester.Config(enzyme, missedCleavages, usingDP, cutNterm);
		RelationFile relations = run(getValue(OPT_FASTA), digestion, getIntValue(Arguments.OPT_MIN_PEP_LEN), getIntValue(Arguments.OPT_MAX_PEP_LEN));
		relations.save(getValue(OPT_REL));
	}

	public static RelationFile run(String fasta, es.ehubio.proteomics.pipeline.Digester.Config digestion, int minPep, int maxPep) throws IOException, InvalidSequenceException {
		RelationFile relations = new RelationFile("protein", "peptide sequence");
		for( Fasta protein : Fasta.readEntries(fasta, SequenceType.PROTEIN) ) {			
			for( String pepSeq : es.ehubio.proteomics.pipeline.Digester.digestSequence(protein.getSequence(), digestion) ) {
				if( pepSeq.length() < minPep || pepSeq.length() > maxPep )
					continue;
				relations.addEntry(new Relation(protein.getAccession(), pepSeq));
			}
		}
		return relations;
	}	
}
