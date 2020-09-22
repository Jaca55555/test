package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.PositionService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz) Kiruvchi xatlar
 */
@Controller
public class ReferenceController {

    private final UserService userService;
    private final PositionService positionService;
    private final HelperService helperService;
    private final DocumentService documentService;
    private final DocumentSubService documentSubService;
    private final DocumentTaskService documentTaskService;
    private final DocumentTaskSubService documentTaskSubService;
    private final DocumentLogService documentLogService;
    private final DocumentOrganizationService documentOrganizationService;
    private final DocumentDescriptionService documentDescriptionService;
    private final DocumentTaskContentService documentTaskContentService;

    public ReferenceController(
            DocumentTaskContentService documentTaskContentService,
            UserService userService,
            PositionService positionService,
            HelperService helperService,
            DocumentService documentService,

            DocumentSubService documentSubService,
            DocumentTaskService documentTaskService,
            DocumentTaskSubService documentTaskSubService,
            DocumentLogService documentLogService,
            DocumentOrganizationService documentOrganizationService, DocumentDescriptionService documentDescriptionService) {
        this.userService = userService;
        this.positionService = positionService;
        this.helperService = helperService;
        this.documentService = documentService;
        this.documentSubService = documentSubService;
        this.documentTaskService = documentTaskService;
        this.documentTaskSubService = documentTaskSubService;
        this.documentLogService = documentLogService;
        this.documentOrganizationService = documentOrganizationService;
        this.documentDescriptionService = documentDescriptionService;
        this.documentTaskContentService=documentTaskContentService;
    }

    @RequestMapping(value = DocUrls.ReferenceList, method = RequestMethod.GET)
    public String getReferenceListPage(@RequestParam(name = "tab_number", required = false)Integer tabNumber, Model model) {
        User user = userService.getCurrentUserFromContext();
        Set<Integer> statuses = new LinkedHashSet<>();
        statuses.add(TaskSubStatus.New.getId());
        Integer New = documentTaskSubService.countByReceiverIdAndStatus(user.getId(), statuses);
        model.addAttribute("tab_number_", tabNumber);
        model.addAttribute("reference", documentTaskSubService.countAllByTypeAndReceiverId(DocumentTypeEnum.AppealDocuments.getId(), user.getId()));

        model.addAttribute("newDocumentCount", New);
        statuses.clear();
        statuses.add(TaskSubStatus.InProgress.getId());
        Integer InProgress=documentTaskSubService.countByReceiverIdAndStatus(user.getId(), statuses);
        statuses.clear();
        statuses.add(TaskSubStatus.Waiting.getId());
        Integer Waiting=documentTaskSubService.countByReceiverIdAndStatus(user.getId(), statuses);
        statuses.clear();
        statuses.add(TaskSubStatus.Agreement.getId());
        Integer Agreement=documentTaskSubService.countByReceiverIdAndStatus(user.getId(), statuses);
        statuses.clear();
        Integer all=InProgress+Waiting+Agreement;
        model.addAttribute("inProgressDocumentCount", all);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, -1);
        model.addAttribute("lessDeadlineDocumentCount", documentTaskSubService.countByReceiverIdAndDueDateLessThanEqual(user.getId(),calendar1.getTime()));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        model.addAttribute("greaterDeadlineDocumentCount", documentTaskSubService.countByReceiverIdAndDueDateGreaterThanEqual(user.getId(), calendar.getTime()));

        statuses = new LinkedHashSet<>();
        statuses.add(TaskSubStatus.Checking.getId());

        model.addAttribute("checkingDocumentCount", documentTaskSubService.countByReceiverIdAndStatus(user.getId(), statuses));
        model.addAttribute("allDocumentCount", documentTaskSubService.countByReceiverIdAll(user.getId()));

