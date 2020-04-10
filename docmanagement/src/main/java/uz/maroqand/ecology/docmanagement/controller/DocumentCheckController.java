package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.PositionService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.dto.IncomingRegFilter;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentLog;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz) Kiruvchi xatlar
 */
@Controller
public class DocumentCheckController {

    private final UserService userService;
    private final PositionService positionService;
    private final HelperService helperService;
    private final DocumentService documentService;
    private final DocumentSubService documentSubService;
    private final DocumentTaskService documentTaskService;
    private final DocumentTaskSubService documentTaskSubService;
    private final DocumentLogService documentLogService;
    private final FileService fileService;

    public DocumentCheckController(
            UserService userService,
            PositionService positionService,
            HelperService helperService,
            DocumentService documentService,
            DocumentSubService documentSubService,
            DocumentTaskService documentTaskService,
            DocumentTaskSubService documentTaskSubService,
            DocumentLogService documentLogService,
            FileService fileService) {
        this.userService = userService;
        this.positionService = positionService;
        this.helperService = helperService;
        this.documentService = documentService;
        this.documentSubService = documentSubService;
        this.documentTaskService = documentTaskService;
        this.documentTaskSubService = documentTaskSubService;
        this.documentLogService = documentLogService;
        this.fileService = fileService;
    }

    @RequestMapping(value = DocUrls.DocumentCheckList, method = RequestMethod.GET)
    public String getDocumentCheckListPage(Model model) {
        User user = userService.getCurrentUserFromContext();
        Set<Integer> statuses = new LinkedHashSet<>();



        statuses.add(TaskStatus.Checking.getId());
        statuses.add(TaskStatus.Complete.getId());
        statuses.add(TaskStatus.Rejected.getId());
        model.addAttribute("allCount", documentTaskService.getCountTaskStatus(statuses));

        statuses = new LinkedHashSet<>();
        statuses.add(TaskStatus.Checking.getId());
        model.addAttribute("inProgressCount", documentTaskService.getCountTaskStatus(statuses));

        statuses = new LinkedHashSet<>();
        statuses.add(TaskStatus.Complete.getId());
        model.addAttribute("inCompleteCount", documentTaskService.getCountTaskStatus(statuses));

        statuses = new LinkedHashSet<>();
        statuses.add(TaskStatus.Rejected.getId());
        model.addAttribute("inRejectedCount", documentTaskService.getCountTaskStatus(statuses));

        model.addAttribute("taskSubTypeList", TaskSubType.getTaskSubTypeList());
        model.addAttribute("taskSubStatusList", TaskSubStatus.getTaskSubStatusList());
        model.addAttribute("performerList", userService.getEmployeeList());
        return DocTemplates.DocumentCheckList;
    }

