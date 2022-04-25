package uz.maroqand.ecology.ecoexpertise.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import uz.maroqand.ecology.core.config.GlobalConfigs;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.constant.user.ToastrType;
import uz.maroqand.ecology.core.dto.expertise.*;
import uz.maroqand.ecology.core.dto.gnk.GnkResponseObject;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.client.OKED;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.sys.SmsSend;
import uz.maroqand.ecology.core.entity.user.Notification;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.*;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.ToastrService;
import uz.maroqand.ecology.core.repository.expertise.CoordinateLatLongRepository;
import uz.maroqand.ecology.core.repository.expertise.CoordinateRepository;
import uz.maroqand.ecology.ecoexpertise.mips.i_passport_info.IndividualPassportInfoResponse;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.client.OKEDService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.client.OpfService;
import uz.maroqand.ecology.core.service.gnk.GnkService;
import uz.maroqand.ecology.ecoexpertise.mips.MIPIndividualsPassportInfoService;
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
    private final MaterialService materialService;
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
    private final ToastrService toastrService;
    private final CoordinateRepository coordinateRepository;
    private final CoordinateLatLongRepository coordinateLatLongRepository;
    private final SmsSendService smsSendService;
    private final ConclusionService conclusionService;
    private final DocumentRepoService documentRepoService;
    private final NotificationService notificationService;
    private final FactureService factureService;
    private Logger logger = LogManager.getLogger(RegApplicationController.class);
    private final GlobalConfigs globalConfigs;
    private final SubstanceService substanceService;

    @Autowired
    public RegApplicationController(
            UserService userService,
            SoatoService soatoService,
            OpfService opfService,
            RegApplicationService regApplicationService,
            ClientService clientService,
            ActivityService activityService,
            ObjectExpertiseService objectExpertiseService,
            MaterialService materialService,
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
            MIPIndividualsPassportInfoService mipIndividualsPassportInfoService,
            ToastrService toastrService,
            CoordinateRepository coordinateRepository,
            CoordinateLatLongRepository coordinateLatLongRepository,
            SmsSendService smsSendService,
            ConclusionService conclusionService,
            DocumentRepoService documentRepoService,
            NotificationService notificationService,
            FactureService factureService, GlobalConfigs globalConfigs, SubstanceService substanceService) {
        this.userService = userService;
        this.soatoService = soatoService;
        this.opfService = opfService;
        this.regApplicationService = regApplicationService;
        this.clientService = clientService;
        this.activityService = activityService;
        this.objectExpertiseService = objectExpertiseService;
        this.materialService = materialService;
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
        this.toastrService = toastrService;
        this.coordinateRepository = coordinateRepository;
        this.coordinateLatLongRepository = coordinateLatLongRepository;
        this.smsSendService = smsSendService;
        this.conclusionService = conclusionService;
        this.documentRepoService = documentRepoService;
        this.notificationService = notificationService;
        this.factureService = factureService;
        this.globalConfigs = globalConfigs;
        this.substanceService = substanceService;
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
        FilterDto filterDto = new FilterDto();
        filterDto.setByLeTin(user.getLeTin());
        filterDto.setByTin(user.getTin());

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                filterDto,
                null,
                null,
                null,
                user.getId(),
                RegApplicationInputType.ecoService,
                pageable);

        HashMap<String, Object> result = new HashMap<>();

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            convenientForJSONArray.add(new Object[]{
                    regApplication.getId(),
                    helperService.getObjectExpertise(regApplication.getObjectId(),locale),
                    helperService.getMaterials(regApplication.getMaterials(),locale),
                    regApplication.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(regApplication.getCreatedAt()):"",
                    regApplication.getStatus()!=null? helperService.getRegApplicationStatus(regApplication.getStatus().getId(),locale):"",
                    regApplication.getStatus()!=null? regApplication.getStatus().getColor():"",
                    regApplication.getApplicant()!=null?regApplication.getApplicant().getName():" ",
                    regApplication.getApplicant()!=null?regApplication.getApplicant().getTin():" ",
                    regApplication.getName()
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(value = RegUrls.RegApplicationDashboard)
    public String getDashboardPage() {

        return RegTemplates.RegApplicationDashboard;
    }


    @RequestMapping(value = RegUrls.RegApplicationWaitingList)
    public String getWaitingContractList(Model model){

        User user = userService.getCurrentUserFromContext();
        List<Notification> notificationList = notificationService.getListByUser(user);

        List<RegApplication> regApplicationList = new ArrayList<>();
        for (Notification notification: notificationList) {
            RegApplication regApplication = regApplicationService.getById(notification.getApplicationNumber());
            if (regApplication!=null){
                regApplicationList.add(regApplication);
            }
        }

        model.addAttribute("regApplicationList",regApplicationList);
        model.addAttribute("actionUrl",RegUrls.RegApplicationResume);

        return RegTemplates.RegApplicationWaitingContractList;
    }

    /*
    * Start
    * */
    @RequestMapping(value = RegUrls.RegApplicationStart)
    public String getStart() {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.create(user,RegApplicationInputType.ecoService,RegApplicationCategoryType.oneToTree);

        return "redirect:"+ RegUrls.RegApplicationApplicant + "?id=" + regApplication.getId();
    }

    @RequestMapping(value = RegUrls.RegApplicationResume,method = RequestMethod.GET)
    public String getResumeMethod(
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return "redirect:" + RegUrls.RegApplicationList;
            }
        }

        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            return "redirect:" + RegUrls.RegApplicationFourCategoryResume + "?id=" + regApplication.getId();
        }

        switch (regApplication.getStep()){
            case APPLICANT: return "redirect:" + RegUrls.RegApplicationApplicant + "?id=" + regApplication.getId();
            case ABOUT: return "redirect:" + RegUrls.RegApplicationAbout + "?id=" + regApplication.getId();
            case WAITING: return "redirect:" + RegUrls.RegApplicationWaiting + "?id=" + regApplication.getId();
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
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regApplicationId, user.getId());
        if (regApplication==null){
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
        String check = check(regApplication,user);
        if(check != null){
            return check;
        }

        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            return "redirect:" + RegUrls.RegApplicationFourCategoryApplicant + "?id=" + id;
        }

        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        //client begin
        Client applicant = regApplication.getApplicant();
        if(applicant==null || applicant.getType()==null){
            applicant = new Client();
            applicant.setType(ApplicantType.LegalEntity);
        }

        IndividualDto individualDto = new IndividualDto();
        LegalEntityDto legalEntityDto = new LegalEntityDto();
        ForeignIndividualDto foreignIndividualDto = new ForeignIndividualDto();
        IndividualEntrepreneurDto individualEntrepreneurDto = new IndividualEntrepreneurDto();

        switch (applicant.getType()){
            case Individual: individualDto = new IndividualDto(applicant);break;
            case LegalEntity: legalEntityDto = new LegalEntityDto(applicant);break;
            case ForeignIndividual: foreignIndividualDto = new ForeignIndividualDto(applicant);break;
            case IndividualEnterprise: individualEntrepreneurDto = new IndividualEntrepreneurDto(applicant);break;
        }
        individualDto.setIndividualTin(individualDto.getIndividualTin()==null?user.getTin():individualDto.getIndividualTin());
        individualDto.setIndividualPinfl(individualDto.getIndividualPinfl()==null?user.getPinfl():individualDto.getIndividualPinfl());
        individualEntrepreneurDto.setIndividualEntrepreneurPinfl(individualEntrepreneurDto.getIndividualEntrepreneurPinfl()==null?user.getPinfl():individualEntrepreneurDto.getIndividualEntrepreneurPinfl());
        legalEntityDto.setLegalEntityTin(legalEntityDto.getLegalEntityTin()==null?user.getLeTin():legalEntityDto.getLegalEntityTin());
        model.addAttribute("applicant", applicant);
        model.addAttribute("individual", individualDto);
        model.addAttribute("legalEntity", legalEntityDto);
        model.addAttribute("foreignIndividual", foreignIndividualDto);
        model.addAttribute("individualEntrepreneur", individualEntrepreneurDto);
        //client end

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
            IndividualEntrepreneurDto individualEntrepreneurDto,
            ForeignIndividualDto foreignIndividualDto,
            LegalEntityDto legalEntityDto,
            IndividualDto individualDto
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        String check = check(regApplication,user);
        if(check!=null){
            return check;
        }

        Client applicant = regApplication.getApplicant();
        switch (applicantType){
            case "LegalEntity":
                applicant = clientService.saveLegalEntity(legalEntityDto, user, "regApplicationId="+regApplication.getId());
                break;
            case "Individual":
                applicant = clientService.saveIndividual(individualDto, user, "regApplicationId="+regApplication.getId());
                break;
            case "IndividualEnterprise":
                applicant = clientService.saveIndividualEntrepreneur(individualEntrepreneurDto, user, "regApplicationId="+regApplication.getId());
                break;
            case "ForeignIndividual":
                applicant = clientService.saveForeignIndividual(foreignIndividualDto, user, "regApplicationId="+regApplication.getId());
                break;
        }

        regApplication.setApplicantType(applicant.getType());
        regApplication.setApplicantId(applicant.getId());
        regApplication.setStep(RegApplicationStep.ABOUT);
        regApplicationService.update(regApplication);

        /*SmsSend smsSend = smsSendService.getById(regApplication.getCheckedSmsId());
        if (smsSend==null){
            toastrService.create(user.getId(), ToastrType.Error, "Telefon raqam tasdiqlanmagan.","Telefon raqam tasdiqlanmagan.");
            return  "redirect:" + RegUrls.RegApplicationApplicant + "?id=" + id;
        }
        applicant.setMobilePhone("");*/

        return "redirect:" + RegUrls.RegApplicationAbout + "?id=" + id;
    }

    //1 --> yuborildi
    //2 --> arizachi topilmadi
    //3 --> bu raqamga jo'natib bo'lmaydi
    @RequestMapping(value = RegUrls.RegApplicationSendSMSCode,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> regApplicationSendSMSCode(
            @RequestParam(name = "mobilePhone") String mobilePhone,
            @RequestParam(name = "id") Integer id
            ){
        HashMap<String,Object> result = new HashMap<>();
        result.put("status",regApplicationService.sendSMSCode(mobilePhone,id));
        return result;
    }

    //status==1 confirm
    //status==2 regApplication == null
    //status==3 smssend == null
    //status==4 notConfirm
    @RequestMapping(value = RegUrls.RegApplicationGetSMSCode,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> regApplicationGetSMSCode(
            @RequestParam(name = "code") Integer code,
            @RequestParam(name = "phone") String phone,
            @RequestParam(name = "id") Integer id
    ){
        HashMap<String,Object> result = new HashMap<>();
        result.put("status",1);

        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication==null){
            result.put("status",2);
            return  result;
        }
        SmsSend smsSend = smsSendService.getRegApplicationId(id);
        if (smsSend==null){
            result.put("status",3);
            return  result;
        }
        String phoneNumber = smsSendService.getPhoneNumber(phone);
        if (smsSend.getMessage().equals(code.toString()) && (!phoneNumber.isEmpty() && smsSend.getPhone().equals(phoneNumber))){
            regApplication.setCheckedSmsId(smsSend.getId());
            regApplicationService.update(regApplication);
            return result;
        }else{
            result.put("status",4);
            return  result;
        }

    }

    @RequestMapping(value = RegUrls.RegApplicationAbout,method = RequestMethod.GET)
    public String getAboutPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if (regApplication==null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                return "redirect:" + RegUrls.RegApplicationList;
            }
        }
        String check = check(regApplication,user);
        if(check!=null){
            return check;
        }

        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            return "redirect:" + RegUrls.RegApplicationFourCategoryAbout + "?id=" + id;
        }

        Coordinate coordinate = coordinateRepository.findTop1ByRegApplicationIdAndDeletedFalseOrderByIdDesc(regApplication.getId());
        if(coordinate != null){
            model.addAttribute("coordinate", coordinate);
            model.addAttribute("coordinateLatLongList", coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId()));
        }

        List<Category> categoryList = new ArrayList<>();
        for (Category category: Category.getCategoryList()) {
            if (category.getId()!=4){
                categoryList.add(category);
            }
        }
        model.addAttribute("substances",substanceService.getList());
        model.addAttribute("substances1",substanceService.getListByType(SubstanceType.SUBSTANCE_TYPE1));
        model.addAttribute("substances2",substanceService.getListByType(SubstanceType.SUBSTANCE_TYPE2));
        model.addAttribute("substances3",substanceService.getListByType(SubstanceType.SUBSTANCE_TYPE3));
        model.addAttribute("objectExpertiseList", objectExpertiseService.getList());
        model.addAttribute("activityList", activityService.getList());
        model.addAttribute("requirementList", requirementService.getAllList());
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("boilerEnumList",BoilerCharacteristicsEnum.getBoilerCharacteristics());
        model.addAttribute("opfList", opfService.getOpfList());
        model.addAttribute("regions", soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("categoryId", regApplication.getCategory() !=null ? regApplication.getCategory().getId() : null);

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("back_url", RegUrls.RegApplicationApplicant + "?id=" + id);
        model.addAttribute("step_id", RegApplicationStep.ABOUT.ordinal()+1);
        return RegTemplates.RegApplicationAbout;
    }

    @RequestMapping(value = RegUrls.RegApplicationAbout,method = RequestMethod.POST)
    public String regApplicationAbout(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "objectId") Integer objectId,
            @RequestParam(name = "regionId", required = false) Integer regionId,
            @RequestParam(name = "objectRegionId", required = false) Integer objectRegionId,
            @RequestParam(name = "objectSubRegionId", required = false) Integer objectSubRegionId,
            @RequestParam(name = "activityId", required = false) Integer activityId,
            @RequestParam(name = "materials", required = false) Set<Integer> materials,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "individualPhone") String individualPhone,
            @RequestParam(name = "tin") String projectDeveloperTin,
            @RequestParam(name = "projectDeveloperName") String projectDeveloperName,
            @RequestParam(name = "opfId") Integer projectDeveloperOpfId,
            @RequestParam(name = "coordinates", required = false) List<Double> coordinates
    ){
        System.out.println("-----------------");
        System.out.println(regionId);
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        String check = check(regApplication,user);
        if(check!=null){
            return check;
        }

        if(coordinates!=null && !coordinates.isEmpty()) {
            Client client = regApplication.getApplicant();
            Coordinate coordinate = new Coordinate();
            coordinate.setRegApplicationId(regApplication.getId());
            coordinate.setClientId(regApplication.getApplicantId());
            coordinate.setClientName(client != null ? client.getName() : null);
            coordinate.setRegionId(client != null ? client.getRegionId() : null);
            coordinate.setSubRegionId(client != null ? client.getSubRegionId() : null);
            coordinate.setName(name);
            coordinate.setObjectRegionId(objectRegionId);
            coordinate.setObjectSubRegionId(objectSubRegionId);
            coordinate.setNumber(regApplication.getContractNumber());
            coordinate.setLongitude(coordinates.get(0).toString());
            coordinate.setLatitude(coordinates.get(1).toString());
            Coordinate savedCoordinate = coordinateRepository.save(coordinate);

            for (int i = 2; i < coordinates.size(); i++) {
                CoordinateLatLong coordinateLatLong = new CoordinateLatLong();
                coordinateLatLong.setCoordinateId(savedCoordinate.getId());
                coordinateLatLong.setLongitude(coordinates.get(i++).toString());
                coordinateLatLong.setLatitude(coordinates.get(i).toString());
                coordinateLatLongRepository.save(coordinateLatLong);
            }
        }


        ProjectDeveloper projectDeveloper1 = regApplication.getDeveloperId()!=null?projectDeveloperService.getById(regApplication.getDeveloperId()):new ProjectDeveloper();
        projectDeveloper1.setName(projectDeveloperName);
        projectDeveloper1.setTin(projectDeveloperTin);
        projectDeveloper1.setOpfId(projectDeveloperOpfId);
        projectDeveloper1 = projectDeveloperService.save(projectDeveloper1);
        regApplication.setDeveloperId(projectDeveloper1.getId());

        Activity activity = null;
        if(activityId!=null){
            activity = activityService.getById(activityId);
        }
        List<Requirement> requirementList;
        Requirement requirement = null;
        if(activity!=null){
            requirementList = requirementService.getRequirementMaterials(objectId, activity.getCategory());
        }else {
            requirementList = requirementService.getRequirementExpertise(objectId);
        }
        if(requirementList.size()==0){
            return "redirect:" + RegUrls.RegApplicationAbout + "?id=" + id + "&failed=1";
        }else if(requirementList.size()==1){
            requirement = requirementList.get(0);
        }

        if(requirement==null){
            return "redirect:" + RegUrls.RegApplicationAbout + "?id=" + id + "&failed=2";
        }
        Organization organization = null;
        Integer categoryId=activity!=null && activity.getCategory()!=null?activity.getCategory().getId():null;
        if (regionId != null && (
                    (categoryId != null && categoryId>= 3 && categoryId <= 4)
                        || objectId == 15
                        || (objectId==4 && categoryId!=null && categoryId==2)
                )
        ){
            organization = organizationService.getByRegionId(regionId);
            regApplication.setRegionId(regionId);
        }
        regApplication.setReviewId(organization!=null?organization.getId():requirement.getReviewId());
        regApplication.setRequirementId(requirement.getId());
        regApplication.setDeadline(requirement.getDeadline());

        regApplication.setObjectId(objectId);
        regApplication.setName(name);
        regApplication.setObjectRegionId(objectRegionId);
        regApplication.setObjectSubRegionId(objectSubRegionId);
        regApplication.setIndividualPhone(individualPhone);
        regApplication.setMaterials(materials);

        regApplication.setActivityId(activityId);
        regApplication.setCategory(activity!=null? activity.getCategory():Category.CategoryAll);

        RegApplicationLog confirmLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
        if(confirmLog==null || !confirmLog.getStatus().equals(LogStatus.Approved)){
            RegApplicationLog regApplicationLog = regApplicationLogService.create(regApplication,LogType.Confirm,"",user);
            regApplication.setConfirmLogAt(new Date());
            regApplication.setConfirmLogId(regApplicationLog.getId());
            regApplication.setStatus(RegApplicationStatus.CheckSent);
        }

        regApplication.setStep(RegApplicationStep.WAITING);
        regApplicationService.update(regApplication);
        return "redirect:" + RegUrls.RegApplicationWaiting + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationWaiting,method = RequestMethod.GET)
    public String getWaitingPage(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "field",required = false) Integer field,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null) {
                return "redirect:" + RegUrls.RegApplicationList;
            }
        }

        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            return "redirect:" + RegUrls.RegApplicationFourCategoryWaiting + "?id=" + id;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
        if (regApplicationLog==null){
            return "redirect:" + RegUrls.RegApplicationAbout + "?id=" + id;
        }
        if (regApplication.getForwardingLogId() != null){
            return "redirect:" + RegUrls.RegApplicationContract + "?id=" + id;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("field", field);
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
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null) {
                return "redirect:" + RegUrls.RegApplicationList;
            }
        }
        if (regApplication.getConfirmLogId()!=null){
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
            if(regApplicationLog.getStatus()!=LogStatus.Approved){
                toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Arizachi ma'lumotlari tasdiqlanmagan.");
                return "redirect:" + RegUrls.RegApplicationWaiting + "?id=" + id;
            }
        }

        return "redirect:" + RegUrls.RegApplicationContract + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationContract,method = RequestMethod.GET)
    public String getContractPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        System.out.println("contractt=====");
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
           /* toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");*/
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                return "redirect:" + RegUrls.RegApplicationList;
            }
        }

        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            return "redirect:" + RegUrls.RegApplicationFourCategoryContract + "?id=" + id;
        }

        if (regApplication.getConfirmLogId()!=null){
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
            if(regApplicationLog.getStatus()!=LogStatus.Approved){
                toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Kiritilgan ma'lumotlar tasdiqlanishi kutilyabdi.");
                return "redirect:" + RegUrls.RegApplicationWaiting + "?id=" + id;
            }
        }
        if (regApplication.getConfirmLogId()==null){
            toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Kiritilgan ma'lumotlar tasdiqlanishi kerak.");
            return "redirect:" + RegUrls.RegApplicationWaiting + "?id=" + id;
        }

        if (regApplication.getPerformerLogId()!=null){
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
            if(regApplicationLog.getStatus() != LogStatus.Modification){
                return "redirect:" + RegUrls.RegApplicationStatus + "?id=" + id;
            }
        }

        Offer offer;
        if(regApplication.getOfferId()!=null){
            //offerta tasdiqlangan
            offer = offerService.getById(regApplication.getOfferId());
            model.addAttribute("action_url", RegUrls.RegApplicationContract);
        }else
            {
            //offerta tasdiqlanmagan
            RegApplication regApplicationCheck = regApplicationService.getByIdAndUserTin(regApplication.getId(),user);
            if (regApplicationCheck==null || !regApplicationCheck.getId().equals(regApplication.getId())){
                return "redirect:" + RegUrls.RegApplicationWaiting + "?id=" + regApplication.getId() +  "&field=" + -1;
            }
            offer = offerService.getOffer(regApplication.getBudget(),regApplication.getReviewId());
            model.addAttribute("action_url", RegUrls.RegApplicationContractConfirm);
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("offer", offer);
        model.addAttribute("step_id", RegApplicationStep.CONTRACT.ordinal());
        return RegTemplates.RegApplicationContract;
    }

    @RequestMapping(value = RegUrls.RegApplicationContractConfirm,method = RequestMethod.POST)
    public String regApplicationContractConfirm(
            @RequestParam(name = "id") Integer id
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return "redirect:" + RegUrls.RegApplicationList;
            }
        }
        if(regApplication.getOfferId() != null){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Oferta tasdiqlangan.");
            return "redirect:" + RegUrls.RegApplicationContract + "?id=" + id;
        }

        Offer offer = offerService.getOffer(regApplication.getBudget(),regApplication.getReviewId());
        if (offer==null){
            return "redirect:" + RegUrls.RegApplicationList;
        }

        offerService.complete(offer.getId());
        regApplication.setOfferId(offer.getId());
        notificationService.confirmContractRegApplication(regApplication.getId());
        String contractNumber = organizationService.getContractNumber(regApplication.getReviewId());
        regApplication.setContractNumber(contractNumber);
        regApplication.setContractDate(new Date());
        regApplication.setStep(RegApplicationStep.CONTRACT);
        regApplicationService.update(regApplication);

        return "redirect:" + RegUrls.RegApplicationContract + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationContract,method = RequestMethod.POST)
    public String regApplicationContract(
            @RequestParam(name = "id") Integer id
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return "redirect:" + RegUrls.RegApplicationList;
            }
        }
        if(regApplication.getOfferId()==null){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Oferta tasdiqlanmagan.");
            return "redirect:" + RegUrls.RegApplicationContract + "?id=" + id;
        }

        regApplication.setStep(RegApplicationStep.PAYMENT);
        regApplicationService.update(regApplication);

        return "redirect:" + RegUrls.RegApplicationPrepayment + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationPrepayment)
    public String getPrepaymentPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        System.out.println("RegApplicationPrepayment");

        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return "redirect:" + RegUrls.RegApplicationList;
            }
        }

        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            return "redirect:" + RegUrls.RegApplicationFourCategoryPrepayment + "?id=" + id;
        }
        if(regApplication.getOfferId() == null){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Oferta tasdiqlanmagan.");
            return "redirect:" + RegUrls.RegApplicationContract + "?id=" + id;
        }
        Requirement requirement = requirementService.getById(regApplication.getRequirementId());
        Invoice invoice;
        if (regApplication.getInvoiceId()==null){
            invoice = invoiceService.create(regApplication,requirement);
            regApplication.setInvoiceId(invoice.getId());
            regApplicationService.update(regApplication);
        }else{
            invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
            if (invoice.getStatus()==InvoiceStatus.Success || invoice.getStatus()==InvoiceStatus.PartialSuccess){
                return "redirect:" + RegUrls.RegApplicationStatus + "?id=" + id;
            }
            invoice = invoiceService.modification(regApplication, invoice, requirement);
        }

        System.out.println("config==");
        System.out.println(globalConfigs.getIsTesting());
        System.out.println(globalConfigs.getUploadedFilesFolder());
        model.addAttribute("invoice", invoice);
        model.addAttribute("regApplication", regApplication);
