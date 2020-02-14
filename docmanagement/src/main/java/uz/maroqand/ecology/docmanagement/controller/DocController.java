package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.dto.Select2Dto;
import uz.maroqand.ecology.docmanagement.dto.Select2PaginationDto;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentLog;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentLogService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentOrganizationService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz)
 * (ru)
 */
@Controller
public class DocController {

    private final UserService userService;
    private final DocumentService documentService;
    private final DocumentOrganizationService documentOrganizationService;
    private final DocumentLogService documentLogService;

    @Autowired
    public DocController(UserService userService, DocumentService documentService, DocumentOrganizationService documentOrganizationService, DocumentLogService documentLogService) {
        this.userService = userService;
        this.documentService = documentService;
        this.documentOrganizationService = documentOrganizationService;
        this.documentLogService = documentLogService;
    }

    @RequestMapping(DocUrls.Dashboard)
    public String getDepartmentList(Model model) {

        return DocTemplates.Dashboard;
    }


    @RequestMapping(value = DocUrls.RegistrationAdditionalDocument, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getDocumentNumberSelectAjax(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page") Integer page
    ){
        search = StringUtils.trimToNull(search);
        PageRequest pageRequest = new PageRequest(page-1, 15, Sort.Direction.ASC, "id");
        Page<Document> documentPage = documentService.getRegistrationNumber(search, pageRequest);
        HashMap<String,Object> resutl = new HashMap<>();
        List<Select2Dto> select2DtoList = new ArrayList<>();
        for (Document document  : documentPage.getContent()) {
            select2DtoList.add(new Select2Dto(document.getId(), document.getRegistrationNumber() +" - "+ document.getRegistrationDate()));
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
        PageRequest pageRequest = new PageRequest(page-1, 15, Sort.Direction.ASC, "id");
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

    @RequestMapping(value = DocUrls.AddComment, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> createLogComment(
            DocumentLog documentLog
    ) {
        documentLog.setCreatedById(userService.getCurrentUserFromContext().getId());
        documentLog.setType(1);
        documentLog = documentLogService.create(documentLog);
        HashMap<String,Object> resutl = new HashMap<>();
        resutl.put("status", "success");
        resutl.put("log", documentLog);
        return resutl;
    }
}
