
package org.biomart.api.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getRootGuiContainerResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getRootGuiContainerResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="guicontainer" type="{http://soap.api.biomart.org/}guiContainer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getRootGuiContainerResponse", propOrder = {
    "guicontainer"
})
public class GetRootGuiContainerResponse {

    protected GuiContainer guicontainer;

    /**
     * Gets the value of the guicontainer property.
     * 
     * @return
     *     possible object is
     *     {@link GuiContainer }
     *     
     */
    public GuiContainer getGuicontainer() {
        return guicontainer;
    }

    /**
     * Sets the value of the guicontainer property.
     * 
     * @param value
     *     allowed object is
     *     {@link GuiContainer }
     *     
     */
    public void setGuicontainer(GuiContainer value) {
        this.guicontainer = value;
    }

}
