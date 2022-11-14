
package uz.maroqand.ecology.core.integration.upay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for confirmPayment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="confirmPayment">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="confirmPaymentRequest" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="StPimsApiPartnerKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *                   &lt;element name="OneTimePassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Lang" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "confirmPayment", propOrder = {
    "confirmPaymentRequest"
})
public class ConfirmPayment {

    protected ConfirmPayment.ConfirmPaymentRequest confirmPaymentRequest;

    /**
     * Gets the value of the confirmPaymentRequest property.
     *
     * @return
     *     possible object is
     *     {@link ConfirmPayment.ConfirmPaymentRequest }
     *
     */
    public ConfirmPayment.ConfirmPaymentRequest getConfirmPaymentRequest() {
        return confirmPaymentRequest;
    }

    /**
     * Sets the value of the confirmPaymentRequest property.
     *
     * @param value
     *     allowed object is
     *     {@link ConfirmPayment.ConfirmPaymentRequest }
     *
     */
    public void setConfirmPaymentRequest(ConfirmPayment.ConfirmPaymentRequest value) {
        this.confirmPaymentRequest = value;
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
     *         &lt;element name="StPimsApiPartnerKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
     *         &lt;element name="OneTimePassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Lang" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "stPimsApiPartnerKey",
        "transactionId",
        "oneTimePassword",
        "lang"
    })
    public static class ConfirmPaymentRequest {

        @XmlElement(name = "StPimsApiPartnerKey")
        protected String stPimsApiPartnerKey;
        @XmlElement(name = "TransactionId")
        protected Long transactionId;
        @XmlElement(name = "OneTimePassword")
        protected String oneTimePassword;
        @XmlElement(name = "Lang")
        protected String lang;

        /**
         * Gets the value of the stPimsApiPartnerKey property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStPimsApiPartnerKey() {
            return stPimsApiPartnerKey;
        }

        /**
         * Sets the value of the stPimsApiPartnerKey property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStPimsApiPartnerKey(String value) {
            this.stPimsApiPartnerKey = value;
        }

        /**
         * Gets the value of the transactionId property.
         * 
         * @return
         *     possible object is
         *     {@link Long }
         *     
         */
        public Long getTransactionId() {
            return transactionId;
        }

        /**
         * Sets the value of the transactionId property.
         * 
         * @param value
         *     allowed object is
         *     {@link Long }
         *     
         */
        public void setTransactionId(Long value) {
            this.transactionId = value;
        }

        /**
         * Gets the value of the oneTimePassword property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOneTimePassword() {
            return oneTimePassword;
        }

        /**
         * Sets the value of the oneTimePassword property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOneTimePassword(String value) {
            this.oneTimePassword = value;
        }

        /**
         * Gets the value of the lang property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLang() {
            return lang;
        }

        /**
         * Sets the value of the lang property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLang(String value) {
            this.lang = value;
        }

    }

}
