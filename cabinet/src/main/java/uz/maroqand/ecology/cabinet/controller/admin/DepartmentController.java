package uz.maroqand.ecology.cabinet.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.admin.AdminTemplates;
import uz.maroqand.ecology.cabinet.constant.admin.AdminUrls;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.UserAdditionalService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.lang.reflect.Type;
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
    private final TableHistoryService tableHistoryService;
    private final UserAdditionalService userAdditionalService;

    @Autowired
    public DepartmentController(DepartmentService departmentService, UserService userService, HelperService helperService, OrganizationService organizationService, ObjectMapper objectMapper, TableHistoryService tableHistoryService, UserAdditionalService userAdditionalService) {
        this.departmentService = departmentService;
        this.userService = userService;
        this.helperService = helperService;
        this.organizationService = organizationService;
        this.objectMapper = objectMapper;
        this.tableHistoryService = tableHistoryService;
        this.userAdditionalService = userAdditionalService;
    }

    @RequestMapping(AdminUrls.DepartmentList)
    public String getDepartmentList(Model model) {
        model.addAttribute("organizationList",organizationService.getList());
        model.addAttribute("add_url", AdminUrls.DepartmentNew);
        return AdminTemplates.DepartmentList;
    }

    @RequestMapping(value = AdminUrls.DepartmentListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getDepartmentListAjaxt(
            @RequestParam(name = "id", required = false) Integer departmentId,
            @RequestParam(name = "organizationId", required = false) Integer organizationId,
            @RequestParam(name = "parentId", required = false) Integer parentId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "nameOz", required = false) String nameOz,
            @RequestParam(name = "nameEn", required = false) String nameEn,
            @RequestParam(name = "nameRu", required = false) String nameRu,
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
                    department.getDocIndex(),
                    department.getName(),
                    department.getNameOz(),
                    department.getNameEn(),
                    department.getNameRu(),
                    department.getOrganizationId()!=null?helperService.getOrganizationName(department.getOrganizationId(),locale):"",
                    department.getParentId()!=null?helperService.getDepartmentName(department.getParentId(),locale):""
            });
        }

        result.put("data", convenientForJSONArray);
        return result;
    }

    @RequestMapping(AdminUrls.DepartmentNew)
    public String departmentNew(Model model) {
        Department department= new Department();
        model.addAttribute("department", department);
        model.addAttribute("organizationList",organizationService.getList());
        model.addAttribute("action_url", AdminUrls.DepartmentCreate);
        model.addAttribute("back_url", AdminUrls.DepartmentList);
        return AdminTemplates.DepartmentNew;
    }

    @RequestMapping(AdminUrls.DepartmentEdit)
    public String departmentEdit(
            Model model,
            @RequestParam(name = "id") Integer departmentId
    ) {
        Department department = departmentService.getById(departmentId);
        if (department == null) {
            return "redirect:" + AdminUrls.DepartmentList;
        }

        model.addAttribute("department", department);
        model.addAttribute("organizationList",organizationService.getList());
        model.addAttribute("action_url", AdminUrls.DepartmentUpdate);
        model.addAttribute("back_url", AdminUrls.DepartmentList);
        return AdminTemplates.DepartmentNew;
    }


    @RequestMapping(AdminUrls.DepartmentCreate)
    public String departmentCreate(Department department) {

        User user = userService.getCurrentUserFromContext();
        Department department1 = new Department();

        department1.setParentId(department.getParentId());
        department1.setOrganizationId(department.getOrganizationId());
        department1.setName(department.getName());
        department1.setNameOz(department.getNameOz());
        department1.setNameEn(department.getNameEn());
        department1.setNameRu(department.getNameRu());
        department1.setDeleted(Boolean.FALSE);
        department1.setCreatedAt(new Date());
        department1.setCreatedById(user.getId());
        department1.setDocIndex(department.getDocIndex());
        department1 = departmentService.save(department1);
        String after="";
        try {
            after = objectMapper.writeValueAsString(department1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        tableHistoryService.create(
                TableHistoryType.add,
                TableHistoryEntity.Department,
                department1.getId(),
                null,
                after,
                "",
                user.getId(),
                user.getUserAdditionalId()
        );


        return "redirect:" + AdminUrls.DepartmentList;
    }

    @RequestMapping(AdminUrls.DepartmentUpdate)
    public String departmentUpdate(
            @RequestParam(name = "id") Integer departmentId,
            Department department
    ) {
        User user = userService.getCurrentUserFromContext();
        Department oldDepartment= departmentService.getById(departmentId);
        if (oldDepartment == null) {
            return "redirect:" + AdminUrls.DepartmentList;
        }

        String oldDepartmentStr="";
        try {
            oldDepartmentStr = objectMapper.writeValueAsString(oldDepartment);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        oldDepartment.setParentId(department.getParentId());
        oldDepartment.setOrganizationId(department.getOrganizationId());
        oldDepartment.setName(department.getName());
        oldDepartment.setNameOz(department.getNameOz());
        oldDepartment.setNameEn(department.getNameEn());
        oldDepartment.setNameRu(department.getNameRu());
        oldDepartment.setDocIndex(department.getDocIndex());
        oldDepartment.setUpdatedAt(new Date());
        oldDepartment.setUpdatedById(user.getId());
        oldDepartment = departmentService.save(oldDepartment);

        String after = "";


        try {
            after = objectMapper.writeValueAsString(oldDepartment);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.Department,
                oldDepartment.getId(),
                oldDepartmentStr,
                after,
                "",
                user.getId(),
                user.getUserAdditionalId()
        );

        return "redirect:" + AdminUrls.DepartmentList;
    }

    @PostMapping(value = AdminUrls.DepartmentGetByOrganization)
    @ResponseBody
    public List<Department> getList(
        @RequestParam("id")Integer organizationId
    ){

        Organization organization = organizationService.getById(organizationId);
        if (organization==null) return null;

        List<Department> departmentList = departmentService.getByOrganizationId(organization.getId());
        return departmentList;
    }

    @RequestMapping(AdminUrls.DepartmentView)
    public String getDepartmentViewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Department department = departmentService.getById(id);
        if (department==null){
            return "redirect:" + AdminUrls.DepartmentList;
        }
        Type type = new TypeToken<List<Department>>(){}.getType();
        List<HashMap<String,Object>> beforeAndAfterList = tableHistoryService.forAudit(type,TableHistoryEntity.Department,id);

        model.addAttribute("department",department);
        model.addAttribute("beforeAndAfterList",beforeAndAfterList);
        return AdminTemplates.DepartmentView;
    }
}
