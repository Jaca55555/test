package uz.maroqand.ecology.core.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.user.NotificationStatus;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.Notification;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.user.NotificationRepository;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by Utkirbek Boltaev on 24.06.2019.
 * (uz)
 * (ru)
 */

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final ClientService clientService;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, UserService userService, ClientService clientService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.clientService = clientService;
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
    public void createForRegContract(Integer tin, NotificationType type, String title, Integer applicationNumber, String message, Integer userId) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setStatus(NotificationStatus.New);

        notification.setTin(tin);
        notification.setTitle(title);
        notification.setApplicationNumber(applicationNumber);
        notification.setMessage(message);

        notification.setCreatedAt(new Date());
        notification.setCreatedById(userId);
        notificationRepository.saveAndFlush(notification);
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

    @Override
    public void confirmContractRegApplication(Integer regId) {
        Notification notification = getByRegApplicationConfirm(regId);
        if (notification!=null){
            notification.setStatus(NotificationStatus.Reviewed);
            notificationRepository.save(notification);
        }
    }

    @Override
    public Notification getByRegApplicationConfirm(Integer regId) {
        return notificationRepository.findByApplicationNumberAndStatusAndTypeAndDeletedFalse(regId,NotificationStatus.New,NotificationType.RegContract);
    }

    public List<Notification> getNotificationList(Integer reviewerId, NotificationType notificationType){
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

    @Override
    public List<Notification> getListByUser(User user) {
        List<Notification> notifications = new ArrayList<>();
        if (user.getLeTin()==null && user.getTin()==null){
            return notifications;
        }
        List<Notification> notificationList = new ArrayList<>();
        if (user.getTin()!=null){
            notifications = notificationRepository.findByTinAndStatusAndDeletedFalse(user.getTin(),NotificationStatus.New);
            if (notifications.size()>0){
                notificationList.addAll(notifications);
            }
        }
        if (user.getLeTin()!=null){
            notifications = notificationRepository.findByTinAndStatusAndDeletedFalse(user.getLeTin(),NotificationStatus.New);
            if (notifications.size()>0){
                notificationList.addAll(notifications);
            }
        }
        return notificationList;
    }

    @Override
    public Integer getNotificationRegContract() {
        User user = userService.getCurrentUserFromContext();
        if (user.getTin()==null && user.getLeTin()==null) return 0;
        List<Notification> notificationList = getListByUser(user);
        return notificationList!=null?notificationList.size():0;
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
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

}