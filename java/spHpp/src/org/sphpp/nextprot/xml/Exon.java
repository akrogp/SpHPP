//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.13 at 12:17:16 PM CET 
//


package org.sphpp.nextprot.xml;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}positionOnGene"/>
 *         &lt;element ref="{}aminoAcid" maxOccurs="2" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="rank" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="codingStatus" use="required" type="{}codingStatusType" />
 *       &lt;attribute name="accession" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "positionOnGene",
    "aminoAcid"
})
@XmlRootElement(name = "exon")
public class Exon {

    @XmlElement(required = true)
    protected PositionOnGene positionOnGene;
    protected List<AminoAcid> aminoAcid;
    @XmlAttribute(name = "rank", required = true)
    protected BigInteger rank;
    @XmlAttribute(name = "codingStatus", required = true)
    protected CodingStatusType codingStatus;
    @XmlAttribute(name = "accession")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String accession;

    /**
     * Gets the value of the positionOnGene property.
     * 
     * @return
     *     possible object is
     *     {@link PositionOnGene }
     *     
     */
    public PositionOnGene getPositionOnGene() {
        return positionOnGene;
    }

    /**
     * Sets the value of the positionOnGene property.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionOnGene }
     *     
     */
    public void setPositionOnGene(PositionOnGene value) {
        this.positionOnGene = value;
    }

    /**
     * Gets the value of the aminoAcid property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aminoAcid property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAminoAcid().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AminoAcid }
     * 
     * 
     */
    public List<AminoAcid> getAminoAcid() {
        if (aminoAcid == null) {
            aminoAcid = new ArrayList<AminoAcid>();
        }
        return this.aminoAcid;
    }

    /**
     * Gets the value of the rank property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRank() {
        return rank;
    }

    /**
     * Sets the value of the rank property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRank(BigInteger value) {
        this.rank = value;
    }

    /**
     * Gets the value of the codingStatus property.
     * 
     * @return
     *     possible object is
     *     {@link CodingStatusType }
     *     
     */
    public CodingStatusType getCodingStatus() {
        return codingStatus;
    }

    /**
     * Sets the value of the codingStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodingStatusType }
     *     
     */
    public void setCodingStatus(CodingStatusType value) {
        this.codingStatus = value;
    }

    /**
     * Gets the value of the accession property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Sets the value of the accession property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccession(String value) {
        this.accession = value;
    }

}
