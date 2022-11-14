package uz.maroqand.ecology.cabinet.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.cabinet.constant.sys.SysUrls;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.ConclusionRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationLogService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.expertise.RequirementService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
@Controller
public class MainController {
    private final UserService userService;
    private final RegApplicationService regApplicationService;
    private final InvoiceService invoiceService;
    private final RequirementService requirementService;
    private final RegApplicationLogService regApplicationLogService;
    private final FileService fileService;
    private final ConclusionService conclusionService;
    private final ConclusionRepository conclusionRepository;

    @Autowired
    public MainController(UserService userService, RegApplicationService regApplicationService, InvoiceService invoiceService, RequirementService requirementService, RegApplicationLogService regApplicationLogService, FileService fileService, ConclusionService conclusionService, ConclusionRepository conclusionRepository) {
        this.userService = userService;
        this.regApplicationService = regApplicationService;
        this.invoiceService = invoiceService;
        this.requirementService = requirementService;
        this.regApplicationLogService = regApplicationLogService;
        this.fileService = fileService;
        this.conclusionService = conclusionService;
        this.conclusionRepository = conclusionRepository;
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

    @PostMapping("/replace")
    public String sendEcoFond() {
        List<Conclusion> conclusionList = conclusionRepository.findAllByDeletedFalse();

        conclusionList.stream().map(conclusion -> {
           conclusion.setHtmlText( conclusion.getHtmlText().replace("sys_conclusion.title_1700","O’ZBEKISTON RESPUBLIKASI EKOLOGIYA VA ATROF-MUHITNI MUHOFAZA QILISH DAVLAT QO’MITASI")
                   .replace("sys_conclusion.full_adress_1700","100043, Toshkent shahri, Chilonzor tumani, Bunyodkor shoh ko`chasi, 7a-uy. Tel: 71-207-11-02,  <br> faks: 71-236-02-32 veb-sahifa: http://www.eco.gov.uz, elektron pochta: info@eco.gov.uz")
                   .replace("sys_conclusion.description_1700","Davlat ekologik ekspertizasining <br> X U L O S A S I")
          );
            System.out.println("conclusionId=="+conclusion.getId());
            conclusionRepository.saveAndFlush(conclusion);
            return conclusion;
        }).collect(Collectors.toList());
        return "hello world";


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

    @PostMapping(ExpertiseUrls.InvoiceModification)
    @ResponseBody
    public String invoiceModif (
            @RequestParam(name = "id",required = false) Integer id,
            @RequestParam(name = "invoice",required = false) String invoiceStr
    ){
        invoiceStr = StringUtils.trimToNull(invoiceStr);
        if (id==null || invoiceStr==null){
            return "error";
        }
        System.out.println("regId==" + id);
        System.out.println("invoiceStr==" + invoiceStr);
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication!=null){
            Invoice invoice = invoiceService.getInvoice(invoiceStr);
            if (invoice!=null && regApplication.getInvoiceId().equals(invoice.getId())){
                invoiceService.modification(regApplication,invoice,requirementService.getById(regApplication.getRequirementId()));
                invoice.setStatus(InvoiceStatus.Initial);
                invoiceService.save(invoice);

                regApplication.setStatus(RegApplicationStatus.CheckConfirmed);
                regApplication.setLogIndex(null);
                regApplication.setPerformerId(null);
                regApplication.setPerformerLogId(null);
                regApplication.setPerformerLogIdNext(null);
                regApplication.setAgreementCompleteLogId(null);
                regApplication.setConclusionId(null);
                regApplication.setConclusionOnline(true);
                regApplication.setAgreementLogs(null);
                regApplication.setForwardingLogId(null);
                regApplication.setPerformerLogIdNext(null);
                regApplicationService.update(regApplication);

                List<RegApplicationLog> regApplicationLogs = regApplicationLogService.getByRegApplicationId(regApplication.getId());
                for (RegApplicationLog regApplicationLog:regApplicationLogs) {
                    if (!regApplicationLog.getType().equals(LogType.Confirm)){
                        regApplicationLog.setDeleted(true);
                        regApplicationLogService.update(regApplicationLog,regApplicationLog.getStatus(),"incoice modification bo'lganligi uchun",1);
                    }
                }
                return "ok";
            }
        }

        return "error1";
    }

    @GetMapping("/reg/appeal/images")
    public ResponseEntity<Resource> getImage(@RequestParam(name = "id") Integer id) {

        User user = userService.getCurrentUserFromContext();
        File file = fileService.findByIdAndUploadUserId(id, user.getId());

        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
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
