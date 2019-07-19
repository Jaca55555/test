package uz.maroqand.ecology.ecoexpertise.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.UserService;

@Controller
public class AccountController {

    private final UserService userService;
    private final TableHistoryService tableHistoryService;
    private final OrganizationService organizationService;
    private final DepartmentService departmentService;
    private final Gson gson;
    @Autowired
    public AccountController(UserService userService, TableHistoryService tableHistoryService, OrganizationService organizationService, DepartmentService departmentService, Gson gson) {
        this.userService = userService;
        this.tableHistoryService = tableHistoryService;
        this.organizationService = organizationService;
        this.departmentService = departmentService;
        this.gson = gson;
    }

    @RequestMapping("/profile")
    public String getProfilePage(Model model){
        User user = userService.getCurrentUserFromContext();
        if (user.getOrganizationId() != null && user.getDepartmentId() != null){
            Organization organization = organizationService.getById(user.getOrganizationId());
            Department department = departmentService.getById(user.getDepartmentId());
            model.addAttribute("organization",organization!=null?organization:"");
            model.addAttribute("department",department!=null?department:"");
        }
        model.addAttribute("user",user);
        model.addAttribute("action_url","/profile/psw_update");
        return "/profile";
    }

    @RequestMapping(value = "/profile/psw_update",method = RequestMethod.POST)
    public String updateUserPsw(
            @RequestParam(name = "userPassword")String userPassword,
            @RequestParam(name = "userPasswordConfirmation")String userPasswordConfirmation,
            User user
    ){
        if (!userPassword.equals(userPasswordConfirmation)){
            return "redirect:/profile";
        }
        User oldUser = userService.findById(user.getId());
        String oldUserString = gson.toJson(oldUser);
        oldUser.setPassword(new BCryptPasswordEncoder().encode(userPassword));
        userService.updateUser(oldUser);
        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.User,
                oldUser.getId(),
                oldUserString,
                gson.toJson(oldUser),
                "Users password updated successfully!!!",
                oldUser.getId(),
                oldUser.getUserAdditionalId());
        return "redirect:/profile";
    }
}
