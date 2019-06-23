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
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz) Ijrochi
 * (ru)
 */
@Controller
public class PerformerController {

    private final RegApplicationService regApplicationService;
    private final ClientService clientService;
    private final UserService userService;
    private final HelperService helperService;
    private final SoatoService soatoService;
    private final InvoiceService invoiceService;
    private final FileService fileService;
    private final ActivityService activityService;
    private final ObjectExpertiseService objectExpertiseService;
    private final RegApplicationLogService regApplicationLogService;
    private final ProjectDeveloperService projectDeveloperService;

    @Autowired
    public PerformerController(
            RegApplicationService regApplicationService,
            ClientService clientService,
            UserService userService,
            HelperService helperService,
            SoatoService soatoService,
            InvoiceService invoiceService,
            FileService fileService,
            ActivityService activityService,
            ObjectExpertiseService objectExpertiseService,
            RegApplicationLogService regApplicationLogService,
            ProjectDeveloperService projectDeveloperService
    ) {
        this.regApplicationService = regApplicationService;
        this.clientService = clientService;
        this.userService = userService;
        this.helperService = helperService;
        this.soatoService = soatoService;
        this.invoiceService = invoiceService;
        this.fileService = fileService;
        this.activityService = activityService;
        this.objectExpertiseService = objectExpertiseService;
        this.regApplicationLogService = regApplicationLogService;
        this.projectDeveloperService = projectDeveloperService;
    }

    @RequestMapping(ExpertiseUrls.PerformerList)
    public String getPerformerListPage(Model model){

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList", LogStatus.getLogStatusList());
        return ExpertiseTemplates.PerformerList;
    }

    @RequestMapping(value = ExpertiseUrls.PerformerListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getPerformerListAjax(
            FilterDto filterDto,
            Pageable pageable
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String,Object> result = new HashMap<>();

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                filterDto,
                user.getOrganizationId(),
                LogType.Performer,
                null,
                pageable
        );

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            Client client = clientService.getById(regApplication.getApplicantId());
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getForwardingLogId());
            convenientForJSONArray.add(new Object[]{
                    regApplication.getId(),
                    client.getTin(),
                    client.getName(),
                    regApplication.getMaterialId() != null ?helperService.getMaterial(regApplication.getMaterialId(),locale):"",
                    regApplication.getCategory() != null ?helperService.getCategory(regApplication.getCategory().getId(),locale):"",
                    regApplication.getRegistrationDate() != null ? Common.uzbekistanDateFormat.format(regApplication.getRegistrationDate()):"",
                    regApplication.getDeadlineDate() != null ?Common.uzbekistanDateFormat.format(regApplication.getDeadlineDate()):"",
                    regApplication.getStatus() != null ?regApplication.getStatus().getName():"",
                    regApplication.getStatus() != null ?regApplication.getStatus().getId():"",
                    regApplicationLog.getStatus() != null ?regApplicationLog.getStatus().getPerformerName():"",
                    regApplicationLog.getStatus() != null ?regApplicationLog.getStatus().getId():""
            });
        }

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.PerformerView)
    public String getForwardingViewPage(
            @RequestParam(name = "id")Integer regApplicationId,
            Model model
    ) {
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.PerformerList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());

        Client client = clientService.getById(regApplication.getApplicantId());
        if(client.getType().equals(ApplicantType.Individual)){
            model.addAttribute("individual", new IndividualDto(client));
        }else {
            model.addAttribute("legalEntity", new LegalEntityDto(client));
        }

        model.addAttribute("invoice",invoiceService.getInvoice(regApplication.getInvoiceId()));
        model.addAttribute("applicant",client);
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("regApplicationLog",regApplicationLog);
        return ExpertiseTemplates.PerformerView;
    }

}
