
package uz.maroqand.ecology.core.integration.upay;

import javax.xml.bind.annotation.*;


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
 *         &lt;element name="ServiceCategoryName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServiceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PersonalAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PaymentAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="Result" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "", propOrder = {
    "serviceCategoryName",
    "serviceName",
    "personalAccount",
    "paymentAmount",
    "transactionId",
    "result"
})
@XmlRootElement(name = "BasePreparePaymentResponse")
public class BasePreparePaymentResponse {

    @XmlElement(name = "ServiceCategoryName")
    protected String serviceCategoryName;
    @XmlElement(name = "ServiceName")
    protected String serviceName;
    @XmlElement(name = "PersonalAccount")
    protected String personalAccount;
    @XmlElement(name = "PaymentAmount")
    protected String paymentAmount;
    @XmlElement(name = "TransactionId")
    protected Long transactionId;
    @XmlElement(name = "Result")
    protected BasePreparePaymentResponse.Result result;

    /**
     * Gets the value of the serviceCategoryName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getServiceCategoryName() {
        return serviceCategoryName;
    }

    /**
     * Sets the value of the serviceCategoryName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setServiceCategoryName(String value) {
        this.serviceCategoryName = value;
    }

    /**
     * Gets the value of the serviceName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setServiceName(String value) {
        this.serviceName = value;
    }

    /**
     * Gets the value of the personalAccount property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPersonalAccount() {
        return personalAccount;
    }

    /**
     * Sets the value of the personalAccount property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPersonalAccount(String value) {
        this.personalAccount = value;
    }

    /**
     * Gets the value of the paymentAmount property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * Sets the value of the paymentAmount property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPaymentAmount(String value) {
        this.paymentAmount = value;
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
     * Gets the value of the result property.
     *
     * @return
     *     possible object is
     *     {@link BasePreparePaymentResponse.Result }
     *
     */
    public BasePreparePaymentResponse.Result getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     *
     * @param value
     *     allowed object is
     *     {@link BasePreparePaymentResponse.Result }
     *
     */
    public void setResult(BasePreparePaymentResponse.Result value) {
        this.result = value;
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
     *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "code",
        "description"
    })
    public static class Result {

        protected String code;
        @XmlElement(name = "Description")
        protected String description;

        /**
         * Gets the value of the code property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCode() {
            return code;
        }

        /**
         * Sets the value of the code property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCode(String value) {
            this.code = value;
        }

        /**
         * Gets the value of the description property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the value of the description property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescription(String value) {
            this.description = value;
        }

    }

}
