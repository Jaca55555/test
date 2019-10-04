package uz.maroqand.ecology.cabinet.controller.expertise;

import org.apache.commons.lang3.StringUtils;
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
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.dto.expertise.*;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.CoordinateLatLongRepository;
import uz.maroqand.ecology.core.repository.expertise.CoordinateRepository;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
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
    private final ConclusionService conclusionService;

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
            CoordinateLatLongRepository coordinateLatLongRepository,
            ConclusionService conclusionService) {
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
        this.conclusionService = conclusionService;
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
                null,//todo shart kerak
                pageable
        );

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            Client client = clientService.getById(regApplication.getApplicantId());
            RegApplicationLog performerLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
            RegApplicationLog agreementCompleteLog = regApplicationLogService.getById(regApplication.getAgreementCompleteLogId());
            convenientForJSONArray.add(new Object[]{
                    regApplication.getId(),
                    client.getTin(),
                    client.getName(),
                    regApplication.getMaterials() != null ?helperService.getMaterialShortNames(regApplication.getMaterials(),locale):"",
                    regApplication.getCategory() != null ?helperService.getCategory(regApplication.getCategory().getId(),locale):"",
                    regApplication.getRegistrationDate() != null ? Common.uzbekistanDateFormat.format(regApplication.getRegistrationDate()):"",
                    regApplication.getDeadlineDate() != null ?Common.uzbekistanDateFormat.format(regApplication.getDeadlineDate()):"",
                    performerLog.getStatus() != null ? helperService.getTranslation(performerLog.getStatus().getPerformerName(), locale):"",
                    performerLog.getStatus() != null ? performerLog.getStatus().getId():"",
                    agreementCompleteLog.getStatus() !=null ? helperService.getTranslation(agreementCompleteLog.getStatus().getAgreementName(), locale):"",
                    agreementCompleteLog.getStatus() !=null ? agreementCompleteLog.getStatus().getId():""
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

        Client applicant = clientService.getById(regApplication.getApplicantId());
        switch (applicant.getType()){
            case Individual:
                model.addAttribute("individual", new IndividualDto(applicant)); break;
            case LegalEntity:
                model.addAttribute("legalEntity", new LegalEntityDto(applicant)) ;break;
            case ForeignIndividual:
                model.addAttribute("foreignIndividual", new ForeignIndividualDto(applicant)); break;
            case IndividualEnterprise:
                model.addAttribute("individualEntrepreneur", new IndividualEntrepreneurDto(applicant)); break;
        }

        Coordinate coordinate = coordinateRepository.findByRegApplicationIdAndDeletedFalse(regApplication.getId());
        if(coordinate != null){
            List<CoordinateLatLong> coordinateLatLongList = coordinateLatLongRepository.getByCoordinateIdAndDeletedFalse(coordinate.getId());
            model.addAttribute("coordinate", coordinate);
            model.addAttribute("coordinateLatLongList", coordinateLatLongList);
        }

        Conclusion conclusion = conclusionService.getByRegApplicationIdLast(regApplicationId);
        model.addAttribute("conclusionId", conclusion!=null?conclusion.getId():0);
        model.addAttribute("conclusionText", conclusion!=null?conclusion.getHtmlText():"");

        model.addAttribute("invoice",invoiceService.getInvoice(regApplication.getInvoiceId()));
        model.addAttribute("applicant",applicant);
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("regApplicationLog",regApplicationLog);

        model.addAttribute("lastCommentList", commentService.getByRegApplicationIdAndType(regApplication.getId(), CommentType.CONFIDENTIAL));
        model.addAttribute("performerLog", regApplicationLogService.getById(regApplication.getPerformerLogId()));
        model.addAttribute("agreementLogList", regApplicationLogService.getByIds(regApplication.getAgreementLogs()));
        model.addAttribute("agreementCompleteLog", regApplicationLog);
        model.addAttribute("regApplicationLogList", regApplicationLogService.getByRegApplicationId(regApplication.getId()));
        return ExpertiseTemplates.AgreementCompleteView;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementCompleteAction,method = RequestMethod.POST)
    public String confirmApplication(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "logId")Integer logId,
            @RequestParam(name = "agreementStatus")Integer agreementStatus,
            @RequestParam(name = "comment")String comment
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.AgreementCompleteList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        regApplicationLogService.update(regApplicationLog, LogStatus.getLogStatus(agreementStatus), comment, user.getId());
        if(StringUtils.trimToNull(comment) != null){
            commentService.create(id, CommentType.CONFIDENTIAL, comment, user.getId());
        }

        if(agreementStatus == 3){
            RegApplicationLog performerLog = regApplicationLogService.getById(regApplication.getPerformerLogId());
            switch (performerLog.getStatus()){
                case Modification: regApplication.setStatus(RegApplicationStatus.Modification); break;
                case Approved: regApplication.setStatus(RegApplicationStatus.Approved); break;
                case Denied: regApplication.setStatus(RegApplicationStatus.NotConfirmed); break;
            }
            regApplication.setAgreementStatus(LogStatus.Approved);
            regApplicationService.update(regApplication);
        }

        if(agreementStatus == 4){
            regApplication.setAgreementStatus(LogStatus.Denied);
            RegApplicationLog performerLogNext = regApplicationLogService.create(regApplication, LogType.Performer, comment, user);
            regApplication.setPerformerLogIdNext(performerLogNext.getId());
            regApplicationService.update(regApplication);
        }

        return "redirect:"+ExpertiseUrls.AgreementCompleteView + "?id=" + regApplication.getId();
    }

}
