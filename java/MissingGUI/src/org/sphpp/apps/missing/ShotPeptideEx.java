package org.sphpp.apps.missing;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sphpp.shotgun.ShotPeptide;

import es.ehubio.io.CsvUtils;

public final class ShotPeptideEx {
	private final ShotPeptide peptide;
	private final Set<ShotProteinEx> proteins;	
	private final String chr;
	private final String ensg;
	private final String gene;
	
	public ShotPeptideEx( ShotPeptide peptide, Map<String,ShotProteinEx> map ) {
		this.peptide = peptide;
		proteins = new HashSet<>();
		Set<String> chr = new HashSet<>();
		Set<String> ensg = new HashSet<>();
		Set<String> gene = new HashSet<>();
		for( String accession : peptide.getAccessions() ) {
			ShotProteinEx protein = map.get(accession);
			if( protein == null ) {
				proteins.add(new ShotProteinEx(accession, null, null, null, null));	// Not unique
				continue;
			}
			if( !protein.getSequence().toLowerCase().contains(getSequence().toLowerCase()) )
				continue;
			proteins.add(protein);
			chr.add(protein.getChr());
			ensg.add(protein.getEnsg());
			gene.add(protein.getGeneName());
			protein.getPeptides().add(this);
		}
		this.chr = CsvUtils.getCsv(';', chr.toArray());
		this.ensg = CsvUtils.getCsv(';', ensg.toArray());
		this.gene = CsvUtils.getCsv(';', gene.toArray());
	}
	
	public String getChr() {
		return chr;
	}
	
	public Set<String> getAccessions() {
		return peptide.getAccessions();
	}

	public Set<Integer> getCharges() {
		return peptide.getCharges();
	}

	public String getCode() {
		return peptide.getCode();
	}

	public Set<String> getExperiments() {
		return peptide.getExperiments();
	}

	public Double getMascotExpectation() {
		return peptide.getMascotExpectation();
	}

	public Double getMascotScore() {
		return peptide.getMascotScore();
	}

	public String getModifSeq() {
		return peptide.getModifSeq();
	}

	public List<String> getModifs() {
		return peptide.getModifs();
	}

	public Double getMzCalc() {
		return peptide.getMzCalc();
	}

	public Double getMzExp() {
		return peptide.getMzExp();
	}

	public Integer getOcurrence() {
		return peptide.getOcurrence();
	}

	public List<String> getRep() {
		return peptide.getRep();
	}

	public String getResearcher() {
		return peptide.getResearcher();
	}

	public List<Double> getRt() {
		return peptide.getRt();
	}

	public String getSequence() {
		return peptide.getSequence();
	}

	public Boolean isUnique() {
		return proteins.isEmpty() ? null : proteins.size() == 1;
	}

	public String getEnsg() {
		return ensg;
	}

	public String getGene() {
		return gene;
	}
	
	public Set<ShotProteinEx> getProteins() {
		return proteins;
	}
}
