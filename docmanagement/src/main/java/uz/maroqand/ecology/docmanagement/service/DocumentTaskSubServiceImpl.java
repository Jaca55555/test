package uz.maroqand.ecology.docmanagement.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskSubRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentSubService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by Utkirbek Boltaev on 15.02.2020.
 * (uz)
 */

@Service
public class DocumentTaskSubServiceImpl implements DocumentTaskSubService {

    private final DocumentTaskSubRepository documentTaskSubRepository;
    private final DocumentSubService documentSubService;

    @Autowired
    public DocumentTaskSubServiceImpl(DocumentTaskSubRepository documentTaskSubRepository, DocumentSubService documentSubService) {
        this.documentTaskSubRepository = documentTaskSubRepository;
        this.documentSubService = documentSubService;
    }

    @Override
    public DocumentTaskSub getById(Integer id) {
        Optional<DocumentTaskSub> optional = documentTaskSubRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public Integer countByReceiverIdAndDueDateGreaterThanEqual(Integer receiverId, Date date){
        return documentTaskSubRepository.countByReceiverIdAndDueDateGreaterThanEqual(receiverId, date);
    }

    @Override
    public Integer countByReceiverIdAndDueDateLessThanEqual(Integer receiverId, Date date){
        return documentTaskSubRepository.countByReceiverIdAndDueDateLessThanEqual(receiverId, date);
    }

    @Override
    public Integer countByReceiverIdAndStatusIn(Integer receiverId, Set<Integer> statuses){
        return documentTaskSubRepository.countByReceiverIdAndStatusIn(receiverId, statuses);
    }

    @Override
    public Integer countByReceiverId(Integer receiverId){
        return documentTaskSubRepository.countByReceiverId(receiverId);
    }

    @Override
    public DocumentTaskSub getByUserAndDocId(Integer userId, Integer docId) {
        return documentTaskSubRepository.findByReceiverIdAndDocumentIdAndDeletedFalse(userId, docId);
    }

    @Override
    public DocumentTaskSub createNewSubTask(Integer docId, Integer taskId, String content, Date dueDate, Integer type, Integer senderId, Integer receiverId, Integer departmentId) {
        DocumentTaskSub documentTaskSub = new DocumentTaskSub();
        documentTaskSub.setDocumentId(docId);
        documentTaskSub.setTaskId(taskId);
        documentTaskSub.setContent(content.trim());
        documentTaskSub.setDueDate(dueDate);
        documentTaskSub.setType(type);
        documentTaskSub.setStatus(0);
        documentTaskSub.setSenderId(senderId);
        documentTaskSub.setReceiverId(receiverId);
        documentTaskSub.setDepartmentId(departmentId);
        documentTaskSub.setDeleted(Boolean.FALSE);
        documentTaskSub.setCreatedAt(new Date());
        documentTaskSub.setCreatedById(senderId);
        return documentTaskSubRepository.save(documentTaskSub);
    }

    @Override
    public DocumentTaskSub update(DocumentTaskSub taskSub) {
        taskSub.setUpdateAt(new Date());
        return documentTaskSubRepository.save(taskSub);
    }

    @Override
    public List<DocumentTaskSub> getListByDocId(Integer docId) {
        return documentTaskSubRepository.findByDocumentIdAndDeletedFalseOrderByIdAsc(docId);
    }

    @Override
    public Page<DocumentTaskSub> findFiltered(
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
    ) {
        Specification<DocumentSub> documentSubSpecification = null;
        if (documentOrganizationId != null) {
            documentSubSpecification = documentSubService.getSpecificationOrganization(documentOrganizationId);
        }
        return documentTaskSubRepository.findAll(getSpesification(
                documentSubSpecification,
                documentOrganizationId, docRegNumber, registrationNumber, dateBegin, dateEnd, taskContent, content, performerId, taskSubType, taskSubStatus,
                deadlineDateBegin, deadlineDateEnd, type, status, departmentId, receiverId), pageable);
    }

    private static Specification<DocumentTaskSub> getSpesification(
            Specification<DocumentSub> documentSubSpecification,
            final Integer documentOrganizationId,
            final String docRegNumber,
            final String registrationNumber,
            final Date dateBegin,
            final Date dateEnd,
            final String taskContent,
            final String content,
            final Integer performerId,
            final Integer taskSubType,
            final Integer taskSubStatus,

            final Date deadlineDateBegin,
            final Date deadlineDateEnd,
            final Integer type,
            final Set<Integer> statuses,
            final Integer departmentId,
            final Integer receiverId
    ) {
        return (Specification<DocumentTaskSub>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

                /*if (documentOrganizationId != null) {
                    Join<DocumentTaskSub, DocumentSub> userProd = root.join("documentId", JoinType.LEFT);
                    predicates.add(criteriaBuilder.equal(userProd.get("organizationId"), documentOrganizationId));
                }*/

                if (StringUtils.trimToNull(docRegNumber) != null) {
                    predicates.add(criteriaBuilder.like(root.get("document").<String>get("docRegNumber"), "%" + docRegNumber + "%"));
                }
                if (StringUtils.trimToNull(registrationNumber) != null) {
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
                if (StringUtils.trimToNull(content) != null) {
                    predicates.add(criteriaBuilder.like(root.get("document").<String>get("content"), "%" + content + "%"));
                }

                if (StringUtils.trimToNull(taskContent) != null) {
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
                }


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
