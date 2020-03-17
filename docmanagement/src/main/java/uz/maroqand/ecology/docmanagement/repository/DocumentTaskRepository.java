package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 11.02.2020
 */
@Repository
public interface DocumentTaskRepository extends DataTablesRepository<DocumentTask, Integer>, JpaRepository<DocumentTask, Integer> {

    List<DocumentTask> findByDocumentIdOrderByIdAsc(Integer docId);

    List<DocumentTask> findByChiefIdAndDeletedFalseOrderByIdAsc(Integer docId);

    DocumentTask findByDocumentIdAndChiefId(Integer docId, Integer userId);

    DocumentTask findByIdAndDocumentIdAndDeletedFalse(Integer id, Integer docId);

    Integer countByStatusInAndDeletedFalse(Set<Integer> status);

    Integer countByDueDateBetweenAndStatusNotAndDeletedFalse(Date begin, Date end, Integer statusId);

    Integer countByDueDateBeforeAndStatusNotAndDeletedFalse(Date now, Integer statusId);

    Integer countByDeletedFalse();

}