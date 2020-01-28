package uz.maroqand.ecology.cabinet.controller.sys;

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
import uz.maroqand.ecology.cabinet.constant.sys.SysTemplates;
import uz.maroqand.ecology.cabinet.constant.sys.SysUrls;
import uz.maroqand.ecology.core.constant.sys.AppealStatus;
import uz.maroqand.ecology.core.constant.sys.AppealType;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.entity.sys.Appeal;
import uz.maroqand.ecology.core.entity.sys.AppealSub;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.AppealService;
import uz.maroqand.ecology.core.service.sys.AppealSubService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class AppealAdminController {
    private final AppealService appealService;
    private final AppealSubService appealSubService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final HelperService helperService;

    @Autowired
    public AppealAdminController(AppealService appealService, AppealSubService appealSubService, UserService userService, NotificationService notificationService, HelperService helperService) {
        this.appealService = appealService;
        this.appealSubService = appealSubService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.helperService = helperService;
    }

    @RequestMapping(SysUrls.AppealAdminList)
    public String appealUserListPage(Model model ) {
        model.addAttribute("appealTypeList",AppealType.getAppealTypeList());
        return SysTemplates.AppealAdminList;
    }

    @RequestMapping(value = SysUrls.AppealAdminListAjax,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

        Page<Appeal> appealPage = appealService.findFiltered(null,type,title,dateBegin,dateEnd,status,null, pageable);

        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", appealPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", appealPage.getTotalElements()); //Filtered elements

        List<Appeal> appealList = appealPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(appealList.size());

        for(Appeal appeal: appealList) {
            convenientForJSONArray.add(new Object[]{
                    appeal.getId(),
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

    @RequestMapping(value = SysUrls.AppealAdminView)
    public String appealAdminView(
            @RequestParam(name = "id") Integer id,
            Model model
    ) {
        Appeal appeal = appealService.findById(id);
        if(appeal==null){
            return "redirect:" + SysUrls.AppealAdminList;
        }

        AppealSub appealSub = new AppealSub();
        List<AppealSub> appealSubList = appealSubService.getById(appeal.getId());

        //UPDATE MAP
        appealService.updateByUserId(appeal.getCreatedById());

        model.addAttribute("createdBy",userService.findById(appeal.getCreatedById()));
        model.addAttribute("appealSub", appealSub);
        model.addAttribute("appealSubList", appealSubList);
        model.addAttribute("appeal",appeal);
        model.addAttribute("action_url", SysUrls.AppealAdminSubCreate);
        model.addAttribute("cancel_url", SysUrls.AppealAdminList);
        model.addAttribute("isAdmin", true);

        return SysTemplates.AppealAdminView;
    }

    @RequestMapping(value = SysUrls.AppealAdminSubCreate, method = RequestMethod.POST)
    public String appealSubCreate(
            @RequestParam(name = "appealId") Integer id,
            @RequestParam(name = "complete",required = false)Integer complete,
            AppealSub appealSub
    ) {
        System.out.println("appealId == " + id);
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        Appeal appeal = appealService.findById(id);
        if(appeal==null){
            return "redirect:" + SysUrls.AppealAdminList;
        }
        if(complete!=null && complete==0){
            appeal.setAppealStatus(AppealStatus.InProgress);
            appealSub.setInProgress(true);
        }else if(complete!=null && complete==1){
            appeal.setAppealStatus(AppealStatus.Closed);
            appealSub.setClosed(true);
        }
        appealSub.setAppealId(appeal.getId());
        appealSubService.update(appealSub,user);
        Integer count = appeal.getShowAdminCommentCount()!=null?appeal.getShowAdminCommentCount():0;
        appeal.setShowAdminCommentCount(count + 1);
        appealService.updateCommentCount(appeal);

        notificationService.create(
                appeal.getCreatedById(),
                NotificationType.Expertise,
                helperService.getTranslation("sys_notification.new", locale),
                "Sizning " + appeal.getId() + " raqamli murojaatingizga izox qoldirildi.",
                "/reg/appeal/view?id=" + appeal.getId(),
                user.getId()
        );

        return "redirect:" + SysUrls.AppealAdminView + "?id=" + appeal.getId();
    }
}
