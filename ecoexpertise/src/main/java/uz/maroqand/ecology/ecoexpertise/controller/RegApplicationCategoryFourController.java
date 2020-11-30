package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.config.GlobalConfigs;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.constant.user.ToastrType;
import uz.maroqand.ecology.core.dto.expertise.*;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.sys.SmsSend;
import uz.maroqand.ecology.core.entity.user.User;
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
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.ToastrService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.ecoexpertise.constant.reg.RegTemplates;
import uz.maroqand.ecology.ecoexpertise.constant.reg.RegUrls;
import uz.maroqand.ecology.ecoexpertise.mips.MIPIndividualsPassportInfoService;

import java.util.*;

@Controller
public class RegApplicationCategoryFourController {

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
    private final RegApplicationCategoryFourAdditionalService regApplicationCategoryFourAdditionalService;
    private final BoilerCharacteristicsService boilerCharacteristicsService;
    private final PollutionMeasuresService pollutionMeasuresService;

    private final GlobalConfigs globalConfigs;

    private final String RegListRedirect = "redirect:" + RegUrls.RegApplicationList;

    @Autowired
    public RegApplicationCategoryFourController(
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
            FactureService factureService, RegApplicationCategoryFourAdditionalService regApplicationCategoryFourAdditionalService, BoilerCharacteristicsService boilerCharacteristicsService, PollutionMeasuresService pollutionMeasuresService, GlobalConfigs globalConfigs) {
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
        this.regApplicationCategoryFourAdditionalService = regApplicationCategoryFourAdditionalService;
        this.boilerCharacteristicsService = boilerCharacteristicsService;
        this.pollutionMeasuresService = pollutionMeasuresService;
        this.globalConfigs = globalConfigs;
    }

