package uz.maroqand.ecology.ecoexpertise.mips.i_passport_info;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import uz.maroqand.ecology.ecoexpertise.mips.APIResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.SimpleDateFormat;

public class IndividualPassportInfoResponse implements APIResponse {

    private CEPResponse cepResponse;

    private Integer statusCode = -1;

    private String statusMessage = "";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dateFormatPassports = new SimpleDateFormat("yyyy-dd-MM");

    private PassportInfo passportInfo;


    public void setSuccessfullResponse(CEPResponse response) {
        cepResponse = response;

//        statusCode = APIResponseStatusCodes.Successfull;
        if (response == null) {
            //TODO: add specific error messages
            return;
        }



   /*
1 – Данные найдены
0 – Сервис временно не доступен
2 – Ошибка при обработке запроса
4 – Данные отсутствуют
201 – Обязательные данные отсутствуют
202 – Неправильный формат данных
        * */



        try {
            statusCode = Integer.parseInt(response.getResult());

            if (statusCode != 1) {
                return;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(cepResponse.data));
            Document doc = dBuilder.parse(inputSource);
            doc.getDocumentElement().normalize();


            PassportInfo localPassportInfo = new PassportInfo();

            localPassportInfo.setDocument(doc.getElementsByTagName("document").item(0).getTextContent());

            localPassportInfo.setSurname_latin(doc.getElementsByTagName("surname_latin").item(0).getTextContent());

            localPassportInfo.setName_latin(doc.getElementsByTagName("name_latin").item(0).getTextContent());

            localPassportInfo.setPatronym_latin(doc.getElementsByTagName("patronym_latin").item(0).getTextContent());
            localPassportInfo.setSurname_engl(doc.getElementsByTagName("surname_engl").item(0).getTextContent());
            localPassportInfo.setName_engl(doc.getElementsByTagName("name_engl").item(0).getTextContent());

            String temp = doc.getElementsByTagName("birth_date").item(0).getTextContent();
            if (!temp.isEmpty()) {
                localPassportInfo.setBirth_date(dateFormat.parse(temp));
            }

            localPassportInfo.setBirth_place(doc.getElementsByTagName("birth_place").item(0).getTextContent());

            temp = doc.getElementsByTagName("birth_place_id").item(0).getTextContent();
            if (!temp.isEmpty()) {
                localPassportInfo.setBirth_place_id(Integer.parseInt(temp));
            }

            localPassportInfo.setBirth_country(doc.getElementsByTagName("birth_country").item(0).getTextContent());

            temp = doc.getElementsByTagName("birth_country_id").item(0).getTextContent();
            if (!temp.isEmpty()) {
                localPassportInfo.setBirth_country_id(Integer.parseInt(temp));
            }


            temp = doc.getElementsByTagName("livestatus").item(0).getTextContent();
            if (!temp.isEmpty()) {
                localPassportInfo.setLivestatus(Integer.parseInt(temp));
            }

            localPassportInfo.setNationality(doc.getElementsByTagName("nationality").item(0).getTextContent());

            temp = doc.getElementsByTagName("nationality_id").item(0).getTextContent();
            if (!temp.isEmpty()) {
                localPassportInfo.setNationality_id(Integer.parseInt(temp));
            }


            localPassportInfo.setCitizenship(doc.getElementsByTagName("citizenship").item(0).getTextContent());

            temp = doc.getElementsByTagName("citizenship_id").item(0).getTextContent();
            if (!temp.isEmpty()) {
                localPassportInfo.setCitizenship_id(Integer.parseInt(temp));
            }

            temp = doc.getElementsByTagName("sex").item(0).getTextContent();
            if (!temp.isEmpty()) {
                localPassportInfo.setSex(Integer.parseInt(temp));
            }

            localPassportInfo.setDoc_give_place(doc.getElementsByTagName("doc_give_place").item(0).getTextContent());
            temp = doc.getElementsByTagName("doc_give_place_id").item(0).getTextContent();
            if (!temp.isEmpty()) {
                localPassportInfo.setDoc_give_place_id(Integer.parseInt(temp));
            }

            temp = doc.getElementsByTagName("date_begin_document").item(0).getTextContent();
            if (!temp.isEmpty()) {
                localPassportInfo.setDate_begin_document(dateFormatPassports.parse(temp));
            }

            temp = doc.getElementsByTagName("date_end_document").item(0).getTextContent();
            if (!temp.isEmpty()) {
                localPassportInfo.setDate_end_document(dateFormatPassports.parse(temp));
            }

            this.passportInfo = localPassportInfo;

        }
        catch (Exception e) {
            e.printStackTrace();
            this.passportInfo = null;
        }





    }

    @Override
    public CEPResponse getResponse() {
        return cepResponse;
    }

    @Override
    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    public String getResponseStatusMessage() {
        return statusMessage;
    }

    public void setResponseStatusCode(int responseStatusCode) {
        this.statusCode = responseStatusCode;
    }

    public PassportInfo getPassportInfo() {
        return passportInfo;
    }
}
