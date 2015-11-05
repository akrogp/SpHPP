package org.sphpp.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.lang.ArrayUtils;
import org.sphpp.tools.AppArgs.Arg;

import es.ehubio.Strings;
import es.ehubio.db.DbItem;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.io.CsvUtils;
import es.ehubio.io.Streams;
import es.ehubio.model.Aminoacid;
import es.ehubio.proteomics.AmbiguityItem;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Enzyme;
import es.ehubio.proteomics.Gene;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.MsMsData.GroupingLevel;
import es.ehubio.proteomics.DecoyBase;
import es.ehubio.proteomics.MsMsLevel;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.ProteinGroup;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.Spectrum;
import es.ehubio.proteomics.Transcript;
import es.ehubio.proteomics.io.EhubioCsv;
import es.ehubio.proteomics.io.MsMsFile;
import es.ehubio.proteomics.pipeline.ConfigDetector;
import es.ehubio.proteomics.pipeline.ConfigDetector.Modification;
import es.ehubio.proteomics.pipeline.DecoyMatcher;
import es.ehubio.proteomics.pipeline.Digester;
import es.ehubio.proteomics.pipeline.FdrCalculator;
import es.ehubio.proteomics.pipeline.FdrCalculator.FdrResult;
import es.ehubio.proteomics.pipeline.Inferer;
import es.ehubio.proteomics.pipeline.ScoreIntegrator.IterativeResult;
import es.ehubio.proteomics.pipeline.ScoreIntegrator.ModelFitness;
import es.ehubio.proteomics.pipeline.AidedMatcher;
import es.ehubio.proteomics.pipeline.Filter;
import es.ehubio.proteomics.pipeline.PAnalyzer;
import es.ehubio.proteomics.pipeline.RandomMatcher;
import es.ehubio.proteomics.pipeline.ScoreIntegrator;
import es.ehubio.proteomics.pipeline.Searcher;
import es.ehubio.proteomics.pipeline.TrypticMatcher;

@SuppressWarnings("unused")
public class FdrWorkflow {	
	private static class MyFormatter extends Formatter {
		@Override
		public String format(LogRecord record) {
			return String.format(
				"%s %s: %s%s", DATE_FORMAT.format(new Date()), record.getLevel(), record.getMessage(), LINE_SEPARATOR);
		}
		private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		private final static String LINE_SEPARATOR = System.getProperty("line.separator");
	}
	
	public static void main( String[] args ) throws Exception {
		List<Arg> opts = parseArgs(args);
		if( opts == null )
			return;
		FdrWorkflow app = new FdrWorkflow();
		double fdr = configureApp(app, opts);
		app.run(0.01);
		/*for( double th = 0.005; th <= 0.01; th += 0.001 )
			app.run(th);*/
	}
	
	private static List<Arg> parseArgs( String[] args ) {
		AppArgs parser = new AppArgs();
		parser.add(new Arg(true, null, "inTarget", "</path/input/target>", "Path to the target input file or directory with files."));
		parser.add(new Arg(true, null, "inDecoy", "</path/input/decoy>", "Path to the decoy input file or directory with files."));
		parser.add(new Arg(true, null, "dbTarget", "</path/target.fasta>", "Path to the target fasta file."));
		parser.add(new Arg(true, null, "dbDecoy", "</path/decoy.fasta>", "Path to the decoy fasta file."));
		parser.add(new Arg(true, null, "geneMap", "</path/genes.map>", "Path to the gene mapping file."));
		parser.add(new Arg(true, 'o', "output", "</path/output>", "Path to the output directory."));
		parser.add(new Arg(true, 'g', "group", "prot|gene", "Grouping level: protein-level or gene-level."));
		parser.add(new Arg(true, 'f', "fdr", "psm|prot|gene|grp", "FDR level: psm-level, protein-level, gene-level or group-level."));
		parser.add(new Arg(false, 'c', "occam", null, "Enable Occam. Default is disabled."));
		parser.add(new Arg(false, 'v', "value", "<fdr>", "FDR threshold value. Default is 0.01."));
		parser.add(new Arg(false, 'm', "mods", null, "Enable modifications. Default is disabled."));
		List<Arg> opts = parser.parse(args);
		if( opts == null ) {
			System.out.println(String.format("\nUsage:\n\tjava -Xms10g -Xmx10g -cp EhuBio.jar org.sphpp.tools.FdrWorkflow %s\n\nWhere:", parser.getUsage()));
			for( Arg arg : parser.getArgs() )
				System.out.println(String.format("\t%s",arg.toString()));
		}
		return opts;
	}
	