    /*
    * Start
    * */
    @RequestMapping(value = RegUrls.RegApplicationFourCategoryStart)
    public String getStartCategoryFour() {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.create(user,RegApplicationInputType.ecoService,RegApplicationCategoryType.fourType);

        return "redirect:"+ RegUrls.RegApplicationFourCategoryApplicant + "?id=" + regApplication.getId();
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryResume,method = RequestMethod.GET)
    public String getResumeMethod(
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return RegListRedirect;
            }
        }

        if (regApplication.getRegApplicationCategoryType()==null || regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.oneToTree)){
            return "redirect:" + RegUrls.RegApplicationResume + "?id=" + regApplication.getId();
        }

        switch (regApplication.getCategoryFourStep()){
            case APPLICANT: return "redirect:" + RegUrls.RegApplicationFourCategoryApplicant + "?id=" + regApplication.getId();
            case ABOUT: return "redirect:" + RegUrls.RegApplicationFourCategoryAbout + "?id=" + regApplication.getId();
            case STEP3:
            case STEP4:
            case STEP5:
            case STEP6:
            case STEP7:
            case WAITING: return "redirect:" + RegUrls.RegApplicationFourCategoryWaiting + "?id=" + regApplication.getId();
            case CONTRACT: return "redirect:" + RegUrls.RegApplicationFourCategoryContract + "?id=" + regApplication.getId();
            case PAYMENT: return "redirect:" + RegUrls.RegApplicationFourCategoryPrepayment + "?id=" + regApplication.getId();
            case STATUS: return "redirect:" + RegUrls.RegApplicationFourCategoryStatus+ "?id=" + regApplication.getId();
        }

        return RegListRedirect;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryApplicant,method = RequestMethod.GET)
    public String getApplicantCategoryFourPage(
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
        individualDto.setIndividualTin(individualDto.getIndividualTin()==null?user.getTin():individualDto.getIndividualTin());
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
        model.addAttribute("step_id", RegApplicationCategoryFourStep.APPLICANT.ordinal()+1);
        return RegTemplates.RegApplicationFourCategoryApplicant;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryApplicant,method = RequestMethod.POST)
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
        regApplication.setCategoryFourStep(RegApplicationCategoryFourStep.ABOUT);
        regApplicationService.update(regApplication);

        /*SmsSend smsSend = smsSendService.getById(regApplication.getCheckedSmsId());
        if (smsSend==null){
            toastrService.create(user.getId(), ToastrType.Error, "Telefon raqam tasdiqlanmagan.","Telefon raqam tasdiqlanmagan.");
            return  "redirect:" + RegUrls.RegApplicationApplicant + "?id=" + id;
        }
        applicant.setMobilePhone("");*/

        return "redirect:" + RegUrls.RegApplicationFourCategoryAbout + "?id=" + id;
    }

    //1 --> yuborildi
    //2 --> arizachi topilmadi
    //3 --> bu raqamga jo'natib bo'lmaydi
    @RequestMapping(value = RegUrls.RegApplicationFourCategorySendSMSCode,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> regApplicationFourCategorySendSMSCode(
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
    @RequestMapping(value = RegUrls.RegApplicationFourCategoryGetSMSCode,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> regApplicationFourCategoryGetSMSCode(
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

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryAbout,method = RequestMethod.GET)
    public String getAboutPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if (regApplication==null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                return RegListRedirect;
            }
        }
        String check = check(regApplication,user);
        if(check!=null){
            return check;
        }

        Coordinate coordinate = coordinateRepository.findByRegApplicationIdAndDeletedFalse(regApplication.getId());
        if(coordinate != null){
            model.addAttribute("coordinate", coordinate);
            model.addAttribute("coordinateLatLongList", coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId()));
        }

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditional==null){
            regApplicationCategoryFourAdditional = new RegApplicationCategoryFourAdditional();
        }

        model.addAttribute("objectExpertiseList", objectExpertiseService.getList());
        model.addAttribute("opfList", opfService.getOpfList());
        model.addAttribute("requirementList", requirementService.getByCategory(Category.Category4));
        model.addAttribute("regions", soatoService.getRegions());
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("regApplicationCategoryFourAdditional", regApplicationCategoryFourAdditional);
        model.addAttribute("back_url", RegUrls.RegApplicationFourCategoryApplicant + "?id=" + id);
        model.addAttribute("step_id", RegApplicationCategoryFourStep.ABOUT.ordinal()+1);
        return RegTemplates.RegApplicationFourCategoryAbout;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryAbout,method = RequestMethod.POST)
    public String regApplicationFourCategoryAbout(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "objectId") Integer objectId,
            @RequestParam(name = "regionId") Integer regionId,
            @RequestParam(name = "activityId", required = false) Integer activityId,
            @RequestParam(name = "materials", required = false) Set<Integer> materials,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "tin") String projectDeveloperTin,
            @RequestParam(name = "objectBlanket") String objectBlanket,
            @RequestParam(name = "coordinateDescription") String coordinateDescription,
            @RequestParam(name = "borderingObjects") String borderingObjects,
            @RequestParam(name = "territoryDescription") String territoryDescription,
            @RequestParam(name = "culturalHeritageDescription") String culturalHeritageDescription,
            @RequestParam(name = "animalCountAdditional") String animalCountAdditional,
            @RequestParam(name = "treeCountAdditional") String treeCountAdditional,
            @RequestParam(name = "waterInformation") String waterInformation,
            @RequestParam(name = "structuresInformation") String structuresInformation,
            @RequestParam(name = "aboutWindSpeed") String aboutWindSpeed,
            @RequestParam(name = "projectDeveloperName") String projectDeveloperName,
            @RequestParam(name = "opfId") Integer projectDeveloperOpfId,
            @RequestParam(name = "coordinates", required = false) List<Double> coordinates
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        String check = check(regApplication,user);
        if(check!=null){
            return check;
        }

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditional==null){
            regApplicationCategoryFourAdditional = new RegApplicationCategoryFourAdditional();
            regApplicationCategoryFourAdditional.setCreatedAt(new Date());
            regApplicationCategoryFourAdditional.setCreatedById(user.getId());
        }else{
            regApplicationCategoryFourAdditional.setUpdateAt(new Date());
            regApplicationCategoryFourAdditional.setUpdateById(user.getId());
        }
        regApplicationCategoryFourAdditional.setRegApplicationId(regApplication.getId());
        regApplicationCategoryFourAdditional.setObjectBlanket(objectBlanket);
        regApplicationCategoryFourAdditional.setCoordinateDescription(coordinateDescription);
        regApplicationCategoryFourAdditional.setBorderingObjects(borderingObjects);
        regApplicationCategoryFourAdditional.setTerritoryDescription(territoryDescription);
        regApplicationCategoryFourAdditional.setCulturalHeritageDescription(culturalHeritageDescription);
        regApplicationCategoryFourAdditional.setAnimalCountAdditional(animalCountAdditional);
        regApplicationCategoryFourAdditional.setTreeCountAdditional(treeCountAdditional);
        regApplicationCategoryFourAdditional.setWaterInformation(waterInformation);
        regApplicationCategoryFourAdditional.setStructuresInformation(structuresInformation);
        regApplicationCategoryFourAdditional.setAboutWindSpeed(aboutWindSpeed);
        regApplicationCategoryFourAdditionalService.save(regApplicationCategoryFourAdditional);


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
            return "redirect:" + RegUrls.RegApplicationAbout + "?id=" + id + "&failed=1";
        }else if(requirementList.size()==1){
            requirement = requirementList.get(0);
        }

        if(requirement==null){
            return "redirect:" + RegUrls.RegApplicationAbout + "?id=" + id + "&failed=2";
        }
        Organization organization = organizationService.getByRegionId(regionId);;
        regApplication.setRegionId(regionId);
        regApplication.setReviewId(organization!=null?organization.getId():requirement.getReviewId());
        regApplication.setRequirementId(requirement.getId());
        regApplication.setDeadline(requirement.getDeadline());

        regApplication.setObjectId(objectId);
        regApplication.setName(name);
        regApplication.setMaterials(materials);

        regApplication.setActivityId(activityId);
        regApplication.setCategory(activity!=null? activity.getCategory():null);



        regApplication.setCategoryFourStep(RegApplicationCategoryFourStep.STEP3);
        regApplicationService.update(regApplication);
        return "redirect:" + RegUrls.RegApplicationFourCategoryStep3 + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryStep3, method = RequestMethod.GET)
    public String regApplicationFourCategoryStep3(
            @RequestParam(name = "id") Integer id,
            Model model
            ){

        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        String check = check(regApplication,user);
        if(check!=null){
            return check;
        }
        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditional==null){
            return RegListRedirect;
        }

        model.addAttribute("regApplication",regApplication);
        model.addAttribute("regApplicationCategoryFourAdditional",regApplicationCategoryFourAdditional
        );
        model.addAttribute("back_url", RegUrls.RegApplicationFourCategoryAbout + "?id=" + id);
        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP3.ordinal()+1);

        return RegTemplates.RegApplicationFourCategoryStep3;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryStep3, method = RequestMethod.POST)
    public String regApplicationFourCategoryStep3Post(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "regApplicationCategoryFourAdditionalId") Integer regApplicationCategoryFourAdditionalId,
            RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional
    ){

        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id,user.getId());
        if (regApplication==null){
            return RegListRedirect;
        }

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditionalOld = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditionalOld==null || !regApplicationCategoryFourAdditionalOld.getId().equals(regApplicationCategoryFourAdditionalId)){
            return RegListRedirect + "#field=2";
        }

        regApplicationCategoryFourAdditionalService.saveStep3(regApplicationCategoryFourAdditional,regApplicationCategoryFourAdditionalOld,user.getId());
        regApplication.setCategoryFourStep(RegApplicationCategoryFourStep.STEP4);
        regApplicationService.update(regApplication);
        return "redirect:" + RegUrls.RegApplicationFourCategoryStep4 + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryStep4, method = RequestMethod.GET)
    public String regApplicationFourCategoryStep4(
            @RequestParam(name = "id") Integer id,
            Model model
    ){

        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        String check = check(regApplication,user);
        if(check!=null){
            return check;
        }

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditional==null){
            return RegListRedirect;
        }

        model.addAttribute("regApplicationCategoryFourAdditional",regApplicationCategoryFourAdditional);
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("back_url", RegUrls.RegApplicationFourCategoryStep3 + "?id=" + id);
        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP4.ordinal()+1);

        return RegTemplates.RegApplicationFourCategoryStep4;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryStep4, method = RequestMethod.POST)
    public String regApplicationFourCategoryStep4Post(
            @RequestParam(name = "id") Integer id
    ){

        return "redirect:" + RegUrls.RegApplicationFourCategoryStep5 + "?id=" + id;
    }


    @RequestMapping(value = RegUrls.RegApplicationFourCategoryStep5, method = RequestMethod.GET)
    public String regApplicationFourCategoryStep5(
            @RequestParam(name = "id") Integer id,
            Model model
    ){

        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        String check = check(regApplication,user);
        if(check!=null){
            return check;
        }

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditional==null){
            return RegListRedirect;
        }

        if (regApplicationCategoryFourAdditional.getBoilerCharacteristics()==null || regApplicationCategoryFourAdditional.getBoilerCharacteristics().isEmpty()){
            regApplicationCategoryFourAdditionalService.createBolier(regApplicationCategoryFourAdditional,user.getId());

        }

        model.addAttribute("regApplicationCategoryFourAdditional",regApplicationCategoryFourAdditional);
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("back_url", RegUrls.RegApplicationFourCategoryStep4 + "?id=" + id);
        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP5.ordinal()+1);

        return RegTemplates.RegApplicationFourCategoryStep5;
    }


    @RequestMapping(value = RegUrls.RegApplicationFourCategoryBoilerCharacteristicsCreate)
    @ResponseBody
    public HashMap<String,Object> regApplicationFourCategoryBoilerCharacteristicsCreate(
            @RequestParam(name = "regId") Integer regId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "type") String type,
            @RequestParam(name = "amount") Double amount
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
        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regId);
        if (regApplicationCategoryFourAdditional==null ){
            return response;
        }
        Set<BoilerCharacteristics> boilerCharacteristics = regApplicationCategoryFourAdditional.getBoilerCharacteristics();
        if (boilerCharacteristics==null) boilerCharacteristics = new HashSet<>();
        BoilerCharacteristics characteristics = new BoilerCharacteristics();
        characteristics.setName(name);
        characteristics.setType(type);
        characteristics.setAmount(amount);
        characteristics.setDeleted(Boolean.FALSE);
        characteristics = boilerCharacteristicsService.save(characteristics);
        boilerCharacteristics.add(characteristics);
        regApplicationCategoryFourAdditional.setBoilerCharacteristics(boilerCharacteristics);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());

        response.put("status",1);
        response.put("data",characteristics);

        return response;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryBoilerCharacteristicsEdit)
    @ResponseBody
    public Object regApplicationFourCategoryBoilerCharacteristicsEdit(
            Model model,
            @RequestParam(name = "regId") Integer regId,
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "name_boiler") String name,
            @RequestParam(name = "type_boiler") String type,
            @RequestParam(name = "amount_boiler") Double amount
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
        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regId);
        if (regApplicationCategoryFourAdditional==null ){
            return -1;
        }
        Set<BoilerCharacteristics> boilerCharacteristicsSet = regApplicationCategoryFourAdditional.getBoilerCharacteristics();

        BoilerCharacteristics  characteristics = boilerCharacteristicsService.getById(id);
        if (!boilerCharacteristicsSet.contains(characteristics)){
            return 2;
        }
        boilerCharacteristicsSet.remove(characteristics);
        characteristics.setName(name);
        characteristics.setType(type);
        characteristics.setAmount(amount);
        characteristics = boilerCharacteristicsService.save(characteristics);
        boilerCharacteristicsSet.add(characteristics);
        regApplicationCategoryFourAdditional.setBoilerCharacteristics(boilerCharacteristicsSet);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());
        return 1 + "";
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryBoilerCharacteristicsDelete)
    @ResponseBody
    public String regApplicationFourCategoryBoilerCharacteristicsDelete(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "regAddId") Integer regAddId
    ) {

        String status = "1";
        User user = userService.getCurrentUserFromContext();
       RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(regAddId);
        if (regApplicationCategoryFourAdditional == null || regApplicationCategoryFourAdditional.getBoilerCharacteristics()==null) {
            status = "0";
            return status;
        }

        BoilerCharacteristics characteristics = boilerCharacteristicsService.getById(id);
        if (characteristics == null) {
            status = "-1";
            return status;
        }

        Set<BoilerCharacteristics> boilerCharacteristicsSet = regApplicationCategoryFourAdditional.getBoilerCharacteristics();
        if (boilerCharacteristicsSet == null || boilerCharacteristicsSet.isEmpty() || !boilerCharacteristicsSet.contains(characteristics)) {
            status = "-2";
            return status;
        }
        boilerCharacteristicsSet.remove(characteristics);
        regApplicationCategoryFourAdditional.setBoilerCharacteristics(boilerCharacteristicsSet);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());

        characteristics.setDeleted(Boolean.TRUE);
        boilerCharacteristicsService.save(characteristics);
        return status;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryBoilerSave)
    @ResponseBody
    public HashMap<String,Object> isSaving(
            @RequestParam(name = "regCategory_id") Integer regCategoryId,
            @RequestParam(name = "boiler_name") String boilerName,
            @RequestBody MultiValueMap<String, String> formData
    ){
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();
        result.put("status",0);
        Map<String,String> map = formData.toSingleValueMap();
        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(regCategoryId);
        if (regApplicationCategoryFourAdditional==null
                || regApplicationCategoryFourAdditional.getBoilerCharacteristics()==null
                || regApplicationCategoryFourAdditional.getBoilerCharacteristics().isEmpty()){
            return result;
        }
        regApplicationCategoryFourAdditional.setBoilerName(boilerName);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());
        // BoilerCharacteristics hammasini id orqali mapga solindi
        HashMap<Integer,BoilerCharacteristics> boilerCharacteristicsHashMap = new HashMap<>();
        Set<BoilerCharacteristics> boilerCharacteristicsSet = regApplicationCategoryFourAdditional.getBoilerCharacteristics();
        for (BoilerCharacteristics boilerCharacteristics: boilerCharacteristicsSet) {
            if (!boilerCharacteristicsHashMap.containsKey(boilerCharacteristics.getId())){
                boilerCharacteristicsHashMap.put(boilerCharacteristics.getId(),boilerCharacteristics);
            }
        }

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
                    boilerCharacteristicsService.save(boilerCharacteristics);

                }

            }
        }
        result.put("status",1);

        return result;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryBoilerIsSave)
    @ResponseBody
    public Boolean isSavedBoilercharacteristic(@RequestParam(name = "id") Integer id){
        return isSavingBoiler(id);
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryStep5, method = RequestMethod.POST)
    public String regApplicationFourCategoryStep5Post(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "addId") Integer addId,
            RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id,user.getId());
        if (regApplication==null){
            return RegListRedirect;
        }

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditionalOld = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditionalOld==null || !regApplicationCategoryFourAdditionalOld.getId().equals(addId)){
            return RegListRedirect;
        }

        regApplicationCategoryFourAdditionalService.saveStep5(regApplicationCategoryFourAdditional,regApplicationCategoryFourAdditionalOld,user.getId());

        regApplication.setCategoryFourStep(RegApplicationCategoryFourStep.STEP6);
        regApplicationService.update(regApplication);

        return "redirect:" + RegUrls.RegApplicationFourCategoryStep6 + "?id=" + id;
    }


    @RequestMapping(value = RegUrls.RegApplicationFourCategoryStep6, method = RequestMethod.GET)
    public String regApplicationFourCategoryStep6(
            @RequestParam(name = "id") Integer id,
            Model model
    ){

        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        String check = check(regApplication,user);
        if(check!=null){
            return check;
        }
        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditional==null){
            return RegListRedirect;
        }


        model.addAttribute("regApplicationCategoryFourAdditional",regApplicationCategoryFourAdditional);
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("back_url", RegUrls.RegApplicationFourCategoryStep5 + "?id=" + id);
        model.addAttribute("next_url", RegUrls.RegApplicationFourCategoryStep7 + "?id=" + id);
        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP6.ordinal()+1);

        return RegTemplates.RegApplicationFourCategoryStep6;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryPollutionMeasuresCreate)
    @ResponseBody
    public HashMap<String,Object> regApplicationFourCategoryPollutionMeasuresCreate(
            @RequestParam(name = "regAddId") Integer regAddId,
            @RequestParam(name = "eventName") String eventName
    ){
        System.out.println("regApplicationFourCategoryBoilerCharacteristicsCreate");
        Integer status = 0;
        User user = userService.getCurrentUserFromContext();

        HashMap<String,Object> response = new HashMap<>();
        response.put("status",status);
        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(regAddId);
        if (regApplicationCategoryFourAdditional==null ){
            return response;
        }
        Set<PollutionMeasures> pollutionMeasuresSet = regApplicationCategoryFourAdditional.getPollutionMeasures();
        if (pollutionMeasuresSet==null) pollutionMeasuresSet = new HashSet<>();
        PollutionMeasures pollutionMeasures = new PollutionMeasures();
        pollutionMeasures.setEventName(eventName);
        pollutionMeasures.setDeleted(Boolean.FALSE);
        pollutionMeasures = pollutionMeasuresService.save(pollutionMeasures);
        pollutionMeasuresSet.add(pollutionMeasures);
        regApplicationCategoryFourAdditional.setPollutionMeasures(pollutionMeasuresSet);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());

        response.put("status",1);
        response.put("data",pollutionMeasures);

        return response;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryPollutionMeasuresEdit)
    @ResponseBody
    public Object regApplicationFourCategoryPollutionMeasuresEdit(
            Model model,
            @RequestParam(name = "regAddId") Integer regAddId,
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "eventName") String eventName
    ){
        System.out.println("regId" + regAddId);
        System.out.println("id" + id);
        System.out.println("eventName" + eventName);
        User user = userService.getCurrentUserFromContext();

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(regAddId);
        if (regApplicationCategoryFourAdditional==null ){
            return -1;
        }
        Set<PollutionMeasures> pollutionMeasuresSet = regApplicationCategoryFourAdditional.getPollutionMeasures();

        PollutionMeasures pollutionMeasures = pollutionMeasuresService.getById(id);
        if (!pollutionMeasuresSet.contains(pollutionMeasures)){
            return 2;
        }
        pollutionMeasuresSet.remove(pollutionMeasures);
        pollutionMeasures.setEventName(eventName);
        pollutionMeasures = pollutionMeasuresService.save(pollutionMeasures);
        pollutionMeasuresSet.add(pollutionMeasures);
        regApplicationCategoryFourAdditional.setPollutionMeasures(pollutionMeasuresSet);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());
        return 1 + "";
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryPollutionMeasuresDelete)
    @ResponseBody
    public String regApplicationFourCategoryPollutionMeasuresDelete(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "regAddId") Integer regAddId
    ) {

        String status = "1";
        User user = userService.getCurrentUserFromContext();
        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(regAddId);
        if (regApplicationCategoryFourAdditional == null || regApplicationCategoryFourAdditional.getBoilerCharacteristics()==null) {
            status = "0";
            return status;
        }

        PollutionMeasures pollutionMeasures = pollutionMeasuresService.getById(id);
        if (pollutionMeasures == null) {
            status = "-1";
            return status;
        }

        Set<PollutionMeasures> pollutionMeasuresSet = regApplicationCategoryFourAdditional.getPollutionMeasures();
        if (pollutionMeasuresSet == null || pollutionMeasuresSet.isEmpty() || !pollutionMeasuresSet.contains(pollutionMeasures)) {
            status = "-2";
            return status;
        }
        pollutionMeasuresSet.remove(pollutionMeasures);
        regApplicationCategoryFourAdditional.setPollutionMeasures(pollutionMeasuresSet);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());

        pollutionMeasures.setDeleted(Boolean.TRUE);
        pollutionMeasuresService.save(pollutionMeasures);
        return status;
    }



  /*  @RequestMapping(value = RegUrls.RegApplicationFourCategoryStep6, method = RequestMethod.POST)
    public String regApplicationFourCategoryStep6Post(
            @RequestParam(name = "id") Integer id
    ){

        return "redirect:" + RegUrls.RegApplicationFourCategoryStep7 + "?id=" + id;
    }*/


    @RequestMapping(value = RegUrls.RegApplicationFourCategoryStep7, method = RequestMethod.GET)
    public String regApplicationFourCategoryStep7(
            @RequestParam(name = "id") Integer id,
            Model model
    ){


        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        String check = check(regApplication,user);
        if(check!=null){
            return check;
        }

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditional==null){
            return RegListRedirect;
        }

        model.addAttribute("regApplicationCategoryFourAdditional",regApplicationCategoryFourAdditional);
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("back_url", RegUrls.RegApplicationFourCategoryStep6 + "?id=" + id);
        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP7.ordinal()+1);
        return RegTemplates.RegApplicationFourCategoryStep7;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryStep7, method = RequestMethod.POST)
    public String regApplicationFourCategoryStep7Post(
            @RequestParam(name = "regAddId") Integer regAddId,
            @RequestParam(name = "id") Integer id,
            RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional
    ){

        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id,user.getId());
        if (regApplication==null){
            return RegListRedirect;
        }

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditionalOld = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditionalOld==null || !regApplicationCategoryFourAdditionalOld.getId().equals(regAddId)){
            return RegListRedirect;
        }

        regApplicationCategoryFourAdditionalService.saveStep7(regApplicationCategoryFourAdditional,regApplicationCategoryFourAdditionalOld,user.getId());

        /*RegApplicationLog confirmLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
        if(confirmLog==null || !confirmLog.getStatus().equals(LogStatus.Approved)){
            RegApplicationLog regApplicationLog = regApplicationLogService.create(regApplication,LogType.Confirm,"",user);
            regApplication.setConfirmLogAt(new Date());
            regApplication.setConfirmLogId(regApplicationLog.getId());
            regApplication.setStatus(RegApplicationStatus.CheckSent);
        }
        regApplication.setCategoryFourStep(RegApplicationCategoryFourStep.WAITING);
        regApplicationService.update(regApplication);*/
        return "redirect:" + RegUrls.RegApplicationFourCategoryStep7 + "?id=" + id;
