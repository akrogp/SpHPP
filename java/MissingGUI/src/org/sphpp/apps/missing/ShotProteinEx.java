package org.sphpp.apps.missing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sphpp.nextprot.chrreport.Entry;
import org.sphpp.shotgun.ShotLine;
import org.sphpp.shotgun.ShotProtein;

import es.ehubio.db.fasta.Fasta;

public final class ShotProteinEx {
	private final String accession;
	private final ShotProtein sphpp;
	private final Entry chr;
	private final Fasta peff;
	private final String ensg;	
	private List<ShotPeptideEx> peptides = new ArrayList<>();
	
	public ShotProteinEx( String accession, ShotProtein protein, Entry chr, Fasta peff, String ensg ) {
		this.accession = accession;
		this.sphpp = protein;
		this.chr = chr;
		this.peff = peff;
		this.ensg = ensg;
	}
	
	public String getSequence() {
		return peff != null ? peff.getSequence() : null;
	}
	
	public String getChr() {
		return chr != null ? chr.getChromosome() : null;
	}
	
	public String getProteinName() {
		return peff != null ? peff.getProteinName() : null;
	}
	
	public String getGeneName() {
		return peff != null ? peff.getGeneName() : null;
	}
	
	public String getProteinEvidence() {
		return chr != null ? chr.getExistence() : null;
	}
	
	public Boolean isMissing() {
		return chr != null ? chr.isMissing() : null;
	}
	
	public Boolean isProteomicsNextprot() {
		return chr != null ? chr.isProteomics() : null;
	}
	
	public Map<String, ShotLine> getLineCounts() {
		return sphpp != null ? sphpp.getLineCounts() : null;
	}

	public Integer getShotgunDetected() {
		return sphpp != null ? sphpp.getShotgunDetected() : null;
	}

	public Integer getShotgunObserved() {
		return sphpp != null ? sphpp.getShotgunObserved() : null;
	}
	
	public String getAccession() {
		return accession;
	}

	public String getOriginalAccession() {
		return sphpp != null ? sphpp.getUniprotAccession() : null;
	}

	public Boolean isHpa() {
		return sphpp != null ? sphpp.isHpa() : null;
	}

	public Boolean isJpr2Missing() {
		return sphpp != null ? sphpp.isJpr2Missing() : null;
	}

	public Boolean isMrmValidated() {
		return sphpp != null ? sphpp.isMrmValidated() : null;
	}

	public Boolean isNappa() {
		return sphpp != null ? sphpp.isNappa() : null;
	}

	public Boolean isProteomicsDb() {
		return sphpp != null ? sphpp.isProteomicsDb() : null;
	}

	public String getEnsg() {
		return ensg;
	}

	public List<ShotPeptideEx> getPeptides() {
		return peptides;
	}
	
	public int getPeptideCount() {
		return getPeptides().size();
	}

	public void addPeptide(ShotPeptideEx peptide) {
		peptides.add(peptide);
	}	
}
