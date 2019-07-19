
package uz.maroqand.ecology.core.integration.eimzo.yt_tsaproxy;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the z.eimzo.yt_tsaproxy package. 
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

    private final static QName _GetTimeStampTokenForSignature_QNAME = new QName("http://v1.tsaproxy.plugin.ss.capi.yt.uz/", "getTimeStampTokenForSignature");
    private final static QName _GetTimeStampTokenForSignatureResponse_QNAME = new QName("http://v1.tsaproxy.plugin.ss.capi.yt.uz/", "getTimeStampTokenForSignatureResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: z.eimzo.yt_tsaproxy
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetTimeStampTokenForSignatureResponse }
     * 
     */
    public GetTimeStampTokenForSignatureResponse createGetTimeStampTokenForSignatureResponse() {
        return new GetTimeStampTokenForSignatureResponse();
    }

    /**
     * Create an instance of {@link GetTimeStampTokenForSignature }
     * 
     */
    public GetTimeStampTokenForSignature createGetTimeStampTokenForSignature() {
        return new GetTimeStampTokenForSignature();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTimeStampTokenForSignature }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.tsaproxy.plugin.ss.capi.yt.uz/", name = "getTimeStampTokenForSignature")
    public JAXBElement<GetTimeStampTokenForSignature> createGetTimeStampTokenForSignature(GetTimeStampTokenForSignature value) {
        return new JAXBElement<GetTimeStampTokenForSignature>(_GetTimeStampTokenForSignature_QNAME, GetTimeStampTokenForSignature.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTimeStampTokenForSignatureResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.tsaproxy.plugin.ss.capi.yt.uz/", name = "getTimeStampTokenForSignatureResponse")
    public JAXBElement<GetTimeStampTokenForSignatureResponse> createGetTimeStampTokenForSignatureResponse(GetTimeStampTokenForSignatureResponse value) {
        return new JAXBElement<GetTimeStampTokenForSignatureResponse>(_GetTimeStampTokenForSignatureResponse_QNAME, GetTimeStampTokenForSignatureResponse.class, null, value);
    }

}
