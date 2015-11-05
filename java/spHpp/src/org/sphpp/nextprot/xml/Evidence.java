//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.13 at 12:17:16 PM CET 
//


package org.sphpp.nextprot.xml;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;attribute name="isNegative" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="qualifierType" use="required" type="{}evidenceType" />
 *       &lt;attribute name="resourceAssocType" use="required" type="{}assocType" />
 *       &lt;attribute name="resourceRef" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "evidence")
public class Evidence {

    @XmlAttribute(name = "isNegative", required = true)
    protected boolean isNegative;
    @XmlAttribute(name = "qualifierType", required = true)
    protected EvidenceType qualifierType;
    @XmlAttribute(name = "resourceAssocType", required = true)
    protected AssocType resourceAssocType;
    @XmlAttribute(name = "resourceRef", required = true)
    protected BigInteger resourceRef;

    /**
     * Gets the value of the isNegative property.
     * 
     */
    public boolean isIsNegative() {
        return isNegative;
    }

    /**
     * Sets the value of the isNegative property.
     * 
     */
    public void setIsNegative(boolean value) {
        this.isNegative = value;
    }

    /**
     * Gets the value of the qualifierType property.
     * 
     * @return
     *     possible object is
     *     {@link EvidenceType }
     *     
     */
    public EvidenceType getQualifierType() {
        return qualifierType;
    }

    /**
     * Sets the value of the qualifierType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EvidenceType }
     *     
     */
    public void setQualifierType(EvidenceType value) {
        this.qualifierType = value;
    }

    /**
     * Gets the value of the resourceAssocType property.
     * 
     * @return
     *     possible object is
     *     {@link AssocType }
     *     
     */
    public AssocType getResourceAssocType() {
        return resourceAssocType;
    }

    /**
     * Sets the value of the resourceAssocType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssocType }
     *     
     */
    public void setResourceAssocType(AssocType value) {
        this.resourceAssocType = value;
    }

    /**
     * Gets the value of the resourceRef property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getResourceRef() {
        return resourceRef;
    }

    /**
     * Sets the value of the resourceRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setResourceRef(BigInteger value) {
        this.resourceRef = value;
    }

}
