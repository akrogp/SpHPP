
package org.biomart.api.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for filterData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filterData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://soap.api.biomart.org/}liteMartConfiguratorObject">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="isSelected" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filterData")
public class FilterData
    extends LiteMartConfiguratorObject
{

    @XmlAttribute(required = true)
    protected boolean isSelected;

    /**
     * Gets the value of the isSelected property.
     * 
     */
    public boolean isIsSelected() {
        return isSelected;
    }

    /**
     * Sets the value of the isSelected property.
     * 
     */
    public void setIsSelected(boolean value) {
        this.isSelected = value;
    }

}
