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
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.Comment;
import uz.maroqand.ecology.core.entity.expertise.ProjectDeveloper;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
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
    private final CommentService commentService;
    private final HelperService helperService;
    private final FileService fileService;
    private final RegApplicationLogService regApplicationLogService;
    private final ProjectDeveloperService projectDeveloperService;

    @Autowired
    public ConfirmController(
            RegApplicationService regApplicationService,
            SoatoService soatoService,
            UserService userService,
            ClientService clientService,
            ActivityService activityService, ObjectExpertiseService objectExpertiseService,
            CommentService commentService,
            HelperService helperService,
            FileService fileService,
            RegApplicationLogService regApplicationLogService,
            ProjectDeveloperService projectDeveloperService){
        this.regApplicationService = regApplicationService;
        this.soatoService = soatoService;
        this.userService = userService;
        this.clientService = clientService;
        this.activityService = activityService;
        this.objectExpertiseService = objectExpertiseService;
        this.commentService = commentService;
        this.helperService = helperService;
        this.fileService = fileService;
        this.regApplicationLogService = regApplicationLogService;
        this.projectDeveloperService = projectDeveloperService;
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
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String, Object> result = new HashMap<>();

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(
                filterDto,
                user.getOrganizationId(),
                LogType.Confirm,
                null,
                null,
                pageable
        );

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            Client client = regApplication.getApplicant();
            RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
            convenientForJSONArray.add(new Object[]{
                regApplication.getId(),
                client.getTin(),
                client.getName(),
                client.getOpfId()!=null? helperService.getOpfName(client.getOpfId(),locale):"",
                client.getOked()!=null?client.getOked():"",
                client.getRegionId()!=null?helperService.getSoatoName(client.getRegionId(),locale):"",
                client.getSubRegionId()!=null?helperService.getSoatoName(client.getSubRegionId(),locale):"",
                regApplicationLog.getCreatedAt()!=null?Common.uzbekistanDateAndTimeFormat.format(regApplicationLog.getCreatedAt()):"",
                regApplicationLog.getStatus()!=null?regApplicationLog.getStatus().getConfirmName():"",
                regApplicationLog.getStatus()!=null?regApplicationLog.getStatus().getId():""
            });
        }

        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.ConfirmView)
    public String getCheckingPage(
            @RequestParam(name = "id")Integer regApplicationId,
            Model model
    ) {
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());

        Client client = clientService.getById(regApplication.getApplicantId());
        if(client.getType().equals(ApplicantType.Individual)){
            model.addAttribute("individual", new IndividualDto(client));
        }else {
            model.addAttribute("legalEntity", new LegalEntityDto(client));
        }

        Comment comment = commentService.getByRegApplicationId(regApplication.getId());
        model.addAttribute("comment",comment!=null?comment:new Comment());

        model.addAttribute("applicant",client);
        model.addAttribute("projectDeveloper", projectDeveloperService.getById(regApplication.getDeveloperId()));
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("regApplicationLog",regApplicationLog);
        return ExpertiseTemplates.ConfirmView;
    }

    @RequestMapping(value = ExpertiseUrls.ConfirmApproved,method = RequestMethod.POST)
    public String confirmApplication(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "comment")String comment
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
        regApplicationLogService.update(regApplicationLog, LogStatus.Approved, comment, user);

        return "redirect:"+ExpertiseUrls.ConfirmView + "?id=" + regApplication.getId();
    }

    @RequestMapping(value = ExpertiseUrls.ConfirmDenied,method = RequestMethod.POST)
    public String notConfirmApplication(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "comment")String comment
    ){
        User user = userService.getCurrentUserFromContext();
        RegApplication regApplication = regApplicationService.getById(id);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }

        RegApplicationLog regApplicationLog = regApplicationLogService.getById(regApplication.getConfirmLogId());
        regApplicationLogService.update(regApplicationLog, LogStatus.Denied, comment, user);

        return "redirect:"+ExpertiseUrls.ConfirmView + "?id=" + regApplication.getId();
    }

    @RequestMapping(ExpertiseUrls.ConfirmFileDownload)
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam(name = "file_id") Integer fileId){
        User user = userService.getCurrentUserFromContext();

        File file = fileService.findByIdAndUploadUserId(fileId, user.getId());

        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

}
