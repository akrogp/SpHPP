package org.sphpp.apps.missing.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sphpp.apps.missing.MissingApp;
import org.sphpp.apps.missing.ShotProteinEx;
import org.sphpp.shotgun.ShotLine;

public class ProteinBean implements Serializable, Comparable<ProteinBean> {
	private static final long serialVersionUID = 1L;
	private String accession;	
	private List<PeptideBean> peptides = new ArrayList<>();
	private ShotProteinEx protein;
	private String geneName;
	
	public String getSequence() {
		return parse(protein == null ? null : protein.getSequence());
	}

	public String getChr() {
		return parse(protein == null ? null : protein.getChr());
	}

	public String getProteinName() {
		return parse(protein == null ? null : protein.getProteinName());
	}

	public String getGeneName() {
		return parse(geneName);
	}

	public String getProteinEvidence() {
		return parse(protein == null ? null : protein.getProteinEvidence());
	}

	public String getMissing() {
		return parse(protein == null ? null : protein.isMissing());
	}

	public String getProteomicsNextprot() {
		return parse(protein == null ? null : protein.isProteomicsNextprot());
	}

	public Map<String, ShotLine> getLineCounts() {
		return protein == null ? null : protein.getLineCounts();
	}
	
	public String getLine( String line ) {
		Integer count;
		if( getLineCounts() == null || (count = getLineCounts().get(line).getCountMax()) == null )
			return "Not found";
		if( count < 0 )
			return "Detected";
		return "Observed";
	}
	
	public String getShotgunStatus() {
		if( protein == null )
			return parse(null);
		if( protein.getShotgunObserved() != null && protein.getShotgunObserved() > 0 )
			return "Observed";
		if( protein.getShotgunDetected() != null )
			return "Detected";
		return parse(null);
	}

	public String getShotgunDetected() {
		return parse(protein == null ? null : protein.getShotgunDetected());
	}

	public String getShotgunObserved() {
		return parse(protein == null ? null : protein.getShotgunObserved());
	}

	public String getUniprotAccession() {
		return parse(protein == null ? null : protein.getOriginalAccession());
	}

	public String getHpa() {
		return parse(protein == null ? null : protein.isHpa());
	}

	public String getJpr2Missing() {
		return parse(protein == null ? null : protein.isJpr2Missing());
	}

	public String getMrmValidated() {
		return parse(protein == null ? null : protein.isMrmValidated());
	}

	public String getNappa() {
		return parse(protein == null ? null : protein.isNappa());
	}

	public String getProteomicsDb() {
		return parse(protein == null ? null : protein.isProteomicsDb());
	}

	public String getEnsg() {
		return parse(protein == null ? null : protein.getEnsg());
	}

	public String getAccession() {
		return accession;
	}
	
	public void setAccession(String accession) {
		this.accession = accession;
	}

	public List<PeptideBean> getPeptides() {
		return peptides;
	}

	public void setPeptides(List<PeptideBean> list) {
		this.peptides = list;
	}
	
	public int getPeptideCount() {
		if( peptides == null )
			return 0;
		return peptides.size();
	}

	public ShotProteinEx getProtein() {
		return protein;
	}

	public void setProtein(ShotProteinEx protein) {
		this.protein = protein;
		geneName = protein.getGeneName();
		if( geneName != null )
			geneName.replaceAll(";", ""+MissingApp.listDelimiter);
	}
	
	private static String parse( Object field ) {
		return field == null ? "?" : field.toString();
	}

	@Override
	public int compareTo(ProteinBean o) {		
		int res;
		if( (res=MissingApp.compareNulls(protein, o.protein)) != 0 )
			return res;
		if( protein == null && o.protein == null )
			return 0;
		if( (res=MissingApp.compareBools(o.protein.isMissing(), protein.isMissing())) != 0 )
			return res;
		if( (res=MissingApp.compareBools(protein.isProteomicsNextprot(), o.protein.isProteomicsNextprot())) != 0 )
			return res;
		if( (res=MissingApp.compareBools(o.protein.isJpr2Missing(), protein.isJpr2Missing())) != 0 )
			return res;
		if( (res=MissingApp.compareBools(protein.isHpa(), o.protein.isHpa())) != 0 )
			return res;
		if( (res=MissingApp.compareBools(protein.isNappa(), o.protein.isNappa())) != 0 )
			return res;
		if( (res=MissingApp.compareBools(protein.isProteomicsDb(), o.protein.isProteomicsDb())) != 0 )
			return res;
		if( (res=MissingApp.compareBools(protein.isMrmValidated(), o.protein.isMrmValidated())) != 0 )
			return res;
		return 0;
	}
}