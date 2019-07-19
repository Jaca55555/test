
package uz.maroqand.ecology.core.integration.eimzo.yt_cryptoauth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDigest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDigest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dataB64" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDigest", propOrder = {
    "dataB64"
})
public class GetDigest {

    protected String dataB64;

    /**
     * Gets the value of the dataB64 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataB64() {
        return dataB64;
    }

    /**
     * Sets the value of the dataB64 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataB64(String value) {
        this.dataB64 = value;
    }

}
