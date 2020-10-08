package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.dto.expertise.*;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.CoordinateLatLongRepository;
import uz.maroqand.ecology.core.repository.expertise.CoordinateRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.TinParser;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 23.06.2019.
 * (uz)
 * (ru)
 */
@Controller
public class RegApplicationMonitoringController {

    private final UserService userService;
    private final RegApplicationService regApplicationService;
    private final RegApplicationLogService regApplicationLogService;
    private final HelperService helperService;
    private final ClientService clientService;
    private final ChangeDeadlineDateService changeDeadlineDateService;
    private final CoordinateRepository coordinateRepository;
    private final CoordinateLatLongRepository coordinateLatLongRepository;
    private final CommentService commentService;
    private final InvoiceService invoiceService;
    private final ProjectDeveloperService projectDeveloperService;
    private final SoatoService soatoService;
    private final ObjectExpertiseService objectExpertiseService;
    private final ActivityService activityService;
    private final RequirementService requirementService;

    public RegApplicationMonitoringController(UserService userService, RegApplicationService regApplicationService, RegApplicationLogService regApplicationLogService, HelperService helperService, ClientService clientService, ChangeDeadlineDateService changeDeadlineDateService, CoordinateRepository coordinateRepository, CoordinateLatLongRepository coordinateLatLongRepository, CommentService commentService, InvoiceService invoiceService, ProjectDeveloperService projectDeveloperService, SoatoService soatoService, ObjectExpertiseService objectExpertiseService, ActivityService activityService, RequirementService requirementService) {
        this.userService = userService;
        this.regApplicationService = regApplicationService;
        this.regApplicationLogService = regApplicationLogService;
        this.helperService = helperService;
        this.clientService = clientService;
        this.changeDeadlineDateService = changeDeadlineDateService;
        this.coordinateRepository = coordinateRepository;
        this.coordinateLatLongRepository = coordinateLatLongRepository;
        this.commentService = commentService;
        this.invoiceService = invoiceService;
        this.projectDeveloperService = projectDeveloperService;
        this.soatoService = soatoService;
        this.objectExpertiseService = objectExpertiseService;
        this.activityService = activityService;
        this.requirementService = requirementService;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationMonitoringList)
    public String expertiseRegApplicationMonitoringList(Model model) {
        List<RegApplicationStatus> logStatusList = RegApplicationStatus.getRegApplicationStatusList();

        model.addAttribute("regions", soatoService.getRegions());
        model.addAttribute("subRegions", soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList", objectExpertiseService.getList());
        model.addAttribute("activityList", activityService.getList());
        model.addAttribute("statusList", logStatusList);
        return ExpertiseTemplates.ExpertiseRegApplicationMonitoringList;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationMonitoringListAjax, produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> expertiseRegApplicationMonitoringListAjax(
            FilterDto filterDto,
            Pageable pageable
    ) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        User user = userService.getCurrentUserFromContext();

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(filterDto,userService.isAdmin()?null:user.getOrganizationId(),null,null,null, null,pageable);
        HashMap<String, Object> result = new HashMap<>();

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            convenientForJSONArray.add(new Object[]{
                    regApplication.getId(),
                    regApplication.getInputType(),
                    helperService.getObjectExpertise(regApplication.getObjectId(),locale),
                    helperService.getMaterials(regApplication.getMaterials(),locale),
                    regApplication.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(regApplication.getCreatedAt()):"",
                    regApplication.getStatus()!=null? helperService.getRegApplicationStatus(regApplication.getStatus().getId(),locale):"",
                    regApplication.getStatus()!=null? regApplication.getStatus().getColor():"",
                    regApplication.getApplicantId()!=null?regApplication.getName():"",
                    regApplication.getApplicantId()!=null?regApplication.getApplicant().getTin():""

            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(value = ExpertiseUrls.ExpertiseRegApplicationMonitoringView)
    public String viewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication==null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationMonitoringList;
        }
        Client applicant = clientService.getById(regApplication.getApplicantId());
        switch (applicant.getType()){
            case Individual:
                model.addAttribute("individual", new IndividualDto(applicant)); break;
            case LegalEntity:
                model.addAttribute("legalEntity", new LegalEntityDto(applicant)) ;break;
            case ForeignIndividual:
                model.addAttribute("foreignIndividual", new ForeignIndividualDto(applicant)); break;
            case IndividualEnterprise:
                model.addAttribute("individualEntrepreneur", new IndividualEntrepreneurDto(applicant)); break;
        }

        Coordinate coordinate = coordinateRepository.findByRegApplicationIdAndDeletedFalse(regApplication.getId());
        if(coordinate != null){
            List<CoordinateLatLong> coordinateLatLongList = coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId());
            model.addAttribute("coordinate", coordinate);
            model.addAttribute("coordinateLatLongList", coordinateLatLongList);
        }

        model.addAttribute("invoice", invoiceService.getInvoice(regApplication.getInvoiceId()));
        model.addAttribute("applicant", applicant);
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));

