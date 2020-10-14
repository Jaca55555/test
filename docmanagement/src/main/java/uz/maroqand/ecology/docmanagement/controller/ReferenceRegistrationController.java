package uz.maroqand.ecology.docmanagement.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import uz.maroqand.ecology.docmanagement.dto.ReferenceRegFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.docmanagement.service.DocumentHelperService;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
@Controller
public class ReferenceRegistrationController {

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
    public ReferenceRegistrationController(
            DocumentTaskContentService documentTaskContentService,
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
            DocumentOrganizationService documentOrganizationService) {
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

    @RequestMapping(value = DocUrls.ReferenceRegistrationList, method = RequestMethod.GET)
    public String getReferenceRegistrationListPage(Model model) {
        User user = userService.getCurrentUserFromContext();
        model.addAttribute("newCount", taskService.countNewForReference());
        model.addAttribute("inProcess", taskService.countInProcessForReference());
        model.addAttribute("nearDate", taskService.countNearDate());
        model.addAttribute("expired", taskService.countExecutedForReference());
        model.addAttribute("executed", taskService.countExecuted());
        model.addAttribute("total", taskService.countTotal());
        model.addAttribute("documentViewList", documentViewService.getStatusActiveAndByType(user.getOrganizationId(),"AppealDocuments"));
        model.addAttribute("organizationList", organizationService.getStatusActive());
        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
        return DocTemplates.ReferenceRegistrationList;
    }

    @RequestMapping(value = DocUrls.ReferenceRegistrationList, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getReferenceRegistrationList(
            ReferenceRegFilterDTO referenceRegFilterDTO,
            Pageable pageable
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> result = new HashMap<>();
        Date deadlineDateBegin = null;
        Date deadlineDateEnd = null;
        Set<Integer> status = null;
        Calendar calendar = Calendar.getInstance();
        Boolean specialControll=null;
        Integer tabFilter = referenceRegFilterDTO.getTabFilter()!=null?referenceRegFilterDTO.getTabFilter():1;
        switch (tabFilter){
            case 3:
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                deadlineDateEnd = calendar.getTime();
                status = new LinkedHashSet<>();
                status.add(TaskStatus.New.getId());
                status.add(TaskStatus.InProgress.getId());
                status.add(TaskStatus.Checking.getId());
                break;//Муддати якинлашаётган
            case 4:
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                deadlineDateBegin = calendar.getTime();
                status = new LinkedHashSet<>();
                status.add(TaskStatus.New.getId());
                status.add(TaskStatus.InProgress.getId());
                status.add(TaskStatus.Checking.getId());
                break;//Муддати кеччикан
            case 5:
                status = new LinkedHashSet<>();
                status.add(TaskStatus.Checking.getId());
                break;//Ижро назоратида
            case 7:
                status = new LinkedHashSet<>();
                status.add(TaskStatus.Complete.getId());
                break;//Якунланган
            case 8:
                specialControll = Boolean.TRUE;
                break;//Якунланган
            default:
                break;//Жами
        }
        //todo documentTypeId=4
        Page<DocumentTask> documentTaskPage = taskService.findFilteredReference(user.getOrganizationId(), 4,referenceRegFilterDTO, deadlineDateBegin, deadlineDateEnd, null, status, null, null,specialControll, pageable);
        List<DocumentTask> documentTaskList = documentTaskPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentTaskList.size());
        String locale = LocaleContextHolder.getLocale().getLanguage();
        for (DocumentTask documentTask : documentTaskList) {
            Document document = documentService.getById(documentTask.getDocumentId());
            DocumentSub documentSub = documentSubService.getByDocumentIdForIncoming(document.getId());
            String docContent="";
            if (documentSub!=null && documentSub.getOrganizationId()!=null){
                DocumentOrganization documentOrganization = documentOrganizationService.getById(documentSub.getOrganizationId());
                docContent+=documentOrganization!=null?documentOrganization.getName()+".":"";
            }
            if (document.getDocRegNumber()!=null && document.getDocRegNumber()!=""){
                docContent+=" №"+ document.getDocRegNumber().trim()+",";
            }
            docContent+=document.getDocRegDate()!=null?( " " + documentHelperService.getTranslation("sys_date",locale) + ": " + Common.uzbekistanDateFormat.format(document.getDocRegDate())):"";
            docContent+="\n" + (document.getContent()!=null?"</br><span class='text-secondary' style='font-size:13px'>"+document.getContent().trim()+"</span>":"");
            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    docContent,
                    documentTask.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(documentTask.getCreatedAt()):"",
                    documentTask.getDueDate()!=null? Common.uzbekistanDateFormat.format(documentTask.getDueDate()):"",
                    documentTask.getStatus()!=null ? documentHelperService.getTranslation(TaskStatus.getTaskStatus(documentTask.getStatus()).getName(),locale):"",
                    documentTask.getContent(),
                    documentTask.getStatus(),
                    taskService.getDueColor(documentTask.getDueDate(),true,documentTask.getStatus(),locale)

            });
        }

