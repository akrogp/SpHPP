package org.sphpp.workflow.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.data.Relations;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.RelationFile;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;

public class PepRelator extends WorkflowModule {
	public PepRelator() {
		super("Builds a relation file deducing peptide sequence from peptide id.");
		
		Argument arg = new Argument(OPT_REL_IN, 'r', "relations");
		arg.setParamName("Seq2Upper.tsv");
		arg.setDescription("Input TSV file with sequence to upper relations.");
		addOption(arg);
		
		arg = new Argument(OPT_PEP_SCORE, 'p', "peptides");
		arg.setParamName("PepScores.tsv");
		arg.setDescription("Input TSV file with peptide scores (omitted).");
		addOption(arg);
		
		arg = new Argument(OPT_REL_OUT, 'o', "output");
		arg.setParamName("Pep2Upper.tsv");
		arg.setDescription("Output TSV file with peptide to upper relations.");
		addOption(arg);
		
		addOption(Arguments.getDiscard());		
	}
	
	public static void main(String[] args) {
		new PepRelator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		RelationFile inRel = RelationFile.load(getValue(OPT_REL_IN), getValue(Arguments.OPT_DISCARD)); 
		ScoreFile<ScoreItem> pep = ScoreFile.load(getValue(OPT_PEP_SCORE));
		RelationFile out = new RelationFile(inRel.getUpperLabel(), pep.getId());
		Set<String> peps = new HashSet<>();
		for( ScoreItem item : pep.getItems() )
			peps.add(item.getId());
		out.save(run(inRel, peps), getValue(OPT_REL_OUT));
	}
	
	public Relations run(Relations inRel, Set<String> peps) {
		logger.info("Mapping relations ...");
		Map<String,List<String>> mapSeq = new HashMap<>();
		for( String pep : peps ) {
			Matcher match = PATTERN.matcher(pep);
			String seq = match.replaceAll("").toLowerCase();
			List<String> list = mapSeq.get(seq);
			if( list == null ) {
				list = new ArrayList<>();
				mapSeq.put(seq, list);
			}
			list.add(pep);
		}
		Relations out = new Relations();
		for( Relation rel : inRel.getEntries() ) {
			List<String> list = mapSeq.get(rel.getLowerId().toLowerCase());
			if( list != null ) 
				for( String pep : list )
					out.addEntry(new Relation(rel.getUpperId(), pep));
		}
		return out;
	}

	private static final Logger logger = Logger.getLogger(PepRelator.class.getName()); 
	private static final int OPT_REL_IN = 1;
	private static final int OPT_PEP_SCORE = 2;
	private static final int OPT_REL_OUT = 3;
	private static final Pattern PATTERN = Pattern.compile("[^a-zA-Z]"); 
}
