package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.dto.expertise.*;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.PerformerHistoryRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.SmsSendService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.ToastrService;
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
    private final CoordinateService coordinateService;
    private final DepartmentService departmentService;
    private final ToastrService toastrService;
    private final NotificationService notificationService;
    private final CommentService commentService;
    private final SmsSendService smsSendService;
    private final RegApplicationCategoryFourAdditionalService regApplicationCategoryFourAdditionalService;
    private final PerformerHistoryRepository performerHistoryRepository;

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
            DepartmentService departmentService,
            CoordinateService coordinateService,
            ToastrService toastrService,
            NotificationService notificationService,
            CommentService commentService,
            SmsSendService smsSendService,
            RegApplicationCategoryFourAdditionalService regApplicationCategoryFourAdditionalService, PerformerHistoryRepository performerHistoryRepository) {
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
        this.coordinateService = coordinateService;
        this.departmentService = departmentService;
        this.toastrService = toastrService;
        this.notificationService = notificationService;
        this.commentService = commentService;
        this.smsSendService = smsSendService;
        this.regApplicationCategoryFourAdditionalService = regApplicationCategoryFourAdditionalService;
        this.performerHistoryRepository = performerHistoryRepository;
    }

    @RequestMapping(ExpertiseUrls.ForwardingList)
    public String getForwardingListPage(Model model){
        List<LogStatus> logStatusList = new ArrayList<>();
        logStatusList.add(LogStatus.Resend);
        logStatusList.add(LogStatus.Approved);

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList", logStatusList);
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
                user.getRole().getId()!=16?user.getOrganizationId():null,
                LogType.Forwarding,
                null,
                null,
                null,//todo shart kerak
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
                    performerLog!=null ? helperService.getTranslation(performerLog.getStatus().getPerformerName(),locale):"",
                    performerLog!=null ? performerLog.getStatus().getId():"",
                    forwardingLog.getStatus()!=null? helperService.getTranslation(forwardingLog.getStatus().getForwardingName(),locale):"",
                    forwardingLog.getStatus()!=null? forwardingLog.getStatus().getId():"",
                    regApplicationService.beforeOrEqualsTrue(regApplication)
            });
        }

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements

        result.put("initials", regApplicationService.findFilteredNumber(LogType.Forwarding, user.getRole().getId()!=16?user.getOrganizationId():null, null));

        result.put("data",convenientForJSONArray);
        return result;
    }

    @GetMapping(ExpertiseUrls.ForwardingView)
    public String getForwardingViewPage(
            @RequestParam(name = "id")Integer regApplicationId,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ForwardingList;
        }

        clientService.clientView(regApplication.getApplicantId(), model);
        coordinateService.coordinateView(regApplicationId, model);

        model.addAttribute("regApplicationLog", regApplicationLogService.getById(regApplication.getForwardingLogId()));
        model.addAttribute("performerLog", regApplicationLogService.getById(regApplication.getPerformerLogId()));
        model.addAttribute("agreementLogList", regApplicationLogService.getByIds(regApplication.getAgreementLogs()));

        model.addAttribute("userList", userService.getEmployeesForForwarding(user.getOrganizationId()));
        model.addAttribute("agreementList", userService.getEmployeesAgreementForForwarding(user.getOrganizationId()));
        model.addAttribute("performerList", userService.getEmployeesPerformerForForwarding(user.getOrganizationId()));
        model.addAttribute("departmentList", departmentService.getByOrganizationId(user.getOrganizationId()));
        model.addAttribute("regApplicationLogList", regApplicationLogService.getByRegApplicationId(regApplication.getId()));


        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = null;
        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        }
        model.addAttribute("regApplicationCategoryFourAdditional", regApplicationCategoryFourAdditional);

        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("invoice", invoiceService.getInvoice(regApplication.getInvoiceId()));
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("action_url",ExpertiseUrls.ForwardingView);
        model.addAttribute("histories",performerHistoryRepository.findByApplicationNumberAndDeletedFalse(regApplicationId));
        return ExpertiseTemplates.ForwardingView;
    }
    @PostMapping(ExpertiseUrls.ForwardingView)
    public String RegApplicationChangePerformer(
            @RequestParam(name= "reg_id") Integer id,
            @RequestParam(name = "userId") Integer userId

    ) {

        System.out.println("userId"+userId);
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id);
        RegApplicationLog regApplicationLog = regApplicationLogService.getByRegApplcationId(id);
        if (regApplicationLog!=null){
            if((regApplicationLog.getStatus().getId()==0||regApplicationLog.getStatus().getId()==1||regApplicationLog.getStatus().getId()==2)&&
                    regApplicationLog.getType().getId()==2){
                regApplicationLog.setUpdateById(userId);
                PerformerHistory performerHistory = new PerformerHistory();
                performerHistory.setBeforePerformer(regApplication.getPerformerId());
                performerHistory.setApplicationNumber(regApplication.getId());
                performerHistory.setCreatedAt(new Date());
                performerHistory.setCreatedById(user.getId());
                regApplication.setPerformerId(userId);
                regApplicationLogService.updateDocument(regApplicationLog);
                regApplicationService.update(regApplication);


                performerHistory.setAfterPerformer(userId);

                performerHistoryRepository.saveAndFlush(performerHistory);

            }

        }



        return "redirect:" + ExpertiseUrls.ForwardingView + "?id=" + regApplication.getId() + "#action";

    }






    @RequestMapping(value = ExpertiseUrls.ForwardingAction,method = RequestMethod.POST)
    public String confirmApplication(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "logId")Integer logId,
            @RequestParam(name = "performerId")Integer performerId,
            @RequestParam(name = "comment")String commentStr
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ForwardingList;
        }

        User performer = userService.findById(performerId);
        if (performer== null){
            return "redirect:" + ExpertiseUrls.ForwardingList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        LogStatus performerStatus = LogStatus.Initial;
        if(regApplicationLog.getStatus().equals(LogStatus.Resend)){
            performerStatus = LogStatus.Resend;
        }
        regApplicationLogService.update(regApplicationLog, LogStatus.Approved, commentStr, user.getId());

        RegApplicationLog regApplicationLogCreate = regApplicationLogService.create(regApplication, LogType.Performer, commentStr,user);
        regApplicationLogService.update(regApplicationLogCreate, performerStatus, commentStr, performer.getId());

        regApplication.setStatus(RegApplicationStatus.Process);
        regApplication.setPerformerId(performerId);
        regApplication.setPerformerLogId(regApplicationLogCreate.getId());
        regApplicationService.update(regApplication);

        Comment comment = null;
        if(StringUtils.trimToNull(commentStr) != null){
            comment = commentService.create(id, CommentType.CONFIDENTIAL, commentStr, user.getId());
        }
        if(regApplicationLog.getDocumentFiles()!=null){
            if(comment == null){
                comment = commentService.create(id, CommentType.CONFIDENTIAL, "", user.getId());
            }
            Set<File> fileSet = new LinkedHashSet<>();
            for (File file:regApplicationLog.getDocumentFiles()){
                fileSet.add(file);
            }
            comment.setDocumentFiles(fileSet);
            commentService.updateComment(comment);
        }

        notificationService.create(
                performerId,
                NotificationType.Expertise,
                "sys_notification.performerNewApp",
                id,
                "sys_notification_message.for_perform",
                "/expertise/performer/view/?id=" + id,
                user.getId());
        Client client = clientService.getById(regApplication.getApplicantId());
        smsSendService.sendSMS(client.getPhone(), "Arizangiz ko'rib chiqish uchun qabul qilindi, ariza raqami " + regApplication.getId(), regApplication.getId(), client.getName());

        return "redirect:" + ExpertiseUrls.ForwardingView + "?id=" + regApplication.getId() + "#action";
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
        if (agreementUser == null){
            result.put("status", "2");
            return result;
        }

        RegApplicationLog regApplicationLogCreate = regApplicationLogService.create(regApplication, LogType.Agreement,"", user);
        regApplicationLogService.update(regApplicationLogCreate, LogStatus.Initial,"", agreementUser.getId());

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
