package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
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
import uz.maroqand.ecology.core.constant.user.ToastrType;
import uz.maroqand.ecology.core.dto.expertise.ForeignIndividualDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualEntrepreneurDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.sys.File;
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
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

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
    private final AirPoolService airPoolService;
    private final HarmfulSubstancesAmountService harmfulSubstancesAmountService;
    private final DescriptionOfSourcesService descriptionOfSourcesService;
    private final DescriptionOfSourcesAdditionalService descriptionOfSourcesAdditionalService;
    private final SubstanceService substanceService;
    private final GlobalConfigs globalConfigs;

    private final String RegListRedirect = "redirect:" + ExpertiseUrls.ExpertiseRegApplicationList;

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
            ToastrService toastrService,
            CoordinateRepository coordinateRepository,
            CoordinateLatLongRepository coordinateLatLongRepository,
            SmsSendService smsSendService,
            ConclusionService conclusionService,
            DocumentRepoService documentRepoService,
            NotificationService notificationService,
            FactureService factureService, RegApplicationCategoryFourAdditionalService regApplicationCategoryFourAdditionalService, BoilerCharacteristicsService boilerCharacteristicsService, PollutionMeasuresService pollutionMeasuresService, AirPoolService airPoolService, HarmfulSubstancesAmountService harmfulSubstancesAmountService, DescriptionOfSourcesService descriptionOfSourcesService, DescriptionOfSourcesAdditionalService descriptionOfSourcesAdditionalService, SubstanceService substanceService, GlobalConfigs globalConfigs) {
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
        this.airPoolService = airPoolService;
        this.harmfulSubstancesAmountService = harmfulSubstancesAmountService;
        this.descriptionOfSourcesService = descriptionOfSourcesService;
        this.descriptionOfSourcesAdditionalService = descriptionOfSourcesAdditionalService;
        this.substanceService = substanceService;
        this.globalConfigs = globalConfigs;
    }

    /*
    * Start
    * */
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStart)
    public String getStartCategoryFour() {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.create(user,RegApplicationInputType.ecoService,RegApplicationCategoryType.fourType);

        return "redirect:"+ ExpertiseUrls.ExpertiseRegApplicationFourCategoryApplicant + "?id=" + regApplication.getId();
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryResume,method = RequestMethod.GET)
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
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryResume + "?id=" + regApplication.getId();
        }

        switch (regApplication.getCategoryFourStep()){
            case APPLICANT: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryApplicant + "?id=" + regApplication.getId();
            case     ABOUT: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryAbout + "?id=" + regApplication.getId();
            case     STEP3: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep3 + "?id=" + regApplication.getId();
//            case     STEP4: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4 + "?id=" + regApplication.getId();
//            case     STEP5: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep5 + "?id=" + regApplication.getId();
//            case     STEP6: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep6 + "?id=" + regApplication.getId();
//            case     STEP7: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep7 + "?id=" + regApplication.getId();
//            case   WAITING: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryWaiting + "?id=" + regApplication.getId();
            case  CONTRACT: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryContract + "?id=" + regApplication.getId();
            case   PAYMENT: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryPrepayment + "?id=" + regApplication.getId();
            case    STATUS: return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStatus+ "?id=" + regApplication.getId();
        }

        return RegListRedirect;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryApplicant,method = RequestMethod.GET)
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
        return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryApplicant;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryApplicant,method = RequestMethod.POST)
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
            return  "redirect:" + ExpertiseUrls.RegApplicationApplicant + "?id=" + id;
        }
        applicant.setMobilePhone("");*/

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryAbout + "?id=" + id;
    }

    //1 --> yuborildi
    //2 --> arizachi topilmadi
    //3 --> bu raqamga jo'natib bo'lmaydi
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategorySendSMSCode,method = RequestMethod.POST)
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
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryGetSMSCode,method = RequestMethod.POST)
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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryAbout,method = RequestMethod.GET)
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

        Coordinate coordinate = coordinateRepository.findTop1ByRegApplicationIdAndDeletedFalseOrderByIdDesc(regApplication.getId());
        if(coordinate != null){
            model.addAttribute("coordinate", coordinate);
            model.addAttribute("coordinateLatLongList", coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId()));
        }

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditional==null){
            regApplicationCategoryFourAdditional = new RegApplicationCategoryFourAdditional();
        }
        model.addAttribute("substances1",substanceService.getListByType(SubstanceType.SUBSTANCE_TYPE1));
        model.addAttribute("substances2",substanceService.getListByType(SubstanceType.SUBSTANCE_TYPE2));
        model.addAttribute("substances3",substanceService.getListByType(SubstanceType.SUBSTANCE_TYPE3));
        model.addAttribute("substances",substanceService.getList());
        model.addAttribute("objectExpertiseList", objectExpertiseService.getList());
        model.addAttribute("opfList", opfService.getOpfList());
        model.addAttribute("requirementList", requirementService.getByCategory(Category.Category4));
        model.addAttribute("regions", soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("regApplicationCategoryFourAdditional", regApplicationCategoryFourAdditional);
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryApplicant + "?id=" + id);
        model.addAttribute("step_id", RegApplicationCategoryFourStep.ABOUT.ordinal()+1);
        return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryAbout;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryAbout,method = RequestMethod.POST)
    public String regApplicationFourCategoryAbout(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "objectId") Integer objectId,
            @RequestParam(name = "regionId") Integer regionId,
            @RequestParam(name = "activityId", required = false) Integer activityId,
            @RequestParam(name = "materials", required = false) Set<Integer> materials,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "tin") String projectDeveloperTin,
            @RequestParam(name = "objectBlanket") String objectBlanket,
