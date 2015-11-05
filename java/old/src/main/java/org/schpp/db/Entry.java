/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.schpp.db;

import org.schpp.utils.Strings;

/**
 *
 * @author gorka
 */
public class Entry {
    public String accession = null;
    public String protein = null;
    public String gene = null;
    public String transcript = null;
    public String chromosome = null;
    public String source = null;
    public int score = 0;
    boolean anchor = false;
    
    public void update( Entry entry ) {
        if( entry.accession != null )
            accession = entry.accession;
        if( entry.protein != null )
            protein = entry.protein;
        if( entry.gene != null )
            gene = entry.gene;
        if( entry.transcript != null )
            transcript = entry.transcript;
        if( entry.chromosome != null )
            chromosome = entry.chromosome;
        if( entry.source != null )
            source = source == null ? entry.source : source + "+" + entry.source;        
    }        
    
    public void merge( Entry entry ) {
        protein = Strings.merge( protein, entry.protein );
        gene = Strings.merge( gene, entry.gene );
        transcript = Strings.merge( transcript, entry.transcript );
        chromosome = Strings.merge( chromosome, entry.chromosome );
    }
        
    @Override
    public String toString() {
        String str = "";
        
        if( protein != null )
            str = str + "\\Protein=" + protein + " ";
        if( gene != null )
            str = str + "\\Gene=" + gene + " ";
        if( chromosome != null )
            str = str + "\\Chromosome=" + chromosome;

        return str;
    }
}