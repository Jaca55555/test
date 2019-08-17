package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.Coordinate;
import uz.maroqand.ecology.core.entity.expertise.CoordinateLatLong;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.CoordinateLatLongRepository;
import uz.maroqand.ecology.core.repository.expertise.CoordinateRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz) Arizani ijrochisini kiritish va kelishish uchun yuborish
 * (ru)
 */
@Controller
public class ForwardingController {

    private final RegApplicationService regApplicationService;
    private final ClientService clientService;
    private final UserService userService;
    private final HelperService helperService;
    private final SoatoService soatoService;
    private final InvoiceService invoiceService;
    private final ActivityService activityService;
    private final ObjectExpertiseService objectExpertiseService;
    private final RegApplicationLogService regApplicationLogService;
    private final ProjectDeveloperService projectDeveloperService;
    private final CoordinateRepository coordinateRepository;
    private final CoordinateLatLongRepository coordinateLatLongRepository;
    private final DepartmentService departmentService;

    @Autowired
    public ForwardingController(
            RegApplicationService regApplicationService,
            ClientService clientService,
            UserService userService,
            HelperService helperService,
            SoatoService soatoService,
            InvoiceService invoiceService,
            ActivityService activityService,
            ObjectExpertiseService objectExpertiseService,
            RegApplicationLogService regApplicationLogService,
            ProjectDeveloperService projectDeveloperService,
            CoordinateRepository coordinateRepository,
            CoordinateLatLongRepository coordinateLatLongRepository,
            DepartmentService departmentService) {
        this.regApplicationService = regApplicationService;
        this.clientService = clientService;
        this.userService = userService;
        this.helperService = helperService;
        this.soatoService = soatoService;
        this.invoiceService = invoiceService;
        this.activityService = activityService;
        this.objectExpertiseService = objectExpertiseService;
        this.regApplicationLogService = regApplicationLogService;
        this.projectDeveloperService = projectDeveloperService;
        this.coordinateRepository = coordinateRepository;
        this.coordinateLatLongRepository = coordinateLatLongRepository;
        this.departmentService = departmentService;
    }

    @RequestMapping(ExpertiseUrls.ForwardingList)
    public String getForwardingListPage(Model model){

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList", LogStatus.getLogStatusList());
        return ExpertiseTemplates.ForwardingList;
    }

