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

    public DocumentCheckController(
            UserService userService,
            PositionService positionService,
            HelperService helperService,
            DocumentService documentService,
            DocumentSubService documentSubService,
            DocumentTaskService documentTaskService,
            DocumentTaskSubService documentTaskSubService,
            DocumentLogService documentLogService
    ) {
        this.userService = userService;
        this.positionService = positionService;
        this.helperService = helperService;
        this.documentService = documentService;
        this.documentSubService = documentSubService;
        this.documentTaskService = documentTaskService;
        this.documentTaskSubService = documentTaskSubService;
        this.documentLogService = documentLogService;
    }

    @RequestMapping(value = DocUrls.DocumentCheckList, method = RequestMethod.GET)
    public String getDocumentCheckListPage(Model model) {
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
        return DocTemplates.DocumentCheckList;
    }

    @RequestMapping(value = DocUrls.DocumentCheckList, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getDocumentCheckRegistrationList(
            IncomingRegFilter incomingRegFilter,
            Pageable pageable
    ) {

        System.out.println(incomingRegFilter.getTabFilter());
        incomingRegFilter.setStatus(TaskStatus.Checking.toString());
        HashMap<String, Object> result = new HashMap<>();
        Page<DocumentTask> documentTaskPage = documentTaskService.findFiltered(incomingRegFilter, null, null, null, null, null, null, pageable);
        List<DocumentTask> documentTaskList = documentTaskPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentTaskList.size());
        String locale = LocaleContextHolder.getLocale().getLanguage();
        for (DocumentTask documentTask : documentTaskList) {
            Document document = documentService.getById(documentTask.getDocumentId());
            JSONArray.add(new Object[]{
                    documentTask.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    documentTask.getContent(),
                    documentTask.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(documentTask.getCreatedAt()):"",
                    documentTask.getDueDate()!=null? Common.uzbekistanDateFormat.format(documentTask.getDueDate()):"",
                    documentTask.getStatus()!=null ? helperService.getTranslation(TaskStatus.getTaskStatus(documentTask.getStatus()).getName(),locale):"",
                    documentTask.getContent()
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
        List<TaskSubStatus> statuses = new LinkedList<>();
        statuses.add(TaskSubStatus.InProgress);
        statuses.add(TaskSubStatus.Waiting);
        statuses.add(TaskSubStatus.Agreement);
        DocFilterDTO docFilterDTO = new DocFilterDTO();
        docFilterDTO.setDocumentTypeEnum(DocumentTypeEnum.OutgoingDocuments);
        List<DocumentTaskSub> documentTaskSubs = documentTaskSubService.getListByDocIdAndTaskId(document.getId(),task.getId());
        model.addAttribute("document", document);
        model.addAttribute("task", task);
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("documentTaskSubs", documentTaskSubs);
        model.addAttribute("user", userService.getCurrentUserFromContext());
        model.addAttribute("comment_url", DocUrls.AddComment);
        model.addAttribute("logs", documentLogService.getAllByDocId(document.getId()));
        model.addAttribute("task_statuses", statuses);
        model.addAttribute("docList", documentService.findFiltered(docFilterDTO, new PageRequest(0,100, Sort.Direction.DESC, "id")));
        return DocTemplates.DocumentCheckView;
    }

    @RequestMapping(DocUrls.DocumentCheckComplete)
    public String documentCheckComplete(
            @RequestParam(name = "id") Integer id
    ){
        User user = userService.getCurrentUserFromContext();
        DocumentTask documentTask = documentTaskService.getById(id);
        if (documentTask==null){
            return "redirect:" + DocUrls.DocumentCheckList;
        }

        Document document = documentService.getById(documentTask.getDocumentId());
        if (document==null){
            return "redirect:" + DocUrls.DocumentCheckList;
        }

        documentTask.setStatus(TaskStatus.Complete.getId());
        documentTaskService.update(documentTask);

        return "redirect:" + DocUrls.DocumentCheckView + "?id=" + documentTask.getId();

    }
}

