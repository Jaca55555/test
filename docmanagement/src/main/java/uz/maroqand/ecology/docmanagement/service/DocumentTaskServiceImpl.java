package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.dto.IncomingRegFilter;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskService;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 11.02.2020
 */

@Service
public class DocumentTaskServiceImpl implements DocumentTaskService
{
    private final DocumentTaskRepository taskRepository;

    @Autowired
    public DocumentTaskServiceImpl(DocumentTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public DocumentTask getById(Integer id) {
        Optional<DocumentTask> task = taskRepository.findById(id);
        return task.orElse(null);
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

    @Override
    public DocumentTask createNewTask(Integer docId, Integer status, String context, Date dueDate, Integer chiefId, Integer createdById) {
        DocumentTask documentTask = new DocumentTask();
        documentTask.setDocumentId(docId);
        documentTask.setStatus(status);
        documentTask.setContent(context.trim());
        documentTask.setDueDate(dueDate);
        documentTask.setChiefId(chiefId);
        documentTask.setCreatedAt(new Date());
        documentTask.setCreatedById(createdById);
        documentTask.setDeleted(Boolean.FALSE);
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

            /*if (docRegNumber != null) {
                predicates.add(criteriaBuilder.like(root.get("document").<String>get("docRegNumber"), "%" + docRegNumber + "%"));
            }
            if (registrationNumber != null) {
                predicates.add(criteriaBuilder.like(root.get("document").<String>get("registrationNumber"), "%" + registrationNumber + "%"));
            }

            if (dateBegin != null && dateEnd == null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), dateBegin));
            }
            if (dateEnd != null && dateBegin == null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), dateEnd));
            }
            if (dateBegin != null && dateEnd != null) {
                predicates.add(criteriaBuilder.between(root.get("document").get("registrationDate").as(Date.class), dateBegin, dateEnd));
            }
            if (content != null) {
                predicates.add(criteriaBuilder.like(root.get("document").<String>get("content"), "%" + content + "%"));
            }

            if (taskContent != null) {
                predicates.add(criteriaBuilder.like(root.<String>get("content"), "%" + taskContent + "%"));
            }
            if (performerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("task").get("performerId"), performerId));
            }
            if (taskSubType != null) {
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