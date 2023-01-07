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
import uz.maroqand.ecology.core.dto.id_egov.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.entity.user.UserIdGov;
import uz.maroqand.ecology.core.repository.user.UserIdGovRepository;
import uz.maroqand.ecology.core.repository.user.UserRepository;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.service.user.UserService;
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
    private static final String RedirectUrl = "https://eco-service.uz" + SysUrls.IdGovUzAccessToken;

    @Autowired
    private IdGovService idGovService;

    private final Logger logger = LogManager.getLogger(IdGovUzAuthController.class);
    private final SimpleDateFormat idGovUzDateFormat = new SimpleDateFormat("yyyyMMdd");

    private UserIdGovRepository userIdGovRepository;
    private UserRepository userRepository;
    private final UserService userService;
    private UserAdditionalService userAdditionalService;

    @Autowired
    public IdGovUzAuthController(UserIdGovRepository userIdGovRepository, UserRepository userRepository, UserService userService, UserAdditionalService userAdditionalService) {
        this.userIdGovRepository = userIdGovRepository;
        this.userRepository = userRepository;
        this.userService = userService;
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

        CabinetType cabinetType = CabinetType.BACK_OFFICE;
        logger.info("User came from IGU with the following code: " + code);

        IdGovToken idGovToken = idGovService.getToken(cabinetType, code);
        if (idGovToken.getAccess_token().isEmpty())
            throw new ApiException(ResponseStatus.OAUTH_INVALID_ID_EGOV_TOKEN);
        IdGovResponseDto idGovResponse = idGovService.getUserInfo(cabinetType, idGovToken.getAccess_token());

        String pin = idGovResponse.getPin();
        Integer leTin = null;
        String leName = "";
        if (idGovResponse.getLegal_info() != null && idGovResponse.getLegal_info().size() > 0) {
            try {
                IdGovLEResponseDto legalInfo = idGovResponse.getLegal_info().stream().filter(IdGovLEResponseDto::getIs_basic).findFirst().orElse(null);

                if (legalInfo != null) {
                    leTin = Integer.parseInt(legalInfo.getTin());
                    leName = legalInfo.getAcron_UZ();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        UserType userType = leTin != null ? UserType.LEGAL_ENTITY : UserType.INDIVIDUAL;
        User user;
//        if (leTin != null) {
//            user = userRepository.findByTinAndLeTin(TinParser.trimIndividualsTinToNull(userIdGov.getTin()), TinParser.trimIndividualsTinToNull(userIdGov.getLegalEntityTIN()));
//        } else {
//            user = userRepository.findByTinAndLeTinIsNull(TinParser.trimIndividualsTinToNull(userIdGov.getTin()));
//        }
//
//        if (user == null) {
//            user = userService.create(idGovResponse, leTin, leName, userType);
//            logger.info("create user:{} ", user);
//        }else {
//            userService.update(idGovResponse, leTin, leName, userType,user.getId());
//            logger.info("update user:{} ", user);
//        }

        /*
         * Insert userIdGov
         * */
//        UserIdGov userIdGov = new UserIdGov();
//        userIdGov.setUsername(username);
//        userIdGov.setAccessToken(accessToken);
//        userIdGov.setRefreshToken(refreshToken);



        /*
         * Insert or Update user
         * */
//        User user;
//        if(TinParser.trimIndividualsTinToNull(userIdGov.getLegalEntityTIN())!=null){
//            user = userRepository.findByTinAndLeTin(TinParser.trimIndividualsTinToNull(userIdGov.getTin()), TinParser.trimIndividualsTinToNull(userIdGov.getLegalEntityTIN()));
//        }else if(TinParser.trimIndividualsTinToNull(userIdGov.getTin())!=null){
//            user = userRepository.findByTinAndLeTinIsNull(TinParser.trimIndividualsTinToNull(userIdGov.getTin()));
//        }else {
//            user = userRepository.findByUsername(userIdGov.getUsername());
//        }
//
//        if (user == null) {
//            user = new User();
//            user.setDateRegistered(new Date());
//            user.setEnabled(true);
//            user.setPassword("       ");
//            user.setUsername(userIdGov.getUsername());
//        }
//        user.setFirstname(userIdGov.getFirstname());
//        user.setLastname(userIdGov.getLastname());
//        user.setMiddlename(userIdGov.getMiddlename());
//        user.setEmail(userIdGov.getEmail());
//        user.setPhone(userIdGov.getMobilePhone());
//        user.setGender(userIdGov.getGender());
//
//        user.setTin(TinParser.trimIndividualsTinToNull(userIdGov.getTin()));
//        user.setLeTin(TinParser.trimIndividualsTinToNull(userIdGov.getLegalEntityTIN()));
//        user.setLastEvent(new Date());
//        user = userRepository.saveAndFlush(user);


//        userIdGov.setUserId(user.getId());
//        userIdGov.setCreatedAt(new Date());
//        userIdGovRepository.save(userIdGov);
//
//        /*
//        * Authorization user
//        * */
//
//        user.setUserAdditionalId(userAdditionalService.createUserAdditional(user));
//
//        if(!userIdGov.getIsValidatedUsingEDS()){
//            return "redirect:"+"/login?notValidated";
//        }

//        UserDetailsImpl userDetails = new UserDetailsImpl(user);

//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        logger.info("Logging in with user: {} {}", user.getId(), user.getUsername());

        return "redirect:"+"/dashboard";
    }

    private String IGUSentWrongData(String receivedData) {
        logger.error("id.gov.uz sent wrong data: " + receivedData);
        return "redirect:" + "/login?errorIdGov";
    }

}
