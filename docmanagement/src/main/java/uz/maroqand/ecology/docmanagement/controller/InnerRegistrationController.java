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
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import javax.transaction.Transactional;
import java.util.*;

@Controller
public class InnerRegistrationController {

    private final DocumentService documentService;
    private final DocumentTypeService documentTypeService;
    private final UserService userService;
    private final FileService fileService;
    private final FolderService folderService;
    private final DocumentOrganizationService documentOrganizationService;
    private final DocumentDescriptionService documentDescriptionService;
    private final JournalService journalService;

    @Autowired
    public InnerRegistrationController(DocumentService documentService, DocumentTypeService documentTypeService, CommunicationToolService communicationToolService, UserService userService, FileService fileService, FolderService folderService, DocumentOrganizationService documentOrganizationService, DocumentDescriptionService documentDescriptionService, JournalService journalService) {
        this.documentService = documentService;
        this.documentTypeService = documentTypeService;
        this.userService = userService;
        this.fileService = fileService;
        this.folderService = folderService;
        this.documentOrganizationService = documentOrganizationService;
        this.documentDescriptionService = documentDescriptionService;
        this.journalService = journalService;
    }

    @RequestMapping(DocUrls.InnerRegistrationList)
    public String getIncomeListPage(Model model) {

        model.addAttribute("doc_type", documentTypeService.getStatusActive());
        model.addAttribute("journalList", journalService.getStatusActive());
        model.addAttribute("doc_type", documentTypeService.getStatusActive());
        model.addAttribute("chief", userService.getEmployeeList());
        model.addAttribute("executes", userService.getEmployeeList());
        return DocTemplates.InnerRegistrationList;
    }

    @RequestMapping(value = DocUrls.InnerRegistrationListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getInnerRegistrationListAjax(
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

    @RequestMapping(DocUrls.InnerRegistrationNew)
    public String getInnerRegistrationNewPage(Model model) {

        model.addAttribute("doc", new Document());
        model.addAttribute("journalList", journalService.getStatusActive());
        model.addAttribute("action_url", DocUrls.InnerRegistrationNew);
        model.addAttribute("descriptionList", documentDescriptionService.getDescriptionList());
        model.addAttribute("doc_type", documentTypeService.getStatusActive());
        model.addAttribute("folder_list", folderService.getFolderList());
        model.addAttribute("chief", userService.getEmployeeList());
        model.addAttribute("executeController", userService.getEmployeeList());
        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());
        return DocTemplates.InnerRegistrationNew;
    }

    @Transactional
    @RequestMapping(value = DocUrls.InnerRegistrationNew, method = RequestMethod.POST)
    public String createDoc(
            @RequestParam(name = "registrationDateStr") String regDate,
            @RequestParam(name = "fileIds") List<Integer> fileIds,
            @RequestParam(name = "executeForm") ExecuteForm executeForm,
            @RequestParam(name = "controlForm") ControlForm controlForm,
            Document document
    ) {
        User user = userService.getCurrentUserFromContext();
        Set<File> files = new HashSet<>();
        for (Integer fileId : fileIds) {
            files.add(fileService.findById(fileId));
        }
        document.setContentFiles(files);
        document.setRegistrationDate(DateParser.TryParse(regDate, Common.uzbekistanDateFormat));
        document.setCreatedAt(new Date());
        document.setCreatedById(user.getId());
        document.setExecuteForm(executeForm);
        document.setControlForm(controlForm);
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
