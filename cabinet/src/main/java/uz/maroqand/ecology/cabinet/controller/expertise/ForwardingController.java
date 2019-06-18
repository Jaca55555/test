package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseTemplates;
import uz.maroqand.ecology.cabinet.constant.expertise.ExpertiseUrls;
import uz.maroqand.ecology.core.constant.expertise.ForwardingStatus;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.ProjectDeveloperService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Controller
public class ForwardingController {

    private final RegApplicationService regApplicationService;
    private final ClientService clientService;
    private final UserService userService;
    private final HelperService helperService;
    private final SoatoService soatoService;
    private final ProjectDeveloperService projectDeveloperService;

    @Autowired
    public ForwardingController(
            RegApplicationService regApplicationService,
            ClientService clientService,
            UserService userService,
            HelperService helperService,
            SoatoService soatoService, ProjectDeveloperService projectDeveloperService) {
        this.regApplicationService = regApplicationService;
        this.clientService = clientService;
        this.userService = userService;
        this.helperService = helperService;
        this.soatoService = soatoService;
        this.projectDeveloperService = projectDeveloperService;
    }

    @RequestMapping(ExpertiseUrls.ForwardingList)
    public String getForwardingListPage(Model model){
        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("statusList", ForwardingStatus.getForwardingStatusList());
        return ExpertiseTemplates.ForwardingList;
    }

    @RequestMapping(value = ExpertiseUrls.ForwardingListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getForwardingListAjaxPage(
            @RequestParam(name = "tin",required = false)Integer tin,
            @RequestParam(name = "name",required = false)String name,
            @RequestParam(name = "regApplicationId",required = false)Integer regApplicationId,
            @RequestParam(name = "status",required = false)Integer status,
            @RequestParam(name = "regionId",required = false)Integer regionId,
            @RequestParam(name = "subRegionId",required = false)Integer subRegionId,
            @RequestParam(name = "regDateBegin",required = false)String registrationDateBegin,
            @RequestParam(name = "regDateEnd",required = false)String registrationDateEnd,
            @RequestParam(name = "contractDateBegin",required = false)String contractDateBeginDate,
            @RequestParam(name = "contractDateEnd",required = false)String contractDateEndDate,
            Pageable pageable
    ){
        HashMap<String,Object> result = new HashMap<>();
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        Date regDateBegin = DateParser.TryParse(registrationDateBegin, Common.uzbekistanDateFormat);
        Date regDateEnd = DateParser.TryParse(registrationDateEnd,Common.uzbekistanDateFormat);
        Date contractDateBegin = DateParser.TryParse(contractDateBeginDate,Common.uzbekistanDateFormat);
        Date contractDateEnd = DateParser.TryParse(contractDateEndDate,Common.uzbekistanDateFormat);

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(user.getId(),pageable);
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
                    regApplication.getMaterialId()!=null?helperService.getMaterial(regApplication.getMaterialId(),locale):"",
                    regApplication.getActivityId()!=null?helperService.getActivity(regApplication.getActivityId(),locale):"",
                    regApplication.getCreatedAt()!=null?Common.uzbekistanDateFormat.format(regApplication.getCreatedAt()):"",
                    regApplication.getPerformerId()!=null?projectDeveloperService.getById(regApplication.getPerformerId()):"",
                    regApplication.getForwardingStatus()!=null?regApplication.getForwardingStatus().getName():""
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.ForwardingChecking)
    public String getCheckingPage(
            @RequestParam(name = "id")Integer regApplicationId,
            Model model
    ) {
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ForwardingList;
        }

        Client client = clientService.getById(regApplication.getApplicantId());
        model.addAttribute("applicant",client);
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("cancel_url",ExpertiseUrls.ForwardingList);
        return ExpertiseTemplates.ForwardingChecking;
    }
}
