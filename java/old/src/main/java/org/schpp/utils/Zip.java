// $Id$

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.schpp.utils;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author gorka
 */
public class Zip {
    public static long zip( String fin ) throws IOException {
        return zip( fin, fin+".zip" );
    }
    
    public static long zip( String fin, String fout ) throws IOException {
        BufferedInputStream rd = new BufferedInputStream(new FileInputStream(fin));
        ZipOutputStream wr = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(fout)));
        ZipEntry ze = new ZipEntry(new File(fin).getName());        
        byte buffer[] = new byte[BUFFER_SIZE];
        int len;
        long count = 0;
        try {
            wr.putNextEntry(ze);
            while( (len=rd.read(buffer)) != -1 ) {
                wr.write(buffer, 0, len);
                //wr.flush();
                count += len;
            }
            wr.close();
        } finally {
            rd.close();
        }
        return count;
    }
    
    private static final int BUFFER_SIZE = 1024 * 1024;
    
    /*public static void main( String[] args ) {
        try {
            zip("/home/gorka/MyProjects/S-CHPP/java/downloads/nextprot_chromosome_16.fasta");
        } catch (IOException ex) {
            Logger.getLogger(Zip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}
