//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.28 at 11:09:09 PM UZT 
//


package uz.maroqand.ecology.ecoexpertise.mips.i_address_info;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the mip.leinfo.bytin package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mip.leinfo.bytin
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ResponseModel }
     * 
     */
    public ResponseModel createResponseModel() {
        return new ResponseModel();
    }

    /**
     * Create an instance of {@link Request }
     * 
     */
    public Request createRequest() {
        return new Request();
    }

    /**
     * Create an instance of {@link ResponseModel.PPermanentAddress }
     * 
     */
    public ResponseModel.PPermanentAddress createResponseModelPPermanentAddress() {
        return new ResponseModel.PPermanentAddress();
    }

    /**
     * Create an instance of {@link ResponseModel.PTemproaryAddress }
     * 
     */
    public ResponseModel.PTemproaryAddress createResponseModelPTemproaryAddress() {
        return new ResponseModel.PTemproaryAddress();
    }

}
