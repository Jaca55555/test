package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import javax.transaction.Transactional;
import java.util.*;

@Controller
public class InnerRegistrationController {

    private final DocumentService documentService;
    private final JournalService journalService;
    private final DocumentTypeService documentTypeService;
    private final CommunicationToolService communicationToolService;
    private final UserService userService;
    private final FileService fileService;
    private final DocumentOrganizationService organizationService;
    private final FolderService folderService;

    @Autowired
    public InnerRegistrationController(DocumentService documentService, JournalService journalService, DocumentTypeService documentTypeService, CommunicationToolService communicationToolService, UserService userService, FileService fileService, DocumentOrganizationService organizationService, FolderService folderService) {
        this.documentService = documentService;
        this.journalService = journalService;
        this.documentTypeService = documentTypeService;
        this.communicationToolService = communicationToolService;
        this.userService = userService;
        this.fileService = fileService;
        this.organizationService = organizationService;
        this.folderService = folderService;
    }

    @RequestMapping(DocUrls.InnerRegistrationNew)
    public String getInnerRegistrationNewPage(Model model) {
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

        List<DocumentOrganization> organizations = organizationService.getStatusActive();
        List<String> orgNames = new ArrayList<>();
        for (DocumentOrganization organization : organizations) {
            orgNames.add(organization.getName());
        }

        model.addAttribute("doc", new Document());
        model.addAttribute("action_url", DocUrls.InnerRegistrationNew);
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
        model.addAttribute("organizations", orgNames);
        return DocTemplates.InnerNew;
    }

    @Transactional
    @RequestMapping(value = DocUrls.InnerRegistrationNew, method = RequestMethod.POST)
    public String createDoc(
            @RequestParam(name = "registrationDateStr") String regDate,
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
        documentService.createDoc(document);
        return "redirect:" + DocUrls.InnerRegistrationList;
    }

    @PostMapping(value = DocUrls.InnerRegistrationFileUpload)
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "file") MultipartFile uploadFile
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> response = new HashMap<>();

        File file = fileService.uploadFile(uploadFile,user.getId(),"documentFile",uploadFile.getOriginalFilename());
        response.put("file", file);
        return response;
    }

}
