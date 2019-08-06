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
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.Coordinate;
import uz.maroqand.ecology.core.entity.expertise.CoordinateLatLong;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.CoordinateLatLongRepository;
import uz.maroqand.ecology.core.repository.expertise.CoordinateRepository;
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
 * (uz) Oxirgi kelishishuv
 * (ru)
 */
@Controller
public class AgreementCompleteController {

    private final RegApplicationService regApplicationService;
    private final ClientService clientService;
    private final SoatoService soatoService;
    private final UserService userService;
    private final ActivityService activityService;
    private final ObjectExpertiseService objectExpertiseService;
    private final CommentService commentService;
    private final HelperService helperService;
    private final RegApplicationLogService regApplicationLogService;
    private final InvoiceService invoiceService;
    private final ProjectDeveloperService projectDeveloperService;
    private final CoordinateRepository coordinateRepository;
    private final CoordinateLatLongRepository coordinateLatLongRepository;

    @Autowired
    public AgreementCompleteController(
            RegApplicationService regApplicationService,
            SoatoService soatoService,
            UserService userService,
            ClientService clientService,
            ActivityService activityService,
            ObjectExpertiseService objectExpertiseService,
            CommentService commentService,
            HelperService helperService,
            RegApplicationLogService regApplicationLogService,
            InvoiceService invoiceService,
            ProjectDeveloperService projectDeveloperService,
            CoordinateRepository coordinateRepository,
            CoordinateLatLongRepository coordinateLatLongRepository
    ) {
        this.regApplicationService = regApplicationService;
        this.soatoService = soatoService;
        this.userService = userService;
        this.clientService = clientService;
        this.activityService = activityService;
        this.objectExpertiseService = objectExpertiseService;
        this.commentService = commentService;
        this.helperService = helperService;
        this.regApplicationLogService = regApplicationLogService;
        this.invoiceService = invoiceService;
        this.projectDeveloperService = projectDeveloperService;
        this.coordinateRepository = coordinateRepository;
        this.coordinateLatLongRepository = coordinateLatLongRepository;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementCompleteList)
    public String getConfirmListPage(Model model) {

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList", LogStatus.getLogStatusList());
        return ExpertiseTemplates.AgreementCompleteList;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementCompleteListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getAgreementCompleteListAjax(
            FilterDto filterDto,
            Pageable pageable
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String,Object> result = new HashMap<>();

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                filterDto,
                user.getOrganizationId(),
                LogType.AgreementComplete,
                null,
                null,
                pageable
        );

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            Client client = clientService.getById(regApplication.getApplicantId());
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getAgreementCompleteLogId());
            convenientForJSONArray.add(new Object[]{
                    regApplication.getId(),
                    client.getTin(),
                    client.getName(),
                    regApplication.getMaterials() != null ?helperService.getMaterials(regApplication.getMaterials(),locale):"",
                    regApplication.getCategory() != null ?helperService.getCategory(regApplication.getCategory().getId(),locale):"",
                    regApplication.getRegistrationDate() != null ? Common.uzbekistanDateFormat.format(regApplication.getRegistrationDate()):"",
                    regApplication.getDeadlineDate() != null ?Common.uzbekistanDateFormat.format(regApplication.getDeadlineDate()):"",
                    regApplication.getStatus() != null ?regApplication.getStatus().getName():"",
                    regApplication.getStatus() != null ?regApplication.getStatus().getId():"",
                    regApplicationLog.getStatus() != null ?regApplicationLog.getStatus().getAgreementName():"",
                    regApplicationLog.getStatus() != null ?regApplicationLog.getStatus().getId():""
            });
        }

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.AgreementCompleteView)
    public String getAgreementCompleteViewPage(
            @RequestParam(name = "id")Integer regApplicationId,
            Model model
    ) {
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.AgreementCompleteList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getAgreementCompleteLogId());

        Client client = clientService.getById(regApplication.getApplicantId());
        if(client.getType().equals(ApplicantType.Individual)){
            model.addAttribute("individual", new IndividualDto(client));
        }else {
            model.addAttribute("legalEntity", new LegalEntityDto(client));
        }

        Coordinate coordinate = coordinateRepository.findByRegApplicationIdAndDeletedFalse(regApplication.getId());
        if(coordinate != null){
            List<CoordinateLatLong> coordinateLatLongList = coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId());
            model.addAttribute("coordinate", coordinate);
            model.addAttribute("coordinateLatLongList", coordinateLatLongList);
        }

        model.addAttribute("invoice",invoiceService.getInvoice(regApplication.getInvoiceId()));
        model.addAttribute("applicant",client);
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("regApplicationLog",regApplicationLog);
        return ExpertiseTemplates.AgreementCompleteView;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementCompleteAction,method = RequestMethod.POST)
    public String confirmApplication(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "logId")Integer logId,
            @RequestParam(name = "performerStatus")Integer performerStatus,
            @RequestParam(name = "comment")String comment
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.AgreementCompleteList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        regApplicationLogService.update(regApplicationLog, LogStatus.getLogStatus(performerStatus), comment, user);

//        RegApplicationLog regApplicationLogCreate = regApplicationLogService.create(regApplication,LogType.AgreementComplete,comment,user);

        regApplication.setStatus(RegApplicationStatus.Approved);
//        regApplication.setAgreementCompleteLogId(regApplicationLogCreate.getId());
        regApplicationService.update(regApplication);

        return "redirect:"+ExpertiseUrls.AgreementCompleteView + "?id=" + regApplication.getId();
    }

}
