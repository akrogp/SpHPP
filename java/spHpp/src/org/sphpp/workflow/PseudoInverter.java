package org.sphpp.workflow;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

import org.apache.commons.io.IOUtils;

import es.ehubio.cli.Argument;
import es.ehubio.io.Streams;
import es.ehubio.proteomics.Enzyme;
import es.ehubio.proteomics.pipeline.DecoyDb;
import es.ehubio.proteomics.pipeline.DecoyDb.Strategy;

public class PseudoInverter extends TsvModule {	
	private static final int OPT_TARGET = OPT_BASE+1;
	private static final int OPT_DECOY = OPT_TARGET+1;
	private static final int OPT_CONCAT = OPT_DECOY+1;
	private static final int OPT_PREFIX = OPT_CONCAT+1;
	
	public static void main( String[] args ) throws Exception {
		new PseudoInverter().run(args);
	}
	
	public PseudoInverter() {
		super(false,"Generates decoys using pseudo-reverse strategy with trypsin and proline rule.");
		
		Argument arg = new Argument(OPT_TARGET, 't', "target");
		arg.setParam("target.fasta");
		arg.setDescription("input fasta file with target sequences");
		addOption(arg);
		
		arg = new Argument(OPT_DECOY, 'd', "decoy");
		arg.setParam("decoy.fasta");
		arg.setDescription("output fasta file with decoy sequences");
		arg.setOptional();
		addOption(arg);
		
		arg = new Argument(OPT_CONCAT, 'c', "concat");
		arg.setParam("concat.fasta");
		arg.setDescription("output fasta file with concatenated target and decoy sequences");
		arg.setOptional();
		addOption(arg);
		
		arg = new Argument(OPT_PREFIX, 'p', "prefix");
		arg.setParam("prefix");
		arg.setDescription("prefix to be added to decoy entries, default is '-prefix'");
		arg.setOptional();
		addOption(arg);
	}
	
	@Override
	protected void run(List<Argument> args) throws Exception {
		Argument argTarget = getArgument(OPT_TARGET);
		Argument argDecoy = getArgument(OPT_DECOY);
		Argument argConcat = getArgument(OPT_CONCAT);
		Argument argPrefix = getArgument(OPT_PREFIX);
		
		String target = argTarget.getParam();
		String decoy = argDecoy == null ? target.replaceAll(".fasta", ".decoy.fasta") : argDecoy.getParam();
		String concat = argDecoy == null ? target.replaceAll(".fasta", ".concat.fasta") : argConcat.getParam();
		String prefix = argPrefix == null ? "decoy-" : argPrefix.getParam();
		
		DecoyDb.create(target, decoy, Strategy.PSEUDO_REVERSE, Enzyme.TRYPSIN, prefix);
		
		try(
			Reader targetReader = Streams.getTextReader(target);
			Reader decoyReader = Streams.getTextReader(decoy);
			Writer concatWriter = Streams.getTextWriter(concat) ) {
			IOUtils.copy(targetReader, concatWriter);
			IOUtils.copy(decoyReader, concatWriter);
		}
	}
}
