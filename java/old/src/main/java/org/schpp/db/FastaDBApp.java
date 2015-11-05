// $Id: FastaDBApp.java 99 2013-11-14 15:34:08Z gorka.prieto@gmail.com $

package org.schpp.db;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.GZIPInputStream;
import org.schpp.inet.FTPClient;
import org.schpp.utils.FileSystem;
import org.schpp.utils.Zip;

/**
 *
 * @author gorka
 */
public class FastaDBApp {        
    public static void main( String[] args ) {
        if( args.length == 0 || args.length > 2 ) {
            showUsage();
            System.exit(1);
        }
        
        try {
            String dest = args[0];
            String chr = args.length != 0 ? args[1] : "16";
            FastaDBApp app = new FastaDBApp( dest, chr );
            app.run();
        } catch( Exception e ) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    private static void showUsage() {
        System.out.println( FastaDBApp.class.getName() + " <dst_dir> [<chr_number>]" );
    }
    
    public FastaDBApp( String dest, String chr ) throws IOException {
        mDest = new File(dest).getAbsolutePath();
        mXls = new File(mDest,XLS);
        if( !mXls.isFile() )
            throw new IOException( "Failed to locate PICR mapping file: " + mXls.getAbsolutePath() );
        File tmp = new File(mDest,"tmp");
        mTmp = tmp.getAbsolutePath();
        mChr = chr;
        if( tmp.exists() )
            for( File file : tmp.listFiles() )
                file.delete();                
        else if( !tmp.mkdirs() )
            throw new IOException( "Failed to create directories" );
        mLog = Logger.getLogger(FastaDBApp.class.getName());
        String log = new File(mTmp,LOG).getAbsolutePath();
        FileHandler fh = new FileHandler(log, false);
        fh.setFormatter(new SimpleFormatter());
        mLog.addHandler(fh);
        mLog.info("Started");
        mEnsembl = new Ensembl();
        mUniProt = new UniProt(this, chr);
        mNextProt = new NextProt(this);
    }
    
    public void run() {
        try {
            downloadDBs();
            generateMaps();
            parseDBs();
            generateDecoys();
            saveMetadata();
            deployDBs();
        } catch( Exception e ) {
            e.printStackTrace();
            mLog.severe(e.getMessage());
        }
    }
    
    public void downloadDBs() throws IOException {
        for( int i = 1; i <= 22; i++ )
            FTPClient.download(NX_BASE_URL+NX_CHR+i+".peff.gz", mTmp, mLog);
        FTPClient.download(NX_BASE_URL+NX_CHR+"X.peff.gz", mTmp, mLog);
        FTPClient.download(NX_BASE_URL+NX_CHR+"Y.peff.gz", mTmp, mLog);
        FTPClient.download(NX_BASE_URL+NX_CHR+"MT.peff.gz", mTmp, mLog);
        FTPClient.download(UP_BASE_URL+UP_ALL+".fasta.gz", mTmp, mLog);
    }
    
    public void generateMaps() throws Exception {
        // UniProt - Ensembl protein map
        PICR picr = new PICR();
        mMapPicr = picr.Uniprot2Ensembl(mXls);
        mLog.log(Level.INFO, "Mapped {0} UniProt accessions to Ensembl using PICR file", mMapPicr.size() );
        
        // Protein - Gene, Transcript, Chromosome
        mLog.info("Connecting to Ensembl ...");
        mMapEnsembl = mEnsembl.getProteinMap(mEnsembl.getMap());
        mLog.log(Level.INFO, "Downloaded information for {0} Ensembl proteins", mMapEnsembl.size() );
        
        // UniProt
        mListUniProt = UniProt.getChrEntries(mChr);
        mLog.log(Level.INFO, "Loaded UniProt chromosome {0} information for {1} proteins", new Object[]{mChr, mListUniProt.size()} );
        
        // Manual curation
        loadManualMap();
        mLog.log(Level.INFO, "Loaded manually curated information for {0} proteins", mMapManual.size() );
    }
    
    private void loadManualMap() {
        mMapManual = new HashMap<String, Entry>();
        try {
            BufferedReader rd = new BufferedReader(new FileReader(new File(mDest,MANUAL)));
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
                mMapManual.put(fields[0], pm);
            }
            rd.close();
        } catch( Exception ex ) {
        }
    }
    
    public void parseDBs() throws Exception {
        int i;
        mCountNextProtAll = 0;
        long tmp;
        boolean delete;
        for( i = 1; i <= 22; i++ ) {
            delete = !mChr.equals(""+i);
            tmp = parseNextProt(""+i, delete);
            mCountNextProtAll += tmp;
            if( !delete )
                mCountNextProtChr = tmp;
        }
        mCountNextProtAll += parseNextProt("X", true);
        mCountNextProtAll += parseNextProt("Y", true);
        mCountNextProtAll += parseNextProt("MT", true);
        mLog.log(Level.INFO, "NextProt completed with {0} total entries", mCountNextProtAll);
        parseUniProt();
    }
    