//        model.addAttribute("action_url", RegUrls.RegApplicationPaymentSendSms);
        model.addAttribute("action_url", globalConfigs.getIsTesting().equals("test")? RegUrls.RegApplicationPaymentFree : RegUrls.RegApplicationPaymentSendSms);
        model.addAttribute("step_id", RegApplicationStep.PAYMENT.ordinal());
        return RegTemplates.RegApplicationPrepayment;
    }


    @RequestMapping(value = RegUrls.RegApplicationPaymentSendSms)
    @ResponseBody
    public Map<String,Object> sendSmsPayment(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "telephone") String telephone,
            @RequestParam(name = "cardNumber") String cardNumber,
            @RequestParam(name = "cardMonth") String cardMonth,
            @RequestParam(name = "cardYear") String cardYear
    ) {
        System.out.println("id=" + id);
        System.out.println("telephone=" + telephone);
        System.out.println("cardNumber=" + cardNumber);
        System.out.println("cardMonth=" + cardMonth);
        System.out.println("cardYear=" + cardYear);
        String failUrl = RegUrls.RegApplicationPaymentSendSms;
        String successUrl = RegUrls.RegApplicationPaymentConfirmSms;
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if (regApplication==null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
        }
        Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());

        return paymentService.sendSmsPaymentAndGetResponseMap(
                invoice,
                telephone,
                cardNumber,
                cardMonth,
                cardYear,
                successUrl,
                failUrl
        );
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

        return paymentService.confirmSmsAndGetResponseAsMap(
                applicationId,
                paymentId,
                trId,
                confirmSms,
                successUrl,
                failUrl
        );
    }

    @RequestMapping(value = RegUrls.RegApplicationPaymentFree)
    public String getPaymentFreeMethod(
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if (regApplication == null) {
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return "redirect:" + RegUrls.RegApplicationList;
            }
        }

        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            return "redirect:" + RegUrls.RegApplicationFourCategoryPaymentFree + "?id=" + id;
        }

        if (regApplication.getInvoiceId() == null){
            return "redirect:" + RegUrls.RegApplicationPrepayment + "?id=" + id;
        }

        Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
        if (invoice == null){
            return "redirect:" + RegUrls.RegApplicationPrepayment + "?id=" + id;
        }

        if(invoice.getStatus().equals(InvoiceStatus.Success) || invoice.getStatus().equals(InvoiceStatus.PartialSuccess)){
            return "redirect:" + RegUrls.RegApplicationStatus + "?id=" + id;
        }

        //todo invoice amount 0 bo'lishi kerak
        invoiceService.payTest(invoice.getId());
        /*if(invoice.getAmount().equals(0.0)){
            invoiceService.payTest(invoice.getId());
        }*/
        return "redirect:" + RegUrls.RegApplicationStatus + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationStatus)
    public String getStatusPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        System.out.println("RegApplicationStatus");
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return "redirect:" + RegUrls.RegApplicationList;
            }
        }

        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            return "redirect:" + RegUrls.RegApplicationFourCategoryStatus + "?id=" + id;
        }

        Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
        if(invoice == null){
            System.out.println("invoice == null");
            return "redirect:" + RegUrls.RegApplicationPrepayment + "?id=" + id;
        }
        if (invoice.getStatus()!=InvoiceStatus.Success && invoice.getStatus()!=InvoiceStatus.PartialSuccess){
            return "redirect:" + RegUrls.RegApplicationPrepayment + "?id=" + id;
        }

        Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());
        model.addAttribute("conclusion", conclusion);
        if(conclusion != null){
            model.addAttribute("documentRepo", documentRepoService.getDocument(conclusion.getDocumentRepoId()));
        }
        Offer offer = offerService.getById(regApplication.getOfferId());
        model.addAttribute("performerLog", regApplicationLogService.getById(regApplication.getPerformerLogId()));
        model.addAttribute("commentList", commentService.getByRegApplicationIdAndType(regApplication.getId(), CommentType.CHAT));
        model.addAttribute("offer", offer);
        model.addAttribute("invoice", invoice);
        model.addAttribute("facture", factureService.getById(regApplication.getFactureId()));
        model.addAttribute("factureProductList", factureService.getByFactureId(regApplication.getFactureId()));
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("back_url", RegUrls.RegApplicationList);
        model.addAttribute("step_id", RegApplicationStep.STATUS.ordinal());
        return RegTemplates.RegApplicationStatus;
    }

    @RequestMapping(value = RegUrls.RegApplicationResend)
    public String getResendMethod(
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if (regApplication==null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return "redirect:" + RegUrls.RegApplicationList;
            }
        }

        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            return "redirect:" + RegUrls.RegApplicationFourCategoryResend + "?id=" + id;
        }

        if(!regApplication.getStatus().equals(RegApplicationStatus.Modification)){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","");
            return "redirect:" + RegUrls.RegApplicationStatus + "?id=" + regApplication.getId();
        }

        regApplication.setLogIndex(regApplication.getLogIndex()+1);
        RegApplicationLog forwardingLog = regApplicationLogService.create(regApplication,LogType.Forwarding,"",user);
        forwardingLog = regApplicationLogService.update(forwardingLog, LogStatus.Resend,"", user.getId());
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        if(regApplication.getCategory()!=null){
            switch (regApplication.getCategory().getId()){
                case 0:
                case 1:
                    c.add(Calendar.DATE,10);
                    Date currentDatePlusZero = c.getTime();
                    regApplication.setDeadlineDate(currentDatePlusZero);
                    break;
                case 2:
                    c.add(Calendar.DATE,7);
                    Date currentDatePlusTwo = c.getTime();
                    regApplication.setDeadlineDate(currentDatePlusTwo);
                    break;
                case 3:
                    c.add(Calendar.DATE,5);
                    Date currentDatePlusThree = c.getTime();
                    regApplication.setDeadlineDate(currentDatePlusThree);
                    break;
                case 4:
                    c.add(Calendar.DATE,3);
                    Date currentDatePlusFour = c.getTime();
                    regApplication.setDeadlineDate(currentDatePlusFour);
                    break;
            }
        }


        regApplication.setForwardingLogId(forwardingLog.getId());
        regApplication.setPerformerId(null);
        regApplication.setPerformerLogId(null);
        regApplication.setPerformerLogIdNext(null);
        regApplication.setAgreementLogs(null);
        regApplication.setAgreementCompleteLogId(null);
        regApplication.setConclusionId(null);

        regApplication.setStatus(RegApplicationStatus.Process);
        regApplicationService.update(regApplication);

        return "redirect:" + RegUrls.RegApplicationStatus + "?id=" + regApplication.getId();
    }


    @RequestMapping(value = RegUrls.RegApplicationConfirmFacture)
    public String getConfirmFacture( @RequestParam(name = "id")Integer id ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if (regApplication == null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return "redirect:" + RegUrls.RegApplicationList;
            }
        }
        /*UserAdditional userAdditional = userAdditionalService.getById(user.getUserAdditionalId());
        if (userAdditional==null){
            return "redirect:" + RegUrls.RegApplicationList;
        }*/

        regApplication.setFacture(Boolean.TRUE);
        regApplicationService.update(regApplication);

        /*if (userAdditional.getLoginType()== LoginType.EcoExpertiseIgGovUz){
            return "redirect:" + RegUrls.RegApplicationStatus + "?id=" + id;
        }else{
            return "redirect:" + SysUrls.EDSLogin;
        }*/
        return "redirect:" + RegUrls.RegApplicationStatus + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationCommentAdd,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> createCommet(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "regApplicationId") Integer regApplicationId,
            @RequestParam(name = "message") String message
    ){
        User user =userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        Integer status=1;
        HashMap<String,Object> result = new HashMap<>();
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication==null){
            status = 0;
            result.put("status",status);
            return result;
        }
        Comment comment;
        if (id!=null){
            comment = commentService.getById(id);
            if (comment==null){
                result.put("status",0);
                return result;
            }
            comment.setMessage(message);
            commentService.updateComment(comment);
        }else {
            comment = commentService.create(regApplicationId, CommentType.CHAT, message, user.getId());
        }

        result.put("status", status);
        result.put("createdAt",comment.getCreatedAt()!=null?Common.uzbekistanDateAndTimeFormat.format(comment.getCreatedAt()):"");
        result.put("userShorName",helperService.getUserLastAndFirstShortById(comment.getCreatedById()));
        result.put("message",comment.getMessage());
        result.put("commentFiles",comment.getDocumentFiles()!=null && comment.getDocumentFiles().size()>0?comment.getDocumentFiles():"");

        if(regApplication.getPerformerId()!=null){
            notificationService.create(
                    regApplication.getPerformerId(),
                    NotificationType.Expertise,
                    "sys_notification.newCommentApplicant",
                    regApplication.getId() ,
                    " raqamli ariza arizachisidan chat orqali xabar yuborildi.",
                    "/expertise/performer/view?id=" + regApplication.getId(),
                    user.getId()
            );
        }

        return result;
    }

    @RequestMapping(value = RegUrls.RegApplicationCommentFileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> uploadCommentFile(
            @RequestParam(name = "id",required = false) Integer id,
            @RequestParam(name = "regApplicationId") Integer regApplicationId,
            @RequestParam(name = "file") MultipartFile multipartFile,
            @RequestParam(name = "file_name") String fileName
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }

        Comment comment;
        if (id!=null){
            comment = commentService.getById(id);
            if (comment==null || (comment!=null && regApplicationId!=comment.getRegApplicationId())){
                if (regApplication == null) {
                    responseMap.put("message", "Object not found.");
                    return responseMap;
                }
            }
        }else{
            comment = commentService.create(regApplicationId, CommentType.CHAT, "",user.getId());
        }

        File file = fileService.uploadFile(multipartFile, user.getId(),"commentId="+comment.getId(),fileName);
        if (file != null) {
            Set<File> fileSet = comment.getDocumentFiles();
            if (fileSet==null) fileSet = new HashSet<>();
            fileSet.add(file);
            comment.setDocumentFiles(fileSet);
            commentService.updateComment(comment);

            responseMap.put("name", file.getName());
            responseMap.put("description", file.getDescription());
            responseMap.put("link", RegUrls.RegApplicationCommentDownload + "?file_id=" + file.getId() + "&comment_id=" + comment.getId());
            responseMap.put("fileId", file.getId());
            responseMap.put("commentId", comment.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    @RequestMapping(RegUrls.RegApplicationCommentDownload)
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "file_id") Integer fileId,
            @RequestParam(name = "comment_id") Integer commentId
    ){
        Comment comment = commentService.getById(commentId);
        if (comment==null){
            return null;
        }
        File file = fileService.findById(fileId);
        if (file == null) {
            return null;
        } else {
            if (comment.getDocumentFiles().contains(file)){
                return fileService.getFileAsResourceForDownloading(file);
            }else{
                return  null;
            }
        }
    }

    @RequestMapping(RegUrls.RegApplicationConclusionDownload)
    public ResponseEntity<Resource> getConclusionDownload(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "fileId") Integer fileId
    ){
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication==null){
            return null;
        }
        RegApplicationLog performerLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
        if (performerLog==null){
            return null;
        }
        File file = fileService.findById(fileId);
        if (file == null) {
            return null;
        } else {
            if (performerLog.getDocumentFiles().contains(file)){
                return fileService.getFileAsResourceForDownloading(file);
            }else{
                return  null;
            }
        }
    }

    @RequestMapping(value = RegUrls.RegApplicationCommentDelete, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> deleteCommentFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "fileId") Integer fileId
    ) {
        User user = userService.getCurrentUserFromContext();

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (id==null){
            responseMap.put("message", "Object not found.");
            return responseMap;
        }

        Comment comment = commentService.getById(id);
        if (comment == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }
        File file = fileService.findByIdAndUploadUserId(fileId, user.getId());

        if (file != null) {
            Set<File> fileSet = comment.getDocumentFiles();
            if(fileSet.contains(file)) {
                fileSet.remove(file);
                comment.setDocumentFiles(fileSet);
                commentService.updateComment(comment);

                file.setDeleted(true);
                file.setDateDeleted(new Date());
                file.setDeletedById(user.getId());
                fileService.save(file);

                responseMap.put("status", 1);
            }
        }
        return responseMap;
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


    @RequestMapping(value = RegUrls.RegApplicationClearCoordinates, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> clearCoordinates(
            @RequestParam(name = "regApplicationId") Integer regApplicationId
    ) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("status", 0);
        if(regApplicationId == null){
            return result;
        }

        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regApplicationId, user.getId());
        if(regApplication == null){
            return result;
        }

        Coordinate coordinate = coordinateRepository.findTop1ByRegApplicationIdAndDeletedFalseOrderByIdDesc(regApplicationId);
        if(coordinate == null){
            return result;
        }
        List<CoordinateLatLong> coordinateLatLongList = coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId());
        for(CoordinateLatLong coordinateLatLong : coordinateLatLongList){
            coordinateLatLong.setDeleted(true);
            coordinateLatLongRepository.save(coordinateLatLong);
        }
        coordinate.setDeleted(true);
        coordinateRepository.save(coordinate);

        result.put("status", 1);
        return result;
    }

    @RequestMapping(value = RegUrls.RegApplicationGetActivity, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getActivity(
            @RequestParam(name = "objectId") Integer objectId
    ) {
        HashMap<String, Object> result = new HashMap<>();
        if(objectId == null){
            return result;
        }

        List<Category> categoryList = new LinkedList<>();
        List<Requirement> requirementList = requirementService.getRequirementExpertise(objectId);
        for(Requirement requirement: requirementList){
            if (requirement.getCategory().getId()!=4)
            categoryList.add(requirement.getCategory());
        }
        List<Activity> activityList = activityService.getByInCategory(categoryList);
        Collections.sort(categoryList);

        result.put("activityList", activityList);
        result.put("activityListSize", activityList.size());
        result.put("requirementList", requirementList);
        result.put("requirementListSize", requirementList.size());
        result.put("categoryList", categoryList);
        return result;
    }

    @RequestMapping(value = RegUrls.RegApplicationGetMaterials, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getMaterialList(
            @RequestParam(name = "objectId") Integer objectId,
            @RequestParam(name = "activityId", required = false) Integer activityId
    ) {
        HashMap<String, Object> result = new HashMap<>();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        if(objectId == null || activityId == null){
            return result;
        }

        Activity activity = activityService.getById(activityId);
        List<Requirement> requirementList = requirementService.getRequirementMaterials(objectId,activity.getCategory());

        result.put("category", helperService.getCategory(activity.getCategory().getId(),locale));
        result.put("requirementList", requirementList);
        return result;
    }

    @RequestMapping(value = RegUrls.RegApplicationGetMaterial, method = RequestMethod.POST)
    @ResponseBody
    public List<Material> getMaterial(
            @RequestParam(name = "materialId") Integer materialId
    ) {
        return materialService.getList();
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
        RegApplication regApplication = regApplicationService.getById(id, user.getId());

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (regApplication == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }

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
        RegApplication regApplication = regApplicationService.getById(id, user.getId());

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

    @RequestMapping(RegUrls.RegApplicationContractOfferDownload)
    public ResponseEntity<Resource> getOfferDownload(
            @RequestParam(name = "offer_id", required = false) Integer offerId
    ){
        Offer offer;
        if(offerId!=null){
            offer = offerService.getById(offerId);
        }else {
            User user = userService.getCurrentUserFromContext();
            offer = offerService.getOffer(false,user.getOrganizationId());
        }
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        Integer fileId = offerService.getOfferFileIdByLanguage(offer,locale);
        File file = fileService.findById(fileId);

        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    private String check(RegApplication regApplication, User user){
        if(regApplication == null){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
            return "redirect:" + RegUrls.RegApplicationList;
        }
        Boolean modification = true;
        if (regApplication.getPerformerLogId()!=null){
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
            if(regApplicationLog.getStatus() != LogStatus.Modification){
                return "redirect:" + RegUrls.RegApplicationStatus + "?id=" + regApplication.getId();
            }else {
                modification = false;
            }
        }
        if (regApplication.getConfirmLogId()!=null && modification){
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
            if(regApplicationLog.getStatus() != LogStatus.Denied){
                toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Arizachi ma'lumotlarini o'zgartirishga Ruxsat yo'q.");
                return "redirect:" + RegUrls.RegApplicationWaiting + "?id=" + regApplication.getId();
            }
        }
        return null;
    }

}
