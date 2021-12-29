package uz.maroqand.ecology.ecoexpertise.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uz.maroqand.ecology.core.component.UserDetailsImpl;
import uz.maroqand.ecology.core.dto.eimzo.VerifyPkcs7Dto;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.entity.user.UserEds;
import uz.maroqand.ecology.core.repository.user.UserEdsRepository;
import uz.maroqand.ecology.core.repository.user.UserRepository;
import uz.maroqand.ecology.core.service.eimzo.EimzoService;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysTemplates;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysUrls;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Utkirbek Boltaev on 16.08.2018.
 * (uz)
 * (ru)
 */
@Controller
public class EDSLoginController {

    private UserEdsRepository userEdsRepository;
    private UserRepository userRepository;
    private EimzoService eimzoService;
    private UserAdditionalService userAdditionalService;

    //Imzo qo'ygan vaqtini tekshiramiz, serverni vaqti bilan farq 45 minutdan oshmasligi kk
    private final long maxAllowedSigningInterval = 1000 * 60 * 45;

    Logger logger = LogManager.getLogger(MainController.class);

    private final Pattern tinPattern = Pattern.compile("=([1-9][0-9]{8}),");
    private final Pattern pinflPattern = Pattern.compile("=([1-9][0-9]{13}),");

    //EDS kirishda API key domenga to'g'ri bog'langan bo'lishi kerak.
    //Bu API keylar GNK NTMdagi Azamat aka tomonidan berilgan
//    private final String apiKey = "D9A09BEE01285498D36BFBE8B3ED289E12840D5E65CF182E298451580343D53B712F982E21B63FBC58151923C262113555CA0DB5E1FE4330536C7A738FE77D1A";
    private final String apiKey = "800D00EAA115F8BB0D5083ECBDEC6ABDF9124235BDD6C2CD558CD686B583743524FB947AF54B11A6A98D3A303CF14CF6D86BD71C975E95B45E34D07F327674CA";


    public EDSLoginController(UserEdsRepository userEdsRepository, UserRepository userRepository, EimzoService eimzoService, UserAdditionalService userAdditionalService) {
        this.userEdsRepository = userEdsRepository;
        this.userRepository = userRepository;
        this.eimzoService = eimzoService;
        this.userAdditionalService = userAdditionalService;
    }


    /*
        (ru) Электро́нная цифровая по́дпись
        (en) Electronic digital signature
        (uz) Elektron raqamli imzo
    */
    @RequestMapping(value = SysUrls.EDSLogin, method = RequestMethod.GET)
    public String getEDSLoginPage( Model model ) {

        model.addAttribute("api_key", apiKey);
        model.addAttribute("api_key_domain", "eco-service.uz");
        return SysTemplates.EDSLogin;
    }

