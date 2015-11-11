package org.sphpp.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;

public class ProbIterator {
	public static void main( String[] args ) {
		ProbIterator app = new ProbIterator();
		app.run(0.005, 100);
	}
	
	private int run( double epsilon, int maxIters ) {
		loadPeptides();
		loadProteins();
		initFactors();
		System.out.println("Start");
		showFactors();
		int iteration = 0;
		while( iteration < maxIters && updateScores(epsilon) ) {
			System.out.println(String.format("\nIteration = %d",++iteration));
			showFactors();			
		}
		return iteration;
	}

	private boolean updateScores( double epsilon ) {
		updateFactors();
		
		boolean changed = false;		
		for( Protein protein : proteins ) {
			double newScore = 0.0;
			for( Peptide peptide : protein.getPeptides() )
				newScore += mapFactors.get(protein).get(peptide)*peptide.getScoreByType(ScoreType.LPP_SCORE).getValue();
			Score score = protein.getScoreByType(ScoreType.LPQCORR_SCORE);			
			if( !changed && Math.abs(newScore-score.getValue()) > epsilon )
				changed = true;
			score.setValue(newScore);
		}
		
		return changed;
	}

	private void updateFactors() {
		for( Protein protein : proteins ) {
			double num = protein.getScoreByType(ScoreType.LPQCORR_SCORE).getValue();
			for( Peptide peptide : protein.getPeptides() ) {
				double den = 0.0;
				for( Protein protein2 : peptide.getProteins() )
					den += protein2.getScoreByType(ScoreType.LPQCORR_SCORE).getValue();				
				mapFactors.get(protein).put(peptide, num/den);
			}
		}
	}

	private void showFactors() {
		System.out.print("\t\t");
		for( Peptide peptide : peptides )
			System.out.print(String.format("%6s", peptide.getName()));
		System.out.println();
		for( Protein protein : proteins ) {
			System.out.print(String.format("Prot. %s = %.2f:\t", protein.getAccession(), protein.getScoreByType(ScoreType.LPQCORR_SCORE).getValue()));
			for( Peptide peptide : peptides ) {
				Double score = mapFactors.get(protein).get(peptide);
				if( score == null )
					System.out.print(String.format("%6s", ""));
				else
					System.out.print(String.format("%6.2f", score));
			}
			System.out.println();
		}
		
	}

	private void initFactors() {
		for( Protein protein : proteins ) {
			Map<Peptide,Double> scores = new HashMap<>();
			for( Peptide peptide : protein.getPeptides() )
				scores.put(peptide, 1.0/peptide.getProteins().size());
			mapFactors.put(protein, scores);
		}
	}

	private void loadProteins() {
		Map<Character, Peptide> mapPeptides = new HashMap<>();
		for( Peptide peptide : peptides )
			mapPeptides.put(peptide.getSequence().charAt(0), peptide);
		
		String[] seqs = {"ab", "bcd", "bcd", "def", "ef", "bd"};
		char ch = 'A';
		for( String seq : seqs ) {
			Protein protein = new Protein();
			protein.setAccession(ch+"");
			protein.setSequence(seq);
			double score = 0.0;
			char[] peptides = seq.toCharArray();
			for( char p : peptides ) {
				Peptide peptide = mapPeptides.get(p);
				protein.linkPeptide(peptide);
				score += peptide.getScoreByType(ScoreType.LPP_SCORE).getValue();
			}
			protein.putScore(new Score(ScoreType.LPQCORR_SCORE, score));
			proteins.add(protein);
			ch++;			
		}
	}

	private void loadPeptides() {
		for( char ch = 'a'; ch <= 'f'; ch++ ) {
			Peptide peptide = new Peptide();
			peptide.setName(ch+"");
			peptide.setSequence(ch+"");
			peptide.putScore(new Score(ScoreType.LPP_SCORE, 3.0));
			peptides.add(peptide);
		}		
	}

	private final List<Protein> proteins = new ArrayList<>();
	private final List<Peptide> peptides = new ArrayList<>();
	private final Map<Protein, Map<Peptide,Double>> mapFactors = new HashMap<>();
}
