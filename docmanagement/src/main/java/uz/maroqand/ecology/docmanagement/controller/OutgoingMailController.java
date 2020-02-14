package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

@Controller
public class OutgoingMailController {

    private final DocumentService documentService;
    private final DocumentSubService documentSubService;
    private final JournalService journalService;
    private final DocumentViewService documentViewService;
    private final CommunicationToolService communicationToolService;
    private final UserService userService;
    private final DocumentOrganizationService documentOrganizationService;

    @Autowired
    public OutgoingMailController(
            DocumentService documentService,
            DocumentSubService documentSubService,
            JournalService journalService,
            DocumentViewService documentViewService,
            CommunicationToolService communicationToolService,
            UserService userService,
            DocumentOrganizationService documentOrganizationService
    ){
        this.documentService = documentService;
        this.documentSubService = documentSubService;
        this.journalService = journalService;
        this.documentViewService = documentViewService;
        this.communicationToolService = communicationToolService;
        this.userService = userService;
        this.documentOrganizationService = documentOrganizationService;
    }

    @RequestMapping(DocUrls.OutgoingMailNew)
    public String newOutgoingMail(Model model){

        model.addAttribute("document", new Document());
        model.addAttribute("documentSub", new DocumentSub());
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("documentViews", documentViewService.getStatusActive());
        model.addAttribute("communicationTools", communicationToolService.getStatusActive());
        model.addAttribute("organizations", documentOrganizationService.getList());
        return DocTemplates.OutgoingMailNew;
    }

    @RequestMapping(value = DocUrls.OutgoingMailNew, method = RequestMethod.POST)
    public String newOutgoingMail(
            @RequestParam(name = "communicationToolId") Integer communicationToolId,
            @RequestParam(name = "documentOrganizationId") Integer documentOrganizationId,
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

        return "redirect:" + DocUrls.OutgoingMailNew;
    }

    @RequestMapping(value = DocUrls.OutgoingMailOrganizationList, produces = "application/json")
    @ResponseBody
    public List<String> getOrganizationNames() {
        return documentOrganizationService.getDocumentOrganizationNames();
    }

    @RequestMapping(value = DocUrls.OutgoingMailListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object>  getOutgoingDocumentListAjax(
            DocFilterDTO filter,
            Pageable pageable
    ){

        Page<Document> documentPage = documentService.findFiltered(filter, pageable);

        HashMap<String, Object> res = new HashMap<>();

        res.put("recordsTotal", documentPage.getTotalElements());
        res.put("recordsFiltered", documentPage.getTotalElements());

        List<Object[]> data = new ArrayList<>(documentPage.getContent().size());

        /*for(Document document: documentPage){
            data.add(new Object[]{document.getRegistrationNumber(), document.getRegistrationDate(), document.getDocumentDescription(), document.getCreatedAt(), document.getCreatedAt(), document.getCreatedAt()});
        }*/

        res.put("data", data);

        return res;
    }

    @RequestMapping(DocUrls.OutgoingMailList)
    public String getOutgoingMailList(Model model) {
        model.addAttribute("organizationList", documentOrganizationService.getList());
        return DocTemplates.OutgoingMailList;
    }



}
