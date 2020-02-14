package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
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
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagement.entity.Journal;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;
import org.springframework.data.domain.Page;

import javax.swing.text.DocumentFilter;
import javax.transaction.Transactional;
import java.util.*;

@Controller
public class OutgoingMailController {

    private final DocumentService documentService;
    private final JournalService journalService;
    private final DocumentViewService documentViewService;
    private final DocumentTypeService documentTypeService;
    private final CommunicationToolService communicationToolService;
    private final UserService userService;
    private final DocumentOrganizationService documentOrganizationService;
    private final FileService fileService;


    @Autowired
    public OutgoingMailController(
            DocumentService documentService,
            JournalService journalService,
            DocumentViewService documentViewService,
            DocumentTypeService documentTypeService,
            CommunicationToolService communicationToolService,
            UserService userService,
            DocumentOrganizationService documentOrganizationService,
            FileService fileService
            ){
        this.documentService = documentService;
        this.journalService = journalService;
        this.documentViewService = documentViewService;
        this.documentTypeService = documentTypeService;
        this.communicationToolService = communicationToolService;
        this.userService = userService;
        this.documentOrganizationService = documentOrganizationService;
        this.fileService = fileService;
    }

    @RequestMapping(DocUrls.OutgoingMailNew)
    public String newOutgoingMail(Model model){
        Document document = new Document();
        model.addAttribute("document", document);
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("documentViews", documentViewService.getStatusActive());
        model.addAttribute("communicationTools", communicationToolService.getStatusActive());
        model.addAttribute("organizations", documentOrganizationService.getList());
        model.addAttribute("documents_", documentService.getList());

        return DocTemplates.OutgoingMailNew;
    }

    @Transactional
    @RequestMapping(value = DocUrls.OutgoingMailNew, method = RequestMethod.POST)
    public String newOutgoingMail(Document document, @RequestParam(name = "file_ids")List<Integer> file_ids){

        Document newDocument = new Document();
        newDocument.setCreatedAt(new Date());
        newDocument.setDocumentTypeId(DocumentTypeEnum.OutgoingDocuments.getId());
    //    System.out.println(file_ids);
        Set<File> files = new HashSet<>();
        for(Integer fileId: file_ids){
            if(fileId == null) continue;
            files.add(fileService.findById(fileId));
        }
        newDocument.setContentFiles(files);

        Journal journal = journalService.getById(document.getJournalId());

        newDocument.setJournalId(journal.getId());
        newDocument.setJournal(journal);
        newDocument.setRegistrationNumber(journal.getPrefix() + '-' + (journal.getNumbering() != null ? journal.getNumbering() : 1));
        newDocument.setRegistrationDate(new Date());

        newDocument.setDocumentViewId(document.getDocumentViewId());

        DocumentSub documentSub = new DocumentSub();
        Integer communicationToolId = document.getDocumentSub().getCommunicationToolId();
        documentSub.setCommunicationTool(communicationToolService.getById(communicationToolId));
        documentSub.setCommunicationToolId(communicationToolId);

        DocumentOrganization documentOrganization = documentOrganizationService.getByName(document.getDocumentSub().getOrganizationName());
        User user = userService.getCurrentUserFromContext();
        if(documentOrganization == null){
            documentOrganization = new DocumentOrganization();
            documentOrganization.setName(document.getDocumentSub().getOrganizationName());
            documentOrganization.setCreatedById(user.getId());
            documentOrganizationService.create(documentOrganization);
        }

        documentSub.setOrganizationName(document.getDocumentSub().getOrganizationName());
        documentSub.setOrganizationId(documentOrganization.getId());
        documentSub.setOrganization(documentOrganization);
        newDocument.setDocumentSub(documentSub);

        newDocument.setDocumentSub(documentSub);
        newDocument.setDocumentDescription(document.getDocumentDescription());

        newDocument.setAnswerDocumentId(document.getAnswerDocumentId());
        newDocument.setAnswerDocument(documentService.getById(document.getAnswerDocumentId()));
        newDocument.setPerformerName(user.getUsername());

        newDocument.setPerformerPhone(user.getPhone());
        newDocument.setRegistrationDate(new Date());

        newDocument.setCreatedById(user.getId());

        documentService.createDoc(newDocument);

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
                    document.getDocumentDescription() != null ? document.getDocumentDescription().getContent(): "",
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
        model.addAttribute("documents_", documentService.getList());

        return DocTemplates.OutgoingMailNew;
    }

    @RequestMapping(DocUrls.OutgoingMailEdit)
    public String outgoingMailEdit(@RequestParam(name="id")Integer id, Model model){

        model.addAttribute("document", documentService.getById(id));
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("documentViews", documentViewService.getStatusActive());
        model.addAttribute("communicationTools", communicationToolService.getStatusActive());
        model.addAttribute("organizations", documentOrganizationService.getList());
        model.addAttribute("documents_", documentService.getList());

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
