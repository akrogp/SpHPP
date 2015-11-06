package org.sphpp.workflow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import es.ehubio.Strings;
import es.ehubio.cli.Argument;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.io.Streams;
import es.ehubio.proteomics.Enzyme;
import es.ehubio.proteomics.pipeline.DecoyDb;
import es.ehubio.proteomics.pipeline.DecoyDb.Strategy;

public class PseudoInverter extends TsvModule {	
	private final static Logger logger = Logger.getLogger(PseudoInverter.class.getName());
	private static final int OPT_TARGET = OPT_BASE+1;
	private static final int OPT_DECOY = OPT_TARGET+1;
	private static final int OPT_CONCAT = OPT_DECOY+1;
	private static final int OPT_PREFIX = OPT_CONCAT+1;
	private static final int OPT_ENZYME = OPT_PREFIX+1;
	
	public static void main( String[] args ) throws Exception {
		new PseudoInverter().run(args);
	}
	
	public PseudoInverter() {
		super(false,"Generates decoys using pseudo-reverse strategy with trypsin and proline rule.");
		
		Argument arg = new Argument(OPT_TARGET, 't', "target");
		arg.setParamName("target.fasta");
		arg.setDescription("Input fasta file path with target sequences.");
		addOption(arg);
		
		arg = new Argument(OPT_DECOY, 'd', "decoy", true);
		arg.setParamName("decoy.fasta");
		arg.setDescription("Output fasta file path with decoy sequences. By default generated from target file name.");
		addOption(arg);
		
		arg = new Argument(OPT_CONCAT, 'c', "concat", true);
		arg.setParamName("concat.fasta");
		arg.setDescription("Output fasta file path with concatenated target and decoy sequences. By default generated from target file name.");
		addOption(arg);
		
		arg = new Argument(OPT_PREFIX, 'p', "prefix");
		arg.setParamName("prefix");
		arg.setDescription("Prefix to be added to decoy entries.");
		arg.setDefaultValue("decoy-");
		addOption(arg);
		
		arg = new Argument(OPT_ENZYME, 'e', "enzyme");
		arg.setChoices(Strings.fromArray(Enzyme.values()));
		arg.setDescription("Enzyme used for digestion.");
		arg.setDefaultValue(Enzyme.TRYPSIN.toString());
		addOption(arg);
	}
	
	@Override
	protected void run(List<Argument> args) throws Exception {		
		run(
			getValue(OPT_TARGET), getValue(OPT_DECOY), getValue(OPT_CONCAT),
			getValue(OPT_PREFIX), Enzyme.valueOf(getValue(OPT_ENZYME))
		);
	}
	
	public void run( String target, String decoy, String concat, String prefix, Enzyme enzyme ) throws FileNotFoundException, IOException, InvalidSequenceException {
		if( decoy == null )
			decoy = target.replaceAll(".fasta", ".decoy.fasta");
		if( concat == null )
			concat = target.replaceAll(".fasta", ".concat.fasta");
		
		logger.info(String.format("Creating decoy in '%s' ...", decoy));
		DecoyDb.create(target, decoy, Strategy.PSEUDO_REVERSE, enzyme, prefix);
		
		logger.info(String.format("Concatenating target and decoy into '%s' ...", concat));
		try(				
			Reader targetReader = Streams.getTextReader(target);
			Reader decoyReader = Streams.getTextReader(decoy);
			Writer concatWriter = Streams.getTextWriter(concat) ) {			
			IOUtils.copy(targetReader, concatWriter);
			IOUtils.copy(decoyReader, concatWriter);
		}
	}
}
