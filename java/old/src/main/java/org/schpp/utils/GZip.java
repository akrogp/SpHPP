// $Id: GZip.java 99 2013-11-14 15:34:08Z gorka.prieto@gmail.com $

package org.schpp.utils;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author gorka
 */
public class GZip {
    public static long unzip( String fin ) throws IOException {
        String fout = fin.replaceFirst(".gz", "");
        return unzip( fin, fout );
    }
    
    public static long unzip( String fin, String fout ) throws IOException {
        GZIPInputStream rd = new GZIPInputStream(new FileInputStream(fin));
        BufferedOutputStream wr = new BufferedOutputStream(new FileOutputStream(fout));
        byte buffer[] = new byte[BUFFER_SIZE];
        int len;
        long count = 0;
        try {
            while( (len=rd.read(buffer)) != -1 ) {
                wr.write(buffer, 0, len);
                wr.flush();
                count += len;
            }
        } finally {
            wr.close();
            rd.close();
        }
        return count;
    }
    
    private static final int BUFFER_SIZE = 1024 * 1024;
}