//            @RequestParam(name = "coordinateDescription") String coordinateDescription,
            @RequestParam(name = "borderingObjects") String borderingObjects,
            @RequestParam(name = "territoryDescription") String territoryDescription,
            @RequestParam(name = "culturalHeritageDescription") String culturalHeritageDescription,
            @RequestParam(name = "animalCountAdditional") String animalCountAdditional,
            @RequestParam(name = "treeCountAdditional") String treeCountAdditional,
            @RequestParam(name = "waterInformation",required = false) String waterInformation,
            @RequestParam(name = "structuresInformation") String structuresInformation,
            @RequestParam(name = "aboutWindSpeed") String aboutWindSpeed,
            @RequestParam(name = "projectDeveloperName") String projectDeveloperName,
            @RequestParam(name = "opfId") Integer projectDeveloperOpfId,
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
//        regApplicationCategoryFourAdditional.setCoordinateDescription(coordinateDescription);
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
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationAbout + "?id=" + id + "&failed=1";
        }else if(requirementList.size()==1){
            requirement = requirementList.get(0);
        }

        if(requirement==null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationAbout + "?id=" + id + "&failed=2";
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
        regApplication.setObjectRegionId(objectRegionId);
        regApplication.setObjectSubRegionId(objectSubRegionId);
        regApplication.setIndividualPhone(individualPhone);


        regApplication.setCategoryFourStep(RegApplicationCategoryFourStep.STEP3);
        regApplicationService.update(regApplication);
        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep3 + "?id=" + id;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep3, method = RequestMethod.GET)
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
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryAbout + "?id=" + id);
        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP3.ordinal()+1);

        return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryStep3;
    }

    //fileUpload
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep3FileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> expertiseRegApplicationFourCategoryStep3FileUpload(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "file_name") String fileNname,
            @RequestParam(name = "file") MultipartFile multipartFile
    ) {
        User user = userService.getCurrentUserFromContext();

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(id);
        if (regApplicationCategoryFourAdditional == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }

        File file = fileService.uploadFile(multipartFile, user.getId(),"regApplicationCategoryFourAdditional="+regApplicationCategoryFourAdditional.getId(),fileNname);
        if (file != null) {
            Set<File> fileSet = regApplicationCategoryFourAdditional.getPlanFiles();
            if (regApplicationCategoryFourAdditional.getPlanFiles()==null) fileSet = new HashSet<>();
            fileSet.add(file);
            regApplicationCategoryFourAdditional.setPlanFiles(fileSet);
            regApplicationCategoryFourAdditionalService.save(regApplicationCategoryFourAdditional);

            responseMap.put("name", file.getName());
            responseMap.put("link", ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep3FileDownload+ "?file_id=" + file.getId() + "&id=" + regApplicationCategoryFourAdditional.getId());
            responseMap.put("fileId", file.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    //fileDownload
    @RequestMapping(ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep3FileDownload)
    public ResponseEntity<Resource> expertiseRegApplicationFourCategoryStep3FileDownload(
            @RequestParam(name = "file_id") Integer fileId,
            @RequestParam(name = "id") Integer id
    ){
        File file = fileService.findById(fileId);
        if (file == null) {
            return null;
        } else {
            RegApplicationCategoryFourAdditional applicationCategoryFourAdditionalirPool = regApplicationCategoryFourAdditionalService.getById(id);
            if (applicationCategoryFourAdditionalirPool==null || applicationCategoryFourAdditionalirPool.getPlanFiles()==null
                    || applicationCategoryFourAdditionalirPool.getPlanFiles().isEmpty() || !applicationCategoryFourAdditionalirPool.getPlanFiles().contains(file)){
                return null;
            }
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    //fileDelete
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep3FileDelete, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> expertiseRegApplicationFourCategoryStep3FileDelete(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "fileId") Integer fileId
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(id);

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (regApplicationCategoryFourAdditional == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }
        File file = fileService.findByIdAndUploadUserId(fileId, user.getId());

        if (file != null) {
            Set<File> fileSet = regApplicationCategoryFourAdditional.getPlanFiles();
            if(fileSet.contains(file)) {
                fileSet.remove(file);
                regApplicationCategoryFourAdditionalService.save(regApplicationCategoryFourAdditional);

                file.setDeleted(true);
                file.setDateDeleted(new Date());
                file.setDeletedById(user.getId());
                fileService.save(file);
                responseMap.put("status", 1);
            }
        }
        return responseMap;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep3, method = RequestMethod.POST)
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


        regApplicationCategoryFourAdditionalOld = regApplicationCategoryFourAdditionalService.saveStep3(regApplicationCategoryFourAdditional,regApplicationCategoryFourAdditionalOld,user.getId());
        if (regApplicationCategoryFourAdditionalOld.getPlanFiles()==null
                || regApplicationCategoryFourAdditionalOld.getPlanFiles().isEmpty()
                || regApplicationCategoryFourAdditionalOld.getPlanFiles().size()==0
        ){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep3 + "?id=" + id + "&field=1";
        }else{

            RegApplicationLog confirmLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
            if(confirmLog==null || !confirmLog.getStatus().equals(LogStatus.Approved)){
                RegApplicationLog regApplicationLog = regApplicationLogService.create(regApplication,LogType.Confirm,"",user);
                regApplication.setConfirmLogAt(new Date());
                regApplication.setConfirmLogId(regApplicationLog.getId());
                regApplication.setStatus(RegApplicationStatus.CheckSent);
            }
            regApplication.setCategoryFourStep(RegApplicationCategoryFourStep.CONTRACT);
            regApplicationService.update(regApplication);
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryContract + "?id=" + id;
        }
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4, method = RequestMethod.GET)
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
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep3 + "?id=" + id);
        model.addAttribute("next_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4Submit + "?id=" + id);
//        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP4.ordinal()+1);

        return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryStep4;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryAirPoolCreate)
    @ResponseBody
    public HashMap<String,Object> regApplicationFourCategoryAirPoolCreate(
            @RequestParam(name = "regAddId") Integer regAddId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "jobName") String jobName,
            @RequestParam(name = "fuelType") String fuelType,
            @RequestParam(name = "numberOfSources") Integer numberOfSources,
            @RequestParam(name = "substances") String substances,
            @RequestParam(name = "airSubstance") String airSubstance
    ){
        System.out.println("regApplicationFourCategoryAirPoolCreate");
        Integer status = 0;
        User user = userService.getCurrentUserFromContext();

        HashMap<String,Object> response = new HashMap<>();
        response.put("status",status);
        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(regAddId);
        if (regApplicationCategoryFourAdditional==null ){
            return response;
        }
        Set<AirPool> airPoolSet = regApplicationCategoryFourAdditional.getAirPools();
        if (airPoolSet==null) airPoolSet = new HashSet<>();
        AirPool airPool = new AirPool();
        airPool.setName(name);
        airPool.setJobName(jobName);
        airPool.setFuelType(fuelType);
        airPool.setNumberOfSources(numberOfSources);
        airPool.setSubstances(substances);
        airPool.setAirSubstance(airSubstance);
        airPool.setDeleted(Boolean.FALSE);

        if (numberOfSources>0){
            Set<DescriptionOfSources> descriptionOfSourcesSet = airPool.getDescriptionOfSources();
            if (descriptionOfSourcesSet==null) descriptionOfSourcesSet = new HashSet<>();
            for (Integer id=1; id<=numberOfSources; id++) {
                DescriptionOfSources descriptionOfSources = new DescriptionOfSources();
                descriptionOfSources.setDeleted(Boolean.FALSE);
                descriptionOfSourcesService.save(descriptionOfSources);
                descriptionOfSourcesSet.add(descriptionOfSources);
            }
            airPool.setDescriptionOfSources(descriptionOfSourcesSet);
        }

        airPool = airPoolService.save(airPool);
        airPoolSet.add(airPool);
        regApplicationCategoryFourAdditional.setAirPools(airPoolSet);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());
        response.put("status",1);
        response.put("data",airPool);

        return response;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryAirPoolEdit)
    @ResponseBody
    public Object regApplicationFourCategoryAirPoolEdit(
            Model model,
            @RequestParam(name = "regAddId") Integer regAddId,
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "jobName") String jobName,
            @RequestParam(name = "fuelType") String fuelType,
            @RequestParam(name = "numberOfSources") Integer numberOfSources,
            @RequestParam(name = "substances") String substances,
            @RequestParam(name = "airSubstance") String airSubstance
    ){
        User user = userService.getCurrentUserFromContext();

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(regAddId);
        if (regApplicationCategoryFourAdditional==null ){
            return -1;
        }
        Set<AirPool> airPoolSet = regApplicationCategoryFourAdditional.getAirPools();

        AirPool airPool = airPoolService.getById(id);
        if (!airPoolSet.contains(airPool)){
            return 2;
        }
        airPoolSet.remove(airPool);
        airPool.setName(name);
        airPool.setJobName(jobName);
        airPool.setFuelType(fuelType);
        if (!airPool.getNumberOfSources().equals(numberOfSources)){
            airPoolService.removeAllDescriptionNotSaved(airPool);
            if (numberOfSources>0){
                Set<DescriptionOfSources> descriptionOfSourcesSet = airPool.getDescriptionOfSources();
                if (descriptionOfSourcesSet==null) descriptionOfSourcesSet = new HashSet<>();
                for (Integer idSource=1; idSource<=numberOfSources; idSource++) {
                    DescriptionOfSources descriptionOfSources = new DescriptionOfSources();
                    descriptionOfSources.setDeleted(Boolean.FALSE);
                    descriptionOfSourcesService.save(descriptionOfSources);
                    descriptionOfSourcesSet.add(descriptionOfSources);
                }
                airPool.setDescriptionOfSources(descriptionOfSourcesSet);
            }
        }
        airPool.setNumberOfSources(numberOfSources);
        airPool.setSubstances(substances);
        airPool.setAirSubstance(airSubstance);
        airPool = airPoolService.save(airPool);
        airPoolSet.add(airPool);
        regApplicationCategoryFourAdditional.setAirPools(airPoolSet);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());
        return 1 + "";
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryAirPoolDelete)
    @ResponseBody
    public String regApplicationFourCategoryAirPoolDelete(
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

        AirPool airPool = airPoolService.getById(id);
        if (airPool == null) {
            status = "-1";
            return status;
        }

        Set<AirPool> airPoolSet = regApplicationCategoryFourAdditional.getAirPools();
        if (airPoolSet == null || airPoolSet.isEmpty() || !airPoolSet.contains(airPool)) {
            status = "-2";
            return status;
        }
        airPoolSet.remove(airPool);
        regApplicationCategoryFourAdditional.setAirPools(airPoolSet);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());

        airPool.setDeleted(Boolean.TRUE);
        airPoolService.removeAllDescriptionNotSaved(airPool);
        airPoolService.save(airPool);
        return status;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4Submit)
    public String regApplicationFourCategoryStep4Submit(
            @RequestParam(name = "id") Integer id
    ){
        User user = userService.getCurrentUserFromContext();

        RegApplication regApplication = regApplicationService.getById(id,user.getId());
        if (regApplication==null){
            return RegListRedirect;
        }

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditional==null){
            return RegListRedirect;
        }

        if (regApplicationCategoryFourAdditional.getAirPools()==null || regApplicationCategoryFourAdditional.getAirPools().isEmpty()){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep5 + "?id=" + id;
        }

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4_2 + "?id=" + id;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4_2, method = RequestMethod.GET)
    public String regApplicationFourCategoryStep4_2Get(
            @RequestParam(name = "id") Integer id,
            Model model
    ){

        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id,user.getId());
        if (regApplication==null){
            return RegListRedirect;
        }

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditional==null){
            return RegListRedirect;
        }

        model.addAttribute("regApplicationCategoryFourAdditional",regApplicationCategoryFourAdditional);
        model.addAttribute("air_pools",regApplicationCategoryFourAdditional.getAirPools());
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4 + "?id=" + id);
//        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP4.ordinal()+1);

        return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryStep4_2;

    }

    //fileUpload
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4_2FileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "file_name") String fileNname,
            @RequestParam(name = "file") MultipartFile multipartFile
    ) {
        User user = userService.getCurrentUserFromContext();

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        AirPool airPool = airPoolService.getById(id);
        if (airPoolService == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }

        File file = fileService.uploadFile(multipartFile, user.getId(),"airPool="+airPool.getId(),fileNname);
        if (file != null) {
            Set<File> fileSet = airPool.getFiles();
            fileSet.add(file);
            airPool.setFiles(fileSet);
            airPoolService.save(airPool);

            responseMap.put("name", file.getName());
            responseMap.put("link", ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4_2FileDownload+ "?file_id=" + file.getId() + "&id=" + airPool.getId());
            responseMap.put("fileId", file.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    //fileDownload
    @RequestMapping(ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4_2FileDownload)
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "file_id") Integer fileId,
            @RequestParam(name = "id") Integer id
    ){
        File file = fileService.findById(fileId);
        if (file == null) {
            return null;
        } else {
            AirPool airPool = airPoolService.getById(id);
            if (airPool==null || airPool.getFiles()==null
                    || airPool.getFiles().isEmpty() || !airPool.getFiles().contains(file)){
                return null;
            }
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    //fileDelete
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4_2FileDelete, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> deleteFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "fileId") Integer fileId
    ) {
        User user = userService.getCurrentUserFromContext();
        AirPool airPool = airPoolService.getById(id);

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (airPool == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }
        File file = fileService.findByIdAndUploadUserId(fileId, user.getId());

        if (file != null) {
            Set<File> fileSet = airPool.getFiles();
            if(fileSet.contains(file)) {
                fileSet.remove(file);
                airPoolService.save(airPool);

                file.setDeleted(true);
                file.setDateDeleted(new Date());
                file.setDeletedById(user.getId());
                fileService.save(file);
                responseMap.put("status", 1);
            }
        }
        return responseMap;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryDescriptionOfSourcesAdditionalCreate)
    @ResponseBody
    public HashMap<String,Object> regApplicationFourCategoryDescriptionOfSourcesAdditionalCreate(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "gSek") Double gSek,
            @RequestParam(name = "tYil") Double tYil,
            @RequestParam(name = "establishedQuota") Double establishedQuota,
            @RequestParam(name = "concentration") Double concentration
    ){
        System.out.println("regApplicationFourCategoryDescriptionOfSourcesAdditionalCreate");
        Integer status = 0;
        User user = userService.getCurrentUserFromContext();

        HashMap<String,Object> response = new HashMap<>();
        response.put("status",status);
        AirPool airPool = airPoolService.getById(id);
        if (airPool==null ){
            return response;
        }
        Set<DescriptionOfSourcesAdditional> descriptionOfSourcesAdditionals = airPool.getDescriptionOfSourcesAdditionals();
        if (descriptionOfSourcesAdditionals==null) descriptionOfSourcesAdditionals = new HashSet<>();
        DescriptionOfSourcesAdditional descriptionOfSourcesAdditional = new DescriptionOfSourcesAdditional();
        descriptionOfSourcesAdditional.setName(name);
        descriptionOfSourcesAdditional.setGSek(gSek);
        descriptionOfSourcesAdditional.setTYil(tYil);
        descriptionOfSourcesAdditional.setEstablishedQuota(establishedQuota);
        descriptionOfSourcesAdditional.setConcentration(concentration);
        descriptionOfSourcesAdditional.setDeleted(Boolean.FALSE);
        descriptionOfSourcesAdditional = descriptionOfSourcesAdditionalService.save(descriptionOfSourcesAdditional);
        descriptionOfSourcesAdditionals.add(descriptionOfSourcesAdditional);
        airPool.setDescriptionOfSourcesAdditionals(descriptionOfSourcesAdditionals);
        airPoolService.save(airPool);
        response.put("status",1);
        response.put("data",descriptionOfSourcesAdditional);

        return response;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryDescriptionOfSourcesAdditionalEdit)
    @ResponseBody
    public Object regApplicationFourCategoryDescriptionOfSourcesAdditionalEdit(
            @RequestParam(name = "regAddId") Integer regAddId,
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "gSek") Double gSek,
            @RequestParam(name = "tYil") Double tYil,
            @RequestParam(name = "establishedQuota") Double establishedQuota,
            @RequestParam(name = "concentration") Double concentration
    ){
        User user = userService.getCurrentUserFromContext();
        System.out.println("regApplicationFourCategoryDescriptionOfSourcesAdditionalEdit");
        System.out.println("regAddId" + regAddId);
        System.out.println("id" + id);
        System.out.println("name" + name);
        System.out.println("tYil" + tYil);
        System.out.println("tYil" + tYil);
        System.out.println("establishedQuota" + establishedQuota);
        System.out.println("concentration" + concentration);
        AirPool airPool = airPoolService.getById(regAddId);
        if (airPool==null ){
            return -2;
        }

        Set<DescriptionOfSourcesAdditional> descriptionOfSourcesAdditionals = airPool.getDescriptionOfSourcesAdditionals();
        if (descriptionOfSourcesAdditionals==null) descriptionOfSourcesAdditionals = new HashSet<>();
        DescriptionOfSourcesAdditional descriptionOfSourcesAdditional = descriptionOfSourcesAdditionalService.getById(id);
        if (descriptionOfSourcesAdditional==null || !descriptionOfSourcesAdditionals.contains(descriptionOfSourcesAdditional)){
            return -1;
        }
        descriptionOfSourcesAdditional.setName(name);
        descriptionOfSourcesAdditional.setGSek(gSek);
        descriptionOfSourcesAdditional.setTYil(tYil);
        descriptionOfSourcesAdditional.setEstablishedQuota(establishedQuota);
        descriptionOfSourcesAdditional.setConcentration(concentration);
        descriptionOfSourcesAdditional.setDeleted(Boolean.FALSE);
        descriptionOfSourcesAdditional = descriptionOfSourcesAdditionalService.save(descriptionOfSourcesAdditional);
        descriptionOfSourcesAdditionals.add(descriptionOfSourcesAdditional);
        airPool.setDescriptionOfSourcesAdditionals(descriptionOfSourcesAdditionals);
        return 1 + "";
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryDescriptionOfSourcesAdditionalDelete)
    @ResponseBody
    public String regApplicationFourCategoryDescriptionOfSourcesAdditionalDelete(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "regAddId") Integer regAddId
    ) {

        String status = "1";
        AirPool airPool = airPoolService.getById(regAddId);
        if (airPool==null ){
            return "-2";
        }

        Set<DescriptionOfSourcesAdditional> descriptionOfSourcesAdditionals = airPool.getDescriptionOfSourcesAdditionals();
        if (descriptionOfSourcesAdditionals==null) descriptionOfSourcesAdditionals = new HashSet<>();
        DescriptionOfSourcesAdditional descriptionOfSourcesAdditional = descriptionOfSourcesAdditionalService.getById(id);
        if (descriptionOfSourcesAdditional==null || !descriptionOfSourcesAdditionals.contains(descriptionOfSourcesAdditional)){
            return "-1";
        }

        descriptionOfSourcesAdditionals.remove(descriptionOfSourcesAdditional);
        airPool.setDescriptionOfSourcesAdditionals(descriptionOfSourcesAdditionals);
        airPoolService.save(airPool);

        descriptionOfSourcesAdditional.setDeleted(Boolean.TRUE);
        descriptionOfSourcesAdditionalService.save(descriptionOfSourcesAdditional);
        return status;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4_2, method = RequestMethod.POST)
    public String postRegApplicationFourCategoryStep4_2(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "regAddId") Integer regAddId,
            @RequestBody MultiValueMap<String, String> formData
    ){

        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id,user.getId());
        if (regApplication==null){
            return RegListRedirect;
        }
        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditional==null || !regApplicationCategoryFourAdditional.getId().equals(regAddId)
                || regApplicationCategoryFourAdditional.getAirPools()==null || regApplicationCategoryFourAdditional.getAirPools().isEmpty()){
            return RegListRedirect;
        }
        Map<String,String> map = formData.toSingleValueMap();

        HashMap<Integer,DescriptionOfSources> descriptionOfSourcesHashMap = new HashMap<>();
        Set<AirPool>airPoolSet = regApplicationCategoryFourAdditional.getAirPools();
        for (AirPool airPool:airPoolSet) {
            for (DescriptionOfSources descriptionOfSources: airPool.getDescriptionOfSources()){
                if (!descriptionOfSourcesHashMap.containsKey(descriptionOfSources.getId())){
                    descriptionOfSourcesHashMap.put(descriptionOfSources.getId(),descriptionOfSources);
                }
            }
        }

        DescriptionOfSources descriptionOfSources = new DescriptionOfSources();

        for (Map.Entry<String,String> mapEntry: map.entrySet()) {
            Integer boilerId = null;

            String[] paramName =  mapEntry.getKey().split("_");
            String  tagName = paramName[0];
            Integer decId=null;
            System.out.println("tagname" + tagName);

            if (paramName.length>1){
                try {
                    decId = Integer.parseInt(paramName[1]);
                }catch (Exception e){

                }
                System.out.println(paramName[1]);
            }
            Double value = null;
            if (decId!=null && descriptionOfSourcesHashMap.containsKey(decId)){
                String valueStr = mapEntry.getValue().replaceAll(" ","");

                try {
                    value = Double.parseDouble(valueStr);
                }catch (Exception e){
                    e.printStackTrace();
                }

                if (tagName.equals("sourcesHeight")){
                    descriptionOfSources = descriptionOfSourcesHashMap.get(decId);
                    descriptionOfSources.setSourcesHeight(value);
                }
                if (tagName.equals("sourcesDiometer")){
                    descriptionOfSources.setSourcesDiometer(value);
                }
                if (tagName.equals("sourcesW")){
                    descriptionOfSources.setSourcesW(value);
                }
                if (tagName.equals("sourcesV")){
                    descriptionOfSources.setSourcesV(value);
                }
                if (tagName.equals("sourcesT")){
                    descriptionOfSources.setSourcesT(value);
                    descriptionOfSourcesService.save(descriptionOfSources);
                }
            }
        }

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4_3 + "?id=" + regApplication.getId();
    }



    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4_3, method = RequestMethod.GET)
    public String regApplicationFourCategoryStep4_3Get(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id,user.getId());
        if (regApplication==null){
            return RegListRedirect;
        }

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        if (regApplicationCategoryFourAdditional==null){
            return RegListRedirect;
        }

        model.addAttribute("regApplicationCategoryFourAdditional",regApplicationCategoryFourAdditional);
        model.addAttribute("step4_3_total",regApplicationCategoryFourAdditionalService.step4_3_total(regApplicationCategoryFourAdditional));
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4_2 + "?id=" + id);
//        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP4.ordinal()+1);

    return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryStep4_3;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryHarmfulSubstancesAmountCreate)
    @ResponseBody
    public HashMap<String,Object> regApplicationFourCategoryHarmfulSubstancesAmountCreate(
            @RequestParam(name = "regAddId") Integer regAddId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "substancesAmount") Double substancesAmount,
            @RequestParam(name = "forCleaning") Double forCleaning,
            @RequestParam(name = "caught") Double caught,
            @RequestParam(name = "used") Double used,
            @RequestParam(name = "atmosphereAmount") Double atmosphereAmount
    ){
        System.out.println("regApplicationFourCategoryAirPoolCreate");
        Integer status = 0;
        User user = userService.getCurrentUserFromContext();

        HashMap<String,Object> response = new HashMap<>();
        response.put("status",status);
        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(regAddId);
        if (regApplicationCategoryFourAdditional==null ){
            return response;
        }
        Set<HarmfulSubstancesAmount> harmfulSubstancesAmounts = regApplicationCategoryFourAdditional.getHarmfulSubstancesAmounts();
        if (harmfulSubstancesAmounts==null) harmfulSubstancesAmounts = new HashSet<>();
        HarmfulSubstancesAmount harmfulSubstancesAmount = new HarmfulSubstancesAmount();
        harmfulSubstancesAmount.setName(name);
        harmfulSubstancesAmount.setSubstancesAmount(substancesAmount);
        harmfulSubstancesAmount.setForCleaning(forCleaning);
        harmfulSubstancesAmount.setCaught(caught);
        harmfulSubstancesAmount.setUsed(used);
        harmfulSubstancesAmount.setAtmosphereAmount(atmosphereAmount);
        harmfulSubstancesAmount.setDeleted(Boolean.FALSE);
        harmfulSubstancesAmount = harmfulSubstancesAmountService.save(harmfulSubstancesAmount);
        harmfulSubstancesAmounts.add(harmfulSubstancesAmount);
        regApplicationCategoryFourAdditional.setHarmfulSubstancesAmounts(harmfulSubstancesAmounts);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());
        response.put("status",1);
        response.put("data",harmfulSubstancesAmount);

        return response;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryHarmfulSubstancesAmountEdit)
    @ResponseBody
    public Object regApplicationFourCategoryHarmfulSubstancesAmountEdit(
            Model model,
            @RequestParam(name = "regAddId") Integer regAddId,
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "substancesAmount") Double substancesAmount,
            @RequestParam(name = "forCleaning") Double forCleaning,
            @RequestParam(name = "caught") Double caught,
            @RequestParam(name = "used") Double used,
            @RequestParam(name = "atmosphereAmount") Double atmosphereAmount
    ){
        User user = userService.getCurrentUserFromContext();

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(regAddId);
        if (regApplicationCategoryFourAdditional==null ){
            return -1;
        }
        Set<HarmfulSubstancesAmount> harmfulSubstancesAmounts = regApplicationCategoryFourAdditional.getHarmfulSubstancesAmounts();

        HarmfulSubstancesAmount harmfulSubstancesAmount = harmfulSubstancesAmountService.getById(id);
        if (!harmfulSubstancesAmounts.contains(harmfulSubstancesAmount)){
            return 2;
        }
        harmfulSubstancesAmounts.remove(harmfulSubstancesAmount);
        harmfulSubstancesAmount.setName(name);
        harmfulSubstancesAmount.setSubstancesAmount(substancesAmount);
        harmfulSubstancesAmount.setForCleaning(forCleaning);
        harmfulSubstancesAmount.setCaught(caught);
        harmfulSubstancesAmount.setUsed(used);
        harmfulSubstancesAmount.setAtmosphereAmount(atmosphereAmount);
        harmfulSubstancesAmount = harmfulSubstancesAmountService.save(harmfulSubstancesAmount);
        harmfulSubstancesAmounts.add(harmfulSubstancesAmount);
        regApplicationCategoryFourAdditional.setHarmfulSubstancesAmounts(harmfulSubstancesAmounts);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());
        return 1 + "";
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryHarmfulSubstancesAmountDelete)
    @ResponseBody
    public String regApplicationFourCategoryHarmfulSubstancesAmountDelete(
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

        HarmfulSubstancesAmount harmfulSubstancesAmount = harmfulSubstancesAmountService.getById(id);
        if (harmfulSubstancesAmount == null) {
            status = "-1";
            return status;
        }

        Set<HarmfulSubstancesAmount> harmfulSubstancesAmounts = regApplicationCategoryFourAdditional.getHarmfulSubstancesAmounts();
        if (harmfulSubstancesAmounts == null || harmfulSubstancesAmounts.isEmpty() || !harmfulSubstancesAmounts.contains(harmfulSubstancesAmount)) {
            status = "-2";
            return status;
        }
        harmfulSubstancesAmounts.remove(harmfulSubstancesAmount);
        regApplicationCategoryFourAdditional.setHarmfulSubstancesAmounts(harmfulSubstancesAmounts);
        regApplicationCategoryFourAdditionalService.update(regApplicationCategoryFourAdditional,user.getId());

        harmfulSubstancesAmount.setDeleted(Boolean.TRUE);
        harmfulSubstancesAmountService.save(harmfulSubstancesAmount);
        return status;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4_3, method = RequestMethod.POST)
    public String regApplicationFourCategoryStep4Post(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "regAddId") Integer regAddId,
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

        regApplicationCategoryFourAdditionalService.saveStep4_3(regApplicationCategoryFourAdditional,regApplicationCategoryFourAdditionalOld,user.getId());

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep5 + "?id=" + id;
    }


    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep5, method = RequestMethod.GET)
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
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep4 + "?id=" + id);
//        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP5.ordinal()+1);

        return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryStep5;
    }


    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryBoilerCharacteristicsCreate)
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

