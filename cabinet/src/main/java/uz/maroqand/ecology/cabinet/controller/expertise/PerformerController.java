package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.constant.user.ToastrType;
import uz.maroqand.ecology.core.dto.expertise.*;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.SmsSendService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.ToastrService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz) Ijrochi
 * (ru)
 */
@Controller
public class PerformerController {

    private final RegApplicationService regApplicationService;
    private final ClientService clientService;
    private final UserService userService;
    private final HelperService helperService;
    private final SoatoService soatoService;
    private final InvoiceService invoiceService;
    private final FileService fileService;
    private final ActivityService activityService;
    private final ObjectExpertiseService objectExpertiseService;
    private final RegApplicationLogService regApplicationLogService;
    private final ProjectDeveloperService projectDeveloperService;
    private final ChangeDeadlineDateService changeDeadlineDateService;
    private final CommentService commentService;
    private final CoordinateService coordinateService;
    private final ToastrService toastrService;
    private final NotificationService notificationService;
    private final ConclusionService conclusionService;
    private final SmsSendService smsSendService;

    @Autowired
    public PerformerController(
            RegApplicationService regApplicationService,
            ClientService clientService,
            UserService userService,
            HelperService helperService,
            SoatoService soatoService,
            InvoiceService invoiceService,
            FileService fileService,
            ActivityService activityService,
            ObjectExpertiseService objectExpertiseService,
            RegApplicationLogService regApplicationLogService,
            ProjectDeveloperService projectDeveloperService,
            ChangeDeadlineDateService changeDeadlineDateService,
            CommentService commentService,
            CoordinateService coordinateService,
            ToastrService toastrService,
            NotificationService notificationService,
            ConclusionService conclusionService,
            SmsSendService smsSendService
    ) {
        this.regApplicationService = regApplicationService;
        this.clientService = clientService;
        this.userService = userService;
        this.helperService = helperService;
        this.soatoService = soatoService;
        this.invoiceService = invoiceService;
        this.fileService = fileService;
        this.activityService = activityService;
        this.objectExpertiseService = objectExpertiseService;
        this.regApplicationLogService = regApplicationLogService;
        this.projectDeveloperService = projectDeveloperService;
        this.changeDeadlineDateService = changeDeadlineDateService;
        this.commentService = commentService;
        this.coordinateService = coordinateService;
        this.toastrService = toastrService;
        this.notificationService = notificationService;
        this.conclusionService = conclusionService;
        this.smsSendService = smsSendService;
    }

    @RequestMapping(ExpertiseUrls.PerformerList)
    public String getPerformerListPage(Model model){
        List<LogStatus> logStatusList = new ArrayList<>();
        logStatusList.add(LogStatus.Initial);
        logStatusList.add(LogStatus.Resend);
        logStatusList.add(LogStatus.Modification);
        logStatusList.add(LogStatus.Denied);
        logStatusList.add(LogStatus.Approved);

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList", logStatusList);
        return ExpertiseTemplates.PerformerList;
    }

