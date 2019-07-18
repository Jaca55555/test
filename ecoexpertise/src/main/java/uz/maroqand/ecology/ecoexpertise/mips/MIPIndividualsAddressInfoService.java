package uz.maroqand.ecology.ecoexpertise.mips;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;
import uz.maroqand.ecology.ecoexpertise.mips.i_address_info.IndividualAddressInfoResponse;
import uz.maroqand.ecology.ecoexpertise.mips.i_address_info.Request;
import uz.maroqand.ecology.ecoexpertise.mips.i_address_info.ResponseModel;

public class MIPIndividualsAddressInfoService
        extends WebServiceGatewaySupport implements APIService {
    private static final String WSDL_URL = "https://ips.gov.uz/mediate/ips/MOI/GetAddress?wsdl";


    private static final String testPINFL = "32707881670014";


    private static final Integer CONNECTION_TIMEOUT = 4000;
    private static final Integer READ_TIMEOUT = 4000;


    public MIPIndividualsAddressInfoService() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECTION_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        setMessageSender(new ClientHttpRequestMessageSender(requestFactory));

        super.setDefaultUri(WSDL_URL);
    }

    public IndividualAddressInfoResponse getIndividualsAddressInfoBy(String PINFL) {

        Request request = new Request();
        request.setPin(PINFL);
        request.setGuid("12345");


        IndividualAddressInfoResponse response = new IndividualAddressInfoResponse();
        try {
            response.setSuccessfullResponse((ResponseModel) getWebServiceTemplate().marshalSendAndReceive(WSDL_URL, request));
        } catch (Exception e) {
            e.printStackTrace();
            response.setResponseStatusCode(-1);
        }

        return response;
    }


    @Override
    public boolean isAvailable() {
        Request request = new Request();
        request.setPin(testPINFL);
        request.setGuid("12345");


        IndividualAddressInfoResponse response = new IndividualAddressInfoResponse();
        try {
            response.setSuccessfullResponse((ResponseModel) getWebServiceTemplate().marshalSendAndReceive(WSDL_URL, request));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }
}
