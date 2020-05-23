package uz.maroqand.ecology.cabinet.controller.sys;

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
import uz.maroqand.ecology.cabinet.constant.sys.SysTemplates;
import uz.maroqand.ecology.cabinet.constant.sys.SysUrls;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.dto.user.NotificationDto;
import uz.maroqand.ecology.core.entity.user.Notification;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 24.06.2019.
 * (uz)
 * (ru)
 */
@Controller
public class NotificationController {

    private NotificationService notificationService;
    private HelperService helperService;
    private UserService userService;

    @Autowired
    public NotificationController(UserService userService, NotificationService notificationService, HelperService helperService) {
        this.notificationService = notificationService;
        this.helperService = helperService;
        this.userService = userService;
    }

    @RequestMapping(value = SysUrls.NotificationShow, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getToastr(
    ) {
        User user = userService.getCurrentUserFromContext();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        HashMap<String, Object> result = new HashMap<>();
        List<NotificationDto> notificationListShow = new LinkedList<>();
        List<NotificationDto> newNotificationListShow = new LinkedList<>();

        List<Notification> notificationList = notificationService.getNotificationList(user.getId(),NotificationType.Expertise);
        List<Notification> newNotificationList = notificationService.getNewNotificationList(user.getId(),NotificationType.Expertise);
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

        result.put("notificationList", notificationListShow);
        result.put("notificationTitle", helperService.getTranslation("sys_notification.oldNotifications",locale));
        result.put("newNotificationList", newNotificationListShow);
        result.put("newNotificationTitle", helperService.getTranslation("sys_notification.newNotifications",locale));
        return result;
    }

    @RequestMapping(value = SysUrls.NotificationShowAfter, method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getShowAfter() {
        User user = userService.getCurrentUserFromContext();
        notificationService.viewNewNotificationList(user.getId(),NotificationType.Expertise);
        HashMap<String, Object> result = new HashMap<>();
        return result;
    }

    @RequestMapping(value = SysUrls.NotificationList)
    public String getNotificationPage() {

        return SysTemplates.NotificationList;
    }

    @RequestMapping(value = SysUrls.NotificationListAjax, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public HashMap<String,Object> getNotificationList(
            @RequestParam(name = "dateBegin", required = false) String dateBeginStr,
            @RequestParam(name = "dateEnd", required = false) String dateEndStr,
            Pageable pageable
    ) {
        User user = userService.getCurrentUserFromContext();
        HashMap<String,Object> result = new HashMap<>();

        Page<Notification> notificationPage = notificationService.findFiltered(dateBeginStr, dateEndStr, user.getId(), null, NotificationType.Expertise,pageable);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        List<Notification> notificationList = notificationPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(notificationList.size());
        for (Notification notification : notificationList){
            convenientForJSONArray.add(new Object[]{
                    notification.getId(),
                    notification.getType(),
                    notification.getStatus(),
                    !notification.getTitle().isEmpty()?helperService.getTranslation(notification.getTitle(),locale):"",
                    notification.getApplicationNumber()!=null?(notification.getApplicationNumber() + ": " +helperService.getTranslation(notification.getMessage(),locale)):helperService.getTranslation(notification.getMessage(),locale),
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
