package org.sphpp.workflow.module;

import java.util.List;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.file.PsmFile;
import org.sphpp.workflow.file.ScoreFile;

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
		ScoreType type = ScoreFile.selectScore(data.getPsms());
		logger.info(String.format("Using '%s' to calculate ranks ...", type.getName()));
		data.updateRanks(type);
		if( decoyPrefix == null ) {
			logger.info(String.format("Saving %s PSMs ...", data.getPsmCount()));
			PsmFile.save(data.getPsms(), getValue(OPT_TARGET), type);
		} else {
			logger.info(String.format("Saving %s target PSMs ...", data.getTargetPsmCount()));
			PsmFile.save(data.getTargetPsms(), getValue(OPT_TARGET), type);
			logger.info(String.format("Saving %s decoy PSMs ...", data.getDecoyPsmCount()));
			PsmFile.save(data.getDecoyPsms(), getValue(OPT_DECOY), type);
		}
	}	
	
	public static MsMsData run( String path, String decoyPrefix ) throws Exception {
		MsMsData data = MsMsFile.autoLoad(path, false);
		data.markDecoys(decoyPrefix);
		return data;
	}

	private static final Logger logger = Logger.getLogger(Parser.class.getName());
	private static final int OPT_INPUT = 1;
	private static final int OPT_TARGET = 2;
	private static final int OPT_DECOY = 3;
}