	private static double configureApp( FdrWorkflow app, List<Arg> opts ) {
		int maxMods = 0;
		double fdr = 0.01;
		for( Arg opt : opts ) {
			switch( opt.getName() ) {
				case "inTarget":
					app.targetPath = opt.getParam();
					break;
				case "inDecoy":
					app.decoyPath = opt.getParam();
					break;
				case "dbTarget":
					app.targetFasta = opt.getParam();
					break;
				case "dbDecoy":
					app.decoyFasta = opt.getParam();
					break;
				case "geneMap":
					app.genesPath = opt.getParam();
					break;
				case "output":
					app.outputDir = opt.getParam();
					break;
				case "group":
					app.groupingLevel = opt.getParam().equals("gene") ? GroupingLevel.GENE : GroupingLevel.PROTEIN;
					break;
				case "fdr":
					if( opt.getParam().equals("psm") )
						app.fdrLevel = MsMsLevel.PSM;
					else if( opt.getParam().equals("prot") )
						app.fdrLevel = MsMsLevel.PROTEIN;
					else if( opt.getParam().equals("gene") )
						app.fdrLevel = MsMsLevel.GENE;
					else
						app.fdrLevel = MsMsLevel.AMBIGUITYGROUP;
					break;
				case "occam":
					app.useOccam = true;
					break;
				case "value":
					fdr = Double.parseDouble(opt.getParam());
					break;
				case "mods":
					maxMods = 10;
					break;				
			}
		}
		app.searching = new Searcher.Config(7,-1,maxMods);
		return fdr;
	}

	private void run(double th) throws Exception {
		initLog();
		load();		
		inputFilter();
		ModelFitness fitness = updateScores();
		if( outputDir != null )
			save(new File(outputDir,"all").getAbsolutePath(), fitness);
		String dir = fdrFilter(th);		
		if( outputDir != null && dir != null )
			save(new File(outputDir,dir).getAbsolutePath());
	}		

	private ModelFitness updateScores() throws IOException, InvalidSequenceException {
		showTitle("Scores computation");		
		updatePsmScores();
		if( fdrLevel == MsMsLevel.PSM )
			return null;		
		updatePeptideScores();
		rebuildGroups();
		ModelFitness fitness = setExpectedPeptides();
		updateProteinScores();
		if( fdrLevel == MsMsLevel.PROTEIN )
			return fitness;
		if( genesPath != null ) {
			updateGroupScores("transcript", data.getTranscripts());
			if( fdrLevel == MsMsLevel.TRANSCRIPT )
				return fitness;		
			updateGroupScores("gene", data.getGenes());
			if( fdrLevel == MsMsLevel.GENE )
				return fitness;		
		}
		updateGroupScores("group", data.getGroups());
		return fitness;
	}
	
	private String fdrFilter(double fdr) {
		/*if( fdrLevel == MsMsLevel.AMBIGUITYGROUP ) {
			showTitle(String.format("%s group FDR=%s filter",data.getAmbiguityLevel().getName(),fdr));
			grpFilter(new Score(ScoreType.GROUP_Q_VALUE, fdr));
			return "groupFdr";
		}*/				
		showTitle(String.format("%s FDR=%s filter",fdrLevel.getName(),fdr));		
		Filter filter = new Filter(data);
		String dir = null;
		if( fdrLevel == MsMsLevel.PSM ) {
			dir = "psmFdr";
			filter.setPsmScoreThreshold(new Score(ScoreType.PSM_Q_VALUE, fdr));
		} else if( fdrLevel == MsMsLevel.PROTEIN ) {
			dir = "protFdr";
			double th = getBadPsmThreshold(fdr);
			filter.setPsmScoreThreshold(new Score(psmScoreType, th));
			logger.info(String.format("Using %s threshold = %s", psmScoreType.getName(), th));
			//filter.setProteinScoreThreshold(new Score(ScoreType.PROTEIN_Q_VALUE, fdr));
		} else if ( fdrLevel == MsMsLevel.TRANSCRIPT ) {
			dir = "transFdr";
			filter.setTranscriptScoreThreshold(new Score(ScoreType.GROUP_Q_VALUE, fdr));
		} else if( fdrLevel == MsMsLevel.GENE ) {
			dir = "geneFdr";
			filter.setGeneScoreThreshold(new Score(ScoreType.GROUP_Q_VALUE, fdr));
		} else if( fdrLevel == MsMsLevel.AMBIGUITYGROUP ) {
			dir = "groupFdr";
			filter.setGroupScoreThreshold(new Score(ScoreType.GROUP_Q_VALUE, fdr));
		} else
			return null;
		filter.run();
		//rebuildGroups();
		showCounts();
		showFdrs();
		return dir;
	}

