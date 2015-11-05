package org.sphpp.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.db.dbptm.Entry;
import es.ehubio.db.dbptm.TxtReader;
import es.ehubio.tools.Command.Interface;

public class PtmChrReport implements Interface {
	@Override
	public String getUsage() {
		return "</path/to/nextprot_chromosome_16.txt>";
	}

	@Override
	public int getMinArgs() {
		return 1;
	}

	@Override
	public int getMaxArgs() {
		return 1;
	}

	@Override
	public void run(String[] args) throws IOException {
		System.out.println( "Loading PTMs ..." );
		List<Entry> ptms = TxtReader.readFile("/media/data/Sequences/dbPTM/dbPTM3.txt");
		System.out.println( "Loading proteins ..." );
		List<org.sphpp.nextprot.chrreport.Entry> prots = org.sphpp.nextprot.chrreport.TxtReader.readFile(args[0]);
		Map<String,Integer> map = new HashMap<>();
		for( org.sphpp.nextprot.chrreport.Entry prot : prots ) {
			System.out.println("Cheking " + prot.getProtein() + " ...");
			for( Entry ptm : ptms ) {
				if( !ptm.getAccession().equals(prot.getProtein().replaceFirst("NX_", "")) )
					continue;
				String type = ptm.getType();
				if( !map.containsKey(type) )
					map.put(type, 1);
				else
					map.put(type, map.get(type)+1);
			}
		}
		for( String type : map.keySet() )
			System.out.println(type + ":" + map.get(type));
	}
}
