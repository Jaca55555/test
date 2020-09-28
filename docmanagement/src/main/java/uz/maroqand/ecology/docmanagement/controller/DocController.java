package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.constant.user.Permissions;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.Role;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.dto.Select2Dto;
import uz.maroqand.ecology.docmanagement.dto.Select2PaginationDto;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz)
 */
@Controller
public class DocController {

    private final UserService userService;
    private final FileService fileService;
    private final DocumentService documentService;
    private final DocumentLogService documentLogService;
    private final DocumentViewService documentViewService;
    private final DocumentTaskService documentTaskService;
    private final DocumentOrganizationService documentOrganizationService;
    private final HelperService helperService;
    private final DocumentSubService documentSubService;
    private final DocumentTaskSubService documentTaskSubService;
    private final NotificationService notificationService;

    @Autowired
    public DocController(
            UserService userService,
            FileService fileService,
            DocumentService documentService,
            DocumentLogService documentLogService,
            DocumentViewService documentViewService,
            DocumentTaskService documentTaskService,
            DocumentOrganizationService documentOrganizationService,
            HelperService helperService,
            DocumentSubService documentSubService,
            DocumentTaskSubService documentTaskSubService,
            NotificationService notificationService) {
        this.userService = userService;
        this.fileService = fileService;
        this.documentService = documentService;
        this.documentLogService = documentLogService;
        this.documentViewService = documentViewService;
        this.documentTaskService = documentTaskService;
        this.documentOrganizationService = documentOrganizationService;
        this.helperService = helperService;
        this.documentSubService = documentSubService;
        this.documentTaskSubService = documentTaskSubService;
        this.notificationService = notificationService;
    }

    @RequestMapping(DocUrls.Dashboard)
    public String getDepartmentList(
            Model model,
            @RequestParam(name = "lang", required = false) String lang
    ) {
        User user = userService.getCurrentUserFromContext();
        if (lang==null || lang.isEmpty()){
            if (user.getLang()==null || user.getLang().isEmpty()){
                user.setLang("oz");
                userService.updateUser(user);
                return "redirect:"+DocUrls.Dashboard+"?lang=oz";

            }
            return "redirect:"+DocUrls.Dashboard+"?lang="+user.getLang();
        }
        Integer organizationId = user.getOrganizationId(), departmentId = user.getDepartmentId(), userId = user.getId();

        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("organizationList", documentOrganizationService.getStatusActive());
        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 3);
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.add(Calendar.HOUR_OF_DAY, 0);
        System.out.println(calendar.getTime());
        Date date = calendar.getTime();
        Date date1 = calendar1.getTime();

        calendar2.add(Calendar.YEAR, -100);
        Date date2=calendar2.getTime();
        System.out.println(date+"####"+date1+"####"+date2);
        model.addAttribute("incoming", documentTaskSubService.countAllByTypeAndReceiverId(DocumentTypeEnum.IncomingDocuments.getId(), userId));
        model.addAttribute("inProgress",documentTaskSubService.getByDocumentType(TaskSubStatus.New.getId(),userId));
        model.addAttribute("nearDate",documentTaskSubService.getByDuedate(date,date1,userId));
        model.addAttribute("afterDate",documentTaskSubService.getByDuedate(date1,date2,userId));
        model.addAttribute("inner", documentTaskSubService.countAllByTypeAndReceiverId(DocumentTypeEnum.InnerDocuments.getId(), userId));

        model.addAttribute("outgoingCountNew", documentService.countAllTodaySDocuments(2, organizationId, departmentId, userId));
        model.addAttribute("outgoingInProgress", documentService.countAllByStatus(2, DocumentStatus.InProgress, organizationId, departmentId, userId));
        model.addAttribute("outgoingCompleted", documentService.countAllByStatus(2, DocumentStatus.Completed, organizationId, departmentId, userId));
        model.addAttribute("outgoingAll", documentService.countAll(2, organizationId, departmentId, userId));
        model.addAttribute("outgoingCount", documentService.getCountersByType(DocumentTypeEnum.OutgoingDocuments.getId()));

        model.addAttribute("appeal", documentTaskSubService.countAllByTypeAndReceiverId(DocumentTypeEnum.AppealDocuments.getId(), userId));

        Role role = userService.getCurrentUserFromContext().getRole();
        String specifiedView = role.getPermissions().contains(Permissions.DOC_MANAGEMENT_REGISTER) ?DocUrls.IncomingRegistrationView : DocUrls.IncomingView;
        model.addAttribute("viewLink_", specifiedView);

