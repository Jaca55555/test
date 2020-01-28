package uz.maroqand.ecology.cabinet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.user.UserService;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
@Controller
public class MainController {
    private final UserService userService;
    private final RegApplicationService regApplicationService;

    @Autowired
    public MainController(UserService userService, RegApplicationService regApplicationService) {
        this.userService = userService;
        this.regApplicationService = regApplicationService;
    }

    @RequestMapping("/")
    public String getPage() {
        return "redirect: /login";
    }

    @RequestMapping("/login")
    public String getLoginPage() {
        System.out.println("--login");
        return "login";
    }

    @RequestMapping("/dashboard")
    public String getDashboardPage(Model model) {
        User user = userService.getCurrentUserFromContext();
        LogType logType = userService.getUserLogType(user);

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                new FilterDto(),
                user.getOrganizationId(),
                logType,
                null,
                null,
                null,//todo shart kerak
                new PageRequest(0,2)
        );

        model.addAttribute("newElements", regApplicationPage.getTotalElements());
        model.addAttribute("logType", logType);
        return "dashboard";
    }

    @RequestMapping("/map")
    public String getMap(){
        return "mapTest";
    }

    /*@RequestMapping(SysUrls.ErrorInternalServerError)
    @RequestMapping(SysUrls.ErrorInternalServerError)
    public String getError500Page() {
        return SysTemplates.ErrorInternalServerError;
    }

    @RequestMapping(SysUrls.ErrorNotFound)
    public String getError404Page() {
        return SysTemplates.ErrorNotFound;
    }

    @RequestMapping(SysUrls.ErrorForbidden)
    public String getError403Page() {
        return SysTemplates.ErrorForbidden;
    }*/

}
