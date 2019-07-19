package uz.maroqand.ecology.ecoexpertise.mips;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;
import uz.maroqand.ecology.ecoexpertise.mips.i_tax_info_by_pin.GetTaxInfobyPinReq;
import uz.maroqand.ecology.ecoexpertise.mips.i_tax_info_by_pin.GetTaxInfobyPinRes;
import uz.maroqand.ecology.ecoexpertise.mips.i_tax_info_by_pin.IndividualsTaxInfoByPINFLResponse;

public class MIPIndividualsTaxInfoByPINFLService extends WebServiceGatewaySupport implements APIService {

    private static final String WSDL_URL = "https://ips.gov.uz/mediate/ips/STC/GetTaxInfobyPin?wsdl";

    private static final Integer CONNECTION_TIMEOUT = 4000;
    private static final Integer READ_TIMEOUT = 4000;

    private static final String testingPINFL = "302940689";

    private static final String paramAction = "get_by_pin";

    private static final String paramLangUzbek = "1";
//    private static final String paramLangRussian = "2";

    public MIPIndividualsTaxInfoByPINFLService() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECTION_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        setMessageSender(new ClientHttpRequestMessageSender(requestFactory));

        super.setDefaultUri(WSDL_URL);
    }




    public IndividualsTaxInfoByPINFLResponse getInfoByPINFL(String pinfl) {
        GetTaxInfobyPinReq request = new GetTaxInfobyPinReq();

        request.setAction(paramAction);
        request.setLang(paramLangUzbek);
        request.setPin(pinfl);


        IndividualsTaxInfoByPINFLResponse taxInfoResponse = new IndividualsTaxInfoByPINFLResponse();
//        LegalEntityInfoResponse legalEntityInfoResponse = new LegalEntityInfoResponse();
        try{
            taxInfoResponse.setResponse((GetTaxInfobyPinRes) getWebServiceTemplate().marshalSendAndReceive(WSDL_URL, request));
            if (taxInfoResponse.getResponse() != null && "0".equals(taxInfoResponse.getResponse().getErrCode())) {
                taxInfoResponse.setReponseStatusCode(APIResponse.APIResponseStatusCodes.Successfull);
            }
        } catch (Exception e) {
            e.printStackTrace();
            taxInfoResponse.setReponseStatusCode(-1);
        }

        return taxInfoResponse;
    }

    @Override
    public boolean isAvailable() {
        GetTaxInfobyPinReq request = new GetTaxInfobyPinReq();

        request.setAction(paramAction);
        request.setLang(paramLangUzbek);
        request.setPin(testingPINFL);


        IndividualsTaxInfoByPINFLResponse taxInfoResponse = new IndividualsTaxInfoByPINFLResponse();
//        LegalEntityInfoResponse legalEntityInfoResponse = new LegalEntityInfoResponse();
        try{
            taxInfoResponse.setResponse((GetTaxInfobyPinRes) getWebServiceTemplate().marshalSendAndReceive(WSDL_URL, request));
        }
        catch (Exception e) {
            e.printStackTrace();
            taxInfoResponse.setReponseStatusCode(-1);
            return false;
        }

        return true;
    }
}


