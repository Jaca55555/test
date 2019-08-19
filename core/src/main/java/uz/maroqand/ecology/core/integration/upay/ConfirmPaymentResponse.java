
package uz.maroqand.ecology.core.integration.upay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for confirmPaymentResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="confirmPaymentResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ServiceId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                   &lt;element name="CardNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="PersonalAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="PaymentAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="PaymentTransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="PaymentPerformedTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Terminal" type="{http://st.apus.com/}Terminal" minOccurs="0"/>
 *                   &lt;element name="Result" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
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
@XmlType(name = "confirmPaymentResponse", propOrder = {
    "_return"
})
public class ConfirmPaymentResponse {

    @XmlElement(name = "return")
    protected ConfirmPaymentResponse.Return _return;

    /**
     * Gets the value of the return property.
     *
     * @return
     *     possible object is
     *     {@link ConfirmPaymentResponse.Return }
     *
     */
    public ConfirmPaymentResponse.Return getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     *
     * @param value
     *     allowed object is
     *     {@link ConfirmPaymentResponse.Return }
     *
     */
    public void setReturn(ConfirmPaymentResponse.Return value) {
        this._return = value;
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
     *         &lt;element name="ServiceId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *         &lt;element name="CardNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="PersonalAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="PaymentAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="PaymentTransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="PaymentPerformedTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Terminal" type="{http://st.apus.com/}Terminal" minOccurs="0"/>
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
        "serviceId",
        "cardNumber",
        "personalAccount",
        "paymentAmount",
        "paymentTransactionId",
        "userName",
        "paymentPerformedTime",
        "terminal",
        "result"
    })
    public static class Return {

        @XmlElement(name = "ServiceId")
        protected Integer serviceId;
        @XmlElement(name = "CardNumber")
        protected String cardNumber;
        @XmlElement(name = "PersonalAccount")
        protected String personalAccount;
        @XmlElement(name = "PaymentAmount")
        protected String paymentAmount;
        @XmlElement(name = "PaymentTransactionId")
        protected String paymentTransactionId;
        @XmlElement(name = "UserName")
        protected String userName;
        @XmlElement(name = "PaymentPerformedTime")
        protected String paymentPerformedTime;
        @XmlElement(name = "Terminal")
        protected Terminal terminal;
        @XmlElement(name = "Result")
        protected ConfirmPaymentResponse.Return.Result result;

        /**
         * Gets the value of the serviceId property.
         *
         * @return
         *     possible object is
         *     {@link Integer }
         *
         */
        public Integer getServiceId() {
            return serviceId;
        }

        /**
         * Sets the value of the serviceId property.
         *
         * @param value
         *     allowed object is
         *     {@link Integer }
         *
         */
        public void setServiceId(Integer value) {
            this.serviceId = value;
        }

        /**
         * Gets the value of the cardNumber property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCardNumber() {
            return cardNumber;
        }

        /**
         * Sets the value of the cardNumber property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCardNumber(String value) {
            this.cardNumber = value;
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
         * Gets the value of the paymentTransactionId property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getPaymentTransactionId() {
            return paymentTransactionId;
        }

        /**
         * Sets the value of the paymentTransactionId property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setPaymentTransactionId(String value) {
            this.paymentTransactionId = value;
        }

        /**
         * Gets the value of the userName property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getUserName() {
            return userName;
        }

        /**
         * Sets the value of the userName property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setUserName(String value) {
            this.userName = value;
        }

        /**
         * Gets the value of the paymentPerformedTime property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getPaymentPerformedTime() {
            return paymentPerformedTime;
        }

        /**
         * Sets the value of the paymentPerformedTime property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setPaymentPerformedTime(String value) {
            this.paymentPerformedTime = value;
        }

        /**
         * Gets the value of the terminal property.
         *
         * @return
         *     possible object is
         *     {@link Terminal }
         *
         */
        public Terminal getTerminal() {
            return terminal;
        }

        /**
         * Sets the value of the terminal property.
         *
         * @param value
         *     allowed object is
         *     {@link Terminal }
         *
         */
        public void setTerminal(Terminal value) {
            this.terminal = value;
        }

        /**
         * Gets the value of the result property.
         *
         * @return
         *     possible object is
         *     {@link ConfirmPaymentResponse.Return.Result }
         *
         */
        public ConfirmPaymentResponse.Return.Result getResult() {
            return result;
        }

        /**
         * Sets the value of the result property.
         *
         * @param value
         *     allowed object is
         *     {@link ConfirmPaymentResponse.Return.Result }
         *
         */
        public void setResult(ConfirmPaymentResponse.Return.Result value) {
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

}