    @RequestMapping(value = DocUrls.DocumentCheckList, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getDocumentCheckRegistrationList(
            IncomingRegFilter incomingRegFilter,
            Pageable pageable
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> result = new HashMap<>();
        Integer tabFilter = incomingRegFilter.getTabFilter();
        Set<Integer> taskStatuses = new HashSet<>();
        if (tabFilter==null || tabFilter>3){
            taskStatuses.add(TaskStatus.Checking.getId());
            taskStatuses.add(TaskStatus.Complete.getId());
            taskStatuses.add(TaskStatus.Rejected.getId());
        }else if(tabFilter==1){
            taskStatuses.add(TaskStatus.Checking.getId());
        }else if(tabFilter==2){
            taskStatuses.add(TaskStatus.Complete.getId());
        }else if(tabFilter==3){
            taskStatuses.add(TaskStatus.Rejected.getId());
        }
        //barcha hujjatlar ko'rinishi uchun documentTypeId=null
        Page<DocumentTask> documentTaskPage = documentTaskService.findFiltered(user.getOrganizationId(), null, incomingRegFilter, null, null, taskStatuses, null, null, null,pageable);
        List<DocumentTask> documentTaskList = documentTaskPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentTaskList.size());
        String locale = LocaleContextHolder.getLocale().getLanguage();
        for (DocumentTask documentTask : documentTaskList) {
            Document document = documentService.getById(documentTask.getDocumentId());
            JSONArray.add(new Object[]{
                    documentTask.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    document.getContent(),
                    documentTask.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(documentTask.getCreatedAt()):"",
                    documentTask.getDueDate()!=null? Common.uzbekistanDateFormat.format(documentTask.getDueDate()):"",
                    documentTask.getStatus()!=null ? helperService.getTranslation(TaskStatus.getTaskStatus(documentTask.getStatus()).getName(),locale):"",
                    documentTask.getContent(),
                    documentTask.getStatus()
            });
        }

        result.put("recordsTotal", documentTaskPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentTaskPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(DocUrls.DocumentCheckView)
    public String getDocumentViewPage(
            @RequestParam(name = "id")Integer taskId,
            Model model
    ) {

        DocumentTask task = documentTaskService.getById(taskId);
        if (task == null) {
            return "redirect:" + DocUrls.DocumentCheckList;
        }

        Document document = documentService.getById(task.getDocumentId());
        if (document == null) {
            return "redirect:" + DocUrls.DocumentCheckList;
        }
        List<TaskStatus> taskStatuses = new ArrayList<>();
        taskStatuses.add(TaskStatus.Complete);
        taskStatuses.add(TaskStatus.Rejected);
        List<DocumentTaskSub> documentTaskSubs = documentTaskSubService.getListByDocIdAndTaskId(document.getId(),task.getId());
        model.addAttribute("document", document);
        model.addAttribute("documentLog", new DocumentLog());
        model.addAttribute("tree", documentService.createTree(document));
        model.addAttribute("task", task);
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("documentTaskSubs", documentTaskSubs);
        model.addAttribute("user", userService.getCurrentUserFromContext());
        model.addAttribute("comment_url", DocUrls.AddComment);
        model.addAttribute("logs", documentLogService.getAllByDocId(document.getId()));
        model.addAttribute("task_statuses",taskStatuses);
        return DocTemplates.DocumentCheckView;
    }

    @RequestMapping(value = DocUrls.DocumentCheckComplete, method = RequestMethod.POST)
    public String documentCheckComplete(
            @RequestParam(name = "status") Integer status,
            @RequestParam(name = "status_file_ids", required = false) List<Integer> file_ids,
            DocumentLog documentLog
            ){
        User user = userService.getCurrentUserFromContext();
        DocumentTask documentTask = documentTaskService.getById(documentLog.getTaskId());
        if (documentTask==null){
            return "redirect:" + DocUrls.DocumentCheckList;
        }

        Document document = documentService.getById(documentTask.getDocumentId());
        if (document==null){
            return "redirect:" + DocUrls.DocumentCheckList;
        }

        TaskStatus oldStatus = TaskStatus.getTaskStatus(documentTask.getStatus());
        TaskStatus newStatus = TaskStatus.getTaskStatus(status);

        documentTask.setStatus(status);
        documentTaskService.update(documentTask);

        if (documentTask.getStatus().equals(TaskStatus.Complete.getId())){
            documentTaskSubService.allTaskSubCompleteGetTaskId(documentTask.getId());
            List<DocumentTask> documentTaskList = documentTaskService.getByDocumetId(document.getId());
            if (documentTaskList.size()==1){
                document.setStatus(DocumentStatus.Completed);
                documentService.update(document);
            }
        }

        documentLogService.createLog(documentLog,DocumentLogType.Log.getId(),file_ids,oldStatus.getName(),oldStatus.getColor(),newStatus.getName(),newStatus.getColor(),user.getId());
        return "redirect:" + DocUrls.DocumentCheckView + "?id=" + documentTask.getId();

    }
}

