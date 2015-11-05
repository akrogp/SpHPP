package org.sphpp.nextprot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Mapping {
	public static Map<String,Set<String>> readEnsg( String path ) throws IOException {
		Map<String,Set<String>> map = new HashMap<>();
		BufferedReader rd = new BufferedReader(new FileReader(path));
		String line;
		String[] fields;
		Set<String> ensgs;
		while( (line=rd.readLine()) != null ) {
			fields = line.split("\\s");
			ensgs = map.get(fields[0]);
			if( ensgs == null ) {
				ensgs = new HashSet<>();
				map.put(NextProt.uniProtAccession(fields[0]), ensgs);
			}
			ensgs.add(fields[1]);
		}
		rd.close();
		return map;
	}
}
