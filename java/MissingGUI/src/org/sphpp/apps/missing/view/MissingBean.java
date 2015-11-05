package org.sphpp.apps.missing.view;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.sphpp.apps.missing.MissingApp;
import org.sphpp.apps.missing.ShotPeptideEx;
import org.sphpp.apps.missing.ShotProteinEx;

import es.ehubio.db.uniprot.UniProtUtils;
import es.ehubio.io.CsvUtils;

@ManagedBean
@SessionScoped
public class MissingBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String inputAccessions;
	private boolean done = false;
	private List<ProteinBean> proteins = new ArrayList<>();	
	private ProteinBean selProtein;
	private String error;
	private boolean sortProteins = true;
	private static final String version = "1.0";

	public String getInputAccessions() {
		return inputAccessions;
	}

	public void setInputAccessions(String inputAccessions) {
		this.inputAccessions = inputAccessions;
	}

	public boolean isDone() {
		return done;
	}
	
	public String search() {
		done = false;
		error = null;
		proteins.clear();
		selProtein = null;
		if( inputAccessions == null || inputAccessions.isEmpty() ) {
			error = "You must enter a valid UniProt/neXtProt accession list";
			return null;
		}
		String[] accessions = inputAccessions.split("[ ;:,\\t\\n]");
		if( accessions.length > 30 ) {
			error = "Sorry, a maximum of 30 proteins are allowed at the moment";
			return null;
		}
		Map<String,ShotProteinEx> proteinMap = databases.getProteins();		
		String accInput, accProtein, accIsoform;
		ProteinBean protein;
		PeptideBean peptide;
		ShotProteinEx shotProtein;
		for( String line : accessions ) {			
			accInput = line.trim();
			accIsoform = UniProtUtils.reducedAccession(accInput);
			accProtein = UniProtUtils.canonicalAccesion(accIsoform);
			if( accProtein.isEmpty() )
				continue;
			if( !UniProtUtils.validAccession(accProtein) ) {
				error = "Error: \""+accProtein+"\" is not a valid UniProt/neXtProt accession";
				return null;
			}			
			protein = new ProteinBean();
			protein.setAccession(accInput);
			shotProtein = proteinMap.get(accIsoform);			
			if( shotProtein != null ) {
				protein.setProtein(shotProtein);
				for( ShotPeptideEx shotPeptide : shotProtein.getPeptides() ) {
					peptide = new PeptideBean();
					peptide.setPeptide(shotPeptide);
					protein.getPeptides().add(peptide);
				}
			}
			proteins.add(protein);
		}
		if( proteins.isEmpty() ) {
			error = "You must enter a valid accessions list";
			return null;
		}
		if( sortProteins )
			Collections.sort(proteins);
		done = true;
		return "proteins";
	}
	
	public String closeSession() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		//FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove("missingBean");
		return "home";
	}
	
	public String showPeptides( ProteinBean protein ) {
		selProtein = protein;
		Collections.sort(selProtein.getPeptides());
		return "peptides";
	}
	
	public void downloadProteins() {
		FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.responseReset();
        ec.setResponseContentType("text/csv");
        ec.setResponseHeader("Content-Disposition",
        	String.format("attachment; filename=\"proteins_neXtProt-%s_SpHpp-%s.csv\"",
        			getNxVersion(), getSpHppVersion()));        
        try {
        	PrintWriter wr = new PrintWriter(ec.getResponseOutputStream());
        	wr.println(CsvUtils.getCsv(MissingApp.cellDelimiter,
        		"Accession","Gene","ENSGs","Chromosome","Evidence","Missing","Proteomics neXtProt","HPA",
        		"ProteomicsDB","NAPPA","JPR2 Missing","MRM Validated","CCD18","MCF7","RAMOS","JURKAT",
        		"SpHPP Status","SpHPP Peptides"));
        	for( ProteinBean protein : proteins )
        		wr.println(CsvUtils.getCsv(MissingApp.cellDelimiter,
        			protein.getAccession(),
        			protein.getGeneName(),
        			protein.getEnsg(),
        			protein.getChr(),
        			protein.getProteinEvidence(),
        			protein.getMissing(),
        			protein.getProteomicsNextprot(),
        			protein.getHpa(),
        			protein.getProteomicsDb(),
        			protein.getNappa(),
        			protein.getJpr2Missing(),
        			protein.getMrmValidated(),
        			protein.getLine("CCD18"), protein.getLine("MCF7"), protein.getLine("RAMOS"), protein.getLine("JURKAT"),
        			protein.getShotgunStatus(),
        			protein.getPeptideCount())
        		);
            wr.flush();
        } catch( Exception e ) {
            e.printStackTrace();
            error = "Internal problem: " + e.getMessage();
        }
        fc.responseComplete();
	}
	
	public void downloadPeptides() {
		FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.responseReset();
        ec.setResponseContentType("text/csv");
        ec.setResponseHeader("Content-Disposition",
        	String.format("attachment; filename=\"peptides_%s_SpHpp-%s.csv\"",
        			selProtein.getAccession(), getSpHppVersion()));
        try {
        	PrintWriter wr = new PrintWriter(ec.getResponseOutputStream());
        	wr.println(CsvUtils.getCsv(MissingApp.cellDelimiter,
        		"Code","Sequence","Unique","Unique*","Proteins","Mascot Score","Mascot Expectation",
        		"Charge","m/z (calc)","m/z (exp)","Retention Time","Ocurrence","Modifs","Modif. Sequence",
        		"Experiments","fr/band/rep","Researcher"));
        	for( PeptideBean peptide : selProtein.getPeptides() )
        		wr.println(CsvUtils.getCsv(MissingApp.cellDelimiter,
            		peptide.getCode(),
            		peptide.getSequence(),
            		peptide.getUnique(),
            		peptide.getUniqueRelaxed(),
            		peptide.getAccessions(),
            		peptide.getMascotScore(),
            		peptide.getMascotExpectation(),
            		peptide.getCharges(),
            		peptide.getMzCalc(),
            		peptide.getMzExp(),
            		peptide.getRt(),
            		peptide.getOcurrence(),
            		peptide.getModifs(),
            		peptide.getModifSeq(),
            		peptide.getExperiments(),
            		peptide.getRep(),
            		peptide.getResearcher())
            	);
        	wr.flush();
        } catch( Exception e ) {
            e.printStackTrace();
            error = "Internal problem: " + e.getMessage();
        }
        fc.responseComplete();
	}

	public List<ProteinBean> getProteins() {
		return proteins;
	}
	
	public String getProteinCount() {
		return String.format("%d %s", proteins.size(), proteins.size() == 1 ? "protein" : "proteins");
	}
	
	public String getPeptideCount() {
		return String.format("%d %s", selProtein.getPeptides().size(), selProtein.getPeptides().size() == 1 ? "peptide" : "peptides");
	}
	
	public boolean isEmptyProteins() {
		return proteins == null || proteins.isEmpty();
	}
	
	public boolean isEmptyPeptides() {
		return getPeptides() == null || getPeptides().isEmpty();
	}

	public String getNxVersion() {
		return databases.getNxVersion();
	}

	public String getError() {
		return error;
	}
	
	public String getSpHppVersion() {
		return databases.getSpHppVersion();
	}
	
	@ManagedProperty(value="#{databasesBean}")
    private DatabasesBean databases;

	public void setDatabases(DatabasesBean databases) {
		this.databases = databases;
	}

	public List<PeptideBean> getPeptides() {
		return selProtein == null ? null : selProtein.getPeptides();
	}

	public ProteinBean getSelProtein() {
		return selProtein;
	}

	public boolean isSortProteins() {
		return sortProteins;
	}

	public void setSortProteins(boolean sortProteins) {
		this.sortProteins = sortProteins;
	}

	public String getVersion() {
		return version;
	}
}
