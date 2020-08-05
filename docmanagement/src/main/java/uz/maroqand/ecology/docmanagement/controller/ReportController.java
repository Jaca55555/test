package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.PositionService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskSubRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;
import java.util.*;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz) Kiruvchi xatlar
 */
@Controller
public class ReportController {
    private final OrganizationService organizationService;
    private final UserService userService;
    private final PositionService positionService;
    private final HelperService helperService;
    private final DepartmentService departmentService;
    private final DocumentService documentService;
    private final DocumentSubService documentSubService;
    private final DocumentTaskService documentTaskService;
    private final DocumentTaskSubService documentTaskSubService;
    private final DocumentTaskSubRepository documentTaskSubRepository;
    private final DocumentLogService documentLogService;
    private final DocumentOrganizationService documentOrganizationService;
    private final DocumentDescriptionService documentDescriptionService;
    private final DocumentTaskContentService documentTaskContentService;
    private final JournalService journalService;
    public ReportController(
            DocumentTaskSubRepository documentTaskSubRepository,
            DepartmentService departmentService,
            OrganizationService organizationService,
            DocumentTaskContentService documentTaskContentService,
            UserService userService,
            PositionService positionService,
            HelperService helperService,
            DocumentService documentService,
            DocumentSubService documentSubService,
            DocumentTaskService documentTaskService,
            DocumentTaskSubService documentTaskSubService,
            DocumentLogService documentLogService,
            JournalService journalService,
            DocumentOrganizationService documentOrganizationService, DocumentDescriptionService documentDescriptionService) {
        this.userService = userService;
        this.journalService=journalService;
        this.positionService = positionService;
        this.helperService = helperService;
        this.documentService = documentService;
        this.departmentService=departmentService;
        this.documentSubService = documentSubService;
        this.documentTaskService = documentTaskService;
        this.documentTaskSubRepository=documentTaskSubRepository;
        this.documentTaskSubService = documentTaskSubService;
        this.documentLogService = documentLogService;
        this.organizationService=organizationService;
        this.documentOrganizationService = documentOrganizationService;
        this.documentDescriptionService = documentDescriptionService;
        this.documentTaskContentService=documentTaskContentService;
    }

    @RequestMapping(value = DocUrls.ReportList, method = RequestMethod.GET)
    public String getIncomingListPage(Model model) {
        model.addAttribute("departments",departmentService.getAll());
        return DocTemplates.ReportList;
    }