        List<RegApplicationLog> regApplicationLogList = regApplicationLogService.getByRegApplicationId(regApplication.getId());
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("regApplicationLogList",regApplicationLogList);
        return ExpertiseTemplates.ExpertiseRegApplicationMonitoringView;
    }

    @GetMapping(value = ExpertiseUrls.ExpertiseRegApplicationMonitoringEdit + "/{id}")
    public String getMonitoringEditPage( @PathVariable("id") Integer id, Model model ) {
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationMonitoringList;
        }

        Coordinate coordinate = coordinateRepository.findByRegApplicationIdAndDeletedFalse(regApplication.getId());
        if(coordinate != null){
            model.addAttribute("coordinate", coordinate);
            model.addAttribute("coordinateLatLongList", coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId()));
        }
        model.addAttribute("objectExpertiseList", objectExpertiseService.getList());
        model.addAttribute("activityList", activityService.getList());
        model.addAttribute("requirementList", requirementService.getAllList());
        model.addAttribute("categoryList", Category.getCategoryList());
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("categoryId", regApplication.getCategory() !=null ? regApplication.getCategory().getId() : null);

        model.addAttribute("regApplication", regApplication);
        return ExpertiseTemplates.ExpertiseRegApplicationMonitoringEdit;
    }

    @PostMapping(value = ExpertiseUrls.ExpertiseRegApplicationMonitoringEdit + "/{id}")
    public String updateRegApplication(
            @PathVariable("id") Integer id,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "applicationTin") String applicationTin,
            @RequestParam(name = "objectId") Integer objectId,
            @RequestParam(name = "activityId", required = false) Integer activityId,
            @RequestParam(name = "materials", required = false) Set<Integer> materials,
            @RequestParam(name = "tin") String projectDeveloperTin,
            @RequestParam(name = "projectDeveloperName") String projectDeveloperName,
            @RequestParam(name = "coordinates", required = false) List<Double> coordinates
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationMonitoringList;
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
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationMonitoringEdit + "?id=" + id + "&failed=1";
        }else if(requirementList.size()==1){
            requirement = requirementList.get(0);
        }

        if(requirement==null){
            return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationMonitoringEdit + "?id=" + id + "&failed=2";
        }
        regApplication.setRequirementId(requirement.getId());
        regApplication.setReviewId(requirement.getReviewId());
        regApplication.setDeadline(requirement.getDeadline());

        regApplication.setObjectId(objectId);
        Client client = regApplication.getApplicant();
        client.setTin(TinParser.trimIndividualsTinToNull(applicationTin));
        client.setName(name);
        clientService.saveForEdit(client);
        regApplication.setMaterials(materials);

        regApplication.setActivityId(activityId);
        regApplication.setCategory(activity!=null? activity.getCategory():null);

        regApplication.setUpdateById(user.getId());
        regApplication.setUpdateAt(new Date());
        regApplicationService.update(regApplication);

        return "redirect:" + ExpertiseUrls.ExpertiseRegApplicationMonitoringView + "?id=" + id;
    }

}