	private void initLog() throws SecurityException, IOException {
		if( outputDir == null )
			return;
		outputDir = String.format("%s-%s", outputDir.replaceAll("-.*", ""), new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
		String logPath = new File(outputDir,"log.txt").getAbsolutePath();
		new File(outputDir).mkdirs();
		FileHandler fh = new FileHandler(logPath);
		//fh.setFormatter(new SimpleFormatter());
		fh.setFormatter(new MyFormatter());
		String[] pkgs = {"org.sphpp", "es.ehubio"};
		for( String pkg : pkgs )
			Logger.getLogger(pkg).addHandler(fh);
		logger.info(String.format("Logging messages to %s ...", logPath));
	}

	private void load() throws Exception {
		showTitle("Loading data");
		
		MsMsData decoy = null;
		if( decoyPath != null ) {
			data = MsMsFile.autoLoad(targetPath,false).markTarget();
			decoy = MsMsFile.autoLoad(decoyPath,false).markDecoy();
		} else
			data = MsMsFile.autoLoad(targetPath,false).markDecoys(decoyPrefix);
		
		if( decoy != null && filterSharedDecoys(data, decoy) ) {
			showCounts(data,"target");
			showCounts(decoy,"decoy");
		}
		if( decoy == null && filterSharedDecoys(data) )
			showCounts(data,"total");
				
		clearAminoacids(data);
		if( decoy != null )
			clearAminoacids(decoy);
		
		if( decoy != null && decoyPrefix != null && !decoy.getProteins().iterator().next().getAccession().contains(decoyPrefix) )
			for( Protein protein : decoy.getProteins() )
				protein.setAccession(decoyPrefix+protein.getAccession());
		
		if( targetFasta != null ) {
			String seq = data.getProteins().iterator().next().getSequence();
			if( seq == null || seq.isEmpty() ) {
				logger.info("Reading protein sequences from fasta files ...");
				data.updateProteinInformation(targetFasta);
				if( decoyFasta != null )
					decoy.updateProteinInformation(decoyFasta);
			}
		}
		
		logger.info("Checking digestion parameters ...");
		updateDigestionParameters();
		
		logger.info("Checking search parameters ...");
		updateSearchParameters();
		
		if( dontTrustRelations ) {
			infer("Target", data, targetFasta);
			infer("Decoy", decoy, decoyFasta);
		}
		
		if( decoy != null ) {
			logger.info("Merging target and decoy data for competition ...");
			data.merge(decoy);
			data.updateRanks(psmScoreType);
			showCounts(data, "total");
		}
		
		data.setAmbiguityLevel(groupingLevel);
		
		if( genesPath != null ) {
			logger.info("Loading genomic information ...");
			loadGenes();
		}
				
		//rebuildGroups();
	}
	
	private void loadGenes() throws Exception {		
		Map<String, Transcript> mapTranscript = new HashMap<>();
		Map<String, Gene> mapGene = new HashMap<>();
		Map<String, Transcript> mapP2T = new HashMap<>();
		Map<String, Gene> mapT2G = new HashMap<>();
		Transcript transcript;
		Gene gene;
		BufferedReader br = new BufferedReader(Streams.getTextReader(genesPath));
		br.readLine(); // header
		String line, accGene, accTranscipt, accProtein, name, description;
		String[] fields;
		while( (line=br.readLine()) != null ) {
			fields = line.split("\\t");
			accGene = fields[0];
			accTranscipt = fields[1];
			accProtein = fields[2];
			name = fields.length > 3 ? fields[3] : null;
			description = fields.length > 4 ? fields[4] : null;
			if( accProtein.isEmpty() )
				continue;
			addItem(Transcript.class, mapTranscript, accTranscipt, name, description, accProtein, mapP2T);
			addItem(Gene.class, mapGene, accGene, name, description, accTranscipt, mapT2G);
		}
		br.close();
		setGenes(mapP2T, mapT2G, mapTranscript, mapGene);		
	}
	
	private void setGenes(Map<String, Transcript> mapP2T, Map<String, Gene> mapT2G, Map<String, Transcript> mapTranscript, Map<String, Gene> mapGene) {
		int unkTranscripts = 0, unkGenes = 0;
		Transcript transcript;
		Gene gene;
		for( Protein protein : data.getProteins() ) {
			transcript = mapP2T.get(protein.getAccession());
			if( transcript == null ) {
				transcript = new Transcript();
				transcript.setAccession(protein.getAccession()+"-unknown-transcript");
				unkTranscripts++;
			}
			gene = mapT2G.get(transcript.getAccession());
			if( gene == null ) {
				gene = new Gene();
				gene.setAccession(protein.getAccession()+"-unknown-gene");
				unkGenes++;
			}
			transcript.linkProtein(protein);
			gene.linkTranscript(transcript);
		}
		if( unkTranscripts != 0 )
			logger.warning(String.format("Transcripts not found for %d proteins", unkTranscripts));
		if( unkGenes != 0 )
			logger.warning(String.format("Genes not found for %d proteins", unkGenes));
		data.loadFromSpectra(data.getSpectra());
	}	

	private <T extends DbItem> void addItem(
		Class<T> cls,
		Map<String, T> map, String acc, String name, String desc,
		String subAcc, Map<String, T> map2
		) throws Exception {
		
		T target = map.get(acc);
		if( target == null ) {
			target = cls.newInstance();
			target.setAccession(acc);
			target.setName(name);
			target.setDescription(desc);
			map.put(acc, target);
		}		
		map2.put(subAcc, target);

		String decoyAcc = decoyPrefix+acc;
		T decoy = map.get(decoyAcc);
		if( decoy == null ) {
			decoy = cls.newInstance();
			decoy.setAccession(decoyAcc);
			map.put(decoyAcc, decoy);
		}		
		map2.put(decoyPrefix+subAcc, decoy);
	}

	private void clearAminoacids(MsMsData data) {
		int count = 0;
		for( Peptide peptide : data.getPeptides() )
			if( peptide.getSequence().toLowerCase().matches(".*[bjzx].*") ) {
				count++;
				peptide.setSequence("");
			}
		if( count != 0 )
			logger.warning(String.format("Ignored %d peptides with [bjzx] ...", count));
	}

	private void infer(String title, MsMsData data, String fastaPath) throws IOException, InvalidSequenceException {
		logger.info(String.format("Updating peptide-protein relations in %s MS/MS Data ...",title.toLowerCase()));
		long count1 = data.getRedundantPeptidesCount();
		Inferer.relink(data,fastaPath,digestion,searching);		
		long count2 = data.getRedundantPeptidesCount();
		if( count2 != count1 )
			logger.warning(String.format("%s peptide relations %d->%s", title, count1, count2));
		else
			logger.info(String.format("%s peptide relations were OK", title));
	}
	
	private void updateDigestionParameters() throws Exception {
		if( digestion == null && data.getProteins().iterator().next().getSequence() == null ) {
			logger.info("No digestion information provided");
			return;
		}
		if( digestion == null && (digestion=detector.getDigestion(data)) == null )
			throw new Exception("Could not detect digestion parameters");
		logger.info(String.format("Enzyme: %s", digestion.getEnzyme().getDescription()));
		logger.info(String.format("Missed cleavages: %d", digestion.getMissedCleavages()));
		logger.info(String.format("Adventitious cleavage at Asp-Pro : %s", digestion.isUsingDP()));
		logger.info(String.format("N-terminal cut of MX: %d", digestion.getCutNterm()));
	}

	private void updateSearchParameters() throws Exception {
		int minPeptideLength = -1, maxPeptideLength = -1;
		if( searching != null ) {
			minPeptideLength = searching.getMinLength();
			maxPeptideLength = searching.getMaxLength();
		}
		if( minPeptideLength < 0 )
			minPeptideLength = detector.getMinPeptideLength(data);
		if( maxPeptideLength < 0 )
			maxPeptideLength = detector.getMaxPeptideLength(data);
		logger.info(String.format("PeptideLength: min=%d, max=%d", minPeptideLength, maxPeptideLength));				
		
		List<Aminoacid> varMods = searching != null ? searching.getVarMods() : new ArrayList<Aminoacid>();
		if( varMods.isEmpty() && (searching == null || searching.getMaxMods() != 0) ) {
			List<Modification> mods = detector.getMods(data);
			varMods = new ArrayList<>();
			for( Modification mod : mods )
				if( !mod.isFixed() )
					varMods.add(mod.getAa()); 
		}
		int maxMods = 0;
		if( varMods.isEmpty() )
			logger.info("No variable modification considered");
		else {
			logger.info(String.format("Variable modifications in: %s", CsvUtils.getCsv(',', varMods.toArray())));
			maxMods = -1;
			if( searching != null )
				maxMods = searching.getMaxMods();
			if( maxMods < 0 )
				maxMods = detector.getMaxModsPerPeptide(data, varMods);
			logger.info(String.format("Maximum number of modifications per peptide: %d", maxMods));
		}
		
		searching = new Searcher.Config(minPeptideLength, maxPeptideLength, maxMods, varMods);
	}	

	
	private void save(String path) {
		save(path, null);
	}
	
	private void save(String path, ModelFitness fitness) {
		//logger.info("Saving ...");
		EhubioCsv csv = new EhubioCsv(data);
		csv.setPsmScoreType(psmScoreType);
		try {
			new File(path).mkdirs();
			csv.save(path);
			if( fitness != null )
				EhubioCsv.saveModel(data, path, fitness);
			//logger.info("Saved!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}		
	}
	
	private void inputFilter() {
		showTitle("Input filter");
		Filter filter = new Filter(data);
		filter.setRankTreshold(1);
		filter.setMinPeptideLength(searching.getMinLength());
		filter.setMaxPeptideLength(searching.getMaxLength());
		filter.setOnlyBestPsmPerPrecursor(psmScoreType);
		//filter.setOnlyBestPsmPerPeptide(psmScoreType);		
		//filter.setUniquePeptides(true);
		filter.run();
		//rebuildGroups();
		showCounts();
	}
	
	private static boolean filterSharedDecoys( MsMsData target, MsMsData decoy ) {
		Set<String> decoySeqs = new HashSet<>();
		Set<String> sharedSeqs = new HashSet<>();		
		for( Peptide peptide : decoy.getPeptides() )
			decoySeqs.add(peptide.getSequence());
		int targetCount = 0;
		for( Peptide peptide : target.getPeptides() )
			if( decoySeqs.contains(peptide.getSequence()) ) {
				//logger.warning(String.format("Peptide %s is present in both target and decoy! Removing ...", peptide.getSequence()));
				sharedSeqs.add(peptide.getSequence());
				peptide.setSequence("");
				targetCount++;
			}
		int decoyCount = 0;
		for( Peptide peptide : decoy.getPeptides() )
			if( sharedSeqs.contains(peptide.getSequence()) ) {
				peptide.setSequence("");
				decoyCount++;
			}
		if( sharedSeqs.isEmpty() )
			return false;
		
		Filter filter = new Filter(target);
		filter.setMinPeptideLength(1);
		filter.run();
		filter = new Filter(decoy);
		filter.setMinPeptideLength(1);
		filter.run();
		logger.warning(String.format(
			"Filtered %d peptide sequences present in both %d target and %d decoy peptides",
			sharedSeqs.size(), targetCount, decoyCount));
		return true;
	}
	
	private boolean filterSharedDecoys( MsMsData data ) {
		int count = 0;
		for( Protein protein : data.getProteins() ) {
			if( protein.isTarget() || protein.getAccession().contains(decoyPrefix) )
				continue;
			for( Peptide peptide : protein.getPeptides() )
				if( peptide.isDecoy() ) {
					peptide.setSequence("");
					count++;
				}
		}
		if( count == 0 )
			return false;
		Filter filter = new Filter(data);
		filter.setMinPeptideLength(1);
		filter.run();
		logger.warning(String.format("Filtered %d peptide sequences present in both target and decoy", count));
		return true;
	}
	
	private void grpFilter( Score score ) {
		PAnalyzer pa = new PAnalyzer(data);		
		PAnalyzer.Counts curCount = pa.getCounts(), prevCount;
		Filter filter = new Filter(data);
		filter.setGroupScoreThreshold(score);
				
		int i = 0;		
		do {
			i++;			
			logger.info(String.format("Iteration %s", i));
			filter.run();
			rebuildGroups();
			prevCount = curCount;
			curCount = pa.getCounts();
			updateGroupScores("group", data.getGroups());
			showFdrs();
		} while( !curCount.equals(prevCount) && i < 15 );
		if( i >= 15 )
			logger.warning("Maximum number of iterations reached!");
	}
	
	private void updatePsmScores() {
		logger.info("Calculating PSM scores ...");
		fdrCalc.updatePsmScores(data.getPsms(), psmScoreType, true);
		ScoreIntegrator.updatePsmScores(data.getPsms());
	}
	
	private void updatePeptideScores() {
		logger.info("Calculating peptide scores ...");
		ScoreIntegrator.psmToPeptide(data.getPeptides());
		fdrCalc.updatePeptideScores(data.getPeptides(), ScoreType.LPP_SCORE, false);
	}
	
	private ModelFitness setExpectedPeptides() throws IOException, InvalidSequenceException {
		logger.info("Calculating expected number of peptides ...");
				
		ScoreIntegrator.peptideToProteinEquitative(data.getProteins());		
				
		// Random matching of peptides to proteins: decoy prefix vs tryptic
		//RandomMatcher matcher = new DecoyMatcher(data.getProteins(), decoyPrefix);
		RandomMatcher matcher = new TrypticMatcher(targetFasta, decoyFasta, decoyPrefix, digestion, searching);
		//RandomMatcher matcher = new TrypticMatcher(data.getProteins(), digestion, searching);
		//RandomMatcher matcher = new AidedMatcher(targetPath.replaceAll("\\..*",".rel.gz"),decoyPath.replaceAll("\\..*",".rel.gz"), searching);
		
		ModelFitness fitness = ScoreIntegrator.setExpectedValues(data.getProteins(), matcher);		
		return fitness;
	}
	
	private void updateProteinScores() throws IOException, InvalidSequenceException {
		logger.info("Calculating protein scores ...");
				
		if( useOccam ) {
			//ScoreIntegrator.peptideToProteinIterative(data.getProteins(),0.1,30);
			ScoreIntegrator.peptideToProteinIterative(data.getProteins(),0.1,300);
		}
		
		// Include random matching in protein score: divide vs model
		//ScoreIntegrator.divideRandom(data.getProteins(), false);
		//ScoreIntegrator.divideRandom(data.getProteins(), true);
		//ScoreIntegrator.modelRandom(data.getProteins(), false);
		ScoreIntegrator.modelRandomProteins(data.getProteins(), true, false);
		//ScoreIntegrator.modelRandomAprox(data.getProteins(), true);
		
		fdrCalc.updateProteinScores(data.getProteins(), ScoreType.LPQCORR_SCORE, false);	
	}
	
	private void updateBadProteinScores() {
		logger.info("Using best PSM as protein score ...");
		for( Protein protein : data.getProteins() )
			protein.setScore(protein.getBestPsm(psmScoreType).getScoreByType(psmScoreType));
		fdrCalc.updateProteinScores(data.getProteins(), psmScoreType, false);
	}
	
	private void updateGroupScores(String name, Collection<? extends ProteinGroup> groups) {
		if( groups.isEmpty() )
			return;
		logger.info(String.format("Calculating %s scores ...",name));
		ScoreIntegrator.proteinToGroup(groups);
		ScoreIntegrator.modelRandomGroups(groups, false);
		fdrCalc.updateGroupScores(groups, ScoreType.LPGCORR_SCORE, false);
	}
		
	private void rebuildGroups() {
		logger.info(String.format("Updating %s groups ...",groupingLevel.getName()));		
		PAnalyzer pAnalyzer = new PAnalyzer(data);
		pAnalyzer.run();
		showCounts();
	}
	
	private void showTitle( String title ) {
		logger.info(String.format("----- %s -----", Strings.capitalizeFirst(title)));
	}
	
	private void showCounts(MsMsData data, String title) {
		if( !data.getGroups().isEmpty() ) {
			PAnalyzer pAnalyzer = new PAnalyzer(data);
			logger.info(String.format("PAnalyzer (target): %s", pAnalyzer.getTargetCounts().toString()));
			logger.info(String.format("PAnalyzer (decoy): %s", pAnalyzer.getDecoyCounts().toString()));
		}
		logger.info(String.format("Data (%s): %s", title, data.toString()));
	}
	
	private void showCounts() {
		if( !data.getGroups().isEmpty() ) {
			PAnalyzer pAnalyzer = new PAnalyzer(data);
			logger.info(String.format("PAnalyzer (target): %s", pAnalyzer.getTargetCounts().toString()));
			logger.info(String.format("PAnalyzer (decoy): %s", pAnalyzer.getDecoyCounts().toString()));
		}
		logger.info(String.format("Data (target): %s", data.toTargetString()));
		logger.info(String.format("Data (decoy): %s", data.toDecoyString()));
	}
	
	private void showFdrs() {
		double psm = fdrCalc.getFdr(data.getPsms()).getRatio();
		double pep = fdrCalc.getFdr(data.getPeptides()).getRatio();
		double prot = fdrCalc.getFdr(data.getProteins()).getRatio();
		double grp = fdrCalc.getFdr(data.getGroups()).getRatio();
		StringBuilder sb = new StringBuilder(String.format("FDRs: PSM=%s, Peptide=%s, Protein=%s, Group=%s", psm, pep, prot, grp));
		if( !data.getTranscripts().isEmpty() ) {
			double tra = fdrCalc.getFdr(data.getTranscripts()).getRatio();
			sb.append(String.format(", Transcript=%s", tra));
		}
		if( !data.getGenes().isEmpty() ) {
			double gen = fdrCalc.getFdr(data.getGenes()).getRatio();
			sb.append(String.format(", Gene=%s", gen));
		}		
		logger.info(sb.toString());
	}	

	private double getBadPsmThreshold( double protFdrTh ) {
		Set<String> targetProteins = new HashSet<>();
		Set<String> decoyProteins = new HashSet<>();
		List<Psm> list = new ArrayList<>(data.getPsms());
		Collections.sort(list, new Comparator<Decoyable>() {
			@Override
			public int compare(Decoyable o1, Decoyable o2) {
				return o2.getScoreByType(psmScoreType).compare(o1.getScoreByType(psmScoreType).getValue());
			}
		});
		double result = 0;
		for( Psm psm : list ) {
			for( Protein protein : psm.getPeptide().getProteins() )
				if( protein.getDecoy().equals(true) )
					decoyProteins.add(protein.getAccession());
				else
					targetProteins.add(protein.getAccession());			
			if( fdrCalc.getFdr(decoyProteins.size(),targetProteins.size()) > protFdrTh )
				break;
			result = psm.getScoreByType(psmScoreType).getValue();
		}
		return result;
	}
	
	private void savePsmsPerPeptide() throws FileNotFoundException {
		PrintWriter pw = new PrintWriter("/home/gorka/psmsPerPeptide.csv");
		for( Peptide peptide : data.getPeptides() )
			pw.println(String.format("%d\t%s", peptide.getPsms().size(), peptide.isDecoy()));
		pw.close();
	}
	
	private void targetVsdecoy() throws IOException {
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("/home/gorka/scans.csv")));
		for( Spectrum spectrum : data.getSpectra() ) {
			Score bestTarget = null, bestDecoy = null;
			for( Psm psm : spectrum.getPsms() ) {
				Score newScore = psm.getScoreByType(psmScoreType);
				if( psm.isTarget() ) {
					if( bestTarget == null || newScore.compare(bestTarget.getValue()) > 0 )
						bestTarget = psm.getScoreByType(psmScoreType);					
				} else {
					if( bestDecoy == null || newScore.compare(bestDecoy.getValue()) > 0 )
						bestDecoy = psm.getScoreByType(psmScoreType);
				}
			}
			if( bestTarget != null && bestDecoy != null )
				pw.println(String.format("%s\t%f\t%f", spectrum.getScan(), bestTarget.getValue(), bestDecoy.getValue()));
		}
		pw.close();
	}
	
