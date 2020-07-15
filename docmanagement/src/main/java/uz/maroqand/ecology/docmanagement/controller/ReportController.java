package uz.maroqand.ecology.docmanagement.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.PositionService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.entity.*;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskSubRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.*;

import javax.servlet.http.HttpServletRequest;
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
            DocumentOrganizationService documentOrganizationService, DocumentDescriptionService documentDescriptionService) {
        this.userService = userService;
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
    public String getIncomingListPage() {

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
                    department.getName(),
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

        return DocTemplates.ReportView;
    }

    @RequestMapping(value = DocUrls.ReportView, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getReportListAjaxt(
            @RequestParam(name = "id", required = false) Integer departmentId,
            @RequestParam(name = "statusId", required = false) Set<Integer> status,
            @RequestParam(name = "typeId", required = false) Integer typeId,
            @RequestParam(name = "dateEnd", required = false)  String dateEndStr,
            @RequestParam(name = "dateBegin", required = false)  String dateBeginStr,
            Pageable pageable
    ) {
        HashMap<String, Object> result = new HashMap<>();
        Page<DocumentTaskSub> documentTaskSubs = documentTaskSubService.findFiltered(
                null,
                null, //todo documentTypeId = 3
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
                typeId,

               null,
                departmentId,
                null,
                null,
                pageable
        );
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        List<DocumentTaskSub> documentTaskSubList = documentTaskSubs.getContent();
        List<Object[]> JSONArray = new ArrayList<>(documentTaskSubList.size());
        for (DocumentTaskSub documentTaskSub : documentTaskSubList) {
            Document document = documentService.getById(documentTaskSub.getDocumentId());
            JSONArray.add(new Object[]{
                    documentTaskSub.getId(),
                    document.getRegistrationNumber(),
                    document.getRegistrationDate()!=null ? Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"",
                    documentTaskSub.getRegistrationDate()!=null ? Common.uzbekistanDateFormat.format(documentTaskSub.getRegistrationDate()):"",
                    document.getOrganizationId()!=null ? organizationService.getById(document.getOrganizationId()).getName():"",
                    document.getContent(),
                    documentTaskSub.getCreatedAt()!=null ? Common.uzbekistanDateFormat.format(documentTaskSub.getCreatedAt()):"",
                    document.getCreatedAt()!=null ? Common.uzbekistanDateFormat.format(document.getCreatedAt()):"",
                    documentTaskSub.getDueDate()!=null ? Common.uzbekistanDateFormat.format(documentTaskSub.getDueDate()):"",
            });
        }

        result.put("recordsTotal", documentTaskSubs.getTotalElements()); //Total elements
        result.put("recordsFiltered", documentTaskSubs.getTotalElements()); //Filtered elements
        result.put("data", JSONArray);

        return result;}







}
