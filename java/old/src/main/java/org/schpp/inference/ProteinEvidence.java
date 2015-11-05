package org.schpp.inference;

/**
 *
 * @author gorka
 */
public enum ProteinEvidence {
    /**
     * At least one unique peptides
     */
    Conclusive,
    /**
     * Only NonDiscrimitating peptides
     */
    NonConclusive,
    /**
     * Shared Discriminating peptides
     */
    AmbiguousGroup,
    /**
     * Same peptides and at least one Discriminating
     */
    Indistinguishable,
    /**
     * No peptides
     */
    Filtered
}
