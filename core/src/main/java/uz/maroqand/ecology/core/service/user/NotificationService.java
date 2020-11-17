package uz.maroqand.ecology.core.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.entity.user.Notification;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 24.06.2019.
 * (uz)
 * (ru)
 */
public interface NotificationService {

//    void initialization();

    void confirmContractRegApplication(Integer regId);

    List<Notification> getNotificationList(Integer reviewerId,NotificationType notificationType);

    List<Notification> getNewNotificationList(Integer reviewerId,NotificationType notificationType);

    void viewNewNotificationList(Integer reviewerId,NotificationType notificationType);

    void create(
            Integer reviewerId,
            NotificationType type,
            String title,
            Integer applicationNumber,
            String message,
            String url,
            Integer userId
    );


    void createForRegContract(
            Integer tin, //type
            NotificationType type,
            String title,
            Integer applicationNumber,
            String message,
            Integer userId
    );

    void create(
            Integer reviewerId,
            NotificationType type,
            String title,
            String registrationNumber,
            String message,
            String url,
            Integer userId
    );

    Page<Notification> findFiltered(
            String dateBeginStr,
            String dateEndStr,
            Integer reviewerId,
            Integer createdById,
            NotificationType notificationType,
            Pageable pageable
    );

    List<Notification> getListByUser(User user);

    Integer getNotificationRegContract();

}
