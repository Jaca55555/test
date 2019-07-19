package uz.maroqand.ecology.ecoexpertise.mips.i_tax_info_by_pin;

import uz.maroqand.ecology.ecoexpertise.mips.APIResponse;

public class IndividualsTaxInfoByPINFLResponse implements APIResponse {
    GetTaxInfobyPinRes response;

    Integer statusCode = -1;

    String statusMessage = "";

    @Override
    public GetTaxInfobyPinRes getResponse() {
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

    public void setResponse(GetTaxInfobyPinRes response) {
        this.response = response;

        //Код результата:
        //0 – данные найдены
        //1 – данные не найдены
        if(this.response.errCode == "0") {
            this.statusCode = APIResponseStatusCodes.Successfull;
        } else {
            statusCode = -1;
            statusMessage = this.response.errText;
        }


    }
}
