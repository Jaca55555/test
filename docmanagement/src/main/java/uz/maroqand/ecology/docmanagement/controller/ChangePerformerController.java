package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.PositionService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import java.util.*;

import static uz.maroqand.ecology.docmanagement.constant.DocUrls.*;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz) Kiruvchi xatlar
 */
@Controller
public class ChangePerformerController {
    private final DocumentTaskSubService taskSubService;
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
    private final DocumentTaskService taskService;
    public ChangePerformerController(
            UserService userService,
            PositionService positionService,
            HelperService helperService,
            DocumentService documentService,
            DocumentSubService documentSubService,
            DocumentTaskService documentTaskService,
            DocumentTaskSubService documentTaskSubService,
            DocumentLogService documentLogService,
            DocumentTaskService taskService,
            DocumentTaskSubService taskSubService,
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
        this.taskService=taskService;
        this.taskSubService=taskSubService;
    }

    @RequestMapping(value = DocUrls.ChangePerformerList, method = RequestMethod.GET)
    public String getChangePerformerListPage(Model model) {
        User user = userService.getCurrentUserFromContext();
        return DocTemplates.ChangePerformerList;
    }

    @RequestMapping(value = DocUrls.ChangePerformerList, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getChangePerformerList(
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
        Set<Integer> status =null;
        Integer departmentId = null;
        Integer receiverId = user.getId();
        Calendar calendar = Calendar.getInstance();
        Boolean specialControll=null;
        status = new LinkedHashSet<>();
        status.add(TaskSubStatus.ForChangePerformer.getId());
        status.add(TaskSubStatus.PerformerChanged.getId());
        System.out.println(status);
        HashMap<String, Object> result = new HashMap<>();
        Page<DocumentTaskSub> documentTaskSubs = documentTaskSubService.findFilter(
                user.getOrganizationId(),
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
                null,
                specialControll,
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
                    documentTaskService.getDueColor(documentTaskSub.getDueDate(),false,documentTaskSub.getStatus(),locale)

            });
        }

        result.put("recordsTotal", documentTaskSubs.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentTaskSubs.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }
    @RequestMapping(ChangePerformerView)
    public String getIncomingViewPage(
            @RequestParam(name = "id")Integer taskSubId,
            Model model
    ) {
        DocumentTaskSub documentTaskSub = documentTaskSubService.getById(taskSubId);
        if (documentTaskSub == null || documentTaskSub.getTaskId() == null) {
            return "redirect:" + DocUrls.IncomingList;
        }
        if (documentTaskSub.getStatus().equals(TaskSubStatus.New.getId())){
            documentTaskSub.setStatus(TaskSubStatus.InProgress.getId());
            documentTaskSubService.update(documentTaskSub);
        }
        DocumentTask task = documentTaskService.getById(documentTaskSub.getTaskId());
        if (task == null || task.getDocumentId()==null) {
            return "redirect:" + DocUrls.IncomingList;
        }

        Document document = documentService.getById(task.getDocumentId());
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
        statuses.add(TaskSubStatus.ForChangePerformer);
        List<Integer> docTypes = new ArrayList<>();
        docTypes.add(DocumentTypeEnum.OutgoingDocuments.getId());
        docTypes.add(DocumentTypeEnum.InnerDocuments.getId());
        List<DocumentTaskSub> documentTaskSubs = documentTaskSubService.getListByDocIdAndTaskId(document.getId(),task.getId());
        model.addAttribute("document", document);
        model.addAttribute("executeForm",document.getExecuteForm().getName());
        model.addAttribute("task", task);
        model.addAttribute("documentLog", new DocumentLog());
        model.addAttribute("tree", documentService.createTree(document));
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("documentTaskSub", documentTaskSub);
        model.addAttribute("documentTaskSubs", documentTaskSubs);
        model.addAttribute("user", userService.getCurrentUserFromContext());
        model.addAttribute("comment_url", DocUrls.AddComment);
        model.addAttribute("logs", documentLogService.getAllByDocId(document.getId()));
        model.addAttribute("task_change_url", DocUrls.DocumentTaskChange);
        model.addAttribute("task_statuses", statuses);
        model.addAttribute("docList", documentService.findAllByDocumentTypeIn(docTypes, PageRequest.of(0,100, Sort.Direction.DESC, "id")));
        model.addAttribute("isView", true);
        model.addAttribute("performers",userService.getEmployeesForDocManage("controller"));
        model.addAttribute("action_url",ChangePerformerTask);
        model.addAttribute("action_uri",ChangePerformerDeny);
        return DocTemplates.ChangePerformerView;
    }
    @PostMapping(ChangePerformerTask)
    public String changePerformer(
            DocumentTaskSub documentTaskSub,
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "userid")Integer userid
    ) {
        DocumentTaskSub documentTaskSub1=documentTaskSubService.getById(id);
        System.out.println(userid);
        documentTaskSub1.setReceiverId(userid);
        documentTaskSub1.setStatus(9);
        documentTaskSubService.update(documentTaskSub1);
        return "redirect:" + DocUrls.ChangePerformerList;
    }
    @PostMapping(ChangePerformerDeny)
    public String denyPerformer(
            DocumentTaskSub documentTaskSub,
            @RequestParam(name = "id")Integer id
    ) {
        DocumentTaskSub documentTaskSub1=documentTaskSubService.getById(id);
        documentTaskSub1.setStatus(documentTaskSub.getStatus());
        documentTaskSubService.update(documentTaskSub1);
        return "redirect:" + DocUrls.ChangePerformerList;
    }
}
