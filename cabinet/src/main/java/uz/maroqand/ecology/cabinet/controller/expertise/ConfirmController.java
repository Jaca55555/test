package uz.maroqand.ecology.cabinet.controller.expertise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.constant.user.ToastrType;
import uz.maroqand.ecology.core.dto.expertise.*;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.CoordinateLatLongRepository;
import uz.maroqand.ecology.core.repository.expertise.CoordinateRepository;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.*;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.ToastrService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz) Ariza ma'lumotlarini tekshirish
 * (ru)
 */
@Controller
public class ConfirmController {

    private final RegApplicationService regApplicationService;
    private final ClientService clientService;
    private final SoatoService soatoService;
    private final UserService userService;
    private final ActivityService activityService;
    private final ObjectExpertiseService objectExpertiseService;
    private final HelperService helperService;
    private final FileService fileService;
    private final RegApplicationLogService regApplicationLogService;
    private final ProjectDeveloperService projectDeveloperService;
    private final CoordinateRepository coordinateRepository;
    private final CoordinateLatLongRepository coordinateLatLongRepository;
    private final ToastrService toastrService;
    private final NotificationService notificationService;

    @Autowired
    public ConfirmController(
            RegApplicationService regApplicationService,
            SoatoService soatoService,
            UserService userService,
            ClientService clientService,
            ActivityService activityService,
            ObjectExpertiseService objectExpertiseService,
            HelperService helperService,
            FileService fileService,
            RegApplicationLogService regApplicationLogService,
            ProjectDeveloperService projectDeveloperService,
            CoordinateRepository coordinateRepository,
            CoordinateLatLongRepository coordinateLatLongRepository,
            ToastrService toastrService,
            NotificationService notificationService){
        this.regApplicationService = regApplicationService;
        this.soatoService = soatoService;
        this.userService = userService;
        this.clientService = clientService;
        this.activityService = activityService;
        this.objectExpertiseService = objectExpertiseService;
        this.helperService = helperService;
        this.fileService = fileService;
        this.regApplicationLogService = regApplicationLogService;
        this.projectDeveloperService = projectDeveloperService;
        this.coordinateRepository = coordinateRepository;
        this.coordinateLatLongRepository = coordinateLatLongRepository;
        this.toastrService = toastrService;
        this.notificationService = notificationService;
    }

    @RequestMapping(value = ExpertiseUrls.ConfirmList)
    public String getConfirmListPage(Model model) {

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList", LogStatus.getLogStatusList());
        return ExpertiseTemplates.ConfirmList;
    }

    @RequestMapping(value = ExpertiseUrls.ConfirmListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getConfirmListPageListAjax(
            FilterDto filterDto,
            Pageable pageable
    ){
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String, Object> result = new HashMap<>();

        Page<RegApplicationLog> regApplicationLogPage = regApplicationLogService.findFiltered(
                filterDto,
                null,
                null,
                LogType.Confirm,
                null,
                pageable
        );

        List<RegApplicationLog> regApplicationLogList = regApplicationLogPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationLogList.size());
        for (RegApplicationLog regApplicationLog : regApplicationLogList){
            RegApplication regApplication = regApplicationService.getById(regApplicationLog.getRegApplicationId());
            Client client = regApplication.getApplicant();

            convenientForJSONArray.add(new Object[]{
                regApplicationLog.getRegApplicationId(),
                client != null ? client.getTin() : "",
                client != null ? client.getName() : "",
                client != null ? helperService.getApplicantType(client.getType().getId(),locale):"",
                client != null ? client.getOpfId()!=null? helperService.getOpfName(client.getOpfId(),locale):"" : "",
                client != null ? client.getOked() : "",
                client != null ? client.getRegionId()!=null?helperService.getSoatoName(client.getRegionId(),locale): "" : "",
                client != null ? client.getSubRegionId()!=null?helperService.getSoatoName(client.getSubRegionId(),locale) : "" : "",
                regApplicationLog.getCreatedAt()!=null?Common.uzbekistanDateAndTimeFormat.format(regApplicationLog.getCreatedAt()):"",
                regApplicationLog.getStatus()!=null? helperService.getTranslation(regApplicationLog.getStatus().getConfirmName(),locale):"",
                regApplicationLog.getStatus()!=null? regApplicationLog.getStatus().getId():"",
                regApplicationLog.getId()
            });
        }

        result.put("recordsTotal", regApplicationLogPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationLogPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.ConfirmView)
    public String getCheckingPage(
            @RequestParam(name = "logId")Integer logId,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        if (regApplicationLog == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }
        if(!regApplicationLog.getType().equals(LogType.Confirm)){
            toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Tasdiqlash uchun ariza topilmadi");
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }

        RegApplication regApplication = regApplicationService.getById(regApplicationLog.getRegApplicationId());
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }

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

        model.addAttribute("applicant", applicant);
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("regApplication", regApplication);
        model.addAttribute("regApplicationLog", regApplicationLog);
        return ExpertiseTemplates.ConfirmView;
    }

    @RequestMapping(value = ExpertiseUrls.ConfirmApproved,method = RequestMethod.POST)
    public String confirmApplication(
            @RequestParam(name = "logId")Integer logId,
            @RequestParam(name = "comment")String comment,
            @RequestParam(name = "budget")Boolean budget
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        if (regApplicationLog == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }
        if(!regApplicationLog.getType().equals(LogType.Confirm)){
            toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Tasdiqlash uchun ariza topilmadi");
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }

        RegApplication regApplication = regApplicationService.getById(regApplicationLog.getRegApplicationId());
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }

        regApplicationLogService.update(regApplicationLog, LogStatus.Approved, comment, user);

