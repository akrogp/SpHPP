package org.sphpp.workflow.module;

import static org.sphpp.workflow.Constants.SEP;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.InterMapeable;
import org.sphpp.workflow.data.Link;
import org.sphpp.workflow.data.LinkMap;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.RelationFile;

import es.ehubio.Numbers;
import es.ehubio.cli.Argument;
import es.ehubio.io.Streams;
import es.ehubio.model.Aminoacid;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class Modeller extends WorkflowModule {
	public Modeller() {
		super("Models peptide distribution.");
		
		Argument arg = new Argument(OPT_REL, 'i', "input");
		arg.setParamName("Seq2Prot.tsv");
		arg.setDescription("Input TSV file with peptide sequence relations.");
		//arg.setDefaultValue("Seq2Prot.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_MQ, 'o', "output");
		arg.setParamName("MdbProt.tsv");
		arg.setDescription("Output TSV file with M and N values.");
		arg.setDefaultValue("MdbProt.tsv.gz");
		addOption(arg);
		
		addOption(Arguments.getVarMods());		
		addOption(Arguments.getMaxPepMods());
		addOption(Arguments.getDiscard());
	}
	
	public static void main( String[] args ) {
		new Modeller().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		RelationFile rel = RelationFile.load(getValue(OPT_REL), getValue(Arguments.OPT_DISCARD), null);
		LinkMap<Link<Void,Void>,Link<Void,Void>> data = rel.getLinkMap();
		String mods = getValue(Arguments.OPT_VAR_MODS);
		Aminoacid[] varMods = new Aminoacid[mods.length()];
		for( int i = 0; i < varMods.length; i++ )
			varMods[i] = Aminoacid.parseLetter(mods.charAt(i));
		Set<ScoreItem> result = run(data, getIntValue(Arguments.OPT_MAX_PEP_MODS), varMods);
		try( PrintWriter pw = new PrintWriter(Streams.getTextWriter(getValue(OPT_MQ))) ) {
			pw.print(rel.getUpperLabel()); pw.print(SEP);
			pw.print(ScoreType.M_DVALUE.getName()); pw.print(SEP);
			pw.println(ScoreType.N_DVALUE.getName());
			for( ScoreItem item : result ) {
				pw.print(item.getId()); pw.print(SEP);
				pw.print(Numbers.toString(item.getScoreByType(ScoreType.M_DVALUE).getValue())); pw.print(SEP);
				pw.println(Numbers.toString(item.getScoreByType(ScoreType.N_DVALUE).getValue()));
			}
		}
	}
	
	public static <U extends InterMapeable<U,L>,L extends InterMapeable<L,U>>
	Set<ScoreItem> run( LinkMap<U,L> data, int maxMods, Aminoacid... varMods) {
		Set<ScoreItem> result = new HashSet<ScoreItem>();
		for( InterMapeable<U,L> protein : data.getUpperMap().values() ) {
			double Mq = 0.0;
			double Nq = 0.0;
			for( L peptide : protein.getLinks() ) {
				double tryptic = (double)getTryptic(peptide.getId(), maxMods, varMods);
				Nq += tryptic;
				Mq += tryptic/peptide.getLinks().size();
			}
			ScoreItem item = new ScoreItem(protein.getId());
			item.putScore(new Score(ScoreType.M_DVALUE, Mq));
			item.putScore(new Score(ScoreType.N_DVALUE, Nq));
			result.add(item);
		}
		return result;
	}
	
	private static long getTryptic( String peptide, int maxMods, Aminoacid... varMods ) {
		if( varMods.length == 0 )
			return 1;
		long n = 1;
		for( int i = 0; i < varMods.length; i++ )
			n *= countChars(peptide, varMods[i], maxMods)+1;
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
	private static final Logger logger = Logger.getLogger(Modeller.class.getName());
}
