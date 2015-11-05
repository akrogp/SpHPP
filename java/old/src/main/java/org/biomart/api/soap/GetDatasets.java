
package org.biomart.api.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDatasets complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDatasets">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mart" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDatasets", propOrder = {
    "mart"
})
public class GetDatasets {

    protected String mart;

    /**
     * Gets the value of the mart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMart() {
        return mart;
    }

    /**
     * Sets the value of the mart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMart(String value) {
        this.mart = value;
    }

}
