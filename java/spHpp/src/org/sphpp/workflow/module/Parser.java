package org.sphpp.workflow.module;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.Constants;

import es.ehubio.Numbers;
import es.ehubio.cli.Argument;
import es.ehubio.io.Streams;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Psm;
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
		if( decoyPrefix == null )
			save(data.getPsms(), getValue(OPT_TARGET));
		else {
			save(data.getTargetPsms(), getValue(OPT_TARGET));
			save(data.getDecoyPsms(), getValue(OPT_DECOY));
		}
	}
	
	private void save( Set<Psm> psms, String path ) throws FileNotFoundException, IOException {
		ScoreType type = selectScore(psms.iterator().next());
		try( PrintWriter pw = new PrintWriter(Streams.getTextWriter(path)) ) {
			pw.print("psm"); pw.print(Constants.SEP);
			pw.print("spectrum"); pw.print(Constants.SEP);
			pw.print("expMass"); pw.print(Constants.SEP);
			pw.print("charge"); pw.print(Constants.SEP);
			pw.print("peptideSequence"); pw.print(Constants.SEP);
			pw.print("peptideMods"); pw.print(Constants.SEP);
			pw.println(type.getName());
			for( Psm psm : psms ) {
				pw.print(psm.getUniqueString()); pw.print(Constants.SEP);
				pw.print(psm.getSpectrum().getUniqueString()); pw.print(Constants.SEP);
				pw.print(Numbers.toString(psm.getExpMz())); pw.print(Constants.SEP);
				pw.print(psm.getCharge()); pw.print(Constants.SEP);
				pw.print(psm.getPeptide().getSequence()); pw.print(Constants.SEP);
				pw.print(psm.getPeptide().getMassSequence()); pw.print(Constants.SEP);
				pw.println(Numbers.toString(psm.getScoreByType(type).getValue()));
			}
		}
	}
	
	private ScoreType selectScore( Psm psm ) {
		ScoreType[] types = {ScoreType.SEQUEST_XCORR, ScoreType.MASCOT_SCORE, ScoreType.XTANDEM_EVALUE};
		for( ScoreType type : types )
			if( psm.getScoreByType(type) != null )
				return type;
		return psm.getScores().iterator().next().getType();
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
