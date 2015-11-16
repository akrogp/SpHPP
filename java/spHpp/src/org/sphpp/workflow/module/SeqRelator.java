package org.sphpp.workflow.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.file.PepFile;
import org.sphpp.workflow.file.RelationFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Peptide;

public class SeqRelator extends WorkflowModule {
	public SeqRelator() {
		super("Creates pep2prot relation from seq2prot and a peptide list.");
		
		Argument arg = new Argument(OPT_SEQ2PROT, 'r', "inRelations");
		arg.setParamName("Seq2Prot.tsv");
		arg.setDescription("Peptide sequence to protein relation input file.");
		addOption(arg);
		
		arg = new Argument(OPT_PEP, 'p', "peptides");
		arg.setParamName("Peptide.tsv");
		arg.setDescription("Peptide input file with id and sequence information.");
		addOption(arg);
		
		arg = new Argument(OPT_PEP2PROT, 'o', "output");
		arg.setParamName("Pep2Prot.tsv");
		arg.setDescription("Peptide id to protein relation output file.");
		arg.setDefaultValue("Pep2Prot.tsv.gz");
		addOption(arg);
	}
	
	public static void main(String[] args) {
		new SeqRelator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		RelationFile seq2prot = RelationFile.load(getValue(OPT_SEQ2PROT));
		Set<Peptide> peptides = PepFile.load(getValue(OPT_PEP));
		RelationFile pep2prot = run(seq2prot, peptides);
		pep2prot.save(getValue(OPT_PEP2PROT));
	}

	public static RelationFile run(RelationFile seq2prot, Set<Peptide> peptides) {
		int count = 0;
		RelationFile pep2prot = new RelationFile(seq2prot.getUpperLabel(), "peptide");
		Map<String, Set<String>> map = new HashMap<>();
		for( Relation rel : seq2prot.getEntries() ) {
			Set<String> set = map.get(rel.getLowerId().toLowerCase());
			if( set == null ) {
				set = new HashSet<>();
				map.put(rel.getLowerId().toLowerCase(), set);
			}
			set.add(rel.getUpperId());
		}
		for( Peptide peptide : peptides ) {
			Set<String> proteins = map.get(peptide.getSequence().toLowerCase());
			if( proteins == null ) {
				logger.warning(String.format(
					"Ignored peptide with sequence '%s': not related to any protein. Wrong digestion parameters?",
					peptide.getSequence()));
				count++;
				continue;
			}
			for( String protein : proteins )
				pep2prot.addEntry(new Relation(protein, peptide.getUniqueString()));
		}
		if( count != 0 )
			logger.warning(String.format("Ignored %d (of %d) peptides. Check digestion parameters", count, peptides.size()));
		return pep2prot;
	}
	
	private static final int OPT_SEQ2PROT = 1;
	private static final int OPT_PEP = 2;
	private static final int OPT_PEP2PROT = 3;
	private static final Logger logger = Logger.getLogger(SeqRelator.class.getName());
}