	//private final MsMsLevel fdrLevel = MsMsLevel.AMBIGUITYGROUP;
	//private final MsMsLevel fdrLevel = MsMsLevel.GENE;
	//private final MsMsLevel fdrLevel = MsMsLevel.PROTEIN;
	//private final GroupingLevel groupingLevel = GroupingLevel.PROTEIN;
	//private final GroupingLevel groupingLevel = GroupingLevel.GENE;
	private final ConfigDetector detector = new ConfigDetector(1000);
	private Digester.Config digestion = null;
	//private Searcher.Config searching = new Searcher.Config(-1,-1,0);
	//private Searcher.Config searching = new Searcher.Config(-1,-1,-1);
	
	private final static Logger logger = Logger.getLogger(FdrWorkflow.class.getName());
	private final FdrCalculator fdrCalc = new FdrCalculator(false);
	private MsMsData data;		
	
	// For CLI arguments
	private MsMsLevel fdrLevel;
	private GroupingLevel groupingLevel;
	private String outputDir;
	private String targetPath, decoyPath;
	private String targetFasta, decoyFasta;	
	private final ScoreType psmScoreType = ScoreType.SEQUEST_XCORR;
	private final String decoyPrefix = "decoy-";
	private Searcher.Config searching;
	private final boolean dontTrustRelations = true;
	private String genesPath;
	private boolean useOccam = false;
	
