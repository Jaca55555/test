package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.maroqand.ecology.ecoexpertise.constant.Templates;
import uz.maroqand.ecology.ecoexpertise.constant.Urls;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
@Controller
public class MainController {

    @RequestMapping("/")
    public String getPage() {
        return "redirect: login";
    }

    @RequestMapping("/login")
    public String getLoginPage() {
        System.out.println("--login");
        return "login";
    }

    @RequestMapping("/dashboard")
    public String getDashboardPage() {
        System.out.println("--dashboard");
        return "dashboard";
    }

    @RequestMapping(Urls.ErrorInternalServerError)
    public String getError500Page() {
        return Templates.ErrorInternalServerError;
    }

    @RequestMapping(Urls.ErrorNotFound)
    public String getError404Page() {
        return Templates.ErrorNotFound;
    }

    @RequestMapping(Urls.ErrorForbidden)
    public String getError403Page() {
        return Templates.ErrorForbidden;
    }

}
