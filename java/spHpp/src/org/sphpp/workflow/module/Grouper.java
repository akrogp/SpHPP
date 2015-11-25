package org.sphpp.workflow.module;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.Degeneracy;
import org.sphpp.workflow.data.Evidence;
import org.sphpp.workflow.data.Identifiable;
import org.sphpp.workflow.data.LinkMap;
import org.sphpp.workflow.data.Linkable;
import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.data.ScoreLink;
import org.sphpp.workflow.file.RelationFile;

import es.ehubio.Strings;
import es.ehubio.cli.Argument;
import es.ehubio.collections.Many2Many;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class Grouper extends WorkflowModule {	

	public Grouper() {
		super("Builds ambiguity groups according to shared peptides using PAnalyzer algorithm.");
		
		Argument arg = new Argument(OPT_IN, 'i', "input");
		arg.setParamName("Pep2Prot.tsv");
		arg.setDescription("Input TSV file with degenerated relations.");
		addOption(arg);
		
		arg = new Argument(OPT_OUT, 'o', "output");
		arg.setParamName("Prot2Grp.tsv");
		arg.setDescription("Output TSV file with group relations.");
		addOption(arg);
		
		addOption(Arguments.getDiscard());
	}
	
	public static void main(String[] args) {
		new Grouper().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		RelationFile input = RelationFile.load(getValue(OPT_IN), getValue(Arguments.OPT_DISCARD));
		LinkMap<ScoreLink,ScoreLink> map = input.getScoreLinkMap();
		Many2Many<ScoreItem,ScoreLink> groups = run(map.getUpperList(), map.getLowerList());
		RelationFile output = new RelationFile("group", input.getUpperLabel());
		for( ScoreItem group : groups.getLvalues() ) {
			String groupName = buildName(group, groups);
			for( ScoreLink item : groups.getRvalues(group) ) {
				Relation rel = new Relation(groupName, item.getId());
				rel.addLabel(getEvidence(item).name());
				output.addEntry(rel);
			}
		}
		output.save(getValue(OPT_OUT));
	}
	
	private String buildName( ScoreItem group, Many2Many<ScoreItem,? extends Identifiable> groups ) {
		Set<String> names = new HashSet<>();
		for( Identifiable item : groups.getRvalues(group) )
			names.add(item.getId());
		return Strings.merge(names);
	}
	
	public static <U extends Linkable<U,L> & Decoyable, L extends Linkable<L,U> & Decoyable>
	Many2Many<ScoreItem,U> run(Collection<U> upperItems, Collection<L> lowerItems ) {
		resetCategories(upperItems, lowerItems);
		classifyLower(upperItems, lowerItems);
		Many2Many<ScoreItem,U> groups = classifyUpper(upperItems);
		return groups;
	}
	
	private static <U extends Linkable<U,L> & Decoyable, L extends Linkable<L,U> & Decoyable>
	void resetCategories( Collection<U> upperItems, Collection<L> lowerItems ) {
		for( U item : upperItems )
			item.putScore(new Score(ScoreType.EVIDENCE, EMPTY));
		for( L item : lowerItems )
			item.putScore(new Score(ScoreType.DEGENERACY, EMPTY));
	}
	
	private static <U extends Linkable<U,L> & Decoyable, L extends Linkable<L,U> & Decoyable>
	void classifyLower( Collection<U> upperItems, Collection<L> lowerItems ) {
		// 1. Locate unique parts
		for( L lower : lowerItems ) {
			if( lower.getLinks().isEmpty() )
				setDegeneracy(lower, null);
			else if( lower.getLinks().size() == 1 ) {
				lower.getScoreByType(ScoreType.DEGENERACY).setValue(Degeneracy.UNIQUE.value());
				setDegeneracy(lower, Degeneracy.UNIQUE);
				setEvidence(lower.getLinks().iterator().next(), Evidence.CONCLUSIVE);
			} else
				setDegeneracy(lower, Degeneracy.DISCRIMINATING);
		}
		
		// 2. Locate non-discriminating parts (first round)
		for( U upper : upperItems )
			if( getEvidence(upper) == Evidence.CONCLUSIVE )
				for( L lower : upper.getLinks() )
					if( getDegeneracy(lower) != Degeneracy.UNIQUE )
						setDegeneracy(lower, Degeneracy.NON_DISCRIMINATING);
		
		// 3. Locate non-discriminating parts (second round)
		for( L lower : lowerItems ) {
			if( getDegeneracy(lower) != Degeneracy.DISCRIMINATING )
				continue;			
			if( lower.getLinks().isEmpty() )
				logger.warning(String.format("No relations for lower item %s", lower));
			for( L lower2 : lower.getLinks().iterator().next().getLinks() ) {
				if( getDegeneracy(lower2) != Degeneracy.DISCRIMINATING )
					continue;
				if( lower2.getLinks().size() <= lower.getLinks().size() )
					continue;
				boolean shared = true;
				for( U upper : lower.getLinks() )
					if( !upper.getLinks().contains(lower2) ) {
						shared = false;
						break;
					}
				if( shared )
					setDegeneracy(lower2, Degeneracy.NON_DISCRIMINATING);
			}
		}
	}
	
	private static <U extends Linkable<U,L> & Decoyable, L extends Linkable<L,U> & Decoyable>
	Many2Many<ScoreItem,U> classifyUpper( Collection<U> upperItems ) {
		Many2Many<ScoreItem,U> groups = new Many2Many<>();
				
		// 1. Locate non-conclusive items
		for( U upper : upperItems ) {
			if( getEvidence(upper) == Evidence.CONCLUSIVE )
				continue;
			if( upper.getLinks().isEmpty() ) {
				setEvidence(upper, null);
				continue;
			}
			setEvidence(upper, Evidence.NON_CONCLUSIVE);
			for( L lower : upper.getLinks() )
				if( getDegeneracy(lower) == Degeneracy.DISCRIMINATING ) {
					setEvidence(upper, Evidence.AMBIGUOUS_GROUP);
					break;
				}			
		}
		
		// 2. Group items
		int gid = 0;
		for( U upper : upperItems ) {
			if( groups.getLvalues(upper) != null )
				continue;
			ScoreItem group = new ScoreItem((++gid)+"");
			buildGroup(group, upper, groups);
		}
		
		// 3. Indistinguishable
		for( ScoreItem group : groups.getLvalues() )
			if( groups.getRvalues(group).size() >= 2 )
				if( isIndistinguishable(group, groups) )
					for( U upper : groups.getRvalues(group) )
						setEvidence(upper, Evidence.INDISTINGUISABLE_GROUP);
		
		// Mark groups
		for( ScoreItem group : groups.getLvalues() )
			setEvidence(group, getEvidence(groups.getRvalues(group).iterator().next()));
		
		return groups;
	}
	
	private static <U extends Linkable<U,L> & Decoyable, L extends Linkable<L,U> & Decoyable>
	void buildGroup(ScoreItem group, U upper, Many2Many<ScoreItem,U> groups) {
		if( groups.getRvalues(group) != null && groups.getRvalues(group).contains(upper) )
			return;
		groups.fwlink(group, upper);
		for( L lower : upper.getLinks() ) {
			if( getDegeneracy(lower) != Degeneracy.DISCRIMINATING )
				continue;
			for( U upper2 : lower.getLinks() )
				buildGroup(group, upper2, groups);
		}
	}
	
	private static <U extends Linkable<U,L> & Decoyable, L extends Linkable<L,U> & Decoyable>
	boolean isIndistinguishable(ScoreItem group, Many2Many<ScoreItem,U> groups) {
		boolean indistinguishable = true;
		Set<L> discrimitating = new HashSet<>();
		for( U upper : groups.getRvalues(group) )
			for( L lower : upper.getLinks() )
				if( getDegeneracy(lower) == Degeneracy.DISCRIMINATING )
					discrimitating.add(lower);			
		for( U upper : groups.getRvalues(group) )
			if( !upper.getLinks().containsAll(discrimitating) ) {
				indistinguishable = false;
				break;
			}
		discrimitating.clear();
		return indistinguishable;
	}
	
	private static void setDegeneracy( Decoyable item, Degeneracy degeneracy ) {
		Score score = item.getScoreByType(ScoreType.DEGENERACY);
		double value = degeneracy==null?EMPTY:degeneracy.value();
		if( score == null ) {
			score = new Score(ScoreType.DEGENERACY, value);
			item.putScore(score);
		} else
			score.setValue(value);
	}
	
	private static Degeneracy getDegeneracy( Decoyable item ) {
		return Degeneracy.parse(item.getScoreByType(ScoreType.DEGENERACY).getValue());
	}
	
	private static void setEvidence( Decoyable item, Evidence evidence ) {
		Score score = item.getScoreByType(ScoreType.EVIDENCE);
		double value = evidence==null?EMPTY:evidence.value();
		if( score == null ) {
			score = new Score(ScoreType.EVIDENCE, value);
			item.putScore(score);
		} else
			score.setValue(value);
	}
	
	private static Evidence getEvidence( Decoyable item ) {
		return Evidence.parse(item.getScoreByType(ScoreType.EVIDENCE).getValue());
	}

	private static final Logger logger = Logger.getLogger(Grouper.class.getName());
	private static final int OPT_IN = 1;
	private static final int OPT_OUT = 2;
	private static final int EMPTY = -1;
}
