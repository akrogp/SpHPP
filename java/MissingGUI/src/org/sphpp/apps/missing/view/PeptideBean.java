package org.sphpp.apps.missing.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.sphpp.apps.missing.MissingApp;
import org.sphpp.apps.missing.ShotPeptideEx;
import org.sphpp.apps.missing.ShotProteinEx;

import es.ehubio.db.uniprot.UniProtUtils;
import es.ehubio.io.CsvUtils;

public class PeptideBean implements Serializable, Comparable<PeptideBean> {
	private static final long serialVersionUID = 1L;
	private ShotPeptideEx peptide;
	private String accessions;
	private String charges;
	private String experiments;
	private String modifs;
	private String rep;
	private String rt;
	private String mascotExpectation;
	private String mascotScore;
	private String mzCalc;
	private String mzExp;
	private String uniqueRelaxed;
	
	public String getChr() {
		return parse(peptide.getChr());
	}

	public String getAccessions() {
		return accessions;
	}

	public String getCharges() {
		return charges;
	}

	public String getCode() {
		return peptide.getCode();
	}

	public String getExperiments() {
		return experiments;
	}

	public String getMascotExpectation() {
		return mascotExpectation;
	}

	public String getMascotScore() {
		return mascotScore;
	}

	public String getModifSeq() {
		return peptide.getModifSeq();
	}

	public String getModifs() {
		return modifs;
	}

	public String getMzCalc() {
		return mzCalc;
	}

	public String getMzExp() {
		return mzExp;
	}

	public Integer getOcurrence() {
		return peptide.getOcurrence();
	}

	public String getRep() {
		return rep;
	}

	public String getResearcher() {
		return peptide.getResearcher();
	}

	public String getRt() {
		return rt;
	}

	public String getSequence() {
		return peptide.getSequence();
	}

	public String getUnique() {
		return peptide.isUnique() ? "yes" : "no";
	}
	
	public String getUniqueRelaxed() {
		return uniqueRelaxed;
	}

	public String getEnsg() {
		return peptide.getEnsg();
	}

	public String getGene() {
		return peptide.getGene();
	}

	public ShotPeptideEx getPeptide() {
		return peptide;
	}
	
	public void setPeptide(ShotPeptideEx peptide) {
		this.peptide = peptide;
		List<String> listAccessions = new ArrayList<>();
		uniqueRelaxed = "yes";
		String acc, base, uniqueAccession = null;
		for( ShotProteinEx protein : peptide.getProteins() ) {
			if( protein == null ) {
				listAccessions.add("?");
				uniqueRelaxed = "no";
				continue;
			}
			acc = protein.getAccession();
			listAccessions.add(acc);
			base = UniProtUtils.canonicalAccesion(acc);
			if( uniqueAccession == null )
				uniqueAccession = base;
			else if( !base.equalsIgnoreCase(uniqueAccession) )
				uniqueRelaxed = "no";
		}
		accessions = CsvUtils.getCsv(MissingApp.listDelimiter, listAccessions.toArray());
		//accessions = Utils.getCsv(';', peptide.getAccessions().toArray());		
		charges = peptide.getCharges() == null ? "" : CsvUtils.getCsv(MissingApp.listDelimiter, peptide.getCharges().toArray());
		experiments = peptide.getExperiments() == null ? "" : CsvUtils.getCsv(MissingApp.listDelimiter, peptide.getExperiments().toArray());
		modifs = peptide.getModifs() == null ? "" : CsvUtils.getCsv(MissingApp.listDelimiter, peptide.getModifs().toArray());
		rep = peptide.getRep() == null ? "" : CsvUtils.getCsv(MissingApp.listDelimiter, peptide.getRep().toArray());
		rt = peptide.getRt() == null ? "" : CsvUtils.getCsv(MissingApp.listDelimiter, peptide.getRt().toArray());
		mascotScore = String.format(Locale.ENGLISH,"%.2f", peptide.getMascotScore());
		mascotExpectation = String.format(Locale.ENGLISH,"%.2e", peptide.getMascotExpectation());
		mzCalc = String.format(Locale.ENGLISH,"%.2f", peptide.getMzCalc());
		mzExp = String.format(Locale.ENGLISH,"%.2f", peptide.getMzExp());
	}
	
	private static String parse( Object field ) {
		return field == null ? "?" : field.toString();
	}

	@Override
	public int compareTo(PeptideBean o) {
		int res;
		if( (res=MissingApp.compareNulls(o.peptide, peptide))!= 0 )
			return res;
		if( (res=MissingApp.compareBools(o.peptide.isUnique(), peptide.isUnique())) != 0 )
			return res;
		if( getUniqueRelaxed().equals("yes") && o.getUniqueRelaxed().equals("no") )
			return -1;
		if( getUniqueRelaxed().equals("no") && o.getUniqueRelaxed().equals("yes") )
			return 1;
		if( (res=MissingApp.compareIntegers(peptide.getOcurrence(),o.peptide.getOcurrence()))!= 0 )
			return res;
		if( (res=MissingApp.compareDoubles(peptide.getMascotScore(),o.peptide.getMascotScore()))!= 0 )
			return res;
		return 0;
	}
}
