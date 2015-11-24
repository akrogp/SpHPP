package org.sphpp.workflow.module;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.Degeneracy;
import org.sphpp.workflow.data.Evidence;
import org.sphpp.workflow.data.Linkable;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.AmbiguityGroup;
import es.ehubio.proteomics.AmbiguityItem;
import es.ehubio.proteomics.AmbiguityPart;
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
	}
	
	public static <U extends Linkable<U,L> & Decoyable, L extends Linkable<L,U> & Decoyable>
	void run(Collection<U> upperItems, Collection<L> lowerItems ) {
		resetCategories(upperItems, lowerItems);
		classifyLower(upperItems, lowerItems);
		classifyUpper(upperItems);
	}
	
	private static <U extends Linkable<U,L> & Decoyable, L extends Linkable<L,U> & Decoyable>
	void resetCategories( Collection<U> upperItems, Collection<L> lowerItems ) {
		for( U item : upperItems )
			item.putScore(new Score(ScoreType.EVIDENCE, EMPTY));
		for( L item : lowerItems )
			item.putScore(new Score(ScoreType.DEGENERAY, EMPTY));
	}
	
	private static <U extends Linkable<U,L> & Decoyable, L extends Linkable<L,U> & Decoyable>
	void classifyLower( Collection<U> upperItems, Collection<L> lowerItems ) {
		// 1. Locate unique parts
		for( L lower : lowerItems ) {
			if( lower.getLinks().isEmpty() )
				setDegeneracy(lower, null);
			else if( lower.getLinks().size() == 1 ) {
				lower.getScoreByType(ScoreType.DEGENERAY).setValue(Degeneracy.UNIQUE.value());
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
	void classifyUpper( Collection<U> upperItems ) {
		// 1. Locate non-conclusive items
		for( U upper : upperItems ) {
			item.linkGroup(null);
			if( item.getConfidence() == AmbiguityItem.Confidence.CONCLUSIVE )
				continue;
			if( item.getAmbiguityParts().isEmpty() ) {
				item.setConfidence(null);
				continue;
			}
			item.setConfidence(AmbiguityItem.Confidence.NON_CONCLUSIVE);
			for( AmbiguityPart part : item.getAmbiguityParts() )
				if( part.getConfidence() == AmbiguityPart.Confidence.DISCRIMINATING ) {
					item.setConfidence(AmbiguityItem.Confidence.AMBIGUOUS_GROUP);
					break;
				}			
		}
		
		// 2. Group items
		data.getGroups().clear();
		for( AmbiguityItem item : data.getAmbiguityItems() ) {
			if( item.getGroup() != null )
				continue;
			AmbiguityGroup group = new AmbiguityGroup();
			data.getGroups().add(group);
			buildGroup(group, item);
		}
		
		// 3. Indistinguishable
		for( AmbiguityGroup group : data.getGroups() )
			if( group.size() >= 2 )
				if( isIndistinguishable(group) )
					for( AmbiguityItem item : group.getItems() )
						item.setConfidence(AmbiguityItem.Confidence.INDISTINGUISABLE_GROUP);
	}
	
	private static void setDegeneracy( Decoyable item, Degeneracy degeneracy ) {
		item.getScoreByType(ScoreType.DEGENERAY).setValue(degeneracy==null?EMPTY:degeneracy.value());
	}
	
	private static Degeneracy getDegeneracy( Decoyable item ) {
		return Degeneracy.parse(item.getScoreByType(ScoreType.DEGENERAY).getValue());
	}
	
	private static void setEvidence( Decoyable item, Evidence evidence ) {
		item.getScoreByType(ScoreType.EVIDENCE).setValue(evidence==null?EMPTY:evidence.value());
	}
	
	private static Evidence getEvidence( Decoyable item ) {
		return Evidence.parse(item.getScoreByType(ScoreType.EVIDENCE).getValue());
	}

	private static final Logger logger = Logger.getLogger(Grouper.class.getName());
	private static final int OPT_IN = 1;
	private static final int OPT_OUT = 2;
	private static final int EMPTY = -1;
}
