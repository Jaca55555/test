package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.config.GlobalConfigs;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.constant.user.LoginType;
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
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.entity.user.UserAdditional;
import uz.maroqand.ecology.core.repository.expertise.CoordinateLatLongRepository;
import uz.maroqand.ecology.core.repository.expertise.CoordinateRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.billing.PaymentService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.client.OKEDService;
import uz.maroqand.ecology.core.service.client.OpfService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.gnk.GnkService;
import uz.maroqand.ecology.core.service.sys.*;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.ToastrService;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.*;

@Controller
public class RegApplicationController {

    private final UserService userService;
    private final UserAdditionalService userAdditionalService;
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
    private final ToastrService toastrService;
    private final CoordinateRepository coordinateRepository;
    private final CoordinateLatLongRepository coordinateLatLongRepository;
    private final SmsSendService smsSendService;
    private final ConclusionService conclusionService;
    private final GlobalConfigs globalConfigs ;
    private final DocumentRepoService documentRepoService;
    private final SubstanceService substanceService;
    private final BoilerCharacteristicsService boilerCharacteristicsService;

    @Autowired
    public RegApplicationController(
            UserService userService,
            UserAdditionalService userAdditionalService, SoatoService soatoService,
            OpfService opfService,
            RegApplicationService regApplicationService,
            ClientService clientService,
            ActivityService activityService,
            ObjectExpertiseService objectExpertiseService,
            MaterialService materialService, ProjectDeveloperService projectDeveloperService,
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
            ToastrService toastrService,
            CoordinateRepository coordinateRepository,
            CoordinateLatLongRepository coordinateLatLongRepository,
            SmsSendService smsSendService,
            ConclusionService conclusionService,
            GlobalConfigs globalConfigs, DocumentRepoService documentRepoService, SubstanceService substanceService, BoilerCharacteristicsService boilerCharacteristicsService) {
        this.userService = userService;
        this.userAdditionalService = userAdditionalService;
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
        this.toastrService = toastrService;
        this.coordinateRepository = coordinateRepository;
        this.coordinateLatLongRepository = coordinateLatLongRepository;
        this.smsSendService = smsSendService;
        this.conclusionService = conclusionService;
        this.globalConfigs = globalConfigs;
        this.documentRepoService = documentRepoService;
        this.substanceService = substanceService;
        this.boilerCharacteristicsService = boilerCharacteristicsService;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationList)
    public String getExpertiseRegApplicationListPage() {

        return ExpertiseTemplates.ExpertiseRegApplicationList;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationListAjax, produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> getExpertiseRegApplicationListAjax(
            Pageable pageable
    ) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        User user = userService.getCurrentUserFromContext();

        FilterDto filterDto = new FilterDto();
        filterDto.setByLeTin(user.getLeTin());
        filterDto.setByTin(user.getTin());
        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(filterDto,null,null,null,user.getRole().getId()!=16 ? user.getId():null,RegApplicationInputType.ecoService,pageable);
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
                    regApplication.getStatus()!=null? regApplication.getStatus().getColor():""
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationDashboard)
    public String getExpertiseRegApplicationDashboardPage() {

        return ExpertiseTemplates.ExpertiseRegApplicationDashboard;
    }


