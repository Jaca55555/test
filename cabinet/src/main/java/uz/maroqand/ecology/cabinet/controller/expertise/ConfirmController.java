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
import uz.maroqand.ecology.core.constant.expertise.ConfirmStatus;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.Comment;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.ActivityService;
import uz.maroqand.ecology.core.service.expertise.CommentService;
import uz.maroqand.ecology.core.service.expertise.ObjectExpertiseService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.sys.FileService;
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

    @Autowired
    public ConfirmController(
            RegApplicationService regApplicationService,
            SoatoService soatoService,
            UserService userService,
            ClientService clientService,
            ActivityService activityService, ObjectExpertiseService objectExpertiseService,
            CommentService commentService,
            HelperService helperService,
            FileService fileService
    ){
        this.regApplicationService = regApplicationService;
        this.soatoService = soatoService;
        this.userService = userService;
        this.clientService = clientService;
        this.activityService = activityService;
        this.objectExpertiseService = objectExpertiseService;
        this.commentService = commentService;
        this.helperService = helperService;
        this.fileService = fileService;
    }

    @RequestMapping(value = ExpertiseUrls.ConfirmList)
    public String getConfirmListPage(Model model) {

        model.addAttribute("regions",soatoService.getRegions());
        model.addAttribute("subRegions",soatoService.getSubRegions());
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("activityList",activityService.getList());
        model.addAttribute("statusList",ConfirmStatus.getConfirmStatusList());
        return ExpertiseTemplates.ConfirmList;
    }

    @RequestMapping(value = ExpertiseUrls.ConfirmListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getConfirmListPageListAjax(
            FilterDto filterDto,
            Pageable pageable
    ){
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> result = new HashMap<>();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        Page<RegApplication> regApplicationPage = regApplicationService.findFiltered(filterDto, user.getId(), pageable);
        result.put("recordsTotal", regApplicationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", regApplicationPage.getTotalElements()); //Filtered elements

        List<RegApplication> regApplicationList = regApplicationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(regApplicationList.size());
        for (RegApplication regApplication : regApplicationList){
            Client client = regApplication.getApplicant();
            if(client==null){
                client = new Client();
            }
            convenientForJSONArray.add(new Object[]{
                regApplication.getId(),
                client.getTin(),
                client.getName(),
                client.getOpf()!=null?client.getOpf().getNameShortTranslation(locale):"",
                client.getOked()!=null?client.getOked():"",
                client.getRegionId()!=null?helperService.getSoatoName(client.getRegionId(),locale):"",
                client.getSubRegionId()!=null?helperService.getSoatoName(client.getSubRegionId(),locale):"",
                regApplication.getCreatedAt()!=null?Common.uzbekistanDateFormat.format(regApplication.getCreatedAt()):"",
                regApplication.getConfirmStatus()!=null?regApplication.getConfirmStatus().getName():""
            });
        }
        result.put("data",convenientForJSONArray);
        return result;
    }

    @RequestMapping(ExpertiseUrls.ConfirmChecking)
    public String getCheckingPage(
            @RequestParam(name = "id")Integer regApplicationId,
            @RequestParam(name = "status",required = false) ConfirmStatus confirmStatus,
            Model model
    ) {
        RegApplication regApplication = regApplicationService.getById(regApplicationId);
        if (regApplication == null){
            return "redirect:" + ExpertiseUrls.ConfirmList;
        }
        Client client = clientService.getById(regApplication.getApplicantId());
        Comment comment = commentService.getByRegApplicationId(regApplication.getId());
        model.addAttribute("status", confirmStatus);
        model.addAttribute("applicant",client);
        model.addAttribute("comment",comment!=null?comment:new Comment());
        model.addAttribute("regApplication",regApplication);
        model.addAttribute("cancel_url",ExpertiseUrls.ConfirmList);
        return ExpertiseTemplates.ConfirmChecking;
    }

    @RequestMapping(value = ExpertiseUrls.ConfirmConfirm,method = RequestMethod.POST)
    public String confirmApplication(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "template")String template,
            @RequestParam(name = "comments")String comments
    ){
        RegApplication regApplication = regApplicationService.getById(id);
        User user = userService.getCurrentUserFromContext();
        Comment comment = commentService.getByRegApplicationId(regApplication.getId());
        if (comment == null){
            Comment newComment = new Comment();
            newComment.setCreatedAt(new Date());
            newComment.setCreatedById(user.getId());
            newComment.setMessage(comments);
            newComment.setDeleted(false);
            newComment.setRegApplicationId(regApplication.getId());
            commentService.createComment(newComment);
        }
        if (comment != null){
            comment.setMessage(comments);
            commentService.updateComment(comment);
        }
        regApplication.setConfirmStatus(ConfirmStatus.Approved);
        regApplicationService.save(regApplication);
        return "redirect:"+ExpertiseUrls.ConfirmChecking + "?id=" + regApplication.getId() + "&status=" + ConfirmStatus.Approved;
    }

    @RequestMapping(value = ExpertiseUrls.ConfirmNotConfirm,method = RequestMethod.POST)
    public String notConfirmApplication(
            @RequestParam(name = "id")Integer id,
            @RequestParam(name = "template")String template,
            @RequestParam(name = "comments")String comments
    ){
        RegApplication regApplication = regApplicationService.getById(id);
        User user = userService.getCurrentUserFromContext();
        Comment commentForNotConfirm = commentService.getByRegApplicationId(regApplication.getId());
        if (commentForNotConfirm == null){
            Comment newComment = new Comment();
            newComment.setCreatedAt(new Date());
            newComment.setCreatedById(user.getId());
            newComment.setMessage(comments);
            newComment.setDeleted(false);
            newComment.setRegApplicationId(regApplication.getId());
            commentService.createComment(newComment);
        }
        if (commentForNotConfirm != null){
            commentForNotConfirm.setMessage(comments);
            commentService.updateComment(commentForNotConfirm);
        }
        regApplication.setConfirmStatus(ConfirmStatus.Denied);
        regApplicationService.save(regApplication);
        return "redirect:"+ExpertiseUrls.ConfirmChecking + "?id=" + regApplication.getId()  + "&status=" + ConfirmStatus.Denied;
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
