package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentLog;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz) Kiruvchi xatlar
 */
@Controller
public class IncomingController {

    private final UserService userService;
    private final DocumentService documentService;
    private final DocumentSubService documentSubService;
    private final DocumentTaskService documentTaskService;
    private final DocumentTaskSubService documentTaskSubService;
    private final DocumentLogService documentLogService;

    public IncomingController(
            UserService userService,
            DocumentService documentService,
            DocumentSubService documentSubService,
            DocumentTaskService documentTaskService,
            DocumentTaskSubService documentTaskSubService,
            DocumentLogService documentLogService
    ) {
        this.userService = userService;
        this.documentService = documentService;
        this.documentSubService = documentSubService;
        this.documentTaskService = documentTaskService;
        this.documentTaskSubService = documentTaskSubService;
        this.documentLogService = documentLogService;
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

        List<DocumentTaskSub> documentTaskSubList = documentTaskSubs.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentTaskSubList.size());
        for (DocumentTaskSub documentTaskSub : documentTaskSubList) {
            Document document = documentService.getById(documentTaskSub.getDocumentId());
            JSONArray.add(new Object[]{
                    documentTaskSub.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    documentTaskSub.getContent(),
                    documentTaskSub.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(documentTaskSub.getCreatedAt()):"",
                    documentTaskSub.getDueDate()!=null? Common.uzbekistanDateFormat.format(documentTaskSub.getDueDate()):"",
                    documentTaskSub.getStatus(),
                    ""
            });
        }

        result.put("recordsTotal", documentTaskSubs.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentTaskSubs.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(DocUrls.IncomingView)
    public String getIncomingViewPage(
            @RequestParam(name = "id")Integer id,
            Model model
    ) {
        DocumentTaskSub documentTaskSub = documentTaskSubService.getById(id);
        if (documentTaskSub == null && documentTaskSub.getTaskId()==null) {
            return "redirect: " + DocUrls.IncomingList;
        }
        DocumentTask task = documentTaskService.getById(documentTaskSub.getTaskId());
        if (task == null) {
            return "redirect: " + DocUrls.IncomingList;
        }

        Document document = documentService.getById(task.getDocumentId());
        if (document == null) {
            return "redirect: " + DocUrls.IncomingList;
        }

        User user = userService.getCurrentUserFromContext();
        DocumentTask task = documentTaskService.getTaskByUser(document.getId(), user.getId());
        List<TaskStatus> taskStatuses = TaskStatus.getTaskStatusList();
        DocumentTaskSub taskSub = new DocumentTaskSub();
        List<TaskSubStatus> taskSubStatuses = TaskSubStatus.getTaskSubStatusList();
        if (task == null) {
            System.out.println("got subTask");
            taskSub = documentTaskSubService.getByUserAndDocId(user.getId(), document.getId());
        }


        List<DocumentTaskSub> documentTaskSubs = documentTaskSubService.getListByDocIdAndTaskId(document.getId(),task.getId());
        model.addAttribute("document", document);
        model.addAttribute("task", task);
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("documentTaskSub", documentTaskSub);
        model.addAttribute("documentTaskSubs", documentTaskSubs);
        model.addAttribute("user", userService.getCurrentUserFromContext());
        model.addAttribute("comment_url", DocUrls.AddComment);
        model.addAttribute("logs", documentLogService.getAllByDocId(document.getId()));
        model.addAttribute("task_change_url", DocUrls.DocumentTaskChange);
        model.addAttribute("task", (task != null) ? task : taskSub);
        model.addAttribute("task_type", (task != null) ? 1 : 2);
        model.addAttribute("task_statuses", (task != null) ? taskStatuses : taskSubStatuses);
        return DocTemplates.IncomingView;
    }

    @RequestMapping(value = DocUrls.IncomingTask)
    public String addTask(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "taskId")Integer taskId,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        DocumentTaskSub documentTaskSub = documentTaskSubService.getById(id);
        if (documentTaskSub == null) {
            return  "redirect:" + DocUrls.IncomingList;
        }
        Document document = documentService.getById(documentTaskSub.getDocumentId());
        if (document == null) {
            return  "redirect:" + DocUrls.IncomingList;
        }

        DocumentTask documentTask = documentTaskService.getByIdAndDocumentId(taskId,document.getId());
        if (documentTask == null) {
            return  "redirect:" + DocUrls.IncomingList;
        }

        List<User> userList = userService.getListByDepartmentAllParent(user.getDepartmentId());

        model.addAttribute("document", document);
        model.addAttribute("documentTask", documentTask);
        model.addAttribute("documentTaskSub", documentTaskSub);
        model.addAttribute("userList", userList);
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("action_url", DocUrls.IncomingTaskSubmit);
        model.addAttribute("back_url", DocUrls.IncomingView+"?id=" + document.getId());

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
                    documentTaskSubService.createNewSubTask(document.getId(),documentTask.getId(),content,dueDate,performerType,documentTaskSub.getReceiverId(),userId,userService.getUserDepartmentId(userId));
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
            @RequestParam(name = "content")String content,
            @RequestParam(name = "taskType")Integer type,
            @RequestParam(name = "taskStatus")Integer status,
            @RequestParam(name = "taskId")Integer taskId,
            @RequestParam(name = "docId")Integer docId
    ) {
        HashMap<String, Object> response = new HashMap<>();
        User user = userService.getCurrentUserFromContext();
        DocumentTask task;
        DocumentTaskSub taskSub;
        DocumentLog log = new DocumentLog();
        log.setCreatedById(user.getId());
        log.setContent(docId + "raqamli hujjat statusi " + user.getFullName() + "tomonidan o'zgardi.");
        log.setDocumentId(docId);
        log.setType(2);
        documentLogService.create(log);
        if (type == 1) {
            task = documentTaskService.getById(taskId);
            task.setStatus(status);
            task.setContent(content);
            task.setUpdateById(user.getId());
            documentTaskService.update(task);
            response.put("task", task);
        } else {
            taskSub = documentTaskSubService.getById(taskId);
            taskSub.setStatus(status);
            taskSub.setContent(content);
            taskSub.setUpdateById(user.getId());
            documentTaskSubService.update(taskSub);
            response.put("task", taskSub);
        }
        response.put("status", "success");
        response.put("log", log);
        return response;
    }
}