
package uz.maroqand.ecology.core.integration.upay;

import javax.xml.namespace.QName;
import javax.xml.ws.*;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6b21 
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "STWS", targetNamespace = "http://st.apus.com/", wsdlLocation = "http://91.212.89.86:9212/STAPI/STWS?wsdl")
public class STWS_Service
    extends Service
{

    private final static URL STWS_WSDL_LOCATION;
    private final static WebServiceException STWS_EXCEPTION;
    private final static QName STWS_QNAME = new QName("http://st.apus.com/", "STWS");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://91.212.89.86:9212/STAPI/STWS?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        STWS_WSDL_LOCATION = url;
        STWS_EXCEPTION = e;
    }

    public STWS_Service() {
        super(__getWsdlLocation(), STWS_QNAME);
    }

    public STWS_Service(WebServiceFeature... features) {
        super(__getWsdlLocation(), STWS_QNAME, features);
    }

    public STWS_Service(URL wsdlLocation) {
        super(wsdlLocation, STWS_QNAME);
    }

    public STWS_Service(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, STWS_QNAME, features);
    }

    public STWS_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public STWS_Service(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns STWS
     */
    @WebEndpoint(name = "STWSPort")
    public STWS getSTWSPort() {
        return super.getPort(new QName("http://st.apus.com/", "STWSPort"), STWS.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns STWS
     */
    @WebEndpoint(name = "STWSPort")
    public STWS getSTWSPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://st.apus.com/", "STWSPort"), STWS.class, features);
    }

    private static URL __getWsdlLocation() {
        if (STWS_EXCEPTION!= null) {
            throw STWS_EXCEPTION;
        }
        return STWS_WSDL_LOCATION;
    }

}
