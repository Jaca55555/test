package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

    List<DocumentTaskSub> findByTaskIdAndDeletedFalse(Integer documentId);

    DocumentTaskSub findByIdAndDeletedFalse(Integer id);

    Integer countByReceiverIdAndDueDateLessThanEqual(Integer receiverId, Date date);//Муддати якинлашаётган

    Integer countByReceiverIdAndDueDateGreaterThanEqual(Integer receiverId, Date date);//Муддати кеччикан

    Integer countByReceiverIdAndStatusIn(Integer receiverId, Set<Integer> statuses);//Янги хатлар, Жараёндаги, Ижро этилган

    Integer countByReceiverId(Integer receiverId);//Жами кирувчи хатлар

    List<DocumentTaskSub> findByDocumentIdAndTaskIdAndDeletedFalseOrderByIdAsc(Integer docId, Integer taskId);

}