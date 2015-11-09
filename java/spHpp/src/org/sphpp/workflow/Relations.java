package org.sphpp.workflow;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import es.ehubio.io.CsvReader;
import es.ehubio.io.CsvUtils;
import es.ehubio.io.Streams;

public class Relations {
	public static class Relation {
		private final String upperId;
		private final String lowerId;
		private final String id;
		private final Set<String> labels = new LinkedHashSet<String>();
		private Double coeficient;
		public Relation( String upperId, String lowerId ) {
			this.upperId = upperId;
			this.lowerId = lowerId;
			id = String.format("%s->%s", lowerId, upperId);
		}
		@Override
		public boolean equals(Object obj) {
			if( !Relation.class.isInstance(obj) )
				return false;
			return ((Relation)obj).id.equals(id);
		}
		@Override
		public int hashCode() {
			return id.hashCode();
		}
		public String getUpperId() {
			return upperId;
		}
		public String getLowerId() {
			return lowerId;
		}
		public Set<String> getLabels() {
			return labels;
		}
		public boolean addLabel( String label ) {
			return labels.add(label);
		}
		public Double getCoeficient() {
			return coeficient;
		}
		public void setCoeficient(Double coeficient) {
			this.coeficient = coeficient;
		}
	}
	
	public Relations( String upperLabel, String lowerLabel ) {
		this.lowerLabel = lowerLabel;
		this.upperLabel = upperLabel;
	}
	
	public static Relations load( String path ) throws IOException {
		try(CsvReader reader = new CsvReader(SEP, true)) {
			reader.open(path);
			Relations relations = new Relations(reader.getHeaderName(0), reader.getHeaderName(1));
			while(reader.readLine()!=null) {
				Relation rel = new Relation(reader.getField(0), reader.getField(1));
				if( reader.getFields().length > 2 )
					if( !reader.getField(2).isEmpty() )
						rel.labels.addAll(Arrays.asList(reader.getField(2).split(SUB_SEP)));
				if( reader.getFields().length > 3 )
					if( !reader.getField(2).isEmpty() )
						try {
							rel.setCoeficient(NumberFormat.getInstance(Locale.ENGLISH).parse(reader.getField(3)).doubleValue());
						} catch (ParseException e) {
							log.warning(e.getMessage());
						}
				relations.entries.add(rel);				
			}
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
						pw.print(String.format(Locale.ENGLISH, "%f", rel.getCoeficient()));
					}
				}
				pw.println();
			}
		}
	}

	public String getLowerLabel() {
		return lowerLabel;
	}

	public String getUpperLabel() {
		return upperLabel;
	}	
	
	public List<Relation> getEntries() {
		return entries;
	}
	
	public void addEntry( Relation rel ) {
		entries.add(rel);
	}
	
	private static final String SEP = "\t";
	private static final String SUB_SEP = ",";
	private final String lowerLabel;
	private final String upperLabel;
	private final List<Relation> entries = new ArrayList<>();
	private static final Logger log = Logger.getLogger(Relations.class.getName());
}