package org.sphpp.uniprot;

public enum DatabaseType {
	UniProtKB("UniProtKB AC","ACC","UniProtKB"),
	RefSeqProtein("RefSeq Protein","P_REFSEQ_AC","RefSeq"),
	RefSeqNucleotide("RefSeq Nucleotide","REFSEQ_NT_ID","RefSeq_NT"),
	EnsemblGene("Ensembl","ENSEMBL_ID","Ensembl"),
	EnsemblProtein("Ensembl Protein","ENSEMBL_PRO_ID","Ensembl_PRO"),
	EnsemblTranscript("Ensembl Transcript","ENSEMBL_TRS_ID","Ensembl_TRS");
	// For more see -> http://www.uniprot.org/faq/28#id_mapping_examples
	
	DatabaseType( String name, String abbreviation, String id ) {
		this.name = name;
		this.abbreviation = abbreviation;
		this.id = id;
	}
	
	public final String name;
	public final String abbreviation;
	public final String id;
}
