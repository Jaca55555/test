package uz.maroqand.ecology.core.service.user;

import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.entity.user.Notification;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 24.06.2019.
 * (uz)
 * (ru)
 */
public interface NotificationService {

    void initialization();

    List<Notification> getNotificationList(Integer reviewerId);

    List<Notification> getNewNotificationList(Integer reviewerId);

    void create(
            Integer reviewerId,
            NotificationType type,
            String title,
            String message,
            String url,
            Integer userId
    );

}
