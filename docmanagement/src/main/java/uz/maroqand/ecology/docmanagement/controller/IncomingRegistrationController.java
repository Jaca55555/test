package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.repository.DocumentSubRepository;
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
    private final DocumentService documentService;
    private final DocumentTypeService documentTypeService;
    private final JournalService journalService;
    private final DocumentViewService documentViewService;
    private final CommunicationToolService communicationToolService;
    private final UserService userService;
    private final FileService fileService;
    private final DocumentOrganizationService organizationService;
    private final DocumentDescriptionService documentDescriptionService;
    private final DocumentSubService documentSubService;
    private final DocumentTaskService taskService;
    private final DocumentLogService documentLogService;
    private final DocumentSubRepository documentSubRepository;

    @Autowired
    public IncomingRegistrationController(
            DocumentService documentService,
            DocumentDescriptionService documentDescriptionService,
            DocumentTypeService documentTypeService,
            CommunicationToolService communicationToolService,
            UserService userService,
            JournalService journalService,
            FileService fileService,
            DocumentOrganizationService organizationService,
            DocumentViewService documentViewService,
            DocumentSubService documentSubService,
            DocumentTaskService taskService,
            DocumentLogService documentLogService,
            DocumentSubRepository documentSubRepository
    ) {
        this.documentService = documentService;
        this.documentSubService = documentSubService;
        this.taskService = taskService;
        this.documentLogService = documentLogService;
        this.documentSubRepository = documentSubRepository;
        this.documentDescriptionService = documentDescriptionService;
        this.documentTypeService = documentTypeService;
        this.communicationToolService = communicationToolService;
        this.userService = userService;
        this.journalService = journalService;
        this.fileService = fileService;
        this.organizationService = organizationService;
        this.documentViewService = documentViewService;
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationList, method = RequestMethod.GET)
    public String getIncomingRegistrationListPage(Model model) {

        model.addAttribute("doc_type", documentTypeService.getStatusActive());
        return DocTemplates.IncomingRegistrationList;
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationList, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getIncomingRegistrationList(
            DocFilterDTO filterDTO,
            Pageable pageable
    ) {
        HashMap<String, Object> result = new HashMap<>();
        Page<Document> documentPage = documentService.findFiltered(filterDTO, pageable);
        List<Document> documentList = documentPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentList.size());
        Integer userId = userService.getCurrentUserFromContext().getId();
        for (Document document : documentList) {
            DocumentTask task = taskService.getTaskByUser(document.getId(), userId);
            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    document.getContent(),
                    document.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(document.getCreatedAt()):"",
                    task!=null ? Common.uzbekistanDateFormat.format(task.getDueDate()):"",
                    task!=null ? task.getStatus():"",
                    task!=null ? task.getContent():""
            });
        }

        result.put("recordsTotal", documentPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(DocUrls.IncomingRegistrationView)
    public String getViewDocumentPage(@RequestParam(name = "id")Integer id, Model model) {
        Document document = documentService.getById(id);
        if (document == null) {
            return "redirect: " + DocUrls.IncomingRegistrationList;
        }
        model.addAttribute("document", document);
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        model.addAttribute("user", userService.getCurrentUserFromContext());
        model.addAttribute("comment_url", DocUrls.AddComment);
        model.addAttribute("logs", documentLogService.getAllByDocId(document.getId()));
        return DocTemplates.IncomingRegistrationView;
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationNew, method = RequestMethod.GET)
    public String getNewDocumentPage(Model model) {

        model.addAttribute("document", new Document());
        model.addAttribute("documentSub", new DocumentSub());

        model.addAttribute("journalList", journalService.getStatusActive());
        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());
        model.addAttribute("managerUserList", userService.getEmployeeList());
        model.addAttribute("controlUserList", userService.getEmployeeList());

        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
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
            @RequestParam(name = "documentOrganizationId") Integer documentOrganizationId,
            @RequestParam(name = "contentId", required = false) Integer contentId,
            @RequestParam(name = "content", required = false) String content,
            @RequestParam(name = "additionalDocumentId", required = false) Integer additionalDocumentId,
            @RequestParam(name = "performerName", required = false) String performerName,
            @RequestParam(name = "performerPhone", required = false) String performerPhone,
            @RequestParam(name = "managerId") Integer managerId,
            @RequestParam(name = "controlId", required = false) Integer controlId,
            @RequestParam(name = "executeForm", required = false) Integer executeFormId,
            @RequestParam(name = "controlForm", required = false) Integer controlFormId,
            @RequestParam(name = "fileIds", required = false) List<Integer> fileIds
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

        if(executeFormId!=null){
            document.setExecuteForm(ExecuteForm.getExecuteForm(executeFormId));
        }
        if(controlFormId!=null){
            document.setControlForm(ControlForm.getControlForm(controlFormId));
        }
        document.setContentFiles(files);
        document.setCreatedById(user.getId());
        document.setRegistrationNumber(journalService.getRegistrationNumberByJournalId(document.getJournalId()));
        document.setRegistrationDate(new Date());
        document.setStatus(DocumentStatus.New);
        document = documentService.createDoc(document);

        DocumentSub documentSub = new DocumentSub();
        documentSub.setCommunicationToolId(communicationToolId);
        documentSub.setOrganizationId(documentOrganizationId);
        documentSubService.create(document.getId(), documentSub, user);

        if(httpServletRequest.getRequestURI().equals(DocUrls.IncomingRegistrationNewTask)){
            return "redirect:" + DocUrls.IncomingRegistrationView + "?id=" + document.getId();
        }else {
            return "redirect:" + DocUrls.IncomingRegistrationList;
        }
    }

    @RequestMapping(DocUrls.IncomingRegistrationEdit)
    public String getEditDocumentPage(@RequestParam(name = "id")Integer id, Model model) {
        Document document = documentService.getById(id);
        if (document == null) {
            return "redirect:" + DocUrls.IncomingRegistrationList;
        }

        model.addAttribute("document", document);
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));

        model.addAttribute("journalList", journalService.getStatusActive());
        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());
        model.addAttribute("managerUserList", userService.getEmployeeList());
        model.addAttribute("controlUserList", userService.getEmployeeList());

        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
        return DocTemplates.IncomingRegistrationNew;
    }

    @RequestMapping(value = {DocUrls.IncomingRegistrationEdit, DocUrls.IncomingRegistrationEditTask}, method = RequestMethod.POST)
    public String updateDocument(
            HttpServletRequest httpServletRequest,
            @RequestParam(name = "docRegDateStr") String docRegDateStr,
            @RequestParam(name = "communicationToolId") Integer communicationToolId,
            @RequestParam(name = "documentOrganizationId") Integer documentOrganizationId,
            @RequestParam(name = "executeForm", required = false) Integer executeFormId,
            @RequestParam(name = "controlForm", required = false) Integer controlFormId,
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

        if(executeFormId!=null){
            document.setExecuteForm(ExecuteForm.getExecuteForm(executeFormId));
        }
        if(controlFormId!=null){
            document.setControlForm(ControlForm.getControlForm(controlFormId));
        }
        document.setContentFiles(files);
        document.setDocRegDate(DateParser.TryParse(docRegDateStr, Common.uzbekistanDateFormat));
        document.setCreatedById(user.getId());
        document.setStatus(DocumentStatus.New);
        documentService.update(document);

        DocumentSub documentSub = new DocumentSub();
        documentSub.setCommunicationToolId(communicationToolId);
        documentSub.setOrganizationId(documentOrganizationId);
        documentSubService.create(document.getId(), documentSub, user);

        if(httpServletRequest.getRequestURL().toString().equals(DocUrls.IncomingRegistrationEditTask)){
            return "redirect:" + DocUrls.IncomingRegistrationTask + "?id=" + document.getId();
        }else {
            return "redirect:" + DocUrls.IncomingRegistrationList;
        }
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationTask)
    public String addTask( @RequestParam(name = "id")Integer id, Model model ) {
        Document document = documentService.getById(id);
        if (document == null) {
            return  "redirect:" + DocUrls.IncomingRegistrationList;
        }

        model.addAttribute("document", document);
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        return DocTemplates.IncomingRegistrationTask;
    }

    @PostMapping(value = DocUrls.IncomeMailFileUpload)
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "file")MultipartFile uploadFile
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> response = new HashMap<>();

        File file = fileService.uploadFile(uploadFile,user.getId(),"documentFile",uploadFile.getOriginalFilename());
        response.put("file", file);
        return response;
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

    /*@GetMapping(value = DocUrls.IncomeMailSpecial)
    @ResponseBody
    public HashMap<String, Object> changeSpecial(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "enabled")Boolean enabled
    ) {
        HashMap<String, Object> response = new HashMap<>();
        Document document = documentService.getById(id);
        if (document == null) {
            response.put("status", "not found");
            return response;
        }
        document.setSpecialControl(enabled);
        documentService.update(document);
        response.put("status", "success");
        response.put("value", enabled);
        return response;
    }*/

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
