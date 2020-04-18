package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
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

    List<DocumentTaskSub> findByTaskIdAndDeletedFalse(Integer documentId);

    List<DocumentTaskSub> findByTaskIdAndLevelAndDeletedFalse(Integer documentId,Integer level);

    DocumentTaskSub findByIdAndDeletedFalse(Integer id);

    Integer countByReceiverIdAndDueDateLessThanEqual(Integer receiverId, Date date);//Муддати якинлашаётган

    Integer countByReceiverIdAndDueDateGreaterThanEqual(Integer receiverId, Date date);//Муддати кеччикан

    Integer countByReceiverIdAndStatusIn(Integer receiverId, Set<Integer> statuses);//Янги хатлар, Жараёндаги, Ижро этилган
    @Query("SELECT COUNT(d) FROM DocumentTaskSub d LEFT JOIN Document dt ON d.documentId = dt.id WHERE dt.documentTypeId =?1 AND d.deleted = FALSE AND d.receiverId=?2 AND d.status=?3 AND d.type=?4")
    Integer countByReceiverIdAndStatusAndType(Integer d_id,Integer receiverId, Set<Integer> statuses,Integer type);//Янги хатлар, Жараёндаги, Ижро этилган

    Integer countByReceiverId(Integer receiverId);//Жами кирувчи хатлар

    DocumentTaskSub findByReceiverIdAndDocumentIdAndDeletedFalse(Integer userId, Integer docId);
    List<DocumentTaskSub> findByDocumentIdAndTaskIdAndDeletedFalseOrderByIdAsc(Integer docId, Integer taskId);

    @Query("SELECT COUNT(d) FROM DocumentTaskSub d LEFT JOIN Document dt ON d.documentId = dt.id WHERE dt.documentTypeId =4 AND d.deleted = FALSE AND d.receiverId=?1 AND d.status=?2")
    Integer countByReceiverIdAndStatus(Integer receiverId, Set<Integer> statuses);
    @Query("SELECT COUNT(d) FROM DocumentTaskSub d LEFT JOIN Document dt ON d.documentId = dt.id WHERE dt.documentTypeId =4 AND d.deleted = FALSE AND d.receiverId=?1")
    Integer countByReceiverIdAll(Integer receiverId);

    @Query("SELECT COUNT(d) FROM DocumentTaskSub d LEFT JOIN Document dt ON d.documentId = dt.id WHERE dt.documentTypeId =4 AND d.deleted = FALSE AND d.receiverId=?1 AND DATEDIFF(d.dueDate,now=?2)<1")
    Integer countByReceiverIdAndDueDateLessThanEqualFor(Integer receiverId, Date now);
}