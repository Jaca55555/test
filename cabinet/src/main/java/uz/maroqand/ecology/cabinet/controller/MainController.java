package uz.maroqand.ecology.cabinet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.maroqand.ecology.cabinet.constant.sys.SysUrls;
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
        return "redirect:/login";
    }

    @RequestMapping("/login")
    public String getLoginPage() {
        System.out.println("--login");
        return "login";
    }

    @RequestMapping("/dashboard")
    public String getDashboardPage(@RequestParam (name = "lang",required = false) String lang) {
        User user = userService.getCurrentUserFromContext();
        if (lang!=null && !lang.isEmpty()) return "dashboard";
        System.out.println(user.getLang());
        if (user.getLang()==null || user.getLang().isEmpty()){
            user.setLang("oz");
            userService.updateUser(user);
            return "redirect:dashboard?lang=oz";

        }

        return "redirect:dashboard?lang="+user.getLang();
    }

    @RequestMapping("/expertise/dashboard")
    public String getExpertiseDashboardPage(Model model) {
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
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        if (user.getLang() == null || user.getLang().isEmpty()){
            user.setLang(locale);
            userService.update(user);
        }else if (!user.getLang().equals(locale)){
            return "redirect:" + SysUrls.SelectLang + "?lang="+ user.getLang() + "&currentUrl=/expertise/dashboard";
        }

        return "expertise_dashboard";
    }

    @RequestMapping("/admin/dashboard")
    public String getAdminDashboardPage() {
        return "admin/dashboard";
    }

    @RequestMapping("/map")
    public String getMap(){
        return "mapTest";
    }

    @RequestMapping(value = SysUrls.SelectLang)
    public String selectLang(
            @RequestParam(name = "lang") String lang,
            @RequestParam(name = "currentUrl") String currentUrl
    ){
        lang = lang.substring(0,2);
        User user = userService.getCurrentUserFromContext();
        user.setLang(lang);
        userService.update(user);
        return "redirect:" + currentUrl;
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
