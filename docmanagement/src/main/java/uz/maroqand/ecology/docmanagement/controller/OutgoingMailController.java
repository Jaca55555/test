package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
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
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

@Controller
public class OutgoingMailController {

    private final DocumentService documentService;
    private final JournalService journalService;
    private final DocumentViewService documentViewService;
    private final DocumentTypeService documentTypeService;
    private final CommunicationToolService communicationToolService;
    private final UserService userService;
    private final DocumentOrganizationService documentOrganizationService;

    @Autowired
    public OutgoingMailController(
            DocumentService documentService,
            JournalService journalService,
            DocumentViewService documentViewService,
            DocumentTypeService documentTypeService,
            CommunicationToolService communicationToolService,
            UserService userService,
            DocumentOrganizationService documentOrganizationService
            ){
        this.documentService = documentService;
        this.journalService = journalService;
        this.documentViewService = documentViewService;
        this.documentTypeService = documentTypeService;
        this.communicationToolService = communicationToolService;
        this.userService = userService;
        this.documentOrganizationService = documentOrganizationService;
    }

    @RequestMapping(DocUrls.OutgoingMailNew)
    public String newOutgoingMail(Model model){

        model.addAttribute("document", new Document());
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("documentViews", documentViewService.getStatusActive());
        model.addAttribute("communicationTools", communicationToolService.getStatusActive());
        model.addAttribute("organizations", documentOrganizationService.getList());
        model.addAttribute("documents_", documentService.getList());

        return DocTemplates.OutgoingMailNew;
    }

    @RequestMapping(value = DocUrls.OutgoingMailNew, method = RequestMethod.POST)
    public String newOutgoingMail(Document document){

        Document newDocument = new Document();
        newDocument.setCreatedAt(new Date());
        newDocument.setDocumentTypeId(DocumentTypeEnum.OutgoingDocuments.getId());

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
            @RequestParam(name = "name", required = false)String name,
            Pageable pageable
    ){
        /*        filter.setDocumentType(DocumentTypeEnum.OutgoingDocuments.getId());*/
        Page<Document> documentPage = documentService.findFiltered(pageable);
        System.out.println("************************");
        System.out.println("name: " + name);
        System.out.println(documentPage);
        System.out.println("*********************");
        HashMap<String, Object> res = new HashMap<>();

        res.put("recordsTotal", documentPage.getTotalElements());
        res.put("recordsFiltered", documentPage.getTotalElements());

        List<Object[]> data = new ArrayList<>(documentPage.getContent().size());

        for(Document document: documentPage){
            data.add(new Object[]{
                    document.getRegistrationNumber(),
                    document.getRegistrationDate(),
                    document.getDocumentDescription() != null ? document.getDocumentDescription().getContent(): "",
                    document.getId(),
                    document.getId(),
                    document.getId(),
                    document.getId()
            });
        }

        res.put("data", data);

        return res;
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


}
