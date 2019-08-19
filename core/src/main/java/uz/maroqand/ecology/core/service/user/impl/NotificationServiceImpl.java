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
    private ConcurrentMap<Integer, List<Notification>> newNotificationMap;

    public void initialization(){
        notificationMap = new ConcurrentHashMap<>();
        newNotificationMap = new ConcurrentHashMap<>();

        List<Notification> notificationList = notificationRepository.findByStatusAndDeletedFalse(NotificationStatus.New);
        for (Notification notification:notificationList){
            if (notification.getStatus().equals(NotificationStatus.Reviewed)){
                if (!notificationMap.containsKey(notification.getReviewerId())) {
                    notificationMap.put(notification.getReviewerId(), new LinkedList<>());
                }
                notificationMap.get(notification.getReviewerId()).add(notification);
            }else {
                if (!newNotificationMap.containsKey(notification.getReviewerId())) {
                    newNotificationMap.put(notification.getReviewerId(), new LinkedList<>());
                }
                newNotificationMap.get(notification.getReviewerId()).add(notification);
            }
        }
    }

    public void create(
            Integer reviewerId,
            NotificationType type,
            String title,
            String message,
            String url,
            Integer userId
    ){
        Notification notification = new Notification();
        notification.setType(type);
        notification.setStatus(NotificationStatus.New);

        notification.setReviewerId(reviewerId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setUrl(url);

        notification.setCreatedAt(new Date());
        notification.setCreatedById(userId);
        notificationRepository.saveAndFlush(notification);

        if (!newNotificationMap.containsKey(notification.getReviewerId())) {
            newNotificationMap.put(notification.getReviewerId(), new LinkedList<>());
        }
        newNotificationMap.get(notification.getReviewerId()).add(notification);
    }

    public List<Notification> getNotificationList(Integer reviewerId){
        if (notificationMap.containsKey(reviewerId)) {
            return notificationMap.get(reviewerId);
        }
        return new LinkedList<>();
    }

    public List<Notification> getNewNotificationList(Integer reviewerId){
        //get and clear
        List<Notification> notificationList = new LinkedList<>();
        if (newNotificationMap.containsKey(reviewerId)) {
            notificationList = newNotificationMap.get(reviewerId);
        }
        newNotificationMap.put(reviewerId, new LinkedList<>());

        //put
        if (notificationList.size()>0 && !notificationMap.containsKey(reviewerId)) {
            notificationMap.put(reviewerId, new LinkedList<>());
            notificationMap.get(reviewerId).addAll(notificationList);
        }

        return notificationList;
    }

}