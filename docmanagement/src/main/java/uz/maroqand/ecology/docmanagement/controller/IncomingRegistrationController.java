package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentSubRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 24.12.2019
 */

@Controller
public class IncomingRegistrationController {
    private final DocumentService documentService;
    private final DocumentTypeService documentTypeService;
    private final JournalService journalService;
    private final DocumentViewService documentViewService;
    private final CommunicationToolService communicationToolService;
    private final UserService userService;
    private final FileService fileService;
    private final DocumentOrganizationService organizationService;
    private final DocumentDescriptionService documentDescriptionService;
    private final DocumentSubRepository documentSubRepository;

    @Autowired
    public IncomingRegistrationController(
            DocumentService documentService,
            DocumentDescriptionService documentDescriptionService,
            DocumentTypeService documentTypeService,
            CommunicationToolService communicationToolService,
            UserService userService,
            JournalService journalService,
            FileService fileService,
            DocumentOrganizationService organizationService,
            DocumentViewService documentViewService,
            DocumentSubRepository documentSubRepository
    ) {
        this.documentService = documentService;
        this.documentSubRepository = documentSubRepository;
        this.documentDescriptionService = documentDescriptionService;
        this.documentTypeService = documentTypeService;
        this.communicationToolService = communicationToolService;
        this.userService = userService;
        this.journalService = journalService;
        this.fileService = fileService;
        this.organizationService = organizationService;
        this.documentViewService = documentViewService;
    }

    @RequestMapping(DocUrls.IncomeMailList)
    public String getIncomeListPage(Model model) {
        model.addAttribute("doc_type", documentTypeService.getStatusActive());

        return DocTemplates.IncomeMailList;
    }

    @RequestMapping(value = DocUrls.IncomeMailListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getIncomeList(
            DocFilterDTO filterDTO,
            Pageable pageable
    ) {
        HashMap<String, Object> result = new HashMap<>();
        Page<Document> documentPage = documentService.findFiltered(filterDTO, pageable);
        List<Document> documentList = documentPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentList.size());
        for (Document document : documentList) {
            JSONArray.add(new Object[]{
                    document.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    document.getContent(),
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

    @RequestMapping(DocUrls.IncomeMailView)
    public String getIncomeMail(@RequestParam(name = "id")Integer id, Model model) {
        Document document = documentService.getById(id);
        if (document == null) {
            return "redirect: " + DocUrls.IncomeMailList;
        }
        model.addAttribute("doc", document);
        return DocTemplates.IncomeMailView;
    }

    @RequestMapping(value = DocUrls.IncomingRegistrationNew, method = RequestMethod.GET)
    public String newDocument(Model model) {

        model.addAttribute("document", new Document());
        model.addAttribute("documentSub", new DocumentSub());

        model.addAttribute("journalList", journalService.getStatusActive());
        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("communicationToolList", communicationToolService.getStatusActive());
        model.addAttribute("managerUserList", userService.getEmployeeList());
        model.addAttribute("controlUserList", userService.getEmployeeList());
        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());

        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
        model.addAttribute("action_url", DocUrls.IncomingRegistrationNew);
        return DocTemplates.IncomeMailNew;
    }

    @Transactional
    @RequestMapping(value = {DocUrls.IncomingRegistrationNew, DocUrls.IncomingRegistrationNewTask}, method = RequestMethod.POST)
    public String createDoc(
            @RequestParam(name = "registrationDateStr") String regDate,
            @RequestParam(name = "communicationToolId") Integer communicationToolId,
            @RequestParam(name = "organizationId") Integer organizationId,
            @RequestParam(name = "fileIds", required = false) List<Integer> fileIds,
            Document document
    ) {
        User user = userService.getCurrentUserFromContext();
        Set<File> files = new HashSet<>();
        if(fileIds!=null){
            for (Integer fileId : fileIds) {
                files.add(fileService.findById(fileId));
            }
        }

        document.setContentFiles(files);
        document.setRegistrationDate(DateParser.TryParse(regDate, Common.uzbekistanDateFormat));
        document.setCreatedAt(new Date());
        document.setCreatedById(user.getId());
        document = documentService.createDoc(document);

        DocumentSub documentSub = new DocumentSub();
        documentSub.setDocumentId(document.getId());
        documentSub.setCommunicationToolId(communicationToolId);
        documentSub.setOrganizationId(organizationId);
        documentSubRepository.save(documentSub);

        return "redirect:" + DocUrls.IncomeMailList;
    }

    @RequestMapping(DocUrls.IncomeMailEdit)
    public String editDocument(@RequestParam(name = "id")Integer id, Model model) {
        String response = DocTemplates.IncomeMailEdit;
        Document document = documentService.getById(id);
        if (document == null) {
            response = "redirect:" + DocUrls.IncomeMailList;
        }

        // TODO: ijro va nazorat shakllarini kerakli joydan olish
        List<Object[]> executeForms = new ArrayList<>();
        executeForms.add(new Object[]{"1","Ijro uchun"});
        executeForms.add(new Object[]{"2", "Ma'lumot uchun"});
        List<Object[]> controlForms = new ArrayList<>();
        controlForms.add(new Object[]{"1","Yo'q"});
        controlForms.add(new Object[]{"2","1A shakl"});
        controlForms.add(new Object[]{"3","2A shakl"});
        controlForms.add(new Object[]{"4","3A shakl"});
        controlForms.add(new Object[]{"5","4A shakl"});

        List<DocumentOrganization> organizations = organizationService.getStatusActive();
        List<String> orgNames = new ArrayList<>();
        for (DocumentOrganization organization : organizations) {
            orgNames.add(organization.getName());
        }
        model.addAttribute("action_url", DocUrls.IncomeMailEdit);
        model.addAttribute("doc", document);
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("doc_type", documentTypeService.getStatusActive());
        model.addAttribute("doc_sub_types", DocumentSubType.getDocumentSubTypeList());
        model.addAttribute("communication_list", communicationToolService.getStatusActive());
        model.addAttribute("chief", userService.getEmployeeList());
        model.addAttribute("executeController", userService.getEmployeeList());
        model.addAttribute("doc_list", documentService.findAllActive());
        model.addAttribute("execute_forms", executeForms);
        model.addAttribute("control_forms", controlForms);
        model.addAttribute("organizations", orgNames);

        return response;
    }

    @RequestMapping(value = DocUrls.IncomeMailEdit, method = RequestMethod.POST)
    public String updateDoc(
            @RequestParam(name = "registrationDateStr") String regDate,
            @RequestParam(name = "documentSubType") DocumentSubType type,
            @RequestParam(name = "fileIds") List<Integer> fileIds,
            Document document
    ) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect: " + DocUrls.IncomeMailList;
        }
        Set<File> files = new HashSet<>();
        for (Integer fileId : fileIds) {
            files.add(fileService.findById(fileId));
        }
/*        DocumentOrganization organization = organizationService.getByName(document.getDocumentSub().getOrganizationName());
        if (organization == null) {
            DocumentOrganization newOrg = new DocumentOrganization();
            newOrg.setName(document.getDocumentSub().getOrganizationName());
            newOrg.setStatus(Boolean.TRUE);
            newOrg.setCreatedById(user.getId());
            organization = organizationService.create(newOrg);
        }

        document.getDocumentSub().setOrganizationId(organization.getId());
        document.setContentFiles(files);
        document.setRegistrationDate(DateParser.TryParse(regDate, Common.uzbekistanDateFormat));
        document.getDocumentSub().setType(type);
        document.setUpdateById(user.getId());
        documentService.update(document);*/
        return "redirect:" + DocUrls.IncomeMailList;
    }

    @PostMapping(value = DocUrls.IncomeMailFileUpload)
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "file")MultipartFile uploadFile
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> response = new HashMap<>();