//    @RequestMapping(value = ExpertiseUrls.RegApplicationFourCategoryBoilerCharacteristicsEditType1)
//    @ResponseBody
//    public Object regApplicationFourCategoryBoilerCharacteristicsEdit(
//            Model model,
//            @RequestParam(name = "regId") Integer regId,
//            @RequestParam(name = "id") Integer id,
//            @RequestParam(name = "name_boiler") String name,
//            @RequestParam(name = "type_boiler") String type,
//            @RequestParam(name = "amount_boiler") Double amount
//    ){
//        System.out.println("regId" + regId);
//        System.out.println("id" + id);
//        System.out.println("name_boiler" + name);
//        System.out.println("type_boiler" + type);
//        System.out.println("amount_boiler" + amount);
//        User user = userService.getCurrentUserFromContext();
//        RegApplication regApplication = regApplicationService.getById(regId);
//        if (regApplication==null ){
//            return 0;
//        }
////        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regId);
////        if (regApplicationCategoryFourAdditional==null ){
////            return -1;
////        }
//        Set<BoilerCharacteristics> boilerCharacteristicsSet = regApplication.getBoilerCharacteristics();
//
//        BoilerCharacteristics  characteristics = boilerCharacteristicsService.getById(id);
//        if (!boilerCharacteristicsSet.contains(characteristics)){
//            return 2;
//        }
//        boilerCharacteristicsSet.remove(characteristics);
//        characteristics.setName(name);
//        characteristics.setType(type);
//        characteristics.setAmount(amount);
//        characteristics = boilerCharacteristicsService.save(characteristics);
//        boilerCharacteristicsSet.add(characteristics);
//        regApplication.setBoilerCharacteristics(boilerCharacteristicsSet);
//        regApplicationService.updateBoiler(regApplication,user.getId());
//        return 1 + "";
//    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryBoilerCharacteristicsDelete)
    @ResponseBody
    public String regApplicationFourCategoryBoilerCharacteristicsDelete(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "regAddId") Integer regAddId
    ) {
        RegApplication regApplication = regApplicationService.getById(regAddId);
        String status = "1";
        User user = userService.getCurrentUserFromContext();
//       RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getById(regAddId);
//        if (regApplicationCategoryFourAdditional == null || regApplicationCategoryFourAdditional.getBoilerCharacteristics()==null) {
//            status = "0";
//            return status;
//        }

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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryBoilerSave)
    @ResponseBody
    public HashMap<String,Object> isSaving(
            @RequestParam(name = "regCategory_id") Integer regCategoryId,
            @RequestParam(name = "boiler_name") String boilerName,
            @RequestBody MultiValueMap<String, String> formData
    ){
        System.out.println("regCategoryId"+regCategoryId);
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();
        result.put("status",0);
        Map<String,String> map = formData.toSingleValueMap();
        RegApplication regApplication = regApplicationService.getById(regCategoryId);
        if (regApplication==null
                || regApplication.getBoilerCharacteristics()==null
                || regApplication.getBoilerCharacteristics().isEmpty()){
            return result;
        }
        regApplication.setBoilerName(boilerName);
        regApplicationService.updateBoiler(regApplication,user.getId());
        // BoilerCharacteristics hammasini id orqali mapga solindi
        HashMap<Integer,BoilerCharacteristics> boilerCharacteristicsHashMap = new HashMap<>();
        Set<BoilerCharacteristics> boilerCharacteristicsSet = regApplication.getBoilerCharacteristics();
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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryBoilerIsSave)
    @ResponseBody
    public Boolean isSavedBoilercharacteristic(@RequestParam(name = "id") Integer id){
        return isSavingBoiler(id);
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep5, method = RequestMethod.POST)
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