        model.addAttribute("taskSubTypeList", TaskSubType.getTaskSubTypeList());
        model.addAttribute("taskSubStatusList", TaskSubStatus.getTaskSubStatusList());
        model.addAttribute("performerList", userService.getEmployeeList());
        return DocTemplates.ReferenceList;
    }

    @RequestMapping(value = DocUrls.ReferenceList, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getReferenceRegistrationList(
            @RequestParam(name = "documentOrganizationId",required = false) Integer documentOrganizationId,
            @RequestParam(name = "docRegNumber") String docRegNumber,
            @RequestParam(name = "registrationNumber") String registrationNumber,
            @RequestParam(name = "dateBegin") String dateBeginStr,
            @RequestParam(name = "dateEnd") String dateEndStr,
            @RequestParam(name = "taskContent") String taskContent,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "performerId") Integer performerId,
            @RequestParam(name = "taskSubType") Integer taskSubType,
            @RequestParam(name = "taskSubStatus") Integer taskSubStatus,
            @RequestParam(name = "tabFilter") Integer tabFilter,
            @RequestParam(name = "fullName", required = false) String fullName,
            Pageable pageable
    ) {
        User user = userService.getCurrentUserFromContext();
        Date deadlineDateBegin = null;
        Date deadlineDateEnd = null;
        Integer type = null;
        Set<Integer> status = null;
        Integer departmentId = null;
        Integer receiverId = user.getId();
        Calendar calendar = Calendar.getInstance();
        Boolean specialControl = null;
        switch (tabFilter){
            case 2: type = TaskSubType.Performer.getId();
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.InProgress.getId());
                status.add(TaskSubStatus.Waiting.getId());
                status.add(TaskSubStatus.Agreement.getId());
                status.add(TaskSubStatus.New.getId());
            break;//Ижро учун
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
            case 6: type = TaskSubType.Info.getId();break;//Малъумот учун
            case 7:
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.Complete.getId());
                break;//Якунланган
            case 8:
                specialControl = Boolean.TRUE;
                break;//Якунланган
            default:
                departmentId = user.getDepartmentId();
