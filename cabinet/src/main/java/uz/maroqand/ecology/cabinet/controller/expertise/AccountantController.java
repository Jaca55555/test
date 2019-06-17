package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
@Controller
public class AccountantController {

    private final RegApplicationService regApplicationService;
    private final SoatoService soatoService;
    private final UserService userService;
    private final ClientService clientService;
    private final HelperService helperService;

    @Autowired
    public AccountantController(
            RegApplicationService regApplicationService,
            SoatoService soatoService,
            UserService userService,
            ClientService clientService,
            HelperService helperService){
        this.regApplicationService = regApplicationService;
        this.soatoService = soatoService;
        this.userService = userService;
        this.clientService = clientService;
        this.helperService = helperService;
    }

    @RequestMapping(value = ExpertiseUrls.AccountantList)
    public String getAccountantListPage(Model model) {
        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        return ExpertiseTemplates.AccountantList;
    }

    @RequestMapping(value = ExpertiseUrls.AccountantListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getAccountantListPageListAjax(
            @RequestParam(name = "tin",required = false)Integer tin,
            @RequestParam(name = "name",required = false)String name,
            @RequestParam(name = "regApplicationId",required = false)Integer regApplicationId,
            @RequestParam(name = "regionId",required = false)Integer regionId,
            @RequestParam(name = "subRegionId",required = false)Integer subRegionId,
            @RequestParam(name = "regDateBegin",required = false)String regDateBegin,
            @RequestParam(name = "regDateEnd",required = false)String regDateEnd,
            @RequestParam(name = "contractDateBegin",required = false)String contractDateBegin,
            @RequestParam(name = "contractDateEnd",required = false)String contractDateEnd,
            Pageable pageable
    ){
        User user = userService.getCurrentUserFromContext();
        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(user.getId(),pageable);
        HashMap<String, Object> result = new HashMap<>();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            Client client = clientService.getById(regApplication.getApplicantId());
            convenientForJSONArray.add(new Object[]{
                regApplication.getId(),
                client.getTin(),
                client.getName(),
                client.getOpf()!=null?client.getOpf().getId():"",
                client.getOked()!=null?client.getOked():"",
                client.getRegionId()!=null?helperService.getSoatoName(client.getRegionId(),locale):"",
                client.getSubRegionId()!=null?helperService.getSoatoName(client.getSubRegionId(),locale):"",
                regApplication.getCreatedAt()!=null?Common.uzbekistanDateFormat.format(regApplication.getCreatedAt()):"",
                null
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.AccountantChecking)
    public String getCheckingPage(
            @RequestParam(name = "id")Integer regApplicationId,
            Model model
    ) {
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.AccountantList;
        }

        Client client = clientService.getById(regApplication.getApplicantId());

        model.addAttribute("applicant",client);
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("confirmUrl",ExpertiseUrls.AccountantConfirm);
        model.addAttribute("notConfirmUrl",ExpertiseUrls.AccountantNotConfirm);
        model.addAttribute("cancel_url",ExpertiseUrls.AccountantList);
        return ExpertiseTemplates.AccountantChecking;
    }

    @RequestMapping(value = ExpertiseUrls.AccountantConfirm,method = RequestMethod.POST)
    public HashMap<String,Object> confirmApplication(){
        HashMap<String,Object> result = new HashMap<>();
        return result;
    }

    @RequestMapping(value = ExpertiseUrls.AccountantNotConfirm,method = RequestMethod.POST)
    public HashMap<String,Object> notConfirmApplication(){
        HashMap<String,Object> result = new HashMap<>();
        return result;
    }

}
