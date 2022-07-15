package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.sys.Option;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.integration.common.EcoGovService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.OptionService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysTemplates;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysUrls;

import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


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
    private final UserService userService;
    private final FileService fileService;
    private final OptionService optionService;

    @Autowired
    public MainController(EcoGovService ecoGovService, RegApplicationService regApplicationService, UserService userService, FileService fileService, OptionService optionService) {
        this.ecoGovService = ecoGovService;
        this.regApplicationService = regApplicationService;
        this.userService = userService;
        this.fileService = fileService;
        this.optionService = optionService;
    }

    @RequestMapping("/")
    public String getPage(Model model, Pageable pageable) {
        FilterDto filterDto = new FilterDto();
        Set<RegApplicationStatus> statusForReg = new HashSet<>();
        statusForReg.add(RegApplicationStatus.CheckSent);
        statusForReg.add(RegApplicationStatus.CheckConfirmed);
        statusForReg.add(RegApplicationStatus.CheckNotConfirmed);
        statusForReg.add(RegApplicationStatus.Process);
        statusForReg.add(RegApplicationStatus.Modification);
        statusForReg.add(RegApplicationStatus.Approved);
        statusForReg.add(RegApplicationStatus.NotConfirmed);
        statusForReg.add(RegApplicationStatus.Canceled);
        filterDto.setStatusForReg(statusForReg);
        System.out.println("filterDto="+filterDto);
        Long total = regApplicationService.findFiltered(filterDto, null, null, null, null, null,pageable).getTotalElements();
        Set<RegApplicationStatus> statusForReg1 = new HashSet<>();
        statusForReg1.add(RegApplicationStatus.CheckConfirmed);
        statusForReg1.add(RegApplicationStatus.CheckNotConfirmed);
        statusForReg1.add(RegApplicationStatus.Modification);
        statusForReg1.add(RegApplicationStatus.Approved);
        statusForReg1.add(RegApplicationStatus.NotConfirmed);
        statusForReg1.add(RegApplicationStatus.Canceled);
        filterDto.setStatusForReg(statusForReg1);
        System.out.println("filterDto="+filterDto);
        Long done = regApplicationService.findFiltered(filterDto, null, null, null, null, null,pageable).getTotalElements();

//        model.addAttribute("element", element);
        model.addAttribute("total", total);
        model.addAttribute("done", done);
        return "index";
    }

    //fileDownload
    @RequestMapping("/get_modal_file")
    public ResponseEntity<Resource> get_modal_file(
    ){
        Option option = optionService.getOption("eco_notification");

        Integer optionVal;
        if(option!=null){
            optionVal = Integer.parseInt(option.getValue());
        }else {
            optionVal = null;
        }
        if (optionVal==null) return null;

        File file = fileService.findById(optionVal);
        if (file == null ) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    @RequestMapping("/login")
    public String getLoginPage(Model model) {

        model.addAttribute("api_key", apiKey);
        model.addAttribute("api_key_domain", "eco-service.uz");
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
    @RequestMapping(SysUrls.EDSLogin + "?failed=1")
    public String getErrorEDSPage() {
        return SysTemplates.ErrorEds;
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

}
