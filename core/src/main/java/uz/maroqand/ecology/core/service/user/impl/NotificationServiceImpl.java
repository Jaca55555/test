package uz.maroqand.ecology.core.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.user.NotificationStatus;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.entity.user.Notification;
import uz.maroqand.ecology.core.repository.user.NotificationRepository;
import uz.maroqand.ecology.core.service.user.NotificationService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Utkirbek Boltaev on 24.06.2019.
 * (uz)
 * (ru)
 */

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    private ConcurrentMap<Integer, List<Notification>> notificationMap;

    public void initialization(){
        notificationMap = new ConcurrentHashMap<>();

        List<Notification> notificationList = notificationRepository.findByStatus(NotificationStatus.New);
        for (Notification notification:notificationList){
            if (!notificationMap.containsKey(notification.getReviewerId())) {
                notificationMap.put(notification.getReviewerId(), new LinkedList<>());
            }
            notificationMap.get(notification.getReviewerId()).add(notification);
        }
    }

    public void create(
            Integer reviewerId,
            NotificationType type,
            String title,
            String message,
            Integer userId
    ){
        Notification notification = new Notification();
        notification.setType(type);
        notification.setStatus(NotificationStatus.New);

        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReviewerId(reviewerId);

        notification.setCreatedAt(new Date());
        notification.setCreatedById(userId);

        if (!notificationMap.containsKey(notification.getReviewerId())) {
            notificationMap.put(notification.getReviewerId(), new LinkedList<>());
        }
        notificationMap.get(notification.getReviewerId()).add(notification);
    }

    public List<Notification> getReviewerNotificationList(Integer reviewerId){
        if (notificationMap.containsKey(reviewerId)) {
            return notificationMap.get(reviewerId);
        }
        return new LinkedList<>();
    }

    //TODO remove Notification

}