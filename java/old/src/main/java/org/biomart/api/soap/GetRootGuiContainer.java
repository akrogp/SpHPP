
package org.biomart.api.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getRootGuiContainer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getRootGuiContainer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="guitype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getRootGuiContainer", propOrder = {
    "guitype"
})
public class GetRootGuiContainer {

    protected String guitype;

    /**
     * Gets the value of the guitype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuitype() {
        return guitype;
    }

    /**
     * Sets the value of the guitype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuitype(String value) {
        this.guitype = value;
    }

}
