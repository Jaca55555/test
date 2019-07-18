package uz.maroqand.ecology.ecoexpertise.mips;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;
import uz.maroqand.ecology.ecoexpertise.mips.i_tax_info_by_tin.GetTaxInfobyTinReq;
import uz.maroqand.ecology.ecoexpertise.mips.i_tax_info_by_tin.GetTaxInfobyTinRes;
import uz.maroqand.ecology.ecoexpertise.mips.i_tax_info_by_tin.IndividualsTaxInfoResponse;

public class MIPIndividualsTaxInfoByTINService extends WebServiceGatewaySupport implements APIService {

    private static final String WSDL_URL = "https://ips.gov.uz/mediate/ips/STC/GetTaxInfobyTin?wsdl";

    private static final Integer CONNECTION_TIMEOUT = 4000;
    private static final Integer READ_TIMEOUT = 4000;

    private static final String testingTIN = "302940689";

    private static final String paramAction = "get_by_tin";

    private static final String paramLangUzbek = "1";
//    private static final String paramLangRussian = "2";

    public MIPIndividualsTaxInfoByTINService() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECTION_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        setMessageSender(new ClientHttpRequestMessageSender(requestFactory));

        super.setDefaultUri(WSDL_URL);
    }

    public IndividualsTaxInfoResponse getInfoBy(String TIN) {
        GetTaxInfobyTinReq request = new GetTaxInfobyTinReq();

        request.setAction(paramAction);
        request.setLang(paramLangUzbek);
        request.setTin(TIN);


        IndividualsTaxInfoResponse taxInfoResponse = new IndividualsTaxInfoResponse();
//        LegalEntityInfoResponse legalEntityInfoResponse = new LegalEntityInfoResponse();
        try{
            taxInfoResponse.setResponse((GetTaxInfobyTinRes) getWebServiceTemplate().marshalSendAndReceive(WSDL_URL, request));
        }
        catch (Exception e) {
            e.printStackTrace();
            taxInfoResponse.setReponseStatusCode(-1);
        }

        return taxInfoResponse;
    }

    @Override
    public boolean isAvailable() {
        GetTaxInfobyTinReq request = new GetTaxInfobyTinReq();

        request.setAction(paramAction);
        request.setLang(paramLangUzbek);
        request.setTin(testingTIN);


        IndividualsTaxInfoResponse taxInfoResponse = new IndividualsTaxInfoResponse();
//        LegalEntityInfoResponse legalEntityInfoResponse = new LegalEntityInfoResponse();
        try{
            taxInfoResponse.setResponse((GetTaxInfobyTinRes) getWebServiceTemplate().marshalSendAndReceive(WSDL_URL, request));
        }
        catch (Exception e) {
            e.printStackTrace();
            taxInfoResponse.setReponseStatusCode(-1);
            return false;
        }

        return true;
    }
}
