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
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.dto.expertise.ForeignIndividualDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualEntrepreneurDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.dto.gnk.GnkResponseObject;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.client.OKED;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.ecoexpertise.mips.i_passport_info.IndividualPassportInfoResponse;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.client.OKEDService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.client.OpfService;
import uz.maroqand.ecology.core.service.gnk.GnkService;
import uz.maroqand.ecology.ecoexpertise.mips.MIPIndividualsPassportInfoService;
import uz.maroqand.ecology.core.service.sys.CountryService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.ecoexpertise.constant.reg.RegTemplates;
import uz.maroqand.ecology.ecoexpertise.constant.reg.RegUrls;

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
    private final InvoiceService invoiceService;
    private final CommentService commentService;
    private final RegApplicationLogService regApplicationLogService;
    private final CountryService countryService;
    private final OKEDService okedService;
    private final GnkService gnkService;
    private final MIPIndividualsPassportInfoService mipIndividualsPassportInfoService;

    @Autowired
    public RegApplicationController(
            UserService userService,
            SoatoService soatoService,
            OpfService opfService,
            RegApplicationService regApplicationService,
            ClientService clientService,
            ActivityService activityService,
            ObjectExpertiseService objectExpertiseService,
            ProjectDeveloperService projectDeveloperService,
            OfferService offerService,
            PaymentService paymentService,
            RequirementService requirementService,
            OrganizationService organizationService,
            HelperService helperService,
            FileService fileService,
            InvoiceService invoiceService,
            CommentService commentService,
            RegApplicationLogService regApplicationLogService,
            CountryService countryService,
            OKEDService okedService,
            GnkService gnkService,
            MIPIndividualsPassportInfoService mipIndividualsPassportInfoService
    ) {
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
        this.invoiceService = invoiceService;
        this.commentService = commentService;
        this.regApplicationLogService = regApplicationLogService;
        this.countryService = countryService;
        this.okedService = okedService;
        this.gnkService = gnkService;
        this.mipIndividualsPassportInfoService = mipIndividualsPassportInfoService;
    }

    @RequestMapping(value = RegUrls.RegApplicationList)
    public String getRegApplicationListPage() {

        return RegTemplates.RegApplicationList;
    }

    @RequestMapping(value = RegUrls.RegApplicationListAjax,produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> getRegApplicationListAjax(
            Pageable pageable
    ) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        User user = userService.getCurrentUserFromContext();

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(null,null,null,null,user.getId(),pageable);
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

    @RequestMapping(value = RegUrls.RegApplicationDashboard)
    public String getDashboardPage() {

        return RegTemplates.RegApplicationDashboard;
    }


    /*
    * Start
    * */
    @RequestMapping(value = RegUrls.RegApplicationStart)
    public String getStart() {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.create(user);

        return "redirect:"+ RegUrls.RegApplicationApplicant + "?id=" + regApplication.getId();
    }

    @RequestMapping(value = RegUrls.RegApplicationResume,method = RequestMethod.GET)
    public String getResumeMethod(
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        switch (regApplication.getStep()){
            case APPLICANT: return "redirect:" + RegUrls.RegApplicationApplicant + "?id=" + regApplication.getId();
            case ABOUT: return "redirect:" + RegUrls.RegApplicationAbout + "?id=" + regApplication.getId();
            case CONTRACT: return "redirect:" + RegUrls.RegApplicationContract + "?id=" + regApplication.getId();
            case PAYMENT: return "redirect:" + RegUrls.RegApplicationPrepayment + "?id=" + regApplication.getId();
            case STATUS: return "redirect:" + RegUrls.RegApplicationStatus+ "?id=" + regApplication.getId();
        }

        return "redirect:" + RegUrls.RegApplicationList;
    }

    @RequestMapping(value = RegUrls.RegApplicationApplicantCancel)
    public String getCancelMethod(
            @RequestParam(name = "id") Integer regApplicationId,
            @RequestParam(name = "message") String message
    ){
        System.out.println("id==" + regApplicationId);
        System.out.println("message=" + message);
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regApplicationId,user.getId());
        if (regApplication==null){
            System.out.println("null");
            return "redirect:" + RegUrls.RegApplicationList;
        }

        regApplication.setMessage(message);
        regApplication.setDeleted(Boolean.TRUE);
        regApplicationService.update(regApplication);

        return "redirect:" + RegUrls.RegApplicationList;
    }

    @RequestMapping(value = RegUrls.RegApplicationApplicant,method = RequestMethod.GET)
    public String getApplicantPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        /*if (regApplication.getConfirmStatus()!=null && regApplication.getConfirmStatus()== LogStatus.Approved){
            return "redirect:" + Urls.RegApplicationWaiting + "?id=" + id;
        }*/
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        Client applicant = regApplication.getApplicant();
        if(applicant==null || applicant.getType()==null){
            applicant = new Client();
            applicant.setType(ApplicantType.LegalEntity);
        }

        model.addAttribute("individual", new IndividualDto());
        model.addAttribute("legalEntity", new LegalEntityDto());
        model.addAttribute("foreignIndividual", new ForeignIndividualDto());
        model.addAttribute("individualEntrepreneur", new IndividualEntrepreneurDto());

        switch (applicant.getType()){
            case Individual:
            model.addAttribute("individual", new IndividualDto(applicant));break;
            case LegalEntity:
            model.addAttribute("legalEntity", new LegalEntityDto(applicant));break;
            case ForeignIndividual:
            model.addAttribute("foreignIndividual", new ForeignIndividualDto(applicant));break;
            case IndividualEnterprise:
                model.addAttribute("individualEntrepreneur", new IndividualEntrepreneurDto(applicant));break;
        }

        model.addAttribute("applicant", applicant);
        model.addAttribute("opfLegalEntityList", opfService.getOpfLegalEntityList());
        model.addAttribute("opfIndividualList", opfService.getOpfIndividualList());
        model.addAttribute("countriesList", countryService.getCountriesList(locale));
        model.addAttribute("regions", soatoService.getRegions());
        model.addAttribute("sub_regions", soatoService.getSubRegions());
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("step_id", RegApplicationStep.APPLICANT.ordinal()+1);
        return RegTemplates.RegApplicationApplicant;
    }

    @RequestMapping(value = RegUrls.RegApplicationApplicant,method = RequestMethod.POST)
    public String createRegApplication(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "applicantType") String applicantType,
            LegalEntityDto legalEntityDto,
            IndividualDto individualDto,
            ForeignIndividualDto foreignIndividualDto,
            IndividualEntrepreneurDto individualEntrepreneurDto
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        Client applicant = regApplication.getApplicant();

        switch (applicantType){
            case "LegalEntity":applicant = clientService.saveLegalEntity(legalEntityDto, user, "regApplicationId="+regApplication.getId());break;
            case "Individual":applicant = clientService.saveIndividual(individualDto, user, "regApplicationId="+regApplication.getId());break;
            case "IndividualEnterprise":applicant = clientService.saveIndividualEntrepreneur(individualEntrepreneurDto, user, "regApplicationId="+regApplication.getId());break;
            case "ForeignIndividual":applicant = clientService.saveForeignIndividual(foreignIndividualDto, user, "regApplicationId="+regApplication.getId());break;
        }

        regApplication.setApplicantType(applicant.getType());
        regApplication.setApplicantId(applicant.getId());
        regApplication.setStep(RegApplicationStep.ABOUT);
        regApplicationService.update(regApplication);
        return "redirect:" + RegUrls.RegApplicationAbout + "?id=" + id;
    }


    @RequestMapping(value = RegUrls.RegApplicationAbout,method = RequestMethod.GET)
    public String getAboutPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        /*if (regApplication.getConfirmStatus()!=null && regApplication.getConfirmStatus()== LogStatus.Approved){
            return "redirect:" + Urls.RegApplicationWaiting + "?id=" + id;
        }*/

        if (regApplication.getInvoiceId()!=null){
            return "redirect:" + RegUrls.RegApplicationPrepayment + "?id=" + id;
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
        model.addAttribute("back_url", RegUrls.RegApplicationApplicant + "?id=" + id);
        model.addAttribute("step_id", RegApplicationStep.ABOUT.ordinal()+1);
        return RegTemplates.RegApplicationAbout;
    }

    @RequestMapping(value = RegUrls.RegApplicationAbout,method = RequestMethod.POST)
    public String regApplicationAbout(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "projectDeveloperName") String projectDeveloperName,
            RegApplication regApplication,
            ProjectDeveloper projectDeveloper
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication1 = regApplicationService.getById(id, user.getId());
        if(regApplication1 == null){
            return "redirect:" + RegUrls.RegApplicationList;
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

        RegApplicationLog regApplicationLog = regApplicationLogService.create(regApplication1,LogType.Confirm,"",user);
        regApplication1.setConfirmLogId(regApplicationLog.getId());

        regApplication1.setStep(RegApplicationStep.ABOUT);
        regApplicationService.update(regApplication1);


        return "redirect:" + RegUrls.RegApplicationWaiting + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationGetMaterial, method = RequestMethod.POST)
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
    @RequestMapping(value = RegUrls.RegApplicationFileUpload, method = RequestMethod.POST, produces = "application/json")
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
            regApplicationService.update(regApplication);

            responseMap.put("name", file.getName());
            responseMap.put("link", RegUrls.RegApplicationFileDownload+ "?file_id=" + file.getId());
            responseMap.put("fileId", file.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    //fileDownload
    @RequestMapping(RegUrls.RegApplicationFileDownload)
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
    @RequestMapping(value = RegUrls.RegApplicationFileDelete, method = RequestMethod.POST, produces = "application/json")
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
        File file = fileService.findByIdAndUploadUserId(fileId, user.getId());

        if (file != null) {
            Set<File> fileSet = regApplication.getDocumentFiles();
            if(fileSet.contains(file)) {
                fileSet.remove(file);
                regApplicationService.update(regApplication);

                file.setDeleted(true);
                file.setDateDeleted(new Date());
                file.setDeletedById(user.getId());
                fileService.save(file);

                responseMap.put("status", 1);
            }
        }
        return responseMap;
    }

    @RequestMapping(value = RegUrls.RegApplicationWaiting,method = RequestMethod.GET)
    public String getWaitingPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
        if (regApplicationLog==null){
            return "redirect:" + RegUrls.RegApplicationAbout + "?id=" + id;
        }

        if (regApplication.getInvoiceId()!=null){
            return "redirect:" + RegUrls.RegApplicationPrepayment + "?id=" + id;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("regApplicationLog", regApplicationLog);
        model.addAttribute("back_url", RegUrls.RegApplicationAbout + "?id=" + id);
        model.addAttribute("step_id", RegApplicationStep.ABOUT.ordinal()+1);
        return RegTemplates.RegApplicationWaiting;
    }

    @RequestMapping(value = RegUrls.RegApplicationWaiting,method = RequestMethod.POST)
    public String regApplicationWaiting(
            @RequestParam(name = "id") Integer id
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        return "redirect:" + RegUrls.RegApplicationContract + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationContract,method = RequestMethod.GET)
    public String getContractPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        if (regApplication.getInvoiceId()!=null){
            return "redirect:" + RegUrls.RegApplicationPrepayment + "?id=" + id;
        }

        Offer offer = offerService.getOffer(locale);

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("offer", offer);
        model.addAttribute("back_url", RegUrls.RegApplicationWaiting + "?id=" + id);
        model.addAttribute("step_id", RegApplicationStep.CONTRACT.ordinal()+1);
        return RegTemplates.RegApplicationContract;
    }

    @RequestMapping(value = RegUrls.RegApplicationContract,method = RequestMethod.POST)
    public String regApplicationContract(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "offerId") Integer offerId
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }


        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        Offer offer = offerService.getOffer(locale);
        regApplication.setOfferId(offer.getId());

        String contractNumber = organizationService.getContractNumber(regApplication.getReviewId());
        regApplication.setContractNumber(contractNumber);
        regApplication.setContractDate(new Date());
        regApplication.setStep(RegApplicationStep.PAYMENT);
        regApplicationService.update(regApplication);

        return "redirect:" + RegUrls.RegApplicationPrepayment + "?id=" + id;
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
        Requirement requirement = requirementService.getById(regApplication.getRequirementId());

        Invoice invoice = null;
        if (regApplication.getInvoiceId()==null){
            invoice = invoiceService.create(regApplication,requirement);
        }else{
            invoice = invoiceService.getInvoice(regApplication.getInvoiceId());

            //todo vaqtinchalik
            if (invoice.getStatus()== InvoiceStatus.Success){
                return "redirect:" + RegUrls.RegApplicationStatus + "?id=" + id + "&invoiceId=" + invoice.getId();
            }
        }
        regApplication.setInvoiceId(invoice.getId());
        regApplicationService.update(regApplication);
        model.addAttribute("invoice", invoice);
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("upay_url", RegUrls.RegApplicationStatus+ "?id=" + id);//todo to`grilash kerak

        model.addAttribute("step_id", RegApplicationStep.PAYMENT.ordinal()+1);
        return RegTemplates.RegApplicationPrepayment;
    }

    /*@RequestMapping(value = Urls.RegApplicationPayment)
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
    }*/

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

        String successUrl = RegUrls.RegApplicationStatus+ "?id=" + applicationId;
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
            @RequestParam(name = "invoiceId") Integer invoiceId,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        Invoice invoice = invoiceService.getInvoice(invoiceId);
        if (invoice.getStatus()!=InvoiceStatus.Success){
            invoice = invoiceService.payTest(invoiceId);
        }

        if(regApplication.getForwardingLogId()==null){
            RegApplicationLog regApplicationLog = regApplicationLogService.create(regApplication,LogType.Forwarding,"",user);
            regApplication.setForwardingLogId(regApplicationLog.getId());
            regApplication.setStatus(RegApplicationStatus.Initial);
            regApplication.setRegistrationDate(new Date());
            regApplication.setDeadlineDate(regApplicationLogService.getDeadlineDate(regApplication.getDeadline(), new Date()));
            regApplicationService.update(regApplication);
        }

        List<Comment> commentList = commentService.getListByRegApplicationId(regApplication.getId());
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("commentList", commentList);
        model.addAttribute("invoice", invoice);
        model.addAttribute("back_url", RegUrls.RegApplicationList);
        model.addAttribute("step_id", RegApplicationStep.STATUS.ordinal()+1);
        return RegTemplates.RegApplicationStatus;
    }

    @RequestMapping(value = RegUrls.RegApplicationCommentAdd,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> createCommet(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "comment") String message
    ){
        User user =userService.getCurrentUserFromContext();
        Integer status=1;
        HashMap<String,Object> result = new HashMap<>();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication==null){
            status = 0;
            result.put("status",status);
            return result;
        }

        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setRegApplicationId(regApplication.getId());
        comment.setCreatedAt(new Date());
        comment.setCreatedById(user.getId());
        comment = commentService.createComment(comment);

        result.put("status", status);
        result.put("created",comment.getCreatedAt()!=null?Common.uzbekistanDateAndTimeFormat.format(comment.getCreatedAt()):"");
        result.put("fioShort",helperService.getUserLastAndFirstShortById(comment.getCreatedById()));
        result.put("message",comment.getMessage());

        return result;
    }

    @RequestMapping(value = RegUrls.RegApplicationGetOkedName)
    @ResponseBody
    public HashMap<String,Object> getOkedName(
            @RequestParam(name = "okedCode") String okedCode
    ){
        Integer status=1;
        HashMap<String,Object> result = new HashMap<>();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        OKED oked = okedService.getOkedFromOkedV1Code(okedCode);
        if (oked==null){
            status=0;
            result.put("status",status);
            return result;
        }
        result.put("status",status);
        result.put("okedName",oked.getNameTranslation(locale));
        return result;
    }


    @RequestMapping(value = RegUrls.GetLegalEntityByTin, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public GnkResponseObject getLegalEntityTaxPayerInfo(
            @RequestParam(name = "tin") Integer tin
    ) {
        return gnkService.getLegalEntityByTin(tin);
    }

    @RequestMapping(value = RegUrls.GetIndividualByPinfl, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public IndividualPassportInfoResponse getIndividualInfo(
            @RequestParam(name = "tin", required = false) String tin,
            @RequestParam(name = "p_serial") String serial,
            @RequestParam(name = "pinfl") String pinfl
    ) {
        System.out.println("serial="+serial);
        System.out.println("pinfl="+pinfl);
        return  mipIndividualsPassportInfoService.getPassportInfoBy(serial,pinfl);
    }

}
