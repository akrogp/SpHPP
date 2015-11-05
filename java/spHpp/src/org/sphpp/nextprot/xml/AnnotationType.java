//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.13 at 12:17:16 PM CET 
//


package org.sphpp.nextprot.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for annotationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="annotationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="active site"/>
 *     &lt;enumeration value="allergen"/>
 *     &lt;enumeration value="amino acid modification"/>
 *     &lt;enumeration value="beta strand"/>
 *     &lt;enumeration value="binding site"/>
 *     &lt;enumeration value="biotechnology"/>
 *     &lt;enumeration value="calcium-binding region"/>
 *     &lt;enumeration value="catalytic activity"/>
 *     &lt;enumeration value="caution"/>
 *     &lt;enumeration value="cleavage site"/>
 *     &lt;enumeration value="coding sequence"/>
 *     &lt;enumeration value="cofactor"/>
 *     &lt;enumeration value="coiled-coil region"/>
 *     &lt;enumeration value="compositionally biased region"/>
 *     &lt;enumeration value="cross-link"/>
 *     &lt;enumeration value="developmental stage"/>
 *     &lt;enumeration value="disease"/>
 *     &lt;enumeration value="disulfide bond"/>
 *     &lt;enumeration value="DNA-binding region"/>
 *     &lt;enumeration value="domain"/>
 *     &lt;enumeration value="domain information"/>
 *     &lt;enumeration value="domain name"/>
 *     &lt;enumeration value="enzyme classification"/>
 *     &lt;enumeration value="enzyme regulation"/>
 *     &lt;enumeration value="expression info"/>
 *     &lt;enumeration value="family name"/>
 *     &lt;enumeration value="function"/>
 *     &lt;enumeration value="glycosylation site"/>
 *     &lt;enumeration value="go biological process"/>
 *     &lt;enumeration value="go cellular component"/>
 *     &lt;enumeration value="go molecular function"/>
 *     &lt;enumeration value="helix"/>
 *     &lt;enumeration value="induction"/>
 *     &lt;enumeration value="initiator methionine"/>
 *     &lt;enumeration value="interacting region"/>
 *     &lt;enumeration value="lipid moiety-binding region"/>
 *     &lt;enumeration value="maturation peptide"/>
 *     &lt;enumeration value="mature protein"/>
 *     &lt;enumeration value="metal ion-binding site"/>
 *     &lt;enumeration value="miscellaneous"/>
 *     &lt;enumeration value="mutagenesis site"/>
 *     &lt;enumeration value="non-consecutive residues"/>
 *     &lt;enumeration value="non-standard amino acid"/>
 *     &lt;enumeration value="non-terminal residue"/>
 *     &lt;enumeration value="nucleotide phosphate-binding region"/>
 *     &lt;enumeration value="pathway"/>
 *     &lt;enumeration value="pharmaceutical"/>
 *     &lt;enumeration value="polymorphism"/>
 *     &lt;enumeration value="PTM"/>
 *     &lt;enumeration value="region of interest"/>
 *     &lt;enumeration value="repeat"/>
 *     &lt;enumeration value="sequence caution"/>
 *     &lt;enumeration value="sequence conflict"/>
 *     &lt;enumeration value="sequence variant"/>
 *     &lt;enumeration value="short sequence motif"/>
 *     &lt;enumeration value="signal peptide"/>
 *     &lt;enumeration value="similarity"/>
 *     &lt;enumeration value="site"/>
 *     &lt;enumeration value="subcellular location"/>
 *     &lt;enumeration value="subcellular location info"/>
 *     &lt;enumeration value="subunit"/>
 *     &lt;enumeration value="tissue specificity"/>
 *     &lt;enumeration value="topological domain"/>
 *     &lt;enumeration value="transit peptide"/>
 *     &lt;enumeration value="transmembrane region"/>
 *     &lt;enumeration value="turn"/>
 *     &lt;enumeration value="uniprot keyword"/>
 *     &lt;enumeration value="unsure residue"/>
 *     &lt;enumeration value="zinc finger region"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "annotationType")
@XmlEnum
public enum AnnotationType {

