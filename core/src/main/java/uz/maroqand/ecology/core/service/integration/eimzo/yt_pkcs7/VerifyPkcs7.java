
package uz.maroqand.ecology.core.service.integration.eimzo.yt_pkcs7;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for verifyPkcs7 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="verifyPkcs7">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pkcs7B64" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verifyPkcs7", propOrder = {
    "pkcs7B64"
})
public class VerifyPkcs7 {

    protected String pkcs7B64;

    /**
     * Gets the value of the pkcs7B64 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPkcs7B64() {
        return pkcs7B64;
    }

    /**
     * Sets the value of the pkcs7B64 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPkcs7B64(String value) {
        this.pkcs7B64 = value;
    }

}
