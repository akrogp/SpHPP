package org.sphpp.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.sphpp.workflow.data.Link;
import org.sphpp.workflow.data.LinkMap;
import org.sphpp.workflow.file.RelationFile;

import es.ehubio.Numbers;
import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.io.CsvUtils;
import es.ehubio.io.FileUtils;
import es.ehubio.io.LogUtils;

public class ProteinInfo {
	public static void main(String[] args) throws Exception {
		LogUtils.disable();
		showPeptides();
		//showLengths();
	}
	
	private static void showLengths() throws Exception {
		List<Fasta> list = Fasta.readEntries(FASTA_PATH, SequenceType.PROTEIN);
		Map<String, Fasta> map = new HashMap<>();
		for( Fasta fasta : list )
			map.put(fasta.getAccession(), fasta);
		//List<String> accs = Streams.readLines("/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/CNB_CCD_NUC_R1/kk.txt");
		//PrintWriter pw = new PrintWriter("/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/CNB_CCD_NUC_R1/kk2.txt");
		for( String acc : PROTEINS )
			System.out.println(map.get(acc).getSequence().length());
			//System.out.println(Strings.counti(map.get(acc).getSequence(),'m'));
		//pw.close();		
	}

	private static void showPeptides() throws Exception {
		System.out.println(CsvUtils.getCsv('\t', "protein", "peptides", "LP of PSMs"));
		for( String protein : PROTEINS )
			showPeptides(protein);		
	}

	private static void showPeptides(String protein) throws Exception {
		System.out.print(protein+"\t");
		LinkMap<Link<Void, Void>, Link<Void, Void>> map = RelationFile.load(FileUtils.concat(LEGO_PATH, "Pep2ProtTarget.tsv.gz")).getLinkMap();
		Set<Link<Void, Void>> peptides = map.getUpper(protein).getLinks();
		System.out.print(CsvUtils.getCsv(',',peptides.toArray()));
		System.out.print('\t');
		
		map = RelationFile.load(FileUtils.concat(LEGO_PATH, "Feat2PepTarget.tsv.gz")).getLinkMap();
		LinkMap<Link<Void, Void>, Link<Void, Void>> lp = RelationFile.load(FileUtils.concat(LEGO_PATH, "LPPsmTarget.tsv.gz")).getLinkMap();
		List<String> scores = new ArrayList<>();
		for( Link<Void, Void> peptide : peptides ) {
			//System.out.print(String.format("%s (%d),", link.getId(), link.getLinks().size()));
			//System.out.println(map.getUpper(peptide.getId()).getLinks().size());			
			for( Link<Void, Void> psm : map.getUpper(peptide.getId()).getLinks() ) {
				double score = Numbers.parseDouble(lp.getUpper(psm.getId()).getLinks().iterator().next().getId());
				scores.add(String.format(Locale.ENGLISH,"%f", score));				
			}			
		}
		System.out.println(CsvUtils.getCsv(',',scores.toArray()));
	}

	private static final String[] PROTEINS = {
			"ENSP00000346349.4",
			"ENSP00000263780.4",
			"ENSP00000364691.4",
			"ENSP00000335084.5",
			"ENSP00000272418.2",
			"ENSP00000363603.3",
			"ENSP00000382250.2",
			"ENSP00000435096.1",
			"ENSP00000271638.2",
			"ENSP00000263856.4",
			"ENSP00000432279.1",
			"ENSP00000260270.2"

	};
	private static final String LEGO_PATH = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/AdultFrontalCortex/Sep16/lpc2";
	private static final String FASTA_PATH = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/gencode24-principal-unique.target.fasta";
	//private static final String LEGO_PATH = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/fdr/CNB_CCD_NUC_R1/LegoMix";
	//private static final String FASTA_PATH = "/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/cima/HPP2014/ensemblCrap.fasta";
}
