package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;


import java.util.List;

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
        document.setDocumentTypeId(2);
        newDocument.setJournalId(document.getJournalId());
        newDocument.setJournal(journalService.getById(document.getJournalId()));
        newDocument.setDocumentViewId(document.getDocumentViewId());

        DocumentSub documentSub = new DocumentSub();
        Integer comtoolId = document.getDocumentSub().getCommunicationToolId();
        documentSub.setCommunicationTool(communicationToolService.getById(comtoolId));
        documentSub.setCommunicationToolId(comtoolId);

        DocumentOrganization documentOrganization = documentOrganizationService.getByName(document.getDocumentSub().getOrganizationName());

        if(documentOrganization == null){
            documentOrganization = new DocumentOrganization();
            documentOrganization.setName(document.getDocumentSub().getOrganizationName());
            documentOrganizationService.create(documentOrganization);
        }

        documentSub.setOrganizationName(document.getDocumentSub().getOrganizationName());
        documentSub.setOrganizationId(documentOrganization.getId());
        documentSub.setOrganization(documentOrganization);
        newDocument.setDocumentSub(documentSub);

        newDocument.setDocumentSub(documentSub);

        newDocument.setContent(document.getContent());

        newDocument.setAnswerDocumentId(document.getAnswerDocumentId());
        newDocument.setAnswerDocument(documentService.getById(document.getAnswerDocumentId()));
        documentService.createDoc(newDocument);

        return "redirect:" + DocUrls.OutgoingMailNew;
    }

    @RequestMapping(value = DocUrls.OutgoingMailOrganizationList, produces = "application/json")
    @ResponseBody
    public List<String> getOrganizationNames(){
        return documentOrganizationService.getDocumentOrganizationNames();
    }



}
