package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.constant.DocumentSubType;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 24.12.2019
 */

@Controller
public class IncomeMailController {
    private final DocumentService documentService;
    private final JournalService journalService;
    private final DocumentTypeService documentTypeService;
    private final CommunicationToolService communicationToolService;
    private final UserService userService;
    private final FileService fileService;
    private final DocumentOrganizationService organizationService;
    private final FolderService folderService;

    @Autowired
    public IncomeMailController(
            DocumentService documentService,
            DocumentTypeService documentTypeService,
            CommunicationToolService communicationToolService,
            UserService userService,
            JournalService journalService,
            FileService fileService, DocumentOrganizationService organizationService, FolderService folderService) {
        this.documentService = documentService;
        this.documentTypeService = documentTypeService;
        this.communicationToolService = communicationToolService;
        this.userService = userService;
        this.journalService = journalService;
        this.fileService = fileService;
        this.organizationService = organizationService;
        this.folderService = folderService;
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
                    document.getDocumentDescription().getContent(),
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

    @RequestMapping(value = DocUrls.IncomeMailNew, method = RequestMethod.GET)
    public String newDocument(Model model) {
        User user = userService.getCurrentUserFromContext();
        if (user == null) {
            return "redirect: " + DocUrls.IncomeMailList;
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
        model.addAttribute("doc", new Document());
        model.addAttribute("action_url", DocUrls.IncomeMailNew);
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("doc_type", documentTypeService.getStatusActive());
        model.addAttribute("doc_sub_types", DocumentSubType.getDocumentSubTypeList());
        model.addAttribute("communication_list", communicationToolService.getStatusActive());
        model.addAttribute("folder_list", folderService.getFolderList());
        model.addAttribute("chief", userService.getEmployeeList());
        model.addAttribute("executeController", userService.getEmployeeList());
        model.addAttribute("doc_list", documentService.findAllActive());
        model.addAttribute("description_list", documentService.getDescriptionsList());
        model.addAttribute("execute_forms", executeForms);
        model.addAttribute("control_forms", controlForms);
        return DocTemplates.IncomeMailNew;
    }

    @Transactional
    @RequestMapping(value = DocUrls.IncomeMailNew, method = RequestMethod.POST)
    public String createDoc(
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
        document.setContentFiles(files);
        document.setRegistrationDate(DateParser.TryParse(regDate, Common.uzbekistanDateFormat));
        document.setCreatedAt(new Date());
        document.setCreatedById(user.getId());
        document.getDocumentSub().setType(type);
        documentService.createDoc(document);
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

        model.addAttribute("action_url", DocUrls.IncomeMailEdit);
        model.addAttribute("doc", document);
        model.addAttribute("journal", journalService.getStatusActive());
        model.addAttribute("doc_type", documentTypeService.getStatusActive());
        model.addAttribute("doc_sub_types", DocumentSubType.getDocumentSubTypeList());
        model.addAttribute("communication_list", communicationToolService.getStatusActive());
        model.addAttribute("folder_list", folderService.getFolderList());
        model.addAttribute("chief", userService.getEmployeeList());
        model.addAttribute("executeController", userService.getEmployeeList());
        model.addAttribute("doc_list", documentService.findAllActive());
        model.addAttribute("description_list", documentService.getDescriptionsList());
        model.addAttribute("execute_forms", executeForms);
        model.addAttribute("control_forms", controlForms);
        model.addAttribute("organizations", organizationService.getStatusActive());

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
        document.setContentFiles(files);
        document.setRegistrationDate(DateParser.TryParse(regDate, Common.uzbekistanDateFormat));
        document.getDocumentSub().setType(type);
        document.setUpdateById(user.getId());
        documentService.update(document);
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
}
