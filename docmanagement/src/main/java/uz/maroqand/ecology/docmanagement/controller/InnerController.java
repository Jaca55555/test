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
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.*;
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
    private final HelperService helperService;
    private final CommunicationToolService communicationToolService;
    private final DocumentDescriptionService documentDescriptionService;
    private final DocumentHelperService documentHelperService;
    private final DocumentLogService documentLogService;
    private final DocumentTaskContentService documentTaskContentService;

    public InnerController(DocumentTaskContentService documentTaskContentService,HelperService helperService, UserService userService, DocumentService documentService, DocumentSubService documentSubService, DocumentTaskService documentTaskService, DocumentTaskSubService documentTaskSubService, DocumentViewService documentViewService, JournalService journalService, CommunicationToolService communicationToolService, DocumentDescriptionService documentDescriptionService, DocumentHelperService documentHelperService, DocumentLogService documentLogService) {
        this.userService = userService;
        this.documentService = documentService;
        this.documentSubService = documentSubService;
        this.documentTaskService = documentTaskService;
        this.documentTaskSubService = documentTaskSubService;
        this.documentViewService = documentViewService;
        this.journalService = journalService;
        this.helperService=helperService;
        this.communicationToolService = communicationToolService;
        this.documentDescriptionService = documentDescriptionService;
        this.documentHelperService = documentHelperService;
        this.documentLogService = documentLogService;
        this.documentTaskContentService=documentTaskContentService;
    }

    @RequestMapping(value = DocUrls.InnerList, method = RequestMethod.GET)
    public String getInnerListPage(@RequestParam(name = "tab_number", required = false)Integer tabNumber, Model model) {
        User user = userService.getCurrentUserFromContext();
        model.addAttribute("tab_number_", tabNumber);
        model.addAttribute("taskSubTypeList", TaskSubType.getTaskSubTypeList());
        model.addAttribute("taskSubStatusList", TaskSubStatus.getTaskSubStatusList());
        model.addAttribute("performerList", userService.getEmployeeList());
        model.addAttribute("statistic", documentTaskSubService.countAllByTypeAndReceiverId(DocumentTypeEnum.InnerDocuments.getId(),user.getId()));
        model.addAttribute("journalList", journalService.getStatusActive(user.getOrganizationId(),1));//todo 3
        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.findAllByOrganizationId(user.getOrganizationId()));
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
        Boolean specialControll=null;

        switch (tabFilter){
            case 1: break;
            case 9: status = new LinkedHashSet<>();
                status.add(TaskSubStatus.Initial.getId());
                status.add(TaskSubStatus.New.getId());
                break;
            case 2:  type = TaskSubType.Performer.getId();
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.New.getId());
                status.add(TaskSubStatus.InProgress.getId());
                status.add(TaskSubStatus.Waiting.getId());
                status.add(TaskSubStatus.Agreement.getId());
                status.add(TaskSubStatus.Rejected.getId());
                status.add(TaskSubStatus.ForChangeDueDate.getId());
                status.add(TaskSubStatus.ForChangePerformer.getId());
                status.add(TaskSubStatus.DueDateChanged.getId());
                status.add(TaskSubStatus.DueDateChangedDeny.getId());
                status.add(TaskSubStatus.PerformerDeny.getId());
                break;
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
            /*case 6: type = TaskSubType.Info.getId();break;//Малъумот учун */
            case 7:
                status = new LinkedHashSet<>();
                status.add(TaskSubStatus.Complete.getId());
                break;//Якунланган
            case 8:
                specialControll = Boolean.TRUE;
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
                Collections.singletonList(3), //todo documentTypeId=3
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
                    document.getRegistrationDate()!=null ? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    docContent,
                    documentTaskSub.getCreatedAt()!=null ? Common.uzbekistanDateFormat.format(documentTaskSub.getCreatedAt()):"",
                    documentTaskSub.getDueDate()!=null ? Common.uzbekistanDateFormat.format(documentTaskSub.getDueDate()):"",
                    documentTaskSub.getStatus()!=null ? documentHelperService.getTranslation(TaskSubStatus.getTaskStatus(documentTaskSub.getStatus()).getName(),locale):"",
                    documentTaskSub.getContent(),
                    documentTaskSub.getStatus(),
                    documentTaskService.getDueColor(documentTaskSub.getDueDate(),false,documentTaskSub.getStatus(),locale),
                    userService.findById(documentTaskSub.getReceiverId()).getFullName(),
                    document.getContentFiles(),
                    document.getExecuteForm().getId(),
                    document.getSpecialControll()!=null? document.getSpecialControll().booleanValue():""

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
            return "redirect:" + DocUrls.InnerList;
        }

        if (documentTaskSub.getStatus().equals(TaskSubStatus.New.getId())){
            documentTaskSub.setStatus(TaskSubStatus.InProgress.getId());
            documentTaskSubService.update(documentTaskSub);
        }
  if (documentTaskSub.getStatus().equals(TaskSubStatus.Rejected.getId())){
            documentTaskSub.setStatus(TaskSubStatus.InProgress.getId());
            documentTaskSubService.update(documentTaskSub);
        }
        if(documentTaskSub.getType()!=null){
            if(documentTaskSub.getType()==3){
                documentTaskSub.setStatus(TaskSubStatus.Complete.getId());
                documentTaskSubService.update(documentTaskSub);
            }}
        if (documentTaskSub.getStatus().equals(TaskSubStatus.DueDateChanged.getId())){
            documentTaskSub.setStatus(TaskSubStatus.InProgress.getId());
            documentTaskSubService.update(documentTaskSub);
        }
        Document document = documentService.getById(documentTaskSub.getDocumentId());
        if (document == null) {
            return "redirect:" + DocUrls.InnerList;
        }
        if (documentTaskSub.getStatus().equals(TaskSubStatus.DueDateChangedDeny.getId())){
            documentTaskSub.setStatus(TaskSubStatus.InProgress.getId());
            documentTaskSubService.update(documentTaskSub);
        }
        if (documentTaskSub.getStatus().equals(TaskSubStatus.PerformerDeny.getId())){
            documentTaskSub.setStatus(TaskSubStatus.InProgress.getId());
            documentTaskSubService.update(documentTaskSub);
        }

        DocumentTask documentTask = documentTaskService.getById(documentTaskSub.getTaskId());
        List<TaskSubStatus> statuses = new LinkedList<>();
        statuses.add(TaskSubStatus.InProgress);
        statuses.add(TaskSubStatus.Waiting);
        statuses.add(TaskSubStatus.Agreement);
        statuses.add(TaskSubStatus.Checking);
        statuses.add(TaskSubStatus.ForChangePerformer);
        statuses.add(TaskSubStatus.ForChangeDueDate);
        List<Integer> docTypes = new ArrayList<>();
        docTypes.add(DocumentTypeEnum.OutgoingDocuments.getId());
        docTypes.add(DocumentTypeEnum.InnerDocuments.getId());
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        List<DocumentTaskSub> documentTaskSubs = documentTaskSubService.getListByDocIdAndTaskId(document.getId(),documentTask.getId());
        model.addAttribute("document", document);
        model.addAttribute("executeForm",document.getExecuteForm().getName());
        model.addAttribute("documentLog", new DocumentLog());
        model.addAttribute("resolutionDocument", documentTaskService.resolutionCreateByTaskId(documentTask.getId(),locale));
        model.addAttribute("tree", documentService.createTree(document));

        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("documentTask", documentTask);
        model.addAttribute("documentTaskSub", documentTaskSub);
        model.addAttribute("status",TaskSubStatus.getTaskStatus(documentTaskSub.getStatus()).getName());
        model.addAttribute("documentTaskSubs", documentTaskSubs);
        model.addAttribute("dueDate",Common.uzbekistanDateFormat.format(documentTaskSub.getDueDate()));
        model.addAttribute("user", userService.getCurrentUserFromContext());
        model.addAttribute("comment_url", DocUrls.AddComment);
        model.addAttribute("logs", documentLogService.getAllByDocId(document.getId()));
        model.addAttribute("special_controll_url", DocUrls.InnerRegistrationControll);
        model.addAttribute("cancel_url",DocUrls.InnerList );
        model.addAttribute("task_change_url", DocUrls.DocumentTaskChange);
        model.addAttribute("task_statuses", statuses);
        model.addAttribute("docList", documentService.findAllByDocumentTypeIn(docTypes, PageRequest.of(0,100, Sort.Direction.DESC, "id")));
        model.addAttribute("isView", true);
        model.addAttribute("files",document.getContentFiles());

        return DocTemplates.InnerView;
    }

    @RequestMapping(value = DocUrls.InnerTask)
    public String addTask(
            @RequestParam(name = "id")Integer taskSubId,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        DocumentTaskSub documentTaskSub = documentTaskSubService.getById(taskSubId);
        if (documentTaskSub == null) {
            return  "redirect:" + DocUrls.InnerList;
        }
        Document document = documentService.getById(documentTaskSub.getDocumentId());
        if (document == null) {
            return  "redirect:" + DocUrls.InnerList;
        }
        DocumentTask documentTask = documentTaskService.getByIdAndDocumentId(documentTaskSub.getTaskId(),document.getId());
        if (documentTask == null) {
            return  "redirect:" + DocUrls.InnerList;
        }

        if (Boolean.TRUE.equals(document.getInsidePurpose())) {
            if (user.getId().equals(documentTask.getPerformerId())) {
                document.setInsidePurpose(Boolean.FALSE);
            }
        }

        List<User> userList = userService.getListByDepartmentAllParent(user.getDepartmentId());
        boolean isExecuteForm = false;
        if (document.getExecuteForm()!=null && document.getExecuteForm().equals(ExecuteForm.Performance)){
            isExecuteForm = true;
        }
        model.addAttribute("status",TaskSubStatus.getTaskStatus(documentTaskSub.getStatus()).getName());
        model.addAttribute("executeForm",document.getExecuteForm().getName());
        model.addAttribute("document", document);
        model.addAttribute("isExecuteForm", isExecuteForm);
        model.addAttribute("documentTask", documentTask);
        model.addAttribute("documentTaskSub", documentTaskSub);
        model.addAttribute("userList", userList);
        model.addAttribute("descriptionList", documentTaskContentService.getTaskContentList(user.getOrganizationId()));
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("action_url", DocUrls.InnerTaskSubmit);
        model.addAttribute("back_url", DocUrls.InnerView+"?id=" + documentTaskSub.getId());
        return DocTemplates.InnerTask;
    }

    @RequestMapping(value = DocUrls.InnerTaskSubmit)
    public String innerTaskSubmit(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "taskId") Integer taskId,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "docRegDateStr", required = false) String docRegDateStr,
            @RequestBody MultiValueMap<String, String> formData
    ){
        User user = userService.getCurrentUserFromContext();
        DocumentTaskSub documentTaskSub = documentTaskSubService.getById(id);
        if (documentTaskSub==null){
            return "redirect:" + DocUrls.InnerList;
        }

        if (documentTaskSub.getStatus().equals(TaskSubStatus.New.getId())){
            documentTaskSub.setStatus(TaskSubStatus.InProgress.getId());
            documentTaskSubService.update(documentTaskSub);
        }

        Document document = documentService.getById(documentTaskSub.getDocumentId());
        if (document==null){
            return "redirect:" + DocUrls.InnerList;
        }

        DocumentTask documentTask = documentTaskService.getByIdAndDocumentId(taskId,document.getId());
        if (documentTask==null){
            return "redirect:" + DocUrls.InnerList;
        }
        boolean isExecuteForm=false;
        if (document.getExecuteForm()!=null && document.getExecuteForm().equals(ExecuteForm.Performance)){
            isExecuteForm = true;
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
                if (!isExecuteForm){
                    documentTaskSubService.createNewSubTask(0,document,documentTask.getId(),content,dueDate,performerType,documentTask.getChiefId(),userId,userService.getUserDepartmentId(userId));
                    userId = null;
                    performerType = null;
                    dueDate = null;
                }
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

        return "redirect:" + DocUrls.InnerView + "?id=" + documentTaskSub.getId();
    }

    @RequestMapping(value = DocUrls.InnerTaskUserName)
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

}
