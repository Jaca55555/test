package uz.maroqand.ecology.docmanagement.service;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

/**
 * Created by Utkirbek Boltaev on 14.02.2020.
 * (uz)
 */
@Service
public class DocumentHelperService {

    private final DocumentService documentService;
    private final UserService userService;
    private final CommunicationToolService communicationToolService;
    private final DocumentOrganizationService documentOrganizationService;
    private final DocumentTypeService documentTypeService;
    private final DocumentViewService documentViewService;
    private final JournalService journalService;

    public DocumentHelperService(DocumentService documentService, UserService userService, CommunicationToolService communicationToolService, DocumentOrganizationService documentOrganizationService, DocumentTypeService documentTypeService, DocumentViewService documentViewService, JournalService journalService) {
        this.documentService = documentService;
        this.userService = userService;
        this.communicationToolService = communicationToolService;
        this.documentOrganizationService = documentOrganizationService;
        this.documentTypeService = documentTypeService;
        this.documentViewService = documentViewService;
        this.journalService = journalService;
    }

    public String getJournalName(Integer id){
        Journal journal = journalService.getById(id);
        return journal!=null? journal.getName():"";
    }

    public String getDocumentViewName(Integer id){
        DocumentView documentView = documentViewService.getById(id);
        return documentView!=null? documentView.getName():"";
    }

    public String getDocumentOrganizationName(Integer id){
        DocumentOrganization documentOrganization = documentOrganizationService.getById(id);
        return documentOrganization!=null? documentOrganization.getName():"";
    }

    public String getCommunicationToolName(Integer id){
        CommunicationTool communicationTool = communicationToolService.getById(id);
        return communicationTool!=null? communicationTool.getName():"";
    }

    public String getDocumentNumberAndDate(Integer id){
        Document document = documentService.getById(id);
        return document!=null? document.getRegistrationNumber()+""+document.getRegistrationDate():"";
    }

}