//        return "redirect:" + RegUrls.RegApplicationFourCategoryWaiting + "?id=" + id;
    }


    @RequestMapping(value = RegUrls.RegApplicationFourCategoryWaiting,method = RequestMethod.GET)
    public String regApplicationFourCategoryWaiting(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "field",required = false) Integer field,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null) {
                return RegListRedirect;
            }
        }
        RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
        if (regApplicationLog==null){
            return "redirect:" + RegUrls.RegApplicationFourCategoryStep7 + "?id=" + id;
        }
        if (regApplication.getForwardingLogId() != null){
            return "redirect:" + RegUrls.RegApplicationFourCategoryContract + "?id=" + id;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("field", field);
        model.addAttribute("regApplicationLog", regApplicationLog);
        model.addAttribute("back_url", RegUrls.RegApplicationFourCategoryStep7 + "?id=" + id);
        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP7.ordinal()+1);
        return RegTemplates.RegApplicationFourCategoryWaiting;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryWaiting,method = RequestMethod.POST)
    public String regApplicationFourCategoryWaiting(
            @RequestParam(name = "id") Integer id
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null) {
                return RegListRedirect;
            }
        }
        if (regApplication.getConfirmLogId()!=null){
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
            if(regApplicationLog.getStatus()!=LogStatus.Approved){
                toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Arizachi ma'lumotlari tasdiqlanmagan.");
                return "redirect:" + RegUrls.RegApplicationFourCategoryWaiting + "?id=" + id;
            }
        }

        return "redirect:" + RegUrls.RegApplicationFourCategoryContract + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryContract,method = RequestMethod.GET)
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
                return RegListRedirect;
            }
        }

        if (regApplication.getConfirmLogId()!=null){
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
            if(regApplicationLog.getStatus()!=LogStatus.Approved){
                toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Kiritilgan ma'lumotlar tasdiqlanishi kutilyabdi.");
                return "redirect:" + RegUrls.RegApplicationFourCategoryWaiting + "?id=" + id;
            }
        }
        if (regApplication.getConfirmLogId()==null){
            toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Kiritilgan ma'lumotlar tasdiqlanishi kerak.");
            return "redirect:" + RegUrls.RegApplicationFourCategoryWaiting + "?id=" + id;
        }

        if (regApplication.getPerformerLogId()!=null){
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
            if(regApplicationLog.getStatus() != LogStatus.Modification){
                return "redirect:" + RegUrls.RegApplicationFourCategoryStatus + "?id=" + id;
            }
        }

        Offer offer;
        if(regApplication.getOfferId()!=null){
            //offerta tasdiqlangan
            offer = offerService.getById(regApplication.getOfferId());
            model.addAttribute("action_url", RegUrls.RegApplicationFourCategoryContract);
        }else {
            //offerta tasdiqlanmagan
            RegApplication regApplicationCheck = regApplicationService.getByIdAndUserTin(regApplication.getId(),user);
            if (regApplicationCheck==null || !regApplicationCheck.getId().equals(regApplication.getId())){
                return "redirect:" + RegUrls.RegApplicationFourCategoryWaiting + "?id=" + regApplication.getId() +  "&field=" + -1;
            }
            offer = offerService.getOffer(regApplication.getBudget(),regApplication.getReviewId());
            model.addAttribute("action_url", RegUrls.RegApplicationFourCategoryContractConfirm);
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("offer", offer);
        model.addAttribute("step_id", RegApplicationStep.CONTRACT.ordinal());
        return RegTemplates.RegApplicationContract;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryContractConfirm,method = RequestMethod.POST)
    public String regApplicationContractConfirm(
            @RequestParam(name = "id") Integer id
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return RegListRedirect;
            }
        }
        if(regApplication.getOfferId() != null){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Oferta tasdiqlangan.");
            return "redirect:" + RegUrls.RegApplicationFourCategoryContract + "?id=" + id;
        }

        Offer offer = offerService.getOffer(regApplication.getBudget(),regApplication.getReviewId());
        regApplication.setOfferId(offer.getId());
        notificationService.confirmContractRegApplication(regApplication.getId());
        String contractNumber = organizationService.getContractNumber(regApplication.getReviewId());
        regApplication.setContractNumber(contractNumber);
        regApplication.setContractDate(new Date());
        regApplication.setStep(RegApplicationStep.CONTRACT);
        regApplicationService.update(regApplication);

        return "redirect:" + RegUrls.RegApplicationFourCategoryContract + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryContract,method = RequestMethod.POST)
    public String regApplicationContract(
            @RequestParam(name = "id") Integer id
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if(regApplication == null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return RegListRedirect;
            }
        }
        if(regApplication.getOfferId()==null){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Oferta tasdiqlanmagan.");
            return "redirect:" + RegUrls.RegApplicationFourCategoryContract + "?id=" + id;
        }

        regApplication.setStep(RegApplicationStep.PAYMENT);
        regApplicationService.update(regApplication);

        return "redirect:" + RegUrls.RegApplicationFourCategoryPrepayment + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryPrepayment)
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
                return RegListRedirect;
            }
        }

        if(regApplication.getOfferId() == null){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Oferta tasdiqlanmagan.");
            return "redirect:" + RegUrls.RegApplicationFourCategoryContract + "?id=" + id;
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
                return "redirect:" + RegUrls.RegApplicationFourCategoryStatus + "?id=" + id;
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


    @RequestMapping(value = RegUrls.RegApplicationFourCategoryPaymentSendSms)
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
        String failUrl = RegUrls.RegApplicationFourCategoryPaymentSendSms;
        String successUrl = RegUrls.RegApplicationFourCategoryPaymentConfirmSms;
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

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryPaymentConfirmSms)
    @ResponseBody
    public Map<String, Object> confirmSmsPayment(
            @RequestParam(name = "id") Integer applicationId,
            @RequestParam(name = "trId") Integer trId,
            @RequestParam(name = "paymentId") Integer paymentId,
            @RequestParam(name = "confirmSms") String confirmSms
    ) {
        String successUrl = RegUrls.RegApplicationFourCategoryStatus+ "?id=" + applicationId;
        String failUrl = RegUrls.RegApplicationFourCategoryPaymentConfirmSms;

        return paymentService.confirmSmsAndGetResponseAsMap(
                applicationId,
                paymentId,
                trId,
                confirmSms,
                successUrl,
                failUrl
        );
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryPaymentFree)
    public String getPaymentFreeMethod(
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if (regApplication == null) {
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return RegListRedirect;
            }
        }
        if (regApplication.getInvoiceId() == null){
            return "redirect:" + RegUrls.RegApplicationFourCategoryPrepayment + "?id=" + id;
        }

        Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
        if (invoice == null){
            return "redirect:" + RegUrls.RegApplicationFourCategoryPrepayment + "?id=" + id;
        }

        if(invoice.getStatus().equals(InvoiceStatus.Success) || invoice.getStatus().equals(InvoiceStatus.PartialSuccess)){
            return "redirect:" + RegUrls.RegApplicationFourCategoryStatus + "?id=" + id;
        }

        //todo invoice amount 0 bo'lishi kerak
        invoiceService.payTest(invoice.getId());
        /*if(invoice.getAmount().equals(0.0)){
            invoiceService.payTest(invoice.getId());
        }*/
        return "redirect:" + RegUrls.RegApplicationFourCategoryStatus + "?id=" + id;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryStatus)
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
                return RegListRedirect;
            }
        }

        Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
        if(invoice == null){
            System.out.println("invoice == null");
            return "redirect:" + RegUrls.RegApplicationFourCategoryPrepayment + "?id=" + id;
        }
        if (invoice.getStatus()!=InvoiceStatus.Success && invoice.getStatus()!=InvoiceStatus.PartialSuccess){
            return "redirect:" + RegUrls.RegApplicationFourCategoryPrepayment + "?id=" + id;
        }

        Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());
        model.addAttribute("conclusion", conclusion);
        if(conclusion != null){
            model.addAttribute("documentRepo", documentRepoService.getDocument(conclusion.getDocumentRepoId()));
        }

        model.addAttribute("performerLog", regApplicationLogService.getById(regApplication.getPerformerLogId()));
        model.addAttribute("commentList", commentService.getByRegApplicationIdAndType(regApplication.getId(), CommentType.CHAT));

        model.addAttribute("invoice", invoice);
        model.addAttribute("facture", factureService.getById(regApplication.getFactureId()));
        model.addAttribute("factureProductList", factureService.getByFactureId(regApplication.getFactureId()));
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("back_url", RegUrls.RegApplicationList);
        model.addAttribute("step_id", RegApplicationStep.STATUS.ordinal());
        return RegTemplates.RegApplicationFourCategoryStatus;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryResend)
    public String getResendMethod(
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
        if (regApplication==null){
            regApplication = regApplicationService.getByIdAndUserTin(id,user);
            if (regApplication==null){
                toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
                return RegListRedirect;
            }
        }

        if(!regApplication.getStatus().equals(RegApplicationStatus.Modification)){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","");
            return "redirect:" + RegUrls.RegApplicationFourCategoryStatus + "?id=" + regApplication.getId();
        }

        regApplication.setLogIndex(regApplication.getLogIndex()+1);
        RegApplicationLog forwardingLog = regApplicationLogService.create(regApplication,LogType.Forwarding,"",user);
        forwardingLog = regApplicationLogService.update(forwardingLog, LogStatus.Resend,"", user.getId());

        regApplication.setForwardingLogId(forwardingLog.getId());
        regApplication.setPerformerId(null);
        regApplication.setPerformerLogId(null);
        regApplication.setPerformerLogIdNext(null);
        regApplication.setAgreementLogs(null);
        regApplication.setAgreementCompleteLogId(null);
        regApplication.setConclusionId(null);

        regApplication.setStatus(RegApplicationStatus.Process);
        regApplicationService.update(regApplication);

        return "redirect:" + RegUrls.RegApplicationFourCategoryStatus + "?id=" + regApplication.getId();
    }




    @RequestMapping(value = RegUrls.RegApplicationFourCategoryGetActivity, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getActivity(
            @RequestParam(name = "objectId") Integer objectId
    ) {
        HashMap<String, Object> result = new HashMap<>();
        if(objectId == null){
            return result;
        }

        List<Category> categoryList = new LinkedList<>();
        categoryList.add(Category.Category4);
        List<Requirement> requirementList = requirementService.getByCategory(Category.Category4);
        /*for(Requirement requirement: requirementList){
            categoryList.add(requirement.getCategory());
        }*/
        List<Activity> activityList = activityService.getByInCategory(categoryList);

        result.put("activityList", activityList);
        result.put("activityListSize", activityList.size());
        result.put("requirementList", requirementList);
        result.put("requirementListSize", requirementList.size());
        result.put("categoryList", categoryList);
        return result;
    }

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryGetMaterials, method = RequestMethod.POST)
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

    @RequestMapping(value = RegUrls.RegApplicationFourCategoryGetMaterial, method = RequestMethod.POST)
    @ResponseBody
    public List<Material> getMaterial(
            @RequestParam(name = "materialId") Integer materialId
    ) {
        return materialService.getList();
    }

    private String check(RegApplication regApplication, User user){
        System.out.println("check method");
        if(regApplication == null || (regApplication.getRegApplicationCategoryType()==null || regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.oneToTree))){
            System.out.println("if=1");
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza boshqa foydalanuvchiga tegishli.");
            return RegListRedirect;
        }
        Boolean modification = true;
        if (regApplication.getPerformerLogId()!=null){
            System.out.println("if=2");
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
            if(regApplicationLog.getStatus() != LogStatus.Modification){
                return "redirect:" + RegUrls.RegApplicationStatus + "?id=" + regApplication.getId();
            }else {
                modification = false;
            }
        }
        if (regApplication.getConfirmLogId()!=null && modification){
            System.out.println("if=3");
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
            if(regApplicationLog.getStatus() != LogStatus.Denied){
                toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Arizachi ma'lumotlarini o'zgartirishga Ruxsat yo'q.");
                return "redirect:" + RegUrls.RegApplicationWaiting + "?id=" + regApplication.getId();
            }
        }
        return null;
    }

    //RegApplicationCategoryFourAdditionalId
    private Boolean isSavingBoiler(Integer regAddId){

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(regAddId);
        if (regApplicationCategoryFourAdditional==null || regApplicationCategoryFourAdditional.getBoilerCharacteristics()==null || regApplicationCategoryFourAdditional.getBoilerCharacteristics().isEmpty()){
            return Boolean.FALSE;
        }
        Set<BoilerCharacteristics> boilerCharacteristicsSet = regApplicationCategoryFourAdditional.getBoilerCharacteristics();
        for (BoilerCharacteristics boilerCharacteristics:boilerCharacteristicsSet){
            if (boilerCharacteristics.getAmount()==null || boilerCharacteristics.getAmount()==0.0){
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