	/*private final static String targetFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/SPHPP_UPV_CCD18/SP_HUMAN.fasta.gz";
	private final static String decoyFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/SPHPP_UPV_CCD18/SP_HUMAN_DECOY.fasta.gz";
	private final static String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/SPHPP_UPV_CCD18/SPHPP_UPV_CCD18_QE_GEL_R1_10_15_target.mzid.gz";
	private final static String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/SPHPP_UPV_CCD18/SPHPP_UPV_CCD18_QE_GEL_R1_10_15_decoy.mzid.gz";
	private final static ScoreType psmScoreType = ScoreType.XTANDEM_EVALUE;
	private final static String decoyPrefix = "decoy-";*/
	/*private Enzyme enzyme = Enzyme.TRYPSIN;
	private int missedCleavages = 1;
	private Aminoacid[] varMods = {Aminoacid.METHIONINE};*/
	
	/*private final static String targetFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/Gencode20cds_TD_Jul14_TARGET.J.fasta.gz";
	private final static String decoyFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/Gencode20cds_TD_Jul14_DECOY.J.fasta.gz";
	private final static String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/Adult_Frontalcortex/Adult_Frontalcortex_bRP_Elite_85.Target_TargetPeptideSpectrumMatch.txt.gz";
	private final static String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/Adult_Frontalcortex/Adult_Frontalcortex_bRP_Elite_85.Decoy_TargetPeptideSpectrumMatch.txt.gz";
	private final static ScoreType psmScoreType = ScoreType.SEQUEST_XCORR;
	private final static String decoyPrefix = "XXX_";*/
	/*private Enzyme enzyme = Enzyme.TRYPSINP;
	private int missedCleavages = 2;
	private Aminoacid[] varMods = {Aminoacid.METHIONINE};*/
	