        regApplication.setBudget(budget);
        regApplication.setStatus(RegApplicationStatus.CheckConfirmed);
        regApplicationService.update(regApplication);

        notificationService.create(
                regApplication.getCreatedById(),
                NotificationType.Expertise,
                "Arizani tasqinlandi",
                "Sizning " + regApplication.getId() + " raqamli arizangiz tasdiqlandi",
                "/reg/application/resume?id=" + regApplication.getId(),
                user.getId()
        );
        return "redirect:"+ExpertiseUrls.ConfirmView + "?logId=" + logId;
    }

    @RequestMapping(value = ExpertiseUrls.ConfirmDenied,method = RequestMethod.POST)
    public String notConfirmApplication(
            @RequestParam(name = "logId")Integer logId,
            @RequestParam(name = "comment")String comment,
            @RequestParam(name = "budget")Boolean budget
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        if (regApplicationLog == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }
        if(!regApplicationLog.getType().equals(LogType.Confirm)){
            toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Tasdiqlash uchun ariza topilmadi");
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }

        RegApplication regApplication = regApplicationService.getById(regApplicationLog.getRegApplicationId());
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }

        regApplicationLogService.update(regApplicationLog, LogStatus.Denied, comment, user);

        regApplication.setBudget(budget);
        regApplication.setStatus(RegApplicationStatus.CheckNotConfirmed);
        regApplicationService.update(regApplication);

        notificationService.create(
                regApplication.getCreatedById(),
                NotificationType.Expertise,
                "Arizani rad javobi berildi",
                "Sizning "+regApplication.getId()+" raqamli arizangizga rad javobi berildi",
                "/reg/application/resume?id=" + regApplication.getId(),
                user.getId()
        );
        return "redirect:"+ExpertiseUrls.ConfirmView + "?logId=" + logId;
    }

    @RequestMapping(value = ExpertiseUrls.ConfirmApprovedEdit,method = RequestMethod.POST)
    public String confirmEditLog(
            @RequestParam(name = "logId")Integer logId,
            @RequestParam(name = "comment")String comment,
            @RequestParam(name = "budget")Boolean budget
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        if (regApplicationLog == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }
        if(!regApplicationLog.getType().equals(LogType.Confirm)){
            toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Tasdiqlash uchun ariza topilmadi");
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }

        RegApplication regApplication = regApplicationService.getById(regApplicationLog.getRegApplicationId());
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }
        if (regApplication.getOfferId() != null){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza keyingi bosqichga o'tib bo'lgan, Shartnoma imzolangan.");
            return "redirect:"+ExpertiseUrls.ConfirmView + "?logId=" + logId;
        }

        RegApplicationLog newRegApplicationLog = regApplicationLogService.create(regApplication, LogType.Confirm, comment, user);
        regApplicationLogService.update(newRegApplicationLog, LogStatus.Approved, comment, user);

        regApplication.setConfirmLogId(newRegApplicationLog.getId());
        regApplication.setBudget(budget);
        regApplication.setStatus(RegApplicationStatus.CheckConfirmed);
        regApplicationService.update(regApplication);

        notificationService.create(
                regApplication.getCreatedById(),
                NotificationType.Expertise,
                "Arizani tasqinlandi",
                "Sizning "+regApplication.getId()+" raqamli arizangiz tasdiqlandi",
                "/reg/application/resume?id=" + regApplication.getId(),
                user.getId()
        );
        return "redirect:"+ExpertiseUrls.ConfirmView + "?logId=" + newRegApplicationLog.getId();
    }

    @RequestMapping(value = ExpertiseUrls.ConfirmDeniedEdit,method = RequestMethod.POST)
    public String confirmDeniedEdit(
            @RequestParam(name = "logId")Integer logId,
            @RequestParam(name = "comment")String comment,
            @RequestParam(name = "budget")Boolean budget
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplicationLog regApplicationLog = regApplicationLogService.getById(logId);
        if (regApplicationLog == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }
        if(!regApplicationLog.getType().equals(LogType.Confirm)){
            toastrService.create(user.getId(), ToastrType.Warning, "Ruxsat yo'q.","Tasdiqlash uchun ariza topilmadi");
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }

        RegApplication regApplication = regApplicationService.getById(regApplicationLog.getRegApplicationId());
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }
        if (regApplication.getOfferId() != null){
            toastrService.create(user.getId(), ToastrType.Error, "Ruxsat yo'q.","Ariza keyingi bosqichga o'tib bo'lgan, Shartnoma imzolangan.");
            return "redirect:"+ExpertiseUrls.ConfirmView + "?logId=" + logId;
        }

        RegApplicationLog newRegApplicationLog = regApplicationLogService.create(regApplication, LogType.Confirm, comment, user);
        regApplicationLogService.update(newRegApplicationLog, LogStatus.Denied, comment, user);

        regApplication.setConfirmLogId(newRegApplicationLog.getId());
        regApplication.setBudget(budget);
        regApplication.setStatus(RegApplicationStatus.CheckNotConfirmed);
        regApplicationService.update(regApplication);

        notificationService.create(
                regApplication.getCreatedById(),
                NotificationType.Expertise,
                "Arizani rad javobi berildi", "Sizning "+regApplication.getId()+" raqamli arizangizga rad javobi berildi",
                "/reg/application/resume?id=" + regApplication.getId(),
                user.getId()
        );
        return "redirect:"+ExpertiseUrls.ConfirmView + "?logId=" + newRegApplicationLog.getId();
    }

    @RequestMapping(ExpertiseUrls.ExpertiseFileDownload)
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam(name = "file_id") Integer fileId){

        File file = fileService.findById(fileId);
        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

}
