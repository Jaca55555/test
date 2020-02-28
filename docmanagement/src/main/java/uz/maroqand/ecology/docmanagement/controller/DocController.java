package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.dto.IncomingRegFilter;
import uz.maroqand.ecology.docmanagement.dto.Select2Dto;
import uz.maroqand.ecology.docmanagement.dto.Select2PaginationDto;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

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
    private final DocumentTaskService taskService;
    private final DocumentOrganizationService documentOrganizationService;
    private final HelperService helperService;
    private final DocumentSubService documentSubService;

    @Autowired
    public DocController(
            UserService userService,
            FileService fileService,
            DocumentService documentService,
            DocumentLogService documentLogService,
            DocumentViewService documentViewService,
            DocumentTaskService taskService,
            DocumentOrganizationService documentOrganizationService,
            HelperService helperService,
            DocumentSubService documentSubService) {
        this.userService = userService;
        this.fileService = fileService;
        this.documentService = documentService;
        this.documentLogService = documentLogService;
        this.documentViewService = documentViewService;
        this.taskService = taskService;
        this.documentOrganizationService = documentOrganizationService;
        this.helperService = helperService;
        this.documentSubService = documentSubService;
    }

    @RequestMapping(DocUrls.Dashboard)
    public String getDepartmentList(Model model) {
        model.addAttribute("documentViewList", documentViewService.getStatusActive());
        model.addAttribute("organizationList", documentOrganizationService.getStatusActive());
        model.addAttribute("executeForms", ControlForm.getControlFormList());
        model.addAttribute("controlForms", ExecuteForm.getExecuteFormList());

        model.addAttribute("incomingCount", documentService.getCountersByType(DocumentTypeEnum.IncomingDocuments.getId()));
        model.addAttribute("innerCount", documentService.getCountersByType(DocumentTypeEnum.InnerDocuments.getId()));
        model.addAttribute("outgoingCount", documentService.getCountersByType(DocumentTypeEnum.OutgoingDocuments.getId()));
        model.addAttribute("appealCount", documentService.getCountersByType(DocumentTypeEnum.AppealDocuments.getId()));

        return DocTemplates.Dashboard;
    }

    @RequestMapping(value = DocUrls.DashboardDocList, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getIncomingRegistrationList(
            IncomingRegFilter incomingRegFilter,
            Pageable pageable
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> result = new HashMap<>();
        String locale = LocaleContextHolder.getLocale().getLanguage();
        //todo documentTypeId=1
        Page<DocumentTask> documentTaskPage = taskService.findFiltered(user.getOrganizationId(), 1, incomingRegFilter, null, null, null, null, null, null, pageable);
        List<DocumentTask> documentTaskList = documentTaskPage.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentTaskList.size());
        for (DocumentTask documentTask : documentTaskList) {
            Document document = documentService.getById(documentTask.getDocumentId());
            JSONArray.add(new Object[]{
                    documentTask.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    document.getContent(),
                    documentTask.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(documentTask.getCreatedAt()):"",
                    documentTask.getDueDate()!=null? Common.uzbekistanDateFormat.format(documentTask.getDueDate()):"",
                    helperService.getTranslation(documentTask.getStatusName(documentTask.getStatus()),locale),
                    documentTask.getContent(),
            });
        }

        result.put("recordsTotal", documentTaskPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentTaskPage.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);
        return result;
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

    @RequestMapping(value = DocUrls.DocumentOpenView)
    public String getViewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Document document = documentService.getById(id);
        if (document==null){
            return "redirect:" + DocUrls.Dashboard;
        }

        Integer viewTagId= document.getDocumentType().getType().getId();
        String viewTag = "";
        switch (viewTagId){
            case 1: viewTag = "doc_incoming"; break;
            case 2: viewTag = "doc_outgoing";break;
            case 3: viewTag = "doc_inner";break;
            case 4: viewTag = "doc_appeal";break;
        }

        model.addAttribute("view_tag" ,viewTag);
        model.addAttribute("document" ,document);
        model.addAttribute("documentSub", documentSubService.getByDocumentIdForIncoming(document.getId()));


        return DocTemplates.DocumentOpenView;
    }
}
