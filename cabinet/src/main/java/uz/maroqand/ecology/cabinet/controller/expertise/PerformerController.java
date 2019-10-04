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
import uz.maroqand.ecology.core.repository.expertise.CoordinateLatLongRepository;
import uz.maroqand.ecology.core.repository.expertise.CoordinateRepository;
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
    private final CoordinateRepository coordinateRepository;
    private final CoordinateService coordinateService;
    private final CoordinateLatLongRepository coordinateLatLongRepository;
    private final ToastrService toastrService;
    private final NotificationService notificationService;
    private final ConclusionService conclusionService;

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
            CoordinateRepository coordinateRepository,
            CoordinateService coordinateService, CoordinateLatLongRepository coordinateLatLongRepository,
            ToastrService toastrService,
            NotificationService notificationService,
            ConclusionService conclusionService) {
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
        this.coordinateRepository = coordinateRepository;
        this.coordinateService = coordinateService;
        this.coordinateLatLongRepository = coordinateLatLongRepository;
        this.toastrService = toastrService;
        this.notificationService = notificationService;
        this.conclusionService = conclusionService;
    }

    @RequestMapping(ExpertiseUrls.PerformerList)
    public String getPerformerListPage(Model model){

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList", LogStatus.getLogStatusList());
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

        Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplicationId);
        model.addAttribute("conclusionId", conclusion!=null?conclusion.getId():0);
        model.addAttribute("conclusionText", conclusion!=null?conclusion.getHtmlText():"");

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
            @RequestParam(name = "performerStatus")Integer performerStatus
    ){
        User user = userService.getCurrentUserFromContext();
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
        regApplicationService.update(regApplication);

        List<RegApplicationLog> agreementLogs = regApplicationLogService.getByIds(regApplication.getAgreementLogs());
        for (RegApplicationLog agreementLog:agreementLogs){
            agreementLog.setStatus(LogStatus.New);
            regApplicationLogService.updateDocument(agreementLog, user);

            notificationService.create(
                    agreementLog.getUpdateById(),
                    NotificationType.Expertise,
                    "Kelishish uchun ariza yuborildi",
                    id + " raqamli ariza kelishish uchun yuborildi.",
                    "/expertise/agreement/view?id=" + id + "&logId=" + agreementLog.getId(),
                    user.getId()
            );
        }
        if(agreementLogs.size()==0){
            RegApplicationLog regApplicationLogCreate = regApplicationLogService.create(regApplication,LogType.AgreementComplete,comment,user);
            regApplication.setAgreementCompleteLogId(regApplicationLogCreate.getId());
            regApplication.setStatus(RegApplicationStatus.Process);
            regApplicationService.update(regApplication);
        }
        return "redirect:"+ExpertiseUrls.PerformerView + "?id=" + regApplication.getId();
    }

    @RequestMapping(value = ExpertiseUrls.PerformerActionEdit,method = RequestMethod.POST)
    public String getPerformerActionEditMethod(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "comment")String comment,
            @RequestParam(name = "performerStatus")Integer performerStatus
    ){
        User user = userService.getCurrentUserFromContext();
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

        Set<Integer> agreementLogs = regApplication.getAgreementLogs();
        List<RegApplicationLog> agreementLogList = regApplicationLogService.getByIds(regApplication.getAgreementLogs());
        for (RegApplicationLog agreementLog :agreementLogList){
            if(!agreementLog.getStatus().equals(LogStatus.Initial)){
                RegApplicationLog regApplicationLogCreate = regApplicationLogService.create(regApplication, LogType.Agreement,"", user);
                regApplicationLogService.update(regApplicationLogCreate, LogStatus.New,"", agreementLog.getUpdateById());

                agreementLogs.remove(agreementLog.getId());
                agreementLogs.add(regApplicationLogCreate.getId());
            }else {
                regApplicationLogService.update(agreementLog, LogStatus.New,"", agreementLog.getUpdateById());
            }

            notificationService.create(
                    agreementLog.getUpdateById(),
                    NotificationType.Expertise,
                    "Kelishish uchun ariza yuborildi",
                    id + " raqamli ariza kelishish uchun yuborildi.",
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

        return "redirect:"+ExpertiseUrls.PerformerView + "?id=" + regApplication.getId();
    }

    @RequestMapping(ExpertiseUrls.PerformerConclusionSave)
    @ResponseBody
    public HashMap<String,Object> saveConclusion(
            @RequestParam(name = "regApplicationId") Integer regApplicationId,
            @RequestParam(name = "conclusionId") Integer conclusionId,
            @RequestParam(name = "htmlText") String htmlText
    ){
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();
        result.put("status",1);
        htmlText = htmlText.replaceAll("figure","div");
        System.out.println("keldi");
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication==null){
            System.out.println("keldi1");
            result.put("status",0);
            return result;
        }
        Conclusion conclusionNew = null;
        if (conclusionId!=0){
            conclusionNew = conclusionService.getByIdAndRegApplicationId(conclusionId,regApplicationId);
            if (conclusionNew==null){
                System.out.println("keldi2");
                result.put("status",0);
                return result;
            }
            conclusionNew.setUpdateAt(new Date());
            conclusionNew.setUpdateById(user.getId());
            conclusionNew.setHtmlText(htmlText.trim());
            conclusionService.save(conclusionNew);
            return result;
        }

        conclusionNew = new Conclusion();
        conclusionNew.setRegApplicationId(regApplicationId);
        conclusionNew.setCreatedById(user.getId());
        conclusionNew.setCreatedAt(new Date());
        conclusionNew.setHtmlText(htmlText);
        conclusionService.save(conclusionNew);
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
