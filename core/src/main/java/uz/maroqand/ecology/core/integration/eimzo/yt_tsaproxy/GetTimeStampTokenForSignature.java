
package uz.maroqand.ecology.core.integration.eimzo.yt_tsaproxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getTimeStampTokenForSignature complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getTimeStampTokenForSignature">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="signatureHex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTimeStampTokenForSignature", propOrder = {
    "signatureHex"
})
public class GetTimeStampTokenForSignature {

    protected String signatureHex;

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

}
