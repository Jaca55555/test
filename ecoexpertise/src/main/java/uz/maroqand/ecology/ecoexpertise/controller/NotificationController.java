package uz.maroqand.ecology.ecoexpertise.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.core.dto.user.NotificationDto;
import uz.maroqand.ecology.core.entity.user.Notification;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.ecoexpertise.constant.sys.SysUrls;

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

    private UserService userService;
    private NotificationService notificationService;
    private HelperService helperService;

    public NotificationController(UserService userService, NotificationService notificationService, HelperService helperService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.helperService = helperService;
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

        List<Notification> notificationList = notificationService.getNotificationList(user.getId());
        List<Notification> newNotificationList = notificationService.getNewNotificationList(user.getId());

        int count = 0;
        for (Notification notification:newNotificationList){
            if(count>7){
                continue;
            }
            newNotificationListShow.add(new NotificationDto(notification, helperService));
            count++;
        }

        for (Notification notification:notificationList){
            if(count>7){
                continue;
            }
            notificationListShow.add(new NotificationDto(notification, helperService));
            count++;
        }

        result.put("notificationList", notificationListShow);
        result.put("newNotificationList", newNotificationListShow);
        result.put("notificationTitle", helperService.getTranslation("sys_notification.oldNotifications",locale));
        result.put("newNotificationTitle", helperService.getTranslation("sys_notification.newNotifications",locale));
        return result;
    }
}
