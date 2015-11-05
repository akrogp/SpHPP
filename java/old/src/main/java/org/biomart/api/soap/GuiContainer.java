
package org.biomart.api.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for guiContainer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="guiContainer">
 *   &lt;complexContent>
 *     &lt;extension base="{http://soap.api.biomart.org/}liteMartConfiguratorObject">
 *       &lt;sequence>
 *         &lt;element name="guiContainers" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="guiContainer" type="{http://soap.api.biomart.org/}guiContainer" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="marts" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="mart" type="{http://soap.api.biomart.org/}mart" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="guiType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isHidden" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "guiContainer", propOrder = {
    "guiContainers",
    "marts"
})
public class GuiContainer
    extends LiteMartConfiguratorObject
{

    protected GuiContainer.GuiContainers guiContainers;
    protected GuiContainer.Marts marts;
    @XmlAttribute
    protected String guiType;
    @XmlAttribute(required = true)
    protected boolean isHidden;

    /**
     * Gets the value of the guiContainers property.
     * 
     * @return
     *     possible object is
     *     {@link GuiContainer.GuiContainers }
     *     
     */
    public GuiContainer.GuiContainers getGuiContainers() {
        return guiContainers;
    }

    /**
     * Sets the value of the guiContainers property.
     * 
     * @param value
     *     allowed object is
     *     {@link GuiContainer.GuiContainers }
     *     
     */
    public void setGuiContainers(GuiContainer.GuiContainers value) {
        this.guiContainers = value;
    }

    /**
     * Gets the value of the marts property.
     * 
     * @return
     *     possible object is
     *     {@link GuiContainer.Marts }
     *     
     */
    public GuiContainer.Marts getMarts() {
        return marts;
    }

    /**
     * Sets the value of the marts property.
     * 
     * @param value
     *     allowed object is
     *     {@link GuiContainer.Marts }
     *     
     */
    public void setMarts(GuiContainer.Marts value) {
        this.marts = value;
    }

    /**
     * Gets the value of the guiType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuiType() {
        return guiType;
    }

    /**
     * Sets the value of the guiType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuiType(String value) {
        this.guiType = value;
    }

    /**
     * Gets the value of the isHidden property.
     * 
     */
    public boolean isIsHidden() {
        return isHidden;
    }

    /**
     * Sets the value of the isHidden property.
     * 
     */
    public void setIsHidden(boolean value) {
        this.isHidden = value;
    }


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
     *         &lt;element name="guiContainer" type="{http://soap.api.biomart.org/}guiContainer" maxOccurs="unbounded" minOccurs="0"/>
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
        "guiContainer"
    })
    public static class GuiContainers {

        protected List<GuiContainer> guiContainer;

        /**
         * Gets the value of the guiContainer property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the guiContainer property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGuiContainer().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link GuiContainer }
         * 
         * 
         */
        public List<GuiContainer> getGuiContainer() {
            if (guiContainer == null) {
                guiContainer = new ArrayList<GuiContainer>();
            }
            return this.guiContainer;
        }

    }


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
     *         &lt;element name="mart" type="{http://soap.api.biomart.org/}mart" maxOccurs="unbounded" minOccurs="0"/>
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
        "mart"
    })
    public static class Marts {

        protected List<Mart> mart;

        /**
         * Gets the value of the mart property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the mart property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMart().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Mart }
         * 
         * 
         */
        public List<Mart> getMart() {
            if (mart == null) {
                mart = new ArrayList<Mart>();
            }
            return this.mart;
        }

    }

}
