package uz.maroqand.ecology.ecoexpertise.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.constant.sys.AppealStatus;
import uz.maroqand.ecology.core.constant.sys.AppealType;
import uz.maroqand.ecology.core.entity.sys.Appeal;
import uz.maroqand.ecology.core.entity.sys.AppealSub;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.AppealService;
import uz.maroqand.ecology.core.service.sys.AppealSubService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.ecoexpertise.constant.Templates;
import uz.maroqand.ecology.ecoexpertise.constant.Urls;

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

    @Autowired
    public AppealController(AppealService appealService, AppealSubService appealSubService, UserService userService, HelperService helperService) {
        this.appealService = appealService;
        this.appealSubService = appealSubService;
        this.userService = userService;
        this.helperService = helperService;
    }


    @RequestMapping(Urls.AppealUserList)
    public String appealUserListPage( Model model ) {
        model.addAttribute("appealTypeList",AppealType.getAppealTypeList());
        return Templates.AppealUserList;
    }

    @RequestMapping(value = Urls.AppealUserListAjax,produces = "application/json")
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
                    appeal.getShowUserCommentCount()
            });
        }

        result.put("data", convenientForJSONArray);
        return result;
    }

    @RequestMapping(Urls.AppealNew)
    public String appealNew( Model model ) {

        Appeal appeal = new Appeal();
        model.addAttribute("appeal", appeal);
        model.addAttribute("action_url", Urls.AppealCreate);
        model.addAttribute("cancel_url", Urls.AppealUserList);
        return Templates.AppealNew;
    }

    @RequestMapping(value = Urls.AppealCreate, method = RequestMethod.POST)
    public String appealCreate(Appeal appeal)
    {
        User user = userService.getCurrentUserFromContext();
        appealService.create(appeal,user);
        return "redirect:" + Urls.AppealUserList;
    }

    @RequestMapping(Urls.AppealEdit)
    public String appealEdit(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        Appeal appeal = appealService.getById(id, user.getId());

        if(appeal==null || appeal.getAppealStatus()!=AppealStatus.Open){
            return "redirect:" + Urls.AppealUserList;
        }
        model.addAttribute("appeal", appeal);
        model.addAttribute("action_url", Urls.AppealUpdate);
        model.addAttribute("cancel_url", Urls.AppealUserList);
        return Templates.AppealNew;
    }

    @RequestMapping(value = Urls.AppealUpdate, method = RequestMethod.POST)
    public String appealUpdate(Appeal appeal) {

        User user = userService.getCurrentUserFromContext();
        Appeal appealCurrent = appealService.getById(appeal.getId(), user.getId());
        if(appealCurrent == null || appealCurrent.getAppealStatus() != AppealStatus.Open){
            return "redirect:" + Urls.AppealUserList;
        }

        appealCurrent.setAppealType(appeal.getAppealType());
        appealCurrent.setPhone(appeal.getPhone());
        appealCurrent.setTitle(appeal.getTitle());
        appealCurrent.setDescription(appeal.getDescription());
        appealService.update(appealCurrent,user);

        return "redirect:" + Urls.AppealUserList;
    }

    @RequestMapping(value = Urls.AppealDelete)
    public String appealDelete(
            @RequestParam(name = "id") Integer id
    ) {
        User user = userService.getCurrentUserFromContext();
        Appeal appeal = appealService.getById(id, user.getId());
        if(appeal==null || appeal.getAppealStatus()!=AppealStatus.Open){
            return "redirect:" + Urls.AppealUserList;
        }
        appealService.delete(appeal,user);

        return "redirect:" + Urls.AppealUserList;
    }

    @RequestMapping(value = Urls.AppealUserView)
    public String appealUser(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        User user = userService.getCurrentUserFromContext();
        Appeal appeal = appealService.getById(id, user.getId());
        if(appeal==null){
            return "redirect:" + Urls.AppealUserList;
        }

        AppealSub appealSub = new AppealSub();
        List<AppealSub> appealSubList = appealSubService.getById(appeal.getId());

        if(appeal.getShowUserCommentCount()!=null && appeal.getShowUserCommentCount()>0){
            appeal.setShowUserCommentCount(0);
            appealService.updateCommentCount(appeal);
        }

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
        model.addAttribute("action_url", Urls.AppealSubCreate);
        model.addAttribute("cancel_url", Urls.AppealUserList);
        model.addAttribute("isAdmin", false);

        return Templates.AppealView;
    }

    @RequestMapping(value = Urls.AppealSubCreate, method = RequestMethod.POST)
    public String appealSubCreate(
            @RequestParam(name = "appealId") Integer id,
            AppealSub appealSub
    ) {
        System.out.println("appealId == " + id);
        User user = userService.getCurrentUserFromContext();
        Appeal appeal = appealService.getById(id, user.getId());
        if(appeal==null){
            return "redirect:" + Urls.AppealUserList;
        }

        appealSub.setAppealId(appeal.getId());
        appealSubService.create(appealSub,user);
        Integer count = appeal.getShowAdminCommentCount()!=null?appeal.getShowAdminCommentCount():0;
        appeal.setShowAdminCommentCount(count + 1);
        appealService.updateCommentCount(appeal);

        return "redirect:" + Urls.AppealUserView + "?id=" + appeal.getId();
    }

}
