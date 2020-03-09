package uz.maroqand.ecology.docmanagement.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.user.UserService;

import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.docmanagement.repository.DocumentSubRepository;
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

    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Autowired
    public ReferenceRegistrationController(
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
    }

    @RequestMapping(value = DocUrls.ReferenceRegistrationNew, method = RequestMethod.GET)
    public String getNewDocumentPage(Model model) {

        model.addAttribute("document", new Document());
        model.addAttribute("documentSub", new DocumentSub());
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("documentViewList", documentViewService.getStatusActive());

        model.addAttribute("journalList", journalService.getStatusActive(1));//todo 1

        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());
        model.addAttribute("managerUserList", userService.getEmployeesForNewDoc("chief"));
        model.addAttribute("controlUserList", userService.getEmployeesForNewDoc("controller"));
        model.addAttribute("organizationList", organizationService.getDocumentOrganizationNames());
        model.addAttribute("executeForms", ExecuteForm.getExecuteFormList());
        model.addAttribute("controlForms", ControlForm.getControlFormList());
        return DocTemplates.ReferenceRegistrationNew;
    }
    @Transactional
    @RequestMapping(value = {DocUrls.ReferenceRegistrationNew, DocUrls.ReferenceRegistrationNewTask}, method = RequestMethod.POST)
    public String createDocument(
            HttpServletRequest httpServletRequest,
            @RequestParam(name = "journalId") Integer journalId,
            @RequestParam(name = "documentViewId") Integer documentViewId,
            @RequestParam(name = "docRegNumber") String docRegNumber,
            @RequestParam(name = "docRegDateStr") String docRegDateStr,
            @RequestParam(name = "communicationToolId") Integer communicationToolId,
            @RequestParam(name = "documentOrganizationId") String documentOrganizationId,
            @RequestParam(name = "docSubAddress") String documentSubAddress,

            @RequestParam(name = "contentId", required = false) Integer contentId,
            @RequestParam(name = "content", required = false) String content,
            @RequestParam(name = "additionalDocumentId", required = false) Integer additionalDocumentId,
            @RequestParam(name = "performerName", required = false) String performerName,
            @RequestParam(name = "performerPhone", required = false) String performerPhone,
            @RequestParam(name = "managerId") Integer managerId,
            @RequestParam(name = "controlId", required = false) Integer controlId,
            @RequestParam(name = "insidePurpose", required = false) Boolean insidePurpose,
            @RequestParam(name = "executeFormId", required = false) Integer executeFormId,
            @RequestParam(name = "controlFormId", required = false) Integer controlFormId,
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
        document.setInsidePurpose(insidePurpose);

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
        document = documentService.createDoc(1, document, user);

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
        documentSub.setOrganizationName(documentSubAddress);
        documentSubService.create(document.getId(), documentSub, user);

        if(httpServletRequest.getRequestURI().equals(DocUrls.ReferenceRegistrationNewTask)){
            return "redirect:" + DocUrls.IncomingRegistrationView + "?id=" + document.getId();
        }else {
            return "redirect:" + DocUrls.IncomingRegistrationList;
        }
    }
    @RequestMapping(DocUrls.ReferenceRegistrationView)
    public String getViewDocumentPage(
            @RequestParam(name = "id")Integer id,
            Model model
    ) {
        Document document = documentService.getById(id);
        if (document == null) {
            return "redirect: " + DocUrls.ReferenceRegistrationList;
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
        model.addAttribute("special_controll_url", DocUrls.IncomingSpecialControll);
        model.addAttribute("cancel_url",DocUrls.IncomingRegistrationList );
        return DocTemplates.IncomingRegistrationView;
    }





}
