package org.sphpp.workflow.module;

import java.util.List;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.file.RelationFile;

import es.ehubio.cli.Argument;

public class Uniquer extends WorkflowModule {
	
	public Uniquer() {
		super("Removes relations with shared items.");
		
		Argument arg = new Argument(OPT_INPUT, 'i', "input");
		arg.setParamName("Lower2Upper.tsv");
		arg.setDescription("Input TSV file with lower to upper relations (upper id in the first column).");
		addOption(arg);
		
		arg = new Argument(OPT_OUTPUT, 'o', "output");
		arg.setParamName("UniqueLower2Upper.tsv");
		arg.setDescription("Output TSV file with lower to upper relations (upper id in the first column).");
		addOption(arg);
		
		addOption(Arguments.getDiscard());
	}
	
	public static void main( String[] args ) {
		new Uniquer().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		RelationFile rel = RelationFile.load(getValue(OPT_INPUT), getValue(Arguments.OPT_DISCARD));
		rel.filterShared();
		rel.save(getValue(OPT_OUTPUT));
	}

	private static final int OPT_INPUT = 1;
	private static final int OPT_OUTPUT = 2;
}