    @XmlEnumValue("active site")
    ACTIVE_SITE("active site"),
    @XmlEnumValue("allergen")
    ALLERGEN("allergen"),
    @XmlEnumValue("amino acid modification")
    AMINO_ACID_MODIFICATION("amino acid modification"),
    @XmlEnumValue("beta strand")
    BETA_STRAND("beta strand"),
    @XmlEnumValue("binding site")
    BINDING_SITE("binding site"),
    @XmlEnumValue("biotechnology")
    BIOTECHNOLOGY("biotechnology"),
    @XmlEnumValue("calcium-binding region")
    CALCIUM_BINDING_REGION("calcium-binding region"),
    @XmlEnumValue("catalytic activity")
    CATALYTIC_ACTIVITY("catalytic activity"),
    @XmlEnumValue("caution")
    CAUTION("caution"),
    @XmlEnumValue("cleavage site")
    CLEAVAGE_SITE("cleavage site"),
    @XmlEnumValue("coding sequence")
    CODING_SEQUENCE("coding sequence"),
    @XmlEnumValue("cofactor")
    COFACTOR("cofactor"),
    @XmlEnumValue("coiled-coil region")
    COILED_COIL_REGION("coiled-coil region"),
    @XmlEnumValue("compositionally biased region")
    COMPOSITIONALLY_BIASED_REGION("compositionally biased region"),
    @XmlEnumValue("cross-link")
    CROSS_LINK("cross-link"),
    @XmlEnumValue("developmental stage")
    DEVELOPMENTAL_STAGE("developmental stage"),
    @XmlEnumValue("disease")
    DISEASE("disease"),
    @XmlEnumValue("disulfide bond")
    DISULFIDE_BOND("disulfide bond"),
    @XmlEnumValue("DNA-binding region")
    DNA_BINDING_REGION("DNA-binding region"),
    @XmlEnumValue("domain")
    DOMAIN("domain"),
    @XmlEnumValue("domain information")
    DOMAIN_INFORMATION("domain information"),
    @XmlEnumValue("domain name")
    DOMAIN_NAME("domain name"),
    @XmlEnumValue("enzyme classification")
    ENZYME_CLASSIFICATION("enzyme classification"),
    @XmlEnumValue("enzyme regulation")
    ENZYME_REGULATION("enzyme regulation"),
    @XmlEnumValue("expression info")
    EXPRESSION_INFO("expression info"),
    @XmlEnumValue("family name")
    FAMILY_NAME("family name"),
    @XmlEnumValue("function")
    FUNCTION("function"),
    @XmlEnumValue("glycosylation site")
    GLYCOSYLATION_SITE("glycosylation site"),
    @XmlEnumValue("go biological process")
    GO_BIOLOGICAL_PROCESS("go biological process"),
    @XmlEnumValue("go cellular component")
    GO_CELLULAR_COMPONENT("go cellular component"),
    @XmlEnumValue("go molecular function")
    GO_MOLECULAR_FUNCTION("go molecular function"),
    @XmlEnumValue("helix")
    HELIX("helix"),
    @XmlEnumValue("induction")
    INDUCTION("induction"),
    @XmlEnumValue("initiator methionine")
    INITIATOR_METHIONINE("initiator methionine"),
    @XmlEnumValue("interacting region")
    INTERACTING_REGION("interacting region"),
    @XmlEnumValue("lipid moiety-binding region")
    LIPID_MOIETY_BINDING_REGION("lipid moiety-binding region"),
    @XmlEnumValue("maturation peptide")
    MATURATION_PEPTIDE("maturation peptide"),
    @XmlEnumValue("mature protein")
    MATURE_PROTEIN("mature protein"),
    @XmlEnumValue("metal ion-binding site")
    METAL_ION_BINDING_SITE("metal ion-binding site"),
    @XmlEnumValue("miscellaneous")
    MISCELLANEOUS("miscellaneous"),
    @XmlEnumValue("mutagenesis site")
    MUTAGENESIS_SITE("mutagenesis site"),
    @XmlEnumValue("non-consecutive residues")
    NON_CONSECUTIVE_RESIDUES("non-consecutive residues"),
    @XmlEnumValue("non-standard amino acid")
    NON_STANDARD_AMINO_ACID("non-standard amino acid"),
    @XmlEnumValue("non-terminal residue")
    NON_TERMINAL_RESIDUE("non-terminal residue"),
    @XmlEnumValue("nucleotide phosphate-binding region")
    NUCLEOTIDE_PHOSPHATE_BINDING_REGION("nucleotide phosphate-binding region"),
    @XmlEnumValue("pathway")
    PATHWAY("pathway"),
    @XmlEnumValue("pharmaceutical")
    PHARMACEUTICAL("pharmaceutical"),
    @XmlEnumValue("polymorphism")
    POLYMORPHISM("polymorphism"),
    PTM("PTM"),
    @XmlEnumValue("region of interest")
    REGION_OF_INTEREST("region of interest"),
    @XmlEnumValue("repeat")
    REPEAT("repeat"),
    @XmlEnumValue("sequence caution")
    SEQUENCE_CAUTION("sequence caution"),
    @XmlEnumValue("sequence conflict")
    SEQUENCE_CONFLICT("sequence conflict"),
    @XmlEnumValue("sequence variant")
    SEQUENCE_VARIANT("sequence variant"),
    @XmlEnumValue("short sequence motif")
    SHORT_SEQUENCE_MOTIF("short sequence motif"),
    @XmlEnumValue("signal peptide")
    SIGNAL_PEPTIDE("signal peptide"),
    @XmlEnumValue("similarity")
    SIMILARITY("similarity"),
    @XmlEnumValue("site")
    SITE("site"),
    @XmlEnumValue("subcellular location")
    SUBCELLULAR_LOCATION("subcellular location"),
    @XmlEnumValue("subcellular location info")
    SUBCELLULAR_LOCATION_INFO("subcellular location info"),
    @XmlEnumValue("subunit")
    SUBUNIT("subunit"),
    @XmlEnumValue("tissue specificity")
    TISSUE_SPECIFICITY("tissue specificity"),
    @XmlEnumValue("topological domain")
    TOPOLOGICAL_DOMAIN("topological domain"),
    @XmlEnumValue("transit peptide")
    TRANSIT_PEPTIDE("transit peptide"),
    @XmlEnumValue("transmembrane region")
    TRANSMEMBRANE_REGION("transmembrane region"),
    @XmlEnumValue("turn")
    TURN("turn"),
    @XmlEnumValue("uniprot keyword")
    UNIPROT_KEYWORD("uniprot keyword"),
    @XmlEnumValue("unsure residue")
    UNSURE_RESIDUE("unsure residue"),
    @XmlEnumValue("zinc finger region")
    ZINC_FINGER_REGION("zinc finger region");
    private final String value;

    AnnotationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AnnotationType fromValue(String v) {
        for (AnnotationType c: AnnotationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
