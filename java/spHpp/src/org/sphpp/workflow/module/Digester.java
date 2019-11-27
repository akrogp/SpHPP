package org.sphpp.workflow.module;

import java.io.IOException;
import java.util.List;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.data.Relations;
import org.sphpp.workflow.file.RelationFile;

import es.ehubio.cli.Argument;
import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.proteomics.Enzyme;

public class Digester extends WorkflowModule {
	private static final int OPT_FASTA = 1;
	private static final int OPT_REL = 2;
	private static final int OPT_PREFIX = 3;
	
	public Digester() {
		super("Models peptide to protein relations from a FASTA file.");
		
		Argument arg = new Argument(OPT_FASTA, 'i', "fasta");
		arg.setParamName("input.fasta");
		arg.setDescription("Input fasta file path.");
		addOption(arg);
		
		arg = new Argument(OPT_REL, 'o', "output");
		arg.setParamName("Seq2Prot.tsv");
		arg.setDescription("Output TSV file with peptide to protein relations.");
		arg.setDefaultValue("Seq2Prot.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_PREFIX, 'p', "decoyPrefix");
		arg.setParamName("decoyPrefix");
		arg.setDescription("If a decoy prefix is provided, separated target and decoy files will we generated.");
		arg.setOptional();
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
		boolean usingDP = getBooleanValue(Arguments.OPT_ASP_PRO);
		int cutNterm = getIntValue(Arguments.OPT_CUT_NTERM);
		es.ehubio.proteomics.pipeline.Digester.Config digestion =
			new es.ehubio.proteomics.pipeline.Digester.Config(enzyme, missedCleavages, usingDP, cutNterm);
		Relations relations = run(getValue(OPT_FASTA), digestion, getIntValue(Arguments.OPT_MIN_PEP_LEN), getIntValue(Arguments.OPT_MAX_PEP_LEN));
		String prefix = getValue(OPT_PREFIX);
		if( prefix == null ) {
			RelationFile file = new RelationFile("protein", "peptideSequence");
			file.save(relations, getValue(OPT_REL));
		} else {
			RelationFile target = new RelationFile("protein", "peptideSequence");
			RelationFile decoy = new RelationFile("protein", "peptideSequence");
			for( Relation rel : relations.getEntries() )
				if( rel.getUpperId().startsWith(prefix) )
					decoy.addEntry(rel);
				else
					target.addEntry(rel);
			String fname = getValue(OPT_REL);
			int i = fname.indexOf(".tsv");
			target.save(fname.substring(0, i)+"Target"+fname.substring(i));
			decoy.save(fname.substring(0, i)+"Decoy"+fname.substring(i));
		}
	}

	public static Relations run(String fasta, es.ehubio.proteomics.pipeline.Digester.Config digestion, int minPep, int maxPep) throws IOException, InvalidSequenceException {
		Relations relations = new Relations();
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