        result.put("recordsTotal", documentTaskPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentTaskPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(value = DocUrls.ReferenceRegistrationNewList, method = RequestMethod.GET)
    public String getIncomingRegistrationNewListPage(){
        return DocTemplates.ReferenceRegistrationNewList;
    }

    @RequestMapping(value = DocUrls.ReferenceRegistrationNewList, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getReferenceRegistrationNewListAjax(Pageable pageable){
        System.out.println(pageable.getSort());
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();
        DocFilterDTO docFilterDTO = new DocFilterDTO();
        Set<DocumentStatus> documentStatuses = new HashSet<>();
        documentStatuses.add(DocumentStatus.New);
        docFilterDTO.setDocumentStatuses(documentStatuses);
        Page<Document> documentPage = documentService.findFiltered(docFilterDTO, user.getOrganizationId(),pageable);

        List<Document> documentList = documentPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentList.size());
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        for (Document document : documentList) {
            DocumentSub documentSub = documentSubService.getByDocumentIdForIncoming(document.getId());
            DocumentView documentView = documentViewService.getById(document.getDocumentViewId());
            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber()!=null?document.getRegistrationNumber():"",
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    document.getContent()!=null?document.getContent():"",
                    document.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(document.getCreatedAt()):"",
                    document.getManagerId()!=null?userService.findById(document.getManagerId()).getFullName():"",
                    documentHelperService.getTranslation(document.getStatus().getName(),locale),
                    document.getStatus().getId()
            });
        }

        result.put("recordsTotal", documentPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(DocUrls.ReferenceRegistrationView)
    public String getViewDocumentPage(
            @RequestParam(name = "id")Integer id,
            Model model
    ) {
        Document document = documentService.getById(id);
        if (document == null) {
            return "redirect:" + DocUrls.ReferenceRegistrationList;
        }
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
        model.addAttribute("special_controll_url", DocUrls.ReferenceSpecialControll);
        model.addAttribute("cancel_url",DocUrls.ReferenceRegistrationList );
        return DocTemplates.ReferenceRegistrationView;
    }

    @RequestMapping(value = DocUrls.ReferenceRegistrationNew, method = RequestMethod.GET)
    public String getNewDocumentPage(Model model) {

        model.addAttribute("document", new Document());
        model.addAttribute("documentSub", new DocumentSub());
        User user = userService.getCurrentUserFromContext();
        List<User> userList = userService.getEmployeesForForwarding(user.getOrganizationId());

        model.addAttribute("userList", userList);
//        model.addAttribute("journalList", journalService.getStatusActive(1));//todo 1
        model.addAttribute("documentViewList", documentViewService.getStatusActiveAndByType(user.getOrganizationId(),"AppealDocuments"));
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.findAllByOrganizationId(user.getOrganizationId()));
        model.addAttribute("taskContentList",documentTaskContentService.getTaskContentList(user.getOrganizationId()));
        model.addAttribute("managerUserList", userService.getEmployeesForDocManageOrganization("chief",user.getOrganizationId()));
        model.addAttribute("controlUserList", userService.getEmployeesForDocManageOrganization("controller",user.getOrganizationId()));
        model.addAttribute("organizationList", organizationService.getDocumentOrganizationNames());
        model.addAttribute("executeForms",ExecuteForm.getExecuteFormList());
        model.addAttribute("controlForms", ControlForm.getControlFormList());
        return DocTemplates.ReferenceRegistrationNew;
    }

