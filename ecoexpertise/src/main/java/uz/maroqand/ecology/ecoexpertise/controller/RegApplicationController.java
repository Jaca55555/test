package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.expertise.ConfirmStatus;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.client.OpfService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.ecoexpertise.constant.Templates;
import uz.maroqand.ecology.ecoexpertise.constant.Urls;

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
    private final ProjectDeveloperService projectDeveloperService;
    private final OfferService offerService;
    private final PaymentService paymentService;
    private final RequirementService requirementService;
    private final OrganizationService organizationService;
    private final HelperService helperService;
    private final FileService fileService;

    @Autowired
    public RegApplicationController(UserService userService, SoatoService soatoService, OpfService opfService, RegApplicationService regApplicationService, ClientService clientService, ActivityService activityService, ObjectExpertiseService objectExpertiseService, ProjectDeveloperService projectDeveloperService, OfferService offerService, PaymentService paymentService, RequirementService requirementService, OrganizationService organizationService, HelperService helperService, FileService fileService) {
        this.userService = userService;
        this.soatoService = soatoService;
        this.opfService = opfService;
        this.regApplicationService = regApplicationService;
        this.clientService = clientService;
        this.activityService = activityService;
        this.objectExpertiseService = objectExpertiseService;
        this.projectDeveloperService = projectDeveloperService;
        this.offerService = offerService;
        this.paymentService = paymentService;

        this.requirementService = requirementService;
        this.organizationService = organizationService;
        this.helperService = helperService;
        this.fileService = fileService;
    }

    @RequestMapping(value = Urls.RegApplicationList)
    public String getRegApplicationListPage() {

        return Templates.RegApplicationList;
    }

    @RequestMapping(value = Urls.RegApplicationListAjax,produces = "application/json", method = RequestMethod.POST)
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
                    regApplication.getId(),
                    helperService.getObjectExpertise(regApplication.getObjectId(),locale),
                    helperService.getMaterial(regApplication.getMaterialId(),locale),
                    regApplication.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(regApplication.getCreatedAt()):"",
                    regApplication.getStatus()!=null? helperService.getRegApplicationStatus(regApplication.getStatus().getId(),locale):"",
                    regApplication.getStatus()!=null? regApplication.getStatus().getColor():""
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

    @RequestMapping(value = Urls.RegApplicationResume,method = RequestMethod.GET)
    public String getResumeMethod(
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + Urls.RegApplicationList;
        }

        switch (regApplication.getStep()){
            case APPLICANT: return "redirect:" + Urls.RegApplicationApplicant + "?id=" + regApplication.getId();
            case ABOUT: return "redirect:" + Urls.RegApplicationAbout + "?id=" + regApplication.getId();
            case CONTRACT: return "redirect:" + Urls.RegApplicationContract + "?id=" + regApplication.getId();
            case PAYMENT: return "redirect:" + Urls.RegApplicationPayment + "?id=" + regApplication.getId();
            case STATUS: return "redirect:" + Urls.RegApplicationStatus+ "?id=" + regApplication.getId();
        }

        return "redirect:" + Urls.RegApplicationList;
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

        if (regApplication.getConfirmStatus()!=null && regApplication.getConfirmStatus()==ConfirmStatus.Approved){
            return "redirect:" + Urls.RegApplicationWaiting + "?id=" + id;
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

        if (regApplication.getConfirmStatus()!=null && regApplication.getConfirmStatus()==ConfirmStatus.Approved){
            return "redirect:" + Urls.RegApplicationWaiting + "?id=" + id;
        }

        ProjectDeveloper projectDeveloper = regApplication.getDeveloperId()!=null?projectDeveloperService.getById(regApplication.getDeveloperId()):null;
        Integer categoryId=regApplication.getCategory()!=null?regApplication.getCategory().getId():null;
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("projectDeveloper",projectDeveloper!=null ? projectDeveloper : new ProjectDeveloper());
        model.addAttribute("categoryList", Category.getCategoryList());
        model.addAttribute("requirementList", requirementService.getAllList());
        model.addAttribute("back_url",Urls.RegApplicationApplicant + "?id=" + id);
        model.addAttribute("step_id", 2);
        return Templates.RegApplicationAbout;
    }

    @RequestMapping(value = Urls.RegApplicationAbout,method = RequestMethod.POST)
    public String regApplicationAbout(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "projectDeveloperName") String projectDeveloperName,
