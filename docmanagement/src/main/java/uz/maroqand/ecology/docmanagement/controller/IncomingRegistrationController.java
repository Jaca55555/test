package uz.maroqand.ecology.docmanagement.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.dto.IncomingRegFilter;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.docmanagement.service.DocumentHelperService;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 24.12.2019
 */

@Controller
public class IncomingRegistrationController {

    private static DocumentTaskSubService documentTaskSubService;
    private final DocumentService documentService;
    private final JournalService journalService;
    private final DocumentViewService documentViewService;
    private final CommunicationToolService communicationToolService;
    private final UserService userService;
    private final FileService fileService;
    private final DocumentOrganizationService organizationService;
    private final DocumentDescriptionService documentDescriptionService;
    private final DocumentSubService documentSubService;
    private final DocumentTaskService taskService;
    private final DocumentTaskSubService taskSubService;
    private final DocumentLogService documentLogService;
    private final DocumentHelperService documentHelperService;
    private final DocumentOrganizationService documentOrganizationService;
    private final DocumentTaskContentService documentTaskContentService;
    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Autowired
    public IncomingRegistrationController(
            DocumentService documentService,
            DocumentDescriptionService documentDescriptionService,
            CommunicationToolService communicationToolService,
            UserService userService,
            JournalService journalService,
            FileService fileService,
            DocumentOrganizationService organizationService,
            DocumentViewService documentViewService,
            DocumentSubService documentSubService,
            DocumentTaskService taskService,
            DocumentTaskSubService taskSubService,
            DocumentLogService documentLogService,
            DocumentHelperService documentHelperService,
            DocumentOrganizationService documentOrganizationService,
            DocumentTaskContentService documentTaskContentService
    ) {
        this.documentService = documentService;
        this.documentSubService = documentSubService;
        this.taskService = taskService;
        this.taskSubService = taskSubService;
        this.documentLogService = documentLogService;
        this.documentDescriptionService = documentDescriptionService;
        this.communicationToolService = communicationToolService;
        this.userService = userService;
        this.journalService = journalService;
        this.fileService = fileService;
        this.organizationService = organizationService;
        this.documentViewService = documentViewService;
        this.documentHelperService = documentHelperService;
        this.documentOrganizationService = documentOrganizationService;
        this.documentTaskContentService=documentTaskContentService;
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationList, method = RequestMethod.GET)
    public String getIncomingRegistrationListPage(Model model) {

        Integer organizationId = userService.getCurrentUserFromContext().getOrganizationId();
        Integer departmentId = userService.getCurrentUserFromContext().getDepartmentId();

        Calendar calendar = Calendar.getInstance();
        Date end = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date begin = calendar.getTime();


        model.addAttribute("new_", taskService.countAllTasksByDocumentTypeIdAndTaskStatus(organizationId, departmentId, 1, TaskStatus.New.getId()));

        model.addAttribute("inProgress", taskService.countAllTasksByDocumentTypeIdAndTaskStatus(organizationId, departmentId, 1, TaskStatus.InProgress.getId()));

        model.addAttribute("nearDueDate", taskService.countAllTasksByDocumentTypeIdAndDueDateBetween(organizationId, departmentId, 1, begin, end));

        model.addAttribute("expired", taskService.countAllTasksByDocumentTypeIdAndDueDateBefore(organizationId, departmentId, 1, end));

        model.addAttribute("completed", taskService.countAllTasksByDocumentTypeIdAndTaskStatus(organizationId, departmentId, 1, TaskStatus.Complete.getId()));

        model.addAttribute("all", taskService.countAllTasksByDocumentTypeId(organizationId, departmentId, 1));

        model.addAttribute("documentViewList", documentViewService.getStatusActiveAndByType("IncomingDocuments"));
        model.addAttribute("organizationList", organizationService.getStatusActive());
        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
        return DocTemplates.IncomingRegistrationList;
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationList, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getIncomingRegistrationList(
            IncomingRegFilter incomingRegFilter,
            Pageable pageable
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> result = new HashMap<>();
        Date deadlineDateBegin = null;
        Date deadlineDateEnd = null;
        Set<DocumentStatus> status = null;
        Calendar calendar = Calendar.getInstance();
        Boolean specialControl = null;

        DocFilterDTO docFilterDTO = new DocFilterDTO();

        Integer tabFilter = incomingRegFilter.getTabFilter() != null ? incomingRegFilter.getTabFilter() : 9;
        switch (tabFilter){
            case 1:
                status = new LinkedHashSet<>();
                status.add(DocumentStatus.New);
                break;
            case 2:
                status = new LinkedHashSet<>();
                status.add(DocumentStatus.New);
                break;
            case 3:
                deadlineDateEnd = calendar.getTime();
                status = new LinkedHashSet<>();
                status.add(DocumentStatus.New);
                status.add(DocumentStatus.InProgress);
                break;//Муддати кеччикан
            case 4:
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                deadlineDateEnd = calendar.getTime();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                deadlineDateBegin = calendar.getTime();
                status = new LinkedHashSet<>();
                status.add(DocumentStatus.New);
                status.add(DocumentStatus.InProgress);
                break;//Муддати якинлашаётган
            case 5:
                /*status = new LinkedHashSet<>();
                status.add(TaskStatus.Checking.getId());*/
                break;//Ижро назоратида
            case 7:
                status = new LinkedHashSet<>();
                status.add(DocumentStatus.Completed);
                break;//Якунланган
            case 8:
                specialControl = Boolean.TRUE;
                break;//Якунланган
            case 9:
                status = new LinkedHashSet<>();
                status.add(DocumentStatus.New);
                status.add(DocumentStatus.InProgress);
                status.add(DocumentStatus.Completed);
                break;
            default:
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(
                        Sort.Order.asc("status"),
                        Sort.Order.desc("dueDate")
                ));
                break;//Жами
        }
        //todo documentTypeId=1

        docFilterDTO.setExecuteDateBegin(deadlineDateBegin!=null?Common.uzbekistanDateAndTimeFormat.format(deadlineDateBegin):null);
        docFilterDTO.setExecuteDateEnd(deadlineDateEnd!=null?Common.uzbekistanDateAndTimeFormat.format(deadlineDateEnd):null);
        docFilterDTO.setDocumentType(DocumentTypeEnum.IncomingDocuments.getId());
        docFilterDTO.setDocumentStatuses(status);
        Page<Document> documentPage = documentService.findFiltered(docFilterDTO, pageable);

        List<Document> documentList = documentPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentList.size());
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        for (Document document : documentList) {
            DocumentSub documentSub = documentSubService.getByDocumentIdForIncoming(document.getId());
            DocumentView documentView = documentViewService.getById(document.getDocumentViewId());
            String documentTypeName="";

            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber()!=null?document.getRegistrationNumber():"",
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    document.getContent()!=null?document.getContent():"",
                    document.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(document.getCreatedAt()):"",
                    document.getManagerId()!=null?userService.findById(document.getManagerId()).getFullName():"",
                    documentHelperService.getTranslation(document.getStatus().getName(),locale),
                    document.getStatus().getId(),
                    document.getDocumentTypeId()!=null?document.getDocumentType().getNameTranslation(locale):""
            });
        }

       /* Page<DocumentTask> documentTaskPage = taskService.findFiltered(user.getOrganizationId(), 1, incomingRegFilter, deadlineDateBegin, deadlineDateEnd, status, user.getDepartmentId(), null, specialControl, pageable);
        List<DocumentTask> documentTaskList = documentTaskPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentTaskList.size());
        String locale = LocaleContextHolder.getLocale().getLanguage();

        for (DocumentTask documentTask : documentTaskList) {
            Document document = documentService.getById(documentTask.getDocumentId());
            DocumentSub documentSub = documentSubService.getByDocumentIdForIncoming(document.getId());
            String docContent = "";
            if (documentSub!=null && documentSub.getOrganizationId()!=null){
                DocumentOrganization documentOrganization = documentOrganizationService.getById(documentSub.getOrganizationId());
                docContent+=documentOrganization!=null?documentOrganization.getName()+".":"";
            }
            if (document.getDocRegNumber()!=null && document.getDocRegNumber()!=""){
                docContent+=" №"+ document.getDocRegNumber().trim()+",";
            }
            docContent += document.getDocRegDate()!=null?( " " + documentHelperService.getTranslation("sys_date", locale) + ": " + Common.uzbekistanDateFormat.format(document.getDocRegDate())):"";
            docContent+="\n" + (document.getContent()!=null?"</br><span class='text-secondary' style='font-size:13px'>"+document.getContent().trim()+"</span>":"");
            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    docContent,
                    documentTask.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(documentTask.getCreatedAt()):"",
                    documentTask.getDueDate()!=null? Common.uzbekistanDateFormat.format(documentTask.getDueDate()):"",
                    documentTask.getStatus() != null ? documentHelperService.getTranslation(TaskStatus.getTaskStatus(documentTask.getStatus()).getName(),locale):"",
                    documentTask.getContent(),
                    documentTask.getStatus(),
                    taskService.getDueColor(documentTask.getDueDate(),true,documentTask.getStatus(),locale)

            });
        }*/

        result.put("recordsTotal", documentPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationNewList, method = RequestMethod.GET)
    public String getIncomingRegistrationNewListPage(){
        return DocTemplates.IncomingRegistrationNewList;
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationNewList, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getIncomingRegistrationNewListAjax(Pageable pageable){
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();
        DocFilterDTO docFilterDTO = new DocFilterDTO();
        Set<DocumentStatus>  documentStatuses = new HashSet<>();
        documentStatuses.add(DocumentStatus.New);
        docFilterDTO.setDocumentStatuses(documentStatuses);
        docFilterDTO.setChief(user.getId());
        Page<Document> documentPage = documentService.findFiltered(docFilterDTO, pageable);

        List<Document> documentList = documentPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentList.size());
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        for (Document document : documentList) {
            DocumentSub documentSub = documentSubService.getByDocumentIdForIncoming(document.getId());
            DocumentView documentView = documentViewService.getById(document.getDocumentViewId());
            String documentTypeName="";

            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber()!=null?document.getRegistrationNumber():"",
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    document.getContent()!=null?document.getContent():"",
                    document.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(document.getCreatedAt()):"",
                    document.getManagerId()!=null?userService.findById(document.getManagerId()).getFullName():"",
                    documentHelperService.getTranslation(document.getStatus().getName(),locale),
                    document.getStatus().getId(),
                    document.getDocumentTypeId()!=null?document.getDocumentType().getNameTranslation(locale):""
            });
        }

        result.put("recordsTotal", documentPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(DocUrls.IncomingRegistrationView)
    public String getViewDocumentPage(
            @RequestParam(name = "id")Integer id,
            Model model
    ) {
        Document document = documentService.getById(id);
        if (document == null) {
            return "redirect:" + DocUrls.IncomingRegistrationList;
        }
//        if (document.getDocumentTypeId().equals(DocumentTypeEnum.InnerDocuments.getId())){
//            return "redirect:" + DocUrls.InnerRegistrationView + "?id=" + id;
//        }
//
//        if (document.getDocumentTypeId().equals(DocumentTypeEnum.OutgoingDocuments.getId())){
//            return "redirect:" + DocUrls.OutgoingMailView + "?id=" + id;
//        }
        List<DocumentTask> documentTasks = taskService.getByDocumetId(document.getId());
        List<DocumentTaskSub> documentTaskSubs = taskSubService.getListByDocId(document.getId());
        model.addAttribute("document", document);
        model.addAttribute("documentLog", new DocumentLog());
        model.addAttribute("tree", documentService.createTree(document));
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("documentTasks", documentTasks);
        model.addAttribute("documentTaskSubs", documentTaskSubs);
        model.addAttribute("user", userService.getCurrentUserFromContext());
        model.addAttribute("comment_url", DocUrls.AddComment);
        model.addAttribute("logs", documentLogService.getAllByDocId(document.getId()));
        model.addAttribute("specialControll", true);
        model.addAttribute("special_controll_url", DocUrls.IncomingSpecialControll);
        model.addAttribute("cancel_url",DocUrls.IncomingRegistrationList );
        return DocTemplates.IncomingRegistrationView;
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationNew, method = RequestMethod.GET)
    public String getNewDocumentPage(Model model) {

        User user = userService.getCurrentUserFromContext();
        model.addAttribute("document", new Document());
        model.addAttribute("documentSub", new DocumentSub());
        List<User> userList = userService.getEmployeesForForwarding(user.getOrganizationId());

        model.addAttribute("journalList", journalService.getStatusActive(1));//todo 1
        model.addAttribute("documentViewList", documentViewService.getStatusActiveAndByType("IncomingDocuments"));
        model.addAttribute("userList", userList);
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());
        model.addAttribute("taskContentList", documentTaskContentService.getTaskContentList());
        model.addAttribute("managerUserList", userService.getEmployeesForNewDoc("chief"));
        model.addAttribute("controlUserList", userService.getEmployeesForNewDoc("controller"));
        model.addAttribute("organizationList", organizationService.getDocumentOrganizationNames());
        model.addAttribute("executeForms",ExecuteForm.getExecuteFormList());
        model.addAttribute("controlForms", ControlForm.getControlFormList());
        return DocTemplates.IncomingRegistrationNew;
    }

    @Transactional
    @RequestMapping(value = {DocUrls.IncomingRegistrationNew, DocUrls.IncomingRegistrationNewTask}, method = RequestMethod.POST)
    public String createDocument(
            HttpServletRequest httpServletRequest,
            @RequestParam(name = "journalId") Integer journalId,
            @RequestParam(name = "documentViewId") Integer documentViewId,
            @RequestParam(name = "docRegNumber") String docRegNumber,
            @RequestParam(name = "docRegDateStr") String docRegDateStr,
            @RequestParam(name = "communicationToolId") Integer communicationToolId,
            @RequestParam(name = "documentOrganizationId") String documentOrganizationId,
            @RequestParam(name = "contentId", required = false) Integer contentId,
            @RequestParam(name = "content", required = false) String content,
            @RequestParam(name = "additionalDocumentId", required = false) Integer additionalDocumentId,
            @RequestParam(name = "performerName", required = false) String performerName,
            @RequestParam(name = "performerPhone", required = false) String performerPhone,
            @RequestParam(name = "managerId") Integer managerId,
            @RequestParam(name = "controlId", required = false) Integer controlId,
            @RequestParam(name = "insidePurpose", required = false ,defaultValue = "") Boolean insidePurpose,
            @RequestParam(name = "executeFormId", required = false) Integer executeFormId,
            @RequestParam(name = "controlFormId", required = false) Integer controlFormId,
            @RequestParam(name = "fileIds", required = false) List<Integer> fileIds,
            @RequestBody MultiValueMap<String, String> formData

    ) {
        User user = userService.getCurrentUserFromContext();
        Set<File> files = new HashSet<>();
        if(fileIds!=null){
            for (Integer fileId : fileIds) {
                files.add(fileService.findById(fileId));
            }
        }

        Document document = new Document();
        document.setJournalId(journalId);
        document.setDocumentViewId(documentViewId);
        document.setDocRegNumber(docRegNumber);
        document.setDocRegDate(DateParser.TryParse(docRegDateStr, Common.uzbekistanDateFormat));
        document.setContentId(contentId);
        document.setContent(content);
        document.setAdditionalDocumentId(additionalDocumentId);
        document.setPerformerName(performerName);
        document.setPerformerPhone(performerPhone);
        document.setManagerId(managerId);
        document.setControlId(controlId);
        document.setInsidePurpose(insidePurpose);
        document.setDepartmentId(user.getDepartmentId());
        if(executeFormId!=null){
            document.setExecuteForm(ExecuteForm.getExecuteForm(executeFormId));
        }
        if(controlFormId!=null){
            document.setControlForm(ControlForm.getControlForm(controlFormId));
        }
        document.setContentFiles(files);
        document.setCreatedById(user.getId());

        document.setSpecialControll(Boolean.FALSE);
        document.setStatus(DocumentStatus.New);
        document = documentService.createDoc(DocumentTypeEnum.IncomingDocuments.getId(), document, user);

        Integer documentOrganizationId1;
        try {
            documentOrganizationId1 = Integer.parseInt(documentOrganizationId);
        }catch(NumberFormatException ex){
            System.out.println("creating new document organization with '" + documentOrganizationId + "' name");
            DocumentOrganization documentOrganization = new DocumentOrganization();
            documentOrganization.setName(documentOrganizationId);
            documentOrganization.setStatus(true);
            documentOrganization.setCreatedById(user.getId());
            documentOrganizationId1 = organizationService.create(documentOrganization).getId();
        }
        DocumentSub documentSub = new DocumentSub();
        documentSub.setCommunicationToolId(communicationToolId);
        documentSub.setOrganizationId(documentOrganizationId1);
        documentSubService.create(document.getId(), documentSub, user);

        DocumentTask documentTask = null;
//        DocumentTask documentTask = taskService.createNewTask(document,TaskStatus.New.getId(),content,DateParser.TryParse(docRegDateStr, Common.uzbekistanDateFormat),document.getManagerId(),user.getId());
        Integer userId = null;
        Integer performerType = null;
        Date dueDate = null;
        String descriptionTextareaTask = null;
        Date taskDocRegDateStr = null;
        Map<String,String> map = formData.toSingleValueMap();
        for (Map.Entry<String,String> mapEntry: map.entrySet()) {

            String[] paramName =  mapEntry.getKey().split("_");
            String  tagName = paramName[0];
            String value= mapEntry.getValue().replaceAll(" ","");
            if (tagName.equals("descriptionTextareaTask")){
                descriptionTextareaTask = value;
            }
            if (tagName.equals("taskDocRegDateStr")){
                taskDocRegDateStr = DateParser.TryParse(value, Common.uzbekistanDateFormat);
            }
            if (tagName.equals("taskNumber")){
                documentTask = taskService.createNewTask(document,TaskStatus.New.getId(),descriptionTextareaTask,taskDocRegDateStr,document.getManagerId(),user.getId());
                descriptionTextareaTask = "";
                taskDocRegDateStr = null;
            }
            if (tagName.equals("user")){
                userId=Integer.parseInt(value);
            }
            if (tagName.equals("performer")){
                performerType = Integer.parseInt(value);
            }

            if (tagName.equals("dueDateStr")){
                dueDate = DateParser.TryParse(value, Common.uzbekistanDateFormat);
                if (userId!=null && performerType!=null && documentTask!=null){
                    taskSubService.createNewSubTask(0,document,documentTask.getId(),documentTask.getContent(),dueDate,performerType,documentTask.getChiefId(),userId,userService.getUserDepartmentId(userId));
                    if (performerType==TaskSubType.Performer.getId()){
                        documentTask.setPerformerId(userId);
                        taskService.update(documentTask);
                    }
                    userId = null;
                    performerType = null;
                    dueDate = null;
                }
            }
        }
        if(!document.getStatus().equals(DocumentStatus.InProgress)){
            document.setStatus(DocumentStatus.InProgress);
            documentService.update(document);
        }
        return "redirect:" + DocUrls.IncomingRegistrationList;


        /*if(httpServletRequest.getRequestURI().equals(DocUrls.IncomingRegistrationNewTask)){
            return "redirect:" + DocUrls.IncomingRegistrationView + "?id=" + document.getId();
        }else {
            return "redirect:" + DocUrls.IncomingRegistrationList;
        }*/

    }

    @RequestMapping(DocUrls.IncomingRegistrationEdit)
    public String getEditDocumentPage(@RequestParam(name = "id")Integer id, Model model) {
        Document document = documentService.getById(id);
        if (document == null) {
            return "redirect:" + DocUrls.IncomingRegistrationList;
        }

        DocumentSub documentSub =  documentSubService.getByDocumentIdForIncoming(document.getId());
        model.addAttribute("document", document);
        model.addAttribute("documentSub",documentSub);

        DocumentOrganization documentOrdanization = null;
        Document  additionalDocument = null;
        String  additionalDocumentText = null;
        if (document.getAdditionalDocumentId()!=null){
            additionalDocument = documentService.getById(document.getAdditionalDocumentId());
            additionalDocumentText = additionalDocument.getRegistrationNumber();
        }
        if (documentSub.getOrganizationId()!=null){
            documentOrdanization = organizationService.getById(documentSub.getOrganizationId());
        }
        model.addAttribute("document", document);
        model.addAttribute("additionalDocument", additionalDocument);
        model.addAttribute("additionalDocumentText", additionalDocumentText);
        model.addAttribute("documentOrdanization", documentOrdanization);

        model.addAttribute("journalList", journalService.getStatusActive(1));//todo 1
        model.addAttribute("documentViewList", documentViewService.getStatusActiveAndByType("IncomingDocuments"));
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());
        model.addAttribute("documentTaskContent",documentTaskContentService.getTaskContentList());
        model.addAttribute("managerUserList", userService.getEmployeesForNewDoc("chief"));
        model.addAttribute("controlUserList", userService.getEmployeesForNewDoc("controller"));

        model.addAttribute("executeForms",ExecuteForm.getExecuteFormList());
        model.addAttribute("controlForms", ControlForm.getControlFormList());
        model.addAttribute("cancel_url",DocUrls.IncomingRegistrationList );

        return DocTemplates.IncomingRegistrationNew;
    }

    @RequestMapping(value = {DocUrls.IncomingRegistrationEdit, DocUrls.IncomingRegistrationEditTask}, method = RequestMethod.POST)
    public String updateDocument(
            HttpServletRequest httpServletRequest,
            @RequestParam(name = "docRegDateStr") String docRegDateStr,
            @RequestParam(name = "communicationToolId") Integer communicationToolId,
            @RequestParam(name = "documentOrganizationId") String documentOrganizationId,
            @RequestParam(name = "docSubId") Integer docSubId,
            @RequestParam(name = "executeFormId", required = false) Integer executeForm,
            @RequestParam(name = "controlFormId", required = false) Integer controlForm,
            @RequestParam(name = "fileIds", required = false) List<Integer> fileIds,
            Document document
    ) {
        User user = userService.getCurrentUserFromContext();
        Set<File> files = new HashSet<>();
        if(fileIds!=null){
            for (Integer fileId : fileIds) {
                files.add(fileService.findById(fileId));
            }
        }

        Document document1 = documentService.getById(document.getId());

        if (document1==null){
            return "redirect:" + DocUrls.IncomingRegistrationList;
        }
        Integer documentOrganizationId1;
        try {
            documentOrganizationId1 = Integer.parseInt(documentOrganizationId);
        }catch(NumberFormatException ex){
            System.out.println("creating new document organization with '" + documentOrganizationId + "' name");
            DocumentOrganization documentOrganization = new DocumentOrganization();
            documentOrganization.setName(documentOrganizationId);
            documentOrganization.setStatus(true);
            documentOrganization.setCreatedById(user.getId());
            documentOrganizationId1 = organizationService.create(documentOrganization).getId();
        }

        documentService.updateAllParameters(document,docSubId,executeForm,controlForm,files,communicationToolId,documentOrganizationId1,DateParser.TryParse(docRegDateStr, Common.uzbekistanDateFormat),user);

        if(httpServletRequest.getRequestURL().toString().equals(DocUrls.IncomingRegistrationEditTask)){
            return "redirect:" + DocUrls.IncomingRegistrationTask + "?id=" + document1.getId();
        }else {
            return "redirect:" + DocUrls.IncomingRegistrationNewList;
        }
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationTask)
    public String addTask( @RequestParam(name = "id")Integer id, Model model ) {
            Document document = documentService.getById(id);
            if (document == null) {
                return  "redirect:" + DocUrls.IncomingRegistrationList;
            }

            List<User> userList = userService.getEmployeesForForwarding(document.getOrganizationId());
            boolean isExecuteForm = false;
            if (document.getExecuteForm()!=null && document.getExecuteForm().equals(ExecuteForm.Performance)){
                isExecuteForm = true;
            }
    //        List<User> userList = userService.getEmployeesForForwarding(document.getOrganizationId());

            model.addAttribute("document", document);
            model.addAttribute("userList", userList);
            model.addAttribute("tasksubtype",document.getExecuteForm());
            model.addAttribute("isExecuteForm", isExecuteForm);
            model.addAttribute("tasksubtype1",document.getExecuteForm().getName());

            System.out.println(document.getExecuteForm().getName());
            model.addAttribute("descriptionList", documentTaskContentService.getTaskContentList());
            model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
            model.addAttribute("action_url", DocUrls.IncomingRegistrationTaskSubmit);
            model.addAttribute("back_url", DocUrls.IncomingRegistrationView+"?id=" + document.getId());

            return DocTemplates.IncomingRegistrationTask;
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationTaskSubmit)
    public String IncomingRegistrationTaskSubmit(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "docRegDateStr",required = false) String docRegDateStr,
            @RequestBody MultiValueMap<String, String> formData
    ){
        User user = userService.getCurrentUserFromContext();
        Document document = documentService.getById(id);
        if (document == null){
            return "redirect:" + DocUrls.IncomingRegistrationList;
        }
        System.out.println(docRegDateStr);
        DocumentTask documentTask = taskService.createNewTask(document,TaskStatus.New.getId(),content,DateParser.TryParse(docRegDateStr, Common.uzbekistanDateFormat),document.getManagerId(),user.getId());
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

//            if (tagName.equals("dueDateStr")){
                dueDate = DateParser.TryParse(value, Common.uzbekistanDateFormat);
                if (userId!=null && performerType!=null){
                    taskSubService.createNewSubTask(0,document,documentTask.getId(),content,dueDate,performerType,documentTask.getChiefId(),userId,userService.getUserDepartmentId(userId));
                    if (performerType==TaskSubType.Performer.getId()){
                        documentTask.setPerformerId(userId);
                        taskService.update(documentTask);
                    }
                     userId = null;
                     performerType = null;
                     dueDate = null;
                }
            }
