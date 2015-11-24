package org.sphpp.workflow.file;

import static org.sphpp.workflow.Constants.SEP;
import static org.sphpp.workflow.Constants.SUB_SEP;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.logging.Logger;

import org.sphpp.workflow.data.Relation;
import org.sphpp.workflow.data.Relations;

import es.ehubio.Numbers;
import es.ehubio.io.CsvReader;
import es.ehubio.io.CsvUtils;
import es.ehubio.io.Streams;

public class RelationFile extends Relations {
	public RelationFile( String upperLabel, String lowerLabel ) {
		this.lowerLabel = lowerLabel;
		this.upperLabel = upperLabel;
	}
	
	public static RelationFile load( String path ) throws IOException {
		return load(path, null, null);
	}
	
	public static RelationFile load( String path, String discard ) throws IOException {
		return load(path, discard, null);
	}
	
	public static RelationFile load( String path, String discard, String prefix ) throws IOException {
		try(CsvReader reader = new CsvReader(SEP, true)) {
			long count = 0;
			reader.open(path);
			RelationFile relations = new RelationFile(reader.getHeaderName(0), reader.getHeaderName(1));
			while(reader.readLine()!=null) {
				if( discard != null && reader.getLine().contains(discard) ) {
					count++;
					continue;
				}
				Relation rel;
				if( prefix == null )
					rel = new Relation(reader.getField(0), reader.getField(1));
				else
					rel = new Relation(prefix+reader.getField(0), prefix+reader.getField(1));
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
				relations.addEntry(rel);				
			}
			log.info(String.format("Loaded %d relations", relations.getEntries().size()));
			if( count != 0 )
				log.info(String.format("Discarded %d relations", count));
			return relations;
		}
	}
	
	public void save( String path ) throws IOException {
		save(this, path);
	}
	
	public void save( Relations rels, String path ) throws IOException {
		save(getUpperLabel(), getLowerLabel(), rels, path);
	}
	
	public static void save( String upperLabel, String lowerLabel, Relations rels, String path ) throws IOException {
		try(PrintWriter pw = new PrintWriter(Streams.getTextWriter(path))) {
			pw.print(upperLabel);
			pw.print(SEP);
			pw.print(lowerLabel);
			if( rels.hasLabels() || rels.hasCoeficients() ) {
				pw.print(SEP);
				pw.print("labels");
				if( rels.hasCoeficients() ) {
					pw.print(SEP);
					pw.print("coeficient");
				}
			}
			pw.println();
			for( Relation rel : rels.getEntries() ) {
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
			log.info(String.format("Saved %d relations", rels.getEntries().size()));
		}
	}

	public String getLowerLabel() {
		return lowerLabel;
	}

	public String getUpperLabel() {
		return upperLabel;
	}		

	private final String lowerLabel;
	private final String upperLabel;
	private static final Logger log = Logger.getLogger(RelationFile.class.getName());
}