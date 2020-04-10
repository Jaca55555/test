package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.docmanagement.dto.StaticInnerInTaskSubDto;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 15.02.2020.
 * (uz)
 */
public interface DocumentTaskSubService {

    DocumentTaskSub getById(Integer id);

    DocumentTaskSub createNewSubTask(Integer level,Integer docId,Integer taskId, String content,Date dueDate,Integer type,Integer senderId,Integer receiverId, Integer departmentId);

    DocumentTaskSub update(DocumentTaskSub taskSub);

    String buildTree(Integer level,List<DocumentTaskSub> documentTaskSubList, Map<Integer,DocumentTaskSub> taskSubMap,String locale);

    List<DocumentTaskSub> getListByDocId(Integer docId);

    List<DocumentTaskSub> getListByTaskIdAndLevel(Integer docId,Integer level);

    void allTaskSubCompleteGetTaskId(Integer taskId);

    List<DocumentTaskSub> getListByDocIdAndTaskId(Integer docId,Integer taskId);

    Page<DocumentTaskSub> findFiltered(
            Integer organizationId,
            Integer documentTypeId,

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
            Boolean specialControll,
            Pageable pageable
    );
    Page<DocumentTaskSub> findFilter(
            Integer organizationId,
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
            Boolean specialControll,
            Pageable pageable
    );

    //statistics
    Integer countByReceiverIdAndDueDateGreaterThanEqual(Integer receiverId, Date date);

    Integer countByReceiverIdAndDueDateLessThanEqual(Integer receiverId, Date date);

    Integer countByReceiverIdAndStatusIn(Integer receiverId, Set<Integer> statuses);
    Integer countByReceiverIdAndStatus(Integer receiverId, Set<Integer> statuses);

    Integer countByReceiverId(Integer receiverId);

    StaticInnerInTaskSubDto countAllInnerByReceiverId(Integer receiverId);

    Integer countByReceiverIdAll(Integer receiverId);
    Integer countByReceiverIdAndDueDateLessThanEqualFor(Integer receiverId,Date now);
}