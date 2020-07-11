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
            @RequestParam(name = "organizationId", required = false) Integer organizationId,
            @RequestParam(name = "parentId", required = false) Integer parentId,
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

        Page<Department>  departmentPage = departmentService.findFiltered(departmentId, organizationId, parentId, name, nameOz,nameEn,nameRu, pageable);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", departmentPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", departmentPage.getTotalElements()); //Filtered elements

        List<Department> departments = departmentPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(departments.size());

        for(Department department : departments) {

            convenientForJSONArray.add(new Object[]{
                    department.getId(),
                    department.getName(),
                    documentTaskSubService.countAllByStatusAndDepartmentId(1,department.getId()),
                    documentTaskSubService.countAllByStatusAndDepartmentId(2,department.getId()),
                    documentTaskSubService.countAllByStatusAndDate(documentTaskSubRepository.findByDepartmentIdAndDeletedFalse(department.getId()).getDueDate(), department.getId()),
                    documentTaskSubService.countAllByStatusAndDate(documentTaskSubRepository.findByDepartmentIdAndDeletedFalse(department.getId()).getDueDate(), department.getId()),
                    documentTaskSubService.countAllByStatusAndDepartmentId(6,department.getId()),
                    documentTaskSubService.countAllByStatusAndDepartmentId(1,department.getId())+
                    documentTaskSubService.countAllByStatusAndDepartmentId(2,department.getId())+
                            documentTaskSubService.countAllByStatusAndDate(documentTaskSubRepository.findByDepartmentIdAndDeletedFalse(department.getId()).getDueDate(), department.getId())+
                            documentTaskSubService.countAllByStatusAndDate(documentTaskSubRepository.findByDepartmentIdAndDeletedFalse(department.getId()).getDueDate(), department.getId())+
                    documentTaskSubService.countAllByStatusAndDepartmentId(6,department.getId()),
            });
        }

        result.put("data", convenientForJSONArray);
        return result; }







}
