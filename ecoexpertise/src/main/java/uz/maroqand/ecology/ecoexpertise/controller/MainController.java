package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.integration.common.EcoGovService;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysTemplates;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysUrls;

import org.springframework.data.domain.Pageable;


/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
@Controller
public class MainController {

    private final String apiKey = "800D00EAA115F8BB0D5083ECBDEC6ABDF9124235BDD6C2CD558CD686B583743524FB947AF54B11A6A98D3A303CF14CF6D86BD71C975E95B45E34D07F327674CA";
    private final EcoGovService ecoGovService;
    private final RegApplicationService regApplicationService;

    @Autowired
    public MainController(EcoGovService ecoGovService, RegApplicationService regApplicationService) {
        this.ecoGovService = ecoGovService;
        this.regApplicationService = regApplicationService;
    }

    @RequestMapping("/")
    public String getPage(Model model, Pageable pageable) {
        String element = ecoGovService.getEcoGovApi();
        try {
            element = new String(element.getBytes("utf8"));
        }catch (Exception e){}

        Long total = regApplicationService.findFiltered(new FilterDto(), null, null, null, null, null,pageable).getTotalElements();
        Long done = regApplicationService.findFiltered(new FilterDto(), null, LogType.AgreementComplete, null, null, null,pageable).getTotalElements();

        model.addAttribute("element", element);
        model.addAttribute("total", total);
        model.addAttribute("done", done);
        return "index";
    }

    @RequestMapping("/login")
    public String getLoginPage(Model model) {

        model.addAttribute("api_key", apiKey);
        model.addAttribute("api_key_domain", "eco-service.uz");
        return "login";
    }

    @RequestMapping("/dashboard")
    public String getDashboardPage() {
        System.out.println("--dashboard");
        return "dashboard";
    }

    @RequestMapping("/news")
    public String getNewsPage(
            Model model,
            @RequestParam(name = "title") String title,
            @RequestParam(name = "create") String create,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "image") String image
    ) {
//        System.out.println(elem);
        model.addAttribute("create",create);
        model.addAttribute("title",title);
        model.addAttribute("img_source",image);
        model.addAttribute("newsText",content);
        System.out.println("--news");
        return "news";
    }
    
    @RequestMapping("/logout")
    public String logout(){
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }

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
    }

}
