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
 *         &lt;element ref="{}neXtProt"/>
 *         &lt;element ref="{}dataSources"/>
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
    "neXtProt",
    "dataSources"
})
@XmlRootElement(name = "release")
public class Release {

    @XmlElement(required = true)
    protected NeXtProt neXtProt;
    @XmlElement(required = true)
    protected DataSources dataSources;

    /**
     * Gets the value of the neXtProt property.
     * 
     * @return
     *     possible object is
     *     {@link NeXtProt }
     *     
     */
    public NeXtProt getNeXtProt() {
        return neXtProt;
    }

    /**
     * Sets the value of the neXtProt property.
     * 
     * @param value
     *     allowed object is
     *     {@link NeXtProt }
     *     
     */
    public void setNeXtProt(NeXtProt value) {
        this.neXtProt = value;
    }

    /**
     * Gets the value of the dataSources property.
     * 
     * @return
     *     possible object is
     *     {@link DataSources }
     *     
     */
    public DataSources getDataSources() {
        return dataSources;
    }

    /**
     * Sets the value of the dataSources property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataSources }
     *     
     */
    public void setDataSources(DataSources value) {
        this.dataSources = value;
    }

}
