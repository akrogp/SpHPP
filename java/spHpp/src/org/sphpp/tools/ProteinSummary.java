package org.sphpp.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.sphpp.workflow.Utils;
import org.sphpp.workflow.data.Link;
import org.sphpp.workflow.data.LinkMap;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.RelationFile;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.io.CsvReader;
import es.ehubio.io.CsvUtils;
import es.ehubio.io.FileUtils;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class ProteinSummary {
	public static class Experiment {
		public Experiment(String subdir, String name, boolean showPeptides) {
			this.name = name == null ? subdir : name;
			this.dir = new File(INPUT,subdir).getAbsolutePath();			
			this.showPeptides = showPeptides;
		}
		public String dir, name;
		public boolean showPeptides;
	}
	
	public static class Peptide {
		public String id;
		public double score;
		public double qValue;
	}
	
	public static class Details {
		public double score, fdr, qValue;
		public final List<Peptide> peptides = new ArrayList<>();
	}
	
	public static class Protein {
		public String acc, name, gene;
		public boolean decoy;
		public int len;
		public double mObs, mExp;
		public final List<Details> exps = new ArrayList<>();
	}
	
	public static void main(String[] args) throws Exception {
		List<Experiment> exps = new ArrayList<>();
		exps.add(new Experiment("lpm0", "lpm", true));
		exps.add(new Experiment("lpm", "lpm mayu", false));
		exps.add(new Experiment("lpc2", "lpc2 mayu", false));
		exps.add(new Experiment("lpc2-lpm", "lpc2-lpm mayu", false));
				
		Collection<Protein> summaryTarget = createSummary(false, exps, 2);
		Collection<Protein> summaryDecoy = createSummary(true, exps, 2);
		List<Protein> summary = new ArrayList<>();
		summary.addAll(summaryTarget);
		summary.addAll(summaryDecoy);
		printSummary(summary, exps);
		
		LOG.info("finished!");
	}
	
	private static Collection<Protein> createSummary(boolean decoy, List<Experiment> exps, int ref) throws Exception {
		Map<String, Protein> map = new HashMap<>();
		String refExp = exps.get(ref).dir;
		loadAccessions(map, new File(refExp,decoy?"FdrProtDecoy.tsv.gz":"FdrProtTarget.tsv.gz"));
		loadFastaInfo(map, decoy, FASTA);
		loadM(map, new File(refExp,decoy?"MProtDecoy.tsv.gz":"MProtTarget.tsv.gz"));
		for( Protein protein : map.values() )
			protein.decoy = decoy;
		for( Experiment exp : exps )
			loadDetails(map, decoy, exp);
		return map.values();
	}

	private static void loadDetails(Map<String, Protein> map, boolean decoy, Experiment exp) throws IOException, ParseException {
		ScoreFile<ScoreItem> protScores = ScoreFile.load(new File(exp.dir,decoy?"FdrProtDecoy.tsv.gz":"FdrProtTarget.tsv.gz").getAbsolutePath());
		Map<String,ScoreItem> pepScores = null;
		LinkMap<Link<Void, Void>, Link<Void, Void>> pep2prot = null;		
		if( exp.showPeptides ) {
			pepScores = Utils.getMap(ScoreFile.load(FileUtils.concat(exp.dir,decoy?"FdrPepDecoy.tsv.gz":"FdrPepTarget.tsv.gz")).getItems());
			pep2prot = RelationFile.load(FileUtils.concat(exp.dir,decoy?"Pep2ProtDecoy.tsv.gz":"Pep2ProtTarget.tsv.gz")).getLinkMap();
		}
		for( ScoreItem item : protScores.getItems() ) {
			Protein protein = map.get(item.getId());
			if( protein == null )
				continue;
			Details details = new Details();
			Score score = item.getScoreByType(ScoreType.LPCORR_SCORE);
			if( score == null )
				score = item.getScoreByType(ScoreType.LP_SCORE);
			details.score = score.getValue();
			details.fdr = item.getScoreByType(ScoreType.LOCAL_FDR).getValue();
			details.qValue = item.getScoreByType(ScoreType.Q_VALUE).getValue();
			protein.exps.add(details);
			if( !exp.showPeptides )
				continue;
			for( Link<Void, Void> pepLink : pep2prot.getUpper(protein.acc).getLinks() ) {
				ScoreItem pepScore = pepScores.get(pepLink.getId());
				Peptide peptide = new Peptide();
				peptide.id = pepLink.getId();
				peptide.score = pepScore.getScoreByType(ScoreType.LP_SCORE).getValue();
				peptide.qValue = pepScore.getScoreByType(ScoreType.Q_VALUE).getValue();
				details.peptides.add(peptide);
			}
			Collections.sort(details.peptides,new Comparator<Peptide>() {
				@Override
				public int compare(Peptide p1, Peptide p2) {
					return (int)Math.signum(p2.score - p1.score);
				}
			});
		}
	}

	private static void loadM(Map<String, Protein> map, File file) throws IOException, ParseException {
		ScoreFile<ScoreItem> scores = ScoreFile.load(file.getAbsolutePath());
		for( ScoreItem item : scores.getItems() ) {
			Protein protein = map.get(item.getId());
			if( protein == null )
				continue;
			protein.mExp = item.getScoreByType(ScoreType.M_EVALUE).getValue();
			protein.mObs = item.getScoreByType(ScoreType.M_OVALUE).getValue();
		}
	}

	private static void loadFastaInfo(Map<String, Protein> map, boolean decoy, String fastaPath ) throws IOException, InvalidSequenceException {
		List<Fasta> fastas = Fasta.readEntries(fastaPath, SequenceType.PROTEIN);
		for( Fasta fasta : fastas ) {
			String acc = fasta.getAccession();
			if( decoy )
				acc = PREFIX+acc;
			Protein protein = map.get(acc);
			if( protein == null )
				continue;
			protein.name = fasta.getProteinName();
			protein.gene = fasta.getGeneName();
			protein.len = fasta.getSequence().length();
		}
	}

	private static void loadAccessions(Map<String, Protein> map, File file) throws IOException {
		CsvReader csv = new CsvReader(SEP, true, false);
		csv.open(file.getAbsolutePath());
		while( csv.readLine() != null ) {
			Protein protein = new Protein();
			protein.acc = csv.getField(0);
			map.put(protein.acc, protein);
		}
		csv.close();
	}

	private static void printSummary(Collection<Protein> summary, List<Experiment> exps) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(OUTPUT);
		pw.print(CsvUtils.getCsv(SEP, "protein", "name", "gene", "T/D", "length", "M(obs)", "M(exp)"));		
		for( Experiment exp : exps ) {
			pw.print(SEP);
			pw.print(CsvUtils.getCsv(SEP,String.format("score (%s)", exp.name), String.format("fdr (%s)", exp.name),String.format("q-value (%s)", exp.name)));
		}
		for( Experiment exp : exps )
			if( exp.showPeptides ) {
				pw.print(SEP);
				pw.print(CsvUtils.getCsv(SEP,
					String.format("#peps (%s)",exp.name),					
					String.format("LPPep (%s)",exp.name),
					String.format("peps (%s)",exp.name)
				));
				pw.print(SEP);
				pw.print(CsvUtils.getCsv(SEP,
					String.format("#peps < %.2f FDR (%s)",FDR,exp.name),					
					String.format("LPPep < %.2f FDR (%s)",FDR,exp.name),
					String.format("peps < %.2f FDR (%s)",FDR,exp.name)
				));
			}
		pw.println();
		for( Protein protein : summary ) {
			pw.print(CsvUtils.getCsv(SEP, protein.acc, protein.name, protein.gene, protein.decoy?"D":"T", protein.len, protein.mObs, protein.mExp));			
			for( Details details : protein.exps ) {
				pw.print(SEP);
				pw.print(CsvUtils.getCsv(SEP, details.score, details.fdr, details.qValue));						
			}
			int exp = 0;
			for( Details details : protein.exps ) {
				if( exps.get(exp).showPeptides ) {
					pw.print(SEP);
					printPeptides(pw, details.peptides);
					pw.print(SEP);
					printPeptides(pw, fdrFilter(details.peptides,FDR));
				}
				exp++;
			}
			pw.println();
		}
		pw.close();
	}

	private static List<Peptide> fdrFilter(List<Peptide> peptides, double fdr) {
		List<Peptide> result = new ArrayList<>();
		for( Peptide peptide : peptides )
			if( peptide.qValue < fdr )
				result.add(peptide);
		return result;
	}

	private static void printPeptides(PrintWriter pw, List<Peptide> peptides ) {
		pw.print(peptides.size());
		pw.print(SEP);
		Object[] tmp = new String[peptides.size()];
		int i = 0;
		for( Peptide peptide : peptides )
			tmp[i++] = ""+peptide.score;
		pw.print(CsvUtils.getCsv(SEP2, tmp));
		pw.print(SEP);
		i = 0;
		for( Peptide peptide : peptides )
			tmp[i++] = peptide.id;						
		pw.print(CsvUtils.getCsv(SEP2, tmp));
	}

	private static final Logger LOG = Logger.getLogger(ProteinSummary.class.getName());
	private static final String INPUT = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/AdultFrontalCortex/Sep16/lego";
	private static final String OUTPUT = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/AdultFrontalCortex/Sep16/comparison/summary.tsv";
	private static final String FASTA = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/gencode24-principal-unique.target.fasta";
	private static final String SEP = "\t";
	private static final String SEP2 = ",";
	private static final double FDR = 0.01;
	private static final String PREFIX = "decoy-";
}