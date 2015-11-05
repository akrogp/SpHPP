package org.sphpp.apps.missing.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.sphpp.apps.missing.ShotPeptideEx;
import org.sphpp.apps.missing.ShotProteinEx;
import org.sphpp.nextprot.Mapping;
import org.sphpp.nextprot.chrreport.Entry;
import org.sphpp.nextprot.chrreport.TxtReader;
import org.sphpp.shotgun.ShotDb;
import org.sphpp.shotgun.ShotPeptide;
import org.sphpp.shotgun.ShotProtein;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.InvalidSequenceException;
import es.ehubio.db.uniprot.UniProtUtils;
import es.ehubio.io.CsvUtils;

@ManagedBean
@ApplicationScoped
public class DatabasesBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(DatabasesBean.class.getName()); 
	private static final String nxPath = "/media/data/Sequences/neXtProt/current";
	private static final String nxPeffFile = "nextprot_all.peff.gz";
	private static final String nxMapFile = "nextprot_ensg.txt";
	private static final String spHppPath = "/media/data/Sequences/SpHpp";
	private static final String spHppProteinsFile = "SpHPP_Proteins.csv.gz";
	private static final String spHppPeptidesFile = "SpHPP_Peptides.csv.gz";
	private static final String spHppVersionFile = "SpHPP.ver";

	private File peffFile;
	private File proteinsFile;
	private File peptidesFile;
	private String nxVersion;
	private String spHppVersion;	
	private long lastModifiedNx = -1;
	private long lastModifiedSpHpp = -1;

	private Map<String, ShotProteinEx> proteins;	
	private List<ShotPeptideEx> peptides;

	public DatabasesBean() throws FileNotFoundException, IOException, InvalidSequenceException {
		loadDatabases();
	}
	
	private void loadDatabases() throws FileNotFoundException, IOException, InvalidSequenceException {
		// neXtProt
		logger.info("Loading peff ...");
		peffFile = new File(nxPath,nxPeffFile);
		Reader rd = new InputStreamReader(new GZIPInputStream(new FileInputStream(peffFile)));		
		List<Fasta> listPeff = Fasta.readEntries(rd, Fasta.SequenceType.PROTEIN);
		rd.close();
		logger.info("Loading chromosome reports ...");
		List<Entry> listChr = TxtReader.readDirectory(nxPath);
		Map<String,Entry> mapChr = new HashMap<>();
		for( Entry entry : listChr )
			mapChr.put(UniProtUtils.canonicalAccesion(entry.getProtein()), entry);
		logger.info("Loading ENSG mappings ...");
		Map<String,Set<String>> mapEnsg = Mapping.readEnsg(new File(nxPath,nxMapFile).getAbsolutePath());
		nxVersion = listChr.get(0).getRelease();
		lastModifiedNx = peffFile.lastModified();		

		// SpHpp				
		logger.info("Loading SpHpp protein list ...");
		proteinsFile = new File(spHppPath,spHppProteinsFile);
		List<ShotProtein> spHppProteins = ShotDb.readProteinList(proteinsFile.getAbsolutePath());
		logger.info("Loading SpHpp peptide list ...");
		peptidesFile = new File(spHppPath,spHppPeptidesFile);
		List<ShotPeptide> spHppPeptides = ShotDb.readPeptideList(peptidesFile.getAbsolutePath());
		Map<String,ShotProtein> mapSpHpp = new HashMap<>();
		for( ShotProtein protein : spHppProteins )
			mapSpHpp.put(protein.getUniprotAccession(), protein);
		lastModifiedSpHpp = peptidesFile.lastModified();
		BufferedReader br = new BufferedReader(new FileReader(new File(spHppPath,spHppVersionFile)));
		spHppVersion = br.readLine();
		br.close();
		
		// Mix
		logger.info("Merging data ...");
		ShotProtein spHppProtein;
		Entry chr;
		String key, cann;
		String ensg;
		Set<String> ensgs;
		ShotProteinEx protein;
		proteins = new HashMap<>();
		for( Fasta peff : listPeff ) {
			key = UniProtUtils.canonicalAccesion(peff.getAccession());
			spHppProtein = mapSpHpp.get(key);
			chr = mapChr.get(key);
			ensgs = mapEnsg.get(key);
			if( ensgs != null )
				ensg = CsvUtils.getCsv(';', ensgs.toArray());
			else
				ensg = null;
			cann = UniProtUtils.canonicalAccesion(peff.getAccession());
			protein = new ShotProteinEx(cann, spHppProtein, chr, peff, ensg);
			proteins.put(cann, protein);
		}
		ShotPeptideEx peptide;
		peptides = new ArrayList<>();
		for( ShotPeptide spHppPeptide  : spHppPeptides ) {
			peptide = new ShotPeptideEx(spHppPeptide, proteins);
			peptides.add(peptide);
		}
		logger.info("Finished!!");
	}
	
	private void reloadDatabases() {
		try {
			loadDatabases();
		} catch( Exception e ) {
			e.printStackTrace();
			logger.severe("Error loadind databases");
		}
	}

	public String getNxVersion() {
		if( nxVersion == null || lastModifiedNx != peffFile.lastModified() )
			reloadDatabases();
		return nxVersion;
	}
	
	public String getSpHppVersion() {
		if( spHppVersion == null || lastModifiedSpHpp != peptidesFile.lastModified() )
			reloadDatabases();
		return spHppVersion;
	}
	
	public Map<String, ShotProteinEx> getProteins() {
		return proteins;
	}

	public List<ShotPeptideEx> getPeptides() {
		return peptides;
	}
}