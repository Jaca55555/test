package uz.maroqand.ecology.core.service.user;

import uz.maroqand.ecology.core.entity.user.Notification;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 24.06.2019.
 * (uz)
 * (ru)
 */
public interface NotificationService {

    void initialization();

    List<Notification> getReviewerNotificationList(Integer reviewerId);

}
