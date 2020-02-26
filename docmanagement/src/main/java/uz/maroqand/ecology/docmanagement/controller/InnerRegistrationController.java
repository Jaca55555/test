package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
import uz.maroqand.ecology.docmanagement.dto.IncomingRegFilter;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
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
    private final DocumentOrganizationService documentOrganizationService;
    private final DocumentHelperService documentHelperService;

    @Autowired
    public InnerRegistrationController(UserService userService, FileService fileService, DocumentService documentService, DocumentViewService documentViewService, JournalService journalService, DocumentDescriptionService documentDescriptionService, DocumentTaskService documentTaskService, DocumentTaskSubService documentTaskSubService, DocumentSubService documentSubService, DocumentOrganizationService documentOrganizationService, DocumentHelperService documentHelperService) {
        this.userService = userService;
        this.fileService = fileService;
        this.documentService = documentService;
        this.documentViewService = documentViewService;
        this.journalService = journalService;
        this.documentDescriptionService = documentDescriptionService;
        this.documentTaskService = documentTaskService;
        this.documentTaskSubService = documentTaskSubService;
        this.documentSubService = documentSubService;
        this.documentOrganizationService = documentOrganizationService;
        this.documentHelperService = documentHelperService;
    }

    @RequestMapping(value = DocUrls.InnerRegistrationList, method = RequestMethod.GET)
    public String getIncomeListPage(Model model) {
        model.addAttribute("newDocumentCount", documentTaskService.countNew());
        model.addAttribute("inProgressDocumentCount", documentTaskService.countInProcess());
        model.addAttribute("lessDeadlineDocumentCount", documentTaskService.countNearDate());
        model.addAttribute("greaterDeadlineDocumentCount", documentTaskService.countExpired());
        model.addAttribute("checkingDocumentCount", documentTaskService.countExecuted());
        model.addAttribute("allDocumentCount", documentTaskService.countTotal());

        model.addAttribute("chief", userService.getEmployeeList());
        model.addAttribute("executes", userService.getEmployeeList());
        return DocTemplates.InnerRegistrationList;
    }

    @RequestMapping(value = DocUrls.InnerRegistrationList, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String, Object> getInnerRegistrationListAjax(
            @RequestParam(name="documentOrganizationId",required = false,defaultValue = "")Set<Integer> documentOrganizationId,
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
        HashMap<String, Object> result = new HashMap<>();
        IncomingRegFilter incomingRegFilter = new IncomingRegFilter();
        Page<DocumentTask> documentPage = documentTaskService.findFiltered(incomingRegFilter,null,null,null,null,null,null, pageable);

        List<DocumentTask> documentTaskList = documentPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentTaskList.size());
        String locale = LocaleContextHolder.getLocale().getLanguage();
        for (DocumentTask documentTask : documentTaskList) {
            Document document = documentService.getById(documentTask.getDocumentId());
            JSONArray.add(new Object[]{
                    documentTask.getId(),
                    document.getRegistrationNumber()!=null?document.getRegistrationNumber():"",
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    documentTask.getContent(),
                    documentTask.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(documentTask.getCreatedAt()):"",
                    documentTask.getUpdateAt()!=null? Common.uzbekistanDateFormat.format(documentTask.getUpdateAt()):"",
                    documentTask.getStatus()!=null ? documentHelperService.getTranslation(TaskStatus.getTaskStatus(documentTask.getStatus()).getName(),locale):"",
                    documentTask.getContent()
            });
        }

        result.put("recordsTotal", documentPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(DocUrls.InnerRegistrationView)
    public String getViewDocumentPage(@RequestParam(name = "id")Integer id, Model model) {
        Document document = documentService.getById(id);
        if (document == null) {
            return "redirect: " + DocUrls.InnerRegistrationList;
        }
        model.addAttribute("document", document);
        model.addAttribute("user", userService.getCurrentUserFromContext());
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        return DocTemplates.InnerRegistrationView;
    }

    @RequestMapping(DocUrls.InnerRegistrationNew)
    public String getInnerRegistrationNewPage(Model model) {
        model.addAttribute("document", new Document());
        model.addAttribute("journalList", journalService.getStatusActive(3));//todo 3
        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());
        model.addAttribute("chief", userService.getEmployeesForNewDoc("chief"));
        model.addAttribute("executeController", userService.getEmployeesForNewDoc("controller"));
        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
        model.addAttribute("action_url", DocUrls.InnerRegistrationNew);
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
        if (executeForm!=null){
            document.setExecuteForm(executeForm);
        }
        if (controlForm!=null){
            document.setControlForm(controlForm);
        }
        document.setStatus(DocumentStatus.New);
        documentService.createDoc(3, document, user);

        DocumentSub documentSub = new DocumentSub();
        documentSub.setOrganizationId(documentOrganizationId);
        documentSubService.create(document.getId(), documentSub, user);
        if(httpServletRequest.getRequestURI().equals(DocUrls.InnerRegistrationNewTask)){
            return "redirect:" + DocUrls.InnerRegistrationView + "?id=" + document.getId();
        }else {
            return "redirect:" + DocUrls.InnerRegistrationList;
        }
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
        response.put("file", file);
        return response;
    }

    @RequestMapping(value = DocUrls.InnerRegistrationTask)
    public String addTask( @RequestParam(name = "id")Integer id, Model model ) {
        Document document = documentService.getById(id);
        if (document == null) {
            return  "redirect:" + DocUrls.InnerRegistrationList;
        }

        model.addAttribute("document", document);
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        return DocTemplates.InnerRegistrationTask;
    }

    @RequestMapping(value = DocUrls.InnerRegistrationTaskSubmit)
    public String innerRegistrationTaskSubmit(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "docRegDateStr") String docRegDateStr,
            @RequestBody MultiValueMap<String, String> formData
    ){

        User user = userService.getCurrentUserFromContext();
        Document document = documentService.getById(id);
        if (document==null){
            return "redirect:" + DocUrls.InnerRegistrationList;
        }


        System.out.println("id=" + id);
        System.out.println("content=" + content.trim());
        System.out.println("docRegDateStr=" + docRegDateStr);

        DocumentTask documentTask = documentTaskService.createNewTask(document.getId(),TaskStatus.New.getId(),content, DateParser.TryParse(docRegDateStr, Common.uzbekistanDateFormat),document.getManagerId(),user.getId());
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
                    documentTaskSubService.createNewSubTask(document.getId(),documentTask.getId(),content,dueDate,performerType,documentTask.getChiefId(),userId,userService.getUserDepartmentId(userId));
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

}