    @RequestMapping(value = ExpertiseUrls.PerformerListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getPerformerListAjax(
            FilterDto filterDto,
            Pageable pageable
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String,Object> result = new HashMap<>();

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                filterDto,
                user.getOrganizationId(),
                LogType.Performer,
                user.getId(),
                null,
                null,//todo shart kerak
                pageable
        );

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            Client client = clientService.getById(regApplication.getApplicantId());
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
            convenientForJSONArray.add(new Object[]{
                    regApplication.getId(),
                    client.getTin(),
                    client.getName(),
                    regApplication.getMaterials() != null ?helperService.getMaterialShortNames(regApplication.getMaterials(),locale):"",
                    regApplication.getCategory() != null ?helperService.getCategory(regApplication.getCategory().getId(),locale):"",
                    regApplication.getRegistrationDate() != null ? Common.uzbekistanDateFormat.format(regApplication.getRegistrationDate()):"",
                    regApplication.getDeadlineDate() != null ?Common.uzbekistanDateFormat.format(regApplication.getDeadlineDate()):"",
                    regApplication.getAgreementStatus() != null ? helperService.getTranslation(regApplication.getAgreementStatus().getAgreementName(),locale):"",
                    regApplication.getAgreementStatus() != null ? regApplication.getAgreementStatus().getId():"",
                    regApplicationLog.getStatus() != null ? helperService.getTranslation(regApplicationLog.getStatus().getPerformerName(), locale):"",
                    regApplicationLog.getStatus() != null ? regApplicationLog.getStatus().getId():""
            });
        }

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.PerformerView)
    public String getPerformerViewPage(
            @RequestParam(name = "id")Integer regApplicationId,
            Model model
    ) {
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.PerformerList;
        }
        clientService.clientView(regApplication.getApplicantId(), model);
        coordinateService.coordinateView(regApplicationId, model);

        if (regApplication.getAgreementStatus() != null && regApplication.getAgreementStatus().equals(LogStatus.Denied)){
            model.addAttribute("performerLog", regApplicationLogService.getById(regApplication.getPerformerLogIdNext()));
            model.addAttribute("action_url", ExpertiseUrls.PerformerActionEdit);
        } else {
            model.addAttribute("performerLog", regApplicationLogService.getById(regApplication.getPerformerLogId()));
            model.addAttribute("action_url", ExpertiseUrls.PerformerAction);
        }
        System.out.println(" performer regApplicationID==" + regApplication.getId());
        Conclusion conclusion = conclusionService.getById(regApplication.getConclusionId());
        model.addAttribute("conclusionId", conclusion!=null?conclusion.getId():0);
        model.addAttribute("conclusionText", conclusion!=null?conclusion.getHtmlText():"");
        model.addAttribute("conclusion", conclusion);

        model.addAttribute("chatList", commentService.getByRegApplicationIdAndType(regApplication.getId(), CommentType.CHAT));
        model.addAttribute("changeDeadlineDateList", changeDeadlineDateService.getListByRegApplicationId(regApplicationId));
        model.addAttribute("changeDeadlineDate", changeDeadlineDateService.getByRegApplicationId(regApplicationId));

        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("invoice", invoiceService.getInvoice(regApplication.getInvoiceId()));

        model.addAttribute("lastCommentList", commentService.getByRegApplicationIdAndType(regApplication.getId(), CommentType.CONFIDENTIAL));
        model.addAttribute("agreementLogList", regApplicationLogService.getByIds(regApplication.getAgreementLogs()));
        model.addAttribute("agreementCompleteLog", regApplicationLogService.getById(regApplication.getAgreementCompleteLogId()));
        model.addAttribute("regApplicationLogList", regApplicationLogService.getByRegApplicationId(regApplication.getId()));
        return ExpertiseTemplates.PerformerView;
    }

    @RequestMapping(value = ExpertiseUrls.PerformerAction,method = RequestMethod.POST)
    public String confirmApplication(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "comment")String comment,
            @RequestParam(name = "number")String number,
            @RequestParam(name = "date")String dateStr,
            @RequestParam(name = "performerStatus")Integer performerStatus,
            @RequestParam(name = "conclusionOnline")Boolean conclusionOnline
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.PerformerList;
        }

        if (!regApplication.getPerformerId().equals(user.getId())){
            toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Sizda ariza ijrochi uchun ruxsat yo'q.");
            return "redirect:" + ExpertiseUrls.PerformerList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
        regApplicationLogService.update(regApplicationLog, LogStatus.getLogStatus(performerStatus), comment, user.getId());
        if(StringUtils.trimToNull(comment) != null){
            commentService.create(id, CommentType.CONFIDENTIAL, comment, user.getId());
        }

        regApplication.setStatus(RegApplicationStatus.Process);
        regApplication.setAgreementStatus(LogStatus.Initial);
        regApplication.setConclusionOnline(conclusionOnline);
        regApplicationService.update(regApplication);

        Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());
        if (conclusion!=null){
            conclusion.setNumber(number);
            conclusion.setDate(DateParser.TryParse(dateStr,Common.uzbekistanDateFormat));
            conclusionService.save(conclusion);
        }

        //kelishiluvchilar bor bo'lsa yuboramiz
        Set<Integer> agreements = new LinkedHashSet<>();
        List<RegApplicationLog> agreementLogs = regApplicationLogService.getByIds(regApplication.getAgreementLogs());
        for (RegApplicationLog agreementLog:agreementLogs){
            if(agreementLog.getUpdateById()!=null) agreements.add(agreementLog.getUpdateById());
            agreementLog.setStatus(LogStatus.New);
            agreementLog.setShow(true);
            agreementLog.setSendAt(new Date());
            regApplicationLogService.updateDocument(agreementLog);

            notificationService.create(
                    agreementLog.getUpdateById(),
                    NotificationType.Expertise,
                    "sys_notification.newAppAgreement",//kelishish uchun yuborildi
                    id ,
                     "sys_notification_message.new",
                    "/expertise/agreement/view?id=" + id + "&logId=" + agreementLog.getId(),
                    user.getId()
            );
        }

        //agar kelishiuvchiga ushbu ariza oldinxam yuborilgan bo'lsa, ularni show=false qilamiz(bitta ariza ikki marta chiqib qolmasligi uchun)
        if(agreements.size()>0){
            List<RegApplicationLog> agreementLogList = regApplicationLogService.getByRegApplicationIdAndType(regApplication.getId(), LogType.Agreement);
            for (RegApplicationLog agreementLog:agreementLogList){
                if(agreementLog.getUpdateById()!=null && agreements.contains(agreementLog.getUpdateById()) && (agreementLog.getStatus().equals(LogStatus.Denied) || agreementLog.getStatus().equals(LogStatus.Approved))){
                    agreementLog.setShow(false);
                    regApplicationLogService.updateDocument(agreementLog);
                }
            }
        }

        if(agreementLogs.size()==0){
            RegApplicationLog regApplicationLogCreate = regApplicationLogService.create(regApplication,LogType.AgreementComplete,comment,user);
            regApplication.setAgreementCompleteLogId(regApplicationLogCreate.getId());
            regApplication.setStatus(RegApplicationStatus.Process);
            regApplicationService.update(regApplication);
        }
        return "redirect:"+ExpertiseUrls.PerformerView + "?id=" + regApplication.getId() + "#action";
    }

    @RequestMapping(value = ExpertiseUrls.PerformerActionEdit,method = RequestMethod.POST)
    public String getPerformerActionEditMethod(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "comment")String comment,
            @RequestParam(name = "number")String number,
            @RequestParam(name = "performerStatus")Integer performerStatus,
            @RequestParam(name = "conclusionOnline")Boolean conclusionOnline
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.PerformerList;
        }

        if (!regApplication.getPerformerId().equals(user.getId())){
            toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Sizda ariza ijrochi uchun ruxsat yo'q.");
            return "redirect:" + ExpertiseUrls.PerformerList;
        }

        if (regApplication.getAgreementStatus()==null || !regApplication.getAgreementStatus().equals(LogStatus.Denied)){
            toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Kelishish yoki tasdiqlash jarayonida rad javobi berilganligi xaqida ma'lumot topilmadi.");
            return "redirect:" + ExpertiseUrls.PerformerList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getPerformerLogIdNext());
        regApplicationLogService.update(regApplicationLog, LogStatus.getLogStatus(performerStatus), comment, user.getId());
        if(StringUtils.trimToNull(comment) != null){
            commentService.create(id, CommentType.CONFIDENTIAL, comment, user.getId());
        }

        regApplication.setStatus(RegApplicationStatus.Process);
        regApplication.setAgreementStatus(LogStatus.Initial);
        regApplication.setPerformerLogId(regApplicationLog.getId());
        regApplication.setConclusionOnline(conclusionOnline);

        Set<Integer> agreementLogs = regApplication.getAgreementLogs();
        List<RegApplicationLog> agreementLogList = regApplicationLogService.getByIds(regApplication.getAgreementLogs());
        for (RegApplicationLog agreementLog :agreementLogList){
            if(!agreementLog.getStatus().equals(LogStatus.Initial)){
                RegApplicationLog regApplicationLogCreate = regApplicationLogService.create(regApplication, LogType.Agreement,"", user);
                regApplicationLogCreate.setShow(true);
                regApplicationLogCreate.setSendAt(new Date());
                regApplicationLogService.update(regApplicationLogCreate, LogStatus.New,"", agreementLog.getUpdateById());

                agreementLog.setShow(false);
                regApplicationLogService.updateDocument(agreementLog);

                agreementLogs.remove(agreementLog.getId());
                agreementLogs.add(regApplicationLogCreate.getId());
            }else {
                agreementLog.setShow(true);
                agreementLog.setSendAt(new Date());
                agreementLog.setStatus(LogStatus.New);
                agreementLog.setIndex(regApplication.getLogIndex());
                regApplicationLogService.updateDocument(agreementLog);
            }

            notificationService.create(
                    agreementLog.getUpdateById(),
                    NotificationType.Expertise,
                    "sys_notification.newAppAgreement",
                    id,
                    "sys_notification_message.new",
                    "/expertise/agreement/view?id=" + id + "&logId=" + agreementLog.getId(),
                    user.getId()
            );
        }
        regApplication.setAgreementLogs(agreementLogs);

        if(agreementLogs.size()==0){
            RegApplicationLog regApplicationLogCreate = regApplicationLogService.create(regApplication,LogType.AgreementComplete,comment,user);
            regApplication.setAgreementCompleteLogId(regApplicationLogCreate.getId());
            regApplication.setStatus(RegApplicationStatus.Process);
        }else {
            regApplication.setAgreementCompleteLogId(null);
        }
        regApplicationService.update(regApplication);

        Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplication.getId());
        if (conclusion!=null){
            conclusion.setNumber(number);
            conclusionService.save(conclusion);
        }

        return "redirect:"+ExpertiseUrls.PerformerView + "?id=" + regApplication.getId() + "#action";
    }

    @RequestMapping(ExpertiseUrls.PerformerConclusionSave)
    @ResponseBody
    public HashMap<String,Object> saveConclusion(
            @RequestParam(name = "regApplicationId") Integer regApplicationId,
            @RequestParam(name = "htmlText") String htmlText
    ){
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();

        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null || !regApplication.getPerformerId().equals(user.getId())){
            result.put("status",1);
            return result;
        }

        Conclusion conclusion = null;
        if(regApplication.getConclusionId() != null){
            conclusion = conclusionService.getById(regApplication.getConclusionId());
        }
        htmlText = htmlText.replaceAll("figure","div");
        htmlText = htmlText.replaceAll("svg","div");
