package uz.maroqand.ecology.cabinet.controller.mgmt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class DepartmentController {

    private final DepartmentService departmentService;
    private final UserService userService;
    private final HelperService helperService;
    private final OrganizationService organizationService;
    private final ObjectMapper objectMapper;
    @Autowired
    public DepartmentController(DepartmentService departmentService, UserService userService, HelperService helperService, OrganizationService organizationService, ObjectMapper objectMapper) {
        this.departmentService = departmentService;
        this.userService = userService;
        this.helperService = helperService;
        this.organizationService = organizationService;
        this.objectMapper = objectMapper;
    }

    @RequestMapping(MgmtUrls.DepartmentList)
    public String getDepartmentList(Model model) {
        model.addAttribute("organizationList",organizationService.getList());
        model.addAttribute("add_url",MgmtUrls.DepartmentNew);
        return MgmtTemplates.DepartmentList;
    }

    @RequestMapping(value = MgmtUrls.DepartmentListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getDepartmentListAjaxt(
            @RequestParam(name = "id", required = false) Integer departmentId,
            @RequestParam(name = "organizationId", required = false) Integer organizationId,
            @RequestParam(name = "parentId", required = false) Integer parentId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "nameRu", required = false) String nameRu,
            Pageable pageable
    ) {
        name = StringUtils.trimToNull(name);
        nameRu = StringUtils.trimToNull(nameRu);

        Page<Department>  departmentPage = departmentService.findFiltered(departmentId, organizationId, parentId, name, nameRu, pageable);
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
                    department.getNameRu(),
                    department.getOrganizationId()!=null?helperService.getOrganizationName(department.getOrganizationId(),locale):"",
                    department.getParentId()!=null?helperService.getDepartmentName(department.getParentId(),locale):""
            });
        }

        result.put("data", convenientForJSONArray);
        return result;
    }

    @RequestMapping(MgmtUrls.DepartmentNew)
    public String departmentNew(Model model) {
        Department department= new Department();
        model.addAttribute("department", department);
        model.addAttribute("organizationList",organizationService.getList());
        model.addAttribute("action_url", MgmtUrls.DepartmentCreate);
        model.addAttribute("back_url", MgmtUrls.DepartmentList);
        return MgmtTemplates.DepartmentNew;
    }

    @RequestMapping(MgmtUrls.DepartmentEdit)
    public String departmentEdit(
            Model model,
            @RequestParam(name = "id") Integer departmentId
    ) {
        Department department = departmentService.getById(departmentId);
        if (department == null) {
            return "redirect:" + MgmtUrls.DepartmentList;
        }

        model.addAttribute("department", department);
        model.addAttribute("organizationList",organizationService.getList());
        model.addAttribute("action_url", MgmtUrls.DepartmentUpdate);
        model.addAttribute("back_url", MgmtUrls.DepartmentList);
        return MgmtTemplates.DepartmentNew;
    }


    @RequestMapping(MgmtUrls.DepartmentCreate)
    public String departmentCreate(Department department) {

        User user = userService.getCurrentUserFromContext();
        Department department1 = new Department();

        department1.setParentId(department.getParentId());
        department1.setOrganizationId(department.getOrganizationId());
        department1.setName(department.getName());
        department1.setNameRu(department.getNameRu());
        department1.setDeleted(Boolean.FALSE);
        department1.setCreatedAt(new Date());
        department1.setCreatedById(user.getId());
        departmentService.save(department1);

        return "redirect:" + MgmtUrls.DepartmentList;
    }

    @RequestMapping(MgmtUrls.DepartmentUpdate)
    public String departmentUpdate(
            @RequestParam(name = "id") Integer departmentId,
            Department department
    ) {
        User user = userService.getCurrentUserFromContext();
        Department oldDepartment= departmentService.getById(departmentId);
        if (oldDepartment == null) {
            return "redirect:" + MgmtUrls.DepartmentList;
        }

        oldDepartment.setParentId(department.getParentId());
        oldDepartment.setOrganizationId(department.getOrganizationId());
        oldDepartment.setName(department.getName());
        oldDepartment.setNameRu(department.getNameRu());
        oldDepartment.setUpdatedAt(new Date());
        oldDepartment.setUpdatedById(user.getId());
        departmentService.save(oldDepartment);

        return "redirect:" + MgmtUrls.DepartmentList;
    }

    @RequestMapping(value = MgmtUrls.DepartmentGetByOrganization)
    @ResponseBody
    public List<Department> getList(
            @RequestParam(name = "id") Integer organizationId
    ){
        Organization organization = organizationService.getById(organizationId);
        if (organization==null) return null;

        List<Department> departmentList = departmentService.getListByOrganizationId(organization.getId());
        return departmentList;
    }
}