    @RequestMapping(value = ExpertiseUrls.ForwardingListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getForwardingListAjaxPage(
            FilterDto filterDto,
            Pageable pageable
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String,Object> result = new HashMap<>();

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                filterDto,
                user.getOrganizationId(),
                LogType.Forwarding,
                null,
                null,
                pageable
        );

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            Client client = clientService.getById(regApplication.getApplicantId());
            RegApplicationLog performerLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
            RegApplicationLog forwardingLog = regApplicationLogService.getById(regApplication.getForwardingLogId());
            convenientForJSONArray.add(new Object[]{
                    regApplication.getId(),
                    client.getTin(),
                    client.getName(),
                    regApplication.getMaterials() != null ?helperService.getMaterialShortNames(regApplication.getMaterials(),locale):"",
                    regApplication.getCategory() != null ?helperService.getCategory(regApplication.getCategory().getId(),locale):"",
                    regApplication.getRegistrationDate() != null ?Common.uzbekistanDateFormat.format(regApplication.getRegistrationDate()):"",
                    regApplication.getDeadlineDate() != null ?Common.uzbekistanDateFormat.format(regApplication.getDeadlineDate()):"",
                    performerLog!=null ? performerLog.getStatus().getPerformerName():"",
                    performerLog!=null ? performerLog.getStatus().getId():"",
                    forwardingLog.getStatus()!=null? forwardingLog.getStatus().getForwardingName():"",
                    forwardingLog.getStatus()!=null? forwardingLog.getStatus().getId():""
            });
        }

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.ForwardingView)
    public String getForwardingViewPage(
            @RequestParam(name = "id")Integer regApplicationId,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ForwardingList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getForwardingLogId());
        if(regApplication.getPerformerLogId()!=null){
            RegApplicationLog performerLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
            model.addAttribute("performerLog", performerLog);
        }
        model.addAttribute("agreementLogList", regApplicationLogService.getByIds(regApplication.getAgreementLogs()));

        Client client = clientService.getById(regApplication.getApplicantId());
        if(client.getType().equals(ApplicantType.Individual)){
            model.addAttribute("individual", new IndividualDto(client));
        }else {
            model.addAttribute("legalEntity", new LegalEntityDto(client));
        }

        Coordinate coordinate = coordinateRepository.findByRegApplicationIdAndDeletedFalse(regApplication.getId());
        if(coordinate != null){
            List<CoordinateLatLong> coordinateLatLongList = coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId());
            model.addAttribute("coordinate", coordinate);
            model.addAttribute("coordinateLatLongList", coordinateLatLongList);
        }

        model.addAttribute("applicant", client);
        model.addAttribute("invoice", invoiceService.getInvoice(regApplication.getInvoiceId()));
        model.addAttribute("userList", userService.getEmployeesForForwarding(user.getOrganizationId()));
        model.addAttribute("departmentList", departmentService.getByOrganizationId(user.getOrganizationId()));
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("regApplicationLog", regApplicationLog);
        return ExpertiseTemplates.ForwardingView;
    }

    @RequestMapping(value = ExpertiseUrls.ForwardingAction,method = RequestMethod.POST)
    public String confirmApplication(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "logId")Integer logId,
            @RequestParam(name = "performerId")Integer performerId,
            @RequestParam(name = "comment")String comment
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ForwardingList;
        }

        User performer = userService.findById(performerId);
        if (performer== null){
            return "redirect:" + ExpertiseUrls.ForwardingList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        regApplicationLogService.update(regApplicationLog, LogStatus.Approved, comment, user);

        RegApplicationLog regApplicationLogCreate = regApplicationLogService.create(regApplication,LogType.Performer,comment,user);

        regApplication.setStatus(RegApplicationStatus.Process);
        regApplication.setPerformerId(performerId);
        regApplication.setPerformerLogId(regApplicationLogCreate.getId());
        regApplicationService.update(regApplication);

        return "redirect:"+ExpertiseUrls.ForwardingView + "?id=" + regApplication.getId();
    }

    @RequestMapping(value = ExpertiseUrls.ForwardingAgreementAdd,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> getAddAgreementMethod(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "agreementUserId")Integer agreementUserId
    ){
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            result.put("status", "1");
            return result;
        }
        Set<Integer> agreementLogs = regApplication.getAgreementLogs();
        if(agreementLogs == null){
            agreementLogs = new HashSet<>();
        }
        Boolean isAgreementUser = false;
        List<RegApplicationLog> regApplicationLogList = regApplicationLogService.getByIds(agreementLogs);
        for (RegApplicationLog regApplicationLog:regApplicationLogList){
            if(regApplicationLog.getUpdateById().equals(agreementUserId)){
                isAgreementUser = true;
            }
        }
        if (isAgreementUser){
            result.put("status", "2");
            return result;
        }

        User agreementUser = userService.findById(agreementUserId);

        RegApplicationLog regApplicationLogCreate = regApplicationLogService.create(regApplication, LogType.Agreement,"", user);
        regApplicationLogService.update(regApplicationLogCreate, LogStatus.Initial,"", agreementUser);

        agreementLogs.add(regApplicationLogCreate.getId());
        regApplication.setAgreementLogs(agreementLogs);
        regApplicationService.update(regApplication);

        result.put("status", "0");
        result.put("shorName", helperService.getUserLastAndFirstShortById(agreementUser.getId()));
        result.put("fullName", helperService.getUserFullNameById(agreementUser.getId()));
        return result;
    }

    @RequestMapping(value = ExpertiseUrls.ForwardingAgreementDelete,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> getDeleteAgreementMethod(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "logId")Integer logId
    ){
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            result.put("status", "1");
            return result;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        if(regApplicationLog == null || !regApplicationLog.getType().equals(LogType.Agreement)){
            result.put("status", "2");
            return result;
        }

        if(!regApplicationLog.getStatus().equals(LogStatus.Initial)){
            result.put("status", "2");
            return result;
        }

        Set<Integer> agreementLogs = regApplication.getAgreementLogs();
        if(!agreementLogs.contains(logId)){
            result.put("status", "2");
            return result;
        }

        agreementLogs.remove(logId);
        regApplication.setAgreementLogs(agreementLogs);
        regApplicationService.update(regApplication);

        result.put("status", "0");
        return result;
    }

}
