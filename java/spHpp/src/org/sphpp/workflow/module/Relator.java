package org.sphpp.workflow.module;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.Link;
import org.sphpp.workflow.data.LinkMap;
import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.data.Relations;
import org.sphpp.workflow.file.RelationFile;

import es.ehubio.cli.Argument;

public class Relator extends WorkflowModule {
	
	public Relator() {
		super("Combines two relations files removing the intermediate step.");
		
		Argument arg = new Argument(OPT_REL_UP, 'u', "upper");
		arg.setParamName("Middle2Upper.tsv");
		arg.setDescription("Input TSV file with intermediate to upper relations.");
		addOption(arg);
		
		arg = new Argument(OPT_REL_LO, 'l', "lower");
		arg.setParamName("Lower2Middle.tsv");
		arg.setDescription("Input TSV file with lower to intermediate relations.");
		addOption(arg);
		
		arg = new Argument(OPT_REL_OUT, 'o', "output");
		arg.setParamName("Lower2Upper.tsv");
		arg.setDescription("Output TSV file with lower to upper relations.");
		addOption(arg);
		
		addOption(Arguments.getDiscard());
		
		arg = new Argument(OPT_UP_PREFIX, null, "upperPrefix");
		arg.setParamName("prefix");
		arg.setDescription("Optional prefix to be added to upper relations ids.");
		arg.setOptional();
		addOption(arg);
		
		arg = new Argument(OPT_LO_PREFIX, null, "lowerPrefix");
		arg.setParamName("prefix");
		arg.setDescription("Optional prefix to be added to lower relations ids.");
		arg.setOptional();
		addOption(arg);
		
		arg = new Argument(OPT_UP_INDEX, null, "upperIndex");
		arg.setParamName("index");
		arg.setDescription("Index of TSV field with upper-level ids.");
		arg.setDefaultValue(0);
		addOption(arg);
		
		arg = new Argument(OPT_MI_INDEX1, null, "middleIndex1");
		arg.setParamName("index");
		arg.setDescription("Index of TSV field with middle-level ids in upper relations file.");
		arg.setDefaultValue(1);
		addOption(arg);
		
		arg = new Argument(OPT_MI_INDEX2, null, "middleIndex2");
		arg.setParamName("index");
		arg.setDescription("Index of TSV field with middle-level ids in lower relations file.");
		arg.setDefaultValue(0);
		addOption(arg);
		
		arg = new Argument(OPT_LO_INDEX, null, "lowerIndex");
		arg.setParamName("index");
		arg.setDescription("Index of TSV field with lower-level ids.");
		arg.setDefaultValue(1);
		addOption(arg);
	}
	
	public static void main(String[] args) {
		new Relator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		RelationFile relUp = RelationFile.load(
			getValue(OPT_REL_UP), getValue(Arguments.OPT_DISCARD), getValue(OPT_UP_PREFIX),
			getIntValue(OPT_UP_INDEX), getIntValue(OPT_MI_INDEX1), false); 
		RelationFile relLo = RelationFile.load(
			getValue(OPT_REL_LO), getValue(Arguments.OPT_DISCARD), getValue(OPT_LO_PREFIX),
			getIntValue(OPT_MI_INDEX2), getIntValue(OPT_LO_INDEX), false);
		boolean uselower = relUp.getLowerLabel().equals(relLo.getLowerLabel());
		RelationFile output = new RelationFile(relUp.getUpperLabel(), uselower?relLo.getUpperLabel():relLo.getLowerLabel());
		output.save(run(relUp, relLo, uselower), getValue(OPT_REL_OUT));
	}

	public Relations run(Relations relUp, Relations relLo, boolean uselower) {
		logger.info("Mapping relations ...");
		LinkMap<Link<Void,Void>,Link<Void,Void>> mapUp = relUp.getLinkMap(true);
		LinkMap<Link<Void,Void>,Link<Void,Void>> mapLo = relLo.getLinkMap(true);
		Relations rels = new Relations();
		Set<String> usedUpper = new HashSet<>();
		Set<String> usedMiddle = new HashSet<>();
		Set<String> usedLower = new HashSet<>();
		boolean upperMapped;
		for(Link<Void,Void> upper : mapUp.getUpperList()) {
			upperMapped = false;
			for(Link<Void,Void> common : upper.getLinks() ) {
				Link<Void,Void> dual;
				if( uselower )
					dual = mapLo.getLower(common.getId().toLowerCase());
				else
					dual = mapLo.getUpper(common.getId().toLowerCase());
				if( dual == null )
					continue;
				usedMiddle.add(dual.getId());
				upperMapped = true;
				for(Link<Void,Void> lower : dual.getLinks() ) {
					rels.addEntry(new Relation(upper.getId(), lower.getId()));
					usedLower.add(lower.getId());
				}
			}
			if( upperMapped )
				usedUpper.add(upper.getId());
		}
		int middleCount = Math.min(mapUp.getLowerList().size(), uselower ? mapLo.getLowerList().size() : mapLo.getUpperList().size());
		logger.info(String.format(
			"Mapped %d (of %d) lower items to %d (of %d) upper items using %d (of %d) intermediate items",
			usedLower.size(), uselower ? mapLo.getUpperList().size() : mapLo.getLowerList().size(),
			usedUpper.size(), mapUp.getUpperList().size(),
			usedMiddle.size(), middleCount
		));
		if( usedMiddle.size() != middleCount )
			logger.warning(String.format("%d of %d intermediate items not used!", middleCount-usedMiddle.size(), middleCount));
		return rels;
	}

	private static final Logger logger = Logger.getLogger(Relator.class.getName()); 
	private static final int OPT_REL_UP = 1;
	private static final int OPT_REL_LO = 2;
	private static final int OPT_REL_OUT = 3;
	private static final int OPT_UP_INDEX = 4;
	private static final int OPT_MI_INDEX1 = 5;
	private static final int OPT_MI_INDEX2 = 6;
	private static final int OPT_LO_INDEX = 7;
	private static final int OPT_LO_PREFIX = 8;
	private static final int OPT_UP_PREFIX = 9;
}
