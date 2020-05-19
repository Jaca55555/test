package uz.maroqand.ecology.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.dto.user.NotificationDto;
import uz.maroqand.ecology.core.entity.user.Notification;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagement.constant.DocTemplates;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sadullayev Akmal on 16.05.2020
 * (uz)
 * (ru)
 */
@Controller
public class DocumentNotificationController {

    private NotificationService notificationService;
    private HelperService helperService;
    private UserService userService;

    @Autowired
    public DocumentNotificationController(
            UserService userService,
            NotificationService notificationService,
            HelperService helperService
    ) {
        this.notificationService = notificationService;
        this.helperService = helperService;
        this.userService = userService;
    }

    @RequestMapping(value = DocUrls.NotificationShow, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getNotificationShow(
    ) {
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String, Object> result = new HashMap<>();
        List<NotificationDto> notificationListShow = new LinkedList<>();
        List<NotificationDto> newNotificationListShow = new LinkedList<>();

        List<Notification> notificationList = notificationService.getNotificationList(user.getId(),NotificationType.Document);
        List<Notification> newNotificationList = notificationService.getNewNotificationList(user.getId(),NotificationType.Document);
        int count = 0;
        for (Notification notification:newNotificationList) {
            if(count>6){
                continue;
            }
            newNotificationListShow.add(new NotificationDto(notification, helperService,locale));
            count++;
        }

        for (Notification notification:notificationList) {
            if(count>6){
                continue;
            }
            notificationListShow.add(new NotificationDto(notification, helperService,locale));
            count++;
        }

        System.out.println("size==" + newNotificationListShow.size());
        result.put("notificationList", notificationListShow);
        result.put("notificationTitle", helperService.getTranslation("sys_notification.oldNotifications",locale));
        result.put("newNotificationList", newNotificationListShow);
        result.put("newNotificationTitle", helperService.getTranslation("sys_notification.newNotifications",locale));
        return result;
    }

    @RequestMapping(value = DocUrls.NotificationShowAfter, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getNotificationShowAfter() {
        User user = userService.getCurrentUserFromContext();
        notificationService.viewNewNotificationList(user.getId(),NotificationType.Document);
        HashMap<String, Object> result = new HashMap<>();
        return result;
    }

    @RequestMapping(value = DocUrls.NotificationList)
    public String getNotificationPage() {

        return DocTemplates.NotificationList;
    }

    @RequestMapping(value = DocUrls.NotificationListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getNotificationList(
            @RequestParam(name = "dateBegin", required = false) String dateBeginStr,
            @RequestParam(name = "dateEnd", required = false) String dateEndStr,
            Pageable pageable
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();

        Page<Notification> notificationPage = notificationService.findFiltered(dateBeginStr, dateEndStr, user.getId(), null, NotificationType.Document,pageable);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        List<Notification> notificationList = notificationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(notificationList.size());
        for (Notification notification : notificationList){
            convenientForJSONArray.add(new Object[]{
                    notification.getId(),
                    notification.getType(),
                    notification.getStatus(),
                    !notification.getTitle().isEmpty()?helperService.getTranslation(notification.getTitle(),locale):"",
                    notification.getRegistrationNumber()!=null?(notification.getRegistrationNumber() + ": " +helperService.getTranslation(notification.getMessage(),locale)):helperService.getTranslation(notification.getMessage(),locale),
                    notification.getUrl(),
                    notification.getCreatedAt()!=null? Common.uzbekistanDateAndTimeFormat.format(notification.getCreatedAt()):"",
                    notification.getCreatedById()!=null? helperService.getUserFullNameById(notification.getCreatedById()):""
            });
        }
        result.put("recordsTotal", notificationPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", notificationPage.getTotalElements()); //Filtered elements
        result.put("data",convenientForJSONArray);
        return result;
    }

}
