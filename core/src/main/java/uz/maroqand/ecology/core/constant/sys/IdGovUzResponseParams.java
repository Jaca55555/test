package uz.maroqand.ecology.core.constant.sys;

/**
 * Created by Utkirbek Boltaev on 26.09.2018.
 * (uz)
 * (ru)
 */
public class IdGovUzResponseParams {
    //General
    public static final String UserType = "user_type"; // I : fiz lico   L : yur lico
    public static final Character UserTypeIndividual = 'I';
    public static final Character UserTypeLegalEntity = 'L';

    public static final String IsValidatedUsingEDS = "valid";
    public static final String Username = "user_id";

    //Passport data
    public static final String PassportNumber = "pport_no"; //format: AA1234567
    public static final String PassportIssueDate = "pport_issue_date";
    public static final String PassportExpiryDate = "pport_expr_date";
    public static final String PassportIssuePlace = "pport_issue_place";
    public static final String PIN = "pin"; //PINFL

    //Address
    public static final String ActualAddress = "tem_adr";
    public static final String PermanentAddress = "per_adr";

    //Name
    public static final String Firstname = "first_name";
    public static final String Lastname = "sur_name";
    public static final String Middlename = "mid_name";
    public static final String FullName = "full_name";

    public static final String Birthdate = "birth_date";
    public static final String BirthCountry = "birth_cntry";
    public static final String BirthPlace = "birth_place";

    public static final String Gender = "gd";
    public static final Character GenderFemale = 'F';
    public static final Character GenderMale = 'M';

    public static final String Nationality = "natn";
    public static final String Citizenship = "ctzn";

    public static final String Email = "email";
    public static final String MobilePhone = "mob_phone_no"; //format: 998951234567


    //Legal entity data
    public static final String LegalEntityName = "le_name";
    public static final String LegalEntityTIN = "le_tin";

    public static final String AuthenticationResult = "ret_cd";
    public static final String WebServicesList = "ws_list";
    public static final String RoleListAsJSON = "role_list";
    public static final String SessionId = "sess_id";

    public static final String AccessToken = "access_token";
    public static final String RefreshToken = "refresh_token";
    public static final String TIN = "tin";
}
