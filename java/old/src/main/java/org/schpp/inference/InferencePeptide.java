package org.schpp.inference;

import java.util.ArrayList;
import java.util.List;
import org.proteored.miapeapi.experiment.model.ExtendedIdentifiedPeptide;

/**
 *
 * @author gorka
 */
public class InferencePeptide extends ExtendedIdentifiedPeptide {
    public InferencePeptide( ExtendedIdentifiedPeptide p ) {
        this(p, PeptideRelation.NonDiscriminating);
    }
    
    public InferencePeptide( ExtendedIdentifiedPeptide p, PeptideRelation r ) {
        super(p);
        Relation = r;
        Proteins = new ArrayList<InferenceProtein>();
    }

    @Override
    public String toString() {
        switch( Relation ) {
            case Discriminating:
                return getId()+"*";
            case NonDiscriminating:
                return getId()+"**";
        }
        return new Integer(getId()).toString();
    }                   
    
    public PeptideRelation Relation;
    public List<InferenceProtein> Proteins;
}