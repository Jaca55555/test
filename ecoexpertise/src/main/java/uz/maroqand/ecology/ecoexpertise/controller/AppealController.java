package uz.maroqand.ecology.ecoexpertise.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.core.constant.sys.AppealStatus;
import uz.maroqand.ecology.core.constant.sys.AppealType;
import uz.maroqand.ecology.core.entity.sys.Appeal;
import uz.maroqand.ecology.core.entity.sys.AppealSub;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.AppealService;
import uz.maroqand.ecology.core.service.sys.AppealSubService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.ecoexpertise.constant.reg.RegTemplates;
import uz.maroqand.ecology.ecoexpertise.constant.reg.RegUrls;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class AppealController {

    private final AppealService appealService;
    private final AppealSubService appealSubService;
    private final UserService userService;
    private final HelperService helperService;
    private final FileService fileService;

    @Autowired
    public AppealController(AppealService appealService, AppealSubService appealSubService, UserService userService, HelperService helperService, FileService fileService) {
        this.appealService = appealService;
        this.appealSubService = appealSubService;
        this.userService = userService;
        this.helperService = helperService;
        this.fileService = fileService;
    }


    @RequestMapping(RegUrls.AppealUserList)
    public String appealUserListPage( Model model ) {
        model.addAttribute("appealTypeList",AppealType.getAppealTypeList());
        return RegTemplates.AppealUserList;
    }

    @RequestMapping(value = RegUrls.AppealUserListAjax,produces = "application/json")
    @ResponseBody
    public HashMap<String,Object> appealUserListAjax(
            @RequestParam(name = "dateBegin", required = false) String registrationDateBegin,
            @RequestParam(name = "dateEnd", required = false) String registrationDateEnd,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "type", required = false) Integer type,
            @RequestParam(name = "status", required = false) Integer status,
            Pageable pageable
    ) {
        Date dateBegin = DateParser.TryParse(registrationDateBegin,Common.uzbekistanDateFormat);
        Date dateEnd = DateParser.TryParse(registrationDateEnd,Common.uzbekistanDateFormat);
        title = StringUtils.trimToNull(title);
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();

        Page<Appeal> appealPage = appealService.findFiltered(null,type,title,dateBegin,dateEnd,status,user.getId(), pageable);

        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", appealPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", appealPage.getTotalElements()); //Filtered elements

        List<Appeal> appealList = appealPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(appealList.size());

        for(Appeal appeal: appealList) {
            convenientForJSONArray.add(new Object[]{
                    appeal.getId(),
                    //appeal.getAppealType()!=null? helperService.getAppealType(appeal.getAppealType().getId(),locale):"",
                    appeal.getAppealType(),
                    appeal.getTitle(),
                    appeal.getCreatedAt()!=null? Common.uzbekistanDateFormat.format(appeal.getCreatedAt()):"",
                    appeal.getAppealStatus(),
                    appeal.getShowAdminCommentCount()
            });
        }

        result.put("data", convenientForJSONArray);
        return result;
    }

    @RequestMapping(RegUrls.AppealNew)
    public String appealNew( Model model ) {

        Appeal appeal = new Appeal();
        model.addAttribute("appeal", appeal);
        model.addAttribute("action_url", RegUrls.AppealCreate);
        model.addAttribute("cancel_url", RegUrls.AppealUserList);
        return RegTemplates.AppealNew;
    }

    @RequestMapping(value = RegUrls.AppealCreate, method = RequestMethod.POST)
    public String appealCreate(Appeal appeal)
    {
        User user = userService.getCurrentUserFromContext();
        appealService.create(appeal,user);
        return "redirect:" + RegUrls.AppealUserList;
    }

    @RequestMapping(RegUrls.AppealEdit)
    public String appealEdit(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        Appeal appeal = appealService.getById(id, user.getId());

        if(appeal==null || appeal.getAppealStatus()!=AppealStatus.Open){
            return "redirect:" + RegUrls.AppealUserList;
        }
        model.addAttribute("appeal", appeal);
        model.addAttribute("action_url", RegUrls.AppealUpdate);
        model.addAttribute("cancel_url", RegUrls.AppealUserList);
        return RegTemplates.AppealNew;
    }

    @RequestMapping(value = RegUrls.AppealUpdate, method = RequestMethod.POST)
    public String appealUpdate(Appeal appeal) {

        User user = userService.getCurrentUserFromContext();
        Appeal appealCurrent = appealService.getById(appeal.getId(), user.getId());
        if(appealCurrent == null || appealCurrent.getAppealStatus() != AppealStatus.Open){
            return "redirect:" + RegUrls.AppealUserList;
        }

        appealCurrent.setAppealType(appeal.getAppealType());
        appealCurrent.setPhone(appeal.getPhone());
        appealCurrent.setTitle(appeal.getTitle());
        appealCurrent.setDescription(appeal.getDescription());
        appealService.update(appealCurrent,user);

        return "redirect:" + RegUrls.AppealUserList;
    }

    @RequestMapping(value = RegUrls.AppealDelete)
    public String appealDelete(
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        Appeal appeal = appealService.getById(id, user.getId());
        if(appeal==null || appeal.getAppealStatus()!=AppealStatus.Open){
            return "redirect:" + RegUrls.AppealUserList;
        }
        appealService.delete(appeal,user);

        return "redirect:" + RegUrls.AppealUserList;
    }

    @RequestMapping(value = RegUrls.AppealUserView)
    public String appealUser(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        Appeal appeal = appealService.getById(id, user.getId());
        if(appeal==null){
            return "redirect:" + RegUrls.AppealUserList;
        }

        AppealSub appealSub = new AppealSub();
        List<AppealSub> appealSubList = appealSubService.getById(appeal.getId());

        //UPDATE MAP
        appealService.updateByUserId(appeal.getCreatedById());
        List<User>appealSubCreatedUserList = new ArrayList<>();
        for (AppealSub appealSub1 : appealSubList){
            User appealSubCreatedUser = userService.findById(appealSub1.getCreatedById());
            appealSubCreatedUserList.add(appealSubCreatedUser);
        }
        model.addAttribute("createdBy",userService.findById(appeal.getCreatedById()));
        model.addAttribute("appealSubCreatedUserList",appealSubCreatedUserList);
        model.addAttribute("appealSub", appealSub);
        model.addAttribute("appealSubList", appealSubList);
        model.addAttribute("appeal",appeal);
        model.addAttribute("action_url", RegUrls.AppealSubCreate);
        model.addAttribute("cancel_url", RegUrls.AppealUserList);
        return RegTemplates.AppealView;
    }

    @RequestMapping(value = RegUrls.AppealSubCreate, method = RequestMethod.POST)
    public String appealSubCreate(
            @RequestParam(name = "appealId") Integer id,
            AppealSub appealSub
    ) {
        System.out.println("appealId == " + id);
        User user = userService.getCurrentUserFromContext();
        Appeal appeal = appealService.getById(id, user.getId());
        if(appeal==null){
            return "redirect:" + RegUrls.AppealUserList;
        }

        appealSub.setAppealId(appeal.getId());
        appealSubService.create(appealSub,user);
        Integer count = appeal.getShowUserCommentCount()!=null?appeal.getShowUserCommentCount():0;
        appeal.setShowUserCommentCount(count + 1);
        appealService.updateCommentCount(appeal);

        return "redirect:" + RegUrls.AppealUserView + "?id=" + appeal.getId();
    }

    @RequestMapping(value = RegUrls.AppealFileUpload, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> uploadFile(@RequestParam(name = "upload")MultipartFile uploadFile) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String, Object> response = new HashMap<>();
        File file = fileService.uploadFile(uploadFile, user.getId(), "appealContent_"+uploadFile.getName(), uploadFile.getName());
        if (file != null) {
            response.put("url", RegUrls.AppealImages + "?id=" + file.getId());
        } else {
            response.put("error", "{'message': 'failed to upload'}");
        }
        return response;
    }

    @RequestMapping(RegUrls.AppealImages)
    public ResponseEntity<Resource> getImage(@RequestParam(name = "id") Integer id) {

        User user = userService.getCurrentUserFromContext();
        File file = fileService.findByIdAndUploadUserId(id, user.getId());

        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    @RequestMapping(value = RegUrls.SuggestAppealLanding, produces = "application/json")
    @ResponseBody
    public boolean suggestAppeallanding(
            @RequestParam(name = "full_name", defaultValue = "") String fullName,
            @RequestParam(name = "company_name", defaultValue = "") String companyName,
            @RequestParam(name = "email", defaultValue = "") String email,
            @RequestParam(name = "phone", defaultValue = "") String phone,
            @RequestParam(name = "message", defaultValue = "") String message
    ){
        if (fullName == null) return false;
        try {
            appealService.createAppealLanding(fullName, companyName, email, phone, message);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
