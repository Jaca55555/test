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
import uz.maroqand.ecology.core.dto.expertise.*;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.SmsSendService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz) Oxirgi kelishishuv
 * (ru)
 */
@Controller
public class AgreementCompleteController {

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
    private final ConclusionService conclusionService;
    private final NotificationService notificationService;
    private final SmsSendService smsSendService;
    private final FileService fileService;
    private final RegApplicationCategoryFourAdditionalService regApplicationCategoryFourAdditionalService;

    @Autowired
    public AgreementCompleteController(
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
            ConclusionService conclusionService,
            NotificationService notificationService,
            SmsSendService smsSendService,
            FileService fileService, RegApplicationCategoryFourAdditionalService regApplicationCategoryFourAdditionalService) {
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
        this.conclusionService = conclusionService;
        this.notificationService = notificationService;
        this.smsSendService = smsSendService;
        this.fileService = fileService;
        this.regApplicationCategoryFourAdditionalService = regApplicationCategoryFourAdditionalService;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementCompleteList)
    public String getConfirmListPage(Model model) {
        List<LogStatus> logStatusList = new ArrayList<>();
        logStatusList.add(LogStatus.Initial);
        logStatusList.add(LogStatus.Denied);
        logStatusList.add(LogStatus.Approved);

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList", logStatusList);
        return ExpertiseTemplates.AgreementCompleteList;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementCompleteListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getAgreementCompleteListAjax(
            FilterDto filterDto,
            Pageable pageable
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String,Object> result = new HashMap<>();

        /*Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                filterDto,
                user.getOrganizationId(),
                LogType.AgreementComplete,
                null,
                null,
                null,//todo shart kerak
                pageable
        );*/

        System.out.println(filterDto);
        Page<RegApplicationLog> regApplicationLogPage = regApplicationLogService.findFiltered(
                filterDto,
                null,
                null,
                LogType.AgreementComplete,
                null,
                pageable
        );

        List<RegApplicationLog> regApplicationLogList = regApplicationLogPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationLogList.size());
        for (RegApplicationLog agreementCompleteLog : regApplicationLogList){
            RegApplication regApplication = null;
            if (agreementCompleteLog.getRegApplicationId()!=null){
                regApplication = regApplicationService.getById(agreementCompleteLog.getRegApplicationId());
            }
            Client client =null;
            if (regApplication!=null && regApplication.getApplicantId()!=null){
                client = clientService.getById(regApplication.getApplicantId());
            }
            RegApplicationLog performerLog = null;
            if (regApplication!=null && agreementCompleteLog.getIndex()!=null){
                performerLog = regApplicationLogService.getByIndex(regApplication.getId(), LogType.Performer, agreementCompleteLog.getIndex());
            }


            convenientForJSONArray.add(new Object[]{
                    regApplication!=null?regApplication.getId():"",
                    client!=null?client.getTin():"",
                    client!=null?client.getName():"",
                    regApplication!=null && regApplication.getMaterials() != null ?helperService.getMaterialShortNames(regApplication.getMaterials(),locale):"",
                    regApplication!=null && regApplication.getCategory() != null ?helperService.getCategory(regApplication.getCategory().getId(),locale):"",
                    regApplication!=null && regApplication.getRegistrationDate() != null ? Common.uzbekistanDateFormat.format(regApplication.getRegistrationDate()):"",
                    regApplication!=null && regApplication.getDeadlineDate() != null ?Common.uzbekistanDateFormat.format(regApplication.getDeadlineDate()):"",
                    (performerLog!=null && performerLog.getStatus() != null ) ? helperService.getTranslation(performerLog.getStatus().getPerformerName(), locale):"",
                    (performerLog!=null && performerLog.getStatus() != null ) ? performerLog.getStatus().getId():"",
                    agreementCompleteLog.getStatus() !=null ? helperService.getTranslation(agreementCompleteLog.getStatus().getAgreementName(), locale):"",
                    agreementCompleteLog.getStatus() !=null ? agreementCompleteLog.getStatus().getId():"",
                    agreementCompleteLog.getId(),
                    regApplication!=null?regApplicationService.beforeOrEqualsTrue(regApplication):null
            });
        }

        result.put("recordsTotal", regApplicationLogPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationLogPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.AgreementCompleteView)
    public String getAgreementCompleteViewPage(
            @RequestParam(name = "id")Integer logId,
            Model model
    ) {
        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        Integer regApplicationId = regApplicationLog.getRegApplicationId();
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.AgreementCompleteList;
        }

        clientService.clientView(regApplication.getApplicantId(), model);
        coordinateService.coordinateView(regApplicationId, model);
        System.out.println(" complete regApplicationID==" + regApplication.getId());
        model.addAttribute("invoice",invoiceService.getInvoice(regApplication.getInvoiceId()));
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("conclusion", conclusionService.getByRegApplicationIdLast(regApplicationId));
        model.addAttribute("regApplicationLog",regApplicationLog);

        RegApplicationLog performerLog = regApplicationLogService.getByIndex(regApplication.getId(), LogType.Performer, regApplicationLog.getIndex());
        List<RegApplicationLog> agreementLogList = regApplicationLogService.getAllByIndex(regApplication.getId(), LogType.Agreement, regApplicationLog.getIndex());

        RegApplicationCategoryFourAdditional regApplicationCategoryFourAdditional = null;
        if (regApplication.getRegApplicationCategoryType()!=null && regApplication.getRegApplicationCategoryType().equals(RegApplicationCategoryType.fourType)){
            regApplicationCategoryFourAdditional = regApplicationCategoryFourAdditionalService.getByRegApplicationId(regApplication.getId());
        }
        model.addAttribute("regApplicationCategoryFourAdditional", regApplicationCategoryFourAdditional);

        model.addAttribute("lastCommentList", commentService.getByRegApplicationIdAndType(regApplication.getId(), CommentType.CONFIDENTIAL));
        model.addAttribute("performerLog", performerLog);
        model.addAttribute("agreementLogList", agreementLogList);
        model.addAttribute("agreementCompleteLog", regApplicationLog);
        model.addAttribute("regApplicationLogList", regApplicationLogService.getByRegApplicationId(regApplication.getId()));
        return ExpertiseTemplates.AgreementCompleteView;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementCompleteAction,method = RequestMethod.POST)
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
            return "redirect:" + ExpertiseUrls.AgreementCompleteList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        regApplicationLogService.update(regApplicationLog, LogStatus.getLogStatus(agreementStatus), comment, user.getId());
        if(StringUtils.trimToNull(comment) != null){
            commentService.create(id, CommentType.CONFIDENTIAL, comment, user.getId());
        }

