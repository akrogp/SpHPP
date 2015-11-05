
package org.biomart.api.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getFilterValuesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getFilterValuesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filtervalue" type="{http://soap.api.biomart.org/}filterData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getFilterValuesResponse", propOrder = {
    "filtervalue"
})
public class GetFilterValuesResponse {

    protected List<FilterData> filtervalue;

    /**
     * Gets the value of the filtervalue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the filtervalue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFiltervalue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FilterData }
     * 
     * 
     */
    public List<FilterData> getFiltervalue() {
        if (filtervalue == null) {
            filtervalue = new ArrayList<FilterData>();
        }
        return this.filtervalue;
    }

}
