package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.constant.billing.PaymentStatus;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.billing.Payment;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.Offer;
import uz.maroqand.ecology.core.entity.expertise.ProjectDeveloper;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.client.OpfService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
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
    private final ClientService clientService;
    private final ActivityService activityService;
    private final ObjectExpertiseService objectExpertiseService;
    private final OrganizationService organizationService;
    private final ProjectDeveloperService projectDeveloperService;
    private final OfferService offerService;
    private final PaymentService paymentService;
    @Autowired
    public RegApplicationController(UserService userService, SoatoService soatoService, OpfService opfService, RegApplicationService regApplicationService, ClientService clientService, ActivityService activityService, ObjectExpertiseService objectExpertiseService, OrganizationService organizationService, ProjectDeveloperService projectDeveloperService, OfferService offerService, PaymentService paymentService) {
        this.userService = userService;
        this.soatoService = soatoService;
        this.opfService = opfService;
        this.regApplicationService = regApplicationService;
        this.clientService = clientService;
        this.activityService = activityService;
        this.objectExpertiseService = objectExpertiseService;
        this.organizationService = organizationService;
        this.projectDeveloperService = projectDeveloperService;
        this.offerService = offerService;
        this.paymentService = paymentService;

    }

    @RequestMapping(value = Urls.RegApplicationList)
    public String getRegApplicationListPage() {

        return Templates.RegApplicationList;
    }

    @RequestMapping(value = Urls.RegApplicationListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getRegApplicationListAjax(
            Pageable pageable
    ) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        User user = userService.getCurrentUserFromContext();

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(user.getId(),pageable);
        HashMap<String, Object> result = new HashMap<>();

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            convenientForJSONArray.add(new Object[]{
                    //data
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(value = Urls.RegApplicationDashboard)
    public String getDashboardPage() {

        return Templates.RegApplicationDashboard;
    }


    /*
    * Start
    * */
    @RequestMapping(value = Urls.RegApplicationStart)
    public String getStart() {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.create(user);

        return "redirect:"+Urls.RegApplicationApplicant + "?id=" + regApplication.getId();
    }

    @RequestMapping(value = Urls.RegApplicationApplicant,method = RequestMethod.GET)
    public String getApplicantPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        Client applicant = regApplication.getApplicant();
        if(applicant==null || applicant.getType()==null){
            applicant = new Client();
            applicant.setType(ApplicantType.LegalEntity);
            model.addAttribute("legalEntity", new LegalEntityDto());
            model.addAttribute("individual", new IndividualDto());

        }else{
            model.addAttribute("legalEntity", new LegalEntityDto(applicant));
            model.addAttribute("individual", new IndividualDto(applicant));
        }

        model.addAttribute("applicant", applicant);
        model.addAttribute("opfList", opfService.getOpfList());
        model.addAttribute("regions", soatoService.getRegions());
        model.addAttribute("sub_regions", soatoService.getSubRegions());
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", 1);
        return Templates.RegApplicationApplicant;
    }

    @RequestMapping(value = Urls.RegApplicationApplicant,method = RequestMethod.POST)
    public String createRegApplication(
            @RequestParam(name = "applicantType") String applicantType,
            @RequestParam(name = "id") Integer id,
            LegalEntityDto legalEntityDto,
            IndividualDto individualDto
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        Client applicant = regApplication.getApplicant();

        if (applicantType.equals("LegalEntity")){
            if (applicant==null){
                applicant = clientService.createClient(legalEntityDto);
                regApplication.setApplicantId(applicant.getId());
            }else{
                clientService.updateClient(applicant,legalEntityDto);
            }
        }else{
            if (applicant==null){
                applicant =  clientService.createClient(individualDto);
                regApplication.setApplicantId(applicant.getId());
            }else{
                clientService.updateClient(applicant,individualDto);
            }
        }
        regApplicationService.save(regApplication);
        return "redirect:" + Urls.RegApplicationAbout + "?id=" + id;
    }


    @RequestMapping(value = Urls.RegApplicationAbout,method = RequestMethod.GET)
    public String getAboutPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        ProjectDeveloper projectDeveloper = regApplication.getDeveloperId()!=null?projectDeveloperService.getById(regApplication.getDeveloperId()):null;
        Integer categoryId=regApplication.getCategory()!=null?regApplication.getCategory().getId():null;
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("projectDeveloper",projectDeveloper!=null ? projectDeveloper : new ProjectDeveloper());
        model.addAttribute("categoryList", Category.getCategoryList());
        model.addAttribute("back_url",Urls.RegApplicationApplicant + "?id=" + id);
        model.addAttribute("step_id", 2);
        return Templates.RegApplicationAbout;
    }

    @RequestMapping(value = Urls.RegApplicationAbout,method = RequestMethod.POST)
    public String regApplicationAbout(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "categoryId") Integer categoryId,
            RegApplication regApplication,
            ProjectDeveloper projectDeveloper
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication1 = regApplicationService.getById(id, user.getId());
        if(regApplication1 == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        System.out.println("projectDeveloper.name===" + projectDeveloper.getName());
        System.out.println("projectDeveloper.tin" + projectDeveloper.getTin());
        ProjectDeveloper projectDeveloper1 = regApplication1.getDeveloperId()!=null?projectDeveloperService.getById(regApplication1.getDeveloperId()):new ProjectDeveloper();
        projectDeveloper1.setName(projectDeveloper.getName());
        projectDeveloper1.setTin(projectDeveloper.getTin());
        projectDeveloper1 = projectDeveloperService.save(projectDeveloper1);
        regApplication1.setObjectId(regApplication.getObjectId());
        regApplication1.setActivityId(regApplication.getActivityId());
        regApplication1.setCategory(Category.getCategory(categoryId));
        regApplication1.setDeveloperId(projectDeveloper1.getId());
        regApplicationService.save(regApplication1);
        return "redirect:" + Urls.RegApplicationWaiting + "?id=" + id;
    }

    @RequestMapping(value = Urls.RegApplicationWaiting,method = RequestMethod.GET)
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
        model.addAttribute("back_url",Urls.RegApplicationAbout + "?id=" + id);
        model.addAttribute("step_id", 2);
        return Templates.RegApplicationWaiting;
    }

    @RequestMapping(value = Urls.RegApplicationWaiting,method = RequestMethod.POST)
    public String regApplicationWaiting(
            @RequestParam(name = "id") Integer id
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        return "redirect:" + Urls.RegApplicationContract + "?id=" + id;
    }

    @RequestMapping(value = Urls.RegApplicationContract,method = RequestMethod.GET)
    public String getContractPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        Offer offer = offerService.getOffer(locale);

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("offer", offer);
        model.addAttribute("back_url",Urls.RegApplicationWaiting + "?id=" + id);
        model.addAttribute("step_id", 3);
        return Templates.RegApplicationContract;
    }

    @RequestMapping(value = Urls.RegApplicationContract,method = RequestMethod.POST)
    public String regApplicationContract(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "offerId") Integer offerId
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        return "redirect:" + Urls.RegApplicationPrepayment + "?id=" + id;
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
//        Payment payment = paymentService.checkRegApplicationPaymentStatus(regApplication);
        /*if (payment.getStatus() != PaymentStatus.Success) {
            billingService.getWorkingInvoiceDtoByRegIndividualAndPayment(regApplication, payment);
            regApplication.setPaymentId(payment.getId());
            regApplicationService.update(regApplication,StepNumbers.PaymentStep);
        }else{
            billingService.getInvoicePaymentInfo(payment);
        }

        if (payment.getPaymentStatus().equals(PaymentStatus.Success)) {
            return "redirect:" + RegUrls.RegApplicationWaiting + "?id=" + regApplication.getId();
        }*/

//        model.addAttribute("payment", payment);
        model.addAttribute("regApplication", regApplication);
//        model.addAttribute("bank_url", Urls.RegApplicationBank + "?id=" + id);
        model.addAttribute("upay_url", Urls.RegApplicationPayment + "?id=" + id);
//        model.addAttribute("transfer_url", Urls.RegApplicationTransfer + "?id=" + id);

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
