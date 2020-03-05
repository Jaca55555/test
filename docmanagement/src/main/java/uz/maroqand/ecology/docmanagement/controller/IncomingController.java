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
public class IncomingController {

    private final UserService userService;
    private final PositionService positionService;
    private final HelperService helperService;
    private final DocumentService documentService;
    private final DocumentSubService documentSubService;
    private final DocumentTaskService documentTaskService;
    private final DocumentTaskSubService documentTaskSubService;
    private final DocumentLogService documentLogService;
    private final DocumentOrganizationService documentOrganizationService;

    public IncomingController(
            UserService userService,
            PositionService positionService,
            HelperService helperService,
            DocumentService documentService,
            DocumentSubService documentSubService,
            DocumentTaskService documentTaskService,
            DocumentTaskSubService documentTaskSubService,
            DocumentLogService documentLogService,
            DocumentOrganizationService documentOrganizationService) {
        this.userService = userService;
        this.positionService = positionService;
        this.helperService = helperService;
        this.documentService = documentService;
        this.documentSubService = documentSubService;
        this.documentTaskService = documentTaskService;
        this.documentTaskSubService = documentTaskSubService;
        this.documentLogService = documentLogService;
        this.documentOrganizationService = documentOrganizationService;
    }

    @RequestMapping(value = DocUrls.IncomingList, method = RequestMethod.GET)
    public String getIncomingListPage(Model model) {
        User user = userService.getCurrentUserFromContext();
        Set<Integer> statuses = new LinkedHashSet<>();

        statuses.add(TaskSubStatus.New.getId());
        model.addAttribute("newDocumentCount", documentTaskSubService.countByReceiverIdAndStatusIn(user.getId(), statuses));

        statuses.add(TaskSubStatus.InProgress.getId());
        statuses.add(TaskSubStatus.Waiting.getId());
        statuses.add(TaskSubStatus.Agreement.getId());
        model.addAttribute("inProgressDocumentCount", documentTaskSubService.countByReceiverIdAndStatusIn(user.getId(), statuses));

        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, -1);
        model.addAttribute("lessDeadlineDocumentCount", documentTaskSubService.countByReceiverIdAndDueDateLessThanEqual(user.getId(), calendar1.getTime()));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        model.addAttribute("greaterDeadlineDocumentCount", documentTaskSubService.countByReceiverIdAndDueDateGreaterThanEqual(user.getId(), calendar.getTime()));

        statuses = new LinkedHashSet<>();
        statuses.add(TaskSubStatus.Checking.getId());
        model.addAttribute("checkingDocumentCount", documentTaskSubService.countByReceiverIdAndStatusIn(user.getId(), statuses));
        model.addAttribute("allDocumentCount", documentTaskSubService.countByReceiverId(user.getId()));

