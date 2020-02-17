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
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.dto.OutgoingFilterDto;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
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
    private final DocumentTypeService documentTypeService;
    private final CommunicationToolService communicationToolService;
    private final UserService userService;
    private final DocumentOrganizationService documentOrganizationService;
    private final FileService fileService;
    private final DepartmentService departmentService;
    private final DocumentDescriptionService descService;
    private final DocumentSubService documentSubService;

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
            DocumentSubService documentSubService
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
    public String newOutgoingMail(Document document,
                                  @RequestParam(name = "communication_tool_id")Integer communicationToolId,
                                  @RequestParam(name = "document_organization_id")String documentOrganizationId_,
                                  @RequestParam(name = "file_ids")List<Integer> file_ids){
        Integer documentOrganizationId;
        User user = userService.getCurrentUserFromContext();
        try {
            documentOrganizationId = Integer.parseInt(documentOrganizationId_);
        }catch(NumberFormatException ex){
            String name = documentOrganizationId_;
            System.out.println("creating new document organization with '" + name + "' name");
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
        document.setStatus(DocumentStatus.New);
        document.setPerformerName(userService.findById(document.getPerformerId()).getFullName());
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
        model.addAttribute("documentViews", documentViewService.getStatusActive());
        Integer organizationId = userService.getCurrentUserFromContext().getOrganizationId();
        model.addAttribute("departments", departmentService.getByOrganizationId(organizationId));




        return DocTemplates.OutgoingMailList;
    }

    @RequestMapping(value = DocUrls.OutgoingMailListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object>  getOutgoingDocumentListAjax(
            @RequestParam(name = "document_organization_id", required = false)Integer documentOrganizationId,
            @RequestParam(name = "registration_number", required = false)String registrationNumber,
            @RequestParam(name = "date_begin", required = false)String dateBegin,
            @RequestParam(name = "date_end", required = false)String dateEnd,
            @RequestParam(name = "document_view_id", required = false)String documentViewId,
            @RequestParam(name = "content", required = false)String content,
            @RequestParam(name = "department_id", required = false)String departmentId,
            Pageable pageable
    ){
        registrationNumber = StringUtils.trimToNull(registrationNumber);
        dateBegin = StringUtils.trimToNull(dateBegin);
        dateEnd = StringUtils.trimToNull(dateEnd);
        content = StringUtils.trimToNull(content);
        departmentId = StringUtils.trimToNull(departmentId);
        documentViewId = StringUtils.trimToNull(documentViewId);

        List<Integer> departmentIds = convertToIntegerList(departmentId);
        List<Integer> documentViewIds = convertToIntegerList(documentViewId);

        Date begin = null, end = null;
        if(dateBegin != null)
            begin = DateParser.TryParse(dateBegin, Common.uzbekistanDateFormat);
        if(dateEnd != null)
            end = DateParser.TryParse(dateEnd, Common.uzbekistanDateFormat);

        HashMap<String, Object> result = new HashMap<>();
        DocFilterDTO filter = new DocFilterDTO();
        result.put("inProcess", 0);
        result.put("today", 0);
        result.put("hasAdditionalDocuments", 0);
        result.put("fifth", 0);
        result.put("all", 0);
        filter.setDocumentType(DocumentTypeEnum.OutgoingDocuments.getId());

        OutgoingFilterDto outgoingFilterDto = new OutgoingFilterDto(documentOrganizationId, begin, end, content, departmentIds, documentViewIds, DocumentTypeEnum.OutgoingDocuments.getId());

        Page<Document> documentPage = documentService.findFiltered(filter, pageable);

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
                    document.getStatus(),
                    (document.getPerformerName() != null ?document.getPerformerName(): "") + "\n" + (departmentService.getById(document.getDepartmentId()) != null ? departmentService.getById(document.getDepartmentId()).getName() : "")
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
    public File deleteAttachment(@RequestParam(name = "id")Integer id){
        File file = fileService.findById(id);
        file.setDeleted(true);
        file.setDateDeleted(new Date());
        file.setDeletedById(userService.getCurrentUserFromContext().getId());
        return fileService.save(file);
    }

    List<Integer> convertToIntegerList(String string){
        if(string == null) return null;
        String[] strings = string.split(",");
        List<Integer> ids = new ArrayList<>(strings.length);
        for(String str: strings){
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }



}
