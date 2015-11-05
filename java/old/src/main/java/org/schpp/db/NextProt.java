// $Id: NextProt.java 99 2013-11-14 15:34:08Z gorka.prieto@gmail.com $

package org.schpp.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 *
 * @author gorka
 */
public class NextProt {
    public NextProt( FastaDBApp app ) {
        mApp = app;
    }
    
    public static long peff2Fasta( String peff, String fasta ) throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(peff));
        PrintWriter wr = new PrintWriter(fasta);
        long count = peff2Fasta( rd, wr );
        rd.close();
        wr.close();
        return count;
    }
    
    public static long peff2Fasta( BufferedReader rd, PrintWriter wr ) throws IOException {        
        String str;
        while( (str=rd.readLine()) != null )
            if( str.charAt(0) == '>' )
                break;
        if( str == null )
            return 0;
        long count = 0;
        do {
            wr.println(str);
            //wr.flush();
            if( str.charAt(0) == '>' )
                count++;
        } while( (str=rd.readLine()) != null );
        return count;
    }
    
    public long peff2Fasta( BufferedReader rd, PrintWriter wr, String chr ) throws IOException {        
        String str, acc;
        Entry entry;
        while( (str=rd.readLine()) != null )
            if( str.charAt(0) == '>' )
                break;
        if( str == null )
            return 0;
        long count = 0;
        do {
            if( str.charAt(0) == '>' ) {
                count++;
                str = str.replaceFirst(" ", "|").replaceFirst("nxp:", "np|").replaceAll("NX_", "");
                acc=str.split("\\|")[1];
                entry = mApp.getEntry(acc);
                if( entry != null ) {
                    entry.chromosome = chr;
                    str = str + " " + entry.toString();
                }
            }
            wr.println(str);
            //wr.flush();            
        } while( (str=rd.readLine()) != null );
        return count;
    }
    
    private FastaDBApp mApp;
}
