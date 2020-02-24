package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagement.constant.ControlForm;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.constant.ExecuteForm;
import uz.maroqand.ecology.docmanagement.dto.Select2Dto;
import uz.maroqand.ecology.docmanagement.dto.Select2PaginationDto;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentLog;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentLogService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentOrganizationService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentViewService;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz)
 */
@Controller
public class DocController {

    private final UserService userService;
    private final FileService fileService;
    private final DocumentService documentService;
    private final DocumentLogService documentLogService;
    private final DocumentViewService documentViewService;
    private final DocumentOrganizationService documentOrganizationService;
    private final HelperService helperService;

    @Autowired
    public DocController(
            UserService userService,
            FileService fileService,
            DocumentService documentService,
            DocumentLogService documentLogService,
            DocumentViewService documentViewService,
            DocumentOrganizationService documentOrganizationService,
            HelperService helperService) {
        this.userService = userService;
        this.fileService = fileService;
        this.documentService = documentService;
        this.documentLogService = documentLogService;
        this.documentViewService = documentViewService;
        this.documentOrganizationService = documentOrganizationService;
        this.helperService = helperService;
    }

    @RequestMapping(DocUrls.Dashboard)
    public String getDepartmentList(Model model) {
        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("organizationList", documentOrganizationService.getStatusActive());
        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());

        return DocTemplates.Dashboard;
    }

    @RequestMapping(value = DocUrls.RegistrationAdditionalDocument, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getDocumentNumberSelectAjax(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page") Integer page
    ){
        search = StringUtils.trimToNull(search);
        PageRequest pageRequest = new PageRequest(page-1, 15, Sort.Direction.DESC, "id");
        Page<Document> documentPage = documentService.getRegistrationNumber(search, pageRequest);
        HashMap<String,Object> resutl = new HashMap<>();
        List<Select2Dto> select2DtoList = new ArrayList<>();
        for (Document document  : documentPage.getContent()) {
            select2DtoList.add(new Select2Dto(document.getId(), document.getRegistrationNumber() +" - "+ Common.uzbekistanDateAndTimeFormat.format(document.getRegistrationDate())));
        }

        Select2PaginationDto paginationDto = new Select2PaginationDto();
        paginationDto.setMore(true);
        paginationDto.setSize(15);
        paginationDto.setTotal(documentPage.getTotalElements());

        resutl.put("results", select2DtoList);
        resutl.put("pagination", paginationDto);
        resutl.put("total_count", documentPage.getTotalElements());
        return resutl;
    }

    @RequestMapping(value = DocUrls.RegistrationOrganization, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getOrganizationSelectAjax(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page") Integer page
    ){
        search = StringUtils.trimToNull(search);
        PageRequest pageRequest = PageRequest.of(page-1, 15, Sort.Direction.ASC, "id");
        Page<DocumentOrganization> documentOrganizationPage = documentOrganizationService.getOrganizationList(search, pageRequest);
        HashMap<String,Object> resutl = new HashMap<>();
        List<Select2Dto> select2DtoList = new ArrayList<>();
        for (DocumentOrganization documentOrganization : documentOrganizationPage.getContent()) {
            select2DtoList.add(new Select2Dto(documentOrganization.getId(), documentOrganization.getName()));
        }

        Select2PaginationDto paginationDto = new Select2PaginationDto();
        paginationDto.setMore(true);
        paginationDto.setSize(15);
        paginationDto.setTotal(documentOrganizationPage.getTotalElements());

        resutl.put("results", select2DtoList);
        resutl.put("pagination", paginationDto);
        resutl.put("total_count", documentOrganizationPage.getTotalElements());
        return resutl;
    }

    @RequestMapping(value = DocUrls.AddComment, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> createLogComment(
            @RequestParam(name = "file_ids", required = false) List<Integer> file_ids,
            DocumentLog documentLog
    ) {
        String locale = LocaleContextHolder.getLocale().getLanguage();
        User user = userService.getCurrentUserFromContext();
        documentLog.setCreatedById(user.getId());
        documentLog.setType(1);
        if (file_ids != null) {
            Set<File> files = new LinkedHashSet<>();
            for (Integer id : file_ids) {
                files.add(fileService.findById(id));
            }
            documentLog.setContentFiles(files);
        }
        documentLog = documentLogService.create(documentLog);
        documentLog.setCreatedBy(userService.findById(documentLog.getCreatedById()));
        HashMap<String,Object> resutl = new HashMap<>();
        resutl.put("status", "success");
        resutl.put("createName", user.getFullName());
        resutl.put("createPosition", user.getPositionId()!=null?helperService.getUserPositionName(user.getPositionId(),locale):"");
        resutl.put("createdAt", Common.uzbekistanDateAndTimeFormat.format(documentLog.getCreatedAt()));
        resutl.put("log", documentLog);
        return resutl;
    }
}
