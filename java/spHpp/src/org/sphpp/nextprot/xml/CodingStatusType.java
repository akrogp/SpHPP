//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.13 at 12:17:16 PM CET 
//


package org.sphpp.nextprot.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for codingStatusType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="codingStatusType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="START"/>
 *     &lt;enumeration value="CODING"/>
 *     &lt;enumeration value="STOP"/>
 *     &lt;enumeration value="STOP_ONLY"/>
 *     &lt;enumeration value="MONO"/>
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="NOT_CODING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "codingStatusType")
@XmlEnum
public enum CodingStatusType {

    START,
    CODING,
    STOP,
    STOP_ONLY,
    MONO,
    NONE,
    NOT_CODING;

    public String value() {
        return name();
    }

    public static CodingStatusType fromValue(String v) {
        return valueOf(v);
    }

}