    @RequestMapping(value = SysUrls.EDSLogin, method = RequestMethod.POST)
    public String submitEDSLoginPage(
            HttpServletRequest request,
            @RequestParam(name = "key") String edsKey,
            @RequestParam(name = "data") String edsData,
            @RequestParam(name = "pkcs7") String edsSign
    ) {
        logger.info("\nkey: {}\ndata: {}\nsign: {}", edsKey, edsData, edsSign);

        String subjectName = "";

        VerifyPkcs7Dto verifyPkcs7Dto = eimzoService.checkVerifyPkcs7(edsSign);
        if (
                //Sertifikat to'g'riligini va muddati tugamaganligini tekshiramiz
                verifyPkcs7Dto.getVerified() && verifyPkcs7Dto.getCertificateVerified() &&

                //Imzo qo'ygan vaqtini tekshiramiz, serverni vaqti bilan farq 45 minutdan oshmasligi kk
                verifyPkcs7Dto.getSigningTime() != null &&
                Math.abs(verifyPkcs7Dto.getSigningTime().getTime() - (new Date()).getTime()) < maxAllowedSigningInterval
        ) {
            subjectName = verifyPkcs7Dto.getCertificateSubjectName();
            logger.info("now: {} signed at: {}", (new Date()), verifyPkcs7Dto.getSigningTime());
        }

        logger.info("subjectName: {}", subjectName);
        String pinfl = subjectName.substring(subjectName.length()-14);
        Matcher matcher1 = pinflPattern.matcher(pinfl);
        System.out.println("matcher1"+matcher1);
        Matcher matcher = tinPattern.matcher(subjectName);

        if (matcher1.find()){
            logger.info("Pinfl: {}", matcher1.group(1));
        }


        Integer tin = null;
        Integer leTIN = null;
//        String pinfl = null;

        //1-fiz litso INN si keladi.
        if (matcher.find()) {
            logger.info("EDS found individual tin: {}", matcher.group(1));
            tin = Integer.parseInt(matcher.group(1));
        }

        //2-yur litso INN si keladi.
        if (matcher.find()) {
            logger.info("EDS found legalEntity tin: {}", matcher.group(1));
            leTIN = Integer.parseInt(matcher.group(1));
        }

        //2-yur litso INN si keladi.
        if (matcher1.find()) {
            logger.info("Pinfl: {}", matcher1.group(1));
            pinfl = matcher1.group(1);
        }

        if (tin == null && pinfl==null) {
            logger.info("Could not find individual TIN.");
            return "redirect:" + SysUrls.EDSLogin + "?failed=1";
        }

        /*
         * Insert userIdGov
         * */
        UserEds userEds = new UserEds();
        String[] res = subjectName.split(",");
        for (String currentRes : res) {
            String[] r = currentRes.split("=");
            switch (r[0]) {
                case "CN": userEds.setFullName(r[1]); break;//to'liq ismi
                case "Name": userEds.setFirstname(r[1]); break;//ism
                case "SURNAME": userEds.setLastname(r[1]); break;//familiya
                case "O": userEds.setOwner(r[1]); break;//korxona
                case "OU": userEds.setWorkplace(r[1]); break;//korxona
                case "T": userEds.setPosition(r[1]); break;//lavozimi
                case "L": userEds.setSubRegion(r[1]); break;//tuman
                case "ST": userEds.setRegion(r[1]); break;//viloyat
                case "E": userEds.setEmail(r[1]); break;//e-mail
                case "C": userEds.setCountry(r[1]); break;//davlat
                case "PINFL": userEds.setPinfl(r[1]); break;//pinfl
                default:
                    if (r[1] != null && r[1].length() == 14) {
                        userEds.setPinfl(r[1]);//pinfl
                        break;
                    }
            }
        }

        userEds.setTin(tin);
        userEds.setLeTin(leTIN);

        /*
         * Insert or Update user
         * */
        User user = null;
        if(userEds.getLeTin()!=null){
            user = userRepository.findByTinAndLeTin(userEds.getTin(), userEds.getLeTin());
        }else if(userEds.getTin()!=null){
            user = userRepository.findByTinAndLeTinIsNull(userEds.getTin());
        }else if (userEds.getPinfl()!=null){
            user = userRepository.findByPinfl(userEds.getPinfl());
        }

        if (user == null) {
            user = new User();
            user.setDateRegistered(new Date());
            user.setEnabled(true);
            user.setPassword("       ");
            user.setGender(null);

            List<User> userList = userRepository.findByTin(tin);
            if(userList.size()>0){
                user.setUsername(pinfl+"_"+userList.size());
            }else {
                user.setUsername(userEds.getTin().toString());
            }
        }
        user.setFirstname(userEds.getFirstname());
        user.setLastname(userEds.getLastname());
        user.setMiddlename("");
        user.setEmail(userEds.getEmail());
        user.setPhone("");

        user.setTin(userEds.getTin());
        user.setLeTin(userEds.getLeTin());
        user.setPinfl(userEds.getPinfl());
        user.setLastEvent(new Date());
        user = userRepository.saveAndFlush(user);

        /*
         * Authorization user
         * */

        user.setUserAdditionalId(userAdditionalService.createUserAdditional(user));

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("Logging in with user: {} {}", user.getId(), user.getUsername());

        return "redirect:"+"/dashboard";
    }

    /*public static void main(String[] args) {
        Pattern tinPattern = Pattern.compile("=([1-9]{1}[0-9]{13}),");
        String sub = "CN=БЕРДИХАНОВ ЖАЛГАС ПИРИМБЕТОВИЧ,Name=ЖАЛГАС,SURNAME=БЕРДИХАНОВ,O=QARAQALPAQSTAN RESPUBLIKASI QURILIS MINISTRLIGI,L=Нукус шаҳар,ST=Қорақалпоғистон Респ.,C=UZ,UID=469577057,1.2.860.3.16.1.2=30106543500069,T=Direktor,1.2.860.3.16.1.1=200358244,BusinessCategory=Бюджет ташкилотлари";
        Matcher matcher = tinPattern.matcher(sub);
        System.out.println(matcher.find());
    }*/

}
