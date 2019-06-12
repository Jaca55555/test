package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.OpfService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.ecoexpertise.constant.Templates;
import uz.maroqand.ecology.ecoexpertise.constant.Urls;

import javax.validation.Valid;
import java.util.*;

@Controller
public class RegApplicationController {

    private final UserService userService;
    private final SoatoService soatoService;
    private final OpfService opfService;
    private final RegApplicationService regApplicationService;

    @Autowired
    public RegApplicationController(UserService userService, SoatoService soatoService, OpfService opfService, RegApplicationService regApplicationService) {
        this.userService = userService;
        this.soatoService = soatoService;
        this.opfService = opfService;
        this.regApplicationService = regApplicationService;
    }

    @RequestMapping(value = Urls.RegApplicationList)
    public String getRegApplicationListPage() {

        return Templates.RegApplicationList;
    }

    @RequestMapping(value = Urls.RegApplicationListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public DataTablesOutput<RegApplication> getRegApplicationListAjax(@Valid DataTablesInput input) {
        User user = userService.getCurrentUserFromContext();
        return regApplicationService.findFiltered(input, user.getId());
    }

    @RequestMapping(value = Urls.RegApplicationDashboard)
    public String getDashboardPage() {

        return Templates.RegApplicationDashboard;
    }

    
    @RequestMapping(value = Urls.RegApplicationStart)
    public String getStart() {
//        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.create(null);

        return "redirect:"+Urls.RegApplicationApplicant + "?id=" + regApplication.getId();
    }

    @RequestMapping(value = Urls.RegApplicationApplicant)
    public String getApplicantPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
//        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, null);
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        model.addAttribute("opfList", opfService.getOpfList());
        model.addAttribute("regions", soatoService.getRegions());
        model.addAttribute("sub_regions", soatoService.getSubRegions());
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 1);
        return Templates.RegApplicationApplicant;
    }

    @RequestMapping(value = Urls.RegApplicationAbout)
    public String getAboutPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 2);
        return Templates.RegApplicationAbout;
    }

    @RequestMapping(value = Urls.RegApplicationWaiting)
    public String getWaitingPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 2);
        return Templates.RegApplicationWaiting;
    }

    @RequestMapping(value = Urls.RegApplicationContract)
    public String getContractPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 3);
        return Templates.RegApplicationContract;
    }

    @RequestMapping(value = Urls.RegApplicationPrepayment)
    public String getPrepaymentPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 4);
        return Templates.RegApplicationPrepayment;
    }

    @RequestMapping(value = Urls.RegApplicationPayment)
    public String getPaymentPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 4);
        return Templates.RegApplicationPayment;
    }

    @RequestMapping(value = Urls.RegApplicationPaymentSendSms)
    @ResponseBody
    public Map<String,Object> sendSmsPayment(
            @RequestParam(name = "id") Integer applicationId,
            @RequestParam(name = "telephone") String telephone,
            @RequestParam(name = "cardNumber") String cardNumber,
            @RequestParam(name = "cardMonth") String cardMonth,
            @RequestParam(name = "cardYear") String cardYear,
            @RequestParam(name = "paymentId") Integer paymentId,
            @RequestParam(name = "serial") String serial
    ) {

        String failUrl = Urls.RegApplicationPaymentSendSms;
        String successUrl = Urls.RegApplicationPaymentConfirmSms;

        /*return paymentService.sendSmsPaymentS2ServiceRegistryAndGetResponseMap(
                applicationId,
                cardNumber,
                telephone,
                cardMonth,
                cardYear,
                paymentId,
                serial,
                successUrl,
                failUrl
        );*/
        return null;
    }

    @RequestMapping(value = Urls.RegApplicationPaymentConfirmSms)
    @ResponseBody
    public Map<String, Object> confirmSmsPayment(
            @RequestParam(name = "id") Integer applicationId,
            @RequestParam(name = "trId") Integer trId,
            @RequestParam(name = "paymentId") Integer paymentId,
            @RequestParam(name = "confirmSms") String confirmSms
    ) {

        String successUrl = Urls.RegApplicationStatus;
        String failUrl = Urls.RegApplicationPaymentConfirmSms;

        /*return paymentService.confirmSmsAndGetResponseAsMap(
                applicationId,
                paymentId,
                trId,
                confirmSms,
                successUrl,
                failUrl
        );*/
        return null;
    }

    @RequestMapping(value = Urls.RegApplicationStatus)
    public String getStatusPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 5);
        return Templates.RegApplicationStatus;
    }

}