//        regApplication.setCategoryFourStep(RegApplicationCategoryFourStep.STEP6);
        regApplicationService.update(regApplication);

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep6 + "?id=" + id;
    }


    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep6, method = RequestMethod.GET)
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
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep5 + "?id=" + id);
        model.addAttribute("next_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep7 + "?id=" + id);
//        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP6.ordinal()+1);

        return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryStep6;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryPollutionMeasuresCreate)
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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryPollutionMeasuresEdit)
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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryPollutionMeasuresDelete)
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



  /*  @RequestMapping(value = ExpertiseUrls.RegApplicationFourCategoryStep6, method = RequestMethod.POST)
    public String regApplicationFourCategoryStep6Post(
            @RequestParam(name = "id") Integer id
    ){

        return "redirect:" + ExpertiseUrls.RegApplicationFourCategoryStep7 + "?id=" + id;
    }*/


    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep7, method = RequestMethod.GET)
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
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep6 + "?id=" + id);
//        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP7.ordinal()+1);
        return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryStep7;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep7, method = RequestMethod.POST)
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

        RegApplicationLog confirmLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
        if(confirmLog==null || !confirmLog.getStatus().equals(LogStatus.Approved)){
            RegApplicationLog regApplicationLog = regApplicationLogService.create(regApplication,LogType.Confirm,"",user);
            regApplication.setConfirmLogAt(new Date());
            regApplication.setConfirmLogId(regApplicationLog.getId());
            regApplication.setStatus(RegApplicationStatus.CheckSent);
        }
        regApplication.setCategoryFourStep(RegApplicationCategoryFourStep.WAITING);
        regApplicationService.update(regApplication);
