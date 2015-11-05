package org.schpp.inference;

import java.util.ArrayList;
import java.util.List;
import org.proteored.miapeapi.experiment.model.ExtendedIdentifiedProtein;

/**
 *
 * @author gorka
 */
public class InferenceProtein extends ExtendedIdentifiedProtein {
    public InferenceProtein( ExtendedIdentifiedProtein p ) {
        this(p,ProteinEvidence.NonConclusive);
    }
    
    public InferenceProtein( ExtendedIdentifiedProtein p, ProteinEvidence e ) {
        super( p );
        Evidence = e;
        Peptides = new ArrayList<InferencePeptide>();
        Group = null;
    }
    
    public ProteinEvidence Evidence;
    public List<InferencePeptide> Peptides;
    public ProteinGroup Group;
}
