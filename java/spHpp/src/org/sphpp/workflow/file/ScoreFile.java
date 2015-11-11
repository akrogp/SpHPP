package org.sphpp.workflow.file;

import static org.sphpp.workflow.Constants.SCORES;
import static org.sphpp.workflow.Constants.SEP;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.sphpp.workflow.Constants;
import org.sphpp.workflow.data.ScoreItem;

import es.ehubio.Numbers;
import es.ehubio.io.CsvReader;
import es.ehubio.io.Streams;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class ScoreFile {
	public ScoreFile( String id ) {
		this.id = id;
	}
	
	public void save(String path, ScoreType... scores) throws FileNotFoundException, IOException {
		save(id, items, path, scores);
	}
	
	public void save(Collection<ScoreItem> items, String path, ScoreType... scores) throws FileNotFoundException, IOException {
		save(id, items, path, scores);
	}
	
	public static void save(String id, Collection<ScoreItem> items, String path, ScoreType... scores) throws FileNotFoundException, IOException {
		if( scores.length == 0 )
			scores = SCORES;
		ScoreItem first = items.iterator().next();
		boolean[] useScore = new boolean[scores.length];
		int last = 0;
		for( int i = 0; i < useScore.length; i++ ) {
			useScore[i] = first.getScoreByType(scores[i]) != null;
			if( useScore[i] )
				last = i;
		}
		try( PrintWriter pw = new PrintWriter(Streams.getTextWriter(path))) {
			pw.print(id); pw.print(SEP);
			for( int i = 0; i < last; i++ )
				if( useScore[i] ) {
					pw.print(scores[i].getName()); pw.print(SEP);
				}
			pw.println(scores[last].getName());
			for( ScoreItem item : items ) {
				pw.print(item.getId()); pw.print(SEP);
				for( int i = 0; i < last; i++ )
					if( useScore[i] ) {
						Score score = item.getScoreByType(scores[i]);
						if( score != null )
							pw.print(score.getValue());
						pw.print(SEP);
					}
				Score score = item.getScoreByType(scores[last]);
				if( score != null )
					pw.print(score.getValue());
				pw.println();
			}
		}
	}
	
	public static ScoreFile load( String path, ScoreType... scores ) throws IOException, ParseException {
		List<String> types = new ArrayList<>();
		for( int i = 0; i < scores.length; i++ )
			types.add(scores[i].getName());
		try( CsvReader rd = new CsvReader(SEP, true, true) ) {
			rd.open(path);
			ScoreFile file = new ScoreFile(rd.getHeaderName(0));
			file.items = new LinkedHashSet<>();
			while( rd.readLine() != null ) {
				ScoreItem item = new ScoreItem(rd.getField(0));
				for( int i = 1; i < rd.getFields().length; i++ ) {
					if( !types.isEmpty() && !types.contains(rd.getHeaderName(i)) )
						continue;
					ScoreType type = ScoreType.getByName(rd.getHeaderName(i));
					if( type == null )
						continue;
					item.putScore(new Score(type,Numbers.parseDouble(rd.getField(i))));
				}
				file.getItems().add(item);
			}
			return file;
		}
	}
	
	public String getId() {
		return id;
	}
	
	public Set<ScoreItem> getItems() {
		return items;
	}

	public void setItems(Set<ScoreItem> items) {
		this.items = items;
	}
	
	public static ScoreType selectScore( Collection<? extends Decoyable> items ) {
		if( items.isEmpty() )
			return null;
		Decoyable item = items.iterator().next();
		for( ScoreType type : Constants.SCORES )
			if( item.getScoreByType(type) != null )
				return type;
		return item.getScores().iterator().next().getType();
	}

	private final String id;	
	private Set<ScoreItem> items;
}
