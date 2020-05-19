package uz.maroqand.ecology.core.service.user.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.user.NotificationStatus;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.entity.user.Notification;
import uz.maroqand.ecology.core.repository.user.NotificationRepository;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

//    private ConcurrentMap<Integer, List<Notification>> notificationMap;
//    private ConcurrentMap<Integer, List<Notification>> newNotificationMap;

    /*public void initialization(){
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
    }*/

    public void create(
            Integer reviewerId,
            NotificationType type,
            String title,
            Integer applicationNumber,
            String message,
            String url,
            Integer userId
    ){
        Notification notification = new Notification();
        notification.setType(type);
        notification.setStatus(NotificationStatus.New);

        notification.setReviewerId(reviewerId);
        notification.setTitle(title);
        notification.setApplicationNumber(applicationNumber);
        notification.setMessage(message);
        notification.setUrl(url);

        notification.setCreatedAt(new Date());
        notification.setCreatedById(userId);
        notificationRepository.saveAndFlush(notification);

        /*if (!newNotificationMap.containsKey(notification.getReviewerId())) {
            newNotificationMap.put(notification.getReviewerId(), new LinkedList<>());
        }
        newNotificationMap.get(notification.getReviewerId()).add(notification);*/
    }

    @Override
    public void create(Integer reviewerId, NotificationType type, String title, String registrationNumber, String message, String url, Integer userId) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setStatus(NotificationStatus.New);

        notification.setReviewerId(reviewerId);
        notification.setTitle(title);
        notification.setRegistrationNumber(registrationNumber);
        notification.setMessage(message);
        notification.setUrl(url);

        notification.setCreatedAt(new Date());
        notification.setCreatedById(userId);
        notificationRepository.saveAndFlush(notification);

        /*if (!newNotificationMap.containsKey(notification.getReviewerId())) {
            newNotificationMap.put(notification.getReviewerId(), new LinkedList<>());
        }
        newNotificationMap.get(notification.getReviewerId()).add(notification);*/
    }

    public List<Notification> getNotificationList(Integer reviewerId,NotificationType notificationType){
        Pageable limit = PageRequest.of(0,10);
        return notificationRepository.findByReviewerIdAndStatusAndTypeAndDeletedFalseOrderByIdDesc(reviewerId, NotificationStatus.Reviewed, notificationType,limit);
        /*if (notificationMap.containsKey(reviewerId)) {
            return notificationMap.get(reviewerId);
        }
        return new LinkedList<>();*/
    }

    public List<Notification> getNewNotificationList(Integer reviewerId,NotificationType notificationType){
        Pageable limit = PageRequest.of(0,10);
        return notificationRepository.findByReviewerIdAndStatusAndTypeAndDeletedFalseOrderByIdDesc(reviewerId, NotificationStatus.New, notificationType, limit);
        //get and clear
        /*List<Notification> notificationList = new LinkedList<>();
        if (newNotificationMap.containsKey(reviewerId)) {
            notificationList = newNotificationMap.get(reviewerId);
        }*/

        //put
        /*if (notificationList.size()>0 && !notificationMap.containsKey(reviewerId)) {
            notificationMap.put(reviewerId, new LinkedList<>());
            notificationMap.get(reviewerId).addAll(notificationList);
        }*/

//        return notificationList;
    }

    public void viewNewNotificationList(Integer reviewerId,NotificationType notificationType){
        Pageable limit = PageRequest.of(0,10);
        List<Notification> notificationList = notificationRepository.findByReviewerIdAndStatusAndTypeAndDeletedFalseOrderByIdDesc(reviewerId, NotificationStatus.New,notificationType, limit);
        for (Notification notification:notificationList){
            notification.setStatus(NotificationStatus.Reviewed);
            notification.setUpdateAt(new Date());
            notificationRepository.save(notification);
        }
        //get and clear
        /*List<Notification> notificationList = new LinkedList<>();
        if (newNotificationMap.containsKey(reviewerId)) {
            notificationList = newNotificationMap.get(reviewerId);
        }
        newNotificationMap.put(reviewerId, new LinkedList<>());*/

        //put
        /*if (notificationList.size()>0 && !notificationMap.containsKey(reviewerId)) {
            notificationMap.put(reviewerId, new LinkedList<>());
            notificationMap.get(reviewerId).addAll(notificationList);
        }*/

//        return notificationList;
    }

    public Page<Notification> findFiltered(
            String dateBeginStr,
            String dateEndStr,
            Integer reviewerId,
            Integer createdById,
            NotificationType notificationType,
            Pageable pageable
    ){
        return notificationRepository.findAll(getFilteringSpecification(dateBeginStr, dateEndStr, reviewerId, createdById,notificationType), pageable);
    }

    private static Specification<Notification> getFilteringSpecification(
            final String dateBeginStr,
            final String dateEndStr,
            final Integer reviewerId,
            final Integer createdById,
            final NotificationType notificationType
    ) {
        return new Specification<Notification>() {
            @Override
            public Predicate toPredicate(Root<Notification> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                Date dateBegin = DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat);
                Date dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);
                if (dateBegin != null && dateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt").as(Date.class), dateBegin));
                }
                if (dateEnd != null && dateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt").as(Date.class), dateEnd));
                }
                if (dateBegin != null && dateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.get("createdAt").as(Date.class), dateBegin, dateEnd));
                }

                if(reviewerId != null){
                    predicates.add(criteriaBuilder.equal(root.get("reviewerId"), reviewerId));
                }
                if(createdById != null){
                    predicates.add(criteriaBuilder.equal(root.get("createdById"), createdById));
                }

                if(notificationType!=null){
                    predicates.add(criteriaBuilder.equal(root.get("type"), notificationType.ordinal()));
                }

                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

}