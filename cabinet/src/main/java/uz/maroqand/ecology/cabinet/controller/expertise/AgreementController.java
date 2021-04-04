package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
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
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.constant.user.ToastrType;
import uz.maroqand.ecology.core.dto.expertise.*;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.ToastrService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz) Kelishish
 * (ru)
 */
@Controller
public class AgreementController {

    private final RegApplicationService regApplicationService;
    private final ClientService clientService;
    private final SoatoService soatoService;
    private final UserService userService;
    private final ActivityService activityService;
    private final ObjectExpertiseService objectExpertiseService;
    private final CommentService commentService;
    private final HelperService helperService;
    private final RegApplicationLogService regApplicationLogService;
    private final InvoiceService invoiceService;
    private final ProjectDeveloperService projectDeveloperService;
    private final CoordinateService coordinateService;
    private final ToastrService toastrService;
    private final ConclusionService conclusionService;
    private final NotificationService notificationService;
    private final FileService fileService;
    private final RegApplicationCategoryFourAdditionalService regApplicationCategoryFourAdditionalService;

    @Autowired
    public AgreementController(
            RegApplicationService regApplicationService,
            SoatoService soatoService,
            UserService userService,
            ClientService clientService,
            ActivityService activityService,
            ObjectExpertiseService objectExpertiseService,
            CommentService commentService,
            HelperService helperService,
            RegApplicationLogService regApplicationLogService,
            InvoiceService invoiceService,
            ProjectDeveloperService projectDeveloperService,
            CoordinateService coordinateService,
            ToastrService toastrService,
            ConclusionService conclusionService,
            NotificationService notificationService,
            FileService fileService, RegApplicationCategoryFourAdditionalService regApplicationCategoryFourAdditionalService){
        this.regApplicationService = regApplicationService;
        this.soatoService = soatoService;
        this.userService = userService;
        this.clientService = clientService;
        this.activityService = activityService;
        this.objectExpertiseService = objectExpertiseService;
        this.commentService = commentService;
        this.helperService = helperService;
        this.regApplicationLogService = regApplicationLogService;
        this.invoiceService = invoiceService;
        this.projectDeveloperService = projectDeveloperService;
        this.coordinateService = coordinateService;
        this.toastrService = toastrService;
        this.conclusionService = conclusionService;
        this.notificationService = notificationService;
        this.fileService = fileService;
        this.regApplicationCategoryFourAdditionalService = regApplicationCategoryFourAdditionalService;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementList)
    public String getConfirmListPage(Model model) {
        List<LogStatus> logStatusList = new ArrayList<>();
        logStatusList.add(LogStatus.New);
        logStatusList.add(LogStatus.Denied);
        logStatusList.add(LogStatus.Approved);

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList", logStatusList);
        return ExpertiseTemplates.AgreementList;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getPerformerListAjax(
            FilterDto filterDto,
            Pageable pageable
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String,Object> result = new HashMap<>();

        Page<RegApplicationLog> regApplicationLogs = regApplicationLogService.findFiltered(
                filterDto,
                null,
                userService.isAdmin() ? null : user.getId(),
                LogType.Agreement,
                null,
                pageable
        );

        List<RegApplicationLog> regApplicationLogList = regApplicationLogs.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationLogList.size());
        for (RegApplicationLog regApplicationLog : regApplicationLogList){
            RegApplication regApplication = null;
            if (regApplicationLog.getRegApplicationId()!=null){
                regApplication = regApplicationService.getById(regApplicationLog.getRegApplicationId());
            }
            Client client = null;
            if (regApplication!=null && regApplication.getApplicantId()!=null){
                client = clientService.getById(regApplication.getApplicantId());
            }
            RegApplicationLog performerLog = null;
            if (regApplication!=null && regApplicationLog.getIndex()!=null){
                performerLog = regApplicationLogService.getByIndex(regApplication.getId(), LogType.Performer, regApplicationLog.getIndex());
            }

            convenientForJSONArray.add(new Object[]{
                    regApplication!=null?regApplication.getId():"",
                    client!=null?client.getTin():"",
                    client!=null?client.getName():"",
                    regApplication!=null && regApplication.getMaterials() != null ?helperService.getMaterialShortNames(regApplication.getMaterials(),locale):"",
                    regApplication!=null && regApplication.getCategory() != null ?helperService.getCategory(regApplication.getCategory().getId(),locale):"",
                    regApplication!=null && regApplication.getRegistrationDate() != null ? Common.uzbekistanDateFormat.format(regApplication.getRegistrationDate()):"",
                    regApplication!=null && regApplication.getDeadlineDate() != null ? Common.uzbekistanDateFormat.format(regApplication.getDeadlineDate()):"",
                    performerLog != null && performerLog.getStatus() != null ? helperService.getTranslation(performerLog.getStatus().getPerformerName(), locale) : "",
                    performerLog != null && performerLog.getStatus() != null ? performerLog.getStatus().getId() : "",
                    regApplicationLog.getStatus() != null ? helperService.getTranslation(regApplicationLog.getStatus().getAgreementName(),locale) : "",
                    regApplicationLog.getStatus() != null ? regApplicationLog.getStatus().getId() : "",
                    regApplicationLog.getId(),
                    regApplication!=null?regApplicationService.beforeOrEqualsTrue(regApplication):null
            });
        }

        result.put("recordsTotal", regApplicationLogs.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationLogs.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.AgreementView)
    public String getAgreementViewPage(
            @RequestParam(name = "id")Integer regApplicationId,
            @RequestParam(name = "logId")Integer logId,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.AgreementList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        if (!userService.isAdmin()){
        if(
            !regApplicationLog.getRegApplicationId().equals(regApplicationId) ||
            !regApplicationLog.getUpdateById().equals(user.getId()) ||
            !regApplicationLog.getType().equals(LogType.Agreement)
        ){
            toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Kelishish bo'yicha ariza topilmadi");
            return "redirect:" + ExpertiseUrls.AgreementList;
        }}

        clientService.clientView(regApplication.getApplicantId(), model);
        coordinateService.coordinateView(regApplicationId, model);

        RegApplicationLog performerLog = regApplicationLogService.getByIndex(regApplication.getId(), LogType.Performer, regApplicationLog.getIndex());
        RegApplicationLog agreementCompleteLog = regApplicationLogService.getByIndex(regApplication.getId(), LogType.AgreementComplete, regApplicationLog.getIndex());
        List<RegApplicationLog> agreementLogList = regApplicationLogService.getAllByIndex(regApplication.getId(), LogType.Agreement, regApplicationLog.getIndex());


        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = null;
        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        }
        model.addAttribute("regApplicationCategoryFourAdditional", regApplicationCategoryFourAdditional);

        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("invoice", invoiceService.getInvoice(regApplication.getInvoiceId()));
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("conclusion", conclusionService.getByRegApplicationIdLast(regApplicationId));
        model.addAttribute("regApplicationLog", regApplicationLog);

        model.addAttribute("lastCommentList", commentService.getByRegApplicationIdAndType(regApplication.getId(), CommentType.CONFIDENTIAL));
        model.addAttribute("performerLog", performerLog);
        model.addAttribute("agreementLogList", agreementLogList);
        model.addAttribute("agreementCompleteLog", agreementCompleteLog);
        model.addAttribute("regApplicationLogList", regApplicationLogService.getByRegApplicationId(regApplication.getId()));
        return ExpertiseTemplates.AgreementView;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementAction,method = RequestMethod.POST)
    public String confirmApplication(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "logId")Integer logId,
            @RequestParam(name = "agreementStatus")Integer agreementStatus,
            @RequestParam(name = "comment")String comment
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.AgreementList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        regApplicationLogService.update(regApplicationLog, LogStatus.getLogStatus(agreementStatus), comment, user.getId());
        if(StringUtils.trimToNull(comment) != null){
            commentService.create(id, CommentType.CONFIDENTIAL, comment, user.getId());
        }

