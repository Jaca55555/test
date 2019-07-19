package uz.maroqand.ecology.ecoexpertise.mips;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;
import uz.maroqand.ecology.ecoexpertise.mips.le_info_by_tin.GetLegalEntityInfo;
import uz.maroqand.ecology.ecoexpertise.mips.le_info_by_tin.LEGALENTITYINFORMATION;
import uz.maroqand.ecology.ecoexpertise.mips.le_info_by_tin.LegalEntityInfoResponse;

@Service
public class MIPLegalEntityInfoService extends WebServiceGatewaySupport implements APIService {

    private static final String HASHKEY = "E001|oDlpS4ZiwZEd5giUkMSxKXBIb9bOafcOJOBXRnzuVyepiReYCylXFRL4qz9gV";

    private static final String WSDL_URL = "https://ips.gov.uz/mediate/ips/ILDS/GetLegalEntityInfo?wsdl";

    private static final Integer CONNECTION_TIMEOUT = 4000;
    private static final Integer READ_TIMEOUT = 4000;

    private static final String testingTIN = "302940689";

    public MIPLegalEntityInfoService() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECTION_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        setMessageSender(new ClientHttpRequestMessageSender(requestFactory));

        super.setDefaultUri(WSDL_URL);
    }

    public LegalEntityInfoResponse getLegalEntityInfoByTIN(String TIN) {

        GetLegalEntityInfo request = new GetLegalEntityInfo();

        GetLegalEntityInfo.Msg msg = new GetLegalEntityInfo.Msg();
        msg.setLHASHKEY(HASHKEY);
        msg.setTIN(TIN);
        request.setMsg(msg);

        LegalEntityInfoResponse legalEntityInfoResponse = new LegalEntityInfoResponse();
        try {
            legalEntityInfoResponse.setResponse((LEGALENTITYINFORMATION) getWebServiceTemplate().marshalSendAndReceive(WSDL_URL, request));
        } catch (Exception e) {
            e.printStackTrace();
            legalEntityInfoResponse.setResponseStatusCode(-1);
        }

        return legalEntityInfoResponse;
    }


    @Override
    public boolean isAvailable() {
        GetLegalEntityInfo request = new GetLegalEntityInfo();

        GetLegalEntityInfo.Msg msg = new GetLegalEntityInfo.Msg();
        msg.setLHASHKEY(HASHKEY);
        msg.setTIN(testingTIN);
        request.setMsg(msg);

        LegalEntityInfoResponse legalEntityInfoResponse = new LegalEntityInfoResponse();
        try {
            legalEntityInfoResponse.setResponse((LEGALENTITYINFORMATION) getWebServiceTemplate().marshalSendAndReceive(WSDL_URL, request));
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
