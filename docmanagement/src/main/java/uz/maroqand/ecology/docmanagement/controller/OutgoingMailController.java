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

@Controller
public class OutgoingMailController {

    private final DocumentService documentService;
    private final DocumentSubService documentSubService;
    private final JournalService journalService;
    private final DocumentViewService documentViewService;
    private final CommunicationToolService communicationToolService;
    private final UserService userService;
    private final DocumentOrganizationService documentOrganizationService;
    private final FileService fileService;
    private final DepartmentService departmentService;

    @Autowired
    public OutgoingMailController(
            DocumentService documentService,
            DocumentSubService documentSubService,
            JournalService journalService,
            DocumentViewService documentViewService,
            CommunicationToolService communicationToolService,
            UserService userService,
            DocumentOrganizationService documentOrganizationService,
            FileService fileService,
            DepartmentService departmentService
    ){
        this.documentService = documentService;
        this.documentSubService = documentSubService;
        this.journalService = journalService;
        this.documentViewService = documentViewService;
        this.communicationToolService = communicationToolService;
        this.userService = userService;
        this.documentOrganizationService = documentOrganizationService;
        this.fileService = fileService;
        this.departmentService = departmentService;
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
            @RequestParam(name = "communication_tool_id") Integer communicationToolId,
            @RequestParam(name = "document_organization_id") Integer documentOrganizationId,
            @RequestParam(name = "file_ids") List<Integer> file_ids,
            Document document
    ){
        User user = userService.getCurrentUserFromContext();
        Document newDocument = new Document();
        newDocument.setDocumentTypeId(2);
        newDocument.setJournalId(document.getJournalId());
        newDocument.setJournal(journalService.getById(document.getJournalId()));
        newDocument.setDocumentViewId(document.getDocumentViewId());
        newDocument.setAdditionalDocumentId(document.getAdditionalDocumentId());
        newDocument.setPerformerName(document.getPerformerName());
        newDocument.setPerformerPhone(document.getPerformerPhone());
        newDocument.setCreatedAt(new Date());
        newDocument.setCreatedById(user.getId());
        documentService.createDoc(newDocument);

        DocumentSub documentSub = new DocumentSub();
        documentSub.setCommunicationToolId(communicationToolId);
        documentSub.setOrganizationId(documentOrganizationId);
        documentSubService.create(newDocument.getId(), documentSub, user);

        /*DocumentOrganization documentOrganization = documentOrganizationService.getByName(document.getDocumentSub().getOrganizationName());
        User user = userService.getCurrentUserFromContext();
        if(documentOrganization == null){
            documentOrganization = new DocumentOrganization();
            documentOrganization.setName(document.getDocumentSub().getOrganizationName());
            documentOrganization.setCreatedById(user.getId());
            documentOrganizationService.create(documentOrganization);
        }*/

        System.out.println(document);
        System.out.println("communication tool id: " + communicationToolId);
        System.out.println("document organization id: " + documentOrganizationId);
        System.out.println("file_ids: " + file_ids);

        return "redirect:" + DocUrls.OutgoingMailNew;
    }

    @RequestMapping(value = DocUrls.OutgoingMailOrganizationList, produces = "application/json")
    @ResponseBody
    public List<String> getOrganizationNames() {
        return documentOrganizationService.getDocumentOrganizationNames();
    }



    @RequestMapping(DocUrls.OutgoingMailList)
    public String getOutgoingMailList(Model model) {
        model.addAttribute("organizationList", documentOrganizationService.getList());
        return DocTemplates.OutgoingMailList;
    }

    @RequestMapping(value = DocUrls.OutgoingMailListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object>  getOutgoingDocumentListAjax(
            DocFilterDTO filter,
            Pageable pageable
    ){
        /*        filter.setDocumentType(DocumentTypeEnum.OutgoingDocuments.getId());*/
        HashMap<String, Object> result = new HashMap<>();
        //   DocFilterDTO filter = new DocFilterDTO();
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
    public String outgoingMailView(@RequestParam(name = "id")Integer id, Model model){

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
    public void deleteAttachment(@RequestParam(name = "id")Integer id){
        fileService.deleteById(id);
    }

}
