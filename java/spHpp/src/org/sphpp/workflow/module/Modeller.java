package org.sphpp.workflow.module;

import static org.sphpp.workflow.data.Constants.SEP;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.LinkedItem;
import org.sphpp.workflow.data.LinkedMap;
import org.sphpp.workflow.data.Relations;
import org.sphpp.workflow.data.ScoreItem;

import es.ehubio.Numbers;
import es.ehubio.cli.Argument;
import es.ehubio.io.Streams;
import es.ehubio.model.Aminoacid;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class Modeller extends WorkflowModule {
	public Modeller() {
		super("Models peptide distribution in proteins.");
		
		Argument arg = new Argument(OPT_REL, 'i', "input");
		arg.setParamName("Pep2Prot.tsv");
		arg.setDescription("Input TSV file with peptide to protein relations.");
		arg.setDefaultValue("Pep2Prot.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_MQ, 'o', "output");
		arg.setParamName("MProt.tsv");
		arg.setDescription("Output TSV file with Mq and Nq values.");
		arg.setDefaultValue("MProt.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_MODS, 'm', "mods");
		arg.setParamName("aalist");
		arg.setDescription("Sequence of amino acids with variable modifications.");
		arg.setDefaultValue("M");
		addOption(arg);
		
		addOption(Arguments.getMaxPepMods());
		addOption(Arguments.getDiscard());
	}
	
	public static void main( String[] args ) {
		new Modeller().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		Relations rel = Relations.load(getValue(OPT_REL), getValue(Arguments.OPT_DISCARD));
		LinkedMap data = new LinkedMap();
		data.load(rel);
		String mods = getValue(OPT_MODS);
		Aminoacid[] varMods = new Aminoacid[mods.length()];
		for( int i = 0; i < varMods.length; i++ )
			varMods[i] = Aminoacid.parseLetter(mods.charAt(i));
		Set<ScoreItem> result = run(data, getIntValue(Arguments.OPT_MAX_PEP_MODS), varMods);
		try( PrintWriter pw = new PrintWriter(Streams.getTextWriter(getValue(OPT_MQ))) ) {
			pw.print(rel.getUpperLabel()); pw.print(SEP);
			pw.print("Mq"); pw.print(SEP);
			pw.println("Nq");
			for( ScoreItem item : result ) {
				pw.print(item.getId()); pw.print(SEP);
				pw.print(Numbers.toString(item.getScoreByType(ScoreType.MQ_EVALUE).getValue())); pw.print(SEP);
				pw.println(Numbers.toString(item.getScoreByType(ScoreType.NQ_EVALUE).getValue()));
			}
		}
	}
	
	public static Set<ScoreItem> run( LinkedMap data, int maxMods, Aminoacid... varMods) {
		Set<ScoreItem> result = new HashSet<ScoreItem>();
		for( LinkedItem protein : data.getUpperMap().values() ) {
			double Mq = 0.0;
			double Nq = 0.0;
			for( LinkedItem peptide : protein.getLinks() ) {
				double tryptic = (double)getTryptic(peptide.getId(), maxMods, varMods);
				Nq += tryptic;
				Mq += tryptic/peptide.getLinks().size();
			}
			ScoreItem item = new ScoreItem(protein.getId());
			item.putScore(new Score(ScoreType.MQ_EVALUE, Mq));
			item.putScore(new Score(ScoreType.NQ_EVALUE, Nq));
			result.add(item);
		}
		return result;
	}
	
	private static long getTryptic( String peptide, int maxMods, Aminoacid... varMods ) {
		if( varMods.length == 0 )
			return 1;
		int n = 0;
		for( int i = 0; i < varMods.length; i++ )
			n += countChars(peptide, varMods[i], maxMods)+1;
		return n;
	}
	
	private static long countChars( String seq, Aminoacid aa, int maxMods ) {
		char ch = Character.toUpperCase(aa.letter);
		char[] chars = seq.toUpperCase().toCharArray();
		long count = 0;
		for( int i = 0; i < chars.length; i++ )
			if( chars[i] == ch )
				count++;
		if( count > maxMods ) {
			logger.fine(String.format("Peptide '%s' with possible '%d' mods truncated to %d mods",seq,count,maxMods));
			count = maxMods;
		}
		return count;
	}
	
	private static final int OPT_REL = 1;
	private static final int OPT_MQ = 2;
	private static final int OPT_MODS = 3;
	private static final Logger logger = Logger.getLogger(Modeller.class.getName());
}