//        return "redirect:" + ExpertiseUrls.RegApplicationFourCategoryStep7 + "?id=" + id;
        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryWaiting + "?id=" + id;
    }


    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryWaiting,method = RequestMethod.GET)
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
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep3 + "?id=" + id;
        }
        if (regApplication.getForwardingLogId() != null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryContract + "?id=" + id;
        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("field", field);
        model.addAttribute("regApplicationLog", regApplicationLog);
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryStep3 + "?id=" + id);
        model.addAttribute("step_id", RegApplicationCategoryFourStep.STEP3.ordinal()+1);
        return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryWaiting;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryWaiting,method = RequestMethod.POST)
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
                return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryWaiting + "?id=" + id;
            }
        }

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryContract + "?id=" + id;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryContract,method = RequestMethod.GET)
    public String getContractPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id, user.getId());
//        String check = check(regApplication,user);
//        if(check!=null){
//            return check;
//        }

        model.addAttribute("regApplication", regApplication);
        model.addAttribute("action_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryContractConfirm);
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryAbout + "?id=" + id);
        model.addAttribute("step_id", RegApplicationStep.CONTRACT.ordinal());
        return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryContract;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryContractConfirm,method = RequestMethod.GET)
    public String regApplicationContractConfirm(
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
        regApplication.setContractDate(DateParser.TryParse(contractDateStr, Common.uzbekistanDateFormat));
        regApplication.setBudget(budget);
        regApplicationService.update(regApplication);

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryPrepayment+ "?id=" + id;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryContract,method = RequestMethod.POST)
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
//            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Oferta tasdiqlanmagan.");
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryContract + "?id=" + id;
        }

        regApplication.setStep(RegApplicationStep.PAYMENT);
        regApplicationService.update(regApplication);

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryPrepayment + "?id=" + id;
    }
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryPrepayment)
    public String getPrepaymentPage(
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
        }else{
            invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
            invoice = invoiceService.modification(regApplication, invoice, requirement);
            invoiceService.checkInvoiceStatus(invoice);
            if (invoice.getStatus().equals(InvoiceStatus.Success) || invoice.getStatus().equals(InvoiceStatus.PartialSuccess)){
                return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStatus + "?id=" + id;
            }
        }
        model.addAttribute("invoice", invoice);
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("action_url", ExpertiseUrls.ExpertiseRegApplicationFourCategoryPaymentSendSms);
        model.addAttribute("action_url", globalConfigs.getIsTesting().equals("test") ? ExpertiseUrls.ExpertiseRegApplicationFourCategoryPaymentFree : ExpertiseUrls.ExpertiseRegApplicationFourCategoryPaymentSendSms);
        model.addAttribute("step_id", RegApplicationStep.PAYMENT.ordinal());
        return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryPrepayment;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryPaymentSendSms)
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
        String failUrl = ExpertiseUrls.ExpertiseRegApplicationFourCategoryPaymentSendSms;
        String successUrl = ExpertiseUrls.ExpertiseRegApplicationFourCategoryPaymentConfirmSms;
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


    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryPaymentConfirmSms)
    @ResponseBody
    public Map<String, Object> confirmSmsPayment(
            @RequestParam(name = "id") Integer applicationId,
            @RequestParam(name = "trId") Integer trId,
            @RequestParam(name = "paymentId") Integer paymentId,
            @RequestParam(name = "confirmSms") String confirmSms
    ) {
        String successUrl = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStatus+ "?id=" + applicationId;
        String failUrl = ExpertiseUrls.ExpertiseRegApplicationFourCategoryPaymentConfirmSms;

        return paymentService.confirmSmsAndGetResponseAsMap(
                applicationId,
                paymentId,
                trId,
                confirmSms,
                successUrl,
                failUrl
        );
    }
    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryPaymentFree)
    public String getPaymentFreeMethod(
            @RequestParam(name = "id",required = false) Integer id
    ) {
        System.out.println("id="+id);
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
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryPrepayment + "?id=" + id;
        }

        Invoice invoice = invoiceService.getInvoice(regApplication.getInvoiceId());
        if (invoice == null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryPrepayment + "?id=" + id;
        }

        if(invoice.getStatus().equals(InvoiceStatus.Success) || invoice.getStatus().equals(InvoiceStatus.PartialSuccess)){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStatus + "?id=" + id;
        }

        //todo invoice amount 0 bo'lishi kerak
        invoiceService.payTest(invoice.getId());
        /*if(invoice.getAmount().equals(0.0)){
            invoiceService.payTest(invoice.getId());
        }*/
        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStatus + "?id=" + id;
    }


    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryStatus)
    public String getStatusPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        System.out.println("RegApplicationStatus");
        System.out.println("id="+id);
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
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryPrepayment + "?id=" + id;
        }
        if (invoice.getStatus()!=InvoiceStatus.Success && invoice.getStatus()!=InvoiceStatus.PartialSuccess){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryPrepayment + "?id=" + id;
        }

        Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());
        model.addAttribute("conclusion", conclusion);
        if(conclusion != null){
            model.addAttribute("documentRepo", documentRepoService.getDocument(conclusion.getDocumentRepoId()));
        }
