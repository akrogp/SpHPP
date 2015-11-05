/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.schpp.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author gorka
 */
@Deprecated
public class UP2EnsPicr {
    public static void main( String[] args ) {
        if( args.length != 5 ) {
            System.out.println( "Usage:\n\tUP2Ens <human.ids> <picr.xls> <ensembl.tsv> <manual.txt> <output.csv>" );
            return;
        }
        try {            
            File log = new File(args[4]);            
            UP2EnsPicr map = new UP2EnsPicr( log.getAbsolutePath().replaceAll("\\..*", ".log") );
            map.generateMaps(args[1], args[2], args[3]);
            List<Entry> entries = map.getEntries(args[0]);
            map.saveMap(entries, args[4]);
        } catch (Exception ex) {
            Logger.getLogger(UP2EnsPicr.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }   
    
    public UP2EnsPicr( String log ) throws IOException {        
        mLog = Logger.getLogger(FastaDBApp.class.getName());
        FileHandler fh = new FileHandler(log, false);
        fh.setFormatter(new SimpleFormatter());
        mLog.addHandler(fh);
        mLog.info("Started");
    }
    
    public void generateMaps( String picrXls, String ensemblTsv, String manualTxt ) throws Exception {                
        mapPicr = loadPicrMap(picrXls);
        mapEnsembl = loadEnsemblMap(ensemblTsv);
        mapUniProt = downloadUniProtMap();
        mapManual = loadManualMap(manualTxt);
    }
    
    public void saveMap( List<Entry> entries, String file ) throws IOException {
        PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        wr.println("Protein,Anchor,Gene,Chromosome,Source,Score");
        for( Entry entry : entries )
            wr.println( entry.accession+","+(entry.anchor?1:0)+","+entry.gene+","+entry.chromosome+","+entry.source+","+entry.score );
        wr.close();
    }
    
    public List<Entry> getEntries( String uniprotTxt ) throws Exception {
        List<Entry> entries = new ArrayList<Entry>();
        Map<String,List<Entry>> map = new HashMap<String, List<Entry>>();
        Entry entry;
        List<String> ids = loadUniProtIds(uniprotTxt);
        for( String id : ids ) {
            entry = getEntry(id);
            if( entry != null ) {
                entries.add(entry);
                if( !map.containsKey(entry.gene) ) {
                    List<Entry> tmp = new ArrayList<Entry>();
                    tmp.add(entry);
                    map.put(entry.gene, tmp);
                } else
                    map.get(entry.gene).add(entry);
            }
        }
        for( String ensg : map.keySet() ) {
            Collections.sort( map.get(ensg),
                new Comparator<Entry>(){
                    @Override
                    public int compare(Entry e1, Entry e2) {
                        return e1.accession.compareTo(e2.accession);
                    }
                }
            );
            map.get(ensg).get(0).anchor = true;
        }
        return entries;
    }   
    
    // UniProt -> ENSG
    public Entry getEntry( String acc ) {
        Entry entry = tryEntry(acc);        
        if( entry != null || acc.indexOf("-") == -1 )
            return entry;
        
        mLog.log(Level.WARNING, "... but trying alternative product");
        String alt = acc.replaceAll("-.*", "");
        entry = tryEntry(alt);
        if( entry != null ) {
            entry.accession = acc;
            mLog.log(Level.WARNING, "... used {0} information for {1}", new Object[]{alt, acc});
        }
        
        return entry;
    }
    
    private Entry tryEntry( String acc ) {
        PICR.Result picr;
        Entry partial;        
        Entry entry = new Entry();
        entry.accession = acc;
        
        // 1. PICR (UniProt -> Ensembl)
        if( (picr=mapPicr.get(acc)) == null )
            mLog.log(Level.WARNING, "Ensembl mapping for {0} not available in PICR", acc);
        else {        
            // 2. Ensembl
            entry.protein = picr.ensp;
            entry.gene = picr.ensg;
            entry.source = "PICR";
            entry.score = picr.score;
            if( (partial=mapEnsembl.get(picr.ensp)) == null )
                for( PICR.Entry pe : picr )
                    if( (partial=mapEnsembl.get(pe.ensp)) != null ) {
                        entry.protein = pe.ensp;
                        entry.score = pe.score;
                        break;
                    }
            if( partial != null ) {
                entry.update(partial);
                return entry;
            }
            mLog.log(Level.WARNING, "No chromosome info found in Ensembl for {0}",
                picr.ensp != null ? (picr.ensp + " (" + acc + ")") : acc );
        }
        
        // 3. Manual
        partial = mapManual.get(acc);
        if( partial != null ) {
            mLog.log(Level.WARNING, "... but curated manually");
            entry.source = "Manual";
            entry.score = 3;
            entry.update(partial);
            return entry;
        }
        
        // 4. UniProt
        for( String chr : mapUniProt.keySet() )
            if( mapUniProt.get(chr).contains(acc) ) {
                mLog.log(Level.WARNING, "... but included in chromosome {0} using UniProt information", chr);
                entry.source = "UniProt";
                entry.score = 3;
                entry.chromosome = chr;
                return entry;
            }
        
        return null;
    }        
    
    // UniProt IDs
    private List<String> loadUniProtIds( String uniprotIds ) throws IOException {
        List<String> list = new ArrayList<String>();
        BufferedReader rd = new BufferedReader(new FileReader(uniprotIds));        
        String line;
        while( (line=rd.readLine()) != null )
            list.add(line);
        rd.close();
        return list;
    }
    
    // UniProt - Ensembl protein map (using PICR)
    private Map<String, PICR.Result> loadPicrMap( String picrXls ) throws Exception {
        File xls = new File(picrXls);
        if( !xls.isFile() )
            throw new IOException( "Failed to locate PICR mapping file: " + xls.getAbsolutePath() );
        PICR picr = new PICR();
        Map<String, PICR.Result> map = picr.Uniprot2Ensembl(xls);
        mLog.log(Level.INFO, "Mapped {0} UniProt accessions to Ensembl using PICR file", map.size() );
        return map;
    }
    
    // Protein - Gene, Transcript, Chromosome (using Ensembl)
    private Map<String, Entry> loadEnsemblMap( String ensemblTsv ) throws IOException {
        Ensembl ensembl = new Ensembl();
        Map<String, Entry> map = ensembl.getProteinMap(ensembl.getMap(ensemblTsv));
        mLog.log(Level.INFO, "Loaded information for {0} Ensembl proteins", map.size() );
        return map;
    }
    
    // UniProt chromosome information
    private Map<String,List<String>> downloadUniProtMap() throws Exception {
        Map<String,List<String>> map = new HashMap<String, List<String>>();
        for( int i = 1; i < 22; i++ ) {
            map.put(""+i, UniProt.getChrEntries(String.format("%02d", i)));
            mLog.log(Level.INFO, "Downloaded UniProt chromosome {0} information for {1} proteins", new Object[]{i, map.get(""+i).size()} );
        }
        map.put("X", UniProt.getChrEntries("x"));
        mLog.log(Level.INFO, "Downloaded UniProt chromosome {0} information for {1} proteins", new Object[]{"X", map.get("X").size()} );
        map.put("Y", UniProt.getChrEntries("y"));
        mLog.log(Level.INFO, "Downloaded UniProt chromosome {0} information for {1} proteins", new Object[]{"Y", map.get("Y").size()} );
        return map;
    }
    
    // Manual curation
    private Map<String,Entry> loadManualMap( String file ) {
        Map<String,Entry> map = new HashMap<String, Entry>();
        try {
            BufferedReader rd = new BufferedReader(new FileReader(file));
            String str;
            String[] fields;
            Entry pm;
            rd.readLine();  // Skip header
            while( (str=rd.readLine()) != null ) {
                fields = str.split(",");
                if( fields[2].charAt(0) == 'x' )
                    continue;
                pm = new Entry();
                pm.chromosome = fields[3];
                pm.gene = fields[2];
                map.put(fields[0], pm);
            }
            rd.close();
        } catch( Exception ex ) {
        }
        mLog.log(Level.INFO, "Loaded manually curated information for {0} proteins", map.size() );
        return map;
    }
    
    private Logger mLog;
    Map<String, PICR.Result> mapPicr;
    Map<String, Entry> mapEnsembl;
    Map<String,List<String>> mapUniProt;
    Map<String,Entry> mapManual;
}