//        }
        if(!document.getStatus().equals(DocumentStatus.InProgress)){
            document.setStatus(DocumentStatus.InProgress);
            documentService.update(document);
        }
        return "redirect:" + DocUrls.IncomingRegistrationView + "?id=" + document.getId();
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationUserName)
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

    @GetMapping(value = DocUrls.IncomingSpecialControll)
    @ResponseBody
    public HashMap<String, Object> toggleSpecialControl(@RequestParam(name = "id")Integer id) {
        HashMap<String, Object> response = new HashMap<>();
        Document document = documentService.getById(id);
        document.setSpecialControll(!document.getSpecialControll());
        documentService.update(document);
        response.put("status", "success");
        return response;
    }

    @PostMapping(value = DocUrls.IncomeMailFileUpload)
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "file")MultipartFile uploadFile
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> response = new HashMap<>();

        File file = fileService.uploadFile(uploadFile,user.getId(),"documentFile",uploadFile.getOriginalFilename());
        response.put("data", file);
        return response;
    }
    @RequestMapping(value = DocUrls.IncomingFileDelete, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public File deleteAttachment(@RequestParam(name = "id")Integer id){
        File file = fileService.findById(id);
        file.setDeleted(true);
        file.setDateDeleted(new Date());
        file.setDeletedById(userService.getCurrentUserFromContext().getId());
        return fileService.save(file);
    }
    @GetMapping(value = DocUrls.FileDownload)
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam(name = "id")Integer id) {
        File file = fileService.findById(id);
        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    /*@GetMapping(value = DocUrls.IncomeMailAddTask)
    public String getAddTaskPage(
            Model model,
            @RequestParam(name = "id")Integer id
    ) {
        Document document = documentService.getById(id);
        if (document == null) {
            return  "redirect:" + DocUrls.IncomeMailList;
        }

        model.addAttribute("doc", document);
        model.addAttribute("task", document.getTask());
        model.addAttribute("attends", userService.getEmployeeList());
        return DocTemplates.IncomeMailAddTask;
    }*/

}
