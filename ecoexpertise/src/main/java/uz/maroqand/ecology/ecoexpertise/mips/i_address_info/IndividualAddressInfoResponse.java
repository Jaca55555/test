package uz.maroqand.ecology.ecoexpertise.mips.i_address_info;

import uz.maroqand.ecology.ecoexpertise.mips.APIResponse;

public class IndividualAddressInfoResponse implements APIResponse {

    ResponseModel responseModel;

    Integer statusCode = -1;
    String statusMessage;



    @Override
    public ResponseModel getResponse() {
        return responseModel;
    }

    @Override
    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    public String getResponseStatusMessage() {
        return statusMessage;
    }

    public void setSuccessfullResponse(ResponseModel successfullResponse) {
        this.responseModel = successfullResponse;

        this.statusCode = APIResponseStatusCodes.Successfull;
    }

    public void setResponseStatusCode(int responseStatusCode) {
        this.statusCode = responseStatusCode;
    }
}
