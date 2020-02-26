package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.constant.TaskStatus;
import uz.maroqand.ecology.docmanagement.dto.IncomingRegFilter;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskService;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 11.02.2020
 */

@Service
public class DocumentTaskServiceImpl implements DocumentTaskService{

    private final DocumentTaskRepository taskRepository;
    private final DocumentService documentService;

    @Autowired
    public DocumentTaskServiceImpl(DocumentTaskRepository taskRepository, DocumentService documentService) {
        this.taskRepository = taskRepository;
        this.documentService = documentService;
    }

    @Override
    public DocumentTask getById(Integer id) {
        Optional<DocumentTask> task = taskRepository.findById(id);
        return task.orElse(null);
    }

    @Override
    public DocumentTask getByIdAndDocumentId(Integer id, Integer docId) {
        return taskRepository.findByIdAndDocumentIdAndDeletedFalse(id,docId);
    }

    @Override
    public List<DocumentTask> getByDocumetId(Integer docId) {
        return taskRepository.findByDocumentId(docId);
    }

    @Override
    public List<DocumentTask> getByStatusNotInactive() {
        return null;
    }

    @Override
    public DocumentTask create(DocumentTask task) {
        return taskRepository.save(task);
    }

    public Integer countNew() {
        Set<Integer> status = new HashSet<>();
        status.add(TaskStatus.Initial.getId());
        status.add(TaskStatus.New.getId());
        return taskRepository.countByStatusInAndDeletedFalse(status);
    }

    public Integer countInProcess() {
        Set<Integer> status = new LinkedHashSet<>();
        status.add(TaskStatus.InProgress.getId());
        return taskRepository.countByStatusInAndDeletedFalse(status);
    }

    public Integer countNearDate() {
        Calendar calendar = Calendar.getInstance();
        Date begin = calendar.getTime();
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        Date end = calendar.getTime();
        return taskRepository.countByDueDateBetweenAndStatusNotAndDeletedFalse(begin, end, TaskStatus.Complete.getId());
    }

    public Integer countExpired() {
        return taskRepository.countByDueDateBeforeAndStatusNotAndDeletedFalse(new Date(), TaskStatus.Complete.getId());
    }

    public Integer countExecuted() {
        Set<Integer> status = new LinkedHashSet<>();
        status.add(TaskStatus.Complete.getId());
        return taskRepository.countByStatusInAndDeletedFalse(status);
    }

    public Integer countTotal() {
        return taskRepository.countByDeletedFalse();
    }

    @Override
    public DocumentTask createNewTask(Document doc, Integer status, String context, Date dueDate, Integer chiefId, Integer createdById) {
        DocumentTask documentTask = new DocumentTask();
        documentTask.setDocumentId(doc.getId());
        documentTask.setStatus(status);
        documentTask.setContent(context.trim());
        documentTask.setDueDate(dueDate);
        documentTask.setChiefId(chiefId);
        documentTask.setCreatedAt(new Date());
        documentTask.setCreatedById(createdById);
        documentTask.setDeleted(Boolean.FALSE);

        if (doc.getStatus().equals(DocumentStatus.New)){
            doc.setStatus(DocumentStatus.InProgress);
            documentService.update(doc);
        }
        return taskRepository.save(documentTask);
    }

    @Override
    public DocumentTask update(DocumentTask task) {
        return taskRepository.save(task);
    }

    public DocumentTask getTaskByUser(Integer docId, Integer userId) {
        return taskRepository.findByDocumentIdAndChiefId(docId, userId);
    }

    @Override
    public Page<DocumentTask> findFiltered(
            IncomingRegFilter incomingRegFilter,

            Date deadlineDateBegin,
            Date deadlineDateEnd,
            Integer type,
            Set<Integer> status,
            Integer departmentId,
            Integer receiverId,
            Pageable pageable
    ) {
        return taskRepository.findAll(getSpesification(
                incomingRegFilter, deadlineDateBegin, deadlineDateEnd, type, status, departmentId, receiverId), pageable);
    }

    private static Specification<DocumentTask> getSpesification(
            final IncomingRegFilter incomingRegFilter,

            final Date deadlineDateBegin,
            final Date deadlineDateEnd,
            final Integer type,
            final Set<Integer> statuses,
            final Integer departmentId,
            final Integer receiverId
    ) {
        return (Specification<DocumentTask>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            if (incomingRegFilter.getDocRegNumber() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("docRegNumber")), "%" + incomingRegFilter.getDocRegNumber().toLowerCase() + "%"));
            }
            if (incomingRegFilter.getRegistrationNumber() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("registrationNumber")), "%" + incomingRegFilter.getRegistrationNumber().toLowerCase() + "%"));
            }

            if (incomingRegFilter.getDateBegin() != null && incomingRegFilter.getDateEnd() == null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), incomingRegFilter.getDateBegin()));
            }
            if (incomingRegFilter.getDateEnd() != null && incomingRegFilter.getDateBegin() == null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), incomingRegFilter.getDateEnd()));
            }
            if (incomingRegFilter.getDateBegin() != null && incomingRegFilter.getDateEnd() != null) {
                predicates.add(criteriaBuilder.between(root.get("document").get("registrationDate").as(Date.class), incomingRegFilter.getDateBegin(), incomingRegFilter.getDateEnd()));
            }
            if (incomingRegFilter.getContent() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("content")), "%" + incomingRegFilter.getContent().toLowerCase() + "%"));
            }
            if (incomingRegFilter.getTaskContent() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("content")), "%" + incomingRegFilter.getTaskContent() + "%"));
            }
            if (incomingRegFilter.getPerformerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("task").get("performerId"), incomingRegFilter.getPerformerId()));
            }
            /*if (taskSubType != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (taskSubStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), taskSubStatus));
            }*/


            if (deadlineDateBegin != null && deadlineDateEnd == null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate").as(Date.class), deadlineDateBegin));
            }
            if (deadlineDateEnd != null && deadlineDateBegin == null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate").as(Date.class), deadlineDateEnd));
            }
            if (deadlineDateBegin != null && deadlineDateEnd != null) {
                predicates.add(criteriaBuilder.between(root.get("dueDate").as(Date.class), deadlineDateBegin, deadlineDateEnd));
            }

            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (statuses != null) {
                predicates.add(criteriaBuilder.in(root.get("status")).value(statuses));
            }
            if (departmentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("departmentId"), departmentId));
            }
            if (receiverId != null) {
                predicates.add(criteriaBuilder.equal(root.get("receiverId"), receiverId));
            }

            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
