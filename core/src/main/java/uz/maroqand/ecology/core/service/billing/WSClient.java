package uz.maroqand.ecology.core.service.billing;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.integration.upay.*;

import javax.xml.ws.BindingProvider;


/**
 * Created by Utkirbek Boltaev on 23.05.2018.
 * (uz)
 * (ru)
 */
@Service
public class WSClient {

//    private static final String UPAY_API_KEY = "B337E84DE8752B27EDA3A12363109E80";
    private static final String UPAY_API_KEY = "2291D2EC3B3048D1A6F86C2C4591B7E0";
    private static final Integer SERVICE_ID = 502;
    private static final String LANG = "ru";
    private static WSClient wsClient = null;
    private STWS_Service service;
    private STWS port;

//    public WSClient() {
//        service = new STWS_Service();
//        port = service.getSTWSPort();mdi-credit-card
//    }

    public WSClient() {
        STWS_Service service = new STWS_Service();
        port = service.getSTWSPort();
        BindingProvider provider = (BindingProvider) port;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "https://api.upay.uz/STAPI/STWS?wsdl");
    }

    public static synchronized WSClient getInstance(){
        if (wsClient == null)
            wsClient=new WSClient();
        return wsClient;
    }

    public PrepaymentResponse.Return prepayment(Prepayment.PrepaymentRequest prepaymentRequest) {
        prepaymentRequest.setStPimsApiPartnerKey(UPAY_API_KEY);
        prepaymentRequest.setServiceId(SERVICE_ID);
        prepaymentRequest.setLang(LANG);
        return port.prepayment(prepaymentRequest);
    }

    public ConfirmPaymentResponse.Return confirmPayment(ConfirmPayment.ConfirmPaymentRequest confirmPaymentRequest) {
        confirmPaymentRequest.setStPimsApiPartnerKey(UPAY_API_KEY);
        return port.confirmPayment(confirmPaymentRequest);
    }

}
