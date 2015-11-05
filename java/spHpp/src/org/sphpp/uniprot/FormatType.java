package org.sphpp.uniprot;

public enum FormatType {
	Html("html","HTML"),
	Tab("tab","Tab-delimited format"),
	Xls("xls","Excel"),
	Fasta("fasta","Returns sequence data only, where applicable"),
	Gff("gff","Returns sequence annotation, where applicable"),
	Txt("txt","Returns full entries"),
	Xml("xml","Returns full entries"),
	Rdf("rdf","Returns full entries"),
	List("list","Returns a list of identifiers"),
	Rss("rss","Returns an OpenSearch RSS feed");
	// See -> http://www.uniprot.org/faq/28#retrieving_entries_via_queries
		
	FormatType( String value, String description ) {
		this.value = value;
		this.description = description; 
	}
	
	public final String value;
	public final String description;
}