//            @RequestParam(name = "categoryId") Integer categoryId,
//            @RequestParam(name = "requirementId") Integer requirementId,
            RegApplication regApplication,
            ProjectDeveloper projectDeveloper
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication1 = regApplicationService.getById(id, user.getId());
        if(regApplication1 == null){
            return "redirect:" + Urls.RegApplicationList;
        }


        ProjectDeveloper projectDeveloper1 = regApplication1.getDeveloperId()!=null?projectDeveloperService.getById(regApplication1.getDeveloperId()):new ProjectDeveloper();
        projectDeveloper1.setName(projectDeveloperName);
        projectDeveloper1.setTin(projectDeveloper.getTin());
        projectDeveloper1 = projectDeveloperService.save(projectDeveloper1);
        regApplication1.setDeveloperId(projectDeveloper1.getId());

        Requirement requirement = requirementService.getById(regApplication.getRequirementId());
        regApplication1.setRequirementId(requirement.getId());
        regApplication1.setReviewId(requirement.getReviewId());
        regApplication1.setDeadline(requirement.getDeadline());

        regApplication1.setObjectId(regApplication.getObjectId());
        regApplication1.setActivityId(regApplication.getActivityId());
        regApplication1.setName(regApplication.getName());
        regApplication1.setCategory(activityService.getById(regApplication.getActivityId()).getCategory());
        regApplication1.setMaterialId(requirement.getMaterialId());

        regApplication1.setConfirmStatus(ConfirmStatus.Initial);

        regApplicationService.save(regApplication1);

        return "redirect:" + Urls.RegApplicationWaiting + "?id=" + id;
    }

    @RequestMapping(value = Urls.RegApplicationGetMaterial, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getRegApplicationGetMaterial(
            @RequestParam(name = "objectId") Integer objectId,
            @RequestParam(name = "activityId") Integer activityId
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> result = new HashMap<>();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        Activity activity = activityService.getById(activityId);
        List<Requirement> requirementList = requirementService.getRequirementMaterials(objectId,activity.getCategory());

        result.put("category", helperService.getCategory(activity.getId(),locale));
        result.put("requirementList", requirementList);
        return result;
    }

    //fileUpload
    @RequestMapping(value = Urls.RegApplicationFileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "file_name") String fileNname,
            @RequestParam(name = "file") MultipartFile multipartFile
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication  = regApplicationService.getById(id);

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (regApplication == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }
        /*if (regApplication.getStatus() != regApplication.Initial) {
            responseMap.put("message", "Object will not able to update.");
            return responseMap;
        }*/

        File file = fileService.uploadFile(multipartFile, user.getId(),"regApplication="+regApplication.getId(),fileNname);
        if (file != null) {
            Set<File> fileSet = regApplication.getDocumentFiles();
            fileSet.add(file);
            regApplicationService.save(regApplication);

            responseMap.put("name", file.getName());
            responseMap.put("link", Urls.RegApplicationFileDownload+ "?file_id=" + file.getId());
            responseMap.put("fileId", file.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    //fileDownload
    @RequestMapping(Urls.RegApplicationFileDownload)
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "file_id") Integer fileId
    ){
        User user = userService.getCurrentUserFromContext();

        File file = fileService.findByIdAndUploadUserId(fileId, user.getId());

        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    //fileDelete
    @RequestMapping(value = Urls.RegApplicationFileDelete, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> deleteFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "fileId") Integer fileId
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id);

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (regApplication == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }
        /*if (regApplication.getStatus() != regApplication.Initial) {
            responseMap.put("message", "Object will not able to update.");
            return responseMap;
        }*/
        File file = fileService.findByIdAndUploadUserId(fileId, user.getId());

        if (file != null) {
            Set<File> fileSet = regApplication.getDocumentFiles();
            if(fileSet.contains(file)) {
                fileSet.remove(file);
                regApplicationService.save(regApplication);

                file.setDeleted(true);
                file.setDateDeleted(new Date());
                file.setDeletedById(user.getId());
                fileService.save(file);

                responseMap.put("status", 1);
            }
        }
        return responseMap;
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

        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        Offer offer = offerService.getOffer(locale);
        regApplication.setOfferId(offer.getId());

        String contractNumber = organizationService.getContractNumber(regApplication.getReviewId());
        regApplication.setContractNumber(contractNumber);
        regApplication.setContractDate(new Date());
        regApplicationService.save(regApplication);

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
