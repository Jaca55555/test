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
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.ecoexpertise.constant.RegTemplates;
import uz.maroqand.ecology.ecoexpertise.constant.RegUrls;

import javax.validation.Valid;
import java.util.*;

@Controller
public class RegApplicationController {

    private UserService userService;
    private RegApplicationService regApplicationService;

    @Autowired
    public RegApplicationController(RegApplicationService regApplicationService) {
        this.regApplicationService = regApplicationService;
    }

    @RequestMapping(value = RegUrls.RegApplicationList)
    public String getRegApplicationListPage() {

        return RegTemplates.RegApplicationList;
    }

    @RequestMapping(value = RegUrls.RegApplicationListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public DataTablesOutput<RegApplication> getRegApplicationListAjax(@Valid DataTablesInput input) {
        User user = userService.getCurrentUserFromContext();
        return regApplicationService.findFiltered(input, user.getId());
    }

    @RequestMapping(value = RegUrls.RegApplicationDashboard)
    public String getDashboardPage() {

        return RegTemplates.RegApplicationDashboard;
    }

    @RequestMapping(value = RegUrls.RegApplicationStart)
    public String getStart() {
//        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.create(null);

        return "redirect:"+RegUrls.RegApplicationApplicant + "?id=" + regApplication.getId();
    }

    @RequestMapping(value = RegUrls.RegApplicationApplicant)
    public String getApplicantPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
//        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, null);
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 1);
        return RegTemplates.RegApplicationApplicant;
    }

    @RequestMapping(value = RegUrls.RegApplicationAbout)
    public String getAboutPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 2);
        return RegTemplates.RegApplicationAbout;
    }

    @RequestMapping(value = RegUrls.RegApplicationWaiting)
    public String getWaitingPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 2);
        return RegTemplates.RegApplicationWaiting;
    }

    @RequestMapping(value = RegUrls.RegApplicationContract)
    public String getContractPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 3);
        return RegTemplates.RegApplicationContract;
    }

    @RequestMapping(value = RegUrls.RegApplicationPrepayment)
    public String getPrepaymentPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 4);
        return RegTemplates.RegApplicationPrepayment;
    }

    @RequestMapping(value = RegUrls.RegApplicationPayment)
    public String getPaymentPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 4);
        return RegTemplates.RegApplicationPayment;
    }

    @RequestMapping(value = RegUrls.RegApplicationPaymentSendSms)
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

        String failUrl = RegUrls.RegApplicationPaymentSendSms;
        String successUrl = RegUrls.RegApplicationPaymentConfirmSms;

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

    @RequestMapping(value = RegUrls.RegApplicationPaymentConfirmSms)
    @ResponseBody
    public Map<String, Object> confirmSmsPayment(
            @RequestParam(name = "id") Integer applicationId,
            @RequestParam(name = "trId") Integer trId,
            @RequestParam(name = "paymentId") Integer paymentId,
            @RequestParam(name = "confirmSms") String confirmSms
    ) {

        String successUrl = RegUrls.RegApplicationStatus;
        String failUrl = RegUrls.RegApplicationPaymentConfirmSms;

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

    @RequestMapping(value = RegUrls.RegApplicationStatus)
    public String getStatusPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 5);
        return RegTemplates.RegApplicationStatus;
    }

}
