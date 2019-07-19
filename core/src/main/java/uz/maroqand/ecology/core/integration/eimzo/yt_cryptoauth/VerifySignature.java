
package uz.maroqand.ecology.core.integration.eimzo.yt_cryptoauth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for verifySignature complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="verifySignature">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="textB64" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signatureHex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signerCertB64" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verifySignature", propOrder = {
    "textB64",
    "signatureHex",
    "signerCertB64"
})
public class VerifySignature {

    protected String textB64;
    protected String signatureHex;
    protected String signerCertB64;

    /**
     * Gets the value of the textB64 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTextB64() {
        return textB64;
    }

    /**
     * Sets the value of the textB64 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTextB64(String value) {
        this.textB64 = value;
    }

    /**
     * Gets the value of the signatureHex property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignatureHex() {
        return signatureHex;
    }

    /**
     * Sets the value of the signatureHex property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignatureHex(String value) {
        this.signatureHex = value;
    }

    /**
     * Gets the value of the signerCertB64 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignerCertB64() {
        return signerCertB64;
    }

    /**
     * Sets the value of the signerCertB64 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignerCertB64(String value) {
        this.signerCertB64 = value;
    }

}
