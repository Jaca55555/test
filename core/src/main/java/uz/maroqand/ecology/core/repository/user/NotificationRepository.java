package uz.maroqand.ecology.core.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.constant.user.NotificationStatus;
import uz.maroqand.ecology.core.entity.user.Notification;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 24.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer>, JpaSpecificationExecutor<Notification> {

    List<Notification> findByStatusAndDeletedFalseOrderByIdDesc(NotificationStatus status, Pageable pageable);

    Page<Notification> findByReviewerIdAndDeletedFalse(Integer reviewerId, Pageable pageable);

}
