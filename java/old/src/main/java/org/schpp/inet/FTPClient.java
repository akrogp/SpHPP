// $Id: FTPClient.java 99 2013-11-14 15:34:08Z gorka.prieto@gmail.com $

package org.schpp.inet;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gorka
 */
public class FTPClient {
    public static long download( String url, String dir, Logger log ) throws MalformedURLException, IOException {
        log.log(Level.INFO, "Downloading {0} ...", url);
        long ret = download(url, dir);
        log.log(Level.INFO, "Downloaded {0} bytes!!", ret);
        return ret;
    }
    
    public static long download( String url, String dir ) throws MalformedURLException, IOException {
        String dest = new File(dir,new File(url).getName()).getAbsolutePath();
        URL myurl = new URL(url+";type=i");
        URLConnection urlc = myurl.openConnection();
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(dest));
        BufferedInputStream is = new BufferedInputStream(urlc.getInputStream());
        byte[] buffer = new byte[BUFFER_SIZE];
        long count = 0;
        int len;
        try {
            while( (len=is.read(buffer)) != -1 ) {
                os.write(buffer, 0, len);
                count += len;
                os.flush();
            }
        } finally {
            os.close();
            is.close();
        }
        return count;
    }
    
    private static final int BUFFER_SIZE = 1024 * 1024;
}
