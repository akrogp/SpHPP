//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.13 at 12:17:16 PM CET 
//


package org.sphpp.nextprot.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element ref="{}cvTerm" minOccurs="0"/>
 *         &lt;element ref="{}description" minOccurs="0"/>
 *         &lt;element ref="{}variant" minOccurs="0"/>
 *         &lt;element ref="{}properties" minOccurs="0"/>
 *         &lt;element ref="{}experimentalEvidences" minOccurs="0"/>
 *         &lt;element ref="{}evidences" minOccurs="0"/>
 *         &lt;element ref="{}isoformSpecificity" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uniqueName" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="qualityQualifier" use="required" type="{}qualityType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cvTerm",
    "description",
    "variant",
    "properties",
    "experimentalEvidences",
    "evidences",
    "isoformSpecificity"
})
@XmlRootElement(name = "annotation")
public class Annotation {

    protected CvTerm cvTerm;
    protected String description;
    protected Variant variant;
    protected Properties properties;
    protected ExperimentalEvidences experimentalEvidences;
    protected Evidences evidences;
    protected IsoformSpecificity isoformSpecificity;
    @XmlAttribute(name = "uniqueName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String uniqueName;
    @XmlAttribute(name = "qualityQualifier", required = true)
    protected QualityType qualityQualifier;

    /**
     * Gets the value of the cvTerm property.
     * 
     * @return
     *     possible object is
     *     {@link CvTerm }
     *     
     */
    public CvTerm getCvTerm() {
        return cvTerm;
    }

    /**
     * Sets the value of the cvTerm property.
     * 
     * @param value
     *     allowed object is
     *     {@link CvTerm }
     *     
     */
    public void setCvTerm(CvTerm value) {
        this.cvTerm = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the variant property.
     * 
     * @return
     *     possible object is
     *     {@link Variant }
     *     
     */
    public Variant getVariant() {
        return variant;
    }

    /**
     * Sets the value of the variant property.
     * 
     * @param value
     *     allowed object is
     *     {@link Variant }
     *     
     */
    public void setVariant(Variant value) {
        this.variant = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link Properties }
     *     
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Properties }
     *     
     */
    public void setProperties(Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the experimentalEvidences property.
     * 
     * @return
     *     possible object is
     *     {@link ExperimentalEvidences }
     *     
     */
    public ExperimentalEvidences getExperimentalEvidences() {
        return experimentalEvidences;
    }

    /**
     * Sets the value of the experimentalEvidences property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExperimentalEvidences }
     *     
     */
    public void setExperimentalEvidences(ExperimentalEvidences value) {
        this.experimentalEvidences = value;
    }

    /**
     * Gets the value of the evidences property.
     * 
     * @return
     *     possible object is
     *     {@link Evidences }
     *     
     */
    public Evidences getEvidences() {
        return evidences;
    }

    /**
     * Sets the value of the evidences property.
     * 
     * @param value
     *     allowed object is
     *     {@link Evidences }
     *     
     */
    public void setEvidences(Evidences value) {
        this.evidences = value;
    }

    /**
     * Gets the value of the isoformSpecificity property.
     * 
     * @return
     *     possible object is
     *     {@link IsoformSpecificity }
     *     
     */
    public IsoformSpecificity getIsoformSpecificity() {
        return isoformSpecificity;
    }

    /**
     * Sets the value of the isoformSpecificity property.
     * 
     * @param value
     *     allowed object is
     *     {@link IsoformSpecificity }
     *     
     */
    public void setIsoformSpecificity(IsoformSpecificity value) {
        this.isoformSpecificity = value;
    }

    /**
     * Gets the value of the uniqueName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * Sets the value of the uniqueName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUniqueName(String value) {
        this.uniqueName = value;
    }

    /**
     * Gets the value of the qualityQualifier property.
     * 
     * @return
     *     possible object is
     *     {@link QualityType }
     *     
     */
    public QualityType getQualityQualifier() {
        return qualityQualifier;
    }

    /**
     * Sets the value of the qualityQualifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link QualityType }
     *     
     */
    public void setQualityQualifier(QualityType value) {
        this.qualityQualifier = value;
    }

}
