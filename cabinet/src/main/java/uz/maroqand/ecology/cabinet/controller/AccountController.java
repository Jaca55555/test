package uz.maroqand.ecology.cabinet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.user.UserService;

@Controller
public class AccountController {

    private final UserService userService;
    private final TableHistoryService tableHistoryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AccountController(
            UserService userService,
            TableHistoryService tableHistoryService,
            ObjectMapper objectMapper) {
        this.userService = userService;
        this.tableHistoryService = tableHistoryService;
        this.objectMapper = objectMapper;
    }

    @RequestMapping("/profile")
    public String getProfilePage(Model model){
        User user = userService.getCurrentUserFromContext();

        model.addAttribute("user",user);
        model.addAttribute("action_url","/profile/psw_update");
        return "profile";
    }

    @RequestMapping(value = "/profile/psw_update",method = RequestMethod.POST)
    public String updateUserPsw(
            @RequestParam(name = "userPassword")String userPassword,
            @RequestParam(name = "userPasswordConfirmation")String userPasswordConfirmation
    ){
        User user = userService.getCurrentUserFromContext();
        if (!userPassword.equals(userPasswordConfirmation)){
            return "redirect:/profile";
        }

        String oldUserString = "";
        try {
            oldUserString = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        user.setPassword(new BCryptPasswordEncoder().encode(userPassword));
        userService.updateUser(user);
        String after ="";
        try {
            after = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

       /* String oldUserString = gson.toJson(user);
        user.setPassword(new BCryptPasswordEncoder().encode(userPassword));
        userService.updateUser(user);*/

        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.User,
                user.getId(),
                oldUserString,
                after,
                "Users password updated successfully!!!",
                user.getId(),
                user.getUserAdditionalId());
        return "redirect:/profile";
    }
}
