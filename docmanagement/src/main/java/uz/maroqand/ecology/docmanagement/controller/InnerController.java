package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentLog;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.service.DocumentHelperService;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import java.util.*;

@Controller
public class InnerController {

    private final UserService userService;
    private final DocumentService documentService;
    private final DocumentSubService documentSubService;
    private final DocumentTaskService documentTaskService;
    private final DocumentTaskSubService documentTaskSubService;
    private final DocumentViewService documentViewService;
    private final JournalService journalService;
    private final CommunicationToolService communicationToolService;
    private final DocumentDescriptionService documentDescriptionService;
    private final DocumentHelperService documentHelperService;
    private final DocumentLogService documentLogService;

    public InnerController(UserService userService, DocumentService documentService, DocumentSubService documentSubService, DocumentTaskService documentTaskService, DocumentTaskSubService documentTaskSubService, DocumentViewService documentViewService, JournalService journalService, CommunicationToolService communicationToolService, DocumentDescriptionService documentDescriptionService, DocumentHelperService documentHelperService, DocumentLogService documentLogService) {
        this.userService = userService;
        this.documentService = documentService;
        this.documentSubService = documentSubService;
        this.documentTaskService = documentTaskService;
        this.documentTaskSubService = documentTaskSubService;
        this.documentViewService = documentViewService;
        this.journalService = journalService;
        this.communicationToolService = communicationToolService;
        this.documentDescriptionService = documentDescriptionService;
        this.documentHelperService = documentHelperService;
        this.documentLogService = documentLogService;
    }

    @RequestMapping(value = DocUrls.InnerList, method = RequestMethod.GET)
    public String getInnerListPage(Model model) {
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
        model.addAttribute("journalList", journalService.getStatusActive(3));//todo 3
        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());
        return DocTemplates.InnerList;
    }

    @RequestMapping(value = DocUrls.InnerList, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getInnerListAjax(
            @RequestParam(name = "documentOrganizationId",required = false,defaultValue = "") Integer documentOrganizationId,
            @RequestParam(name = "docRegNumber",required = false,defaultValue = "") String docRegNumber,
            @RequestParam(name = "registrationNumber",required = false,defaultValue = "") String registrationNumber,
            @RequestParam(name = "dateBegin",required = false,defaultValue = "") String dateBeginStr,
            @RequestParam(name = "dateEnd",required = false,defaultValue = "") String dateEndStr,
            @RequestParam(name = "taskContent",required = false,defaultValue = "") String taskContent,
            @RequestParam(name = "content",required = false,defaultValue = "") String content,
            @RequestParam(name = "performerId",required = false,defaultValue = "") Integer performerId,
            @RequestParam(name = "taskSubType",required = false,defaultValue = "") Integer taskSubType,
            @RequestParam(name = "taskSubStatus",required = false,defaultValue = "") Integer taskSubStatus,
            @RequestParam(name = "tabFilter",required = false,defaultValue = "") Integer tabFilter,
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
                3, //todo documentTypeId=3
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
                    documentTaskSub.getStatus()!=null ? documentHelperService.getTranslation(TaskSubStatus.getTaskStatus(documentTaskSub.getStatus()).getName(),locale):"",
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

    @RequestMapping(DocUrls.InnerView)
    public String getInnerViewPage(
            @RequestParam(name = "id")Integer id,
            Model model
    ) {
        DocumentTaskSub documentTaskSub = documentTaskSubService.getById(id);
        if (documentTaskSub == null) {
            return "redirect: " + DocUrls.IncomingRegistrationList;
        }

        Document document = documentService.getById(documentTaskSub.getDocumentId());
        if (document == null) {
            return "redirect: " + DocUrls.IncomingRegistrationList;
        }

        DocumentTask documentTask = documentTaskService.getById(documentTaskSub.getTaskId());
        List<TaskSubStatus> statuses = new LinkedList<>();
        statuses.add(TaskSubStatus.InProgress);
        statuses.add(TaskSubStatus.Waiting);
        statuses.add(TaskSubStatus.Agreement);
        statuses.add(TaskSubStatus.Checking);
        DocFilterDTO docFilterDTO = new DocFilterDTO();
        docFilterDTO.setDocumentTypeEnum(DocumentTypeEnum.OutgoingDocuments);

        List<DocumentTaskSub> documentTaskSubs = documentTaskSubService.getListByDocIdAndTaskId(document.getId(),documentTask.getId());
        model.addAttribute("document", document);
        model.addAttribute("documentLog", new DocumentLog());
        model.addAttribute("tree", documentService.createTree(document));
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("documentTask", documentTask);
        model.addAttribute("documentTaskSub", documentTaskSub);
        model.addAttribute("documentTaskSubs", documentTaskSubs);
        model.addAttribute("user", userService.getCurrentUserFromContext());
        model.addAttribute("comment_url", DocUrls.AddComment);
        model.addAttribute("logs", documentLogService.getAllByDocId(document.getId()));
        model.addAttribute("specialControll", true);
        model.addAttribute("special_controll_url", DocUrls.IncomingSpecialControll);
        model.addAttribute("cancel_url",DocUrls.IncomingRegistrationList );
        model.addAttribute("task_change_url", DocUrls.DocumentTaskChange);
        model.addAttribute("task_statuses", statuses);
        model.addAttribute("docList", documentService.findFiltered(docFilterDTO, PageRequest.of(0,100, Sort.Direction.DESC, "id")));
        model.addAttribute("isView", true);

        return DocTemplates.InnerView;
    }

}
