package org.sphpp.workflow.module;

import java.util.List;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.PsmFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.io.MsMsFile;

public class Parser extends WorkflowModule {
	public Parser() {
		super("Parses output files of different search engines into a single SpHPP TSV format.");
		
		Argument arg = new Argument(OPT_INPUT, 'i', "input");
		arg.setParamName("/path/dir[/file]");
		arg.setDescription("Input path of search engine output file or directory with files.");
		addOption(arg);
		
		arg = new Argument(OPT_TARGET, 't', "target");
		arg.setParamName("Target.tsv");
		arg.setDescription("Output TSV file.");
		arg.setDefaultValue("Target.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_DECOY, 'd', "decoy");
		arg.setParamName("Decoy.tsv");
		arg.setDescription("Output TSV file for decoys.");
		arg.setDefaultValue("Decoy.tsv.gz");
		addOption(arg);
				
		arg = Arguments.getDecoyPrefix();
		arg.setDefaultValue(null);
		arg.setOptional();
		addOption(arg);
	}
	
	public static void main( String[] args ) {
		new Parser().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		String decoyPrefix = getValue(Arguments.OPT_PREFIX);
		MsMsData data = run(getValue(OPT_INPUT), decoyPrefix);
		ScoreType type = PsmFile.selectScore(data.getPsms());
		data.updateRanks(type);
		if( decoyPrefix == null )
			PsmFile.save(data.getPsms(), getValue(OPT_TARGET), type);
		else {
			PsmFile.save(data.getTargetPsms(), getValue(OPT_TARGET), type);
			PsmFile.save(data.getDecoyPsms(), getValue(OPT_DECOY), type);
		}
	}	
	
	public MsMsData run( String path, String decoyPrefix ) throws Exception {
		MsMsData data = MsMsFile.autoLoad(path, false);
		data.markDecoys(decoyPrefix);
		return data;
	}

	private static final int OPT_INPUT = 1;
	private static final int OPT_TARGET = 2;
	private static final int OPT_DECOY = 3;
}
