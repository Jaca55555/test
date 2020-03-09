package uz.maroqand.ecology.cabinet.controller.mgmt;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class ExpertiseDepartmentController {

    private final DepartmentService departmentService;
    private final UserService userService;
    private final HelperService helperService;
    private final ObjectMapper objectMapper;
    private final TableHistoryService tableHistoryService;

    @Autowired
    public ExpertiseDepartmentController(DepartmentService departmentService, UserService userService, HelperService helperService, ObjectMapper objectMapper, TableHistoryService tableHistoryService) {
        this.departmentService = departmentService;
        this.userService = userService;
        this.helperService = helperService;
        this.objectMapper = objectMapper;
        this.tableHistoryService = tableHistoryService;
    }

    @RequestMapping(MgmtUrls.DepartmentList)
    public String getDepartmentList(Model model) {
        model.addAttribute("add_url",MgmtUrls.DepartmentNew);
        return MgmtTemplates.DepartmentList;
    }

    @RequestMapping(value = MgmtUrls.DepartmentListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getDepartmentListAjaxt(
            @RequestParam(name = "id", required = false) Integer departmentId,
            @RequestParam(name = "parentId", required = false) Integer parentId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "nameOz", required = false) String nameOz,
            @RequestParam(name = "nameEn", required = false) String nameEn,
            @RequestParam(name = "nameRu", required = false) String nameRu,
            Pageable pageable
    ) {
        User currentUser = userService.getCurrentUserFromContext();
        name = StringUtils.trimToNull(name);
        nameOz = StringUtils.trimToNull(nameOz);
        nameEn = StringUtils.trimToNull(nameEn);
        nameRu = StringUtils.trimToNull(nameRu);

        Page<Department>  departmentPage = departmentService.findFiltered(departmentId, currentUser.getOrganizationId(), parentId, name, nameOz,nameEn,nameRu, pageable);
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

    @RequestMapping(MgmtUrls.DepartmentNew)
    public String departmentNew(Model model) {
        User currentUser = userService.getCurrentUserFromContext();
        Department department= new Department();
        model.addAttribute("department", department);
        model.addAttribute("departmentList", departmentService.getByOrganizationId(currentUser.getOrganizationId()));
        model.addAttribute("action_url", MgmtUrls.DepartmentCreate);
        model.addAttribute("back_url", MgmtUrls.DepartmentList);
        return MgmtTemplates.DepartmentNew;
    }

    @RequestMapping(MgmtUrls.DepartmentEdit)
    public String departmentEdit(
            Model model,
            @RequestParam(name = "id") Integer departmentId
    ) {
        User currentUser = userService.getCurrentUserFromContext();
        Department department = departmentService.getById(departmentId);
        if (department == null) {
            return "redirect:" + MgmtUrls.DepartmentList;
        }

        model.addAttribute("department", department);
        model.addAttribute("action_url", MgmtUrls.DepartmentUpdate);
        model.addAttribute("back_url", MgmtUrls.DepartmentList);
        return MgmtTemplates.DepartmentNew;
    }

    @RequestMapping(MgmtUrls.DepartmentCreate)
    public String departmentCreate(Department department) {
        User currentUser = userService.getCurrentUserFromContext();

        Department department1 = new Department();
        department1.setParentId(department.getParentId());
        department1.setOrganizationId(currentUser.getOrganizationId());
        department1.setName(department.getName());
        department1.setNameOz(department.getNameOz());
        department1.setNameEn(department.getNameEn());
        department1.setNameRu(department.getNameRu());
        department1.setDeleted(Boolean.FALSE);
        department1.setCreatedAt(new Date());
        department1.setCreatedById(currentUser.getId());
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
                currentUser.getId(),
                currentUser.getUserAdditionalId()
        );

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

        String oldDepartmentStr="";
        try {
            oldDepartmentStr = objectMapper.writeValueAsString(oldDepartment);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        oldDepartment.setParentId(department.getParentId());
        oldDepartment.setName(department.getName());
        oldDepartment.setNameOz(department.getNameOz());
        oldDepartment.setNameEn(department.getNameEn());
        oldDepartment.setNameRu(department.getNameRu());
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

        return "redirect:" + MgmtUrls.DepartmentList;
    }

    @RequestMapping(MgmtUrls.DepartmentView)
    public String getDepartmentViewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Department department = departmentService.getById(id);
        if (department==null){
            return "redirect:" + MgmtUrls.DepartmentList;
        }
        Type type = new TypeToken<List<Department>>(){}.getType();
        List<HashMap<String,Object>> beforeAndAfterList = tableHistoryService.forAudit(type,TableHistoryEntity.Department,id);

        model.addAttribute("department",department);
        model.addAttribute("beforeAndAfterList",beforeAndAfterList);
        return MgmtTemplates.DepartmentView;
    }

}