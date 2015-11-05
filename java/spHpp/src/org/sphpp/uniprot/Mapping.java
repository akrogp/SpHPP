package org.sphpp.uniprot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Mapping {
	public static String getUrl( DatabaseType from, DatabaseType to, FormatType format, String acc ) {
		return String.format(
			"http://www.uniprot.org/mapping?from=%s&to=%s&format=%s&query=%s",
			from.abbreviation, to.abbreviation, format.value, acc );
	}
	
	public static Map<String,List<String>> loadCurrentDatGz( String path, DatabaseType db ) throws FileNotFoundException, IOException {
		if( db == DatabaseType.UniProtKB )
			throw new IllegalArgumentException("Destination database should be different than the databse used as the key");
		Map<String,List<String>> results = new HashMap<>();		
		String acc = null, fields[], line;
		List<String> entries = null;
        BufferedReader rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path))));
        
        while( (line=rd.readLine()) != null ) {
            if( !line.contains(db.id+"\t") )
                continue;
            fields = line.split("\t");
            if( acc != null && !acc.equals(fields[0]) ) {
                results.put(acc, entries);
                acc = null;
            }
            if( acc == null ) {
                acc = fields[0];
                entries = new ArrayList<>();
            }
            entries.add(fields[2]);
        }
        rd.close();
        if( acc != null )
        	results.put(acc, entries);
		
		return results;
	}
}