//        Offer offer = offerService.getById(regApplication.getOfferId());
        model.addAttribute("performerLog", regApplicationLogService.getById(regApplication.getPerformerLogId()));
        model.addAttribute("commentList", commentService.getByRegApplicationIdAndType(regApplication.getId(), CommentType.CHAT));
//        model.addAttribute("offer", offer);
        model.addAttribute("invoice", invoice);
        model.addAttribute("facture", factureService.getById(regApplication.getFactureId()));
        model.addAttribute("factureProductList", factureService.getByFactureId(regApplication.getFactureId()));
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("back_url", ExpertiseUrls.ExpertiseRegApplicationList);
        model.addAttribute("step_id", RegApplicationStep.STATUS.ordinal()+1);
        return ExpertiseTemplates.ExpertiseRegApplicationFourCategoryStatus;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryResend)
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
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStatus + "?id=" + regApplication.getId();
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

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStatus + "?id=" + regApplication.getId();
    }



    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryGetActivity, method = RequestMethod.POST)
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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryGetMaterials, method = RequestMethod.POST)
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

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationFourCategoryGetMaterial, method = RequestMethod.POST)
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
                return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryStatus + "?id=" + regApplication.getId();
            }else {
                modification = false;
            }
        }
        if (regApplication.getConfirmLogId()!=null && modification){
            System.out.println("if=3");
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
            if(regApplicationLog.getStatus() != LogStatus.Denied){
                toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Arizachi ma'lumotlarini o'zgartirishga Ruxsat yo'q.");
                return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationFourCategoryWaiting + "?id=" + regApplication.getId();
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
