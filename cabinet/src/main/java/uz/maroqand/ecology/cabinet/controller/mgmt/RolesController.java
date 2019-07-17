package uz.maroqand.ecology.cabinet.controller.mgmt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.constant.user.Permissions;
import uz.maroqand.ecology.core.entity.user.Role;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.sys.TranslationService;
import uz.maroqand.ecology.core.service.user.RoleService;
import uz.maroqand.ecology.core.service.user.UserService;

import javax.validation.Valid;
import java.time.Period;
import java.util.*;

@Controller
public class RolesController {

    private final TranslationService translationService;
    private final TableHistoryService tableHistoryService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final RoleService roleService;
    private final Gson gson;

    @Autowired
    public RolesController(TranslationService translationService, TableHistoryService tableHistoryService, UserService userService, ObjectMapper objectMapper, RoleService roleService, Gson gson) {
        this.translationService = translationService;
        this.tableHistoryService = tableHistoryService;
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.roleService = roleService;
        this.gson = gson;
    }

    @RequestMapping(MgmtUrls.RolesList)
    public String getUnitList() {
        return MgmtTemplates.RolesList;
    }

    @RequestMapping(value = MgmtUrls.RolesListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public DataTablesOutput<Role> listDatatable(@Valid DataTablesInput input){
        System.out.println("RoleListAjax---");
        return roleService.getAll(input);
    }

    @RequestMapping(MgmtUrls.RolesNew)
    public String rolesNew(Model model) {
        Role role = new Role();
        model.addAttribute("role", role);
        model.addAttribute("permissions", Permissions.getPermissionsList());
        model.addAttribute("action_url", MgmtUrls.RolesCreate);
        model.addAttribute("back_url", MgmtUrls.RolesList);
        return MgmtTemplates.RolesNew;
    }

    @RequestMapping(MgmtUrls.RolesEdit)
    public String rolesEdit(
            Model model,
            @RequestParam(name = "id", required = true) Integer roleId
    ) {
        Role role = roleService.getById(roleId);
        if (role == null) {
            return "redirect:" + MgmtUrls.RolesList;
        }

        model.addAttribute("role", role);
        model.addAttribute("permissions", Permissions.getPermissionsList());
        model.addAttribute("action_url", MgmtUrls.RolesUpdate);
        model.addAttribute("back_url", MgmtUrls.RolesList);
        return MgmtTemplates.RolesNew;
    }

    @RequestMapping(MgmtUrls.RolesCreate)
    public String rolesCreate(
            Role role,
            @RequestParam(name = "checkedList") List<Integer> checkedList
    ) {
        User user = userService.getCurrentUserFromContext();
        Role  role1 = new Role();
        role1.setName(role.getName().trim());
        role1.setDescription(role.getDescription().trim());

        Set<Permissions> permissions = new HashSet<>();
        for (Integer id: checkedList) {
            permissions.add(Permissions.getPermissions(id));
        }
        role1.setPermissions(permissions);
        roleService.createRole(role1);

        String after = "";
        try {
            after = objectMapper.writeValueAsString(role1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        tableHistoryService.create(
                TableHistoryType.add,
                TableHistoryEntity.UserRole,
                role1.getId(),
                null,
                after,
                "",
                user.getId(),
                user.getUserAdditionalId());

        return "redirect:" + MgmtUrls.RolesList;
    }

    @RequestMapping(MgmtUrls.RolesUpdate)
    public String rolesUpdate(
            @RequestParam(name = "id") Integer id,
            Role role,
            @RequestParam(name = "checkedList") List<Integer> checkedList
    ) {
        User user = userService.getCurrentUserFromContext();
        Role role1 = roleService.getById(id);
        if (role1==null){
            return "redirect:" + MgmtUrls.RolesList;
        }
        String oldRole = "";
        try {
            oldRole = objectMapper.writeValueAsString(role1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        role1.setName(role.getName().trim());
        role1.setDescription(role.getDescription().trim());
        Set<Permissions> permissionsSet =new HashSet<>();
        role1.setPermissions(null);
        for (Integer index: checkedList) {
            permissionsSet.add(Permissions.getPermissions(index));
        }
        role1.setPermissions(permissionsSet);
        roleService.updateRole(role1);

        String after = "";
        try {
            after = objectMapper.writeValueAsString(role1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.UserRole,
                role1.getId(),
                oldRole,
                after,
                "",
                user.getId(),
                user.getUserAdditionalId());

        return "redirect:" + MgmtUrls.RolesList;
    }


    /*@RequestMapping(MgmtUrls.RolesView)
    public String getRolesViewPage(
            @RequestParam(name = "id") Integer roleId,
            Model model
    ){
        Role role = roleService.getById(roleId);
        if (role==null){
            return "redirect:" + MgmtUrls.RolesList;
        }
       *//* Type type = new TypeToken<List<Unit>>(){}.getType();
        List<TableHistory> tableHistoryList = tableHistoryService.getByEntityId(TableHistoryEntity.Unit,unitId);
        List<HashMap<String,Object>> beforeAndAfterList = new ArrayList<>();
        for (TableHistory tableHistory: tableHistoryList){
            HashMap<String,Object> stringObjectHashMap = new HashMap<>();
            List<Object> categoryList = gson.fromJson("["+tableHistory.getChangesSerialized()+"]",type);
            stringObjectHashMap.put("before",categoryList.get(0));
            stringObjectHashMap.put("after",categoryList.get(1));
            stringObjectHashMap.put("userName",userService.findById(tableHistory.getUserId()).getUsername());
            stringObjectHashMap.put("registeredDate", tableHistory.getRegisteredDate()!=null?uzbekistanDateFormat.format(tableHistory.getRegisteredDate()) : "");
            stringObjectHashMap.put("userAdditional", tableHistory.getUserAdditionalId()!=null?userAdditionalService.getByIdUserAdditional(tableHistory.getUserAdditionalId()):null);
            beforeAndAfterList.add(stringObjectHashMap);
        }
*//*
        model.addAttribute("role",role);
//        model.addAttribute("beforeAndAfterList",beforeAndAfterList);
        return MgmtTemplates.RolesView;
    }*/
}
