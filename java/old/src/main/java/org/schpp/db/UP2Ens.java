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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gorka
 */
public class UP2Ens {
    public static void main( String[] args ) {
        if( args.length % 2 != 0  ) {
            showUsage( "Incorrect number of parameters" );
            return;
        }
        String ids = null, ensembl = null, uniprot = null , picr = null, output = null;
        for( int i = 0; i < args.length; i += 2 ) {
            switch( args[i] ) {
                case "-i":
                    ids = args[i+1]; break;
                case "-e":
                    ensembl = args[i+1]; break;
                case "-u":
                    uniprot = args[i+1]; break;
                case "-p":
                    picr = args[i+1]; break;
                case "-o":
                    output = args[i+1]; break;
                default:
                    showUsage("Option '" + args[i] + "' not recognized");
                    return;
            }
        }
        if( ids == null || ensembl == null || uniprot == null || output == null ) {
            showUsage("Mandatory parameters missing");
            return;
        }
        try {                        
            UP2Ens map = new UP2Ens();
            map.loadMaps(ensembl, uniprot, picr);
            List<Entry> entries = map.getEntries(ids);
            map.saveMap(entries, output);
        } catch (Exception ex) {
            Logger.getLogger(UP2Ens.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void showUsage( String desc ) {
        Logger.getLogger(UP2Ens.class.getName()).log(Level.SEVERE, "Error: {0}", desc);
        System.out.println("Usage:\n\tUP2Ens -i <human.ids> -e <ensembl-vXX.tsv> -u <HUMAN_9606_idmapping.dat.gz> [-p <picr.xls>] -o <output.csv>");
    }
    
    public UP2Ens() {
        mLog = Logger.getLogger(FastaDBApp.class.getName());
    }

    private void loadMaps(String ensemblCsv, String uniprotGz, String picrXls) throws Exception {
        Ensembl ensembl = new Ensembl();
        List<Entry> list = ensembl.getMap(ensemblCsv,"\t",true);
        mapEnsemblGene = ensembl.getGeneMap(list);
        mLog.log(Level.INFO, "Loaded {0} Ensembl mappings", list.size() );
        
        mapUniProt = UniProt.getEnsemblMapping(uniprotGz, UniProt.MappingFileType.CurrentKB);
        mLog.log(Level.INFO, "Loaded {0} UniProt mappings", mapUniProt.size() );
        
        if( picrXls != null ) {
            mapEnsemblProtein = ensembl.getProteinMap(list);
            PICR picr = new PICR();
            mapPicr = picr.Uniprot2Ensembl(new File(picrXls));
            mLog.log(Level.INFO, "Loaded {0} PICR mappings", mapPicr.size() );
        }                
    }
    
    private List<String> loadUniProtIds( String uniprotIds ) throws IOException {
        List<String> list = new ArrayList<String>();
        BufferedReader rd = new BufferedReader(new FileReader(uniprotIds));        
        String line;
        while( (line=rd.readLine()) != null )
            list.add(line);
        rd.close();
        return list;
    }

    private List<Entry> getEntries(String fileIds) throws IOException {
        List<String> ids = loadUniProtIds(fileIds);
        List<Entry> entries = new ArrayList<Entry>();
        
        long mappedCount = 0;
        long totalCount = ids.size();
        for( String acc : ids ) {
            Entry entry = new Entry();
            entry.accession = acc;            
            entries.add(entry);
            acc = acc.replaceAll("-.*", "");
            if( mapUniProt.containsKey(acc) ) {
                entry.source = "UniProt";
                entry.score = 3;
                entry.gene = mapUniProt.get(acc);
            } else if( mapPicr != null && mapPicr.containsKey(acc) ) {
                PICR.Result picr = mapPicr.get(acc);                
                int score = 0;
                if( picr.ensg != null ) {                    
                    entry.gene = picr.ensg;
                    score = 2;
                } else if( picr.ensp != null && mapEnsemblProtein.containsKey(picr.ensp) ) {
                    entry.gene = mapEnsemblProtein.get(picr.ensp).gene;
                    score = 1;
                }
                if( entry.gene != null ) {
                    entry.source = "PICR";
                    entry.score = score;
                }
            }
            if( entry.gene == null )
                continue;
            mappedCount++;
            entry.chromosome = "";            
            for( String gene : entry.gene.split(";") ) {
                if( !mapEnsemblGene.containsKey(gene) )
                    continue;
                Entry ens = mapEnsemblGene.get(gene);
                if( ens.chromosome != null && !entry.chromosome.contains(ens.chromosome+";") )
                    entry.chromosome += ens.chromosome + ";";
            }
            entry.chromosome = entry.chromosome.replaceAll(";$", "");
        }
        
        mLog.log(Level.INFO, "Mapped {0} of {1} entries", new Object[]{mappedCount, totalCount});
        
        return entries;
    }

    public void saveMap( List<Entry> entries, String file ) throws IOException {
        PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        wr.println("Protein,Anchor,Gene,Chromosome,Source,Score");
        for( Entry entry : entries )
            wr.println( entry.accession+","+(entry.anchor?1:0)+","+entry.gene+","+entry.chromosome+","+entry.source+","+entry.score );
        wr.close();
    }
    
    private Logger mLog;
    Map<String,Entry> mapEnsemblGene;
    Map<String,Entry> mapEnsemblProtein = null;
    Map<String,String> mapUniProt;
    Map<String, PICR.Result> mapPicr = null;
}
