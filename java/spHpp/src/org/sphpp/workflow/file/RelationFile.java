package org.sphpp.workflow.file;

import static org.sphpp.workflow.Constants.SEP;
import static org.sphpp.workflow.Constants.SUB_SEP;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.data.Link;
import org.sphpp.workflow.data.LinkList;
import org.sphpp.workflow.data.Relation;

import es.ehubio.Numbers;
import es.ehubio.io.CsvReader;
import es.ehubio.io.CsvUtils;
import es.ehubio.io.Streams;

public class RelationFile {
	public RelationFile( String upperLabel, String lowerLabel ) {
		this.lowerLabel = lowerLabel;
		this.upperLabel = upperLabel;
	}
	
	public static RelationFile load( String path ) throws IOException {
		return load(path, null);
	}
	
	public static RelationFile load( String path, String discard ) throws IOException {
		try(CsvReader reader = new CsvReader(SEP, true)) {
			long count = 0;
			reader.open(path);
			RelationFile relations = new RelationFile(reader.getHeaderName(0), reader.getHeaderName(1));
			while(reader.readLine()!=null) {
				if( discard != null && reader.getLine().contains(discard) ) {
					count++;
					continue;
				}
				Relation rel = new Relation(reader.getField(0), reader.getField(1));
				if( reader.getFields().length > 2 )
					if( !reader.getField(2).isEmpty() )
						rel.getLabels().addAll(Arrays.asList(reader.getField(2).split(SUB_SEP)));
				if( reader.getFields().length > 3 )
					if( !reader.getField(2).isEmpty() )
						try {
							rel.setCoeficient(Numbers.parseDouble(reader.getField(3)));
						} catch (ParseException e) {
							log.warning(e.getMessage());
						}
				relations.entries.add(rel);				
			}
			log.info(String.format("Loaded %d relations", relations.entries.size()));
			if( count != 0 )
				log.info(String.format("Discarded %d relations", count));
			return relations;
		}
	}
	
	public void save( String path ) throws IOException {
		try(PrintWriter pw = new PrintWriter(Streams.getTextWriter(path))) {
			pw.print(getUpperLabel());
			pw.print(SEP);
			pw.println(getLowerLabel());
			for( Relation rel : getEntries() ) {
				pw.print(rel.getUpperId());
				pw.print(SEP);
				pw.print(rel.getLowerId());
				if( rel.getCoeficient() != null || !rel.getLabels().isEmpty() ) {
					pw.print(SEP);
					if( !rel.getLabels().isEmpty() )
						pw.print(CsvUtils.getCsv(SUB_SEP, rel.getLabels().toArray()));
					if( rel.getCoeficient() != null ) {
						pw.print(SEP);
						pw.print(Numbers.toString(rel.getCoeficient()));
					}
				}
				pw.println();
			}
			log.info(String.format("Saved %d relations", entries.size()));
		}
	}

	public String getLowerLabel() {
		return lowerLabel;
	}

	public String getUpperLabel() {
		return upperLabel;
	}	
	
	public Set<Relation> getEntries() {
		return entries;
	}
	
	public boolean addEntry( Relation rel ) {
		return entries.add(rel);
	}
		
	public LinkList<Link<Void,Void>,Link<Void,Void>> getLinks() {
		LinkList<Link<Void,Void>,Link<Void,Void>> map = new LinkList<>();
		for( Relation rel : getEntries() ) {
			Link<Void,Void> upper = new Link<>(rel.getUpperId());
			Link<Void,Void> lower = new Link<>(rel.getLowerId());
			map.addLink(upper, lower);
		}
		return map;
	}

	private final String lowerLabel;
	private final String upperLabel;
	private final Set<Relation> entries = new LinkedHashSet<>();
	private static final Logger log = Logger.getLogger(RelationFile.class.getName());
}