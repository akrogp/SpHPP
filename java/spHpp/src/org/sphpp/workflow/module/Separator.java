package org.sphpp.workflow.module;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.file.PsmFile;
import org.sphpp.workflow.file.RelationFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Psm;

public class Separator extends WorkflowModule {
	
	public Separator() {
		super("Separates target and decoy PSMs");
		
		Argument arg = new Argument(OPT_PSMS, null, "input");
		arg.setParamName("Psms.tsv");
		arg.setDescription("Input TSV file with target and decoy PSMs.");
		addOption(arg);
		
		arg = new Argument(OPT_REL_TARGET, null, "relTarget");
		arg.setParamName("Seq2ProtTraget.tsv");
		arg.setDescription("Input TSV file with sequence to target proteins relations.");
		addOption(arg);
		
		arg = new Argument(OPT_REL_DECOY, null, "relDecoy");
		arg.setParamName("Seq2ProtDecoy.tsv");
		arg.setDescription("Input TSV file with sequence to decoy proteins relations.");
		addOption(arg);
				
		arg = new Argument(OPT_OUT_TARGET, null, "outTarget");
		arg.setParamName("TargetComp.tsv");
		arg.setDescription("Output TSV file for target PSMs after competition.");
		arg.setDefaultValue("TargetComp.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_OUT_DECOY, null, "outDecoy");
		arg.setParamName("DecoyComp.tsv");
		arg.setDescription("Output TSV file for decoy PSMs after competition.");
		arg.setDefaultValue("DecoyComp.tsv.gz");
		addOption(arg);
		
		addOption(Arguments.getDecoyPrefix());
		addOption(Arguments.getDiscard());
	}
	
	public static void main( String[] args ) {
		new Separator().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		Set<Psm> psms = PsmFile.load(getValue(OPT_PSMS)).getPsms();
		Set<String> setTargets = RelationFile
			.load(getValue(OPT_REL_TARGET), getValue(Arguments.OPT_DISCARD))
			.getLinkMap().getLowerMap().keySet();
		Set<String> setDecoys = RelationFile
			.load(getValue(OPT_REL_DECOY), getValue(Arguments.OPT_DISCARD))
			.getLinkMap().getLowerMap().keySet();
		
		Set<Psm> targets = new HashSet<>();
		Set<Psm> decoys = new HashSet<>();		
		run(psms, setTargets, setDecoys, targets, decoys);		
		
		PsmFile.save(targets, getValue(OPT_OUT_TARGET));
		PsmFile.save(decoys, getValue(OPT_OUT_DECOY));
	}

	private void run(Set<Psm> psms, Set<String> setTargets, Set<String> setDecoys, Set<Psm> targets, Set<Psm> decoys) {
		int dups = 0;
		for(Psm psm : psms) {
			boolean isTarget = setTargets.contains(psm.getPeptide().getSequence());
			boolean isDecoy = setDecoys.contains(psm.getPeptide().getSequence());
			if( isTarget && isDecoy )
				dups++;
			else if( isTarget )
				targets.add(psm);
			else if( isDecoy )
				decoys.add(psm);
		}
		if( dups == 0 )
			logger.info("No shared target/decoy sequences found");
		else
			logger.warning(String.format("Removed %d PSMs with same peptide sequence in target and decoy", dups));		
	}

	private static final Logger logger = Logger.getLogger(Separator.class.getName());
	private static final int OPT_PSMS = 1;
	private static final int OPT_REL_TARGET = 2;
	private static final int OPT_REL_DECOY = 3;
	private static final int OPT_OUT_TARGET = 4;
	private static final int OPT_OUT_DECOY = 5;
}
