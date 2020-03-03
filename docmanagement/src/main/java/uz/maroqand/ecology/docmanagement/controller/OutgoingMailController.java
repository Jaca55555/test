package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.service.DocumentHelperService;
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
    private final DocumentTypeService documentTypeService;
    private final CommunicationToolService communicationToolService;
    private final UserService userService;
    private final DocumentOrganizationService documentOrganizationService;
    private final FileService fileService;
    private final DepartmentService departmentService;
    private final DocumentDescriptionService descService;
    private final DocumentSubService documentSubService;
    private final DocumentHelperService documentHelperService;

    @Autowired
    public OutgoingMailController(
            DocumentService documentService,
            JournalService journalService,
            DocumentViewService documentViewService,
            DocumentTypeService documentTypeService,
            CommunicationToolService communicationToolService,
            UserService userService,
            DocumentOrganizationService documentOrganizationService,
            FileService fileService,
            DepartmentService departmentService,
            DocumentDescriptionService descService,
            DocumentSubService documentSubService,
            DocumentHelperService documentHelperService
    ){
        this.documentService = documentService;
        this.journalService = journalService;
        this.documentViewService = documentViewService;
        this.documentTypeService = documentTypeService;
        this.communicationToolService = communicationToolService;
        this.userService = userService;
        this.documentOrganizationService = documentOrganizationService;
        this.fileService = fileService;
        this.departmentService = departmentService;
        this.descService = descService;
        this.documentSubService = documentSubService;
        this.documentHelperService = documentHelperService;
    }

    @RequestMapping(DocUrls.OutgoingMailNew)
    public String newOutgoingMail(Model model){

        model.addAttribute("document", new Document());
        model.addAttribute("journals", journalService.getStatusActive(2));//todo 2
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
    public String newOutgoingMail(Document document,
                                  @RequestParam(name = "communication_tool_id")Integer communicationToolId,
                                  @RequestParam(name = "document_organization_id")String documentOrganizationId_,
                                  @RequestParam(name = "file_ids")List<Integer> file_ids){

        User user = userService.getCurrentUserFromContext();

        Integer documentOrganizationId = parseIdOrCreateNew(documentOrganizationId_, user.getId());

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
        document.setStatus(DocumentStatus.InProgress);
        document.setPerformerName(userService.findById(document.getPerformerId()).getFullName());
        //setting document sub
        DocumentSub docSub = new DocumentSub();
        docSub.setOrganizationId(documentOrganizationId);
        DocumentOrganization documentOrganization = documentOrganizationService.getById(documentOrganizationId);
        docSub.setOrganization(documentOrganization);
        docSub.setOrganizationName(documentOrganization.getName());
        docSub.setCommunicationToolId(communicationToolId);

        Document savedDocument = documentService.createDoc(2, document, user);

        docSub.setDocumentId(savedDocument.getId());
        docSub.setDocument(savedDocument);
        documentSubService.createDocumentSub(docSub);

        return "redirect:" + DocUrls.OutgoingMailList;
    }

    @RequestMapping(DocUrls.OutgoingMailList)
    public String getOutgoingMailList(Model model) {
        Integer organizationId = userService.getCurrentUserFromContext().getOrganizationId();
        Integer outgoingMailType = DocumentTypeEnum.OutgoingDocuments.getId();

        model.addAttribute("documentViews", documentViewService.getStatusActive());
        model.addAttribute("departments", departmentService.getByOrganizationId(organizationId));

        long totalOutgoing = documentService.countAll(outgoingMailType, organizationId);
        long haveAdditionalDocument = documentService.countAllWhichHaveAdditionalDocuments(outgoingMailType, organizationId);

        model.addAttribute("totalOutgoing", totalOutgoing);
        model.addAttribute("inProgress", documentService.countAllByStatus(outgoingMailType, DocumentStatus.InProgress, organizationId));
        model.addAttribute("todayDocuments", documentService.countAllTodaySDocuments(outgoingMailType, organizationId));
        model.addAttribute("haveAdditionalDocument", haveAdditionalDocument);
        model.addAttribute("answerNotAccepted", totalOutgoing - haveAdditionalDocument);
        model.addAttribute("edit_link", DocUrls.OutgoingMailEdit);
        model.addAttribute("view_link", DocUrls.OutgoingMailView);
        model.addAttribute("change_status_link", DocUrls.OutgoingMailChangeStatus);

        return DocTemplates.OutgoingMailList;
    }

    @RequestMapping(value = DocUrls.OutgoingMailListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object>  getOutgoingDocumentListAjax(
            @RequestParam(name = "document_status_id_to_exclude", required = false)Integer documentStatusIdToExclude,
            @RequestParam(name = "document_organization_id", required = false)Integer documentOrganizationId,
            @RequestParam(name = "registration_number", required = false)String registrationNumber,
            @RequestParam(name = "date_begin", required = false)String dateBegin,
            @RequestParam(name = "date_end", required = false)String dateEnd,
            @RequestParam(name = "document_view_id", required = false)Integer documentViewId,
            @RequestParam(name = "content", required = false)String content,
            @RequestParam(name = "department_id", required = false)Integer departmentId,
            Pageable pageable
    ){
        registrationNumber = StringUtils.trimToNull(registrationNumber);
        dateBegin = StringUtils.trimToNull(dateBegin);
        dateEnd = StringUtils.trimToNull(dateEnd);
        content = StringUtils.trimToNull(content);

        Date begin = castDate(dateBegin), end = castDate(dateEnd);

        HashMap<String, Object> result = new HashMap<>();

        Pageable specificPageable = specifyPageableForCurrentFilter(pageable);

        Page<DocumentSub> documentSubPage = documentSubService.findFiltered(
                DocumentTypeEnum.OutgoingDocuments.getId(),
                documentStatusIdToExclude,
                documentOrganizationId,
                registrationNumber,
                begin,
                end,
                documentViewId,
                content,
                departmentId,
                null,
                specificPageable);

        List<Object[]> JSONArray = new ArrayList<>(documentSubPage.getTotalPages());
        for (DocumentSub documentSub : documentSubPage) {
            Document document = documentSub.getDocument();
            if(document == null) continue;
            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    document.getContent() != null ? document.getContent() : "",
                    document.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(document.getCreatedAt()):"",
                    document.getUpdateAt()!=null? Common.uzbekistanDateFormat.format(document.getUpdateAt()):"",
                    document.getStatus(),
                    (document.getPerformerName() != null ?document.getPerformerName(): "") + "<br>" + (departmentService.getById(document.getDepartmentId()) != null ? departmentService.getById(document.getDepartmentId()).getName() : "")
            });
        }

        result.put("recordsTotal", documentSubPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentSubPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);

        return result;
    }


    @RequestMapping(DocUrls.OutgoingMailView)
    public String outgoingMailView(@RequestParam(name = "id")Integer id, Model model){
        Document document = documentService.getById(id);
        document.getJournal().getName();

        model.addAttribute("journal_name", document.getJournal().getName());
        model.addAttribute("document_view_name", document.getDocumentView().getName());
        model.addAttribute("registration_number", document.getRegistrationNumber());
        model.addAttribute("registration_date", Common.uzbekistanDateFormat.format(document.getRegistrationDate()));
        model.addAttribute("updated_at", document.getUpdateAt() != null ? document.getUpdateAt() : "");
        model.addAttribute("document_id", document.getId());
        model.addAttribute("document", document);
        model.addAttribute("tree", documentService.createTree(document));
        model.addAttribute("document_status", document.getStatus());
        DocumentSub documentSub = documentSubService.findOneByDocumentId(document.getId());
        model.addAttribute("communication_tool_name", documentSub.getCommunicationTool().getName());
        Document additionalDocument = documentService.getById(document.getAdditionalDocumentId());
        if(additionalDocument != null) {
            model.addAttribute("additional_document_registration_number", additionalDocument.getRegistrationNumber());
            model.addAttribute("additional_document_view_link", DocUrls.OutgoingMailView + "?id=" + additionalDocument.getId());
        }
        model.addAttribute("document_organization_name", documentSub.getOrganization().getName());

        model.addAttribute("department_name", departmentService.getById(document.getDepartmentId()).getName());
        model.addAttribute("performer_name", document.getPerformerName());
        model.addAttribute("content", document.getContent());
        model.addAttribute("files", document.getContentFiles());

        return DocTemplates.OutgoingMailView;
    }

    @RequestMapping(DocUrls.OutgoingMailEdit)
    public String outgoingMailEdit(@RequestParam(name="id")Integer id, Model model){
        Document document = documentService.getById(id);
        List<DocumentSub> documentSubList = documentSubService.findByDocumentId(document.getId());

        DocumentSub documentSub = null;
        if(documentSubList.size() != 0){
            documentSub = documentSubList.get(documentSubList.size() - 1);
        }

        model.addAttribute("communication_tool_id", documentSub.getCommunicationToolId());
        model.addAttribute("communication_tool_name", documentSub.getCommunicationTool().getName());
        model.addAttribute("document_organization_id", documentSub.getOrganization().getId());
        model.addAttribute("document_organization_name", documentSub.getOrganization().getName());

        model.addAttribute("document", document);
        model.addAttribute("journals", journalService.getStatusActive(2));//TODO 2
        model.addAttribute("documentViews", documentViewService.getStatusActive());
        model.addAttribute("communicationTools", communicationToolService.getStatusActive());
        Document additionalDocument = documentService.getById(document.getAdditionalDocumentId());
        model.addAttribute("additional_document_id", additionalDocument.getId());
        model.addAttribute("registration_number_and_date", additionalDocument.getRegistrationNumber() + " - " + Common.uzbekistanDateFormat.format(additionalDocument.getRegistrationDate()));
        Integer organizationId = userService.getCurrentUserFromContext().getOrganizationId();
        model.addAttribute("departments", departmentService.getByOrganizationId(organizationId));
        model.addAttribute("performers", userService.getEmployeesForForwarding(organizationId));

        return DocTemplates.OutgoingMailEdit;
    }

    @RequestMapping(value = DocUrls.OutgoingMailEdit, method = RequestMethod.POST)
    public String outgoingMailEditSubmit(Document document,
                                         @RequestParam(name = "communication_tool_id")Integer communicationToolId,
                                         @RequestParam(name = "document_organization_id")String documentOrganizationId_,
                                         @RequestParam(name = "file_ids")List<Integer> file_ids){

        Document documentForUpdate = documentService.getById(document.getId());
        User user = userService.getCurrentUserFromContext();
        //updating journal
        documentForUpdate.setJournalId(document.getJournalId());
        documentForUpdate.setJournal(journalService.getById(document.getJournalId()));

        //updating document view
        documentForUpdate.setDocumentViewId(document.getDocumentViewId());
        documentForUpdate.setDocumentView(documentViewService.getById(document.getDocumentViewId()));

        documentForUpdate.setContent(document.getContent());
        User performer = userService.findById(document.getPerformerId());
        documentForUpdate.setPerformerId(performer.getId());
        documentForUpdate.setPerformerPhone(performer.getPhone());
        documentForUpdate.setDepartmentId(document.getDepartmentId());
        documentForUpdate.setAdditionalDocumentId(document.getAdditionalDocumentId());
        document.setUpdateById(user.getId());
        //updating documentSub

        Integer documentOrganizationId = parseIdOrCreateNew(documentOrganizationId_, user.getId());
        DocumentSub documentSub = documentSubService.findOneByDocumentId(document.getId());
        documentSub.setOrganizationId(documentOrganizationId);

        DocumentOrganization documentOrganization = documentOrganizationService.getById(documentOrganizationId);
        documentSub.setOrganization(documentOrganization);
        documentSub.setOrganizationName(documentOrganization.getName());
        documentSub.setCommunicationToolId(communicationToolId);
        documentSub.setCommunicationTool(communicationToolService.getById(communicationToolId));
        documentSub.setDocument(documentForUpdate);

        Set<File> files = new HashSet<>();
        for(Integer id: file_ids) {
            if (id != null) files.add(fileService.findById(id));
        }
        documentForUpdate.setContentFiles(files);

        documentSubService.update(documentSub, user);
        documentService.update(documentForUpdate);

        return "redirect:" + DocUrls.OutgoingMailList;
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

    @RequestMapping(value = DocUrls.OutgoingMailChangeStatus, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody HashMap<String, Object> changeStatus(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "target_status_id")Integer statusId
    ){
        System.out.println("id: " + id + ", statusId: " + statusId);
        HashMap<String, Object> result = new HashMap<>();
        Document document = documentService.getById(id);
        switch (statusId){
            case 1:
            case 2:
                document.setStatus(DocumentStatus.InProgress);
                result.put("first", "Completed");
                result.put("firstId", 3);
                break;
            case 3:
                document.setStatus(DocumentStatus.Completed);
                result.put("first", "InProgress");
                result.put("firstId", 2);
                break;
        }

        Date date = new Date();
        document.setUpdateAt(date);
        document.setUpdateById(userService.getCurrentUserFromContext().getId());

        result.put("updatedAt", Common.uzbekistanDateFormat.format(date));
        result.put("changedOnStatusId", statusId);
        documentService.update(document);
        return result;
    }

    Pageable specifyPageableForCurrentFilter(Pageable pageable){

        String sortString = pageable.getSort().toString();

        String sortDirection = sortString.substring(sortString.indexOf(' ') + 1);
        String sortField = sortString.substring(0, sortString.indexOf(':'));

        if(sortDirection.equals("ASC")){
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("document." + sortField).ascending());
        }else{
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("document." + sortField).descending());
        }
    }

    Date castDate(String dateString){
        if(dateString == null)
            return null;
        else return DateParser.TryParse(dateString, Common.uzbekistanDateFormat);
    }

    Integer parseIdOrCreateNew(String documentOrganizationId_, Integer userId){
        Integer documentOrganizationId;
        try {
            documentOrganizationId = Integer.parseInt(documentOrganizationId_);
        }catch(NumberFormatException ex){
            String name = documentOrganizationId_;
            System.out.println("creating new document organization with '" + name + "' name");
            DocumentOrganization documentOrganization = new DocumentOrganization();
            documentOrganization.setName(name);
            documentOrganization.setStatus(true);
            documentOrganization.setCreatedById(userId);
            documentOrganizationId = documentOrganizationService.create(documentOrganization).getId();
        }
        return documentOrganizationId;
    }

}
