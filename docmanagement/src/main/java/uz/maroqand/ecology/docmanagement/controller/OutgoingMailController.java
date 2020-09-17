package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.entity.user.Position;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.PositionService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.service.DocumentHelperService;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;
import org.springframework.data.domain.Page;
import javax.transaction.Transactional;
import java.util.*;
import java.lang.NumberFormatException;

import static uz.maroqand.ecology.docmanagement.constant.DocUrls.ChangePerformerTask;
import static uz.maroqand.ecology.docmanagement.constant.DocUrls.OutgoingMailTask;

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
    private final PositionService positionService;

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
            DocumentHelperService documentHelperService,
            PositionService positionService
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
        this.positionService = positionService;
    }


    @RequestMapping(DocUrls.OutgoingMailList)
    public String getOutgoingMailList(Model model) {
        Integer organizationId = userService.getCurrentUserFromContext().getOrganizationId();
        Integer outgoingMailType = DocumentTypeEnum.OutgoingDocuments.getId();

        model.addAttribute("documentViews", documentViewService.getStatusActiveAndByType(organizationId,"OutgoingDocuments"));
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
            @RequestParam(name = "tab", required = false)Integer tab,
            Pageable pageable
    ){
        registrationNumber = StringUtils.trimToNull(registrationNumber);

        dateBegin = StringUtils.trimToNull(dateBegin);
        dateEnd = StringUtils.trimToNull(dateEnd);
        content = StringUtils.trimToNull(content);

        Date begin = castDate(dateBegin), end = castDate(dateEnd);

        HashMap<String, Object> result = new HashMap<>();

        MutableBoolean hasAdditionalDocument = new MutableBoolean();
        MutableBoolean findTodayS = new MutableBoolean();
        MutableBoolean hasAdditionalNotRequired = new MutableBoolean();
        MutableBoolean  findTodaySNotRequired = new MutableBoolean();
        List<DocumentStatus> statuses = new ArrayList<>(2);

        documentSubService.defineFilterInputForOutgoingListTabs(tab, hasAdditionalDocument, findTodayS, statuses, hasAdditionalNotRequired, findTodaySNotRequired);

//        if(tab == 7){
//            statuses.clear();
//            statuses.add(DocumentStatus.InProgress);
//        }
        Boolean hasAdditional = !hasAdditionalNotRequired.booleanValue() ? hasAdditionalDocument.booleanValue() : null;
        Boolean findTodayS_ = !findTodaySNotRequired.booleanValue() ? findTodayS.booleanValue() : null;
        User user = userService.getCurrentUserFromContext();
        Pageable specificPageable = specifyPageableForCurrentFilter(pageable);

        Page<DocumentSub> documentSubPage = documentSubService.findFiltered(
                DocumentTypeEnum.OutgoingDocuments.getId(),
                user.getOrganizationId(),
                documentStatusIdToExclude,
                documentOrganizationId,
                null,
                registrationNumber,
                begin,
                end,
                documentViewId,
                content,
                departmentId,
                null,
                statuses,
                hasAdditional,
                findTodayS_,
                specificPageable);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        Integer userId = userService.getCurrentUserFromContext().getId();
        List<Object[]> JSONArray = new ArrayList<>(documentSubPage.getTotalPages());
        for (DocumentSub documentSub : documentSubPage) {
            Document document = documentSub.getDocument();
            if(document == null) continue;
            if(document.getInsidePurpose() != null && document.getInsidePurpose()) {
                    if(document.getCreatedById() != userId && document.getPerformerId() != userId)
                        continue;
            }
            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate() != null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()) : "",
                    document.getContent() != null ? document.getContent() : "",
//                    document.getCreatedAt() != null ? Common.uzbekistanDateFormat.format(document.getCreatedAt()) : ""
                    documentSub.getDocumentOrganizations(),
                    document.getUpdateAt() != null ? Common.uzbekistanDateFormat.format(document.getUpdateAt()) : "",
                    document.getStatus().getName(),
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
        model.addAttribute("document_status", document.getStatus().getName());
        DocumentSub documentSub = documentSubService.findOneByDocumentId(document.getId());
        model.addAttribute("communication_tool_name", documentSub.getCommunicationTool().getName());
        Document additionalDocument = documentService.getById(document.getAdditionalDocumentId());
        if(additionalDocument != null) {
            model.addAttribute("additional_document_registration_number", additionalDocument.getRegistrationNumber());

            Integer additionalDocumentTypeId = additionalDocument.getDocumentTypeId();
            String viewLink;

            if(additionalDocumentTypeId.equals(DocumentTypeEnum.IncomingDocuments.getId()))
                viewLink = DocUrls.IncomingRegistrationView;
            else if(additionalDocumentTypeId.equals(DocumentTypeEnum.OutgoingDocuments.getId()))
                viewLink = DocUrls.OutgoingMailView;
            else
                viewLink = DocUrls.InnerView;

            model.addAttribute("additional_document_view_link", viewLink + "?id=" + additionalDocument.getId());
        }
        String document_organization_name = "";
        Set<DocumentOrganization> documentOrganizationSet = documentSub.getDocumentOrganizations();
        if (documentOrganizationSet!=null && documentOrganizationSet.size()>0){
            for (DocumentOrganization documentOrganization: documentOrganizationSet) {
                document_organization_name +=documentOrganization.getName() + ", ";
            }
        }else if (documentSub.getOrganizationId()!=null){
            document_organization_name=documentSub.getOrganization().getName();
        }
        model.addAttribute("document_organization_name", document_organization_name);

        model.addAttribute("department_name", departmentService.getById(document.getDepartmentId()).getName());
        model.addAttribute("performer_name", document.getPerformerName());
        model.addAttribute("content", document.getContent());
        model.addAttribute("files", document.getContentFiles());

        return DocTemplates.OutgoingMailView;
    }

    @RequestMapping(DocUrls.OutgoingMailNew)
    public String newOutgoingMail(Model model){
        User user =userService.getCurrentUserFromContext();
        model.addAttribute("document", new Document());
        model.addAttribute("journals", journalService.getStatusActive(user.getOrganizationId(),2));//todo 2
        model.addAttribute("documentViews", documentViewService.getStatusActiveAndByType(user.getOrganizationId(),"OutgoingDocuments"));
        model.addAttribute("communicationTools", communicationToolService.getStatusActive());
        model.addAttribute("organizations", documentOrganizationService.getList());
        Integer organizationId = userService.getCurrentUserFromContext().getOrganizationId();
        model.addAttribute("departments", departmentService.getByOrganizationId(organizationId));
        model.addAttribute("performers", userService.getEmployeesForForwarding(organizationId));
        List<Position> positions = positionService.getAll();
        Collections.reverse(positions);
        model.addAttribute("positions", positions);
        model.addAttribute("users", userService.getEmployeesForDocManageAndIsExecutive("chief"));
        model.addAttribute("performerId",null);
        return DocTemplates.OutgoingMailNew;
    }

    @RequestMapping(DocUrls.OutgoingMailListIn)
    public String getOutgoingListInPage(Model model) {
        User user = userService.getCurrentUserFromContext();

        Integer departmentId = user.getDepartmentId();
        Integer organizationId = user.getOrganizationId();
        Integer outgoingMailType = DocumentTypeEnum.OutgoingDocuments.getId();
        Integer userId = user.getId();
        long totalOutgoing = documentService.countAll(outgoingMailType, organizationId, departmentId, userId);
        long haveAdditionalDocument =  documentService.countAllWhichHaveAdditionalDocuments(outgoingMailType, organizationId, departmentId, userId);

        model.addAttribute("inProgress", documentService.countAllByStatus(outgoingMailType, DocumentStatus.InProgress, organizationId, departmentId, userId));
        model.addAttribute("todayDocuments", documentService.countAllTodaySDocuments(outgoingMailType, organizationId,departmentId, userId));

        model.addAttribute("haveAdditionalDocument", haveAdditionalDocument);
        //additional document is null, 'answer not accepted translation tag'
        model.addAttribute("answerNotAccepted", totalOutgoing - haveAdditionalDocument);
        model.addAttribute("totalOutgoing", totalOutgoing);

        model.addAttribute("departments", departmentService.getByOrganizationId(organizationId));
        model.addAttribute("documentViews", documentViewService.getStatusActive());


        model.addAttribute("view_link", DocUrls.OutgoingMailListView);

        return DocTemplates.OutgoingMailListIn;
    }
    @RequestMapping(value = DocUrls.OutgoingMailListInAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object>  getOutgoingDocumentListInAjax(
            @RequestParam(name = "document_status_id_to_exclude", required = false)Integer documentStatusIdToExclude,
            @RequestParam(name = "document_organization_id", required = false)Integer documentOrganizationId,
            @RequestParam(name = "registration_number", required = false)String registrationNumber,
            @RequestParam(name = "date_begin", required = false)String dateBegin,
            @RequestParam(name = "date_end", required = false)String dateEnd,
            @RequestParam(name = "document_view_id", required = false)Integer documentViewId,
            @RequestParam(name = "content", required = false)String content,
            @RequestParam(name = "department_id", required = false)Integer departmentId,
            @RequestParam(name = "tab", required = false)Integer tab,
            Pageable pageable
    ){
        registrationNumber = StringUtils.trimToNull(registrationNumber);

        dateBegin = StringUtils.trimToNull(dateBegin);
        dateEnd = StringUtils.trimToNull(dateEnd);
        content = StringUtils.trimToNull(content);

        Date begin = castDate(dateBegin), end = castDate(dateEnd);

        HashMap<String, Object> result = new HashMap<>();
        User user = userService.getCurrentUserFromContext();
        MutableBoolean hasAdditionalDocument = new MutableBoolean();
        MutableBoolean findTodayS = new MutableBoolean();
        MutableBoolean hasAdditionalNotRequired = new MutableBoolean();
        MutableBoolean  findTodaySNotRequired = new MutableBoolean();
        List<DocumentStatus> statuses = new ArrayList<>(2);
        Set<Integer> documentOrganizations= documentOrganizationService.getByOrganizationId(user.getOrganizationId());
        documentSubService.defineFilterInputForOutgoingListTabs(tab, hasAdditionalDocument, findTodayS, statuses, hasAdditionalNotRequired, findTodaySNotRequired);
        statuses.clear();
        statuses.add(DocumentStatus.Completed);
        Boolean hasAdditional = !hasAdditionalNotRequired.booleanValue() ? hasAdditionalDocument.booleanValue() : null;
        Boolean findTodayS_ = !findTodaySNotRequired.booleanValue() ? findTodayS.booleanValue() : null;
        Pageable specificPageable = specifyPageableForCurrentFilter(pageable);

        Page<DocumentSub> documentSubPage = documentSubService.findFiltered(
                DocumentTypeEnum.OutgoingDocuments.getId(),
                null,
                documentStatusIdToExclude,
                documentOrganizationId,
                documentOrganizations,
                registrationNumber,
                begin,
                end,
                documentViewId,
                content,
                departmentId,
                user.getId(),
                statuses,
                hasAdditional,
                findTodayS_,
                specificPageable);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        Integer userId = userService.getCurrentUserFromContext().getId();
        List<Object[]> JSONArray = new ArrayList<>(documentSubPage.getTotalPages());
        for (DocumentSub documentSub : documentSubPage) {
            Document document = documentSub.getDocument();
            if(document == null) continue;
            if(document.getInsidePurpose() != null && document.getInsidePurpose()) {
                if(document.getCreatedById() != userId && document.getPerformerId() != userId)
                    continue;
            }
            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate() != null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()) : "",
                    document.getContent() != null ? document.getContent() : "",
                    document.getCreatedAt() != null ? Common.uzbekistanDateFormat.format(document.getCreatedAt()) : "",
                    document.getUpdateAt() != null ? Common.uzbekistanDateFormat.format(document.getUpdateAt()) : "",
                    document.getStatus().getName(),
                    (document.getPerformerName() != null ?document.getPerformerName(): "") + "<br>" + (departmentService.getById(document.getDepartmentId()) != null ? departmentService.getById(document.getDepartmentId()).getName() : "")
            });
        }

        result.put("recordsTotal", documentSubPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentSubPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);

        return result;
    }
    @RequestMapping(DocUrls.OutgoingMailListView)
    public String getOutgoingViewPage(@RequestParam(name = "id")Integer id, Model model) {

        Document document = documentService.getById(id);
        document.getJournal().getName();
        model.addAttribute("doc_type", DocumentTypeEnum.getList());
        model.addAttribute("journal_name", document.getJournal().getName());
        model.addAttribute("document_view_name", document.getDocumentView().getName());
        model.addAttribute("registration_number", document.getRegistrationNumber());
        model.addAttribute("registration_date", Common.uzbekistanDateFormat.format(document.getRegistrationDate()));
        model.addAttribute("updated_at", document.getUpdateAt() != null ? document.getUpdateAt() : "");
        model.addAttribute("document", document);
        model.addAttribute("tree", documentService.createTree(document));
        model.addAttribute("document_id", document.getId());
        model.addAttribute("document_status", document.getStatus().getName());
        DocumentSub documentSub = documentSubService.findOneByDocumentId(document.getId());
        model.addAttribute("communication_tool_name", documentSub.getCommunicationTool().getName());
        Document additionalDocument = documentService.getById(document.getAdditionalDocumentId());
        if(additionalDocument != null) {
            model.addAttribute("additional_document_registration_number", additionalDocument.getRegistrationNumber());
            Integer additionalDocumentTypeId = additionalDocument.getDocumentTypeId();
            String viewLink;
            if(additionalDocumentTypeId.equals(DocumentTypeEnum.IncomingDocuments.getId()))
                viewLink = DocUrls.IncomingView;
            else if(additionalDocumentTypeId.equals(DocumentTypeEnum.OutgoingDocuments.getId()))
                viewLink = DocUrls.OutgoingView;
            else
                viewLink = DocUrls.InnerView;

            model.addAttribute("additional_document_view_link", viewLink + "?id=" + additionalDocument.getId());
        }
        String document_organization_name = "";
        Set<DocumentOrganization> documentOrganizationSet = documentSub.getDocumentOrganizations();
        if (documentOrganizationSet!=null && documentOrganizationSet.size()>0){
            for (DocumentOrganization documentOrganization: documentOrganizationSet) {
                if(documentOrganization.getLevel()!=null&&documentOrganization.getLevel()!=0)
                {document_organization_name +=documentOrganization.getName() + ", ";}
                else{
                    document_organization_name+=documentOrganizationService.getById(documentOrganization.getParent()).getName();
                }
            }
        }else if (documentSub.getOrganizationId()!=null){
            document_organization_name=documentSub.getOrganization().getName();
        }

        model.addAttribute("document_organization_name", document_organization_name);

        model.addAttribute("department_name", departmentService.getById(document.getDepartmentId()).getName());
        model.addAttribute("performer_name", document.getPerformerName());
        model.addAttribute("content", document.getContent());
        model.addAttribute("files", document.getContentFiles());
        model.addAttribute("action_url",OutgoingMailTask);
        return DocTemplates.OutgoingMailListView;
    }
    @PostMapping(OutgoingMailTask)
    public String changePerformer(
            Document document,
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "type")Integer type
    ) {
        Document document1=documentService.getById(id);
        System.out.println(type);
        document1.setDocumentTypeId(type);
        document1.setExecuteForm(ExecuteForm.Performance);
        documentService.update(document1);
        return "redirect:" + DocUrls.OutgoingMailListIn;
    }

    @Transactional
    @PostMapping(DocUrls.OutgoingMailNew)
    public String newOutgoingMail(Document document,
                                  @RequestParam(name = "communication_tool_id")Integer communicationToolId,
                                  @RequestParam(name = "document_organization_id")List<String> documentOrganizationIds,
                                  @RequestParam(name = "file_ids")List<Integer> file_ids,
                                  @RequestParam(name = "position_id")Integer position_id){

        User user = userService.getCurrentUserFromContext();

        boolean insidePurpose = document.getInsidePurpose() != null ? document.getInsidePurpose() : false;

        Set<File> files = new HashSet<File>();
        for(Integer id: file_ids) {
            if (id != null) files.add(fileService.findById(id));
        }
        document.setContentFiles(files);
        //journal, registration number and registration date
        Journal journal = journalService.getById(document.getJournalId());
        document.setJournal(journal);
        document.setRegistrationNumber(documentService.getOutDocNumber(position_id,document.getDepartmentId(),1));
        document.setRegistrationDate(new Date());
        Department department = departmentService.getById(document.getDepartmentId());
        Document documentLast = documentService.getLastOutDocument(department.getOrganizationId());
        Integer lastDocNumber=1;
        if (documentLast!=null && documentLast.getDocOutLastNumber()!=null){
            lastDocNumber = documentLast.getDocOutLastNumber()+1;
        }
        document.setDocOutLastNumber(lastDocNumber);

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
        Set<DocumentOrganization> documentOrganizationSet = new HashSet<>();

        for (String docIdOrName: documentOrganizationIds) {
            DocumentOrganization documentOrganization = documentOrganizationService.getById(parseIdOrCreateNew(docIdOrName, user.getId()));
            if (documentOrganization!=null&&documentOrganization.getParent()!=null){
                documentOrganizationSet.add(documentOrganization);
            }else{
                documentOrganizationSet.add(documentOrganization);
                documentOrganizationSet.addAll(documentOrganizationService.getByParent(documentOrganization.getId()));
            }
        }
        docSub.setDocumentOrganizations(documentOrganizationSet);
        docSub.setCommunicationToolId(communicationToolId);
        document.setInsidePurpose(insidePurpose);
        Document savedDocument = documentService.createDoc2(2, DocumentStatus.InProgress, document, user);
        docSub.setDocumentId(savedDocument.getId());
        docSub.setDocument(savedDocument);
        documentSubService.createDocumentSub(docSub);

        return "redirect:" + DocUrls.OutgoingMailList;
    }

    @RequestMapping(DocUrls.OutgoingMailEdit)
    public String outgoingMailEdit(@RequestParam(name="id")Integer id, Model model){
        Document document = documentService.getById(id);
        List<DocumentSub> documentSubList = documentSubService.findByDocumentId(document.getId());

        DocumentSub documentSub = null;
        if(documentSubList.size() != 0){
            documentSub = documentSubList.get(documentSubList.size() - 1);
        }

        Set<DocumentOrganization> documentOrganizationSet = documentSub.getDocumentOrganizations();
        if (documentOrganizationSet==null && documentSub.getOrganizationId()!=null){
            documentOrganizationSet = new HashSet<>();
            documentOrganizationSet.add(documentSub.getOrganization());
        }
        User user =userService.getCurrentUserFromContext();
        model.addAttribute("communication_tool_id", documentSub.getCommunicationToolId());
        model.addAttribute("communication_tool_name", documentSub.getCommunicationTool().getName());
        model.addAttribute("document_organization_ids", documentOrganizationSet);

        model.addAttribute("document", document);
        model.addAttribute("journals", journalService.getStatusActive(user.getOrganizationId(),2));//TODO 2
        model.addAttribute("documentViews", documentViewService.getStatusActiveAndByType(user.getOrganizationId(),"OutgoingDocuments"));
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
                                         @RequestParam(name = "document_organization_id")List<String> documentOrganizationIds,
                                         @RequestParam(name = "file_ids")List<Integer> file_ids){

        Document documentForUpdate = documentService.getById(document.getId());
        User user = userService.getCurrentUserFromContext();
        //updating journal
        documentForUpdate.setJournalId(document.getJournalId());
        documentForUpdate.setJournal(journalService.getById(document.getJournalId()));

        boolean insidePurpose = document.getInsidePurpose() != null ? document.getInsidePurpose() : false;
        documentForUpdate.setInsidePurpose(insidePurpose);

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

        DocumentSub documentSub = documentSubService.findOneByDocumentId(document.getId());

        Set<DocumentOrganization> documentOrganizationSet = documentSub.getDocumentOrganizations();
        if (documentOrganizationSet==null){
            documentOrganizationSet = new HashSet<>();
        }
        for (String docIdOrName: documentOrganizationIds) {
            DocumentOrganization documentOrganization = documentOrganizationService.getById(parseIdOrCreateNew(docIdOrName, user.getId()));
            if (documentOrganization!=null){
                documentOrganizationSet.add(documentOrganization);
            }
        }
        documentSub.setDocumentOrganizations(documentOrganizationSet);
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

        HashMap<String, Object> result = new HashMap<>();
        Document document = documentService.getById(id);
        switch (statusId){
            case 1:
            case 2:
                document.setStatus(DocumentStatus.InProgress);
                result.put("first", DocumentStatus.Completed.getName());
                result.put("firstId", 3);
                break;
            case 3:
                document.setStatus(DocumentStatus.Completed);
                result.put("first", DocumentStatus.InProgress.getName());
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

    static Date castDate(String dateString){
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

    @RequestMapping(DocUrls.OutgoingRegNumber)
    @ResponseBody
    public HashMap<String,Object> getRegNumber(
            @RequestParam(name = "positionId") Integer positionId,
            @RequestParam(name = "departmentId") Integer departmentId
    ){
        HashMap<String,Object> result = new HashMap<>();
        result.put("status",0);
            String regNumber = documentService.getOutDocNumber(positionId,departmentId,0);
            if (regNumber !=null) {
                result.put("status", 1);
                result.put("regNumber", regNumber);
            }
        return result;
    }

}