//        htmlText = htmlText.replaceAll("figcaption","div");

        if(conclusion == null){
            conclusion = conclusionService.create(regApplicationId, htmlText.trim(), user.getId());
        }else {
            conclusion = conclusionService.update(conclusion, htmlText.trim(), user.getId());
        }

        regApplication.setConclusionId(conclusion.getId());
        regApplicationService.update(regApplication);

        result.put("status",1);
        return result;
    }

    @RequestMapping(ExpertiseUrls.PerformerChangeDeadlineDate)
    @ResponseBody
    public HashMap<String,Object> getMethod(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "reason") String reason
    ){
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();
        Integer status=1;
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            status=0;
            result.put("status",status);
            return result;
        }

        ChangeDeadlineDate changeDeadlineDate = new ChangeDeadlineDate();
        changeDeadlineDate.setRegApplicationId(id);
        changeDeadlineDate.setBeforeDeadlineDate(regApplication.getDeadlineDate());
        changeDeadlineDate.setReason(reason);
        changeDeadlineDate.setStatus(ChangeDeadlineDateStatus.Initial);
        changeDeadlineDateService.save(changeDeadlineDate);

        result.put("status",status);
        result.put("beforeDate",regApplication.getDeadlineDate()!=null?Common.uzbekistanDateFormat.format(regApplication.getDeadlineDate()):"");
        return result;

    }

    @RequestMapping(value = ExpertiseUrls.CommentAdd)
    @ResponseBody
    public HashMap<String,Object> addComment(
            @RequestParam(name = "id", required = false) Integer commentId,
            @RequestParam(name = "regApplicationId") Integer regApplicationId,
            @RequestParam(name = "message") String message
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String,Object> result = new HashMap<>();
        Comment comment;
        if (commentId != null){
            comment = commentService.getById(commentId);
            if (comment == null){
                result.put("status",0);
                return result;
            }
            comment.setMessage(message);
            comment = commentService.updateComment(comment);
        }else {
            comment = commentService.create(regApplicationId, CommentType.CHAT, message, user.getId());
        }

        result.put("status",1);
        result.put("message",message);
        result.put("createdAt",Common.uzbekistanDateFormat.format(comment.getCreatedAt()));
        result.put("userShorName",helperService.getUserLastAndFirstShortById(user.getId()));
        result.put("commentFiles",comment.getDocumentFiles()!=null && comment.getDocumentFiles().size()>0?comment.getDocumentFiles():"");
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        Client client = clientService.getById(regApplication.getApplicantId());
        notificationService.create(
                regApplication.getCreatedById(),
                NotificationType.Expertise,
                "sys_notification.new",
                regApplicationId,
                "sys_notification_message.send",
                "/reg/application/resume?id=" + regApplicationId,
                user.getId()
        );
        smsSendService.sendSMS(client.getPhone(), " Arinangiz bo'yicha xabar mavjud, ariza raqami "+regApplication.getId(), regApplication.getId(), client.getName());
        return result;
    }

    @RequestMapping(value = ExpertiseUrls.CommentFileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "id",required = false) Integer id,
            @RequestParam(name = "regApplicationId") Integer regApplicationId,
            @RequestParam(name = "file") MultipartFile multipartFile,
            @RequestParam(name = "file_name") String fileName
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        System.out.println("keldi");
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }

        Comment comment;
        if (id!=null){
            comment = commentService.getById(id);
            if (comment==null || !comment.getRegApplicationId().equals(regApplicationId)){
                responseMap.put("message", "Object not found.");
                return responseMap;
            }
        }else{
            comment = commentService.create(regApplicationId, CommentType.CHAT, "", user.getId());
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
            responseMap.put("link", ExpertiseUrls.CommentFileDownload + "?file_id=" + file.getId() + "&comment_id=" + comment.getId());
            responseMap.put("fileId", file.getId());
            responseMap.put("commentId", comment.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    @RequestMapping(ExpertiseUrls.CommentFileDownload)
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

    @RequestMapping(value = ExpertiseUrls.CommentFileDelete, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> deleteFile(
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

}
