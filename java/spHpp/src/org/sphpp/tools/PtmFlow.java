package org.sphpp.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import es.ehubio.db.ebi.DbFetcher;
import es.ehubio.db.phosphositeplus.PhosphoCsv;
import es.ehubio.db.phosphositeplus.PhosphoEntry;
import es.ehubio.db.uniprot.UniProtUtils;
import es.ehubio.db.uniprot.xml.FeatureType;
import es.ehubio.io.CsvUtils;
import es.ehubio.model.ProteinModification;
import es.ehubio.panalyzer.MainModel;
import es.ehubio.proteomics.AmbiguityGroup;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Ptm;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.tools.Command.Interface;

public class PtmFlow implements Interface {
	private final static String PhosphoGzPath = "/media/data/Sequences/PhosphoSitePlus/current/Phosphorylation_site_dataset.gz";
	private final static Logger logger = Logger.getLogger(PtmFlow.class.getName());
	private final static char SEP = '\t';
	private final static char INTER = ',';
	private final MainModel panalyzer;
	private final DbFetcher fetcher = new DbFetcher();
	private final Set<String> reportedPtms = new HashSet<>(Arrays.asList("Phospho"));
	private final Map<String,List<PhosphoEntry>> mapPhospho = new HashMap<>();
	
	public PtmFlow() throws FileNotFoundException, IOException {
		panalyzer = new MainModel();
		
		logger.info(String.format("Loading %s ...", PhosphoGzPath));
		List<PhosphoEntry> data = PhosphoCsv.loadByOrganism(PhosphoGzPath, "human");		
		for( PhosphoEntry entry : data ) {
			List<PhosphoEntry> list = mapPhospho.get(entry.getAccession());
			if( list == null ) {
				list = new ArrayList<>();
				mapPhospho.put(entry.getAccession(), list);
			}
			list.add(entry);
		}
	}

	@Override
	public String getUsage() {
		return "ShotgunFlow <experiment.pax>";
	}

	@Override
	public int getMinArgs() {
		return 1;
	}

	@Override
	public int getMaxArgs() {
		return 1;
	}

	@Override
	public void run(String[] args) throws Exception {
		panalyzer.run(args[0]);
		
		String path = panalyzer.getConfig().getOutput().replaceAll("\\..*", "");
		savePtms(path+"-ptms.csv");
	}
	
	private void savePtms(String path) throws IOException {
		logger.info(String.format("Saving %s ...", path));
		
		PrintWriter pw = new PrintWriter(path);
		pw.println(CsvUtils.getCsv(SEP,
			"canonical_id", "protein_isoforms",
			"group_id", "group_name", "group_type",
			"peptide_sequence", "peptide_ptm_sequence", "peptide_type", "peptide_p-value", "peptide_q-value",
			"ptm_type", "ptm_position",
			"protein_position", "reported_by_uniprot", "reported_by_phosphositeplus"
			));
		
		Map<String,List<Protein>> mapCanonical = new HashMap<>();
		for( Peptide peptide : panalyzer.getData().getPeptides() )
			for( Ptm ptm : peptide.getPtms() ) {
				if( !reportedPtms.contains(ptm.getName()) )
					continue;
				createMap(mapCanonical,peptide.getProteins());				
				for( Entry<String,List<Protein>> entry : mapCanonical.entrySet() ) {
					AmbiguityGroup group = entry.getValue().iterator().next().getGroup();
					pw.println(CsvUtils.getCsv(SEP,
						entry.getKey(), CsvUtils.getCsv(INTER, entry.getValue().toArray()),
						group.getId(), group.buildName(), group.getConfidence(),
						peptide.getSequence(), peptide.getMassSequence(), peptide.getConfidence(),						
						peptide.getScoreByType(ScoreType.PEPTIDE_P_VALUE),
						peptide.getScoreByType(ScoreType.PEPTIDE_Q_VALUE),
						ptm.getName(), ptm.getPosition(),
						getEvidences(ptm,peptide,entry.getKey())
						));
				}
			}
		pw.close();
	}	

	private void createMap(Map<String, List<Protein>> mapCanonical, Set<Protein> proteins) {
		mapCanonical.clear();				
		for( Protein protein : proteins ) {
			if( Boolean.TRUE.equals(protein.getDecoy()) )
				continue;
			String can = UniProtUtils.canonicalAccesion(protein.getAccession());
			List<Protein> list = mapCanonical.get(can);
			if( list == null ) {
				list = new ArrayList<>();
				mapCanonical.put(can, list);
			}
			list.add(protein);
		}
	}
	
	private String getEvidences(Ptm ptm, Peptide peptide, String acc) {
		if( ptm.getPosition() == null || ptm.getResidues() == null || ptm.getResidues().length() != 1 )
			return "?";	
		logger.info(String.format("Fetching from UniProt: %s ...", acc));
		es.ehubio.db.uniprot.xml.Entry uniprotEntry = fetcher.fetchUniProt(acc);
		List<PhosphoEntry> phosphoList = mapPhospho.get(acc);
		String protSeq = uniprotEntry.getSequence().getValue().toUpperCase().replaceAll("[ \t\n]", "");
		String pepSeq = peptide.getSequence().toUpperCase();
		int from = 0;
		List<Integer> positions = new ArrayList<>();
		List<Integer> uniprot = new ArrayList<>();
		List<Integer> phosphosite = new ArrayList<>();
		do {
			from = protSeq.indexOf(pepSeq, from);
			if( from == -1 )
				break;
			positions.add(from+ptm.getPosition());			
			uniprot.add(findUniProt(uniprotEntry, from, ptm)?1:0);
			phosphosite.add(findPhosphoSite(phosphoList, from, ptm)?1:0);
			from += pepSeq.length();
		} while( true );		
		return CsvUtils.getCsv(SEP,
			CsvUtils.getCsv(INTER, positions.toArray()),
			CsvUtils.getCsv(INTER, uniprot.toArray()),
			CsvUtils.getCsv(INTER, phosphosite.toArray())
			);
	}
	
	private boolean findUniProt(es.ehubio.db.uniprot.xml.Entry uniprotEntry, int from, Ptm ptm) {
		boolean foundUniprot = false;
		for( FeatureType feature : uniprotEntry.getFeature() ) {
			ProteinModification mod = UniProtUtils.featureToModification(feature);
			if( mod != null && mod.getType() != null && mod.getType() == ptm.getType() && mod.getPosition() == from+ptm.getPosition() ) {
				foundUniprot = true;
				break;
			}
		}
		return foundUniprot;
	}
	
	private boolean findPhosphoSite(List<PhosphoEntry> phosphoList, int from, Ptm ptm) {		
		if( phosphoList == null )
			return false;
		boolean foundPhoshphosite = false;
		for( PhosphoEntry phosphoEntry : phosphoList )
			if( phosphoEntry.getType() != null || phosphoEntry.getType() == ptm.getType() && phosphoEntry.getPosition().equals(from+ptm.getPosition()) ) {
				foundPhoshphosite = true;
				break;
			}
		return foundPhoshphosite;
	}
}