        return DocTemplates.Dashboard;
    }

    @RequestMapping(value = DocUrls.DashboardDocList, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getIncomingRegistrationList(
            @RequestParam(name = "documentOrganizationId",required = false,defaultValue = "") Integer documentOrganizationId,
            @RequestParam(name = "docRegNumber",required = false,defaultValue = "") String docRegNumber,
            @RequestParam(name = "registrationNumber",required = false,defaultValue = "") String registrationNumber,
            @RequestParam(name = "dateBeginStr",required = false,defaultValue = "") String dateBeginStr,
            @RequestParam(name = "dateEndStr",required = false,defaultValue = "") String dateEndStr,
            @RequestParam(name = "taskContent",required = false,defaultValue = "") String taskContent,
            @RequestParam(name = "content",required = false,defaultValue = "") String content,
            @RequestParam(name = "performerId",required = false,defaultValue = "") Integer performerId,
            @RequestParam(name = "taskSubType",required = false,defaultValue = "") Integer taskSubType,
            @RequestParam(name = "taskSubStatus",required = false,defaultValue = "") Integer taskSubStatus,
            @RequestParam(name = "tabFilter",required = false,defaultValue = "") Integer tabFilter,
            @RequestParam(name = "documentTypeId", required = false) Integer documentTypeId,
            Pageable pageable
    ) {
//        User user = userService.getCurrentUserFromContext();
//        HashMap<String, Object> result = new HashMap<>();
//        String locale = LocaleContextHolder.getLocale().getLanguage();
//        Calendar calendar = Calendar.getInstance();
//        Date dueDateBegin = null;
//        Date dueDateEnd = null;
//        Set<Integer> statuses = new HashSet<>();
//        switch (tabFilter) {
//            case 2:
//                statuses.add(TaskStatus.InProgress.getId());
//                break;
//            case 3:
//                statuses = null;
//                dueDateEnd = calendar.getTime();
//                break;
//            case 4:
//                statuses = null;
//                dueDateBegin = calendar.getTime();
//                calendar.add(Calendar.DAY_OF_MONTH, 3);
//                dueDateEnd = calendar.getTime();
//                break;
//            case 5:
//                statuses.add(TaskStatus.Checking.getId());
//                break;
////            case 6:
////                statuses = null;
////                incomingRegFilter.setInsidePurpose(Boolean.TRUE);
////                break;
//            case 7:
//                statuses.add(TaskStatus.Complete.getId());
//                break;
//            default:
//                statuses = null;
//                break;
//        }
//        //todo documentTypeId=1
//        List<Integer> list = new ArrayList<>();
//
//        Page<DocumentTaskSub> documentTaskPage = taskSubService.findFiltered()
//        List<DocumentTask> documentTaskList = documentTaskPage.getContent();
//        List<Object[]> JSONArray = new ArrayList<>(documentTaskList.size());
//
//        Role role = userService.getCurrentUserFromContext().getRole();
//        Boolean hasPermission = role.getPermissions().contains(Permissions.DOC_MANAGEMENT_REGISTER);
//
//        Set<Integer> collectedIds = new TreeSet<>();
//        if(hasPermission)
//        for (DocumentTask documentTask : documentTaskList) {
//            Document document = documentTask.getDocument();
//            if(collectedIds.contains(document.getId()))
//                continue;
//            else
//                collectedIds.add(document.getId());
//
//            JSONArray.add(new Object[]{
//                    document.getId(),
//                    document.getRegistrationNumber(),
//                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
//                    document.getContent(),
//                    documentTask.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(documentTask.getCreatedAt()):"",
//                    documentTask.getDueDate()!=null? Common.uzbekistanDateFormat.format(documentTask.getDueDate()):"",
//                    helperService.getTranslation(documentTask.getStatusName(documentTask.getStatus()),locale),
//                    documentTask.getContent(),
//                    documentTask.getStatus(),
//                    taskService.getDueColor(documentTask.getDueDate(),true,documentTask.getStatus(),locale)
//            });
//        }
//        else{
//            for (DocumentTask documentTask : documentTaskList) {
//                Document document = documentTask.getDocument();
//
//                if(collectedIds.contains(document.getId()))
//                    continue;
//                else
//                    collectedIds.add(document.getId());
//
//                List<DocumentTaskSub> taskSubs = taskSubService.getListByDocId(document.getId());
//
//                for(DocumentTaskSub sub: taskSubs){
//                    JSONArray.add(new Object[]{
//                            sub.getId(),
//                            document.getRegistrationNumber(),
//                            document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
//                            document.getContent(),
//                            documentTask.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(documentTask.getCreatedAt()):"",
//                            documentTask.getDueDate()!=null? Common.uzbekistanDateFormat.format(documentTask.getDueDate()):"",
//                            helperService.getTranslation(documentTask.getStatusName(documentTask.getStatus()),locale),
//                            documentTask.getContent(),
//                            documentTask.getStatus(),
//                            taskService.getDueColor(documentTask.getDueDate(),true,documentTask.getStatus(),locale)
//                    });
//                }
//            }
//        }
//        result.put("recordsTotal", documentTaskPage.getTotalElements()); //Total elements
//        result.put("recordsFiltered", JSONArray.size()); //Filtered elements
//        result.put("data", JSONArray);

        dateBeginStr = StringUtils.trimToNull(dateBeginStr);
        dateEndStr = StringUtils.trimToNull(dateEndStr);
        User user = userService.getCurrentUserFromContext();
        Date deadlineDateBegin = null;
        Date deadlineDateEnd = null;
        Integer type = null;
        Set<Integer> status = null;
        Integer departmentId = null;
        Integer receiverId = user.getId();
        Calendar calendar = Calendar.getInstance();
        Boolean specialControll = null;

        List<Integer> documentTypeIds = new ArrayList<>(4);
        if(documentTypeId == null)
            documentTypeId = -1;
        switch(documentTypeId){
            case 1:
                documentTypeIds.add(1);
                break;
            case 2:
                documentTypeIds.add(2);
                break;
            case 3:
                documentTypeIds.add(3);
                break;
            case 4:
                documentTypeIds.add(4);
                break;
            default:
                documentTypeIds.addAll(Arrays.asList(1,2,3,4));
        }


        switch (tabFilter){
            case 9: status = new LinkedHashSet<>();
                status.add(TaskSubStatus.Initial.getId());
                status.add(TaskSubStatus.New.getId());
                break;
            case 2:  status = new LinkedHashSet<>();
                status.add(TaskSubStatus.InProgress.getId());
                status.add(TaskSubStatus.Waiting.getId());
                status.add(TaskSubStatus.Agreement.getId());
                break;
          /*   case 3:
                calendar.add(Calendar.DATE, 1);
                deadlineDateEnd = calendar.getTime();
                deadlineDateEnd.setHours(23);
                deadlineDateEnd.setMinutes(59);
                deadlineDateEnd.setSeconds(0);
                calendar.add(Calendar.DATE, -2);
                deadlineDateBegin = calendar.getTime();
                deadlineDateBegin.setHours(23);
                deadlineDateBegin.setMinutes(59);
                deadlineDateBegin.setSeconds(59);

                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.Initial.getId());
                status.add(TaskSubStatus.New.getId());
                status.add(TaskSubStatus.InProgress.getId());
                status.add(TaskSubStatus.Waiting.getId());
                status.add(TaskSubStatus.Agreement.getId());
                break;//Муддати якинлашаётган
            case 4:
                calendar.add(Calendar.DATE, -1);
                deadlineDateEnd = calendar.getTime();
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.Initial.getId());
                status.add(TaskSubStatus.New.getId());
                status.add(TaskSubStatus.InProgress.getId());
                status.add(TaskSubStatus.Waiting.getId());
                status.add(TaskSubStatus.Agreement.getId());
                break;//Муддати кеччикан */
            case 3:
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                deadlineDateEnd = calendar.getTime();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                deadlineDateBegin = calendar.getTime();
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.New.getId());
                status.add(TaskSubStatus.InProgress.getId());
                status.add(TaskSubStatus.Waiting.getId());
                status.add(TaskSubStatus.Agreement.getId());
                break;//Муддати якинлашаётган
            case 4:
                deadlineDateEnd = calendar.getTime();
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.New.getId());
                status.add(TaskSubStatus.InProgress.getId());
                status.add(TaskSubStatus.Waiting.getId());
                status.add(TaskSubStatus.Agreement.getId());
                break;//Муддати кеччикан
            case 5:
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.Checking.getId());
                break;//Ижро назоратида
            /*case 6: type = TaskSubType.Info.getId();break;*///Малъумот учун
            case 7:
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.Complete.getId());
                break;//Якунланган
           /* case 8:
                specialControll=Boolean.TRUE;
                break;//Якунланган*/
            default:
                departmentId = user.getDepartmentId();
                /*receiverId=null;*/
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(
                        Sort.Order.asc("status"),
                        Sort.Order.desc("dueDate")
                ));
                break;//Жами
        }
        HashMap<String, Object> result = new HashMap<>();
        Page<DocumentTaskSub> documentTaskSubs = documentTaskSubService.findFiltered(
                user.getOrganizationId(),
                documentTypeIds, //todo documentTypeId = 3
                documentOrganizationId,
                docRegNumber,
                registrationNumber,
                DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat),
                DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat),
                taskContent,
                content,
                performerId,
                taskSubType,
                taskSubStatus,

                deadlineDateBegin,
                deadlineDateEnd,
                type,
                status,
                departmentId,
                receiverId,
                specialControll,
                pageable
        );
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        List<DocumentTaskSub> documentTaskSubList = documentTaskSubs.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentTaskSubList.size());
        for (DocumentTaskSub documentTaskSub : documentTaskSubList) {
            Document document = documentService.getById(documentTaskSub.getDocumentId());
            JSONArray.add(new Object[]{
                    documentTaskSub.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null ? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    document.getContent(),
                    documentTaskSub.getCreatedAt()!=null ? Common.uzbekistanDateFormat.format(documentTaskSub.getCreatedAt()):"",
                    documentTaskSub.getDueDate()!=null ? Common.uzbekistanDateFormat.format(documentTaskSub.getDueDate()):"",
                    documentTaskSub.getStatus()!=null ? helperService.getTranslation(TaskSubStatus.getTaskStatus(documentTaskSub.getStatus()).getName(),locale):"",
                    documentTaskSub.getContent(),
                    documentTaskSub.getStatus(),
                    documentTaskService.getDueColor(documentTaskSub.getDueDate(),false,documentTaskSub.getStatus(),locale),
                    document.getDocumentTypeId()
            });
        }

        result.put("recordsTotal", documentTaskSubs.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentTaskSubs.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);

        return result;
    }

    @RequestMapping(value = DocUrls.RegistrationAdditionalDocument, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getDocumentNumberSelectAjax(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page") Integer page
    ){
        User user = userService.getCurrentUserFromContext();
        search = StringUtils.trimToNull(search);
        PageRequest pageRequest = new PageRequest(page-1, 15, Sort.Direction.DESC, "id");
        Page<Document> documentPage = documentService.getRegistrationNumber(search,user.getOrganizationId(), pageRequest);
        HashMap<String,Object> result = new HashMap<>();
        List<Select2Dto> select2DtoList = new ArrayList<>();
        for (Document document  : documentPage.getContent()) {

                select2DtoList.add(new Select2Dto(document.getId(), document.getRegistrationNumber()+" - "+ Common.uzbekistanDateFormat.format(document.getRegistrationDate())));
       }
//        +" - "+ Common.uzbekistanDateAndTimeFormat.format(document.getRegistrationDate())
        Select2PaginationDto paginationDto = new Select2PaginationDto();
        paginationDto.setMore(true);
        paginationDto.setSize(15);
        paginationDto.setTotal(documentPage.getTotalElements());

        result.put("results", select2DtoList);
        result.put("pagination", paginationDto);
        result.put("total_count", documentPage.getTotalElements());
        return result;
    }

    @RequestMapping(value = DocUrls.RegistrationOrganization, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getOrganizationSelectAjax(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page") Integer page
    ){
        User user = userService.getCurrentUserFromContext();
        search = StringUtils.trimToNull(search);
        PageRequest pageRequest = PageRequest.of(page-1, 15, Sort.Direction.ASC, "id");
        Page<DocumentOrganization> documentOrganizationPage = documentOrganizationService.getOrganizationList(search,null, pageRequest);
        HashMap<String,Object> result = new HashMap<>();
        List<Select2Dto> select2DtoList = new ArrayList<>();
        for (DocumentOrganization documentOrganization : documentOrganizationPage.getContent()) {
            select2DtoList.add(new Select2Dto(documentOrganization.getId(), documentOrganization.getName()));
        }

        Select2PaginationDto paginationDto = new Select2PaginationDto();
        paginationDto.setMore(true);
        paginationDto.setSize(15);
        paginationDto.setTotal(documentOrganizationPage.getTotalElements());

        result.put("results", select2DtoList);
        result.put("pagination", paginationDto);
        result.put("total_count", documentOrganizationPage.getTotalElements());
        return result;
    }

    @RequestMapping(value = DocUrls.AddComment, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> createLogComment(
            @RequestParam(name = "file_ids", required = false) List<Integer> file_ids,
            DocumentLog documentLog
    ) {
        String locale = LocaleContextHolder.getLocale().getLanguage();
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();
        DocumentLog documentLog1 = documentLogService.createLog(documentLog,DocumentLogType.Comment.getId(),file_ids,"","","","",user.getId());
        Document document = documentService.getById(documentLog1.getDocumentId());
        if (document!=null && documentLog.getTaskSubId()!=null){
            DocumentTaskSub documentTaskSub = documentTaskSubService.getById(documentLog.getTaskSubId());
            if (documentTaskSub!=null){
                documentTaskService.createNotificationForAddComment(document,documentTaskSub);

            }
        }
        result.put("status", "success");
        result.put("createName", user.getFullName());
        result.put("createPosition", user.getPositionId()!=null?helperService.getUserPositionName(user.getPositionId(),locale):"");
        result.put("createdAt", Common.uzbekistanDateAndTimeFormat.format(documentLog1.getCreatedAt()));
        result.put("log", documentLog1);
        return result;
    }

    @RequestMapping(value = DocUrls.DocumentOpenView)
    public String getViewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Document document = documentService.getById(id);
        if (document==null){
            return "redirect:" + DocUrls.Dashboard;
        }

        Integer viewTagId= document.getDocumentType().getType();
        String viewTag = "";
        switch (viewTagId){
            case 1: viewTag = "doc_incoming"; break;
            case 2: viewTag = "doc_outgoing";break;
            case 3: viewTag = "doc_inner";break;
            case 4: viewTag = "doc_appeal";break;
        }
        DocumentSub documentSub =  documentSubService.getByDocumentIdForIncoming(document.getId());
        String document_organization_name="";
        Set<DocumentOrganization> documentOrganizationSet = documentSub.getDocumentOrganizations();
        if (documentOrganizationSet!=null && documentOrganizationSet.size()>0){
            for (DocumentOrganization documentOrganization: documentOrganizationSet) {
                document_organization_name +=documentOrganization.getName() + ", ";
            }
        }else if (documentSub.getOrganizationId()!=null){
            document_organization_name=documentSub.getOrganization().getName();
        }
        model.addAttribute("document_organization_name", document_organization_name);

        model.addAttribute("view_tag" ,viewTag);
        model.addAttribute("document" ,document);
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));


        return DocTemplates.DocumentOpenView;
    }

    @RequestMapping(value = DocUrls.TaskChangeStatus, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody HashMap<String, Object> taskChangeStatus(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "target_status_id")Integer statusId
    ){
        System.out.println("id: " + id + ", statusId: " + statusId);
        HashMap<String, Object> result = new HashMap<>();
        String locale = LocaleContextHolder.getLocale().getLanguage();
        DocumentTask documentTask = documentTaskService.getById(id);
        switch (statusId){
            case 1:
                documentTask.setStatus(TaskStatus.New.getId());
                result.put("first", helperService.getTranslation(TaskStatus.InProgress.getName(),locale));
                result.put("firstId", 2);
                result.put("second", helperService.getTranslation(TaskStatus.Checking.getName(),locale));
                result.put("secondId", 3);
                break;
            case 2:
                documentTask.setStatus(TaskStatus.InProgress.getId());
                result.put("first", helperService.getTranslation(TaskStatus.New.getName(),locale));
                result.put("firstId", 1);
                result.put("second", helperService.getTranslation(TaskStatus.Checking.getName(),locale));
                result.put("secondId", 3);
                break;
            case 3:
                documentTask.setStatus(TaskStatus.Checking.getId());
                result.put("first",helperService.getTranslation(TaskStatus.New.getName(),locale));
                result.put("firstId", 1);
                result.put("second", helperService.getTranslation(TaskStatus.InProgress.getName(),locale));
                result.put("secondId", 2);
                break;
        }

        documentTask.setUpdateAt(new Date());
        documentTask.setUpdateById(userService.getCurrentUserFromContext().getId());

        result.put("updatedAt", Common.uzbekistanDateFormat.format(new Date()));
        result.put("changedOnStatusId", statusId);
        documentTaskService.update(documentTask);
        return result;
    }

    @RequestMapping(value = DocUrls.DocLangSelect)
    public String selectLang(
            @RequestParam(name = "lang") String lang,
            @RequestParam(name = "currentUrl") String currentUrl
    ){
        lang = lang.substring(0,2);
        User user = userService.getCurrentUserFromContext();
        user.setLang(lang);
        userService.updateUser(user);
        return "redirect:" + currentUrl;
    }

    @RequestMapping(value = DocUrls.FileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, File> fileUpload(@RequestParam(name = "file") MultipartFile file){

        File file_ = fileService.uploadFile(file, userService.getCurrentUserFromContext().getId(), file.getOriginalFilename(), file.getContentType());
        HashMap<String, File> res = new HashMap<>();
        res.put("data", file_);

        return res;
    }
}
