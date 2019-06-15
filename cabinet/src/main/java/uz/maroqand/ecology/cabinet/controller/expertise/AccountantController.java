package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
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
import uz.maroqand.ecology.core.service.user.UserService;

import javax.validation.Valid;
import java.util.HashMap;

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

    @Autowired
    public AccountantController(RegApplicationService regApplicationService, SoatoService soatoService, UserService userService, ClientService clientService) {
        this.regApplicationService = regApplicationService;
        this.soatoService = soatoService;
        this.userService = userService;
        this.clientService = clientService;
    }

    @RequestMapping(value = ExpertiseUrls.AccountantList)
    public String getAccountantListPage(Model model) {
        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        return ExpertiseTemplates.AccountantList;
    }

    @RequestMapping(value = ExpertiseUrls.AccountantListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public DataTablesOutput<RegApplication> getAccountantListPageListAjax(@Valid DataTablesInput input) {
        User user = userService.getCurrentUserFromContext();
        return regApplicationService.findFiltered(input, user.getId());
    }

    @RequestMapping(ExpertiseUrls.AccountantChecking)
    public String getCheckingPage(
            @RequestParam(name = "id")Integer regApplicationId,
            Model model
    ) {
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.AccountantList;
        }
        if (regApplication.getApplicantId() != null){
            Client client = clientService.getById(regApplication.getApplicantId());
            if (client.getRegionId()!=null && client.getSubRegionId()!=null){
                model.addAttribute("region",soatoService.getById(client.getRegionId()));
                model.addAttribute("subRegion",soatoService.getById(client.getSubRegionId()));
            }
        }

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
