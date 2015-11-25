package org.sphpp.workflow.module;

import java.util.Collection;
import java.util.List;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.InterMapeable;
import org.sphpp.workflow.data.LinkMap;
import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.data.Relations;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.data.ScoreLink;
import org.sphpp.workflow.file.RelationFile;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.ScoreType;

public class Occam extends WorkflowModule {

	public Occam() {
		super("Updates relation coeficients according to upper-level scores.");
		
		Argument arg = new Argument(OPT_SCORES, 's', "scores");
		arg.setParamName("Scores.tsv");
		arg.setDescription("Input TSV file with upper level scores.");
		addOption(arg);
		
		arg = new Argument(OPT_INPUT, 'i', "input");
		arg.setParamName("Pep2Prot.tsv");
		arg.setDescription("Input TSV file with relations.");
		addOption(arg);
		
		arg = new Argument(OPT_OUTPUT, 'o', "output");
		arg.setParamName("Occam.tsv");
		arg.setDescription("Output TSV file with relations.");
		addOption(arg);
		
		addOption(Arguments.getDiscard());
	}
	
	public static void main(String[] args) {
		new Occam().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		RelationFile rels = RelationFile.load(getValue(OPT_INPUT), getValue(Arguments.OPT_DISCARD));		
		ScoreFile<ScoreItem> scores = ScoreFile.load(getValue(OPT_SCORES));
		LinkMap<ScoreLink,ScoreLink> map = Relations.getScoreLinkMap(scores.getItems(), rels);
		rels.save(next(map.getUpperList(), scores.selectScore()),getValue(OPT_OUTPUT));
	}
	
	public static <U extends InterMapeable<U,L> & Decoyable, L extends InterMapeable<L,U>> 
	Relations next(Collection<U> items, ScoreType type) {
		Relations rels = new Relations();
		for( U item : items ) {
			double num = item.getScoreByType(type).getValue();
			for( L lower : item.getLinks() ) {
				double den = 0.0;
				for( U upper : lower.getLinks() )
					den += upper.getScoreByType(type).getValue();
				double factor = den < 1e-10 ? 1.0/lower.getLinks().size() : num/den;
				rels.addEntry(new Relation(item.getId(), lower.getId(), factor));
			}
		}
		return rels;
	}

	private static final int OPT_SCORES = 1;
	private static final int OPT_INPUT = 2;
	private static final int OPT_OUTPUT = 3;
}