    @RequestMapping(value = DocUrls.ReportList, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getDepartmentListAjaxt(
            @RequestParam(name = "id", required = false) Integer departmentId,
            @RequestParam(name = "dateEnd", required = false)  String dateEndStr,
            @RequestParam(name = "dateBegin", required = false)  String dateBeginStr,
            @RequestParam(name = "departmentName", required = false) String name,
            @RequestParam(name = "departmentName", required = false) String nameOz,
            @RequestParam(name = "departmentName", required = false) String nameEn,
            @RequestParam(name = "departmentName", required = false) String nameRu,
            Pageable pageable
    ) {
        name = StringUtils.trimToNull(name);
        nameOz = StringUtils.trimToNull(nameOz);
        nameEn = StringUtils.trimToNull(nameEn);
        nameRu = StringUtils.trimToNull(nameRu);
        System.out.println("allo"+dateBeginStr);
        System.out.println(dateEndStr);
        Page<Department>  departmentPage = departmentService.findFilter(departmentId, DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat), DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat), name, nameOz,nameEn,nameRu, pageable);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", departmentPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", departmentPage.getTotalElements()); //Filtered elements

        List<Department> departments = departmentPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(departments.size());

        for(Department department : departments) {
            Calendar calendar = Calendar.getInstance();
            Calendar calendar1 = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, -72);
            Date date = calendar.getTime();
            calendar1.add(Calendar.HOUR_OF_DAY,0);
            Date date1=calendar1.getTime();
            convenientForJSONArray.add(new Object[]{
                    department.getId(),
                    department.getNameTranslation(locale),
                    documentTaskSubService.countAllByStatusAndDepartmentId(1,department.getId()),
                    documentTaskSubService.countAllByStatusAndDepartmentId(2,department.getId()),
                    documentTaskSubService.countAllByDueDateAndDepartmentId(date,department.getId()),
                    documentTaskSubService.countAllByDueDate1AndDepartmentId(date1,department.getId()),
                    documentTaskSubService.countAllByStatusAndDepartmentId(6,department.getId()),
                    documentTaskSubService.countAllByStatusAndDepartmentId(1,department.getId())+
                    documentTaskSubService.countAllByStatusAndDepartmentId(2,department.getId())+
                    documentTaskSubService.countAllByDueDateAndDepartmentId(date,department.getId())+
                    documentTaskSubService.countAllByDueDate1AndDepartmentId(date1,department.getId())+
                    documentTaskSubService.countAllByStatusAndDepartmentId(6,department.getId()),
            });
        }

        result.put("data", convenientForJSONArray);
        return result; }
    @RequestMapping(value = DocUrls.ReportView, method = RequestMethod.GET)
    public String getReportListPage(Model model) {
        model.addAttribute("departments",departmentService.getAll());
        model.addAttribute("doc_type",DocumentTypeEnum.getList());
        model.addAttribute("statuses",TaskSubStatus.getTaskSubStatusList());
        model.addAttribute("journals",journalService.getAll());
        return DocTemplates.ReportView;
    }

    @RequestMapping(value = DocUrls.ReportView, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getReportListAjaxt(
            @RequestParam(name = "id", required = false) Integer departmentId,
            @RequestParam(name = "statusId", required = false) Integer status,
            @RequestParam(name = "typeId", required = false) Integer typeId,
            @RequestParam(name = "dateEnd", required = false)  String dateEndStr,
            @RequestParam(name = "dateBegin", required = false)  String dateBeginStr,
            Pageable pageable
    ) {
        int id=0;
        Set<Integer> statuses = null;
        if(status!=null){
        statuses = new LinkedHashSet<>();
        statuses.add(status);}
        if(departmentId==null){id=0;}else{id=departmentId;};

        HashMap<String, Object> result = new HashMap<>();
        Page<DocumentTaskSub> documentTaskSubs = documentTaskSubService.findFiltered(
                null,
                typeId!=null ? Collections.singletonList(typeId):null, //todo documentTypeId = 3
                null,
                null,
                null,
                DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat),
                DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,

               statuses,
                id ,
                null,
                null,
                pageable
        );
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        List<DocumentTaskSub> documentTaskSubList = documentTaskSubs.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentTaskSubList.size());
        for (DocumentTaskSub documentTaskSub : documentTaskSubList) {
            Document document = documentService.getById(documentTaskSub.getDocumentId());
            DocumentSub documentSub=documentSubService.getById(document.getId());
            JSONArray.add(new Object[]{
                    documentTaskSub.getId(),
                    document.getRegistrationNumber(),
                    document.getDocRegDate()!=null ? Common.uzbekistanDateFormat.format(document.getDocRegDate()):"",
                    documentTaskSub.getCreatedAt()!=null ? Common.uzbekistanDateFormat.format(documentTaskSub.getCreatedAt()):"",
                    documentSub.getOrganizationId()!=null ? documentOrganizationService.getById(documentSub.getOrganizationId()).getName():"",
                    document.getContent(),
                    documentTaskSub.getCreatedAt()!=null ? Common.uzbekistanDateFormat.format(documentTaskSub.getCreatedAt()):"",
                    documentLogService.findFirstByDocumentIdOrderByIdDesc(document.getId())!=null?documentLogService.findFirstByDocumentIdOrderByIdDesc(document.getId()).getContent():"",
                    documentTaskSub.getDueDate()!=null ? Common.uzbekistanDateFormat.format(documentTaskSub.getDueDate()):"",
                    document.getDocRegNumber(),
                    documentTaskSub.getReceiver().getShortName(),
                    document.getRegistrationDate()!=null ? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    documentLogService.findFirstByDocumentIdOrderByIdDesc(document.getId())!=null?Common.uzbekistanDateFormat.format(documentLogService.findFirstByDocumentIdOrderByIdDesc(document.getId()).getCreatedAt()):"",
                    documentLogService.findFirstByDocumentIdOrderByIdDesc(document.getId())!=null?documentLogService.findFirstByDocumentIdOrderByIdDesc(document.getId()).getCreatedBy().getFullName():"",
                    document.getJournal().getName()
            });
        }

        result.put("recordsTotal", documentTaskSubs.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentTaskSubs.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);

        return result;}







}