	/*private final static String targetFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey2/Gencode20cds.Target.Decoy/Gencode20cds_TD_Jul14_TARGET.fasta.gz";
	private final static String decoyFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey2/Gencode20cds.Target.Decoy/Gencode20cds_TD_Jul14_DECOY_goodHeader.fasta.gz";
	private final static String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey2/Frontalcortex_FetalHeart_squest_results/Adult_Frontalcortex_Target_All_TargetPeptideSpectrumMatch.txt.gz";
	private final static String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey2/Frontalcortex_FetalHeart_squest_results/Adult_Frontalcortex_Decoy_All_TargetPeptideSpectrumMatch.txt.gz";
	private final static ScoreType psmScoreType = ScoreType.SEQUEST_XCORR;
	private final static String decoyPrefix = "decoy-";*/
	/*private Enzyme enzyme = Enzyme.TRYPSINP;
	private int missedCleavages = 2;
	private Aminoacid[] varMods = {Aminoacid.METHIONINE};*/
	
	/*private String outputDir = "/home/gorka/Descargas/Temp/Adult_Frontalcortex_bRP_Elite_85";
	private final String targetFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey2/Gencode20cds.Target.Decoy/Gencode20cds_TD_Jul14_TARGET.fasta.gz";
	private final String decoyFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey2/Gencode20cds.Target.Decoy/Gencode20cds_TD_Jul14_DECOY_goodHeader.fasta.gz";
	private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey3/Adult_Frontalcortex_bRP_Elite_85.target.PeptideSpectrumMatch.txt.gz";
	private final String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey3/Adult_Frontalcortex_bRP_Elite_85.decoy.PeptideSpectrumMatch.txt.gz";
	private final ScoreType psmScoreType = ScoreType.SEQUEST_XCORR;
	private final String decoyPrefix = "decoy-";*/
	/*private Enzyme enzyme = Enzyme.TRYPSINP;
	private int missedCleavages = 2;
	private Aminoacid[] varMods = {Aminoacid.METHIONINE};
	private int maxMods = 4;*/
	
