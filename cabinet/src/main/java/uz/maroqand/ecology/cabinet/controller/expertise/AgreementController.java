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
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.constant.user.ToastrType;
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
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.ToastrService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz) Kelishish
 * (ru)
 */
@Controller
public class AgreementController {

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
    private final ToastrService toastrService;

    @Autowired
    public AgreementController(
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
            ToastrService toastrService){
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
        this.toastrService = toastrService;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementList)
    public String getConfirmListPage(Model model) {

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList", LogStatus.getLogStatusList());
        return ExpertiseTemplates.AgreementList;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getPerformerListAjax(
            FilterDto filterDto,
            Pageable pageable
    ){
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String,Object> result = new HashMap<>();

        Page<RegApplicationLog> regApplicationLogs = regApplicationLogService.findFiltered(
                filterDto,
                null,
                user.getId(),
                LogType.Agreement,
                null,
                pageable
        );

        List<RegApplicationLog> regApplicationLogList = regApplicationLogs.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationLogList.size());
        for (RegApplicationLog regApplicationLog : regApplicationLogList){
            RegApplication regApplication = regApplicationService.getById(regApplicationLog.getRegApplicationId());
            Client client = clientService.getById(regApplication.getApplicantId());
            RegApplicationLog performerLog = regApplicationLogService.getById(regApplication.getPerformerLogId());

            convenientForJSONArray.add(new Object[]{
                    regApplication.getId(),
                    client.getTin(),
                    client.getName(),
                    regApplication.getMaterials() != null ?helperService.getMaterialShortNames(regApplication.getMaterials(),locale):"",
                    regApplication.getCategory() != null ?helperService.getCategory(regApplication.getCategory().getId(),locale):"",
                    regApplication.getRegistrationDate() != null ? Common.uzbekistanDateFormat.format(regApplication.getRegistrationDate()):"",
                    regApplication.getDeadlineDate() != null ?Common.uzbekistanDateFormat.format(regApplication.getDeadlineDate()):"",
                    performerLog != null && performerLog.getStatus() != null ? performerLog.getStatus().getPerformerName() : "",
                    performerLog != null && performerLog.getStatus() != null ? performerLog.getStatus().getId() : "",
                    regApplicationLog.getStatus() != null ? regApplicationLog.getStatus().getAgreementName() : "",
                    regApplicationLog.getStatus() != null ? regApplicationLog.getStatus().getId() : "",
                    regApplicationLog.getId()
            });
        }

        result.put("recordsTotal", regApplicationLogs.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationLogs.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.AgreementView)
    public String getAgreementViewPage(
            @RequestParam(name = "id")Integer regApplicationId,
            @RequestParam(name = "logId")Integer logId,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.AgreementList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        if(
            !regApplicationLog.getRegApplicationId().equals(regApplicationId) ||
            !regApplicationLog.getUpdateById().equals(user.getId()) ||
            !regApplicationLog.getType().equals(LogType.Agreement)
        ){
            toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Kelishish bo'yicha ariza topilmadi");
            return "redirect:" + ExpertiseUrls.AgreementList;
        }

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

        model.addAttribute("applicant", client);
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("invoice", invoiceService.getInvoice(regApplication.getInvoiceId()));
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("regApplicationLog", regApplicationLog);

        model.addAttribute("lastCommentList", commentService.getByRegApplicationIdAndType(regApplication.getId(), CommentType.CONFIDENTIAL));
        model.addAttribute("performerLog", regApplicationLogService.getById(regApplication.getPerformerLogId()));
        model.addAttribute("agreementLogList", regApplicationLogService.getByIds(regApplication.getAgreementLogs()));
        model.addAttribute("agreementCompleteLog", regApplicationLogService.getById(regApplication.getAgreementCompleteLogId()));
        model.addAttribute("regApplicationLogList", regApplicationLogService.getByRegApplicationId(regApplication.getId()));
        return ExpertiseTemplates.AgreementView;
    }

    @RequestMapping(value = ExpertiseUrls.AgreementAction,method = RequestMethod.POST)
    public String confirmApplication(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "logId")Integer logId,
            @RequestParam(name = "agreementStatus")Integer agreementStatus,
            @RequestParam(name = "comment")String comment
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.AgreementList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        regApplicationLogService.update(regApplicationLog, LogStatus.getLogStatus(agreementStatus), comment, user);
        commentService.create(id, CommentType.CONFIDENTIAL, comment, user.getId());

        Boolean createAgreementComplete = true;
        List<RegApplicationLog> agreementLogList = regApplicationLogService.getByIds(regApplication.getAgreementLogs());
        for(RegApplicationLog agreementLog :agreementLogList){
            if(!agreementLog.getStatus().equals(LogStatus.Approved)){
                createAgreementComplete = false;
            }
        }
        if(createAgreementComplete){
            RegApplicationLog regApplicationLogCreate = regApplicationLogService.create(regApplication,LogType.AgreementComplete,comment,user);
            regApplication.setAgreementCompleteLogId(regApplicationLogCreate.getId());
            regApplication.setStatus(RegApplicationStatus.Process);
            regApplicationService.update(regApplication);
        }

        return "redirect:"+ExpertiseUrls.AgreementView + "?id=" + regApplication.getId() + "&logId=" + logId;
    }
}
