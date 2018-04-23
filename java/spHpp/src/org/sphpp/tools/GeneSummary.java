package org.sphpp.tools;

import java.io.File;
import java.io.FilenameFilter;
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
import es.ehubio.io.Streams;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class GeneSummary {
	private static class Experiment {
		public Experiment(String dir, String subdir, String name, boolean showScore, boolean showPeptides, boolean showPeptideCount) {
			this.name = name == null ? subdir : name;
			this.dir = new File(dir,subdir).getAbsolutePath();
			this.showScore = showScore;
			this.showPeptides = showPeptides;
			this.showPeptideCount = showPeptideCount;
		}
		
		public Experiment(String dir, String subdir, String name, boolean showScore, boolean showPeptides) {
			this(dir, subdir, name, showScore, showPeptides, false);
		}
		
		public Experiment(String dir, String subdir) {
			this(dir,subdir,null,true,false);
		}
		public Experiment(String dir, String subdir, boolean showScore) {
			this(dir,subdir,null,showScore,false);
		}
		public final String dir, name;
		public final boolean showPeptides, showScore, showPeptideCount;
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
		public String name;
		public boolean decoy;
		public final Map<String,Details> exps = new HashMap<>();
	}
	
	public static void main(String[] args) throws Exception {
		/*if( args.length != 2 )
			throw new Exception(String.format("Usage: %s <path> <tissue>", GeneSummary.class.getCanonicalName()));
		run(args[0], args[1]);*/
		//run("/home/gorka/Descargas/ownCloud/Bio/Feb17-IL","Proteome");
		//run("/home/gorka/Descargas/ownCloud/Bio/Feb17-IL","HouseKeeping");
		/*run("/home/gorka/Descargas/ownCloud/Bio/Feb17-IL","Adult_Testis");
		run("/home/gorka/Descargas/ownCloud/Bio/Feb17-IL","Adult_Frontalcortex");
		run("/home/gorka/Descargas/ownCloud/Bio/Feb17-IL","Adult_Heart");
		run("/home/gorka/Descargas/ownCloud/Bio/Feb17-IL","Adult_Liver");*/
		//run("/home/gorka/Descargas/ownCloud/Bio/Mar17-Sequest","Adult_Heart");
		//run("/media/gorka/EhuBio/Lego","Comet","Proteome");
		//run("/media/gorka/EhuBio/Lego","XTandem","Proteome");
		
		//runAll("/media/gorka/EhuBio/Lego");
		
		//runEngineComp("/media/gorka/EhuBio/Lego", "Proteome", "LPG1-LPGN-FDRr");
		
		//runProteomeTissues("/media/gorka/EhuBio/Lego");
		
		runPandeyComp("/media/gorka/EhuBio/Lego");
	}

	public static void runAll( String path ) throws Exception {
		//String[] engines = { "XTandem" };
		String[] engines = { "Comet", "Fragger" };
		
		for( String engine : engines ) {
			File dir = new File(path);
			File[] tissues = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.contains("Adult_") || name.contains("Fetal_");
				}
			});
			for( File tissue : tissues ) {
				LOG.info(String.format("Engine: %s, Tissue: %s", engine, tissue.getName()));
				runExp(dir.getAbsolutePath(), engine, tissue.getName());
			}
			runProteome(path, engine);
		}
	}
	
	public static void runProteomeTissues( String path ) throws Exception {
		String[] engines = { "XTandem", "Comet", "Fragger" };
		//String[] engines = { "XTandem" };
		//int count = 0;
		for( String engine : engines ) {
			File dir = new File(path);
			File[] tissues = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.contains("Adult_") || name.contains("Fetal_");
				}
			});
			List<Experiment> exps = new ArrayList<>();
			exps.add(new Experiment(path, String.format("Proteome/%s/LPG1-LPGN-FDRr", engine), null, true, false, false));
			for( File tissue : tissues ) {
				exps.add(new Experiment(path, String.format("%s/%s/LPGN-FDRr", tissue.getName(), engine), null, true, false, true));
				/*if( count++ > 2 )
					break;*/
			}
			run(exps, String.format("%s/Summary/ProteomeTissues-%s.tsv.gz", path, engine));
		}
	}
	
	public static void runEngineComp( String path, String tissue, String score ) throws Exception {
		List<Experiment> exps = new ArrayList<>();
		
		exps.add(new Experiment(path,String.format("%s/XTandem/%s",tissue,score)));
		exps.add(new Experiment(path,String.format("%s/Comet/%s",tissue,score)));
		exps.add(new Experiment(path,String.format("%s/Fragger/%s",tissue,score)));
		
		run(exps, String.format("%s/Summary/%s-%s-Engines.tsv.gz",path,tissue,score));
	}
	
	private static void runPandeyComp(String path) throws Exception {
		List<Experiment> exps = new ArrayList<>();
		
		exps.add(new Experiment(path,"Proteome/XTandem/FILTER-LPM-FDRn"));
		exps.add(new Experiment(path,"Proteome/XTandem/LPG1-LPGN-FDRr"));
		
		run(exps, String.format("%s/Proteome/Studies/Pandey/genes.tsv",path));
	}
	
	public static void runExp( String path, String engine, String tissue ) throws Exception {
		List<Experiment> exps = new ArrayList<>();
		
		exps.add(new Experiment(path,String.format("%s/%s/LPM-FDRn",tissue,engine)));
		exps.add(new Experiment(path,String.format("%s/%s/LPM-FDRm",tissue,engine),false));
		exps.add(new Experiment(path,String.format("%s/%s/LPF-FDRn",tissue,engine)));
		exps.add(new Experiment(path,String.format("%s/%s/LPM-FDRp",tissue,engine)));
		exps.add(new Experiment(path,String.format("%s/%s/LPG1-FDRr",tissue,engine)));
		exps.add(new Experiment(path,String.format("%s/%s/LPGN-FDRr",tissue,engine),null,true,true));
		exps.add(new Experiment(path,String.format("%s/%s/LPG-FDRr",tissue,engine)));
		exps.add(new Experiment(path,String.format("%s/%s/LPGB-FDRr",tissue,engine)));
		
		run(exps, String.format("%s/Summary/%s-%s.tsv.gz",path,tissue,engine));
	}
	
	public static void runProteome( String path, String engine ) throws Exception {
		List<Experiment> exps = new ArrayList<>();
				
		exps.add(new Experiment(path,String.format("Proteome/%s/FILTER-LPM-FDRn",engine)));
		exps.add(new Experiment(path,String.format("All_Tissues/%s/LPM-FDRn",engine)));
		exps.add(new Experiment(path,String.format("All_Tissues/%s/LPM-FDRm",engine)));
		exps.add(new Experiment(path,String.format("All_Tissues/%s/LPM-FDRp",engine)));
		exps.add(new Experiment(path,String.format("All_Tissues/%s/LPF-FDRn",engine)));
		exps.add(new Experiment(path,String.format("All_Tissues/%s/LPGN-FDRr",engine)));
		//exps.add(new Experiment(path,String.format("All_Tissues/%s/LPGN-FDRr",engine),null,true,true));
		exps.add(new Experiment(path,String.format("Proteome/%s/LPG1-LPG-FDRr",engine)));
		exps.add(new Experiment(path,String.format("Proteome/%s/LPG1-LPG1-FDRr",engine)));
		exps.add(new Experiment(path,String.format("Proteome/%s/LPG1-LPGN-FDRr",engine)));
		exps.add(new Experiment(path,String.format("Proteome/%s/LPG1-LPGB-FDRr",engine)));
		
		run(exps, String.format("%s/Summary/Proteome-%s.tsv.gz",path,engine));
	}
	
	public static void run( List<Experiment> exps, String outputPath ) throws Exception {
		Collection<Gene> summaryTarget = createSummary(false, exps);
		Collection<Gene> summaryDecoy = createSummary(true, exps);
		List<Gene> summary = new ArrayList<>();
		summary.addAll(summaryTarget);
		summary.addAll(summaryDecoy);
		printSummary(summary, exps, outputPath);
		
		LOG.info("finished!");
	}
	
	private static Collection<Gene> createSummary(boolean decoy, List<Experiment> exps) throws Exception {
		Map<String, Gene> map = new HashMap<>();		
		for( Experiment exp : exps )
			loadDetails(map, decoy, exp);
		if( !decoy )
			loadFastaInfo(map, FASTA);
		for( Gene gene : map.values() )
			gene.decoy = decoy;		
		return map.values();
	}

	private static void loadDetails(Map<String, Gene> map, boolean decoy, Experiment exp) throws IOException, ParseException {
		ScoreFile<ScoreItem> genScores = ScoreFile.load(new File(exp.dir,decoy?"FdrGenDecoy.tsv.gz":"FdrGenTarget.tsv.gz").getAbsolutePath());
		Map<String,ScoreItem> pepScores = null;
		LinkMap<Link<Void, Void>, Link<Void, Void>> pep2gen = null;		
		if( exp.showPeptides || exp.showPeptideCount ) {
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
			if( !exp.showPeptides && !exp.showPeptideCount )
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

	private static void loadFastaInfo(Map<String, Gene> map, String fastaPath ) throws IOException, InvalidSequenceException {
		List<Fasta> fastas = Fasta.readEntries(fastaPath, SequenceType.PROTEIN);
		for( Fasta fasta : fastas ) {
			String acc = fasta.getGeneAccession();
			Gene gene = map.get(acc);
			if( gene == null )
				continue;
			gene.name = fasta.getGeneName();
		}
	}

	private static void printSummary(Collection<Gene> summary, List<Experiment> exps, String output) throws IOException {
		PrintWriter pw = new PrintWriter(Streams.getTextWriter(output));
		pw.print(CsvUtils.getCsv(SEP, "gene", "name", "T/D"));	
		for( Experiment exp : exps ) {
			pw.print(SEP);
			if( exp.showScore ) {
				pw.print(String.format("score (%s)", exp.name));
				pw.print(SEP);
			}
			//pw.print(CsvUtils.getCsv(SEP,String.format("fdr (%s)", exp.name),String.format("q-value (%s)", exp.name)));
			pw.print(CsvUtils.getCsv(SEP,String.format("q-value (%s)", exp.name)));
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
			} else if ( exp.showPeptideCount )
				pw.print(String.format("%s#peps (%s)%s#peps < %.2f FDR (%s)", SEP, exp.name, SEP, FDR, exp.name));
		}
		pw.println();
		for( Gene gene : summary ) {
			pw.print(CsvUtils.getCsv(SEP, gene.acc, gene.name, gene.decoy?"D":"T"));
			for( Experiment exp : exps ) {
				Details details = gene.exps.get(exp.name);				
				pw.print(SEP);				
				if( exp.showScore ) {
					pw.print(details==null?"":details.score);
					pw.print(SEP);
				}
				//pw.print(CsvUtils.getCsv(SEP, details==null?"":details.fdr, details==null?"":details.qValue));
				pw.print(CsvUtils.getCsv(SEP, details==null?"":details.qValue));
				if( exp.showPeptides ) {
					pw.print(SEP);
					printPeptides(pw, details==null?null:details.peptides);
					pw.print(SEP);
					printPeptides(pw, details==null?null:fdrFilter(details.peptides,FDR));
				} else if ( exp.showPeptideCount ) {
					if( details == null )
						pw.print(String.format("%s%s", SEP, SEP));
					else {
						int nFdr = 0;
						for ( Peptide peptide : details.peptides )
							if ( peptide.qValue < FDR )
								nFdr++;
						pw.print(String.format("%s%d%s%d", SEP, details.peptides.size(), SEP, nFdr));
					}
				}
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
	private static final String FASTA = "/media/gorka/EhuBio/Fasta/gencode25.target.IL.gorka.fasta";
	private static final String SEP = "\t";
	private static final String SEP2 = ",";
	private static final double FDR = 0.01;
	//private static final String PREFIX = "decoy-";
}