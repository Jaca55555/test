package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
    private final CommunicationToolService communicationToolService;
    private final DocumentDescriptionService documentDescriptionService;
    private final DocumentSubService documentSubService;
    private final DocumentOrganizationService documentOrganizationService;

    @Autowired
    public InnerRegistrationController(UserService userService, FileService fileService, DocumentService documentService, DocumentViewService documentViewService, JournalService journalService, CommunicationToolService communicationToolService, DocumentDescriptionService documentDescriptionService, DocumentSubService documentSubService, DocumentOrganizationService documentOrganizationService) {
        this.userService = userService;
        this.fileService = fileService;
        this.documentService = documentService;
        this.documentViewService = documentViewService;
        this.journalService = journalService;
        this.communicationToolService = communicationToolService;
        this.documentDescriptionService = documentDescriptionService;
        this.documentSubService = documentSubService;
        this.documentOrganizationService = documentOrganizationService;
    }

    @RequestMapping(value = DocUrls.InnerRegistrationList, method = RequestMethod.GET)
    public String getIncomeListPage(Model model) {
        model.addAttribute("chief", userService.getEmployeeList());
        model.addAttribute("executes", userService.getEmployeeList());
        return DocTemplates.InnerRegistrationList;
    }

    @RequestMapping(value = DocUrls.InnerRegistrationList, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String, Object> getInnerRegistrationListAjax(
            DocFilterDTO dto,
            Pageable pageable
    ) {
        HashMap<String, Object> result = new HashMap<>();

        Page<Document> documentPage = documentService.findFiltered(dto, pageable);

        List<Document> documentList = documentPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentList.size());
        for (Document document : documentList) {
            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    document.getContent(),
                    document.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(document.getCreatedAt()):"",
                    document.getUpdateAt()!=null? Common.uzbekistanDateFormat.format(document.getUpdateAt()):"",
                    document.getStatus(),
                    "Resolution and parcipiants"
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
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));
        return DocTemplates.InnerRegistrationView;
    }

    @RequestMapping(DocUrls.InnerRegistrationNew)
    public String getInnerRegistrationNewPage(Model model) {
        model.addAttribute("doc", new Document());
        model.addAttribute("journalList", journalService.getStatusActive());
        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());
        model.addAttribute("chief", userService.getEmployeeList());
        model.addAttribute("executeController", userService.getEmployeeList());
        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
        model.addAttribute("action_url", DocUrls.InnerRegistrationNew);
        return DocTemplates.InnerRegistrationNew;
    }

    @Transactional
    @RequestMapping(value = {DocUrls.InnerRegistrationNew, DocUrls.InnerRegistrationNewTask}, method = RequestMethod.POST)
    public String createDoc(
            HttpServletRequest httpServletRequest,
            @RequestParam(name = "registrationDateStr") String regDate,
            @RequestParam(name = "fileIds") List<Integer> fileIds,
            @RequestParam(name = "executeForm") ExecuteForm executeForm,
            @RequestParam(name = "controlForm") ControlForm controlForm,
            @RequestParam(name = "communicationToolId") Integer communicationToolId,
            Document document
    ) {
        User user = userService.getCurrentUserFromContext();
        Set<File> files = new HashSet<>();
        for (Integer fileId : fileIds) {
            files.add(fileService.findById(fileId));
        }
        document.setContentFiles(files);
        document.setRegistrationDate(DateParser.TryParse(regDate, Common.uzbekistanDateFormat));
        document.setCreatedAt(new Date());
        document.setCreatedById(user.getId());
        document.setExecuteForm(executeForm);
        document.setControlForm(controlForm);
        document.setStatus(DocumentStatus.Initial);
        documentService.createDoc(document);

        DocumentSub documentSub = new DocumentSub();
        documentSub.setCommunicationToolId(communicationToolId);
        documentSub.setOrganizationId(document.getOrganizationId());
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
        DocumentOrganization documentOrganization = documentOrganizationService.getById(document.getOrganizationId());
        Document documentAdditional = documentService.getById(document.getAdditionalDocumentId());

        model.addAttribute("doc", document);
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));

        model.addAttribute("docOrganization",documentOrganization);
        model.addAttribute("docAdditional",documentAdditional);
        model.addAttribute("journalList", journalService.getStatusActive());
        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());
        model.addAttribute("managerUserList", userService.getEmployeeList());
        model.addAttribute("controlUserList", userService.getEmployeeList());

        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
        return DocTemplates.InnerRegistrationNew;
    }

    @RequestMapping(value = {DocUrls.InnerRegistrationEdit, DocUrls.InnerRegistrationEditTask}, method = RequestMethod.POST)
    public String updateDocument(
            HttpServletRequest httpServletRequest,
            @RequestParam(name = "docRegDateStr") String docRegDateStr,
            @RequestParam(name = "communicationToolId") Integer communicationToolId,
            @RequestParam(name = "documentOrganizationId") Integer documentOrganizationId,
            @RequestParam(name = "executeFormId", required = false) Integer executeFormId,
            @RequestParam(name = "controlFormId", required = false) Integer controlFormId,
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

}
