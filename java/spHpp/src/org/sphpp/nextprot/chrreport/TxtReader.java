package org.sphpp.nextprot.chrreport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class TxtReader {
	public static List<Entry> readFile( String report ) throws IOException {
		List<Entry> entries = new ArrayList<>();
		BufferedReader rd = new BufferedReader(new FileReader(report));
		String line, chr = "?", release="?";		
		String[] fields;
		Entry entry;
		while( (line=rd.readLine()) != null )
			if( line.startsWith("Name:") ) {
				chr = line.split("_")[2];
				line = rd.readLine();
				release = line.split(":")[1].trim();
				break;
			}
		while( (line=rd.readLine()) != null )
			if( line.startsWith("__") )
				break;
		while( (line=rd.readLine()) != null ) {
			if( line.startsWith("__") )
				break;
			//System.out.println(line);
			fields = line.replaceAll("\\t", " ").replaceAll(" +", " ").split(" ");
			entry = new Entry();			
			entry.setChromosome(chr);
			entry.setRelease(release);
			int i = 0;
			entry.setGene(fields[i++]);
			entry.setProtein(fields[i++]);
			entry.setPosition(fields[i++]);
			if( fields[i].equals("-") && !fields[i+1].equals("-") ) {
				i++;
				entry.setPosition(entry.getPosition()+"-"+fields[i++]);
			}
			entry.setStart(fields[i++]);
			entry.setStop(fields[i++]);
			entry.setExistence(fields[i++]);
			if( fields[i].contains("level") )
				i++;
			entry.setProteomics(parseBoolean(fields[i++]));
			entry.setAntibody(parseBoolean(fields[i++]));
			entry.setThreeD(parseBoolean(fields[i++]));
			entry.setDisease(parseBoolean(fields[i++]));
			entry.setIsoforms(Integer.parseInt(fields[i++]));
			entry.setVariants(Integer.parseInt(fields[i++]));
			entry.setPTMs(Integer.parseInt(fields[i++]));
			StringBuilder desc = new StringBuilder(fields[i++]);
			for( int j = i; j < fields.length; j++ )
				desc.append(" "+fields[j]);
			entry.setDescription(desc.toString());
			entries.add(entry);
		}
		rd.close();
		return entries;
	}
	
	private static boolean parseBoolean( String string ) {
		if( string.equalsIgnoreCase("yes") )
			return true;
		return false;
	}
	
	public static List<Entry> readDirectory( String path ) throws IOException {
		File directory = new File(path);
		if( !directory.exists() || !directory.isDirectory() )
			throw new IOException("Not a valid directory");
		FilenameFilter filter = new FilenameFilter() {			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("nextprot_chromosome_") && name.endsWith(".txt");
			}
		};
		List<Entry> entries = new ArrayList<>();
		for( File file : directory.listFiles(filter) ) {
			//System.out.println(file.getAbsolutePath());
			entries.addAll(readFile(file.getAbsolutePath()));
		}
		return entries;
	}
}