    /*
    * Start
    * */
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationStart)
    public String getExpertiseRegApplicationStart() {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.create(user,RegApplicationInputType.cabinet,RegApplicationCategoryType.oneToTree);

        return "redirect:"+ ExpertiseUrls.ExpertiseRegApplicationApplicant + "?id=" + regApplication.getId();
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationResume,method = RequestMethod.GET)
    public String getExpertiseRegApplicationResumeMethod(
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;
        }

        switch (regApplication.getStep()){
            case APPLICANT: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationApplicant + "?id=" + regApplication.getId();
            case ABOUT: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationAbout + "?id=" + regApplication.getId();
            case CONTRACT: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationContract + "?id=" + regApplication.getId();
            case PAYMENT: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationPrepayment + "?id=" + regApplication.getId();
            case STATUS: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationStatus+ "?id=" + regApplication.getId();
        }

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationApplicantCancel)
    public String getExpertiseRegApplicationApplicantCancelMethod(
            @RequestParam(name = "id") Integer regApplicationId,
            @RequestParam(name = "message") String message
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regApplicationId, user.getId());
        if (regApplication==null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;
        }

        regApplication.setMessage(message);
        regApplication.setDeleted(Boolean.TRUE);
        regApplicationService.update(regApplication);

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;
    }


    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationApplicant,method = RequestMethod.GET)
    public String getExpertiseRegApplicationApplicantPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        String check = check(regApplication,user);
        if(check != null){
            return check;
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
        return ExpertiseTemplates.ExpertiseRegApplicationApplicant;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationApplicant,method = RequestMethod.POST)
    public String createExpertiseRegApplicationApplicant(
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
            return  "redirect:" + ExpertiseUrls.RegApplicationApplicant + "?id=" + id;
        }
        applicant.setMobilePhone("");*/

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationAbout + "?id=" + id;
    }

    //1 --> yuborildi
    //2 --> arizachi topilmadi
    //3 --> bu raqamga jo'natib bo'lmaydi
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationSendSMSCode,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> expertiseRegApplicationSendSMSCode(
            @RequestParam(name = "mobilePhone") String mobilePhone,
            @RequestParam(name = "id") Integer id
            ){
        HashMap<String,Object> result = new HashMap<>();
        result.put("status",regApplicationService.sendSMSCode(mobilePhone,id));
        return result;
    }
    @RequestMapping(value = ExpertiseUrls.ExpertiseGetByUserId,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> expertiseGetByUserId(
            @RequestParam(name = "userId") Integer id
    ){
        HashMap<String,Object> result = new HashMap<>();
        result.put("fio",userService.findById(id).getFName());
        result.put("status",1);
        return result;
    }

    //status==1 confirm
    //status==2 regApplication == null
    //status==3 smssend == null
    //status==4 notConfirm
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationGetSMSCode,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> expertiseRegApplicationGetSMSCode(
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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationAbout,method = RequestMethod.GET)
    public String getExpertiseRegApplicationAboutPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        String check = check(regApplication,user);
        if(check!=null){
            return check;
        }

        Coordinate coordinate = coordinateRepository.findTop1ByRegApplicationIdAndDeletedFalseOrderByIdDesc(regApplication.getId());
        if(coordinate != null){
            model.addAttribute("coordinate", coordinate);
            model.addAttribute("coordinateLatLongList", coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId()));
        }

        model.addAttribute("substances1",substanceService.getListByType(SubstanceType.SUBSTANCE_TYPE1));
        model.addAttribute("substances2",substanceService.getListByType(SubstanceType.SUBSTANCE_TYPE2));
        model.addAttribute("substances3",substanceService.getListByType(SubstanceType.SUBSTANCE_TYPE3));
        System.out.println("substance="+substanceService.getList());
        model.addAttribute("objectExpertiseList", objectExpertiseService.getList());
        model.addAttribute("activityList", activityService.getList());
        model.addAttribute("requirementList", requirementService.getAllList());
        model.addAttribute("categoryList", Category.getCategoryList());
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("categoryId", regApplication.getCategory() !=null ? regApplication.getCategory().getId() : null);

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("opfList", opfService.getOpfList());
        model.addAttribute("regions", soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationApplicant + "?id=" + id);
        model.addAttribute("step_id", RegApplicationStep.ABOUT.ordinal()+1);
        return ExpertiseTemplates.ExpertiseRegApplicationAbout;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationAbout,method = RequestMethod.POST)
    public String expertiseRegApplicationAbout(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "objectId") Integer objectId,
            @RequestParam(name = "regionId", required = false) Integer regionId,
            @RequestParam(name = "activityId", required = false) Integer activityId,
            @RequestParam(name = "materials", required = false) Set<Integer> materials,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "tin") String projectDeveloperTin,
            @RequestParam(name = "opfId") Integer projectDeveloperOpfId,
            @RequestParam(name = "projectDeveloperName") String projectDeveloperName,
            @RequestParam(name = "coordinates", required = false) List<Double> coordinates,
            @RequestParam(name = "objectRegionId", required = false) Integer objectRegionId,
            @RequestParam(name = "objectSubRegionId", required = false) Integer objectSubRegionId,
            @RequestParam(name = "individualPhone") String individualPhone
            ){
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
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationAbout + "?id=" + id + "&failed=1";
        }else if(requirementList.size()==1){
            requirement = requirementList.get(0);
        }

        if(requirement==null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationAbout + "?id=" + id + "&failed=2";
        }
        Integer categoryId=activity!=null && activity.getCategory()!=null?activity.getCategory().getId():null;
        Organization organization = null;
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
        regApplication.setMaterials(materials);
        regApplication.setObjectRegionId(objectRegionId);
        regApplication.setObjectSubRegionId(objectSubRegionId);
        regApplication.setIndividualPhone(individualPhone);
        regApplication.setActivityId(activityId);
        regApplication.setCategory(activity!=null? activity.getCategory():null);
        //todo to'g'rilash kerak
        /*RegApplicationLog confirmLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
        if(confirmLog==null || !confirmLog.getStatus().equals(LogStatus.Approved)){
            RegApplicationLog regApplicationLog = regApplicationLogService.create(regApplication,LogType.Confirm,"",user);
            regApplication.setConfirmLogAt(new Date());
            regApplication.setConfirmLogId(regApplicationLog.getId());
            regApplication.setStatus(RegApplicationStatus.CheckSent);
        }*/

        regApplication.setStep(RegApplicationStep.CONTRACT);
        regApplicationService.update(regApplication);
        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationContract + "?id=" + id;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationContract)
    public String getExpertiseRegApplicationContractPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        String check = check(regApplication,user);
        if(check!=null){
            return check;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("action_url", ExpertiseUrls.ExpertiseRegApplicationContractSubmit);
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationAbout + "?id=" + id);
        model.addAttribute("step_id", RegApplicationStep.CONTRACT.ordinal());
        return ExpertiseTemplates.ExpertiseRegApplicationContract;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationContractSubmit)
    public String expertiseRegApplicationContractSubmit(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "contractDate") String contractDateStr,
            @RequestParam(name = "contractNumber") String contractNumber,
            @RequestParam(name = "budget") Boolean budget
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id,user.getId());

        if (regApplication==null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;
        }

        regApplication.setContractNumber(contractNumber);
        regApplication.setContractDate(DateParser.TryParse(contractDateStr,Common.uzbekistanDateFormat));
        regApplication.setBudget(budget);
        regApplicationService.update(regApplication);

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationPrepayment + "?id=" + id;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationContractUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> expertiseRegApplicationContractUpload(
            @RequestParam(name = "id") Integer regApplicationId,
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

        File file = fileService.uploadFile(multipartFile, user.getId(),"regApplicationId="+regApplicationId,fileName);
        if (file != null) {
            Set<File> fileSet = regApplication.getContractFiles();
            if (fileSet==null) fileSet = new HashSet<>();
            fileSet.add(file);
            regApplication.setContractFiles(fileSet);
            regApplicationService.update(regApplication);

            responseMap.put("name", file.getName());
            responseMap.put("description", file.getDescription());
            responseMap.put("link", ExpertiseUrls.ExpertiseRegApplicationContractDownload + "?file_id=" + file.getId() + "&regApplicationId=" + regApplicationId);
            responseMap.put("fileId", file.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationContractDelete, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> expertiseRegApplicationContractDelete(
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
            Set<File> fileSet = regApplication.getContractFiles();
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

    @RequestMapping(ExpertiseUrls.ExpertiseRegApplicationContractDownload)
    public ResponseEntity<Resource> expertiseRegApplicationContractDownload(
            @RequestParam(name = "file_id") Integer fileId
    ){
        File file = fileService.findById(fileId);

        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationPrepayment)
    public String getExpertiseRegApplicationPrepayment(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;
        }
        regApplication.setStep(RegApplicationStep.PAYMENT);
        regApplicationService.update(regApplication);

        Requirement requirement = requirementService.getById(regApplication.getRequirementId());
        Invoice invoice;
        if (regApplication.getInvoiceId()==null){
            invoice = invoiceService.create(regApplication,requirement);
            regApplication.setInvoiceId(invoice.getId());
            regApplicationService.update(regApplication);
        }else
            {
            invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
            invoiceService.checkInvoiceStatus(invoice);
            if (invoice.getStatus().equals(InvoiceStatus.Success) || invoice.getStatus().equals(InvoiceStatus.PartialSuccess)){
                return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationStatus + "?id=" + id;
            }

            invoice = invoiceService.modification(regApplication, invoice, requirement);

        }
        model.addAttribute("invoice", invoice);
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("action_url", ExpertiseUrls.ExpertiseRegApplicationPaymentSendSms);
        model.addAttribute("action_url", globalConfigs.getIsTesting().equals("test") ? ExpertiseUrls.ExpertiseRegApplicationPaymentFree : ExpertiseUrls.ExpertiseRegApplicationPaymentSendSms);
        model.addAttribute("step_id", RegApplicationStep.PAYMENT.ordinal());
        return ExpertiseTemplates.ExpertiseRegApplicationPrepayment;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationPaymentSendSms)
    @ResponseBody
    public Map<String,Object> sendExpertiseRegApplicationPaymentSendSms(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "telephone") String telephone,
            @RequestParam(name = "cardNumber") String cardNumber,
            @RequestParam(name = "cardMonth") String cardMonth,
            @RequestParam(name = "cardYear") String cardYear
    ) {
        String failUrl = ExpertiseUrls.ExpertiseRegApplicationPaymentSendSms;
        String successUrl = ExpertiseUrls.ExpertiseRegApplicationPaymentConfirmSms;
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationPaymentConfirmSms)
    @ResponseBody
    public Map<String, Object> expertiseRegApplicationPaymentConfirmSms(
            @RequestParam(name = "id") Integer applicationId,
            @RequestParam(name = "trId") Integer trId,
            @RequestParam(name = "paymentId") Integer paymentId,
            @RequestParam(name = "confirmSms") String confirmSms
    ) {
        String successUrl = ExpertiseUrls.ExpertiseRegApplicationStatus+ "?id=" + applicationId;
        String failUrl = ExpertiseUrls.ExpertiseRegApplicationPaymentConfirmSms;

        return paymentService.confirmSmsAndGetResponseAsMap(
                applicationId,
                paymentId,
                trId,
                confirmSms,
                successUrl,
                failUrl
        );
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationPaymentFree)
    public String expertiseRegApplicationPaymentFree(
            @RequestParam(name = "id") Integer id
    ) {
        System.out.println("id="+id);
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if (regApplication == null) {
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;
        }
        if (regApplication.getInvoiceId() == null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationPrepayment + "?id=" + id;
        }

        Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
        if (invoice == null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationPrepayment + "?id=" + id;
        }

        if(invoice.getStatus().equals(InvoiceStatus.Success) || invoice.getStatus().equals(InvoiceStatus.PartialSuccess)){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationStatus + "?id=" + id;
        }

        //todo invoice amount 0 bo'lishi kerak
        invoiceService.payTest(invoice.getId());
        /*if(invoice.getAmount().equals(0.0)){
            invoiceService.payTest(invoice.getId());
        }*/
        if(regApplication.getForwardingLogId() == null){
            regApplication.setLogIndex(1);
            RegApplicationLog forwardingLog = regApplicationLogService.create(regApplication,LogType.Forwarding,"",user);
            regApplication.setDeadlineDate(regApplicationLogService.getDeadlineDate(regApplication.getDeadline(), new Date()));
            regApplication.setForwardingLogId(forwardingLog.getId());
            regApplication.setRegistrationDate(new Date());
            regApplication.setPerformerLogId(null);
            regApplication.setAgreementLogs(null);
            regApplication.setAgreementCompleteLogId(null);
            regApplication.setStatus(RegApplicationStatus.Process);
            regApplicationService.update(regApplication);
        }

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationStatus + "?id=" + id;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationStatus)
    public String getExpertiseRegApplicationStatusPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;
        }
        regApplication.setStep(RegApplicationStep.STATUS);
        regApplicationService.update(regApplication);

        //testPay
        Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
        if(invoice == null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationPrepayment + "?id=" + id;
        }
        invoiceService.checkInvoiceStatus(invoice);
        if (!invoice.getStatus().equals(InvoiceStatus.Success) && !invoice.getStatus().equals(InvoiceStatus.PartialSuccess)){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationPrepayment + "?id=" + id;
        }

        if(regApplication.getForwardingLogId()==null){
            regApplicationService.sendRegApplicationAfterPayment(regApplication,user,invoice,LocaleContextHolder.getLocale().toLanguageTag());
        }
        List<Comment> commentList = commentService.getByRegApplicationIdAndType(regApplication.getId(), CommentType.CHAT);
        RegApplicationLog performerLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
        File conclusionFile = new File();
        if(performerLog!=null && performerLog.getDocumentFiles()!=null && performerLog.getDocumentFiles().size()>0){
            conclusionFile = performerLog.getDocumentFiles().iterator().next();
        }
        model.addAttribute("conclusionFile", conclusionFile);

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getAgreementCompleteLogId());

        Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());
        model.addAttribute("conclusion", conclusion);
        if(conclusion != null){
            model.addAttribute("documentRepo", documentRepoService.getDocument(conclusion.getDocumentRepoId()));
        }

        model.addAttribute("agreementLogList", regApplicationLogService.getByIds(regApplication.getAgreementLogs()));
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("agreementCompleteLog", regApplicationLog);
        model.addAttribute("commentList", commentList);
        model.addAttribute("invoice", invoice);
        model.addAttribute("performerLog", performerLog);
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationList);
        model.addAttribute("step_id", RegApplicationStep.STATUS.ordinal());
        return ExpertiseTemplates.ExpertiseRegApplicationStatus;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationResend)
    public String getExpertiseRegApplicationResendMethod(
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if (regApplication==null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;
        }

        if(!regApplication.getStatus().equals(RegApplicationStatus.Modification)){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","");
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationStatus + "?id=" + regApplication.getId();
        }

        regApplication.setLogIndex(regApplication.getLogIndex()+1);
        RegApplicationLog forwardingLog = regApplicationLogService.create(regApplication,LogType.Forwarding,"",user);
        regApplication.setForwardingLogId(forwardingLog.getId());
        regApplication.setPerformerLogId(null);
        regApplication.setAgreementLogs(null);
        regApplication.setAgreementCompleteLogId(null);

        regApplication.setStatus(RegApplicationStatus.Process);
        regApplicationService.update(regApplication);

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationStatus + "?id=" + regApplication.getId();
    }


    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationConfirmFacture)
    public String getExpertiseRegApplicationConfirmFacture(@RequestParam(name = "id")Integer id){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if (regApplication==null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;
        }
        UserAdditional userAdditional = userAdditionalService.getById(user.getUserAdditionalId());
        if (userAdditional==null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;
        }

        if (userAdditional.getLoginType()== LoginType.EcoExpertiseIgGovUz){
            regApplication.setFacture(Boolean.TRUE);
            regApplicationService.update(regApplication);
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationStatus + "?id=" + id;
        }else{
            return null;
        }
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationCommentAdd,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> createExpertiseRegApplicationCommentAdd(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "regApplicationId") Integer regApplicationId,
            @RequestParam(name = "message") String message
    ){
        User user =userService.getCurrentUserFromContext();
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

        return result;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationCommentFileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> expertiseRegApplicationCommentFileUpload(
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
            responseMap.put("link", ExpertiseUrls.ExpertiseRegApplicationCommentDownload + "?file_id=" + file.getId() + "&comment_id=" + comment.getId());
            responseMap.put("fileId", file.getId());
            responseMap.put("commentId", comment.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    @RequestMapping(ExpertiseUrls.ExpertiseRegApplicationCommentDownload)
    public ResponseEntity<Resource> expertiseRegApplicationCommentDownload(
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

    @RequestMapping(ExpertiseUrls.ExpertiseRegApplicationConclusionDownload)
    public ResponseEntity<Resource> expertiseRegApplicationConclusionDownload(
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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationCommentDelete, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> expertiseRegApplicationCommentDelete(
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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationGetOkedName)
    @ResponseBody
    public HashMap<String,Object> expertiseRegApplicationGetOkedName(
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


    @RequestMapping(value = ExpertiseUrls.GetLegalEntityByTin, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public GnkResponseObject getLegalEntityTaxPayerInfo(
            @RequestParam(name = "tin") Integer tin
    ) {
        return gnkService.getLegalEntityByTin(tin);
    }

    /*@RequestMapping(value = ExpertiseUrls.GetIndividualByPinfl, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public IndividualPassportInfoResponse getIndividualInfo(
            @RequestParam(name = "tin", required = false) String tin,
            @RequestParam(name = "p_serial") String serial,
            @RequestParam(name = "pinfl") String pinfl
    ) {
        System.out.println("serial="+serial);
        System.out.println("pinfl="+pinfl);
        return  mipIndividualsPassportInfoService.getPassportInfoBy(serial,pinfl);
    }*/


    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationClearCoordinates, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> expertiseRegApplicationClearCoordinates(
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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationGetActivity, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> expertiseRegApplicationGetActivity(
            @RequestParam(name = "objectId") Integer objectId
    ) {
        HashMap<String, Object> result = new HashMap<>();
        if(objectId == null){
            return result;
        }

        List<Category> categoryList = new LinkedList<>();
        List<Requirement> requirementList = requirementService.getRequirementExpertise(objectId);
        for(Requirement requirement: requirementList){
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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationGetMaterials, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> expertiseRegApplicationGetMaterials(
            @RequestParam(name = "objectId") Integer objectId,
            @RequestParam(name = "activityId", required = false) Integer activityId
    ) {
        HashMap<String, Object> result = new HashMap<>();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        if(objectId == null){
            return result;
        }

        ObjectExpertise objectExpertise = objectExpertiseService.getById(objectId);

        if(objectExpertise == null){
            return result;
        }
        List<Requirement> requirementList=new ArrayList<>();
        if (activityId==null){
            requirementList = requirementService.getRequirementExpertise(objectId);
            if (requirementList.size()<1) return result;
            Category category = requirementList.get(0).getCategory();
            for (Requirement requirement:requirementList) {
                if (!category.equals(requirement.getCategory())) return result;
            }
        }else{
            Activity activity = activityService.getById(activityId);
            requirementList = requirementService.getRequirementMaterials(objectId,activity.getCategory());
            result.put("category", helperService.getCategory(activity.getCategory().getId(),locale));
        }
        result.put("requirementList", requirementList);
        return result;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationGetMaterial, method = RequestMethod.POST)
    @ResponseBody
    public List<Material> expertiseRegApplicationGetMaterial(
            @RequestParam(name = "materialId") Integer materialId
    ) {
        return materialService.getList();
    }

    //fileUpload
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> expertiseRegApplicationFileUpload(
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
            responseMap.put("link", ExpertiseUrls.ExpertiseRegApplicationFileDownload+ "?file_id=" + file.getId());
            responseMap.put("fileId", file.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    //fileDownload
    @RequestMapping(ExpertiseUrls.ExpertiseRegApplicationFileDownload)
    public ResponseEntity<Resource> expertiseRegApplicationFileDownload(
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
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFileDelete, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> expertiseRegApplicationFileDelete(
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



    private String check(RegApplication regApplication, User user){
        if(regApplication == null){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;
        }
        if (regApplication.getInvoiceId()!=null){
            Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
            System.out.println("invoice status=" + invoice.getStatus());
            if(invoice!=null && (invoice.getStatus().equals(InvoiceStatus.Success) || invoice.getStatus().equals(InvoiceStatus.PartialSuccess))){
                toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Arizachi ma'lumotlarini o'zgartirishga Ruxsat yo'q.");
                return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationStatus + "?id=" + regApplication.getId();
            }
        }

        return null;
    }




    //Boiler CRUD
    @RequestMapping(value = ExpertiseUrls.RegApplicationFourCategoryBoilerCharacteristicsCreate)
    @ResponseBody
    public HashMap<String,Object> regApplicationFourCategoryBoilerCharacteristicsCreate(
            @RequestParam(name = "regId") Integer regId,
            @RequestParam(name = "typeBoiler") Integer typeBoiler,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "type") String type,
            @RequestParam(name = "amount") Double amount,
            @RequestParam(name = "boilerEnum",required = false) Integer boilerEnum

            ){
        System.out.println("regApplicationFourCategoryBoilerCharacteristicsCreate");
        Integer status = 0;
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regId);

        HashMap<String,Object> response = new HashMap<>();
        response.put("status",status);

        if (regApplication==null ){
            return response;
        }
//        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regId);
//        if (regApplicationCategoryFourAdditional==null ){
//            return response;
//        }
        Set<BoilerCharacteristics> boilerCharacteristics = regApplication.getBoilerCharacteristics();
        if (boilerCharacteristics==null || boilerCharacteristics.isEmpty()) boilerCharacteristics = new HashSet<>();
        BoilerCharacteristics characteristics = new BoilerCharacteristics();
        characteristics.setName(name);
        characteristics.setType(type);
        characteristics.setAmount(amount);
        characteristics.setSubstanceType(typeBoiler);
        characteristics.setBoilerType(BoilerCharacteristicsEnum.getBoilerCharacteristicById(boilerEnum));


        characteristics.setDeleted(Boolean.FALSE);
        characteristics = boilerCharacteristicsService.save(characteristics);
        boilerCharacteristics.add(characteristics);
        regApplication.setBoilerCharacteristics(boilerCharacteristics);
        regApplicationService.updateBoiler(regApplication,user.getId());

        response.put("status",1);
        response.put("data",characteristics);

        return response;
    }

    @RequestMapping(value = ExpertiseUrls.RegApplicationFourCategoryBoilerCharacteristicsEditType1)
    @ResponseBody
    public Object regApplicationFourCategoryBoilerCharacteristicsEdit1(
            Model model,
            @RequestParam(name = "regId") Integer regId,
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "name_boiler1") String name,
            @RequestParam(name = "type_boiler1") String type,
            @RequestParam(name = "amount_boiler1") Double amount,
            @RequestParam(name = "boilerEnum",required = false) Integer boilerEnum

    ){
        System.out.println("regId" + regId);
        System.out.println("id" + id);
        System.out.println("name_boiler" + name);
        System.out.println("type_boiler" + type);
        System.out.println("amount_boiler" + amount);
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regId);
        if (regApplication==null ){
            return 0;
        }

        Set<BoilerCharacteristics> boilerCharacteristicsSet = regApplication.getBoilerCharacteristics();

        BoilerCharacteristics  characteristics = boilerCharacteristicsService.getById(id);
        if (!boilerCharacteristicsSet.contains(characteristics)){
            return 2;
        }
        boilerCharacteristicsSet.remove(characteristics);
        characteristics.setName(name);
        characteristics.setType(type);
        characteristics.setBoilerType(BoilerCharacteristicsEnum.getBoilerCharacteristicById(boilerEnum));

        characteristics.setAmount(amount);
        characteristics = boilerCharacteristicsService.save(characteristics);
        boilerCharacteristicsSet.add(characteristics);
        regApplication.setBoilerCharacteristics(boilerCharacteristicsSet);
        regApplicationService.updateBoiler(regApplication,user.getId());
        return 1 + "";
    }
    @RequestMapping(value = ExpertiseUrls.RegApplicationFourCategoryBoilerCharacteristicsEditType2)
    @ResponseBody
    public Object regApplicationFourCategoryBoilerCharacteristicsEdit2(
            Model model,
            @RequestParam(name = "regId") Integer regId,
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "name_boiler2") String name,
            @RequestParam(name = "type_boiler2") String type,
            @RequestParam(name = "amount_boiler2") Double amount,
            @RequestParam(name = "boilerEnum",required = false) Integer boilerEnum

    ){
        System.out.println("regId" + regId);
        System.out.println("id" + id);
        System.out.println("name_boiler" + name);
        System.out.println("type_boiler" + type);
        System.out.println("amount_boiler" + amount);
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regId);
        if (regApplication==null ){
            return 0;
        }

        Set<BoilerCharacteristics> boilerCharacteristicsSet = regApplication.getBoilerCharacteristics();

        BoilerCharacteristics  characteristics = boilerCharacteristicsService.getById(id);
        if (!boilerCharacteristicsSet.contains(characteristics)){
            return 2;
        }
        boilerCharacteristicsSet.remove(characteristics);
        characteristics.setName(name);
        characteristics.setType(type);
        characteristics.setAmount(amount);
        characteristics.setBoilerType(BoilerCharacteristicsEnum.getBoilerCharacteristicById(boilerEnum));

        characteristics = boilerCharacteristicsService.save(characteristics);
        boilerCharacteristicsSet.add(characteristics);
        regApplication.setBoilerCharacteristics(boilerCharacteristicsSet);
        regApplicationService.updateBoiler(regApplication,user.getId());
        return 1 + "";
    }
    @RequestMapping(value = ExpertiseUrls.RegApplicationFourCategoryBoilerCharacteristicsEditType3)
    @ResponseBody
    public Object regApplicationFourCategoryBoilerCharacteristicsEdit3(
            Model model,
            @RequestParam(name = "regId") Integer regId,
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "name_boiler3") String name,
            @RequestParam(name = "type_boiler3") String type,
            @RequestParam(name = "amount_boiler3") Double amount,
            @RequestParam(name = "boilerEnum",required = false) Integer boilerEnum

    ){
        System.out.println("regId" + regId);
        System.out.println("id" + id);
        System.out.println("name_boiler" + name);
        System.out.println("type_boiler" + type);
        System.out.println("amount_boiler" + amount);
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regId);
        if (regApplication==null ){
            return 0;
        }

        Set<BoilerCharacteristics> boilerCharacteristicsSet = regApplication.getBoilerCharacteristics();

        BoilerCharacteristics  characteristics = boilerCharacteristicsService.getById(id);
        if (!boilerCharacteristicsSet.contains(characteristics)){
            return 2;
        }
        boilerCharacteristicsSet.remove(characteristics);
        characteristics.setName(name);
        characteristics.setType(type);
        characteristics.setBoilerType(BoilerCharacteristicsEnum.getBoilerCharacteristicById(boilerEnum));

        characteristics.setBoilerType(BoilerCharacteristicsEnum.getBoilerCharacteristicById(boilerEnum));
        characteristics.setAmount(amount);
        characteristics = boilerCharacteristicsService.save(characteristics);
        boilerCharacteristicsSet.add(characteristics);
        regApplication.setBoilerCharacteristics(boilerCharacteristicsSet);
        regApplicationService.updateBoiler(regApplication,user.getId());
        return 1 + "";
    }

    @PostMapping(value = ExpertiseUrls.RegApplicationBoilerCharacteristicsCheck)
    @ResponseBody
    public HashMap<String,Boolean> regApplicationFourCategoryBoilerCharacteristicsGet(
            @RequestParam(name = "regId") Integer regId
    ){
        System.out.println("regApplicationFourCategoryBoilerCharacteristicsCreate");

        RegApplication regApplication = regApplicationService.getById(regId);

        HashMap<String,Boolean> response = new HashMap<>();
        response.put("status",false);

        if (regApplication==null ){
            return response;
        }
        Set<Integer> materials = regApplication.getMaterials();
        if(materials.contains(5) && !regApplication.getBoilerGroups().contains(BoilerGroupEnum.TCM)){
            response.put("status",false);
        }
        else
        {
            response.put("status",true);
        }

        if(materials.contains(6) && !regApplication.getBoilerGroups().contains(BoilerGroupEnum.OCM)){
            response.put("status",false);
        }
        else
        {
            response.put("status",true);
        }

        if(materials.contains(7) && !regApplication.getBoilerGroups().contains(BoilerGroupEnum.CCM)){
            response.put("status",false);
        }
        else
        {
            response.put("status",true);
        }
        if(materials.contains(8) && !(regApplication.getBoilerGroups().contains(BoilerGroupEnum.TCM)||regApplication.getBoilerGroups().contains(BoilerGroupEnum.OCM)||regApplication.getBoilerGroups().contains(BoilerGroupEnum.CCM))){
            response.put("status",false);
        }
        else
        {
            response.put("status",true);
        }

        return response;
    }

    @PostMapping(value = ExpertiseUrls.RegApplicationBoilerCharacteristicsConfirm)
    @ResponseBody
    public HashMap<String,Boolean> regApplicationFourCategoryBoilerCharacteristicsBoiler(
            @RequestParam(name = "regId") Integer regId,
            @RequestParam(name = "boiler_type") Integer boilerType
    ){
        System.out.println("regApplicationFourCategoryBoilerCharacteristicsCreate");
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regId);

        HashMap<String,Boolean> response = new HashMap<>();
        response.put("status",false);

        if (regApplication==null ){
            return response;
        }
        List<BoilerGroupEnum> boilerGroupEnums = new LinkedList<>();
        switch (boilerType){
            case 1:
                if(regApplication.getBoilerGroups()!=null){
                regApplication.getBoilerGroups().add(BoilerGroupEnum.TCM);
                }else{
                   boilerGroupEnums.add(BoilerGroupEnum.TCM);
                   regApplication.setBoilerGroups(boilerGroupEnums);
                }
                regApplicationService.update(regApplication);
                response.put("status",true);
                break;
            case 2:
                if(regApplication.getBoilerGroups()!=null){
                    regApplication.getBoilerGroups().add(BoilerGroupEnum.OCM);
                }else{
                    boilerGroupEnums.add(BoilerGroupEnum.OCM);
                    regApplication.setBoilerGroups(boilerGroupEnums);
                }
                regApplicationService.update(regApplication);
                response.put("status",true);
                break;
            case 3:
                if(regApplication.getBoilerGroups()!=null){
                    regApplication.getBoilerGroups().add(BoilerGroupEnum.CCM);
                }else{
                    boilerGroupEnums.add(BoilerGroupEnum.CCM);
                    regApplication.setBoilerGroups(boilerGroupEnums);
                }
                regApplicationService.update(regApplication);
                response.put("status",true);
                break;
        }
        return response;
    }

    @PostMapping(value = ExpertiseUrls.RegApplicationBoilerCharacteristicsCheckBoiler)
    @ResponseBody
    public HashMap<String,Boolean> regApplicationFourCategoryBoilerCharacteristicsGet(
            @RequestParam(name = "regId") Integer regId,
            @RequestParam(name = "boiler_type") Integer boilerType
    ){
        System.out.println("regApplicationFourCategoryBoilerCharacteristicsCreate");
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regId);

        HashMap<String,Boolean> response = new HashMap<>();
        response.put("status",false);

        if (regApplication==null ){
            return response;
        }


        switch (boilerType){
            case 1:
                if(regApplication.getBoilerGroups().contains(BoilerGroupEnum.TCM)){
                    response.put("status",true);
                }
                break;
            case 2:
                if(regApplication.getBoilerGroups().contains(BoilerGroupEnum.OCM)){
                    response.put("status",true);
                }
                break;
            case 3:
                if(regApplication.getBoilerGroups().contains(BoilerGroupEnum.CCM)){
                    response.put("status",true);
                }
                break;
        }
        return response;
    }







    @RequestMapping(value = ExpertiseUrls.RegApplicationFourCategoryBoilerCharacteristicsDelete)
    @ResponseBody
    public String regApplicationFourCategoryBoilerCharacteristicsDelete(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "regAddId") Integer regAddId
    ) {

        String status = "1";
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regAddId);
        if (regApplication == null || regApplication.getBoilerCharacteristics()==null) {
            status = "0";
            return status;
        }

        BoilerCharacteristics characteristics = boilerCharacteristicsService.getById(id);
        if (characteristics == null) {
            status = "-1";
            return status;
        }

        Set<BoilerCharacteristics> boilerCharacteristicsSet = regApplication.getBoilerCharacteristics();
        if (boilerCharacteristicsSet == null || boilerCharacteristicsSet.isEmpty() || !boilerCharacteristicsSet.contains(characteristics)) {
            status = "-2";
            return status;
        }
        boilerCharacteristicsSet.remove(characteristics);
        regApplication.setBoilerCharacteristics(boilerCharacteristicsSet);
        regApplicationService.updateBoiler(regApplication,user.getId());

        characteristics.setDeleted(Boolean.TRUE);
        boilerCharacteristicsService.save(characteristics);
        return status;
    }

    @RequestMapping(value = ExpertiseUrls.RegApplicationFourCategoryBoilerSave)
    @ResponseBody
    public HashMap<String,Object> isSaving(
            @RequestParam(name = "regCategory_id") Integer regCategoryId,
//            @RequestParam(name = "boiler_name") String boilerName,
            @RequestBody MultiValueMap<String, String> formData
    ){
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("regCategoryId=="+regCategoryId);
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();
        result.put("status",0);
        Map<String,String> map = formData.toSingleValueMap();
        System.out.println("Map="+map);
        String val = (String)map.get("typeBoiler");
        System.out.println("val="+val);
        System.out.println(map.values().toArray()[result.size()-1]);
        RegApplication regApplication = regApplicationService.getById(regCategoryId);
        if (regApplication==null
                || regApplication.getBoilerCharacteristics()==null
                || regApplication.getBoilerCharacteristics().isEmpty()){
            return result;
        }
//        regApplication.setBoilerName(boilerName);
        regApplicationService.updateBoiler(regApplication,user.getId());
        // BoilerCharacteristics hammasini id orqali mapga solindi
        HashMap<Integer,BoilerCharacteristics> boilerCharacteristicsHashMap = new HashMap<>();
        Set<BoilerCharacteristics> boilerCharacteristicsSet = regApplication.getBoilerCharacteristics();
        for (BoilerCharacteristics boilerCharacteristics: boilerCharacteristicsSet) {
            if (!boilerCharacteristicsHashMap.containsKey(boilerCharacteristics.getId())){
                boilerCharacteristicsHashMap.put(boilerCharacteristics.getId(),boilerCharacteristics);
            }
        }
        System.out.println("boilerCharacteristicsHashMap="+boilerCharacteristicsHashMap);
        for (Map.Entry<String,String> mapEntry: map.entrySet()) {
            Integer boilerId = null;

            String[] paramName =  mapEntry.getKey().split("_");
            String  tagName = paramName[0];
            if (tagName.equals("boilerAmount")){

                try {
                    boilerId = Integer.parseInt(paramName[1]);
                }catch (Exception ignored){}

                Double value = null;
                if (boilerCharacteristicsHashMap.containsKey(boilerId) && boilerId!=null){
                    String valueStr = mapEntry.getValue().replaceAll(" ","");

                    try {
                        value = Double.parseDouble(valueStr);
                    }catch (Exception e){
                        e.printStackTrace();
                        return result;
                    }

                    BoilerCharacteristics boilerCharacteristics = boilerCharacteristicsHashMap.get(boilerId);
                    boilerCharacteristics.setAmount(value);
                    System.out.println("Integer.parseInt(val)="+Integer.parseInt(val));
                    System.out.println("SubstanceType.getSubstance(Integer.parseInt(val))="+SubstanceType.getSubstance(Integer.parseInt(val)));
                    boilerCharacteristics.setSubstanceType(Integer.parseInt(val));

                    boilerCharacteristicsService.save(boilerCharacteristics);

                }

            }
        }
        result.put("status",1);

        return result;
    }

    @RequestMapping(value = ExpertiseUrls.RegApplicationFourCategoryBoilerIsSave)
    @ResponseBody
    public Boolean isSavedBoilercharacteristic(@RequestParam(name = "id") Integer id){
        return isSavingBoiler(id);
    }
    private Boolean isSavingBoiler(Integer regAddId){

        RegApplication regApplication = regApplicationService.getById(regAddId);
        if (regApplication==null || regApplication.getBoilerCharacteristics()==null || regApplication.getBoilerCharacteristics().isEmpty()){
            return Boolean.FALSE;
        }
        Set<BoilerCharacteristics> boilerCharacteristicsSet = regApplication.getBoilerCharacteristics();
        for (BoilerCharacteristics boilerCharacteristics:boilerCharacteristicsSet){
            if (boilerCharacteristics.getAmount()==null || boilerCharacteristics.getAmount()==0.0){
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }



}