	/*private final String targetFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey2/Gencode20cds.Target.Decoy/Gencode20cds_TD_Jul14_TARGET.fasta.gz";
	private final String decoyFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey2/Gencode20cds.Target.Decoy/Gencode20cds_TD_Jul14_DECOY_goodHeader.fasta.gz";
	private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey4/sequestTargetResults/Adult_Frontalcortex_bRP_Elite_85.target.PeptideSpectrumMatch.txt.gz";
	private final String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey4/sequestDecoyResults/Adult_Frontalcortex_bRP_Elite_85.decoy_TargetPeptideSpectrumMatch.txt.gz";
	private final ScoreType psmScoreType = ScoreType.SEQUEST_XCORR;
	private final String decoyPrefix = "decoy-";*/
	
	/*private String outputDir = "/home/gorka/Descargas/Temp/Adult_Frontalcortex_bRP_Elite_AllRaws";
	private final String targetFasta = null;
	private final String decoyFasta = null;
	private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey5/Adult_Frontalcortex_bRP_Elite_Target_AllRaws_PeptideSpectrumMatch.txt.gz";
	private final String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey5/Adult_Frontalcortex_bRP_Elite_Decoy_AllRaws_PeptideSpectrumMatch.txt.gz";
	private final ScoreType psmScoreType = ScoreType.SEQUEST_XCORR;
	private final String decoyPrefix = "decoy-";*/
	
	/*private final static String targetFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/UPS2/search/SP_HUMAN_UPS_TARGET.fasta.gz";
	private final static String decoyFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/UPS2/search/SP_HUMAN_UPS_DECOY.fasta.gz";
	private final static String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/UPS2/search/target.mzid.gz";
	private final static String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/UPS2/search/decoy.mzid.gz";
	private final static ScoreType psmScoreType = ScoreType.XTANDEM_EVALUE;
	private final static String decoyPrefix = "decoy-";*/
	/*private Enzyme enzyme = Enzyme.TRYPSINP;
	private int missedCleavages = 2;
	private Aminoacid[] varMods = {Aminoacid.CYSTEINE, Aminoacid.TRYPTOPHAN, Aminoacid.HISTIDINE, Aminoacid.METHIONINE};*/
	
