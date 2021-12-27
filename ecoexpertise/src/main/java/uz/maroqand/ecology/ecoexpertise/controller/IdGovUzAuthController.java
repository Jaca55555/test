package uz.maroqand.ecology.ecoexpertise.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uz.maroqand.ecology.core.component.UserDetailsImpl;
import uz.maroqand.ecology.core.constant.sys.IdGovUzResponseParams;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.entity.user.UserIdGov;
import uz.maroqand.ecology.core.repository.user.UserIdGovRepository;
import uz.maroqand.ecology.core.repository.user.UserRepository;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.util.HttpRequestHelper;
import uz.maroqand.ecology.core.util.TinParser;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysTemplates;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysUrls;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 26.09.2018.
 * (uz)
 * (ru)
 */
@Controller
public class IdGovUzAuthController {

    private static final String AuthorizationUrl = "https://sso.gov.uz:8443/sso/oauth/Authorization.do"; 
    private static final String LogoutUrl = "https://sso.gov.uz:8443/sso/svc/tk/SLO.do";
    private static final String ClientId = "davekoekspertiza_uz";
    private static final String Scope = "davekoekspertiza_uz";
    private static final String ClientSecret = "XbzKDhhq8+3tDnuU/e02bA==";
    private static final String RedirectUrl = "http://eco-service.uz" + SysUrls.IdGovUzAccessToken;

    private final Logger logger = LogManager.getLogger(IdGovUzAuthController.class);
    private final SimpleDateFormat idGovUzDateFormat = new SimpleDateFormat("yyyyMMdd");

    private UserIdGovRepository userIdGovRepository;
    private UserRepository userRepository;
    private UserAdditionalService userAdditionalService;

    @Autowired
    public IdGovUzAuthController(UserIdGovRepository userIdGovRepository, UserRepository userRepository, UserAdditionalService userAdditionalService) {
        this.userIdGovRepository = userIdGovRepository;
        this.userRepository = userRepository;
        this.userAdditionalService = userAdditionalService;
    }

    @RequestMapping(SysUrls.IdGovUzLogin)
    public String getIdGovUzLoginPage(Model model) {

        Map<String, String> stateMap = new HashMap<>();
        stateMap.put("method", "IDPW");

        Gson gson = new Gson();

        sun.misc.BASE64Encoder b64 = new sun.misc.BASE64Encoder();
        String state = b64.encode(gson.toJson(stateMap).getBytes());
        model.addAttribute("state", state);
        model.addAttribute("authorization_url", AuthorizationUrl);

        model.addAttribute("client_id", ClientId);
        model.addAttribute("scope", Scope);
        model.addAttribute("redirect_uri", RedirectUrl);
        return SysTemplates.IdGovUzLogin;
    }

    @RequestMapping(SysUrls.IdGovUzLogout)
    public String logoutFromIdGovUz(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        return "redirect:" + LogoutUrl + "?id=" + ClientId;
    }

    @RequestMapping(value = SysUrls.IdGovUzAccessToken, method = RequestMethod.GET)
    public String submitIdGovUzLoginPage(
            @RequestParam("code") String code,
            HttpServletRequest request
    ) {
        logger.info("User came from IGU with the following code: " + code);

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            logger.info("request paramName: " + paramName);
            String[] paramValues = request.getParameterValues(paramName);
            for (int i = 0; i < paramValues.length; i++) {
                String paramValue = paramValues[i];
                logger.info("request paramValue: " + paramValue);
            }
        }

        /*
        * Get accessToken
        * */
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("grant_type", "one_authorization_code");
        paramMap.put("client_id", IdGovUzAuthController.ClientId);
        paramMap.put("client_secret", IdGovUzAuthController.ClientSecret);
        paramMap.put("code", code);
        logger.info("Sending one_authorization_code (step 2) Request to IGU: " + paramMap.toString());

        String resultData = HttpRequestHelper.httpRequest(IdGovUzAuthController.AuthorizationUrl, paramMap);
        logger.info("Received from IGU for one_authorization_code request (2-request) data: " + resultData);


        Gson gson = new Gson();
        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(resultData, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            return IGUSentWrongData(resultData);
        }

        if (jsonObject == null) {
            return IGUSentWrongData(resultData);
        }


        if (!jsonObject.has("access_token")) {
            //Error data is sent in the following format:
            //Example: {"error":"token_not_found","error_description":"Token Not Found"}
            if (jsonObject.has("error") && jsonObject.has("error_description")) {
                String errorName = jsonObject.get("error").getAsString();
                String errorDescription = jsonObject.get("error_description").getAsString();
                logger.info("Received from IGU for one_authorization_code request (2-request) ERROR: {}\n{}\n{}", errorName, errorDescription, resultData);
            }
            return IGUSentWrongData(resultData);
        }

