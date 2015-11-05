package org.sphpp.nextprot;

public class NextProt {
	public static String baseAccession( String acc ) {
		return acc.replaceAll("-.*", "");
	}
	
	public static String uniProtAccession( String acc ) {
		return acc.replaceAll("NX_", "");
	}
}
