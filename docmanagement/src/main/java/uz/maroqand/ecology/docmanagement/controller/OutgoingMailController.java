package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import org.springframework.data.domain.Page;
import javax.transaction.Transactional;
import java.util.*;
import java.lang.NumberFormatException;


@Controller
public class OutgoingMailController {

    private final DocumentService documentService;
    private final JournalService journalService;
    private final DocumentViewService documentViewService;
    private final CommunicationToolService communicationToolService;
    private final UserService userService;
    private final DocumentOrganizationService documentOrganizationService;
    private final FileService fileService;
    private final DepartmentService departmentService;
    private final DocumentDescriptionService descService;
    private final DocumentSubService documentSubService;
    private final DocumentTypeService documentTypeService;

    @Autowired
    public OutgoingMailController(
            DocumentService documentService,
            JournalService journalService,
            DocumentViewService documentViewService,
            CommunicationToolService communicationToolService,
            UserService userService,
            DocumentOrganizationService documentOrganizationService,
            FileService fileService,
            DepartmentService departmentService,
            DocumentDescriptionService descService,
            DocumentSubService documentSubService,
            DocumentTypeService documentTypeService
    ){
        this.documentService = documentService;
        this.journalService = journalService;
        this.documentViewService = documentViewService;
        this.communicationToolService = communicationToolService;
        this.userService = userService;
        this.documentOrganizationService = documentOrganizationService;
        this.fileService = fileService;
        this.departmentService = departmentService;
        this.descService = descService;
        this.documentSubService = documentSubService;
        this.documentTypeService = documentTypeService;
    }

    @RequestMapping(DocUrls.OutgoingMailNew)
    public String newOutgoingMail(Model model){

        model.addAttribute("document", new Document());
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("documentViews", documentViewService.getStatusActive());
        model.addAttribute("communicationTools", communicationToolService.getStatusActive());
        model.addAttribute("organizations", documentOrganizationService.getList());
        Integer organizationId = userService.getCurrentUserFromContext().getOrganizationId();
        model.addAttribute("departments", departmentService.getByOrganizationId(organizationId));
        model.addAttribute("performers", userService.getEmployeesForForwarding(organizationId));

        return DocTemplates.OutgoingMailNew;
    }

    @Transactional
    @RequestMapping(value = DocUrls.OutgoingMailNew, method = RequestMethod.POST)
    public String newOutgoingMail(
            Document document,
            @RequestParam(name = "communication_tool_id")Integer communicationToolId,
            @RequestParam(name = "document_organization_id")String documentOrganizationId_,
            @RequestParam(name = "file_ids")List<Integer> file_ids
    ){
        Integer documentOrganizationId;
        User user = userService.getCurrentUserFromContext();
        try {
            documentOrganizationId = Integer.parseInt(documentOrganizationId_);
        }catch(NumberFormatException ex){
            String name = documentOrganizationId_;
            System.out.println("creating new document organization with '" + documentOrganizationId_ + "' name");
            DocumentOrganization documentOrganization = new DocumentOrganization();
            documentOrganization.setName(name);
            documentOrganization.setStatus(true);
            documentOrganization.setCreatedById(user.getId());
            documentOrganizationId = documentOrganizationService.create(documentOrganization).getId();
        }
        Set<File> files = new HashSet<File>();
        for(Integer id: file_ids) {
            if (id != null) files.add(fileService.findById(id));
        }
        document.setContentFiles(files);
        //journal, registration number and registration date
        Journal journal = journalService.getById(document.getJournalId());
        document.setJournal(journal);
        document.setRegistrationNumber(journal.getPrefix() + '-' + (journal.getNumbering() != null ? journal.getNumbering() : 1));
        document.setRegistrationDate(new Date());
        //document view
        document.setDocumentView(documentViewService.getById(document.getDocumentViewId()));
        //content and creating documentDescription
        DocumentDescription description = new DocumentDescription();
        description.setContent(document.getContent());
        document.setContentId(descService.save(description).getId());
        //setting createdAt and created by and organization

        document.setCreatedById(user.getId());
        document.setCreatedAt(new Date());
        document.setOrganizationId(user.getOrganizationId());
        document.setOrganization(user.getOrganization());
        //setting document type
        document.setDocumentTypeId(DocumentTypeEnum.OutgoingDocuments.getId());
        document.setDocumentType(documentTypeService.getById(document.getDocumentTypeId()));

        //setting document sub
        DocumentSub docSub = new DocumentSub();
        docSub.setOrganizationId(documentOrganizationId);
        DocumentOrganization documentOrganization = documentOrganizationService.getById(documentOrganizationId);
        docSub.setOrganization(documentOrganization);
        docSub.setOrganizationName(documentOrganization.getName());
        docSub.setCommunicationToolId(communicationToolId);

        Document savedDocument = documentService.createDoc(document);


        docSub.setDocumentId(savedDocument.getId());
        docSub.setDocument(savedDocument);
        documentSubService.createDocumentSub(docSub);

        return "redirect:" + DocUrls.OutgoingMailList;
    }