    public void generateDecoys() throws Exception {
        mLog.info("Generating decoys ...");
        File dir = new File(mTmp);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File file, String string) {
                return string.contains(".fasta");
            }
        };
        String fasta, decoy, prefix = "x";
        Process p;
        for( String file : dir.list(filter) ) {
            if( file.indexOf("nextprot") != -1 )
                prefix = "np";
            else if( file.indexOf("sprot") != -1 )
                prefix = "sp";
            else if( file.indexOf("trembl") != -1 )
                prefix = "tr";
            decoy = file.replaceAll(".fasta","_decoy.fasta");
            fasta = new File(mTmp,file).getAbsolutePath();
            p=Runtime.getRuntime().exec("decoy.pl 2 "+fasta+" "+decoy+" 0 "+prefix);
            if( p.waitFor() != 0 )
                throw new Exception("decoy failed");
            mLog.log(Level.INFO, "{0} completed", decoy);
            decoy = file.replaceAll(".fasta","_target_decoy.fasta");
            p=Runtime.getRuntime().exec("decoy.pl 2 "+fasta+" "+decoy+" 1 "+prefix);
            if( p.waitFor() != 0 )
                throw new Exception("decoy+target failed");
            mLog.log(Level.INFO, "{0} completed", decoy);
        }
        mLog.info("Decoy generation completed");
    }
    
    public void deployDBs() throws IOException {
        mLog.info("Deploying databases ...");
        File dir = new File(mTmp);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File file, String string) {
                return string.contains(".fasta");
            }
        };
        for( String file : dir.list(filter) ) {
            FileSystem.move(new File(mTmp,file), mDest);
            Zip.zip(new File(mDest,file).getAbsolutePath());
        }
        FileSystem.move(new File(mTmp,LOG), mDest);
        FileSystem.move(new File(mTmp,INI), mDest);
        new File(mTmp).delete();
        mLog.info("Databases update process completed successfully!!");
    }
    
    public long parseNextProt( String name, boolean delete ) throws IOException {
        File file1 = new File(mTmp,NX_CHR+name + ".peff.gz");
        File file2 = new File(mTmp,NX_CHR+name + ".fasta");
        mLog.log(Level.INFO, "Generating {0} ...", file2.getAbsolutePath());
        BufferedReader rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file1))));
        PrintWriter wr = new PrintWriter(file2);
        long count = mNextProt.peff2Fasta(rd, wr, name);
        rd.close();
        wr.close();
        file1.delete();
        mLog.log(Level.INFO, "Completed with {0} entries", count);
        FileSystem.concatenate(file2.getAbsolutePath(), new File(mTmp,NX_ALL+".fasta").getAbsolutePath());
        if( delete )
            file2.delete();
        return count;
    }
    
    public void parseUniProt() throws Exception {
        mLog.log(Level.INFO, "Separating SwissProt and TrEMBL entries for chromosome {0} ...", mChr);
        File up = new File(mTmp,UP_ALL + ".fasta.gz");
        File sp = new File(mTmp,SP_ALL + ".fasta");
        File sp_chr = new File(mTmp,SP_ALL + "_chr" + mChr + ".fasta");
        File tr = new File(mTmp,TR_ALL + ".fasta");
        File tr_chr = new File(mTmp,TR_ALL + "_chr" + mChr + ".fasta");
        mLog.log(Level.INFO, "Separating SwissProt and TrEMBL entries from {0} ...", up.getAbsolutePath());
        BufferedReader rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(up))));
        PrintWriter wr_sp = new PrintWriter(sp); PrintWriter wr_chr_sp = new PrintWriter(sp_chr);
        PrintWriter wr_tr = new PrintWriter(tr); PrintWriter wr_chr_tr = new PrintWriter(tr_chr);
        long[] counts = mUniProt.split( rd, wr_sp, wr_chr_sp, wr_tr, wr_chr_tr );
        rd.close();
        wr_sp.close(); wr_chr_sp.close();
        wr_tr.close(); wr_chr_tr.close();
        up.delete();
        mCountSwissProtAll = counts[0]; mCountSwissProtGenesAll = counts[1];
        mCountSwissProtChr = counts[2]; mCountSwissProtGenesChr = counts[3];
        mCountTremblAll = counts[4]; mCountTremblGenesAll = counts[5];
        mCountTremblChr = counts[6]; mCountTremblGenesChr = counts[7];
        mUniProtProtAll = counts[9]; mUniProtUnmapped = counts[8];
        mLog.log(Level.INFO, "SwissProt completed with a total of {0} entries corresponding to {1} genes",
                new Object[]{mCountSwissProtAll,mCountSwissProtGenesAll});
        mLog.log(Level.INFO, "SwissProt chromosome {0} resulted in {1} entries corresponding to {2} genes",
                new Object[]{mChr,mCountSwissProtChr,mCountSwissProtGenesChr});
        mLog.log(Level.INFO, "TrEMBL completed with a total of {0} entries corresponding to {1} genes",
                new Object[]{mCountTremblAll,mCountTremblGenesAll});
        mLog.log(Level.INFO, "TrEMBL chromosome {0} resulted in {1} entries corresponding to {2} genes",
                new Object[]{mChr,mCountTremblChr,mCountTremblGenesChr});
        if( counts[4] != 0 )
            mLog.log(Level.WARNING, "Ensembl mapping of {0} out of {1} UniProt entries not found in PICR/UniProt/Manual",
                    new Object[]{mUniProtUnmapped,mUniProtProtAll});        
    }
    
    private Entry tryEntry( String acc ) {
        PICR.Result picr;
        Entry partial;        
        Entry entry = new Entry();
        entry.accession = acc;
        
        // 1. PICR (UniProt -> Ensembl)
        if( (picr=mMapPicr.get(acc)) == null )
            mLog.log(Level.WARNING, "Ensembl mapping for {0} not available in PICR", acc);
        else {        
            // 2. Ensembl
            entry.protein = picr.ensp;
            entry.gene = picr.ensg;
            if( (partial=mMapEnsembl.get(picr.ensp)) == null )
                for( PICR.Entry pe : picr )
                    if( (partial=mMapEnsembl.get(pe.ensp)) != null ) {
                        entry.protein = pe.ensp;
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
        partial = mMapManual.get(acc);
        if( partial != null ) {
            mLog.log(Level.WARNING, "... but curated manually");
            entry.update(partial);
            return entry;
        }
        
        // 4. UniProt
        if( mListUniProt != null && mListUniProt.contains(acc) ) {
            mLog.log(Level.WARNING, "... but included in chromosome {0} using UniProt information", mChr);
            entry.chromosome = mChr;                
            return entry;
        }
        
        return null;
    }
    
    // UniProt -> ENSG
    public Entry getEntry( String acc ) {
        Entry entry = tryEntry(acc);        
        if( entry != null || acc.indexOf("-") == -1 )
            return entry;
        
        mLog.log(Level.WARNING, "... but trying alternative product");
        String alt = acc.replaceAll("-.*", "");
        entry = tryEntry(alt);        
        if( entry != null )
            mLog.log(Level.WARNING, "... used {0} information for {1}", new Object[]{alt, acc});
        
        return entry;
    }
    
    void saveMetadata() throws FileNotFoundException {
        PrintWriter wr = new PrintWriter(new File(mTmp,INI));
        wr.println("[nextprot]");
        wr.println("rel=2013-02-12");
        wr.println("ver=0.9");
        wr.println("info=Customized by Spanish C-HPP");
        wr.println("all="+mCountNextProtAll);        
        wr.println("chr"+mChr+"="+mCountNextProtChr);
        wr.println();
        wr.println("[uniprot]");
        wr.println("rel=2013-05-01");
        wr.println("ver=0.9");
        wr.println("info=Customized by Spanish C-HPP");
        wr.println("sprot_all="+mCountSwissProtAll);
        wr.println("sprot_all_genes="+mCountSwissProtGenesAll);
        wr.println("sprot_chr"+mChr+"="+mCountSwissProtChr);
        wr.println("sprot_chr"+mChr+"_genes="+mCountSwissProtGenesChr);
        wr.println("trembl_all="+mCountTremblAll);
        wr.println("trembl_genes_all="+mCountTremblGenesAll);
        wr.println("trembl_chr"+mChr+"="+mCountTremblChr);
        wr.println("trembl_chr"+mChr+"_genes="+mCountTremblGenesChr);
        wr.println();
        wr.println("[ensembl]");
        wr.println("rel=v71");
        wr.close();
    }

    private String mDest, mTmp;
    private String mChr;
    private Logger mLog;
    private File mXls;
    
    private static final String NX_BASE_URL = "ftp://ftp.nextprot.org/pub/current_release/peff/";
    private static final String NX_CHR = "nextprot_chromosome_";
    private static final String NX_ALL = "nextprot_all";
    private static final String UP_BASE_URL = "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/proteomes/";
    private static final String UP_ALL = "HUMAN";
    private static final String SP_ALL = "uniprot_sprot_human";
    private static final String TR_ALL = "uniprot_trembl_human";
    private static final String MANUAL = "manual.txt";
    private static final String LOG = "log.txt";
    private static final String XLS = "picr.xls";
    private static final String INI = "db_metadata.ini";
    
    private long mCountNextProtAll, mCountNextProtChr;
    private long mUniProtProtAll, mUniProtUnmapped;
    private long mCountSwissProtAll, mCountSwissProtGenesAll, mCountSwissProtChr, mCountSwissProtGenesChr;
    private long mCountTremblAll, mCountTremblGenesAll, mCountTremblChr, mCountTremblGenesChr;
    
    private Map<String, PICR.Result> mMapPicr;
    private Map<String, Entry> mMapEnsembl;
    private Map<String, Entry> mMapManual;
    private List<String> mListUniProt;
    
    private Ensembl mEnsembl;
    private UniProt mUniProt;
    private NextProt mNextProt;
}