    @Transactional
    @RequestMapping(value = {DocUrls.ReferenceRegistrationNew, DocUrls.ReferenceRegistrationNewTask}, method = RequestMethod.POST)
    public String createDocument(
            HttpServletRequest httpServletRequest,
//            @RequestParam(name = "journalId") Integer journalId,
            @RequestParam(name = "documentViewId") Integer documentViewId,
            @RequestParam(name = "docRegNumber") String docRegNumber,
            @RequestParam(name = "docRegDateStr") String docRegDateStr,
            @RequestParam(name = "communicationToolId") Integer communicationToolId,
            @RequestParam(name = "documentOrganizationId") String documentOrganizationId,
            @RequestParam(name = "contentId", required = false) Integer contentId,
            @RequestParam(name = "content", required = false) String content,
            @RequestParam(name = "address" ) String address,
            @RequestParam(name = "additionalDocumentId", required = false) Integer additionalDocumentId,
            @RequestParam(name = "fullName", required = false) String fullName,
            @RequestParam(name = "performerPhone", required = false) String performerPhone,
            @RequestParam(name = "managerId") Integer managerId,
            @RequestParam(name = "controlId", required = false) Integer controlId,
            @RequestParam(name = "insidePurpose", required = false) Boolean insidePurpose,
            @RequestParam(name = "executeFormId", required = false) Integer executeFormId,
            @RequestParam(name = "controlFormId", required = false) Integer controlFormId,
            @RequestParam(name = "file_ids")List<Integer> file_ids,
            @RequestParam(name = "specialControl",required = false)Boolean specialControll,
            @RequestBody MultiValueMap<String, String> formData

    ) {
        User user = userService.getCurrentUserFromContext();
        Set<File> files = new HashSet<>();
        for(Integer id: file_ids) {
            if (id != null) files.add(fileService.findById(id));
        }

        Document document = new Document();
//        document.setJournalId(journalId);
        document.setDocumentViewId(documentViewId);
        document.setDocRegNumber(docRegNumber);
        document.setDocRegDate(DateParser.TryParse(docRegDateStr, Common.uzbekistanDateFormat));
        document.setContentId(contentId);
        document.setContent(content);
        document.setAdditionalDocumentId(additionalDocumentId);
        document.setPerformerPhone(performerPhone);
        document.setManagerId(managerId);
        document.setControlId(controlId);
        document.setInsidePurpose(insidePurpose);

        if(executeFormId!=null){
            document.setExecuteForm(ExecuteForm.getExecuteForm(executeFormId));
        }
        if(controlFormId!=null){
            document.setControlForm(ControlForm.getControlForm(controlFormId));
        }
        document.setContentFiles(files);
        document.setCreatedById(user.getId());

        if(specialControll!=null) { document.setSpecialControll(specialControll);} else{ document.setSpecialControll(Boolean.FALSE);}
        document.setStatus(DocumentStatus.New);
        document.setRegistrationNumber(docRegNumber);
        document = documentService.createReference(4, document, user);

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
        documentSub.setFullName(fullName);
        documentSub.setAddress(address);
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
            String value= mapEntry.getValue().replaceAll(" "," ");
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
        return "redirect:" + DocUrls.ReferenceRegistrationList;

       /* if(httpServletRequest.getRequestURI().equals(DocUrls.ReferenceRegistrationNewTask)){
            return "redirect:" + DocUrls.ReferenceRegistrationView + "?id=" + document.getId();
        }else {
            return "redirect:" + DocUrls.ReferenceRegistrationList;
        }*/
    }

