package org.sphpp.workflow.module;

import java.util.List;

import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.data.Relations;
import org.sphpp.workflow.file.RelationFile;

import es.ehubio.cli.Argument;
import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;

public class FastaRelator extends WorkflowModule {
	public FastaRelator() {
		super("Generates a protein to gene relations file from a (target) fasta file");
		
		Argument arg = new Argument(OPT_INPUT, 'i', "input");
		arg.setParamName("target.fasta");
		arg.setDescription("Input fasta file using UniProt headers.");
		addOption(arg);
		
		arg = new Argument(OPT_OUTPUT, 'o', "output");
		arg.setParamName("Prot2Gen.tsv");
		arg.setDescription("Output TSV file with protein to gene relations.");
		addOption(arg);
		
		arg = new Argument(OPT_PREFIX, 'p', "decoyPrefix");
		arg.setParamName("decoyPrefix");
		arg.setDescription("Used for skipping decoy entries.");
		arg.setOptional();
		addOption(arg);
	}
	
	public static void main( String[] args ) {
		new FastaRelator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		List<Fasta> fastas = Fasta.readEntries(getValue(OPT_INPUT), SequenceType.PROTEIN);
		String prefix = getValue(OPT_PREFIX);
		Relations rels = new Relations();
		for( Fasta fasta : fastas ) {
			if( prefix != null && fasta.getAccession().startsWith(prefix) )
				continue;
			String gene = fasta.getGeneName() == null ? fasta.getAccession() : fasta.getGeneName();
			Relation rel = new Relation(gene, fasta.getAccession());
			rels.addEntry(rel);
		}
		RelationFile.save("Gene", "Protein", rels, getValue(OPT_OUTPUT));
	}

	private static final int OPT_INPUT = 1;
	private static final int OPT_OUTPUT = 2;
	private static final int OPT_PREFIX = 3;
}
