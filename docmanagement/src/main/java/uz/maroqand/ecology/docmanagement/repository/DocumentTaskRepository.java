package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    Integer countByStatusInAndDeletedFalseAndCreatedById(Set<Integer> status,Integer userId);
    Integer countByDueDateBetweenAndStatusNotAndDeletedFalse(Date begin, Date end, Integer statusId);

    Integer countByDueDateBeforeAndStatusNotAndDeletedFalse(Date now, Integer statusId);

    Integer countByDeletedFalse();
    @Query("SELECT COUNT(d) FROM DocumentTask d LEFT JOIN Document dt ON d.documentId = dt.id WHERE dt.documentTypeId =4 AND d.deleted = FALSE AND  d.status=?1")
    Integer countByReceiverIdAndStatusForReference(Set<Integer> statuses);

    @Query(value = "SELECT COUNT(documentTask.id) from document_task documentTask where documentTask.document_id in (select document.id from Document document where document.organization_id = ?1 and document.department_id = ?2 and document.document_type_id = ?3)", nativeQuery = true)
    Long countAllDocumentTaskByDocumentType(Integer organizationId, Integer departmentId, Integer documentTypeId);

    @Query(value = "SELECT COUNT(documentTask.id) from document_task documentTask where documentTask.document_id in (select document.id from Document document where document.organization_id = ?1 and document.department_id = ?2 and document.document_type_id = ?3) and documentTask.due_date > ?4 and documentTask.due_date < ?5", nativeQuery = true)
    Long countAllDocumentTaskByDocumentTypeAndDueDateBetween(Integer organizationId, Integer departmentId, Integer documentTypeId, Date dateBegin, Date dateEnd);

    @Query(value = "SELECT COUNT(documentTask.id) from document_task documentTask where documentTask.document_id in (select document.id from Document document where document.organization_id = ?1 and document.department_id = ?2 and document.document_type_id = ?3) and documentTask.due_date < ?4", nativeQuery = true)
    Long countAllDocumentTaskByDocumentTypeAndDueDateBefore(Integer organizationId, Integer departmentId, Integer documentTypeId, Date dueDate);

    @Query(value = "SELECT COUNT(documentTask.id) from document_task documentTask where documentTask.document_id in (select document.id from Document document where document.organization_id = ?1 and document.department_id = ?2 and document.document_type_id = ?3) and documentTask.status = ?4", nativeQuery = true)
    Long countAllDocumentTaskByDocumentTypeIdAndStatus(Integer organizationId, Integer departmentId, Integer documentTypeId, Integer status);



}