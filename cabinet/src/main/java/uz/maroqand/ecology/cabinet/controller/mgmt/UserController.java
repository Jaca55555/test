package uz.maroqand.ecology.cabinet.controller.mgmt;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.user.Role;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.PositionService;
import uz.maroqand.ecology.core.service.user.RoleService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 27.03.2019.
 * (uz)
 * (ru)
 */
@Controller
public class UserController {

    private final UserService userService;
    private final PositionService positionService;
    private final DepartmentService departmentService;
    private final RoleService userRoleService;
    private final TableHistoryService tableHistoryService;
    private final SoatoService soatoService;
    private final OrganizationService organizationService;
    private Gson gson;

    @Autowired
    public UserController(UserService userService, PositionService positionService, DepartmentService departmentService, TableHistoryService tableHistoryService, RoleService userRoleService, SoatoService soatoService, OrganizationService organizationService) {
        this.userService = userService;
        this.positionService = positionService;
        this.departmentService = departmentService;
        this.tableHistoryService = tableHistoryService;
        this.userRoleService = userRoleService;
        this.soatoService = soatoService;
        this.organizationService = organizationService;
        this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    @RequestMapping(MgmtUrls.UsersList)
    public String getUserListPage(Model model) {

        model.addAttribute("departmentList",departmentService.getAll());
        model.addAttribute("organizationList",organizationService.getList());
        model.addAttribute("positionList",positionService.getAll());
        model.addAttribute("roleList",userRoleService.getRoleList());
        model.addAttribute("add_url",MgmtUrls.UsersNew);
        return MgmtTemplates.UserList;
    }

    @RequestMapping(value = MgmtUrls.UsersListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getUserAjaxList(
            @RequestParam(name = "userId", defaultValue = "", required = false) Integer userId,
            @RequestParam(name = "firstname", defaultValue = "", required = false) String firstname,
            @RequestParam(name = "lastname", defaultValue = "", required = false) String lastname,
            @RequestParam(name = "middlename", defaultValue = "", required = false) String middlename,
            @RequestParam(name = "username", defaultValue = "", required = false) String username,
            @RequestParam(name = "organizationId", defaultValue = "", required = false) Integer organizationId,
            @RequestParam(name = "departmentId", defaultValue = "", required = false) Integer departmentId,
            @RequestParam(name = "positionId", defaultValue = "", required = false) Integer positionId,
            Pageable pageable
    ) {

        firstname = StringUtils.trimToNull(firstname);
        lastname = StringUtils.trimToNull(lastname);
        middlename = StringUtils.trimToNull(middlename);
        username = StringUtils.trimToNull(username);

        Page<User> usersPage = userService.findFiltered(userId,firstname,lastname,middlename,username,organizationId,departmentId,positionId,pageable);
        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", usersPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", usersPage.getTotalElements()); //Filtered elements

        List<User> users = usersPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(users.size());
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        for (User user : usersPage) {
            convenientForJSONArray.add(new Object[]{
                    user.getId(),
                    user.getFullName(),
                    user.getUsername(),
                    user.getOrganizationId()!=null? organizationService.getById(user.getOrganizationId()).getNameTranslation(locale):"",
                    user.getDepartmentId()!=null? departmentService.getById(user.getDepartmentId()).getName():"",
                    user.getPositionId()!=null? positionService.getById(user.getPositionId()).getName():"",
                    user.getPhone(),
                    user.getEnabled()
            });
        }

        result.put("data", convenientForJSONArray);
        return result;
    }

    @RequestMapping(MgmtUrls.UsersNew)
    public String getUsersNewPage(Model model) {

        model.addAttribute("user",new User());
        model.addAttribute("departmentList",departmentService.getAll());
        model.addAttribute("organizationList",organizationService.getList());
        model.addAttribute("positionList",positionService.getAll());
        model.addAttribute("roleList",userRoleService.getRoleList());
        model.addAttribute("action_url",MgmtUrls.UsersCreate);
        model.addAttribute("back_url",MgmtUrls.UsersList);
        return MgmtTemplates.UserNew;
    }

    @RequestMapping(MgmtUrls.UsersEdit)
    public String getUsersEditPage(
            @RequestParam(name = "id") Integer userId,
            Model model
    ) {
        User user = userService.findById(userId);
        if (user==null) {
            return "redirect:" + MgmtUrls.UsersList;
        }
        model.addAttribute("user",user);
        model.addAttribute("department",user.getDepartmentId()!=null?departmentService.getById(user.getDepartmentId()):null);
        model.addAttribute("departmentList",departmentService.getAll());
        model.addAttribute("organizationList",organizationService.getList());
        model.addAttribute("positionList",positionService.getAll());
        model.addAttribute("roleList",userRoleService.getRoleList());
        model.addAttribute("action_url",MgmtUrls.UsersUpdate);
        model.addAttribute("back_url",MgmtUrls.UsersList);
        return MgmtTemplates.UserNew;
    }

    @RequestMapping(value = MgmtUrls.UsersUsernameCheck, produces = "application/json",method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getUsernameCheck(
            @RequestParam(name = "username", defaultValue = "", required = false) String username,
            @RequestParam(name = "userId", required = false) Integer userId
    ) {
        System.out.println("username="+username);
        User user1 =null;
        if (userId!=null){
            user1 = userService.findById(userId);
        }
        HashMap<String, Object> result = new HashMap<>();
        Integer nameStatus = 2;
        if(username.length()>4){
            User user = userService.findByUsername(username);
            if(user==null || (user1!=null && user1.getId().equals(user.getId()))){
                nameStatus = 0;
            }else {
                nameStatus = 1;
            }
        }

        result.put("nameStatus", nameStatus);
        result.put("username", username);
        return result;
    }

    @RequestMapping(value = MgmtUrls.UsersCreate, method = RequestMethod.POST)
    public String createUserMethod(
            @RequestParam(name = "userPassword") String userPassword,
            @RequestParam(name = "userPasswordConfirmation") String userPasswordConfirmation,
            @RequestParam(name = "departmentId") Integer departmentId,
            @RequestParam(name = "roleId") Integer roleId,
            @RequestParam(name = "enebled") Integer enebled,
            User userCreate
    ) {
        User user = userService.getCurrentUserFromContext();
        if(userCreate.getUsername()==null){
            return "redirect:" + MgmtUrls.UsersList;
        }
        if(!userPassword.equals(userPasswordConfirmation)){
            return "redirect:" + MgmtUrls.UsersList;
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        User user1 = new User();
        User userCheck = userService.findByUsername(userCreate.getUsername());
        if(userCheck==null){
            user1.setFirstname(userCreate.getFirstname());
            user1.setLastname(userCreate.getLastname());
            user1.setMiddlename(userCreate.getMiddlename());
            user1.setOrganizationId(userCreate.getOrganizationId());
            user1.setDepartmentId(departmentId);
            user1.setPositionId(userCreate.getPositionId());
            user1.setRole(userRoleService.getById(roleId));
            user1.setEnabled(enebled==1?Boolean.TRUE:Boolean.FALSE);
            user1.setPhone(userCreate.getPhone());
            user1.setEmail(userCreate.getEmail());
            user1.setUsername(userCreate.getUsername());
            user1.setPassword(encoder.encode(userPassword));
            userService.createUser(user1);
            tableHistoryService.create(
                    TableHistoryType.add,
                    TableHistoryEntity.User,
                    userCreate.getId(),
                    null,
                    gson.toJson(userCreate),
                    "",
                    user.getId(),
                    user.getUserAdditionalId());
        }
        return "redirect:" + MgmtUrls.UsersList;
    }


    @RequestMapping(value = MgmtUrls.UsersUpdate, method = RequestMethod.POST)
    public String userUpdateMethod(
            @RequestParam(name = "id") Integer userId,
            @RequestParam(name = "departmentId") Integer departmentId,
            @RequestParam(name = "roleId") Integer roleId,
            @RequestParam(name = "enebled") Integer enebled,
            User userUpdate
    ) {
        User user = userService.getCurrentUserFromContext();
        if(userUpdate.getUsername()==null){
            return "redirect:" + MgmtUrls.UsersList;
        }
        User updateUser  = userService.findById(userId);
        String oldUser = gson.toJson(updateUser);
        if (updateUser==null){
            return "redirect:" + MgmtUrls.UsersList;
        }
        User userCheck = userService.findByUsername(userUpdate.getUsername());
        if(userCheck==null || userCheck.getId().equals(userId)){
            updateUser.setLastname(userUpdate.getLastname());
            updateUser.setFirstname(userUpdate.getFirstname());
            updateUser.setMiddlename(userUpdate.getMiddlename());
            updateUser.setOrganizationId(userUpdate.getOrganizationId());
            updateUser.setDepartmentId(departmentId);
            updateUser.setPositionId(userUpdate.getPositionId());
            updateUser.setRole(userRoleService.getById(roleId));
            updateUser.setEnabled(enebled==1?Boolean.TRUE:Boolean.FALSE);
            updateUser.setPhone(userUpdate.getPhone());
            updateUser.setEmail(userUpdate.getEmail());
            updateUser.setUsername(userUpdate.getUsername());
            userService.updateUser(updateUser);
            tableHistoryService.create(
                    TableHistoryType.edit,
                    TableHistoryEntity.User,
                    updateUser.getId(),
                    oldUser,
                    gson.toJson(updateUser),
                    "",
                    user.getId(),
                    user.getUserAdditionalId());
        }
        return "redirect:" + MgmtUrls.UsersList;
    }

    @RequestMapping(value = MgmtUrls.UsersEditEnebled)
    @ResponseBody
    public String userDelete(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "status") boolean status
    ){
        User user = userService.getCurrentUserFromContext();
        Integer result=1;
        User editedUser = userService.findById(id);
        String oldUser = gson.toJson(editedUser);
        if (editedUser == null){
            result=-1;
            return result.toString();
        }
        editedUser.setEnabled(status);
        userService.updateUser(editedUser);
        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.User,
                editedUser.getId(),
                oldUser,
                gson.toJson(editedUser),
                "",
                user.getId(),
                user.getUserAdditionalId());
        return result.toString();
    }

    /*@RequestMapping(value = MgmtUrls.UserDelete)
    @ResponseBody
    public String userDelete(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "message") String message
    ){
        User user = userService.getCurrentUserFromContext();
        Integer status=1;
        User deletedUser = userService.findById(id);
        String oldUser = gson.toJson(deletedUser);
        if (deletedUser == null){
            status=-1;
            return status.toString();
        }
        deletedUser.setDeleted(true);
        deletedUser.setMessage(message);
        deletedUser.setEnabled(Boolean.FALSE);
        userService.updateUser(deletedUser);
        tableHistoryService.create(
                TableHistoryType.delete,
                TableHistoryEntity.User,
                deletedUser.getId(),
                oldUser,
                gson.toJson(deletedUser),
                "",
                user.getId(),
                user.getUserAdditionalId());
        return status.toString();
    }*/


}
