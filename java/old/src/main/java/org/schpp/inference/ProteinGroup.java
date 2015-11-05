package org.schpp.inference;

import java.io.PrintStream;
import java.util.ArrayList;
import org.proteored.miapeapi.experiment.model.ExtendedIdentifiedProtein;

/**
 *
 * @author gorka
 */
public class ProteinGroup extends ArrayList<InferenceProtein> {
    public ProteinGroup( ProteinEvidence e ) {
        super();
        Evidence = e;
        Minimum = null;
    }
    
    public ProteinGroup() {
        this( ProteinEvidence.AmbiguousGroup );
    }
    
    public void dump( PrintStream stream ) {
        stream.println( Evidence.toString() );
        for( InferenceProtein prot : this ) {
            stream.print( "\t" + prot.getAccession() + ": " );
            for( InferencePeptide pept : prot.Peptides )
                stream.print(pept.toString()+" ");
            stream.println();
        }
    }
    
    /*public int updateMinimum() {        
    }
    
    List<ProteinGroup> getRecursive( ProteinGroup group, Iterator<ExtendedIdentifiedProtein> it ) {
        List<ProteinGroup> res = new ArrayList<ProteinGroup>();
        if( group == null )
            group = new ProteinGroup();
        res.add(group);
        if( !it.hasNext() )
            return res;
        ProteinGroup group2 = (ProteinGroup)group.clone();
        group2.add(it.next());
        res.addAll(getRecursive(group, it));
        res.addAll(getRecursive(group2, it));
        return res;
    }*/
    
    public ProteinEvidence Evidence;    
    public ProteinGroup Minimum;
}