        Boolean createAgreementComplete = true;
        List<RegApplicationLog> agreementLogList = regApplicationLogService.getByIds(regApplication.getAgreementLogs());
        for(RegApplicationLog agreementLog :agreementLogList){
            if(!agreementLog.getStatus().equals(LogStatus.Approved)){
                createAgreementComplete = false;
            }
        }
        if(createAgreementComplete){
            //yangi log ni ko'rsatamiz
            RegApplicationLog regApplicationLogCreate = regApplicationLogService.create(regApplication, LogType.AgreementComplete, comment, user);
            regApplication.setAgreementCompleteLogId(regApplicationLogCreate.getId());
            regApplication.setStatus(RegApplicationStatus.Process);
            regApplicationService.update(regApplication);
        }

        if(agreementStatus.equals(LogStatus.Denied.getId())){
            for(RegApplicationLog agreementLog :agreementLogList){
                if(agreementLog.getStatus().equals(LogStatus.New)){
                    regApplicationLogService.update(agreementLog, LogStatus.Initial, agreementLog.getComment(), agreementLog.getUpdateById());
                }
            }

            regApplication.setAgreementStatus(LogStatus.Denied);
            regApplication.setLogIndex(regApplication.getLogIndex()+1);
            RegApplicationLog performerLogNext = regApplicationLogService.create(regApplication, LogType.Performer, comment, user);
            performerLogNext.setUpdateById(regApplicationLog.getCreatedById());
            regApplicationLogService.updateDocument(performerLogNext);
            regApplication.setPerformerLogIdNext(performerLogNext.getId());
            regApplicationService.update(regApplication);

            notificationService.create(
                    regApplication.getPerformerId(),
                    NotificationType.Expertise,
                    helperService.getTranslation("sys_notification.performerInfo", locale),
                    regApplication.getId(),
                    "sys_notification_message.performer_not_confirm",
                    "/reg/application/resume?id=" + regApplication.getId(),
                    user.getId()
            );
        }

        return "redirect:"+ExpertiseUrls.AgreementView + "?id=" + regApplication.getId() + "&logId=" + logId + "#result";
    }
}
