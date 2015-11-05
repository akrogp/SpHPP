// $Id: UniProt.java 99 2013-11-14 15:34:08Z gorka.prieto@gmail.com $

package org.schpp.db;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author gorka
 */
public class UniProt {
    public enum MappingFileType { LocalCsv, PreviousKB, CurrentKB };
    
    public UniProt( FastaDBApp app, String chr ) {
        mApp = app;
        mChr = chr;
    }
    
    public static long[] split( String up, String sp, String tr ) throws Exception {
        BufferedReader rd = new BufferedReader(new FileReader(up));
        PrintWriter wr_sp = new PrintWriter( sp );
        PrintWriter wr_tr = new PrintWriter( tr );
        long[] counts = split( rd, wr_sp, wr_tr );
        rd.close();
        wr_sp.close();
        wr_tr.close();
        return counts;
    }

    public static long[] split(BufferedReader rd, PrintWriter wr_sp, PrintWriter wr_tr) throws Exception {
        String str;
        long sp_count = 0, tr_count = 0;
        boolean sp = false;
        while( (str=rd.readLine()) != null ) {            
            if( str.startsWith(">sp") ) {
                sp = true;
                sp_count++;                
            } else if( str.startsWith(">tr") ) {
                sp = false;
                tr_count++;
            } else if( str.charAt(0) == '>' )
                throw new Exception("Unexpected UniProt entry type");
            if( sp )
                wr_sp.println(str);
            else
                wr_tr.println(str);
        }
        wr_sp.flush();
        wr_tr.flush();
        return new long[]{sp_count, tr_count};
    }
    
    public static List<String> getChrEntries( String chr ) throws Exception {
        URL myurl = new URL("ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete/docs/humchr"+chr+".txt");
        URLConnection urlc = myurl.openConnection();
        BufferedReader rd = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
        List<String> list = new ArrayList<String>();
        String line;
        int seps = 0;
        while( (line=rd.readLine()) != null ) {
            if( line.startsWith("___") ) {
                seps++;
                continue;
            }
            if( seps < 2 )
                continue;
            if( line.isEmpty() )
                break;
            line = line.replaceAll(" +", " ");
            list.add(line.split(" ")[2]);
        }
        rd.close();       
        return list;
    }   
    
    public long[] split( BufferedReader rd,
        PrintWriter wr_sp, PrintWriter wr_chr_sp,
        PrintWriter wr_tr, PrintWriter wr_chr_tr) throws Exception {
        
        Entry entry;        
        String str, acc;
        long sp_count = 0, sp_chr_count = 0, tr_count = 0, tr_chr_count = 0, unmapped = 0, total = 0;
        boolean sp = false, chr = false;
        List<String> sp_genes_all = new ArrayList<String>();
        List<String> sp_genes_chr = new ArrayList<String>();
        List<String> tr_genes_all = new ArrayList<String>();
        List<String> tr_genes_chr = new ArrayList<String>();
        while( (str=rd.readLine()) != null ) {
            if( str.charAt(0) == '>' ) {
                total++;
                acc = str.split("\\|")[1];
                if( (entry = mApp.getEntry(acc)) == null )                                
                    unmapped++;
                chr = false;
                if( entry != null && entry.chromosome.equals(mChr) )
                    chr = true;
                               
                // SwissProt vs TrEMBL
                if( str.startsWith(">sp") ) {
                    sp = true;
                    sp_count++;
                    if( chr )
                        sp_chr_count++;
                } else if( str.startsWith(">tr") ) {
                    sp = false;
                    tr_count++;
                    if( chr )
                        tr_chr_count++;
                } else
                    throw new Exception("Unexpected UniProt entry type");
                                
                if( entry != null ) {
                    // New header
                    str = str + " " + entry.toString();
                    // Stats
                    if( sp ) {
                        if( !sp_genes_all.contains(entry.gene) )
                            sp_genes_all.add(entry.gene);
                        if( chr && !sp_genes_chr.contains(entry.gene) )
                            sp_genes_chr.add(entry.gene);
                    } else {
                        if( !tr_genes_all.contains(entry.gene) )
                            tr_genes_all.add(entry.gene);
                        if( chr && !tr_genes_chr.contains(entry.gene) )
                            tr_genes_chr.add(entry.gene);
                    }
                }
            }
            if( sp ) {
                wr_sp.println(str);
                if( chr )
                    wr_chr_sp.println(str);
            } else {
                wr_tr.println(str);
                if( chr )
                    wr_chr_tr.println(str);
            }
        }
        wr_sp.flush(); wr_chr_sp.flush();
        wr_tr.flush(); wr_chr_tr.flush();
        return new long[] {
            sp_count, sp_genes_all.size(), sp_chr_count, sp_genes_chr.size(),
            tr_count, tr_genes_all.size(), tr_chr_count, tr_genes_chr.size(),
            unmapped, total
        };
    }
    
