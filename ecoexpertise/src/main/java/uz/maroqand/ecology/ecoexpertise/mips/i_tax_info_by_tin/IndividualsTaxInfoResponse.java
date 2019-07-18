package uz.maroqand.ecology.ecoexpertise.mips.i_tax_info_by_tin;

import uz.maroqand.ecology.ecoexpertise.mips.APIResponse;

public class IndividualsTaxInfoResponse implements APIResponse {

    GetTaxInfobyTinRes response;

    Integer statusCode = -1;

    String statusMessage = "";

    @Override
    public GetTaxInfobyTinRes getResponse() {
        return response;
    }

    @Override
    public Integer getStatusCode() {
        return statusCode;
    }

    public void setReponseStatusCode(int code) {
        statusCode = code;
    }

    @Override
    public String getResponseStatusMessage() {
        return statusMessage;
    }

    public void setResponse(GetTaxInfobyTinRes response) {
        this.response = response;

        if ("0".equals(this.response.errCode) ) {
            this.statusCode = APIResponseStatusCodes.Successfull;
        } else {
            statusCode = -1;
            statusMessage = this.response.errText;
        }
    }
}
