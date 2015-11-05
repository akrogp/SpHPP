package org.sphpp.nextprot.chrreport;

public final class Entry {
	public String getGene() {
		return gene;
	}
	
	public void setGene(String gene) {
		this.gene = gene;
	}
	
	public String getProtein() {
		return protein;
	}
	
	public void setProtein(String protein) {
		this.protein = protein;
	}
	
	public String getPosition() {
		return position;
	}
	
	public void setPosition(String position) {
		this.position = position;
	}
	
	public String getStart() {
		return start;
	}
	
	public void setStart(String start) {
		this.start = start;
	}
	
	public String getStop() {
		return stop;	
	}
	
	public void setStop(String stop) {
		this.stop = stop;
	}
	
	public String getExistence() {
		return existence;
	}
	
	public void setExistence(String existence) {
		this.existence = existence;
		if( existence.contains("protein") || existence.contains("uncertain") )
			missing = false;
		else
			missing = true;
	}
	
	public boolean isProteomics() {
		return proteomics;
	}
	
	public void setProteomics(boolean proteomics) {
		this.proteomics = proteomics;
	}
	
	public boolean isAntibody() {
		return antibody;
	}
	
	public void setAntibody(boolean antibody) {
		this.antibody = antibody;
	}
	
	public boolean isThreeD() {
		return threeD;
	}
	
	public void setThreeD(boolean threeD) {
		this.threeD = threeD;
	}
	
	public boolean isDisease() {
		return disease;
	}
	
	public void setDisease(boolean disease) {
		this.disease = disease;
	}
	
	public int getIsoforms() {
		return isoforms;
	}
	
	public void setIsoforms(int isoforms) {
		this.isoforms = isoforms;
	}
	
	public int getVariants() {
		return variants;
	}
	
	public void setVariants(int variants) {
		this.variants = variants;
	}
	
	public int getPTMs() {
		return PTMs;
	}
	
	public void setPTMs(int pTMs) {
		PTMs = pTMs;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getChromosome() {
		return chromosome;
	}
	
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public boolean isMissing() {
		return missing;
	}
	
	@Override
	public String toString() {
		return gene+":"+protein+":"+chromosome+":"+position+":"+start+":"+stop+":"+existence+":"+missing+":"+proteomics+":"+antibody+":"+threeD+":"+disease+":"+isoforms+":"+variants+":"+PTMs+":"+description;
	}
	
	public static String getHeader() {
		return "gene:protein:chromosome:position:start:stop:existence:missing:proteomics:antibody:3D:disease:isoforms:variants:PTMs:description";
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	private String chromosome;
	private String gene;
	private String protein;
	private String position;
	private String start;
	private String stop;
	private String existence;
	private boolean proteomics;
	private boolean antibody;
	private boolean threeD;
	private boolean disease;
	private int isoforms;
	private int variants;
	private int PTMs;
	private String description;
	private boolean missing;
	private String release;
}
