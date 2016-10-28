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
import es.ehubio.io.CsvUtils;
import es.ehubio.io.FileUtils;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class ProteinSummary {
	public static class Experiment {
		public Experiment(String subdir, String name, boolean showScore, boolean showPeptides) {
			this.name = name == null ? subdir : name;
			this.dir = new File(INPUT,subdir).getAbsolutePath();
			this.showScore = showScore;
			this.showPeptides = showPeptides;
		}
		public String dir, name;
		public boolean showPeptides, showScore;
	}
	
	public static class Peptide {
		public String id;
		public double score;
		public double qValue;
	}
	
	public static class Details {
		public double score;
		public Double fdr, qValue;
		public final List<Peptide> peptides = new ArrayList<>();
	}
	
	public static class Protein {
		public String acc, name, gene;
		public boolean decoy;
		public int len;
		public double mObs, mExp;
		public final Map<String,Details> exps = new HashMap<>();
	}
	
	public static void main(String[] args) throws Exception {
		List<Experiment> exps = new ArrayList<>();
		exps.add(new Experiment("AdultFrontalCortex/LPM", null, true, false));
		exps.add(new Experiment("AdultFrontalCortex/LPQGb", null, true, false));
		exps.add(new Experiment("AdultHeart/LPM", null, true, false));
		exps.add(new Experiment("AdultHeart/LPQGb", null, true, false));
		exps.add(new Experiment("AdultLiver/LPM", null, true, false));
		exps.add(new Experiment("AdultLiver/LPQGb", null, true, false));
		exps.add(new Experiment("AdultTestis/LPM", null, true, false));
		exps.add(new Experiment("AdultTestis/LPQGb", null, true, false));
		exps.add(new Experiment("Proteome/REPLICA-LPM", null, true, false));
		exps.add(new Experiment("Proteome/REPLICA-LPQGb", null, true, false));
		exps.add(new Experiment("Proteome/TISSUE-LPM", null, true, false));
		exps.add(new Experiment("Proteome/TISSUE-LPQGb", null, true, false));
		exps.add(new Experiment("Proteome/HOUSE_KEEPING-LPM", null, true, false));
		exps.add(new Experiment("Proteome/HOUSE_KEEPING-LPQGb", null, true, false));
		
		Collection<Protein> summaryTarget = createSummary(false, exps);
		Collection<Protein> summaryDecoy = createSummary(true, exps);
		List<Protein> summary = new ArrayList<>();
		summary.addAll(summaryTarget);
		summary.addAll(summaryDecoy);
		printSummary(summary, exps);
		
		LOG.info("finished!");
	}
	
	private static Collection<Protein> createSummary(boolean decoy, List<Experiment> exps) throws Exception {
		Map<String, Protein> map = new HashMap<>();		
		for( Experiment exp : exps )
			loadDetails(map, decoy, exp);
		loadFastaInfo(map, decoy, FASTA);
		String refDir = exps.get(0).dir;
		loadM(map, new File(refDir,decoy?"../MProtDecoy.tsv.gz":"../MProtTarget.tsv.gz"));
		for( Protein protein : map.values() )
			protein.decoy = decoy;		
		return map.values();
	}

	private static void loadDetails(Map<String, Protein> map, boolean decoy, Experiment exp) throws IOException, ParseException {
		ScoreFile<ScoreItem> protScores = ScoreFile.load(new File(exp.dir,decoy?"FdrProtDecoy.tsv.gz":"FdrProtTarget.tsv.gz").getAbsolutePath());
		Map<String,ScoreItem> pepScores = null;
		LinkMap<Link<Void, Void>, Link<Void, Void>> pep2prot = null;		
		if( exp.showPeptides ) {
			pepScores = Utils.getMap(ScoreFile.load(FileUtils.concat(exp.dir,decoy?"../FdrPepDecoy.tsv.gz":"../FdrPepTarget.tsv.gz")).getItems());
			pep2prot = RelationFile.load(FileUtils.concat(exp.dir,decoy?"../Pep2ProtDecoy.tsv.gz":"../Pep2ProtTarget.tsv.gz")).getLinkMap();
		}
		for( ScoreItem item : protScores.getItems() ) {
			Protein protein = map.get(item.getId());
			if( protein == null ) {
				protein = new Protein();
				protein.acc = item.getId();
				map.put(protein.acc, protein);
			}
			Details details = new Details();
			Score score = item.getScoreByType(ScoreType.LPCORR_SCORE);
			if( score == null )
				score = item.getScoreByType(ScoreType.LP_SCORE);
			details.score = score.getValue();
			score = item.getScoreByType(ScoreType.LOCAL_FDR);
			if( score != null )
				details.fdr = score.getValue();
			score = item.getScoreByType(ScoreType.Q_VALUE);
			if( score != null )
				details.qValue = score.getValue();
			protein.exps.put(exp.name, details);
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
		if( !file.exists() )
			return;
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

	private static void printSummary(Collection<Protein> summary, List<Experiment> exps) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(OUTPUT);
		pw.print(CsvUtils.getCsv(SEP, "protein", "name", "gene", "T/D", "length", "M(obs)", "M(exp)"));		
		for( Experiment exp : exps ) {
			pw.print(SEP);
			if( exp.showScore ) {
				pw.print(String.format("score (%s)", exp.name));
				pw.print(SEP);
			}
			pw.print(CsvUtils.getCsv(SEP,String.format("fdr (%s)", exp.name),String.format("q-value (%s)", exp.name)));
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
			for( Experiment exp : exps ) {
				Details details = protein.exps.get(exp.name);				
				pw.print(SEP);				
				if( exp.showScore ) {
					pw.print(details==null?"":details.score);
					pw.print(SEP);
				}
				pw.print(CsvUtils.getCsv(SEP, details==null?"":details.fdr, details==null?"":details.qValue));
			}
			for( Experiment exp : exps )
				if( exp.showPeptides ) {
					Details details = protein.exps.get(exp.name);
					pw.print(SEP);
					printPeptides(pw, details==null?null:details.peptides);
					pw.print(SEP);
					printPeptides(pw, details==null?null:fdrFilter(details.peptides,FDR));
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
		if( peptides == null ) {
			pw.print(CsvUtils.getCsv(SEP, "", "", ""));
			return;
		}
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
	private static final String INPUT = "/home/gorka/Descargas/ownCloud/Bio/Pandey-UniquePeptipes";
	private static final String OUTPUT = "/home/gorka/Descargas/ownCloud/Bio/Pandey-UniquePeptipes/summary/Proteome.tsv";
	private static final String FASTA = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/gencode24-principal-unique.target.fasta";
	private static final String SEP = "\t";
	private static final String SEP2 = ",";
	private static final double FDR = 0.01;
	private static final String PREFIX = "decoy-";
}