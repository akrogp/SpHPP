
package org.biomart.api.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for container complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="container">
 *   &lt;complexContent>
 *     &lt;extension base="{http://soap.api.biomart.org/}liteMartConfiguratorObject">
 *       &lt;sequence>
 *         &lt;element name="attributes" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="attribute" type="{http://soap.api.biomart.org/}attribute" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="containers" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="container" type="{http://soap.api.biomart.org/}container" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="filters" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="filter" type="{http://soap.api.biomart.org/}filter" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="independent" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="maxAttributes" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="maxContainers" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "container", propOrder = {
    "attributes",
    "containers",
    "filters"
})
public class Container
    extends LiteMartConfiguratorObject
{

    protected Container.Attributes attributes;
    protected Container.Containers containers;
    protected Container.Filters filters;
    @XmlAttribute(required = true)
    protected boolean independent;
    @XmlAttribute(required = true)
    protected int maxAttributes;
    @XmlAttribute(required = true)
    protected int maxContainers;

    /**
     * Gets the value of the attributes property.
     * 
     * @return
     *     possible object is
     *     {@link Container.Attributes }
     *     
     */
    public Container.Attributes getAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Container.Attributes }
     *     
     */
    public void setAttributes(Container.Attributes value) {
        this.attributes = value;
    }

    /**
     * Gets the value of the containers property.
     * 
     * @return
     *     possible object is
     *     {@link Container.Containers }
     *     
     */
    public Container.Containers getContainers() {
        return containers;
    }

    /**
     * Sets the value of the containers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Container.Containers }
     *     
     */
    public void setContainers(Container.Containers value) {
        this.containers = value;
    }

    /**
     * Gets the value of the filters property.
     * 
     * @return
     *     possible object is
     *     {@link Container.Filters }
     *     
     */
    public Container.Filters getFilters() {
        return filters;
    }

    /**
     * Sets the value of the filters property.
     * 
     * @param value
     *     allowed object is
     *     {@link Container.Filters }
     *     
     */
    public void setFilters(Container.Filters value) {
        this.filters = value;
    }

    /**
     * Gets the value of the independent property.
     * 
     */
    public boolean isIndependent() {
        return independent;
    }

    /**
     * Sets the value of the independent property.
     * 
     */
    public void setIndependent(boolean value) {
        this.independent = value;
    }

    /**
     * Gets the value of the maxAttributes property.
     * 
     */
    public int getMaxAttributes() {
        return maxAttributes;
    }

    /**
     * Sets the value of the maxAttributes property.
     * 
     */
    public void setMaxAttributes(int value) {
        this.maxAttributes = value;
    }

    /**
     * Gets the value of the maxContainers property.
     * 
     */
    public int getMaxContainers() {
        return maxContainers;
    }

    /**
     * Sets the value of the maxContainers property.
     * 
     */
    public void setMaxContainers(int value) {
        this.maxContainers = value;
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
     *         &lt;element name="attribute" type="{http://soap.api.biomart.org/}attribute" maxOccurs="unbounded" minOccurs="0"/>
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
        "attribute"
    })
    public static class Attributes {

        protected List<Attribute> attribute;

        /**
         * Gets the value of the attribute property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the attribute property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAttribute().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Attribute }
         * 
         * 
         */
        public List<Attribute> getAttribute() {
            if (attribute == null) {
                attribute = new ArrayList<Attribute>();
            }
            return this.attribute;
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
     *         &lt;element name="container" type="{http://soap.api.biomart.org/}container" maxOccurs="unbounded" minOccurs="0"/>
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
        "container"
    })
    public static class Containers {

        protected List<Container> container;

        /**
         * Gets the value of the container property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the container property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getContainer().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Container }
         * 
         * 
         */
        public List<Container> getContainer() {
            if (container == null) {
                container = new ArrayList<Container>();
            }
            return this.container;
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
     *         &lt;element name="filter" type="{http://soap.api.biomart.org/}filter" maxOccurs="unbounded" minOccurs="0"/>
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
        "filter"
    })
    public static class Filters {

        protected List<Filter> filter;

        /**
         * Gets the value of the filter property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the filter property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFilter().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Filter }
         * 
         * 
         */
        public List<Filter> getFilter() {
            if (filter == null) {
                filter = new ArrayList<Filter>();
            }
            return this.filter;
        }

    }

}
