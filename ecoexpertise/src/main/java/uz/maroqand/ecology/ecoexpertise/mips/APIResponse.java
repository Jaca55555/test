package uz.maroqand.ecology.ecoexpertise.mips;

public interface APIResponse {

    class APIResponseStatusCodes {
        public static final Integer Successfull = 1;
    }

    Object getResponse();


    /**
     * @return Returns Integer status code of an API call
     */
    Integer getStatusCode();

    /**
     *
     * @return Returns API call response status message. E.g: when an error happens, this method must return short error description.
     * When API call is successful, it can return "OK";
     */
    String getResponseStatusMessage();
}
