package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.telegram.SendQueryType;
import uz.maroqand.ecology.docmanagement.constant.TaskSubStatus;
import uz.maroqand.ecology.docmanagement.dto.StaticInnerInTaskSubDto;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
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
    List<DocumentTaskSub> getByDepartmentId(Integer departmentId);
    Integer getCountByStatus();
    Integer getCountByDueDate();
    DocumentTaskSub createNewSubTask(Integer level, Document document, Integer taskId, String content, Date dueDate, Integer type, Integer senderId, Integer receiverId, Integer departmentId);
    Integer countAllByStatusAndDepartmentId(Integer status,Integer departmentId);
    Integer countAllByDueDateAndDepartmentId(Date date,Integer departmentId);
    Integer countAllByDueDate1AndDepartmentId(Date date,Integer departmentId);
    DocumentTaskSub update(DocumentTaskSub taskSub);
    Integer countAllByDepartmentId(Integer departmentId);

    String buildTree(Integer level,List<DocumentTaskSub> documentTaskSubList, Map<Integer,DocumentTaskSub> taskSubMap,String locale);

    List<DocumentTaskSub> getListByDocId(Integer docId);

    List<DocumentTaskSub> getListByTaskIdAndLevel(Integer docId,Integer level);

    void allTaskSubCompleteGetTaskId(DocumentTask task, Integer userId);
    void allTaskSubRejectedGetTaskId(DocumentTask task,Integer userId);

    List<DocumentTaskSub> getListByDocIdAndTaskId(Integer docId,Integer taskId);

    Page<DocumentTaskSub> findFiltered(
            Integer organizationId,
            List<Integer> documentTypeId,
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
            Boolean specialControl,
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
            Boolean specialControl,
            Pageable pageable
    );

    //statistics
    Integer countByReceiverIdAndDueDateGreaterThanEqual(Integer receiverId, Date date);

    Integer countByReceiverIdAndDueDateLessThanEqual(Integer receiverId, Date date);

    Integer countByReceiverIdAndStatusIn(Integer typeId,Integer receiverId, Set<Integer> statuses,Integer taskSubtypeId);
    Integer countByReceiverIdAndStatus(Integer receiverId, Set<Integer> statuses);

    Integer countByReceiverId(Integer receiverId);

    StaticInnerInTaskSubDto countAllByTypeAndReceiverId(Integer documentTypeId, Integer receiverId);
    List<DocumentTaskSub> getByDocumentType(Integer documentTypeId,Integer receiverId);
    Integer countByReceiverIdAll(Integer receiverId);
    Integer countByReceiverIdAndDueDateLessThanEqualFor(Integer receiverId,Date now);
    List<DocumentTaskSub> getByDuedate(Date date,Date date1,Integer receiverId);
    String getUrl(Document document,Integer  taskSubId);

    List<DocumentTaskSub> getAllByReceiverIdAndStatuses(Integer receiverId, Set<Integer> statusSet);

    String getMessageText(Integer telegramUserId, SendQueryType sendQueryType);
}