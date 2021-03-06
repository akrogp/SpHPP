package org.sphpp.workflow.file;

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
import java.util.logging.Logger;

import org.sphpp.workflow.Constants;
import org.sphpp.workflow.data.Identifiable;
import org.sphpp.workflow.data.ScoreItem;

import es.ehubio.Numbers;
import es.ehubio.io.CsvReader;
import es.ehubio.io.Streams;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class ScoreFile<T extends Identifiable & Decoyable> {
	public ScoreFile( String id ) {
		this.id = id;
	}
	
	public void save(String path, ScoreType... scores) throws FileNotFoundException, IOException {
		save(id, items, path, scores);
	}
	
	public static <T extends Identifiable & Decoyable>
	void save(String id, Collection<T> items, String path, ScoreType... scores) throws FileNotFoundException, IOException {
		logger.info("Saving scores ...");
		boolean[] useScore;
		int last;
		if( scores.length == 0 ) {
			Set<ScoreType> set = new LinkedHashSet<>();
			for( T item : items )
				for( Score score : item.getScores() )
					set.add(score.getType());			
			scores = set.toArray(new ScoreType[0]);
			useScore = new boolean[scores.length];
			for( int i = 0; i < useScore.length; i++ )
				useScore[i] = true;
			last = useScore.length-1;
		} else {
			useScore = new boolean[scores.length];
			T first = items.iterator().next();
			last = 0;			
			for( int i = 0; i < useScore.length; i++ ) {
				useScore[i] = first.getScoreByType(scores[i]) != null;
				if( useScore[i] )
					last = i;
			}
		}
		try( PrintWriter pw = new PrintWriter(Streams.getTextWriter(path))) {
			pw.print(id); pw.print(SEP);
			for( int i = 0; i < last; i++ )
				if( useScore[i] ) {
					pw.print(scores[i].getName()); pw.print(SEP);
				}
			pw.println(scores[last].getName());
			for( T item : items ) {
				pw.print(item.getId()); pw.print(SEP);
				for( int i = 0; i < last; i++ )
					if( useScore[i] ) {
						Score score = item.getScoreByType(scores[i]);
						if( score != null )
							pw.print(Numbers.toString(score.getValue()));
						pw.print(SEP);
					}
				Score score = item.getScoreByType(scores[last]);
				if( score != null )
					pw.print(Numbers.toString(score.getValue()));
				pw.println();
			}
		}
		logger.info(String.format("Saved %d items", items.size()));
	}
	
	public static ScoreFile<ScoreItem> load( String path, ScoreType... scores ) throws IOException, ParseException {
		logger.info("Loading scores ...");
		List<String> types = new ArrayList<>();
		for( int i = 0; i < scores.length; i++ )
			types.add(scores[i].getName());
		try( CsvReader rd = new CsvReader(SEP, true, true) ) {
			rd.open(path);
			ScoreFile<ScoreItem> file = new ScoreFile<>(rd.getHeaderName(0));
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
				if( item.getScores().isEmpty() )
					item.putScore(new Score(ScoreType.OTHER_LARGER, rd.getHeaderName(1), Numbers.parseDouble(rd.getField(1))));
				file.getItems().add(item);
			}
			logger.info(String.format("Loaded %d items", file.getItems().size()));
			return file;
		}
	}
	
	public String getId() {
		return id;
	}
	
	public Set<T> getItems() {
		return items;
	}

	public void setItems(Set<T> items) {
		this.items = items;
	}
	
	public ScoreType selectScore( ScoreType... scores ) {
		return selectScore(items, scores);
	}
	
	public static ScoreType selectScore( Collection<? extends Decoyable> items, ScoreType... scores ) {
		if( items.isEmpty() )
			return null;
		if( scores.length == 0 )
			scores = Constants.SCORES;
		Decoyable item = items.iterator().next();
		for( ScoreType type : scores )
			if( item.getScoreByType(type) != null )
				return type;
		return item.getScores().iterator().next().getType();
	}

	private static final Logger logger = Logger.getLogger(ScoreFile.class.getName());
	private final String id;	
	private Set<T> items;
}
