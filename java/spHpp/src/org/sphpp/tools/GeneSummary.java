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

import es.ehubio.io.CsvUtils;
import es.ehubio.io.FileUtils;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class GeneSummary {
	private static class Experiment {
		public Experiment(String dir, String subdir, String name, boolean showScore, boolean showPeptides) {
			this.name = name == null ? subdir : name;
			this.dir = new File(dir,subdir).getAbsolutePath();
			this.showScore = showScore;
			this.showPeptides = showPeptides;
		}
		public Experiment(String dir, String subdir) {
			this(dir,subdir,null,true,false);
		}
		public Experiment(String dir, String subdir, boolean showScore) {
			this(dir,subdir,null,showScore,false);
		}
		public String dir, name;
		public boolean showPeptides, showScore;
	}
	
	private static class Peptide {
		public String id;
		public double score;
		public double qValue;
	}
	
	private static class Details {
		public double score;
		public Double fdr, qValue;
		public final List<Peptide> peptides = new ArrayList<>();
	}
	
	private static class Gene {
		public String acc;
		public boolean decoy;
		public final Map<String,Details> exps = new HashMap<>();
	}
	
	public static void main(String[] args) throws Exception {
		/*if( args.length != 2 )
			throw new Exception(String.format("Usage: %s <path> <tissue>", GeneSummary.class.getCanonicalName()));
		run(args[0], args[1]);*/
		//run("/home/gorka/Descargas/ownCloud/Bio/Feb17-IL","Proteome");
		run("/home/gorka/Descargas/ownCloud/Bio/Feb17-IL","HouseKeeping");
		/*run("/home/gorka/Descargas/ownCloud/Bio/Feb17-IL","Adult_Testis");
		run("/home/gorka/Descargas/ownCloud/Bio/Feb17-IL","Adult_Frontalcortex");
		run("/home/gorka/Descargas/ownCloud/Bio/Feb17-IL","Adult_Heart");
		run("/home/gorka/Descargas/ownCloud/Bio/Feb17-IL","Adult_Liver");*/
	}
	
	public static void run( String path, String tissue ) throws Exception {
		List<Experiment> exps = new ArrayList<>();
		
		/*exps.add(new Experiment(path,tissue+"/LPM-FDRn"));
		exps.add(new Experiment(path,tissue+"/LPM-FDRm",false));
		exps.add(new Experiment(path,tissue+"/LPF-FDRn"));
		exps.add(new Experiment(path,tissue+"/LPM-FDRp"));
		exps.add(new Experiment(path,tissue+"/LPG1-FDRr"));
		exps.add(new Experiment(path,tissue+"/LPGN-FDRr",null,true,true));
		exps.add(new Experiment(path,tissue+"/LPG-FDRr"));
		exps.add(new Experiment(path,tissue+"/LPGB-FDRr"));*/
		
		/*exps.add(new Experiment(path,"Proteome/FILTER-LPM-FDRn"));
		exps.add(new Experiment(path,"All_Tissues/LPM-FDRn"));
		exps.add(new Experiment(path,"All_Tissues/LPM-FDRm"));
		exps.add(new Experiment(path,"All_Tissues/LPM-FDRp"));
		exps.add(new Experiment(path,"All_Tissues/LPF-FDRn"));
		exps.add(new Experiment(path,"Proteome/LPG1-LPG-FDRr"));
		exps.add(new Experiment(path,"Proteome/LPG1-LPG1-FDRr"));
		exps.add(new Experiment(path,"Proteome/LPG1-LPGN-FDRr"));
		exps.add(new Experiment(path,"Proteome/LPG1-LPGB-FDRr"));*/
		
		exps.add(new Experiment(path,"Adult_Frontalcortex/LPG1-FDRr"));
		exps.add(new Experiment(path,"Adult_Frontalcortex/LPGN-FDRr"));
		exps.add(new Experiment(path,"Adult_Testis/LPG1-FDRr"));
		exps.add(new Experiment(path,"Adult_Testis/LPGN-FDRr"));
		exps.add(new Experiment(path,"Adult_Heart/LPG1-FDRr"));
		exps.add(new Experiment(path,"Adult_Heart/LPGN-FDRr"));
		exps.add(new Experiment(path,"Adult_Liver/LPG1-FDRr"));
		exps.add(new Experiment(path,"Adult_Liver/LPGN-FDRr"));
		
		
		Collection<Gene> summaryTarget = createSummary(false, exps);
		Collection<Gene> summaryDecoy = createSummary(true, exps);
		List<Gene> summary = new ArrayList<>();
		summary.addAll(summaryTarget);
		summary.addAll(summaryDecoy);
		printSummary(summary, exps,String.format("%s/Summary/%s.tsv",path,tissue));
		
		LOG.info("finished!");
	}
	
	private static Collection<Gene> createSummary(boolean decoy, List<Experiment> exps) throws Exception {
		Map<String, Gene> map = new HashMap<>();		
		for( Experiment exp : exps )
			loadDetails(map, decoy, exp);
		//loadFastaInfo(map, decoy, FASTA);
		//String refDir = exps.get(0).dir;
		//loadM(map, new File(refDir,decoy?"../MProtDecoy.tsv.gz":"../MProtTarget.tsv.gz"));
		for( Gene gene : map.values() )
			gene.decoy = decoy;		
		return map.values();
	}

	private static void loadDetails(Map<String, Gene> map, boolean decoy, Experiment exp) throws IOException, ParseException {
		ScoreFile<ScoreItem> genScores = ScoreFile.load(new File(exp.dir,decoy?"FdrGenDecoy.tsv.gz":"FdrGenTarget.tsv.gz").getAbsolutePath());
		Map<String,ScoreItem> pepScores = null;
		LinkMap<Link<Void, Void>, Link<Void, Void>> pep2gen = null;		
		if( exp.showPeptides ) {
			pepScores = Utils.getMap(ScoreFile.load(FileUtils.concat(exp.dir,decoy?"../FdrPepDecoy.tsv.gz":"../FdrPepTarget.tsv.gz")).getItems());
			pep2gen = RelationFile.load(FileUtils.concat(exp.dir,decoy?"../UPep2GenDecoy.tsv.gz":"../UPep2GenTarget.tsv.gz")).getLinkMap();
		}
		for( ScoreItem item : genScores.getItems() ) {
			Gene gene = map.get(item.getId());
			if( gene == null ) {
				gene = new Gene();
				gene.acc = item.getId();
				map.put(gene.acc, gene);
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
			gene.exps.put(exp.name, details);
			if( !exp.showPeptides )
				continue;
			for( Link<Void, Void> pepLink : pep2gen.getUpper(gene.acc).getLinks() ) {
				ScoreItem pepScore = pepScores.get(pepLink.getId());
				Peptide peptide = new Peptide();
				peptide.id = pepLink.getId();
				//peptide.score = pepScore.getScoreByType(ScoreType.LPCORR_SCORE).getValue();
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

	/*private static void loadM(Map<String, Protein> map, File file) throws IOException, ParseException {
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
	}*/

	/*private static void loadFastaInfo(Map<String, Protein> map, boolean decoy, String fastaPath ) throws IOException, InvalidSequenceException {
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
	}*/

	private static void printSummary(Collection<Gene> summary, List<Experiment> exps, String output) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(output);
		pw.print(CsvUtils.getCsv(SEP, "gene", "T/D"));	
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
		for( Gene gene : summary ) {
			pw.print(CsvUtils.getCsv(SEP, gene.acc, gene.decoy?"D":"T"));
			for( Experiment exp : exps ) {
				Details details = gene.exps.get(exp.name);				
				pw.print(SEP);				
				if( exp.showScore ) {
					pw.print(details==null?"":details.score);
					pw.print(SEP);
				}
				pw.print(CsvUtils.getCsv(SEP, details==null?"":details.fdr, details==null?"":details.qValue));
			}
			for( Experiment exp : exps )
				if( exp.showPeptides ) {
					Details details = gene.exps.get(exp.name);
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

	private static final Logger LOG = Logger.getLogger(GeneSummary.class.getName());
	//private static final String OUTPUT = "/home/gorka/Descargas/ownCloud/Bio/Pandey-GeneUniquePeptides/Summary/Proteome.tsv";
	//private static final String FASTA = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/gencode25.target.fasta";
	private static final String SEP = "\t";
	private static final String SEP2 = ",";
	private static final double FDR = 0.01;
	//private static final String PREFIX = "decoy-";
}