        if(agreementStatus.equals(LogStatus.Approved.getId())){
            RegApplicationLog conclusionLog = regApplicationLogService.create(regApplication,LogType.ConclusionComplete,"xulosaga sana va number qo'yish",user);
            conclusionLog.setShow(true);
            regApplicationLogService.update(conclusionLog,LogStatus.Initial,"",user.getId());
           /* RegApplicationLog performerLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
            switch (performerLog.getStatus()){
                case Modification: regApplication.setStatus(RegApplicationStatus.Modification); break;
                case Approved: regApplication.setStatus(RegApplicationStatus.Approved); break;
                case Denied: regApplication.setStatus(RegApplicationStatus.NotConfirmed); break;
            }*/
            regApplication.setConclusionCompleteLogId(conclusionLog.getId());
            regApplication.setAgreementStatus(LogStatus.Approved);
            regApplicationService.update(regApplication);

//            conclusionService.complete(regApplication.getConclusionId());

          /*  notificationService.create(
                    regApplication.getCreatedById(),
                    NotificationType.Expertise,
                    "sys_notification.new",
                    regApplication.getId(),
                    "sys_notification_message.finished",
                    "/reg/application/resume?id=" + regApplication.getId(),
                    user.getId()
            );
            notificationService.create(
                    regApplication.getPerformerId(),
                    NotificationType.Expertise,
                    "sys_notification.performerInfo",
                    regApplication.getId(),
                     "sys_notification_message.performer_confirm",
                    "/reg/application/resume?id=" + regApplication.getId(),
                    user.getId()
            );*/
          /*  Client client = clientService.getById(regApplication.getApplicantId());
            smsSendService.sendSMS(client.getPhone(), " Arizangiz ko'rib chiqildi, ariza raqami ", regApplication.getId(), client.getName());*/
        }

        if(agreementStatus.equals(LogStatus.Denied.getId())){
            regApplication.setAgreementStatus(LogStatus.Denied);
            regApplication.setLogIndex(regApplication.getLogIndex()+1);
            RegApplicationLog performerLogNext = regApplicationLogService.create(regApplication, LogType.Performer, comment, user);
            performerLogNext.setUpdateById(regApplication.getPerformerId());
            regApplicationLogService.updateDocument(performerLogNext);
            regApplication.setPerformerLogIdNext(performerLogNext.getId());
            regApplicationService.update(regApplication);

            notificationService.create(
                    regApplication.getPerformerId(),
                    NotificationType.Expertise,
                    "sys_notification.performerInfo",
                    regApplication.getId(),
                    "sys_notification_message.performer_not_confirm",
                    "/reg/application/resume?id=" + regApplication.getId(),
                    user.getId()
            );
        }

        return "redirect:"+ExpertiseUrls.AgreementCompleteView + "?id=" + regApplicationLog.getId() + "#action";
    }

}