    @RequestMapping(value = DocUrls.OutgoingMailOrganizationList, produces = "application/json")
    @ResponseBody
    public List<String> getOrganizationNames() {
        return documentOrganizationService.getDocumentOrganizationNames();
    }

    @RequestMapping(DocUrls.OutgoingMailList)
    public String getOutgoingMailList(Model model) {
        model.addAttribute("organizationList", documentOrganizationService.getList());
        model.addAttribute("documentViews", documentViewService.getStatusActive());
        return DocTemplates.OutgoingMailList;
    }

    @RequestMapping(value = DocUrls.OutgoingMailListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object>  getOutgoingDocumentListAjax(

            Pageable pageable
    ){
        /*filter.setDocumentType(DocumentTypeEnum.OutgoingDocuments.getId());*/
        HashMap<String, Object> result = new HashMap<>();
           DocFilterDTO filter = new DocFilterDTO();
        filter.setDocumentType(DocumentTypeEnum.OutgoingDocuments.getId());
        Page<Document> documentPage = documentService.findFiltered(filter, pageable);
        System.out.println("*************************************");
        // List<Document> documentList = documentPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentPage.getTotalPages());
        for (Document document : documentPage) {
            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    document.getContent() != null ? document.getContent() : "",
                    document.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(document.getCreatedAt()):"",
                    document.getUpdateAt()!=null? Common.uzbekistanDateFormat.format(document.getUpdateAt()):"",
                    "docStatus",
                    "Resolution and parcipiants"
            });
        }

        result.put("recordsTotal", documentPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
    }

    @RequestMapping(DocUrls.OutgoingMailView)
    public String outgoingMailView(
        @RequestParam(name = "id")Integer id,
        Model model
    ){
        model.addAttribute("document", documentService.getById(id));
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("documentViews", documentViewService.getStatusActive());
        model.addAttribute("communicationTools", communicationToolService.getStatusActive());
        model.addAttribute("organizations", documentOrganizationService.getList());
        return DocTemplates.OutgoingMailNew;
    }

    @RequestMapping(DocUrls.OutgoingMailEdit)
    public String outgoingMailEdit(@RequestParam(name="id")Integer id, Model model){

        model.addAttribute("document", documentService.getById(id));
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("documentViews", documentViewService.getStatusActive());
        model.addAttribute("communicationTools", communicationToolService.getStatusActive());
        model.addAttribute("organizations", documentOrganizationService.getList());
        return DocTemplates.OutgoingMailNew;
    }

    @RequestMapping(value = DocUrls.OutgoingMailFileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, File> uploadFile(@RequestParam(name = "file")MultipartFile file){

        File file_ = fileService.uploadFile(file, userService.getCurrentUserFromContext().getId(), file.getOriginalFilename(), file.getContentType());
        HashMap<String, File> res = new HashMap<>();
        res.put("data", file_);

        return res;
    }

    @RequestMapping(value = DocUrls.OutgoingMailFileDownload, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<Resource> downloadAttachedDocument(@RequestParam(name = "id")Integer id){
        File file = fileService.findById(id);
        if(file == null)
            return ResponseEntity.badRequest().body(null);
        else
            return fileService.getFileAsResourceForDownloading(file);

    }

    @RequestMapping(value = DocUrls.OutgoingMailFileDelete, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public File deleteAttachment(@RequestParam(name = "id")Integer id){
        File file = fileService.findById(id);
        file.setDeleted(true);
        file.setDateDeleted(new Date());
        file.setDeletedById(userService.getCurrentUserFromContext().getId());
        return fileService.save(file);
    }

}