        File file = fileService.uploadFile(uploadFile,user.getId(),"documentFile",uploadFile.getOriginalFilename());
        response.put("file", file);
        return response;
    }

    @GetMapping(value = DocUrls.FileDownload)
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam(name = "id")Integer id) {
        File file = fileService.findById(id);
        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    /*@GetMapping(value = DocUrls.IncomeMailSpecial)
    @ResponseBody
    public HashMap<String, Object> changeSpecial(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "enabled")Boolean enabled
    ) {
        HashMap<String, Object> response = new HashMap<>();
        Document document = documentService.getById(id);
        if (document == null) {
            response.put("status", "not found");
            return response;
        }
        document.setSpecialControl(enabled);
        documentService.update(document);
        response.put("status", "success");
        response.put("value", enabled);
        return response;
    }*/

    /*@GetMapping(value = DocUrls.IncomeMailAddTask)
    public String getAddTaskPage(
            Model model,
            @RequestParam(name = "id")Integer id
    ) {
        Document document = documentService.getById(id);
        if (document == null) {
            return  "redirect:" + DocUrls.IncomeMailList;
        }

        model.addAttribute("doc", document);
        model.addAttribute("task", document.getTask());
        model.addAttribute("attends", userService.getEmployeeList());
        return DocTemplates.IncomeMailAddTask;
    }*/

    @PostMapping(value = DocUrls.IncomeMailAddTask)
    public String addTask(
            @RequestParam(name = "id")Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect:" + DocUrls.IncomeMailList;
        }
        Document document = documentService.getById(id);
        if (document == null) {
            return  "redirect:" + DocUrls.IncomeMailList;
        }

        return "redirect:" + DocUrls.IncomeMailList;
    }
}
