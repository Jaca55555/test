
package uz.maroqand.ecology.core.service.integration.eimzo.yt_cryptoauth;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the z.eimzo.yt_cryptoauth package. 
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

    private final static QName _GetDigestResponse_QNAME = new QName("http://v1.cryptoauth.plugin.ss.capi.yt.uz/", "getDigestResponse");
    private final static QName _VerifyDigestResponse_QNAME = new QName("http://v1.cryptoauth.plugin.ss.capi.yt.uz/", "verifyDigestResponse");
    private final static QName _VerifyDigest_QNAME = new QName("http://v1.cryptoauth.plugin.ss.capi.yt.uz/", "verifyDigest");
    private final static QName _VerifySignature_QNAME = new QName("http://v1.cryptoauth.plugin.ss.capi.yt.uz/", "verifySignature");
    private final static QName _GetDigest_QNAME = new QName("http://v1.cryptoauth.plugin.ss.capi.yt.uz/", "getDigest");
    private final static QName _VerifySignatureResponse_QNAME = new QName("http://v1.cryptoauth.plugin.ss.capi.yt.uz/", "verifySignatureResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: z.eimzo.yt_cryptoauth
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link VerifySignature }
     * 
     */
    public VerifySignature createVerifySignature() {
        return new VerifySignature();
    }

    /**
     * Create an instance of {@link GetDigest }
     * 
     */
    public GetDigest createGetDigest() {
        return new GetDigest();
    }

    /**
     * Create an instance of {@link VerifySignatureResponse }
     * 
     */
    public VerifySignatureResponse createVerifySignatureResponse() {
        return new VerifySignatureResponse();
    }

    /**
     * Create an instance of {@link GetDigestResponse }
     * 
     */
    public GetDigestResponse createGetDigestResponse() {
        return new GetDigestResponse();
    }

    /**
     * Create an instance of {@link VerifyDigestResponse }
     * 
     */
    public VerifyDigestResponse createVerifyDigestResponse() {
        return new VerifyDigestResponse();
    }

    /**
     * Create an instance of {@link VerifyDigest }
     * 
     */
    public VerifyDigest createVerifyDigest() {
        return new VerifyDigest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDigestResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.cryptoauth.plugin.ss.capi.yt.uz/", name = "getDigestResponse")
    public JAXBElement<GetDigestResponse> createGetDigestResponse(GetDigestResponse value) {
        return new JAXBElement<GetDigestResponse>(_GetDigestResponse_QNAME, GetDigestResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyDigestResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.cryptoauth.plugin.ss.capi.yt.uz/", name = "verifyDigestResponse")
    public JAXBElement<VerifyDigestResponse> createVerifyDigestResponse(VerifyDigestResponse value) {
        return new JAXBElement<VerifyDigestResponse>(_VerifyDigestResponse_QNAME, VerifyDigestResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyDigest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.cryptoauth.plugin.ss.capi.yt.uz/", name = "verifyDigest")
    public JAXBElement<VerifyDigest> createVerifyDigest(VerifyDigest value) {
        return new JAXBElement<VerifyDigest>(_VerifyDigest_QNAME, VerifyDigest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifySignature }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.cryptoauth.plugin.ss.capi.yt.uz/", name = "verifySignature")
    public JAXBElement<VerifySignature> createVerifySignature(VerifySignature value) {
        return new JAXBElement<VerifySignature>(_VerifySignature_QNAME, VerifySignature.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDigest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.cryptoauth.plugin.ss.capi.yt.uz/", name = "getDigest")
    public JAXBElement<GetDigest> createGetDigest(GetDigest value) {
        return new JAXBElement<GetDigest>(_GetDigest_QNAME, GetDigest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifySignatureResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.cryptoauth.plugin.ss.capi.yt.uz/", name = "verifySignatureResponse")
    public JAXBElement<VerifySignatureResponse> createVerifySignatureResponse(VerifySignatureResponse value) {
        return new JAXBElement<VerifySignatureResponse>(_VerifySignatureResponse_QNAME, VerifySignatureResponse.class, null, value);
    }

}
