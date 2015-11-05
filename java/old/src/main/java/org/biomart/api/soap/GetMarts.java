
package org.biomart.api.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getMarts complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMarts">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="guicontainer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMarts", propOrder = {
    "guicontainer"
})
public class GetMarts {

    protected String guicontainer;

    /**
     * Gets the value of the guicontainer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuicontainer() {
        return guicontainer;
    }

    /**
     * Sets the value of the guicontainer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuicontainer(String value) {
        this.guicontainer = value;
    }

}
