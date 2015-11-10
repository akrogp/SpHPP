package org.sphpp.workflow.module;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sphpp.workflow.data.PsmFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Psm;

public class Filter extends WorkflowModule {
	public Filter() {
		super("Filters entries from PSMS SpHPP TSV file.");
		
		Argument arg = new Argument(OPT_INPUT, 'i', "input");
		arg.setParamName("Psms.tsv");
		arg.setDescription("Input TSV file with PSM entries.");
		addOption(arg);
		
		arg = new Argument(OPT_OUTPUT, 'o', "output");
		arg.setParamName("Filter.tsv");
		arg.setDescription("Output TSV file.");
		arg.setDefaultValue("Filter.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_RANK, 'r', "rank");
		arg.setParamName("rank");
		arg.setDescription("PSM rank threshold.");
		arg.setDefaultValue(1);
		addOption(arg);
	}
	
	public static void main( String[] args ) {
		new Filter().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		MsMsData data = PsmFile.load(getValue(OPT_INPUT));
		Set<Psm> psms = data.getPsms();
		psms = run(psms, getIntValue(OPT_RANK));
		PsmFile.save(psms, getValue(OPT_OUTPUT));
	}
	
	public Set<Psm> run( Set<Psm> inputPsms, int rank ) {
		if( rank == 0 )
			return inputPsms;
		Set<Psm> outputPsms = new HashSet<>();
		for( Psm psm : inputPsms )
			if( psm.getRank() <= rank )
				outputPsms.add(psm);
		return outputPsms;
	}

	private static final int OPT_INPUT = 1;
	private static final int OPT_OUTPUT = 2;
	private static final int OPT_RANK = 3;
}
