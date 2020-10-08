package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.constant.TaskSubStatus;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 14.02.2020.
 * (uz)
 */
@Repository
public interface DocumentTaskSubRepository extends JpaRepository<DocumentTaskSub, Integer>, JpaSpecificationExecutor<DocumentTaskSub> {

    List<DocumentTaskSub> findByDocumentIdAndDeletedFalseOrderByIdAsc(Integer documentId);

    List<DocumentTaskSub> findByReceiverIdAndDeletedFalseOrderByIdAsc(Integer receiverId);
//    getAllByReceiverIdAndStatuses
    List<DocumentTaskSub> findByReceiverIdAndStatusInAndDeletedFalseOrderByIdAsc(Integer receiverId, Set<Integer> statusSet);

    List<DocumentTaskSub> findByTaskIdAndDeletedFalse(Integer documentId);
//    @Query("SELECT COUNT(d) FROM DocumentTaskSub d LEFT JOIN User u ON d.receiverId = u.id WHERE d.status =?1 AND d.deleted = FALSE AND u.departmentId=?2")
    Integer countAllByStatusAndDepartmentId(Integer status,Integer departmentId);
    @Query("SELECT COUNT(d) FROM DocumentTaskSub d WHERE d.dueDate<?1 AND d.departmentId=?2")
    Integer countAllByDueDateAndDepartmentId(Date date,Integer departmentId);
    @Query("SELECT d FROM DocumentTaskSub d WHERE d.dueDate<?1 AND d.dueDate>?2 AND d.receiverId=?3 AND d.status<>6 ORDER BY d.dueDate asc")
    List<DocumentTaskSub> getAllByDueDateAndReceiverId(Date date,Date date1,Integer receiverId);

    @Query("SELECT COUNT(d) FROM DocumentTaskSub d WHERE d.dueDate<?1 AND d.departmentId=?2")
    Integer countAllByDueDate1AndDepartmentId(Date date,Integer departmentId);
    Integer countAllByDepartmentIdAndDeletedFalse(Integer departmentId);
    List<DocumentTaskSub> findByTaskIdAndLevelAndDeletedFalse(Integer documentId,Integer level);

    DocumentTaskSub findByIdAndDeletedFalse(Integer id);
    DocumentTaskSub findByDepartmentIdAndDeletedFalse(Long departmentId);

    Integer countByReceiverIdAndDueDateLessThanEqual(Integer receiverId, Date date);//Муддати якинлашаётган

    Integer countByReceiverIdAndDueDateGreaterThanEqual(Integer receiverId, Date date);//Муддати кеччикан
//    @Query("SELECT COUNT(d) FROM DocumentTaskSub d LEFT JOIN Document dt ON d.documentId = dt.id WHERE dt.documentTypeId =?1 AND d.deleted = FALSE AND d.receiverId=?2 AND d.status=?3")
//    Integer countByReceiverIdAndStatusIn(Integer typeId,Integer receiverId, Set<Integer> statuses);//Янги хатлар, Жараёндаги, Ижро этилган;//Янги хатлар, Жараёндаги, Ижро этилган
    @Query("SELECT COUNT(d) FROM DocumentTaskSub d LEFT JOIN Document dt ON d.documentId = dt.id WHERE dt.documentTypeId = :d_id AND d.deleted = FALSE AND d.receiverId= :receiverId AND d.status IN :statuses AND d.type= :typeId")
    Integer countByReceiverIdAndStatus(@Param("d_id") Integer d_id, @Param("receiverId") Integer receiverId, @Param("statuses") Set<Integer> statuses,@Param("typeId") Integer taskSubTypeId);//Янги хатлар, Жараёндаги, Ижро этилган
    Integer countByStatus( Set<Integer> statuses);
    Integer countByReceiverId(Integer receiverId);//Жами кирувчи хатлар

    DocumentTaskSub findByReceiverIdAndDocumentIdAndDeletedFalse(Integer userId, Integer docId);
    DocumentTaskSub findByDepartmentIdAndDeletedFalse(Integer departmentId);

    List<DocumentTaskSub> findByDocumentIdAndTaskIdAndDeletedFalseOrderByIdAsc(Integer docId, Integer taskId);
    List<DocumentTaskSub> findByDepartmentId(Integer departmentId);
    List<DocumentTaskSub> findByStatusAndReceiverIdOrderByCreatedAtDesc(Integer documentTypeStatusId,Integer receiverId);
    @Query("SELECT COUNT(d) FROM DocumentTaskSub d LEFT JOIN Document dt ON d.documentId = dt.id WHERE dt.documentTypeId =4 AND d.deleted = FALSE AND d.receiverId=?1 AND d.status=?2")
    Integer countByReceiverIdAndStatus(Integer receiverId, Set<Integer> statuses);
    @Query("SELECT COUNT(d) FROM DocumentTaskSub d LEFT JOIN Document dt ON d.documentId = dt.id WHERE dt.documentTypeId =4 AND d.deleted = FALSE AND d.receiverId=?1")
    Integer countByReceiverIdAll(Integer receiverId);

    @Query("SELECT COUNT(d) FROM DocumentTaskSub d LEFT JOIN Document dt ON d.documentId = dt.id WHERE dt.documentTypeId =4 AND d.deleted = FALSE AND d.receiverId=?1 AND DATEDIFF(d.dueDate,now=?2)<1")
    Integer countByReceiverIdAndDueDateLessThanEqualFor(Integer receiverId, Date now);

}