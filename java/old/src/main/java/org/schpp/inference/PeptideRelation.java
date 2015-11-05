package org.schpp.inference;

/**
 *
 * @author gorka
 */
public enum PeptideRelation {
    /**
     * Peptides that can only be assigned to a single protein in the database
     */
    Unique,
    /**
     * Shared peptides which presence is explained by a set of proteins without unique peptides
     */
    Discriminating,
    /**
     *  Shared peptides which presence is explained by proteins with unique or discriminating peptides
     */
    NonDiscriminating
}
