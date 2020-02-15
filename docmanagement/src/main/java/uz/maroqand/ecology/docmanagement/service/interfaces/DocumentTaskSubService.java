package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;

import java.util.Date;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 15.02.2020.
 * (uz)
 * (ru)
 */
public interface DocumentTaskSubService {

    Integer countByReceiverIdAndDueDateGreaterThanEqual(Integer receiverId, Date date);

    Integer countByReceiverIdAndDueDateLessThanEqual(Integer receiverId, Date date);

    Integer countByReceiverIdAndStatusIn(Integer receiverId, Set<Integer> statuses);

    Integer countByReceiverId(Integer receiverId);

    Page<DocumentTaskSub> findFiltered(
            Integer documentOrganizationId,
            String docRegNumber,
            String registrationNumber,
            Date dateBegin,
            Date dateEnd,
            String taskContent,
            String content,
            Integer performerId,
            Integer taskSubType,
            Integer taskSubStatus,

            Date deadlineDateBegin,
            Date deadlineDateEnd,
            Integer type,
            Set<Integer> status,
            Integer departmentId,
            Integer receiverId,
            Pageable pageable
    );

}