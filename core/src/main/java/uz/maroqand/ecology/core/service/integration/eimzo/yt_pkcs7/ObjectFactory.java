
package uz.maroqand.ecology.core.service.integration.eimzo.yt_pkcs7;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the z.eimzo.yt_pkcs7 package. 
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

    private final static QName _CreatePkcs7Response_QNAME = new QName("http://v1.pkcs7.plugin.ss.capi.yt.uz/", "createPkcs7Response");
    private final static QName _VerifyPkcs7Response_QNAME = new QName("http://v1.pkcs7.plugin.ss.capi.yt.uz/", "verifyPkcs7Response");
    private final static QName _VerifyPkcs7_QNAME = new QName("http://v1.pkcs7.plugin.ss.capi.yt.uz/", "verifyPkcs7");
    private final static QName _CreatePkcs7_QNAME = new QName("http://v1.pkcs7.plugin.ss.capi.yt.uz/", "createPkcs7");
    private final static QName _CreatePkcs7Document_QNAME = new QName("", "document");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: z.eimzo.yt_pkcs7
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CreatePkcs7Response }
     * 
     */
    public CreatePkcs7Response createCreatePkcs7Response() {
        return new CreatePkcs7Response();
    }

    /**
     * Create an instance of {@link VerifyPkcs7Response }
     * 
     */
    public VerifyPkcs7Response createVerifyPkcs7Response() {
        return new VerifyPkcs7Response();
    }

    /**
     * Create an instance of {@link VerifyPkcs7 }
     * 
     */
    public VerifyPkcs7 createVerifyPkcs7() {
        return new VerifyPkcs7();
    }

    /**
     * Create an instance of {@link CreatePkcs7 }
     * 
     */
    public CreatePkcs7 createCreatePkcs7() {
        return new CreatePkcs7();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreatePkcs7Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.pkcs7.plugin.ss.capi.yt.uz/", name = "createPkcs7Response")
    public JAXBElement<CreatePkcs7Response> createCreatePkcs7Response(CreatePkcs7Response value) {
        return new JAXBElement<CreatePkcs7Response>(_CreatePkcs7Response_QNAME, CreatePkcs7Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyPkcs7Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.pkcs7.plugin.ss.capi.yt.uz/", name = "verifyPkcs7Response")
    public JAXBElement<VerifyPkcs7Response> createVerifyPkcs7Response(VerifyPkcs7Response value) {
        return new JAXBElement<VerifyPkcs7Response>(_VerifyPkcs7Response_QNAME, VerifyPkcs7Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyPkcs7 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.pkcs7.plugin.ss.capi.yt.uz/", name = "verifyPkcs7")
    public JAXBElement<VerifyPkcs7> createVerifyPkcs7(VerifyPkcs7 value) {
        return new JAXBElement<VerifyPkcs7>(_VerifyPkcs7_QNAME, VerifyPkcs7 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreatePkcs7 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v1.pkcs7.plugin.ss.capi.yt.uz/", name = "createPkcs7")
    public JAXBElement<CreatePkcs7> createCreatePkcs7(CreatePkcs7 value) {
        return new JAXBElement<CreatePkcs7>(_CreatePkcs7_QNAME, CreatePkcs7 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "document", scope = CreatePkcs7 .class)
    public JAXBElement<byte[]> createCreatePkcs7Document(byte[] value) {
        return new JAXBElement<byte[]>(_CreatePkcs7Document_QNAME, byte[].class, CreatePkcs7 .class, ((byte[]) value));
    }

}
