package org.sphpp.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.sphpp.nextprot.chrreport.Entry;
import org.sphpp.nextprot.chrreport.TxtReader;

import es.ehubio.tools.Command.Interface;

public class Matcher implements Interface {
	@Override
	public String getUsage() {
		return "<acc.txt> </path/to/nextprot_directory>";
	}

	@Override
	public int getMinArgs() {
		return 2;
	}

	@Override
	public int getMaxArgs() {
		return 2;
	}

	@Override
	public void run(String[] args) throws IOException {
		String file = args[0];
		String dir = args[1];
		List<Entry> entries = TxtReader.readDirectory(dir);
		BufferedReader rd = new BufferedReader(new FileReader(file));
		String line, acc;
		boolean found;
		System.out.println("Accesion:Chromosome:Missing");
		while( (line=rd.readLine()) != null ) {
			acc = line.trim().replaceAll("-.*", "");
			found = false;
			for( Entry entry : entries )
				if( entry.getProtein().contains(acc) ) {
					System.out.println(line+":"+entry.getChromosome()+":"+entry.isMissing());
					found = true;
					break;
				}
			if( !found )
				System.out.println(line+":?:?");
		}
		rd.close();		
	}

}
