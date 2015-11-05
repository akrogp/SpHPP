// $Id: FileSystem.java 99 2013-11-14 15:34:08Z gorka.prieto@gmail.com $

package org.schpp.utils;

import java.io.*;

/**
 *
 * @author gorka
 */
public class FileSystem {
    public static void move( String old_path, String new_path ) {
        File old_file = new File(old_path);        
        File new_file = new File(new_path);
        move(old_file, new_file);
    }
    
    public static void move( File old_file, String new_path ) {
        File new_file = new File(new_path);
        move(old_file, new_file);
    }
    
    public static void move( File old_file, File new_file ) {
        if( new_file.isDirectory() )
            new_file = new File(new_file.getAbsolutePath(),old_file.getName());
        if( new_file.exists() )
            new_file.delete();
        old_file.renameTo(new_file);
    }
    
    public static void concatenate( String infile, String outfile ) throws IOException {
        File of = new File(outfile);
        if( !of.exists() )
            of.createNewFile();
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream rd = new BufferedInputStream(new FileInputStream(infile));
        BufferedOutputStream wr = new BufferedOutputStream(new FileOutputStream(outfile,true));
        int len;
        while( (len=rd.read(buffer)) != -1 )
            wr.write(buffer, 0, len);
        wr.close();
        rd.close();        
    }
    
    private static final int BUFFER_SIZE = 1024 * 1024;
}
