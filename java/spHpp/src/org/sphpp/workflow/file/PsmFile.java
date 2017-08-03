package org.sphpp.workflow.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.sphpp.workflow.Constants;

import es.ehubio.Numbers;
import es.ehubio.io.CsvReader;
import es.ehubio.io.Streams;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Ptm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.Spectrum;

public class PsmFile {
	public static void save( Set<Psm> psms, String path ) throws FileNotFoundException, IOException {
		ScoreType type = ScoreFile.selectScore(psms);
		logger.info(String.format("Selected '%s' score", type.getName()));
		save(psms, path, type);
	}
	
	public static void save( Set<Psm> psms, String path, ScoreType type ) throws FileNotFoundException, IOException {
		List<Psm> list = new ArrayList<>(psms);
		list.sort(new Comparator<Psm>() {
			@Override
			public int compare(Psm o1, Psm o2) {
				int res = o1.getSpectrum().getUniqueString().compareTo(o2.getSpectrum().getUniqueString());
				if( res == 0 )
					res = o1.getRank()-o2.getRank();
				return res;
			}
		});
		try( PrintWriter pw = new PrintWriter(Streams.getTextWriter(path)) ) {
			pw.print("psm"); pw.print(Constants.SEP);
			pw.print("spectrum"); pw.print(Constants.SEP);
			pw.print("rank"); pw.print(Constants.SEP);
			pw.print("expMass"); pw.print(Constants.SEP);
			pw.print("charge"); pw.print(Constants.SEP);
			pw.print("peptideSequence"); pw.print(Constants.SEP);
			pw.print("peptideMods"); pw.print(Constants.SEP);
			pw.println(type.getName());
			for( Psm psm : list ) {
				pw.print(psm.getUniqueString()); pw.print(Constants.SEP);
				pw.print(psm.getSpectrum().getUniqueString()); pw.print(Constants.SEP);
				//pw.print(psm.getSpectrum().getScan()); pw.print(Constants.SEP);
				//pw.print(String.format("%s@%s", psm.getSpectrum().getFileName(), psm.getSpectrum().getScan())); pw.print(Constants.SEP);
				pw.print(psm.getRank()); pw.print(Constants.SEP);				
				pw.print(Numbers.toString(psm.getExpMz())); pw.print(Constants.SEP);
				pw.print(psm.getCharge()); pw.print(Constants.SEP);
				pw.print(psm.getPeptide().getSequence()); pw.print(Constants.SEP);
				pw.print(psm.getPeptide().getMassSequence()); pw.print(Constants.SEP);
				pw.println(Numbers.toString(psm.getScoreByType(type).getValue()));
			}
		}
	}
	
	public static MsMsData load( String path ) throws IOException, ParseException {
		try(CsvReader rd = new CsvReader(Constants.SEP, true, true) ) {
			Map<String, Spectrum> mapSpectrum = new HashMap<>();
			Map<String, Peptide> mapPeptide = new HashMap<>();
			rd.open(path);
			ScoreType type = null;
			boolean headersReady = false;
			while( rd.readLine() != null ) {
				if( headersReady == false ) {
					headersReady = true;
					type = findScore(rd);					
				}
				String spectrumId = rd.getField("spectrum");
				Spectrum spectrum = mapSpectrum.get(spectrumId);
				if( spectrum == null ) {
					spectrum = new Spectrum();
					spectrum.setUniqueString(spectrumId);
					mapSpectrum.put(spectrumId, spectrum);
				}
				String peptideId = rd.getField("peptideMods");
				Peptide peptide = mapPeptide.get(peptideId);
				if( peptide == null ) {
					peptide = parsePeptide(rd);
					mapPeptide.put(peptideId, peptide);
				}				
				Psm psm = new Psm();
				psm.setRank(rd.getIntField("rank"));
				psm.setExpMz(rd.getDoubleField("expMass"));
				psm.setCharge(rd.getIntField("charge"));
				psm.linkSpectrum(spectrum);
				psm.linkPeptide(peptide);
				if( type != null )
					psm.putScore(new Score(type, Numbers.parseDouble(rd.getField(type.getName()))));
			}
			MsMsData data = new MsMsData();
			data.loadFromSpectra(mapSpectrum.values());
			return data;
		}
	}
	
	private static ScoreType findScore(CsvReader rd) {
		List<String> names = Arrays.asList(rd.getHeaders());
		for( ScoreType type : ScoreType.values() )
			if( names.contains(type.getName()) )
				return type;
		return null;
	}

	private static Peptide parsePeptide(CsvReader rd) throws ParseException {
		Peptide peptide = new Peptide();
		peptide.setSequence(rd.getField("peptideSequence"));
		String modSeq = rd.getField("peptideMods");
		peptide.setUniqueString(modSeq);
		for( int i = 0, aa = -1; i < modSeq.length(); i++ ) {
			if( modSeq.charAt(i) == '(' ) {
				int j = i+1;
				while( j < modSeq.length() && modSeq.charAt(j) != ')' ) j++;
				double mass = Numbers.parseDouble(modSeq.substring(i+1, j));
				i = j;
				Ptm ptm = new Ptm();
				if( aa >= 0 )
					ptm.setPosition(aa+1);
				ptm.setMassDelta(mass);
				peptide.addPtm(ptm);
			} else if( modSeq.charAt(i) == '?' )
				aa=-1;
			else
				aa++;
		}
		return peptide;
	}
	
	private static final Logger logger = Logger.getLogger(PsmFile.class.getName());
}
