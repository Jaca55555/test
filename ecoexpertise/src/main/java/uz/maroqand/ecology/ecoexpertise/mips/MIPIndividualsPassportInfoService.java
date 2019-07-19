package uz.maroqand.ecology.ecoexpertise.mips;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;
import uz.maroqand.ecology.ecoexpertise.mips.i_passport_info.CEPRequest;
import uz.maroqand.ecology.ecoexpertise.mips.i_passport_info.CEPResponse;
import uz.maroqand.ecology.ecoexpertise.mips.i_passport_info.IndividualPassportInfoResponse;

@Service
public class MIPIndividualsPassportInfoService
        extends WebServiceGatewaySupport implements APIService
{
    private static final String WSDL_URL = "https://ips.gov.uz/mediate/ips/PC/PersonDocInfoService?wsdl";

    private static final Integer CONNECTION_TIMEOUT = 4000;
    private static final Integer READ_TIMEOUT = 4000;

    private static final String fpPINFL = "__PINFL__" ;
    private static final String fpPassportSerial = "__P_SERIAL__" ;

    private static final String testPassportSerial = "AA2673523";
    private static final String testPINFL = "32707881670014";

    private final Logger logger = LogManager.getLogger(MIPIndividualsPassportInfoService.class);

    private final Gson gson = new Gson();


    private static final String XMLDataTemplate =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<DataCEPRequest>\n" +
                    "     <pinpp>" + fpPINFL + "</pinpp>\n" +
                    "     <document>" + fpPassportSerial + "</document>\n" +
                    "     <langId>1</langId>\n" +
                    "</DataCEPRequest>";

    public MIPIndividualsPassportInfoService() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECTION_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        setMessageSender(new ClientHttpRequestMessageSender(requestFactory));

        super.setDefaultUri(WSDL_URL);
    }

    public IndividualPassportInfoResponse getPassportInfoBy(String passportSerial, String PINFL) {

        CEPRequest request = new CEPRequest();

        request.setData(
                XMLDataTemplate
                        .replace(fpPassportSerial, passportSerial)
                        .replace(fpPINFL, PINFL)
        );

        IndividualPassportInfoResponse response = new IndividualPassportInfoResponse();
        try {
            response.setSuccessfullResponse((CEPResponse) getWebServiceTemplate().marshalSendAndReceive(WSDL_URL, request));
            logger.info(gson.toJson(response.getPassportInfo()));
        } catch (Exception e) {
            e.printStackTrace();
            response.setResponseStatusCode(-1);
        }

        return response;
    }

    @Override
    public boolean isAvailable() {
        IndividualPassportInfoResponse passportInfoBy = getPassportInfoBy(testPassportSerial, testPINFL);
        return APIResponse.APIResponseStatusCodes.Successfull.equals(passportInfoBy.getStatusCode());
    }
}