        /*
         * Get userInfo
         * */
        String accessToken = jsonObject.get(IdGovUzResponseParams.AccessToken).getAsString();
        String refreshToken = jsonObject.get(IdGovUzResponseParams.RefreshToken).getAsString();
        paramMap.clear();

        paramMap.put("grant_type", "one_access_token_identify");
        paramMap.put("client_id", IdGovUzAuthController.ClientId);
        paramMap.put("client_secret", IdGovUzAuthController.ClientSecret);
        paramMap.put("scope", IdGovUzAuthController.Scope);
        paramMap.put("access_token", accessToken);

        logger.info("Sending one_access_token_identify Request (3-step) to IGU: " + paramMap.toString());
        resultData = HttpRequestHelper.httpRequestUsingCURL(IdGovUzAuthController.AuthorizationUrl, paramMap);
        logger.info("Received for one_access_token_identify Request (3-request) data: " + resultData);

        JsonObject userInfoObject;
        try {
            userInfoObject = gson.fromJson(resultData, JsonObject.class);
        } catch (Exception e) {
            return IGUSentWrongData(resultData);
        }

        if ( !userInfoObject.has(IdGovUzResponseParams.AuthenticationResult) ||
             !userInfoObject.get(IdGovUzResponseParams.AuthenticationResult).getAsString().equals("0")
        ) {
            return IGUSentWrongData(resultData);
        }

        //Authorization successful
        if (!userInfoObject.has(IdGovUzResponseParams.Username)) {
            return IGUSentWrongData(resultData);
        }

        String username = userInfoObject.get(IdGovUzResponseParams.Username).getAsString();

        /*
         * Insert userIdGov
         * */
        UserIdGov userIdGov = new UserIdGov();
        userIdGov.setUsername(username);
        userIdGov.setAccessToken(accessToken);
        userIdGov.setRefreshToken(refreshToken);

