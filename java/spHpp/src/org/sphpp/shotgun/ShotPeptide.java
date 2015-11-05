package org.sphpp.shotgun;

import java.util.List;
import java.util.Set;

public class ShotPeptide {
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Set<String> getExperiments() {
		return experiments;
	}
	public void setExperiments(Set<String> list) {
		this.experiments = list;
	}
	public List<String> getRep() {
		return rep;
	}
	public void setRep(List<String> rep) {
		this.rep = rep;
	}
	public String getResearcher() {
		return researcher;
	}
	public void setResearcher(String researcher) {
		this.researcher = researcher;
	}
	public Set<String> getAccessions() {
		return accessions;
	}
	public void setAccessions(Set<String> accessions) {
		this.accessions = accessions;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public Double getMascotScore() {
		return mascotScore;
	}
	public void setMascotScore(Double mascotScore) {
		this.mascotScore = mascotScore;
	}
	public Double getMascotExpectation() {
		return mascotExpectation;
	}
	public void setMascotExpectation(Double mascotExpectation) {
		this.mascotExpectation = mascotExpectation;
	}
	public Integer getOcurrence() {
		return ocurrence;
	}
	public void setOcurrence(Integer ocurrence) {
		this.ocurrence = ocurrence;
	}
	public Double getMzExp() {
		return mzExp;
	}
	public void setMzExp(Double mzExp) {
		this.mzExp = mzExp;
	}
	public Double getMzCalc() {
		return mzCalc;
	}
	public void setMzCalc(Double mzCalc) {
		this.mzCalc = mzCalc;
	}
	public List<Double> getRt() {
		return rt;
	}
	public void setRt(List<Double> rt) {
		this.rt = rt;
	}
	public Set<Integer> getCharges() {
		return charges;
	}
	public void setCharges(Set<Integer> charges) {
		this.charges = charges;
	}
	public String getModifSeq() {
		return modifSeq;
	}
	public void setModifSeq(String modifSeq) {
		this.modifSeq = modifSeq;
	}
	public List<String> getModifs() {
		return modifs;
	}
	public void setModifs(List<String> modifs) {
		this.modifs = modifs;
	}
	private String code;
	private Set<String> experiments;
	private List<String> rep;
	private String researcher;
	private Set<String> accessions;
	private String sequence;
	private Double mascotScore;
	private Double mascotExpectation;
	private Integer ocurrence;
	private Double mzExp;
	private Double mzCalc;
	private List<Double> rt;
	private Set<Integer> charges;
	private String modifSeq;
	private List<String> modifs;
}
