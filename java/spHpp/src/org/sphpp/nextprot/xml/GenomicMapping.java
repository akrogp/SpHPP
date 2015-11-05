//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.13 at 12:17:16 PM CET 
//


package org.sphpp.nextprot.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{}gene" minOccurs="0"/>
 *         &lt;element ref="{}isoformMappings"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "gene",
    "isoformMappings"
})
@XmlRootElement(name = "genomicMapping")
public class GenomicMapping {

    protected Gene gene;
    @XmlElement(required = true)
    protected IsoformMappings isoformMappings;

    /**
     * Gets the value of the gene property.
     * 
     * @return
     *     possible object is
     *     {@link Gene }
     *     
     */
    public Gene getGene() {
        return gene;
    }

    /**
     * Sets the value of the gene property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gene }
     *     
     */
    public void setGene(Gene value) {
        this.gene = value;
    }

    /**
     * Gets the value of the isoformMappings property.
     * 
     * @return
     *     possible object is
     *     {@link IsoformMappings }
     *     
     */
    public IsoformMappings getIsoformMappings() {
        return isoformMappings;
    }

    /**
     * Sets the value of the isoformMappings property.
     * 
     * @param value
     *     allowed object is
     *     {@link IsoformMappings }
     *     
     */
    public void setIsoformMappings(IsoformMappings value) {
        this.isoformMappings = value;
    }

}
