
package org.biomart.api.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dataset complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dataset">
 *   &lt;complexContent>
 *     &lt;extension base="{http://soap.api.biomart.org/}liteMartConfiguratorObject">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="isHidden" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dataset")
public class Dataset
    extends LiteMartConfiguratorObject
{

    @XmlAttribute(required = true)
    protected boolean isHidden;

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

}
