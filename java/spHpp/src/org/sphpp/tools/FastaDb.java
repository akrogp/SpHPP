package org.sphpp.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.sphpp.nextprot.chrreport.Entry;
import org.sphpp.nextprot.chrreport.TxtReader;
import org.sphpp.uniprot.DatabaseType;
import org.sphpp.uniprot.Mapping;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.db.uniprot.UniProtUtils;
import es.ehubio.io.CsvUtils;
import es.ehubio.tools.Command.Interface;

public class FastaDb implements Interface {
	Logger logger = Logger.getLogger(FastaDb.class.getName());
	
	@Override
	public String getUsage() {
		return "</path/to/UniProt> </path/to/neXtProt>";
	}

	@Override
	public int getMinArgs() {
		return 2;
	}

	@Override
	public int getMaxArgs() {
		return 2;
	}

	@Override
	public void run(String[] args) throws Exception {
		// Parse args
		File uniprotFile = new File(args[0],"HUMAN.fasta.gz");
		File uniprotVersionFile = new File(args[0],"version.txt");
		File mappingFile = new File(args[0],"HUMAN_9606_idmapping.dat.gz");
		File nextprotDir = new File(args[1]);		
		checkArgs(uniprotFile, uniprotVersionFile, mappingFile, nextprotDir);
		
		// Load databases
		List<Fasta> swissprot = loadSwissProt(uniprotFile);
		BufferedReader rd = new BufferedReader(new FileReader(uniprotVersionFile.getAbsoluteFile()));
		String uniprotVersion = rd.readLine();
		rd.close();
		Map<String,List<String>> up2ens = loadEnsgs(mappingFile);
		Map<String,Entry> up2chr = loadChrs(nextprotDir);
		String nextprotVersion = up2chr.values().iterator().next().getRelease();
		
		// Generate SpHpp DB
		saveSpHppFasta(swissprot, up2ens, up2chr, uniprotVersion, nextprotVersion);
		logger.info("Finished!!");
	}
	
	private void checkArgs( File fastaFile, File uniprotVersionFile, File mappingFile, File nextprotDir ) throws Exception {
		if( !fastaFile.exists() || !fastaFile.isFile() )
			throw new Exception(String.format("Fasta file %s not found!", fastaFile.getAbsolutePath()));
		if( !uniprotVersionFile.exists() || !uniprotVersionFile.isFile() )
			throw new Exception(String.format("Version file %s not found!", uniprotVersionFile.getAbsolutePath()));
		if( !mappingFile.exists() || !mappingFile.isFile() )
			throw new Exception(String.format("Mapping file %s not found!", mappingFile.getAbsolutePath()));
		if( !nextprotDir.exists() || !nextprotDir.isDirectory() )
			throw new Exception(String.format("neXtProt directory %s not found!", nextprotDir.getAbsolutePath()));
	}
	
	private List<Fasta> loadSwissProt( File fastaFile ) throws IOException, InvalidSequenceException {
		logger.info(String.format("Loading %s ...", fastaFile.getAbsolutePath()));
		List<Fasta> uniprot = Fasta.readEntries(fastaFile.getAbsolutePath(), SequenceType.PROTEIN);
		List<Fasta> swissprot = new ArrayList<>();
		for( Fasta fasta : uniprot )
			if( fasta.getEntry().startsWith("sp") )
				swissprot.add(fasta);
		return swissprot;
	}
	
	private Map<String,List<String>> loadEnsgs( File mappingFile ) throws FileNotFoundException, IOException {
		logger.info(String.format("Loading %s ...", mappingFile.getAbsolutePath()));
		return Mapping.loadCurrentDatGz(mappingFile.getAbsolutePath(), DatabaseType.EnsemblGene);
	}
	
	private Map<String,Entry> loadChrs( File nextprotDir ) throws IOException {
		logger.info(String.format("Loading %s/nextprot_chromosome_*.txt ...", nextprotDir.getAbsolutePath()));
		List<Entry> nxchr = TxtReader.readDirectory(nextprotDir.getAbsolutePath());
		Map<String,Entry> up2chr = new HashMap<>();
		for( Entry entry : nxchr )
			up2chr.put(UniProtUtils.canonicalAccesion(entry.getProtein()), entry);
		return up2chr;
	}
	
	private void saveSpHppFasta( List<Fasta> swissprot, Map<String,List<String>> up2ens, Map<String,Entry> up2chr, String uniprotVersion, String nextprotVersion) throws InvalidSequenceException, FileNotFoundException, IOException {
		File spHppFile = new File("uniprot_sprot_human.fasta");
		logger.info(String.format("Generating %s ...", spHppFile.getAbsolutePath()));
		List<Fasta> spHpp = new ArrayList<>();
		String acc, ensg, chr;
		List<String> ensgs;
		Entry entry;
		long totalCount = 0, chr16Count = 0;
		for( Fasta fasta : swissprot ) {
			acc = UniProtUtils.canonicalAccesion(fasta.getAccession());
			ensgs = up2ens.get(acc);
			ensg = ensgs == null ? "?" : CsvUtils.getCsv(';',ensgs.toArray());
			entry = up2chr.get(acc);
			chr = entry == null ? "?" : entry.getChromosome();
			spHpp.add(new Fasta(
				String.format("%s \\Gene=%s \\Chromosome=%s", fasta.getHeader(), ensg, chr),
				fasta.getSequence(), fasta.getType()));
			totalCount++;
			if( chr.equals("16") )
				chr16Count++;
		}
		Fasta.writeEntries(spHppFile.getAbsolutePath(), spHpp);
		spHpp = null;
		
		File spHppIni = new File("db_metadata.ini");
		logger.info(String.format("Generating %s ...", spHppIni.getAbsolutePath()));
		PrintWriter wr = new PrintWriter(spHppIni);
		wr.println("[nextprot]");
		wr.println(String.format("rel=%s",nextprotVersion));
		wr.println();
		wr.println("[uniprot]");
		wr.println(String.format("rel=%s",uniprotVersion));
		wr.println(String.format("sprot_all=%d",totalCount));
		wr.println(String.format("sprot_chr16=%d",chr16Count));
		wr.close();
	}
}