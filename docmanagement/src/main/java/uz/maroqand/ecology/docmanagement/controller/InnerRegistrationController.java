package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.dto.IncomingRegFilter;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.docmanagement.service.DocumentHelperService;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
import java.util.List;


@Controller
public class InnerRegistrationController {

    private final UserService userService;
    private final FileService fileService;
    private final DocumentService documentService;
    private final DocumentViewService documentViewService;
    private final JournalService journalService;
    private final DocumentDescriptionService documentDescriptionService;
    private final DocumentTaskService documentTaskService;
    private final DocumentTaskSubService documentTaskSubService;
    private final DocumentSubService documentSubService;
    private final DocumentLogService documentLogService;
    private final DocumentOrganizationService documentOrganizationService;
    private final DocumentHelperService documentHelperService;
    private final DocumentTaskContentService documentTaskContentService;
    private final CommunicationToolService communicationToolService;
    private final OrganizationService organizationService;

    @Autowired
    public InnerRegistrationController(DocumentTaskContentService documentTaskContentService,UserService userService, FileService fileService, DocumentService documentService, DocumentViewService documentViewService, JournalService journalService, DocumentDescriptionService documentDescriptionService, DocumentTaskService documentTaskService, DocumentTaskSubService documentTaskSubService, DocumentSubService documentSubService, DocumentLogService documentLogService, DocumentOrganizationService documentOrganizationService, DocumentHelperService documentHelperService) {
    public InnerRegistrationController(UserService userService, FileService fileService, DocumentService documentService, DocumentViewService documentViewService, JournalService journalService, DocumentDescriptionService documentDescriptionService, DocumentTaskService documentTaskService, DocumentTaskSubService documentTaskSubService, DocumentSubService documentSubService, DocumentLogService documentLogService, DocumentOrganizationService documentOrganizationService, DocumentHelperService documentHelperService, CommunicationToolService communicationToolService, OrganizationService organizationService) {
        this.userService = userService;
        this.fileService = fileService;
        this.documentService = documentService;
        this.documentViewService = documentViewService;
        this.journalService = journalService;
        this.documentDescriptionService = documentDescriptionService;
        this.documentTaskService = documentTaskService;
        this.documentTaskSubService = documentTaskSubService;
        this.documentSubService = documentSubService;
        this.documentLogService = documentLogService;
        this.documentOrganizationService = documentOrganizationService;
        this.documentHelperService = documentHelperService;
        this.documentTaskContentService=documentTaskContentService;
        this.communicationToolService = communicationToolService;
        this.organizationService = organizationService;
    }

    @RequestMapping(value = DocUrls.InnerRegistrationList, method = RequestMethod.GET)
    public String getIncomeListPage(Model model) {
        User user = userService.getCurrentUserFromContext();
        Integer organizationId = user.getOrganizationId();
        Integer departmentId = user.getDepartmentId();

        Calendar calendar = Calendar.getInstance();
        Date end = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date begin = calendar.getTime();

        model.addAttribute("new_", documentTaskService.countAllTasksByDocumentTypeIdAndTaskStatus(organizationId, departmentId, 3, TaskStatus.New.getId()));

        model.addAttribute("inProgress", documentTaskService.countAllTasksByDocumentTypeIdAndTaskStatus(organizationId, departmentId, 3, TaskStatus.InProgress.getId()));

        model.addAttribute("nearDueDate", documentTaskService.countAllTasksByDocumentTypeIdAndDueDateBetween(organizationId, departmentId, 3, begin, end));

        model.addAttribute("expired", documentTaskService.countAllTasksByDocumentTypeIdAndDueDateBefore(organizationId, departmentId, 3, end));

        model.addAttribute("completed", documentTaskService.countAllTasksByDocumentTypeIdAndTaskStatus(organizationId, departmentId, 3, TaskStatus.Complete.getId()));

        model.addAttribute("all", documentTaskService.countAllTasksByDocumentTypeId(organizationId, departmentId, 3));


        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
        model.addAttribute("chief", userService.getEmployeeList());
        model.addAttribute("statistic", documentTaskService.countAllInnerByReceiverId(user.getId()));

        model.addAttribute("executes", userService.getEmployeeList());
        return DocTemplates.InnerRegistrationList;
    }

    @RequestMapping(value = DocUrls.InnerRegistrationList, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String, Object> getInnerRegistrationListAjax(
            @RequestParam(name="documentOrganizationId",required = false,defaultValue = "")Integer documentOrganizationId,
            @RequestParam(name="registrationNumber",required = false,defaultValue = "")String registrationNumber,
            @RequestParam(name="docRegNumber",required = false,defaultValue = "")String docRegNumber,
            @RequestParam(name="registrationDateBegin",required = false,defaultValue = "")String registrationDateBegin,
            @RequestParam(name="registrationDateEnd",required = false,defaultValue = "")String registrationDateEnd,
            @RequestParam(name="controlCard",required = false,defaultValue = "")String controlCard,
            @RequestParam(name="journalId",required = false,defaultValue = "")Set<Integer> journalId,
            @RequestParam(name="documentViewId",required = false,defaultValue = "")Set<Integer> documentViewId,
            @RequestParam(name="content",required = false,defaultValue = "")String content,
            @RequestParam(name="chief",required = false,defaultValue = "")Set<Integer> chief,
            @RequestParam(name="resolution",required = false,defaultValue = "")String resolution,
            @RequestParam(name="executors",required = false,defaultValue = "")Set<Integer> executors,
            @RequestParam(name="executePath",required = false,defaultValue = "")String executePath,
            @RequestParam(name="executeDateBegin",required = false,defaultValue = "")String executeDateBegin,
            @RequestParam(name="executeDateEnd",required = false,defaultValue = "")String executeDateEnd,
            @RequestParam(name="executeStatus",required = false,defaultValue = "")Integer executeStatus,
            @RequestParam(name="insidePurposeStatus",required = false,defaultValue = "")Boolean insidePurposeStatus,
            @RequestParam(name="coexecutorStatus",required = false,defaultValue = "")Integer coexecutorStatus,
            @RequestParam(name="replies",required = false,defaultValue = "")Set<Integer> replies,
            @RequestParam(name = "tabFilter",required = false,defaultValue = "")Integer tabFilter,
            Pageable pageable
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> result = new HashMap<>();
        IncomingRegFilter incomingRegFilter = new IncomingRegFilter();
        incomingRegFilter.initNull();
        incomingRegFilter.setDocRegNumber(StringUtils.trimToNull(docRegNumber));
        incomingRegFilter.setRegistrationNumber(StringUtils.trimToNull(registrationNumber));
        incomingRegFilter.setDateBeginStr(StringUtils.trimToNull(registrationDateBegin));
        incomingRegFilter.setDateEndStr(StringUtils.trimToNull(registrationDateEnd));
        incomingRegFilter.setContent(StringUtils.trimToNull(content));
        incomingRegFilter.setDocumentOrganizationId(documentOrganizationId);

        Date deadlineDateBegin = null;
        Date deadlineDateEnd = null;
        Set<Integer> status = null;
        Calendar calendar = Calendar.getInstance();
        Boolean specialControl = null;
        tabFilter = tabFilter != null ? tabFilter : 9;

        switch (tabFilter){
            case 1:
                status = new LinkedHashSet<>();
                status.add(TaskStatus.New.getId());
                break;
            case 2:
                status = new LinkedHashSet<>();
                status.add(TaskStatus.InProgress.getId());
                break;
            case 3:
                deadlineDateEnd = calendar.getTime();
                status = new LinkedHashSet<>();
                status.add(TaskStatus.New.getId());
                status.add(TaskStatus.InProgress.getId());
                status.add(TaskStatus.Checking.getId());
                break;//Муддати кеччикан
            case 4:
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                deadlineDateEnd = calendar.getTime();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                deadlineDateBegin = calendar.getTime();
                status = new LinkedHashSet<>();
                status.add(TaskStatus.New.getId());
                status.add(TaskStatus.InProgress.getId());
                status.add(TaskStatus.Checking.getId());
                break;//Муддати якинлашаётган
            case 5:
                status = new LinkedHashSet<>();
                status.add(TaskStatus.Checking.getId());
                break;//Ижро назоратида
            case 7:
                status = new LinkedHashSet<>();
                status.add(TaskStatus.Complete.getId());
                break;//Якунланган
            case 8:
                specialControl = Boolean.TRUE;
                break;//Якунланган
            case 9:
                status = new LinkedHashSet<>();
                status.add(TaskStatus.New.getId());
                status.add(TaskStatus.InProgress.getId());
                status.add(TaskStatus.Checking.getId());
                break;
            default:
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(
                        Sort.Order.asc("status"),
                        Sort.Order.desc("dueDate")
                ));
                break;//Жами
        }
        //todo documentTypeId=3
        Page<DocumentTask> documentPage = documentTaskService.findFiltered(
                user.getOrganizationId(),
                3,
                incomingRegFilter,
                deadlineDateBegin,
                deadlineDateEnd,
                status,
                user.getDepartmentId(),
                null,
                specialControl,
                pageable);

        List<DocumentTask> documentTaskList = documentPage.getContent();
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
                    document.getRegistrationNumber()!=null?document.getRegistrationNumber():"",
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    docContent,
                    documentTask.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(documentTask.getCreatedAt()):"",
                    documentTask.getDueDate()!=null? Common.uzbekistanDateFormat.format(documentTask.getDueDate()):"",
                    documentTask.getStatus()!=null ? documentHelperService.getTranslation(TaskStatus.getTaskStatus(documentTask.getStatus()).getName(),locale):"",
                    documentTask.getContent(),
                    documentTask.getStatus(),
                    documentTaskService.getDueColor(documentTask.getDueDate(),true,documentTask.getStatus(),locale)

            });
        }

        result.put("recordsTotal", documentPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(DocUrls.InnerRegistrationView)
    public String getViewDocumentPage(
            @RequestParam(name = "id")Integer id,
            Model model
    ) {

        Document document = documentService.getById(id);
        if (document == null) {
            return "redirect:" + DocUrls.InnerRegistrationList;
        }

        List<DocumentTask> documentTasks = documentTaskService.getByDocumetId(document.getId());
        List<DocumentTaskSub> documentTaskSubs = documentTaskSubService.getListByDocId(document.getId());
        model.addAttribute("document", document);
        model.addAttribute("documentLog", new DocumentLog());
        model.addAttribute("tree", documentService.createTree(document));
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("documentTasks", documentTasks);
        model.addAttribute("documentTaskSubs", documentTaskSubs);
        model.addAttribute("user", userService.getCurrentUserFromContext());
        model.addAttribute("comment_url", DocUrls.AddComment);
        model.addAttribute("logs", documentLogService.getAllByDocId(document.getId()));
        model.addAttribute("special_controll_url", DocUrls.InnerRegistrationControll);
        model.addAttribute("cancel_url",DocUrls.IncomingRegistrationList );
        return DocTemplates.InnerRegistrationView;
    }

    @RequestMapping(value = DocUrls.InnerRegistrationNew,method = RequestMethod.GET)
    public String getInnerRegistrationNewPage(Model model) {
        User user = userService.getCurrentUserFromContext();
        Document document = new Document();
        document.setControlForm(ControlForm.FormNot);
        document.setExecuteForm(ExecuteForm.Performance);
        List<User> userList = userService.getEmployeesForForwarding(user.getOrganizationId());

        model.addAttribute("document", document);
        model.addAttribute("userList", userList);
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("managerUserList", userService.getEmployeesForNewDoc("chief"));
        model.addAttribute("controlUserList", userService.getEmployeesForNewDoc("controller"));
        model.addAttribute("journalList", journalService.getStatusActive(3));//todo 3
        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());
        model.addAttribute("chief", userService.getEmployeesForNewDoc("chief"));
        model.addAttribute("executeController", userService.getEmployeesForNewDoc("controller"));
        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
        model.addAttribute("action_url", DocUrls.InnerRegistrationNew);
        model.addAttribute("back_url", DocUrls.InnerRegistrationList);
        return DocTemplates.InnerRegistrationNew;
    }

    @Transactional
    @RequestMapping(value = {DocUrls.InnerRegistrationNew, DocUrls.InnerRegistrationNewTask}, method = RequestMethod.POST)
    public String createDoc(
            HttpServletRequest httpServletRequest,
            @RequestParam(name = "documentOrganizationId") Integer documentOrganizationId,
            @RequestParam(name = "fileIds",required = false) List<Integer> fileIds,
            @RequestParam(name = "executeForm",required = false) ExecuteForm executeForm,
            @RequestParam(name = "controlForm",required = false) ControlForm controlForm,
            @RequestBody MultiValueMap<String, String> formData,
            Document document
    ) {
        User user = userService.getCurrentUserFromContext();
        Set<File> files = new HashSet<>();
        if (fileIds!=null){
            for (Integer fileId : fileIds) {
                files.add(fileService.findById(fileId));
            }
        }
        document.setContentFiles(files);
        document.setCreatedAt(new Date());
        document.setCreatedById(user.getId());
        document.setDepartmentId(user.getDepartmentId());
        if (executeForm!=null){
            document.setExecuteForm(executeForm);
        }
        if (controlForm!=null){
            document.setControlForm(controlForm);
        }
        document.setStatus(DocumentStatus.New);
        document.setSpecialControll(Boolean.FALSE);
        documentService.createDoc(3, document, user);

        DocumentSub documentSub = new DocumentSub();
        documentSub.setOrganizationId(documentOrganizationId);
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
                documentTask = documentTaskService.createNewTask(document,TaskStatus.New.getId(),descriptionTextareaTask,taskDocRegDateStr,document.getManagerId(),user.getId());
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
                    documentTaskSubService.createNewSubTask(0,document.getId(),documentTask.getId(),documentTask.getContent(),dueDate,performerType,documentTask.getChiefId(),userId,userService.getUserDepartmentId(userId));
                    if (performerType==TaskSubType.Performer.getId()){
                        documentTask.setPerformerId(userId);
                        documentTaskService.update(documentTask);
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
        return "redirect:" + DocUrls.InnerRegistrationList;
        /*if(httpServletRequest.getRequestURI().equals(DocUrls.InnerRegistrationNewTask)){
            return "redirect:" + DocUrls.InnerRegistrationView + "?id=" + document.getId();
        }else {
            return "redirect:" + DocUrls.InnerRegistrationList;
        }*/
    }

    @RequestMapping(DocUrls.InnerRegistrationEdit)
    public String getEditDocumentPage(@RequestParam(name = "id")Integer id, Model model) {
        Document document = documentService.getById(id);
        if (document == null) {
            return "redirect:" + DocUrls.InnerRegistrationList;
        }
        DocumentSub documentSub = documentSubService.getByDocumentIdForIncoming(document.getId());
        DocumentOrganization documentOrganization = documentOrganizationService.getById(documentSub.getOrganizationId());
        Document documentAdditional = documentService.getById(document.getAdditionalDocumentId());

        model.addAttribute("document", document);
        model.addAttribute("documentSub", documentSub);

        model.addAttribute("docOrganization",documentOrganization);
        model.addAttribute("docAdditional",documentAdditional);
        model.addAttribute("journalList", journalService.getStatusActive(3));//todo 3
        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());
        model.addAttribute("chief", userService.getEmployeesForNewDoc("chief"));
        model.addAttribute("executeController", userService.getEmployeesForNewDoc("controller"));

        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
        return DocTemplates.InnerRegistrationNew;
    }

    @RequestMapping(value = {DocUrls.InnerRegistrationEdit, DocUrls.InnerRegistrationEditTask}, method = RequestMethod.POST)
    public String updateDocument(
            HttpServletRequest httpServletRequest,
            @RequestParam(name = "documentOrganizationId") Integer documentOrganizationId,
            @RequestParam(name = "fileIds",required = false) List<Integer> fileIds,
            @RequestParam(name = "executeForm",required = false) ExecuteForm executeForm,
            @RequestParam(name = "controlForm",required = false) ControlForm controlForm,
            Document document
    ) {
        User user = userService.getCurrentUserFromContext();
        Set<File> files = new HashSet<>();
        if(fileIds!=null){
            for (Integer fileId : fileIds) {
                files.add(fileService.findById(fileId));
            }
        }

        if (executeForm!=null){
            document.setExecuteForm(executeForm);
        }
        if (controlForm!=null){
            document.setControlForm(controlForm);
        }
        document.setContentFiles(files);
        document.setCreatedById(user.getId());
        document.setStatus(DocumentStatus.New);
        documentService.update(document);

        DocumentSub documentSub = new DocumentSub();
        document.setOrganizationId(documentOrganizationId);
        documentSubService.create(document.getId(), documentSub, user);

        if(httpServletRequest.getRequestURL().toString().equals(DocUrls.InnerRegistrationEditTask)){
            return "redirect:" + DocUrls.InnerRegistrationView + "?id=" + document.getId();
        }else {
            return "redirect:" + DocUrls.InnerRegistrationList;
        }
    }

    @PostMapping(value = DocUrls.InnerRegistrationFileUpload)
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "file") MultipartFile uploadFile
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> response = new HashMap<>();

        File file = fileService.uploadFile(uploadFile,user.getId(),"documentFile",uploadFile.getOriginalFilename());
        response.put("data", file);
        return response;
    }

    @RequestMapping(value = DocUrls.InnerRegistrationTask)
    public String addTask(
            @RequestParam(name = "id")Integer id,
            Model model
    ) {

        Document document = documentService.getById(id);
        if (document == null) {
            return  "redirect:" + DocUrls.InnerRegistrationList;
        }

        List<User> userList = userService.getEmployeesForForwarding(document.getOrganizationId());
        boolean isExecuteForm = false;
        if (document.getExecuteForm()!=null && document.getExecuteForm().equals(ExecuteForm.Performance)){
            isExecuteForm = true;
        }
        model.addAttribute("userList", userList);
        model.addAttribute("document", document);
        model.addAttribute("isExecuteForm", isExecuteForm);
        model.addAttribute("descriptionList", documentTaskContentService.getTaskContentList());
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("action_url", DocUrls.InnerRegistrationTaskSubmit);
        model.addAttribute("back_url", DocUrls.InnerRegistrationView+"?id=" + document.getId());
        return DocTemplates.InnerRegistrationTask;
    }

    @RequestMapping(value = DocUrls.InnerRegistrationTaskSubmit)
    public String innerRegistrationTaskSubmit(
            @RequestParam(name = "content") String content,
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "docRegDateStr", required = false) String docRegDateStr,
            @RequestBody MultiValueMap<String, String> formData
    ){

        User user = userService.getCurrentUserFromContext();

        Document document = documentService.getById(id);
        if (document == null){
            return "redirect:" + DocUrls.IncomingRegistrationList;
        }
        boolean isExecuteForm=false;
        if (document.getExecuteForm()!=null && document.getExecuteForm().equals(ExecuteForm.Performance)){
            isExecuteForm = true;
        }
        DocumentTask documentTask = documentTaskService.createNewTask(document,TaskStatus.New.getId(),content,DateParser.TryParse(docRegDateStr, Common.uzbekistanDateFormat),document.getManagerId(),user.getId());

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
                    documentTaskSubService.createNewSubTask(0,documentTask.getDocumentId(),documentTask.getId(),content,dueDate,performerType,documentTask.getChiefId(),userId,userService.getUserDepartmentId(userId));
                    userId = null;
                    performerType = null;
                    dueDate = null;
                }

            }

            if (tagName.equals("dueDateStr")){
                dueDate = DateParser.TryParse(value, Common.uzbekistanDateFormat);
                if (userId!=null && performerType!=null){
                    documentTaskSubService.createNewSubTask(0,documentTask.getDocumentId(),documentTask.getId(),content,dueDate,performerType,documentTask.getChiefId(),userId,userService.getUserDepartmentId(userId));
                    if (performerType.equals(TaskSubType.Performer.getId())){
                        documentTask.setPerformerId(userId);
                        documentTaskService.update(documentTask);
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

        return "redirect:" + DocUrls.InnerRegistrationView + "?id=" + document.getId();
    }

    @RequestMapping(value = DocUrls.InnerRegistrationUserName)
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
        result.put("position", user.getPositionId()!=null?user.getPosition().getNameTranslation(locale):"");
        return result;
    }

    @GetMapping(value = DocUrls.InnerRegistrationControll)
    @ResponseBody
    public HashMap<String, Object> innerRegistrationControll(@RequestParam(name = "id")Integer id) {
        HashMap<String, Object> response = new HashMap<>();
        Document document = documentService.getById(id);
        document.setSpecialControll(document.getSpecialControll()!=null?(!document.getSpecialControll()):(Boolean.FALSE));
        documentService.update(document);
        response.put("status", "success");
        return response;
    }

}
