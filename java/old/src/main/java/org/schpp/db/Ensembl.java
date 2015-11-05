// $Id$

package org.schpp.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.biomart.api.soap.BioMartSoapService;
import org.biomart.api.soap.PortalServiceImpl;

/**
 *
 * @author gorka
 */
public class Ensembl {   
    public Ensembl() {
        BioMartSoapService service = new org.biomart.api.soap.BioMartSoapService();
        mPort = service.getPortalServiceImplPort();
    }
    
    String query( String xml ) {
        return mPort.getResults("<!DOCTYPE Query>"+xml);
    }
    
    String query( String processor, String dataset, String fname, String fvalue, String att ) {
        String xml = String.format("<!DOCTYPE Query>"
            + "<Query client=\"javaclient\" processor=\"%s\" limit=\"-1\" header=\"0\">"
            + "  <Dataset name=\"%s\">"
            + "    <Filter name=\"%s\" value=\"%s\"/>"
            + "    <Attribute name=\"%s\"/>"
            + "  </Dataset>"
            + "</Query>", processor, dataset, fname, fvalue, att );
        return mPort.getResults(xml);
    }
    
    String[] getHumanSwissProt( String chr ) {
        String res = query("TSV", "hsapiens_gene_ensembl", "chromosome_name", chr, "uniprot_swissprot_accession");
        return res.trim().replaceAll("\n\n", "\n").split("\n");
    }
    
    String[] getHumanTrEMBL( String chr ) {
        String res = query("TSV", "hsapiens_gene_ensembl", "chromosome_name", chr, "uniprot_sptrembl");
        return res.trim().replaceAll("\n\n", "\n").split("\n");
    }
    
    String[] getHumanEnsembl( String chr ) {
        String res = query("TSV", "hsapiens_gene_ensembl", "chromosome_name", chr, "ensembl_peptide_id");
        return res.trim().replaceAll("\n\n", "\n").split("\n");
    }
    
    void downloadMap( String fname, String chr ) throws IOException {
        String xml = String.format("<!DOCTYPE Query>"
            + "<Query virtualSchemaName=\"default\" formatter=\"TSV\" header=\"0\">"
            + "  <Dataset name=\"hsapiens_gene_ensembl\">"
            + "    <Filter name=\"chromosome_name\" value=\"%s\"/>"
            + "    <Attribute name=\"ensembl_gene_id\"/>"
            + "    <Attribute name=\"ensembl_transcript_id\"/>"
            + "    <Attribute name=\"ensembl_peptide_id\"/>"
            + "  </Dataset>"
            + "</Query>", chr );
        BufferedReader rd = new BufferedReader(new StringReader((mPort.getResults(xml))));
        PrintWriter wr = new PrintWriter( fname );
        String line, gene = null;
        String[] fields;
        Vector<String> transcripts = new Vector<String>();
        Vector<String> peptides = new Vector<String>();
        int i;
        while( (line=rd.readLine()) != null ) {
            fields = line.split("\t");
            if( gene != null && !fields[0].equals(gene) ) {
                wr.print(gene+":");
                for( i = 0; i < transcripts.size()-1; i++ )
                    wr.print(transcripts.elementAt(i)+", ");
                if( !transcripts.isEmpty() )
                    wr.print(transcripts.elementAt(i));
                wr.print(":");
                for( i = 0; i < peptides.size()-1; i++ )
                    wr.print(peptides.elementAt(i)+", ");
                if( !peptides.isEmpty() )
                    wr.print(peptides.elementAt(i));
                wr.println();
                transcripts.clear();
                peptides.clear();
            }
            gene = fields[0];
            if( fields.length > 1 && !transcripts.contains(fields[1]) )
                transcripts.add(fields[1]);
            if( fields.length > 2 && !peptides.contains(fields[2]) )
                peptides.add(fields[2]);
        }
        wr.close();
    }
    
    public List<Entry> getMap() throws IOException {
        String str = query(
            "<Query client=\"javaclient\" formatter = \"TSV\" header = \"0\" uniqueRows = \"0\" limit = \"-1\" >"+
                "<Dataset name = \"hsapiens_gene_ensembl\" interface = \"default\" >"+
                    "<Attribute name = \"ensembl_gene_id\" />"+
                    "<Attribute name = \"ensembl_transcript_id\" />"+
                    "<Attribute name = \"ensembl_peptide_id\" />"+
                    "<Attribute name = \"chromosome_name\" />"+
                "</Dataset>"+
            "</Query>"
        );
        BufferedReader rd = new BufferedReader(new StringReader(str));
        List<Entry> result = getMap(rd, "\t", false);
        rd.close();
        return result;
    }
    
    public List<Entry> getMap( String file ) throws IOException {
        return getMap(file, "\t", true);
    }
    
    public List<Entry> getMap( String file, String sep, boolean header ) throws IOException {
        List<Entry> result;
        BufferedReader rd = new BufferedReader(new FileReader(file));
        result = getMap(rd, sep, header);
        rd.close();
        return result;
    }
    
    public List<Entry> getMap( BufferedReader rd, String sep, boolean header ) throws IOException {
        List<Entry> entries = new ArrayList<Entry>();
        String line;
        String[] fields;
        Entry info;
        if( header )
            rd.readLine();
        while( (line=rd.readLine()) != null ) {
            fields = line.split(sep);
            info = new Entry();
            info.gene = fields[0];
            info.transcript = fields[1];
            info.protein = fields[2];
            info.chromosome = fields[3];
            entries.add(info);
        }
        return entries;
    }
    
    public Map<String, Entry> getProteinMap( List<Entry> list ) throws IOException {
        Map<String, Entry> map = new HashMap<String, Entry>();
        for( Entry entry : list )
            if( entry.protein != null && !entry.protein.isEmpty() )
                map.put(entry.protein, entry);
        return map;
    }
    
    public Map<String, Entry> getGeneMap( List<Entry> list ) throws IOException {
        Map<String, Entry> map = new HashMap<String, Entry>();
        for( Entry entry : list )
            if( entry.gene != null && !entry.gene.isEmpty() ) {
                if( map.containsKey(entry.gene) )
                    map.get(entry.gene).merge(entry);
                else
                    map.put(entry.gene, entry);
            }
        return map;
    }
    
    /*public static void main( String[] args ) {
        Ensembl ens = new Ensembl();
        try {
            ens.getProteinMap();
        } catch (IOException ex) {
            Logger.getLogger(Ensembl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    /*public static void main( String[] args ) {
        Ensembl ens = new Ensembl();
        try {
            ens.createMap("map_chr16.txt", "16");
        } catch (IOException ex) {
            Logger.getLogger(Ensembl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    /*public static void main( String[] args ) {
        Ensembl ens = new Ensembl();
        String res = ens.query(
                "<Query client=\"javaclient\" formatter = \"FASTA\" header = \"0\" uniqueRows = \"0\" limit = \"10\" >"+                
                    "<Dataset name = \"hsapiens_gene_ensembl\" interface = \"default\" >"+
                        "<Filter name = \"snptype_filters\" value = \"NON_SYNONYMOUS_CODING\"/>"+
                        "<Attribute name = \"peptide\" />"+
                        "<Attribute name = \"ensembl_gene_id\" />"+
                        "<Attribute name = \"ensembl_transcript_id\" />"+
                    "</Dataset>"+
                "</Query>"
        );
        System.out.println(res);
    }*/
    
    private PortalServiceImpl mPort;
}
