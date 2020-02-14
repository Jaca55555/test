package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;

import java.util.HashMap;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 13.02.2020
 */

@Controller
public class UserManagementController
{
    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = DocUrls.Chiefs)
    public String getChiefList(Model model) {

        model.addAttribute("isChief", true);
        model.addAttribute("isController", false);
        model.addAttribute("users", userService.getEmployeesForDocManage("chief"));
        return DocTemplates.Controllers;
    }

    @GetMapping(value = DocUrls.Controllers)
    public String getControllerList(Model model) {

        model.addAttribute("isChief", false);
        model.addAttribute("isController", true);
        model.addAttribute("users", userService.getEmployeesForDocManage("controller"));
        return DocTemplates.Controllers;
    }

    @PostMapping(value = DocUrls.Change)
    @ResponseBody
    public HashMap<String, Object> changeChief(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "action")String action,
            @RequestParam(name = "status")Boolean status
    ) {
        HashMap<String, Object> response = new HashMap<>();
        User user = userService.findById(id);
        if (user == null) {
            response.put("status", "error");
            return response;
        }
        if (action.equals("chief"))
            user.setIsExecuteChief(status);
        if (action.equals("controller"))
            user.setIsExecuteController(status);
        userService.updateUser(user);
        response.put("status", "success");
        return response;
    }

}
