package org.sphpp.workflow.module;

import java.util.List;

import org.sphpp.workflow.Constants;
import org.sphpp.workflow.data.Configuration;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.io.MsMsFile;
import es.ehubio.proteomics.pipeline.Digester;
import es.ehubio.proteomics.pipeline.Searcher;

public class ConfigDetector extends WorkflowModule {
	public ConfigDetector() {
		super("Guesses search and digestion paramteres from identified peptides.");
		
		Argument arg = new Argument(OPT_INPUT, 'i', "input");
		arg.setParamName("input.data");
		arg.setDescription("Input path of search engine output file or directory with files.");
		addOption(arg);
		
		arg = new Argument(OPT_FASTA, 'f', "fasta");
		arg.setParamName("input.fasta");
		arg.setDescription("Input fasta file used by the search engine.");
		addOption(arg);
		
		arg = new Argument(OPT_CFG, 'c', "config");
		arg.setParamName("config.ini");
		arg.setDescription("Output path for writing an ini file with the configuration detected.");
		arg.setDefaultValue();
		addOption(arg);		
		
		arg = new Argument(OPT_COUNT, null, "max");
		arg.setParamName("count");
		arg.setDescription("Number of proteins to be analyzed.");
		arg.setDefaultValue(Constants.DETECT_COUNT);
		addOption(arg);		
	}
	
	public static void main( String[] args ) {
		new ConfigDetector().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		Configuration config = run(getValue(OPT_INPUT), getValue(OPT_FASTA));		
		config.save("SpHPP detected configuration", getValue(OPT_CFG));
	}
	
	public Configuration run( String dataPath, String fastaPath ) throws Exception {
		MsMsData data = MsMsFile.autoLoad(dataPath, false);
		data.updateProteinInformation(fastaPath);
		es.ehubio.proteomics.pipeline.ConfigDetector detector = new es.ehubio.proteomics.pipeline.ConfigDetector(Constants.DETECT_COUNT);
		Digester.Config digestConfig = detector.getDigestion(data);
		Searcher.Config searchConfig = detector.getSearching(data);
		Configuration config = new Configuration(digestConfig, searchConfig);
		return config;
	}

	private static final int OPT_INPUT = 1;
	private static final int OPT_CFG = 2;
	private static final int OPT_FASTA = 3;
	private static final int OPT_COUNT = 4;
}
