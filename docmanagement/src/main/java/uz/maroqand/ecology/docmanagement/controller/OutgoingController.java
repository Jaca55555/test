package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;
import uz.maroqand.ecology.docmanagement.entity.DocumentType;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentSubService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentViewService;

import java.sql.SQLOutput;
import java.util.*;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz) Chiquvchi xatlar
 */
@Controller
public class OutgoingController {

    private final DocumentService documentService;
    private final UserService userService;
    private final DepartmentService departmentService;
    private final DocumentViewService documentViewService;
    private final DocumentSubService documentSubService;

    public OutgoingController(DocumentService documentService, UserService userService, DepartmentService departmentService, DocumentViewService documentViewService, DocumentSubService documentSubService){
        this.documentService = documentService;
        this.userService = userService;
        this.departmentService = departmentService;
        this.documentViewService = documentViewService;
        this.documentSubService = documentSubService;
    }


    @RequestMapping(DocUrls.OutgoingList)
    public String getOutgoingListPage(Model model) {
        User user = userService.getCurrentUserFromContext();

        Integer departmentId = user.getDepartmentId();
        Integer organizationId = user.getOrganizationId();
        Integer outgoingMailType = DocumentTypeEnum.OutgoingDocuments.getId();
        long totalOutgoing = documentService.countAll(outgoingMailType, organizationId, departmentId);
        long haveAdditionalDocument =  documentService.countAllWhichHaveAdditionalDocuments(outgoingMailType, organizationId, departmentId);

        model.addAttribute("inProgress", documentService.countAllByStatus(outgoingMailType, DocumentStatus.InProgress, organizationId, departmentId));
        model.addAttribute("todayDocuments", documentService.countAllTodaySDocuments(outgoingMailType, organizationId,departmentId));

        model.addAttribute("haveAdditionalDocument", haveAdditionalDocument);
        //additional document is null, 'answer not accepted translation tag'
        model.addAttribute("answerNotAccepted", totalOutgoing - haveAdditionalDocument);
        model.addAttribute("totalOutgoing", totalOutgoing);

        model.addAttribute("departments", departmentService.getByOrganizationId(organizationId));
        model.addAttribute("documentViews", documentViewService.getStatusActive());


        model.addAttribute("view_link", DocUrls.OutgoingView);

        return DocTemplates.OutgoingList;
    }

    @RequestMapping(DocUrls.OutgoingView)
    public String getOutgoingViewPage(@RequestParam(name = "id")Integer id, Model model) {

        Document document = documentService.getById(id);
        document.getJournal().getName();

        model.addAttribute("journal_name", document.getJournal().getName());
        model.addAttribute("document_view_name", document.getDocumentView().getName());
        model.addAttribute("registration_number", document.getRegistrationNumber());
        model.addAttribute("registration_date", Common.uzbekistanDateFormat.format(document.getRegistrationDate()));
        model.addAttribute("updated_at", document.getUpdateAt() != null ? document.getUpdateAt() : "");
        model.addAttribute("document", document);
        model.addAttribute("tree", documentService.createTree(document));
        model.addAttribute("document_id", document.getId());
        model.addAttribute("document_status", document.getStatus());
        DocumentSub documentSub = documentSubService.findOneByDocumentId(document.getId());
        model.addAttribute("communication_tool_name", documentSub.getCommunicationTool().getName());
        Document additionalDocument = documentService.getById(document.getAdditionalDocumentId());
        if(additionalDocument != null) {
            model.addAttribute("additional_document_registration_number", additionalDocument.getRegistrationNumber());

            DocumentType type = additionalDocument.getDocumentType();
            String viewLink;
            if(type.getType().getId()  == DocumentTypeEnum.IncomingDocuments.getId())
                viewLink = DocUrls.IncomingView;
            else if(type.getType().getId() == DocumentTypeEnum.OutgoingDocuments.getId())
                viewLink = DocUrls.OutgoingView;
            else
                viewLink = DocUrls.InnerView;

            model.addAttribute("additional_document_view_link", viewLink + "?id=" + additionalDocument.getId());
        }
        String document_organization_name="";
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

        return DocTemplates.OutgoingView;
    }

    @RequestMapping(value = DocUrls.OutgoingListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getOutgoingDocumentListAjax(
            @RequestParam(name = "document_status_id_to_exclude", required = false)Integer documentStatusIdToExclude,
            @RequestParam(name = "document_organization_id", required = false)Integer documentOrganizationId,
            @RequestParam(name = "registration_number", required = false)String registrationNumber,
            @RequestParam(name = "date_begin", required = false)String dateBegin,
            @RequestParam(name = "date_end", required = false)String dateEnd,
            @RequestParam(name = "document_view_id", required = false)Integer documentViewId,
            @RequestParam(name = "content", required = false)String content,
            Pageable pageable
    ){
        registrationNumber = StringUtils.trimToNull(registrationNumber);
        dateBegin = StringUtils.trimToNull(dateBegin);
        dateEnd = StringUtils.trimToNull(dateEnd);
        content = StringUtils.trimToNull(content);

        Date begin = castDate(dateBegin), end = castDate(dateEnd);

        HashMap<String, Object> result = new HashMap<>();

        Pageable specificPageable = specifyPageableForCurrentFilter(pageable);
        User user = userService.getCurrentUserFromContext();
        Integer departmentId = user.getDepartmentId();
        Integer performerId = user.getId();
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
                performerId,
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

}