    public static Map<String,String> getEnsemblMapping( String file, MappingFileType type  ) throws Exception {
        switch( type ) {
            case LocalCsv:
                return getEnsemblMappingCsv(file);
            case PreviousKB:
                return getEnsemblMappingPreviousDatGz(file);
            case CurrentKB:
                return getEnsemblMappingCurrentDatGz(file);
        }
        return null;
    }
    
    public static Map<String,String> getEnsemblMappingCsv( String file ) throws Exception {
        Map<String,String> map = new HashMap<String,String>();
        BufferedReader rd = new BufferedReader(new FileReader(file));
        String line, fields[];
        while( (line=rd.readLine()) != null ) {
            fields = line.split(",");
            if( fields.length == 1 )
                map.put(fields[0], "");
            else
                map.put(fields[0], fields[1]);
        }
        rd.close();
        return map;
    }
    
    public static Map<String,String> getEnsemblMappingPreviousDatGz( String file ) throws Exception {
        Map<String,String> map = new HashMap<String,String>();
        String acc = null, ensg = "", fields[], line, tmp;
        boolean human = false;
        BufferedReader rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
        while( (line=rd.readLine()) != null ) {
            if( line.startsWith("AC") ) {
                if( acc != null && human ) {
                    fields=acc.split(";");
                    for( String field : fields )
                        map.put(field.trim(), ensg);
                }
                acc = line.replace("AC", "").trim();
                human = false;
                ensg = "";
            } else if( line.contains("OX   NCBI_TaxID=9606") )
                human = true;
            else if( line.contains("DR   Ensembl") ) {
                tmp = null;
                for( String str : line.split(";") )
                    if( str.contains("ENSG") ) {
                        tmp = str.replace('.', ' ').trim();
                        break;
                    }
                if( tmp != null && !ensg.contains(tmp) )
                    ensg += tmp+";";
            }
        }
        rd.close();
        if( acc != null && human ) {
            fields=acc.split(";");
            for( String field : fields )
                map.put(field.trim(), ensg);
        }
        
        return map;
    }
    
    public static Map<String,String> getEnsemblMappingCurrentDatGz( String file ) throws Exception {
        Map<String,String> map = new HashMap<String,String>();
        String acc = null, ensg = "", fields[], line, tmp;
        
        BufferedReader rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
        while( (line=rd.readLine()) != null ) {
            if( !line.contains("Ensembl\t") )
                continue;
            fields = line.split("\t");            
            if( acc != null && !acc.equals(fields[0]) ) {
                map.put(acc, ensg);
                acc = null;
            }
            if( acc == null ) {
                acc = fields[0];
                ensg = "";
            }
            if( ensg.length() > 0 )
                ensg += ";";
            ensg += fields[2];
        }
        rd.close();
        if( acc != null )
            map.put(acc, ensg);        
        
        return map;
    }
    
    public static void main( String[] args ) {
        if( args.length != 1 ) {
            System.out.println( "Usage:\n\tUniProt <file.dat.gz>" );
            return;
        }
        try {
            Map<String,String> map = getEnsemblMapping(args[0], MappingFileType.PreviousKB);
            for( String acc : map.keySet() )
                System.out.println(acc+","+map.get(acc));
        } catch (Exception ex) {
            Logger.getLogger(UniProt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private FastaDBApp mApp;
    private String mChr;
}