        model.addAttribute("taskSubTypeList", TaskSubType.getTaskSubTypeList());
        model.addAttribute("taskSubStatusList", TaskSubStatus.getTaskSubStatusList());
        model.addAttribute("performerList", userService.getEmployeeList());
        return DocTemplates.IncomingList;
    }

    @RequestMapping(value = DocUrls.IncomingList, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getIncomingRegistrationList(
            @RequestParam(name = "documentOrganizationId") Integer documentOrganizationId,
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

        switch (tabFilter){
            case 2: type = TaskSubType.Performer.getId();break;//Ижро учун
            case 3:
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                deadlineDateBegin = calendar.getTime();
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.New.getId());
                status.add(TaskSubStatus.InProgress.getId());
                status.add(TaskSubStatus.Waiting.getId());
                status.add(TaskSubStatus.Agreement.getId());
                break;//Муддати кеччикан
            case 4:
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                deadlineDateEnd = calendar.getTime();
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.New.getId());
                status.add(TaskSubStatus.InProgress.getId());
                status.add(TaskSubStatus.Waiting.getId());
                status.add(TaskSubStatus.Agreement.getId());
                break;//Муддати якинлашаётган
            case 5:
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.Checking.getId());
                break;//Ижро назоратида
            case 6: type = TaskSubType.Info.getId();break;//Малъумот учун
            case 7:
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.Complete.getId());
                break;//Якунланган
            default:
                departmentId = user.getDepartmentId();
                receiverId=null;
                break;//Жами
        }

        HashMap<String, Object> result = new HashMap<>();
        Page<DocumentTaskSub> documentTaskSubs = documentTaskSubService.findFiltered(
                user.getOrganizationId(),
                1, //todo documentTypeId=1

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
                docContent+=documentOrganization!=null?documentOrganization.getName():"";
            }
            docContent+=" №"+ document.getDocRegNumber().trim() + " " + helperService.getTranslation("sys_date",locale) + ": " + (document.getDocRegDate()!=null?Common.uzbekistanDateFormat.format(document.getDocRegDate()):"");
            docContent+="\n" + (document.getContent()!=null?document.getContent().trim():"");
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
                    documentTaskService.getDueColor(documentTaskSub.getDueDate(),false,documentTaskSub.getStatus(),locale)

            });
        }

        result.put("recordsTotal", documentTaskSubs.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentTaskSubs.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(DocUrls.IncomingView)
    public String getIncomingViewPage(
            @RequestParam(name = "id")Integer taskSubId,
            Model model
    ) {
        DocumentTaskSub documentTaskSub = documentTaskSubService.getById(taskSubId);
        if (documentTaskSub == null || documentTaskSub.getTaskId()==null) {
            return "redirect:" + DocUrls.IncomingList;
        }
        DocumentTask task = documentTaskService.getById(documentTaskSub.getTaskId());
        if (task == null) {
            return "redirect:" + DocUrls.IncomingList;
        }

        Document document = documentService.getById(task.getDocumentId());
        if (document == null) {
            return "redirect:" + DocUrls.IncomingList;
        }
        if (document.getInsidePurpose()) {
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
        DocFilterDTO docFilterDTO = new DocFilterDTO();
        docFilterDTO.setDocumentTypeEnum(DocumentTypeEnum.OutgoingDocuments);
        List<DocumentTaskSub> documentTaskSubs = documentTaskSubService.getListByDocIdAndTaskId(document.getId(),task.getId());
        model.addAttribute("document", document);
        model.addAttribute("task", task);
        model.addAttribute("documentLog", new DocumentLog());
        model.addAttribute("tree", documentService.createTree(document));
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("documentTaskSub", documentTaskSub);
        model.addAttribute("documentTaskSubs", documentTaskSubs);
        model.addAttribute("user", userService.getCurrentUserFromContext());
        model.addAttribute("comment_url", DocUrls.AddComment);
        model.addAttribute("logs", documentLogService.getAllByDocAndTaskSubId(document.getId(), documentTaskSub.getId()));
        model.addAttribute("task_change_url", DocUrls.DocumentTaskChange);
        model.addAttribute("task_statuses", statuses);
        model.addAttribute("docList", documentService.findFiltered(docFilterDTO, PageRequest.of(0,100, Sort.Direction.DESC, "id")));
        model.addAttribute("isView", true);
        return DocTemplates.IncomingView;
    }

    @RequestMapping(value = DocUrls.IncomingTask)
    public String addTask(
            @RequestParam(name = "id")Integer taskSubId,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        DocumentTaskSub documentTaskSub = documentTaskSubService.getById(taskSubId);
        if (documentTaskSub == null) {
            return  "redirect:" + DocUrls.IncomingList;
        }
        Document document = documentService.getById(documentTaskSub.getDocumentId());
        if (document == null) {
            return  "redirect:" + DocUrls.IncomingList;
        }
        DocumentTask documentTask = documentTaskService.getByIdAndDocumentId(documentTaskSub.getTaskId(),document.getId());
        if (documentTask == null) {
            return  "redirect:" + DocUrls.IncomingList;
        }

        if (document.getInsidePurpose()) {
            if (user.getId().equals(documentTask.getPerformerId())) {
                document.setInsidePurpose(Boolean.FALSE);
            }
        }

        List<User> userList = userService.getListByDepartmentAllParent(user.getDepartmentId());

        model.addAttribute("document", document);
        model.addAttribute("documentTask", documentTask);
        model.addAttribute("documentTaskSub", documentTaskSub);
        model.addAttribute("userList", userList);
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("action_url", DocUrls.IncomingTaskSubmit);
        model.addAttribute("back_url", DocUrls.IncomingView+"?id=" + documentTaskSub.getId());
        return DocTemplates.IncomingTask;
    }

    @RequestMapping(value = DocUrls.IncomingTaskSubmit)
    public String incomingTaskSubmit(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "taskId") Integer taskId,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "docRegDateStr") String docRegDateStr,
            @RequestBody MultiValueMap<String, String> formData
    ){
        User user = userService.getCurrentUserFromContext();
        DocumentTaskSub documentTaskSub = documentTaskSubService.getById(id);
        if (documentTaskSub==null){
            return "redirect:" + DocUrls.IncomingList;
        }

        Document document = documentService.getById(documentTaskSub.getDocumentId());
        if (document==null){
            return "redirect:" + DocUrls.IncomingList;
        }

        System.out.println("id=" + id);
        System.out.println("content=" + content);
        System.out.println("docRegDateStr=" + docRegDateStr);

        DocumentTask documentTask = documentTaskService.getByIdAndDocumentId(taskId,document.getId());
        if (documentTask==null){
            return "redirect:" + DocUrls.IncomingList;
        }
        Integer userId = null;
        Integer performerType = null;
        Date dueDate = null;
        Map<String,String> map = formData.toSingleValueMap();
        for (Map.Entry<String,String> mapEntry: map.entrySet()) {

            String[] paramName =  mapEntry.getKey().split("_");
            String  tagName = paramName[0];
            String value= mapEntry.getValue().replaceAll(" ","");

            if (tagName.equals("user")){
                userId=Integer.parseInt(value);
            }
            if (tagName.equals("performer")){
                performerType = Integer.parseInt(value);
            }

            if (tagName.equals("dueDateStr")){
                dueDate = DateParser.TryParse(value, Common.uzbekistanDateFormat);
                if (userId!=null && performerType!=null){
                    documentTaskSubService.createNewSubTask(documentTaskSub.getLevel(),document.getId(),documentTask.getId(),content,dueDate,performerType,documentTaskSub.getReceiverId(),userId,userService.getUserDepartmentId(userId));
                    userId = null;
                    performerType = null;
                    dueDate = null;
                }
            }
        }

        return "redirect:" + DocUrls.IncomingView + "?id=" + documentTaskSub.getId();
    }

    @RequestMapping(value = DocUrls.IncomingTaskUserName)
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

    @RequestMapping(DocUrls.DocumentTaskChange)
    @ResponseBody
    public HashMap<String, Object> changeTaskStatus(
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
