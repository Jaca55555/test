package uz.maroqand.ecology.ecoexpertise.mips.le_info_by_tin;

import uz.maroqand.ecology.ecoexpertise.mips.APIResponse;

public class LegalEntityInfoResponse implements APIResponse {


    private LEGALENTITYINFORMATION response = null;

    Integer statusCode = -1;

    String statusMessage = "";


    public void setSuccessfullResponse(LEGALENTITYINFORMATION legalentityinformation) {
        response = legalentityinformation;

        statusCode = APIResponseStatusCodes.Successfull;
    }

    public void setResponseStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }


    @Override
    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    public String getResponseStatusMessage() {
        return statusMessage;
    }

    @Override
    public LEGALENTITYINFORMATION getResponse() {
        return response;
    }

    public void setResponse(LEGALENTITYINFORMATION response) {
        this.response = response;
    }
}