	/*private final static String targetFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Scerevisiae/sc_SGD_0604_TARGET.fasta.gz";
	private final static String decoyFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Scerevisiae/sc_SGD_0604_DECOY_TRYPSINP.fasta.gz";
	private final static String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Scerevisiae/mzid/Target";
	private final static String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Scerevisiae/mzid/Decoy";
	private final static ScoreType psmScoreType = ScoreType.XTANDEM_EVALUE;
	private final static String decoyPrefix = "decoy-";
	private final static Enzyme enzyme = Enzyme.TRYPSINP;
	private final static int missedCleavages = 2;
	private final static Aminoacid[] varMods = {};*/
	
	/*private String outputDir = "/home/gorka/Descargas/Temp/Adult_Frontalcortex_bRP_Elite_38_f01";
	private final String targetFasta = "/media/data/Sequences/UniProt/current/HUMAN-TARGET-DECOY.fasta.gz";
	private final String decoyFasta = null;
	private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/XTandem/UniProt/Adult_Frontalcortex_bRP_Elite_38_f01.2015_04_14_14_05_22.t.xml.gz";
	//private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/XTandem/UniProt/Adult_Frontalcortex_bRP_Elite_38_f01.mzid.gz";
	private final String decoyPath = null;
	private final ScoreType psmScoreType = ScoreType.XTANDEM_EVALUE;
	private final String decoyPrefix = "decoy-";
	private Searcher.Config searching = new Searcher.Config(7,-1,-1);*/
	
	/*private String outputDir = "/home/gorka/Descargas/Temp/Adult_Frontalcortex_bRP_Elite_38_f01";
	private final String targetFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/ensemblCrap.fasta.gz";
	private final String decoyFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/ensemblCrapDecoy.fasta.gz";
	private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/XTandem/Target/Adult_Frontalcortex_bRP_Elite_38_f01-target.2015_04_16_17_28_58.t.xml.gz";
	private final String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/XTandem/Decoy/Adult_Frontalcortex_bRP_Elite_38_f01-decoy.2015_04_16_17_32_03.t.xml.gz";
	//private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/XTandem/Target/Adult_Frontalcortex_bRP_Elite_38_f01-target.mzid.gz";
	//private final String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/XTandem/Decoy/Adult_Frontalcortex_bRP_Elite_38_f01-decoy.mzid.gz";
	private final ScoreType psmScoreType = ScoreType.XTANDEM_EVALUE;
	private final String decoyPrefix = "decoy-";
	private Searcher.Config searching = new Searcher.Config(7,-1,-1);*/
	
	/*private String outputDir = "/home/gorka/Descargas/Temp/Adult_Frontalcortex";
	private final String targetFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/ensemblCrap.fasta.gz";
	private final String decoyFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/ensemblCrapDecoy.fasta.gz";
	private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/XTandem/Target";
	private final String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/XTandem/Decoy";
	private final ScoreType psmScoreType = ScoreType.XTANDEM_EVALUE;
	private final String decoyPrefix = "decoy-";
	private Searcher.Config searching = new Searcher.Config(7,-1,-1);*/
	
	/*private String outputDir = "/home/gorka/Descargas/Temp/Adult_Frontalcortex";	
	private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/PD/Adult_Frontalcortex/Target";
	private final String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/PD/Adult_Frontalcortex/Decoy";*/
	/*private String outputDir = "/home/gorka/Descargas/Temp/Adult_Heart";
	private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/PD/Adult_Heart/Target";
	private final String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/PD/Adult_Heart/Decoy";*/
	/*private String outputDir = "/home/gorka/Descargas/Temp/Adult_Liver";
	private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/PD/Adult_Liver/Target";
	private final String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/PD/Adult_Liver/Decoy";*/
	/*private String outputDir = "/home/gorka/Descargas/Temp/Adult_Testis";
	private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/PD/Adult_Testis/Target";
	private final String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/PD/Adult_Testis/Decoy";*/
	/*private final String targetFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/ensemblCrap.fasta.gz";
	private final String decoyFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/ensemblCrapDecoy.fasta.gz";	
	private final ScoreType psmScoreType = ScoreType.SEQUEST_XCORR;
	private final String decoyPrefix = "decoy-";
	private Searcher.Config searching = new Searcher.Config(7,-1,0);
	private final boolean dontTrustRelations = true;
	//private final String genesPath = "/media/data/Sequences/Ensembl/current/mart_export.txt.gz";
	private final String genesPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/iakes_mapping.txt.gz";*/
	
	/*private String outputDir = "/home/gorka/Descargas/Temp/Adult_Frontalcortex";
	private final String targetFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/ensemblCrap.fasta.gz";
	private final String decoyFasta = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/ensemblCrapDecoy.fasta.gz";
	private final String targetPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/Mascot/Adult_Frontalcortex/Target";
	private final String decoyPath = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/Mascot/Adult_Frontalcortex/Decoy";
	private final ScoreType psmScoreType = ScoreType.MASCOT_SCORE;
	private final String decoyPrefix = "decoy-";
	private Searcher.Config searching = new Searcher.Config(7,50,0);
	//private Searcher.Config searching = null;
	private final boolean dontTrustRelations = false;*/	
}