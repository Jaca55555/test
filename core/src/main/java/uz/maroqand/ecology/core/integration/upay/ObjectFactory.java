
package uz.maroqand.ecology.core.integration.upay;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uz.maroqand.ecology.core.integration.upay package.
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PreparePaymentRequest_QNAME = new QName("http://st.apus.com/", "PreparePaymentRequest");
    private final static QName _ConfirmPayment_QNAME = new QName("http://st.apus.com/", "confirmPayment");
    private final static QName _PrepaymentResponse_QNAME = new QName("http://st.apus.com/", "prepaymentResponse");
    private final static QName _ConfirmPaymentResponse_QNAME = new QName("http://st.apus.com/", "confirmPaymentResponse");
    private final static QName _Prepayment_QNAME = new QName("http://st.apus.com/", "prepayment");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uz.maroqand.ecology.core.integration.upay
     * 
     */
    public ObjectFactory() {
    }


    /**
     * Create an instance of {@link BasePreparePaymentResponse }
     * 
     */
    public BasePreparePaymentResponse createBasePreparePaymentResponse() {
        return new BasePreparePaymentResponse();
    }

    /**
     * Create an instance of {@link BaseConfirmPaymentResponse }
     * 
     */
    public BaseConfirmPaymentResponse createBaseConfirmPaymentResponse() {
        return new BaseConfirmPaymentResponse();
    }


    /**
     * Create an instance of {@link ConfirmPayment }
     * 
     */
    public ConfirmPayment createConfirmPayment() {
        return new ConfirmPayment();
    }


    /**
     * Create an instance of {@link PrepaymentResponse }
     * 
     */
    public PrepaymentResponse createPrepaymentResponse() {
        return new PrepaymentResponse();
    }

    /**
     * Create an instance of {@link PrepaymentResponse.Return }
     * 
     */
    public PrepaymentResponse.Return createPrepaymentResponseReturn() {
        return new PrepaymentResponse.Return();
    }

    /**
     * Create an instance of {@link ConfirmPaymentResponse }
     * 
     */
    public ConfirmPaymentResponse createConfirmPaymentResponse() {
        return new ConfirmPaymentResponse();
    }

    /**
     * Create an instance of {@link ConfirmPaymentResponse.Return }
     * 
     */
    public ConfirmPaymentResponse.Return createConfirmPaymentResponseReturn() {
        return new ConfirmPaymentResponse.Return();
    }

    /**
     * Create an instance of {@link Prepayment }
     * 
     */
    public Prepayment createPrepayment() {
        return new Prepayment();
    }


    /**
     * Create an instance of {@link Terminal }
     * 
     */
    public Terminal createTerminal() {
        return new Terminal();
    }

    /**
     * Create an instance of {@link NamedParam }
     * 
     */
    public NamedParam createNamedParam() {
        return new NamedParam();
    }

    /**
     * Create an instance of {@link uz.maroqand.ecology.core.integration.upay.Result }
     * 
     */
    public uz.maroqand.ecology.core.integration.upay.Result createResult() {
        return new uz.maroqand.ecology.core.integration.upay.Result();
    }


    /**
     * Create an instance of {@link AccountParams }
     * 
     */
    public AccountParams createAccountParams() {
        return new AccountParams();
    }


    /**
     * Create an instance of {@link BasePreparePaymentResponse.Result }
     * 
     */
    public BasePreparePaymentResponse.Result createBasePreparePaymentResponseResult() {
        return new BasePreparePaymentResponse.Result();
    }

    /**
     * Create an instance of {@link uz.maroqand.ecology.core.integration.upay.ConfirmPaymentRequest }
     * 
     */
    public uz.maroqand.ecology.core.integration.upay.ConfirmPaymentRequest createConfirmPaymentRequest() {
        return new uz.maroqand.ecology.core.integration.upay.ConfirmPaymentRequest();
    }

    /**
     * Create an instance of {@link BaseConfirmPaymentResponse.Result }
     * 
     */
    public BaseConfirmPaymentResponse.Result createBaseConfirmPaymentResponseResult() {
        return new BaseConfirmPaymentResponse.Result();
    }

    /**
     * Create an instance of {@link ConfirmPayment.ConfirmPaymentRequest }
     * 
     */
    public ConfirmPayment.ConfirmPaymentRequest createConfirmPaymentConfirmPaymentRequest() {
        return new ConfirmPayment.ConfirmPaymentRequest();
    }

    /**

    /**
     * Create an instance of {@link PrepaymentResponse.Return.Result }
     * 
     */
    public PrepaymentResponse.Return.Result createPrepaymentResponseReturnResult() {
        return new PrepaymentResponse.Return.Result();
    }



    /**
     * Create an instance of {@link ConfirmPaymentResponse.Return.Result }
     * 
     */
    public ConfirmPaymentResponse.Return.Result createConfirmPaymentResponseReturnResult() {
        return new ConfirmPaymentResponse.Return.Result();
    }

    /**
     * Create an instance of {@link Prepayment.PrepaymentRequest }
     * 
     */
    public Prepayment.PrepaymentRequest createPrepaymentPrepaymentRequest() {
        return new Prepayment.PrepaymentRequest();
    }



    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://st.apus.com/", name = "PreparePaymentRequest")
    public JAXBElement<Object> createPreparePaymentRequest(Object value) {
        return new JAXBElement<Object>(_PreparePaymentRequest_QNAME, Object.class, null, value);
    }



    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConfirmPayment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://st.apus.com/", name = "confirmPayment")
    public JAXBElement<ConfirmPayment> createConfirmPayment(ConfirmPayment value) {
        return new JAXBElement<ConfirmPayment>(_ConfirmPayment_QNAME, ConfirmPayment.class, null, value);
    }



    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrepaymentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://st.apus.com/", name = "prepaymentResponse")
    public JAXBElement<PrepaymentResponse> createPrepaymentResponse(PrepaymentResponse value) {
        return new JAXBElement<PrepaymentResponse>(_PrepaymentResponse_QNAME, PrepaymentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConfirmPaymentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://st.apus.com/", name = "confirmPaymentResponse")
    public JAXBElement<ConfirmPaymentResponse> createConfirmPaymentResponse(ConfirmPaymentResponse value) {
        return new JAXBElement<ConfirmPaymentResponse>(_ConfirmPaymentResponse_QNAME, ConfirmPaymentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Prepayment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://st.apus.com/", name = "prepayment")
    public JAXBElement<Prepayment> createPrepayment(Prepayment value) {
        return new JAXBElement<Prepayment>(_Prepayment_QNAME, Prepayment.class, null, value);
    }

}