        if (userInfoObject.has(IdGovUzResponseParams.ActualAddress)) {
            userIdGov.setActualAddress(userInfoObject.get(IdGovUzResponseParams.ActualAddress).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.LegalEntityName)) {
            userIdGov.setLegalEntityName(userInfoObject.get(IdGovUzResponseParams.LegalEntityName).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.BirthCountry)) {
            userIdGov.setBirthCountry(userInfoObject.get(IdGovUzResponseParams.BirthCountry).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.Birthdate)) {
            try {
                userIdGov.setBirthdate(idGovUzDateFormat.parse(userInfoObject.get(IdGovUzResponseParams.Birthdate).getAsString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (userInfoObject.has(IdGovUzResponseParams.BirthPlace)) {
            userIdGov.setBirthPlace(userInfoObject.get(IdGovUzResponseParams.BirthPlace).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.Citizenship)) {
            userIdGov.setCitizenship(userInfoObject.get(IdGovUzResponseParams.Citizenship).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.Email)) {
            userIdGov.setEmail(userInfoObject.get(IdGovUzResponseParams.Email).getAsString());
        }


        if (userInfoObject.has(IdGovUzResponseParams.Firstname)) {
            userIdGov.setFirstname(userInfoObject.get(IdGovUzResponseParams.Firstname).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.Lastname)) {
            userIdGov.setLastname(userInfoObject.get(IdGovUzResponseParams.Lastname).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.Middlename)) {
            userIdGov.setMiddlename(userInfoObject.get(IdGovUzResponseParams.Middlename).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.FullName)) {
            userIdGov.setFullName(userInfoObject.get(IdGovUzResponseParams.FullName).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.Gender)) {
            if (userInfoObject.get(IdGovUzResponseParams.Gender).getAsCharacter() == IdGovUzResponseParams.GenderFemale) {
                userIdGov.setFemale(true);
            } else {
                userIdGov.setFemale(false);
            }
        }

        if (userInfoObject.has(IdGovUzResponseParams.IsValidatedUsingEDS)) {
            if (userInfoObject.get(IdGovUzResponseParams.IsValidatedUsingEDS).getAsString().equals("true")) {
                userIdGov.setIsValidatedUsingEDS(true);
            } else {
                userIdGov.setIsValidatedUsingEDS(false);
            }
        }

        if (userInfoObject.has(IdGovUzResponseParams.LegalEntityName)) {
            userIdGov.setLegalEntityName(userInfoObject.get(IdGovUzResponseParams.LegalEntityName).toString());
        }


        if (userInfoObject.has(IdGovUzResponseParams.LegalEntityTIN)) {
            userIdGov.setLegalEntityTIN(userInfoObject.get(IdGovUzResponseParams.LegalEntityTIN).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.TIN)) {
            try {
                userIdGov.setTin(userInfoObject.get(IdGovUzResponseParams.TIN).getAsString());
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        if (userInfoObject.has(IdGovUzResponseParams.MobilePhone)) {
            userIdGov.setMobilePhone(userInfoObject.get(IdGovUzResponseParams.MobilePhone).getAsString());
        }


        if (userInfoObject.has(IdGovUzResponseParams.Nationality)) {
            userIdGov.setNationality(userInfoObject.get(IdGovUzResponseParams.Nationality).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.PassportExpiryDate)) {
            try {
                userIdGov.setPassportExpiryDate(idGovUzDateFormat.parse(userInfoObject.get(IdGovUzResponseParams.PassportExpiryDate).getAsString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (userInfoObject.has(IdGovUzResponseParams.PassportIssueDate)) {
            try {
                userIdGov.setPassportIssueDate(idGovUzDateFormat.parse(userInfoObject.get(IdGovUzResponseParams.PassportIssueDate).getAsString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (userInfoObject.has(IdGovUzResponseParams.PassportIssuePlace)) {
            userIdGov.setPassportIssuePlace(userInfoObject.get(IdGovUzResponseParams.PassportIssuePlace).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.PassportNumber)) {
            userIdGov.setPassportNumber(userInfoObject.get(IdGovUzResponseParams.PassportNumber).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.PermanentAddress)) {
            userIdGov.setPermanentAddress(userInfoObject.get(IdGovUzResponseParams.PermanentAddress).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.PIN)) {
            userIdGov.setPassportPIN(userInfoObject.get(IdGovUzResponseParams.PIN).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.RoleListAsJSON)) {
            userIdGov.setRoleListAsJSON(userInfoObject.get(IdGovUzResponseParams.RoleListAsJSON).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.WebServicesList)) {
            userIdGov.setWebServicesList(userInfoObject.get(IdGovUzResponseParams.WebServicesList).getAsString());
        }


        if (userInfoObject.has(IdGovUzResponseParams.SessionId)) {
            userIdGov.setSessionId(userInfoObject.get(IdGovUzResponseParams.SessionId).getAsString());
        }

        if (userInfoObject.has(IdGovUzResponseParams.UserType)) {
            if (userInfoObject.get(IdGovUzResponseParams.UserType).getAsCharacter() == IdGovUzResponseParams.UserTypeIndividual) {
                userIdGov.setUserType(IdGovUzResponseParams.UserTypeIndividual);
            } else {
                userIdGov.setUserType(IdGovUzResponseParams.UserTypeLegalEntity);
            }
        }


        /*
         * Insert or Update user
         * */
        User user;
        if(TinParser.trimIndividualsTinToNull(userIdGov.getLegalEntityTIN())!=null){
            user = userRepository.findByTinAndLeTin(TinParser.trimIndividualsTinToNull(userIdGov.getTin()), TinParser.trimIndividualsTinToNull(userIdGov.getLegalEntityTIN()));
        }else if(TinParser.trimIndividualsTinToNull(userIdGov.getTin())!=null){
            user = userRepository.findByTinAndLeTinIsNull(TinParser.trimIndividualsTinToNull(userIdGov.getTin()));
        }else {
            user = userRepository.findByUsername(userIdGov.getUsername());
        }

        if (user == null) {
            user = new User();
            user.setDateRegistered(new Date());
            user.setEnabled(true);
            user.setPassword("       ");
            user.setUsername(userIdGov.getUsername());
        }
        user.setFirstname(userIdGov.getFirstname());
        user.setLastname(userIdGov.getLastname());
        user.setMiddlename(userIdGov.getMiddlename());
        user.setEmail(userIdGov.getEmail());
        user.setPhone(userIdGov.getMobilePhone());
        user.setGender(userIdGov.getGender());

        user.setTin(TinParser.trimIndividualsTinToNull(userIdGov.getTin()));
        user.setLeTin(TinParser.trimIndividualsTinToNull(userIdGov.getLegalEntityTIN()));
        user.setLastEvent(new Date());
        user = userRepository.saveAndFlush(user);


        userIdGov.setUserId(user.getId());
        userIdGov.setCreatedAt(new Date());
        userIdGovRepository.save(userIdGov);

        /*
        * Authorization user
        * */

        user.setUserAdditionalId(userAdditionalService.createUserAdditional(user));

        if(!userIdGov.getIsValidatedUsingEDS()){
            return "redirect:"+"/login?notValidated";
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("Logging in with user: {} {}", user.getId(), user.getUsername());

        return "redirect:"+"/dashboard";
    }

    private String IGUSentWrongData(String receivedData) {
        logger.error("id.gov.uz sent wrong data: " + receivedData);
        return "redirect:" + "/login?errorIdGov";
    }

}