//                receiverId=null;
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(
                        Sort.Order.asc("status"),
                        Sort.Order.desc("dueDate")
                ));
                break;//Жами
        }

        HashMap<String, Object> result = new HashMap<>();
        Page<DocumentTaskSub> documentTaskSubs = documentTaskSubService.findFiltered(
                user.getOrganizationId(),
                Collections.singletonList(4), //todo documentTypeId=1
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
                specialControl,
                pageable
        );
        String locale = LocaleContextHolder.getLocale().getLanguage();
        List<DocumentTaskSub> documentTaskSubList = documentTaskSubs.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentTaskSubList.size());
        for (DocumentTaskSub documentTaskSub : documentTaskSubList) {
            Document document = documentTaskSub.getDocument();
            DocumentSub documentSub = documentSubService.getByDocumentIdForIncoming(document.getId());
            String docContent="";
            if (documentSub!=null && documentSub.getOrganizationId()!=null){
                DocumentOrganization documentOrganization = documentSub.getOrganization();
                docContent+=documentOrganization!=null?documentOrganization.getName()+".":"";
            }
            if (document.getDocRegNumber()!=null && document.getDocRegNumber()!=""){
                docContent+=" №"+ document.getDocRegNumber().trim()+",";
            }
            docContent+=document.getDocRegDate()!=null?( " " + helperService.getTranslation("sys_date",locale) + ": " + Common.uzbekistanDateFormat.format(document.getDocRegDate())):"";
            docContent+="\n" + (document.getContent()!=null?"</br><span class='text-secondary' style='font-size:13px'>"+document.getContent().trim()+"</span>":"");
            JSONArray.add(new Object[]{
                    documentTaskSub.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    docContent,
                    documentTaskSub.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(documentTaskSub.getCreatedAt()):"",
                    documentTaskSub.getDueDate()!=null? Common.uzbekistanDateFormat.format(documentTaskSub.getDueDate()):"",
                    documentTaskSub.getStatus()!=null ? helperService.getTranslation(TaskSubStatus.getTaskStatus(documentTaskSub.getStatus()).getName(),locale):"",
                    documentTaskSub.getContent(),
                    documentTaskSub.getStatus(),
                    documentTaskService.getDueColor(documentTaskSub.getDueDate(),false,documentTaskSub.getStatus(),locale),
                    document.getContentFiles()

            });
        }

        result.put("recordsTotal", documentTaskSubs.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentTaskSubs.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(DocUrls.ReferenceView)
    public String getIncomingViewPage(
            @RequestParam(name = "id")Integer taskSubId,
            Model model
    ) {
        DocumentTaskSub documentTaskSub = documentTaskSubService.getById(taskSubId);
        if (documentTaskSub == null || documentTaskSub.getTaskId()==null) {
            return "redirect:" + DocUrls.ReferenceList;
        }
        if (documentTaskSub.getStatus().equals(TaskSubStatus.New.getId())){
            documentTaskSub.setStatus(TaskSubStatus.InProgress.getId());
            documentTaskSubService.update(documentTaskSub);
        }
        DocumentTask task = documentTaskService.getById(documentTaskSub.getTaskId());
        if (task == null || task.getDocumentId()==null) {
            return "redirect:" + DocUrls.ReferenceList;
        }

        Document document = documentService.getById(task.getDocumentId());
        if (document == null) {
            return "redirect:" + DocUrls.ReferenceList;
        }
        DocumentSub documentSub =  documentSubService.getByDocumentIdForIncoming(document.getId());

        if (Boolean.TRUE.equals(document.getInsidePurpose())) {
            User user = userService.getCurrentUserFromContext();
            if (user.getId().equals(task.getPerformerId())) {
                document.setInsidePurpose(Boolean.FALSE);
            }
        }
        List<TaskSubStatus> statuses = new LinkedList<>();
        statuses.add(TaskSubStatus.InProgress);
        statuses.add(TaskSubStatus.Waiting);
        statuses.add(TaskSubStatus.Agreement);
        statuses.add(TaskSubStatus.Checking);
        List<Integer> docTypes = new ArrayList<>();
        docTypes.add(DocumentTypeEnum.OutgoingDocuments.getId());
        docTypes.add(DocumentTypeEnum.InnerDocuments.getId());
        docTypes.add(DocumentTypeEnum.IncomingDocuments.getId());
        List<DocumentTaskSub> documentTaskSubs = documentTaskSubService.getListByDocIdAndTaskId(document.getId(),task.getId());
        model.addAttribute("document", document);
        model.addAttribute("executeForm",document.getExecuteForm().getName());
        model.addAttribute("documentSub",documentSub);
        model.addAttribute("task", task);
        model.addAttribute("documentLog", new DocumentLog());
        model.addAttribute("tree", documentService.createTree(document));
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("documentTaskSub", documentTaskSub);
        model.addAttribute("dueDate",Common.uzbekistanDateFormat.format(documentTaskSub.getDueDate()));
        model.addAttribute("documentTaskSubs", documentTaskSubs);
        model.addAttribute("user", userService.getCurrentUserFromContext());
        model.addAttribute("comment_url", DocUrls.AddComment);
        model.addAttribute("logs", documentLogService.getAllByDocAndTaskId(document.getId(), task.getId()));
        model.addAttribute("task_change_url", DocUrls.DocumentTaskChange);
        model.addAttribute("task_statuses", statuses);
        System.out.println("check getting additional doc list");
        model.addAttribute("docList", documentService.findAllByDocumentTypeIn(docTypes, PageRequest.of(0,100, Sort.Direction.DESC, "id")));
        model.addAttribute("isView", true);
        return DocTemplates.ReferenceView;
    }

    @RequestMapping(value = DocUrls.ReferenceTask)
    public String addTask(
            @RequestParam(name = "id")Integer taskSubId,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        DocumentTaskSub documentTaskSub = documentTaskSubService.getById(taskSubId);
        if (documentTaskSub == null) {
            return  "redirect:" + DocUrls.ReferenceList;
        }
        Document document = documentService.getById(documentTaskSub.getDocumentId());
        if (document == null) {
            return  "redirect:" + DocUrls.ReferenceList;
        }
        DocumentTask documentTask = documentTaskService.getByIdAndDocumentId(documentTaskSub.getTaskId(),document.getId());
        if (documentTask == null) {
            return  "redirect:" + DocUrls.ReferenceList;
        }

        if (Boolean.TRUE.equals(document.getInsidePurpose())) {
            if (user.getId().equals(documentTask.getPerformerId())) {
                document.setInsidePurpose(Boolean.FALSE);
            }
        }

        List<User> userList = userService.getListByDepartmentAllParent(user.getDepartmentId());

        model.addAttribute("document", document);
        model.addAttribute("executeForm",document.getExecuteForm().getName());
        model.addAttribute("task", documentTask);
        model.addAttribute("documentTaskSub", documentTaskSub);
        model.addAttribute("userList", userList);
        model.addAttribute("descriptionList", documentTaskContentService.getTaskContentList(user.getOrganizationId()));
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("action_url", DocUrls.ReferenceTaskSubmit);
        model.addAttribute("back_url", DocUrls.ReferenceView+"?id=" + documentTaskSub.getId());
        return DocTemplates.ReferenceTask;
    }

    @RequestMapping(value = DocUrls.ReferenceTaskSubmit)
    public String referenceTaskSubmit(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "taskId") Integer taskId,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "docRegDateStr") String docRegDateStr,
            @RequestBody MultiValueMap<String, String> formData
    ){
        User user = userService.getCurrentUserFromContext();
        DocumentTaskSub documentTaskSub = documentTaskSubService.getById(id);
        if (documentTaskSub==null){
            return "redirect:" + DocUrls.ReferenceList;
        }

        if (documentTaskSub.getStatus().equals(TaskSubStatus.New.getId())){
            documentTaskSub.setStatus(TaskSubStatus.InProgress.getId());
            documentTaskSubService.update(documentTaskSub);
        }

        Document document = documentService.getById(documentTaskSub.getDocumentId());
        if (document==null){
            return "redirect:" + DocUrls.ReferenceList;
        }

        System.out.println("id=" + id);
        System.out.println("content=" + content);
        System.out.println("docRegDateStr=" + docRegDateStr);

        DocumentTask documentTask = documentTaskService.getByIdAndDocumentId(taskId,document.getId());
        if (documentTask==null){
            return "redirect:" + DocUrls.ReferenceList;
        }
        Integer userId = null;
        Integer performerType = null;
        Date dueDate = null;
        Map<String,String> map = formData.toSingleValueMap();
        for (Map.Entry<String,String> mapEntry: map.entrySet()) {

            String[] paramName =  mapEntry.getKey().split("_");
            String  tagName = paramName[0];
            String value= mapEntry.getValue().replaceAll(" "," ");

            if (tagName.equals("user")){
                userId=Integer.parseInt(value);
            }
            if (tagName.equals("performer")){
                performerType = Integer.parseInt(value);
            }

            if (tagName.equals("dueDateStr")){
                dueDate = DateParser.TryParse(value, Common.uzbekistanDateFormat);
                if (userId!=null && performerType!=null){
                    documentTaskSubService.createNewSubTask(documentTaskSub.getLevel(),document,documentTask.getId(),content,dueDate,performerType,documentTaskSub.getReceiverId(),userId,userService.getUserDepartmentId(userId));
                    userId = null;
                    performerType = null;
                    dueDate = null;
                }
            }
        }

        return "redirect:" + DocUrls.ReferenceView + "?id=" + documentTaskSub.getId();
    }

    @RequestMapping(value = DocUrls.ReferenceTaskUserName)
    @ResponseBody
    public HashMap<String,Object> getUserName(@RequestParam(name = "id") Integer userId){
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        HashMap<String,Object> result = new HashMap<>();
        result.put("status",1);
        User user = userService.findById(userId);
        if (user==null){
            result.put("status",0);
            return result;
        }

        result.put("userId",user.getId());
        result.put("userFullName",user.getFullName());
        result.put("position", user.getPosition().getNameTranslation(locale));
        return result;
    }

    @RequestMapping(DocUrls.ReferenceTaskChange)
    @ResponseBody
    public HashMap<String, Object> changeTaskStatusReference(
            @RequestParam(name = "taskStatus")Integer status,
            @RequestParam(name = "taskSubId")Integer taskSubId,
            DocumentLog documentLog
    ) {
        HashMap<String, Object> response = new HashMap<>();
        User user = userService.getCurrentUserFromContext();

        DocumentTaskSub taskSub = documentTaskSubService.getById(taskSubId);
        TaskSubStatus oldStatus = TaskSubStatus.getTaskStatus(taskSub.getStatus());
        TaskSubStatus newStatus = TaskSubStatus.getTaskStatus(status);
        taskSub.setStatus(status);
        taskSub.setContent(documentLog.getContent());
        taskSub.setUpdateById(user.getId());
        taskSub.setAdditionalDocumentId(documentLog.getAttachedDocId());
        taskSub = documentTaskSubService.update(taskSub);
        String locale = LocaleContextHolder.getLocale().getLanguage();
        String logAuthorPos = positionService.getById(user.getPositionId()).getName();
        DocumentLog documentLog1 =  documentLogService.createLog(documentLog,DocumentLogType.Log.getId(),null,oldStatus.getName(),oldStatus.getColor(),newStatus.getName(),newStatus.getColor(),user.getId());

        DocumentTask documentTask = documentTaskService.getById(taskSub.getTaskId());
        if (documentTask!=null && documentTask.getPerformerId()!=null && documentTask.getPerformerId().equals(user.getId()) && TaskSubStatus.getTaskStatus(status).equals(TaskSubStatus.Checking)){
            documentTask.setStatus(TaskStatus.Checking.getId());
            documentTaskService.update(documentTask);
        }
        Document document = documentService.getById(documentLog1.getAttachedDocId());
        response.put("task", taskSub);
        response.put("taskStatus", helperService.getTranslation(taskSub.getStatusName(taskSub.getStatus()),LocaleContextHolder.getLocale().toLanguageTag()));
        response.put("status", "success");
        response.put("log", documentLog1);
        response.put("logCreateName", user.getFullName());
        response.put("logCreatePosition", logAuthorPos);
        response.put("registrationNumber", document!=null?document.getRegistrationNumber():"");
        response.put("beforeStatus", helperService.getTranslation(documentLog1.getBeforeStatus(),locale));
        response.put("afterStatus", helperService.getTranslation(documentLog1.getAfterStatus(),locale));
        return response;
    }
}