    @RequestMapping(DocUrls.ReferenceRegistrationEdit)
    public String getEditDocumentPage(@RequestParam(name = "id")Integer id, Model model) {
        Document document = documentService.getById(id);
        if (document == null) {
            return "redirect:" + DocUrls.ReferenceRegistrationList;
        }


        DocumentSub documentSub =  documentSubService.getByDocumentIdForIncoming(document.getId());
        model.addAttribute("document", document);
        model.addAttribute("documentSub",documentSub);

        DocumentOrganization documentOrdanization = null;
        Document  additionalDocument = null;
        String  additionalDocumentText = null;
        if (document.getAdditionalDocumentId()!=null){
            additionalDocument = documentService.getById(document.getAdditionalDocumentId());
            additionalDocumentText = additionalDocument.getRegistrationNumber() +" - "+ Common.uzbekistanDateAndTimeFormat.format(additionalDocument.getRegistrationDate());
        }
        if (documentSub.getOrganizationId()!=null){
            documentOrdanization = organizationService.getById(documentSub.getOrganizationId());

        }
        User user = userService.getCurrentUserFromContext();
        model.addAttribute("document", document);
        model.addAttribute("additionalDocument", additionalDocument);
        model.addAttribute("additionalDocumentText", additionalDocumentText);
        model.addAttribute("documentOrdanization", documentOrdanization);
//        model.addAttribute("journalList", journalService.getStatusActive(1));//todo 1
        model.addAttribute("documentViewList", documentViewService.getStatusActiveAndByType(user.getOrganizationId(),"AppealDocuments"));
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.findAllByOrganizationId(user.getOrganizationId()));
        model.addAttribute("managerUserList", userService.getEmployeesForNewDoc("chief"));
        model.addAttribute("controlUserList", userService.getEmployeesForNewDoc("controller"));

        model.addAttribute("executeForms",ExecuteForm.getExecuteFormList());
        model.addAttribute("controlForms", ControlForm.getControlFormList());
        model.addAttribute("cancel_url",DocUrls.ReferenceRegistrationList );

        return DocTemplates.ReferenceRegistrationNew;
    }

    @RequestMapping(value = {DocUrls.ReferenceRegistrationEdit, DocUrls.ReferenceRegistrationEditTask}, method = RequestMethod.POST)
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
            return "redirect:" + DocUrls.ReferenceRegistrationList;
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

        if(httpServletRequest.getRequestURL().toString().equals(DocUrls.ReferenceRegistrationEditTask)){
            return "redirect:" + DocUrls.ReferenceRegistrationTask + "?id=" + document1.getId();
        }else {
            return "redirect:" + DocUrls.ReferenceRegistrationNewList;
        }
    }

    @RequestMapping(value = DocUrls.ReferenceRegistrationTask)
    public String addTask( @RequestParam(name = "id")Integer id, Model model ) {
        Document document = documentService.getById(id);
        if (document == null) {
            return  "redirect:" + DocUrls.ReferenceRegistrationList;
        }

        List<User> userList = userService.getEmployeesForForwarding(document.getOrganizationId());
        User user = userService.getCurrentUserFromContext();
        model.addAttribute("document", document);
        model.addAttribute("userList", userList);
        model.addAttribute("descriptionList", documentTaskContentService.getTaskContentList(user.getOrganizationId()));
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("action_url", DocUrls.ReferenceRegistrationTaskSubmit);
        model.addAttribute("back_url", DocUrls.ReferenceRegistrationView+"?id=" + document.getId());

        return DocTemplates.ReferenceRegistrationTask;
    }

    @RequestMapping(value = DocUrls.ReferenceRegistrationTaskSubmit)
    public String ReferenceRegistrationTaskSubmit(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "docRegDateStr") String docRegDateStr,
            @RequestBody MultiValueMap<String, String> formData
    ){
        User user = userService.getCurrentUserFromContext();
        Document document = documentService.getById(id);
        if (document == null){
            return "redirect:" + DocUrls.ReferenceRegistrationList;
        }
        DocumentTask documentTask = taskService.createNewTask(document,TaskStatus.New.getId(),content,DateParser.TryParse(docRegDateStr, Common.uzbekistanDateFormat),document.getManagerId(),user.getId());
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
        }
        if(!document.getStatus().equals(DocumentStatus.InProgress)){
            document.setStatus(DocumentStatus.InProgress);
            documentService.update(document);
        }
        return "redirect:" + DocUrls.ReferenceRegistrationView + "?id=" + document.getId();
    }

    @RequestMapping(value = DocUrls.ReferenceRegistrationUserName)
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

    @GetMapping(value = DocUrls.ReferenceSpecialControll)
    @ResponseBody
    public HashMap<String, Object> toggleSpecialControl(@RequestParam(name = "id")Integer id) {
        HashMap<String, Object> response = new HashMap<>();
        Document document = documentService.getById(id);
        document.setSpecialControll(!document.getSpecialControll());
        documentService.update(document);
        response.put("status", "success");
        return response;
    }

    @PostMapping(value = DocUrls.ReferenceMailFileUpload)
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



}
