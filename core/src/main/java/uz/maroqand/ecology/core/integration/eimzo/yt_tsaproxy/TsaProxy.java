
package uz.maroqand.ecology.core.integration.eimzo.yt_tsaproxy;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6b21 
 * Generated source version: 2.2
 * 
 */
@WebService(name = "TsaProxy", targetNamespace = "http://v1.tsaproxy.plugin.ss.capi.yt.uz/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface TsaProxy {


    /**
     * 
     * @param signatureHex
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getTimeStampTokenForSignature", targetNamespace = "http://v1.tsaproxy.plugin.ss.capi.yt.uz/", className = "z.eimzo.yt_tsaproxy.GetTimeStampTokenForSignature")
    @ResponseWrapper(localName = "getTimeStampTokenForSignatureResponse", targetNamespace = "http://v1.tsaproxy.plugin.ss.capi.yt.uz/", className = "z.eimzo.yt_tsaproxy.GetTimeStampTokenForSignatureResponse")
    @Action(input = "http://v1.tsaproxy.plugin.ss.capi.yt.uz/TsaProxy/getTimeStampTokenForSignatureRequest", output = "http://v1.tsaproxy.plugin.ss.capi.yt.uz/TsaProxy/getTimeStampTokenForSignatureResponse")
    String getTimeStampTokenForSignature(
            @WebParam(name = "signatureHex", targetNamespace = "")
                    String signatureHex);

}
