// $Id$

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.schpp.db;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Sheet;
import jxl.Workbook;

/**
 *
 * @author gorka
 */
public class PICR {
    public class Entry {
        String ensp;
        int score;
        
        public Entry( String ensp, int score ) {
            this.ensp = ensp;
            this.score = score;
        }
    }
    
    public class Result extends ArrayList<Entry> {
        public String ensp = null;
        public String ensg = null;
        public int score = 0;
    }
    
    public Map<String, Result> Uniprot2Ensembl( File Xls ) throws Exception {
        Map<String, Result> map = new HashMap<String, Result>();
        Workbook book = Workbook.getWorkbook(Xls);
        String acc = "", status, tmp, ensp;
        int i, score;
        Result res = new Result();
        boolean first = true;
        for( Sheet sheet : book.getSheets() )
            for( i = 1; i < sheet.getRows(); i++ ) {
                tmp = sheet.getCell(0, i).getContents();//.replaceAll("-.*", "");
                if( !tmp.equals(acc) ) { // New accession
                    acc = tmp;
                    if( first )
                        first = false;
                    else 
                        res = new Result();
                    map.put(acc, res);
                }
                ensp = sheet.getCell(2, i).getContents();
                if( ensp.startsWith("ENSG") ) {
                    if( res.ensg == null )
                        res.ensg = ensp;
                    else
                        res.ensg += ";" + ensp;
                    continue;
                }
                if( !ensp.startsWith("ENSP") )
                    continue;                
                status = sheet.getCell(3, i).getContents();
                if( status.equals("identical") ) {
                    score = 3;
                    res.ensp = ensp;
                } else if( status.equals("logical") )
                    score = 2;
                else if( status.equals("deleted") )
                    score = 1;
                else
                    score = 0;
                if( score > res.score )
                    res.score = score;
                if( res.ensp == null )
                    res.ensp = ensp;
                res.add(new Entry(ensp, score));                
            }
        return map;
    }
    
    /*public static void main( String[] args ) {
        try {
            Uniprot2Ensembl(new File("/home/gorka/MyProjects/S-CHPP/java/downloads/picr.xls"));
        } catch (Exception ex) {
            Logger.getLogger(PICR.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}
