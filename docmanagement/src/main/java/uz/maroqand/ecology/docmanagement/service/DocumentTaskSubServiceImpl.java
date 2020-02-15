package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskSubRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.02.2020.
 * (uz)
 */

@Service
public class DocumentTaskSubServiceImpl implements DocumentTaskSubService {

    private final DocumentTaskSubRepository documentTaskSubRepository;

    @Autowired
    public DocumentTaskSubServiceImpl(DocumentTaskSubRepository documentTaskSubRepository) {
        this.documentTaskSubRepository = documentTaskSubRepository;
    }

    @Override
    public Page<DocumentTaskSub> findFiltered(
            DocFilterDTO filterDTO,
            Date deadlineDateBegin,
            Date deadlineDateEnd,
            Integer type,
            Integer status,
            Integer departmentId,
            Integer receiverId,
            Pageable pageable
    ) {
        return documentTaskSubRepository.findAll(getSpesification(filterDTO, deadlineDateBegin, deadlineDateEnd, type, status, departmentId, receiverId), pageable);
    }

    private static Specification<DocumentTaskSub> getSpesification(
            final DocFilterDTO filterDTO,
            final Date deadlineDateBegin,
            final Date deadlineDateEnd,
            final Integer type,
            final Integer status,
            final Integer departmentId,
            final Integer receiverId
    ) {
        return (Specification<DocumentTaskSub>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            if (filterDTO != null) {
                if (filterDTO.getDocumentId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), filterDTO.getDocumentId()));
                }

                if (filterDTO.getRegistrationNumber() != null) {
                    System.out.println(filterDTO.getRegistrationNumber());
                    predicates.add(criteriaBuilder.like(root.<String>get("registrationNumber"), "%" + filterDTO.getRegistrationNumber() + "%"));
                }

                Date dateBegin = DateParser.TryParse(filterDTO.getRegistrationDateBegin(), Common.uzbekistanDateFormat);
                Date dateEnd = DateParser.TryParse(filterDTO.getRegistrationDateEnd(), Common.uzbekistanDateFormat);
                if (dateBegin != null && dateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("registrationDate").as(Date.class), dateBegin));
                }
                if (dateEnd != null && dateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("registrationDate").as(Date.class), dateEnd));
                }
                if (dateBegin != null && dateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.get("registrationDate").as(Date.class), dateBegin, dateEnd));
                }

                if (filterDTO.getControlCard() != null) {
                    predicates.add(criteriaBuilder.equal(root, filterDTO.getControlCard()));
                }

                if (filterDTO.getDocumentType() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("documentTypeId"), filterDTO.getDocumentType()));
                }

                if (filterDTO.getCorrespondentType() != null) {
                    predicates.add(criteriaBuilder.equal(root, filterDTO.getCorrespondentType()));
                }

                if (filterDTO.getContent() != null) {
                    predicates.add(criteriaBuilder.like(root.join("documentDescription").<String>get("content"), "%" + filterDTO.getContent() + "%"));
                }

                if (filterDTO.getChief() != null) {
                    predicates.add(criteriaBuilder.equal(root, filterDTO.getChief()));
                }

                if (filterDTO.getExecutors() != null) {
                    predicates.add(criteriaBuilder.equal(root, filterDTO.getExecutors()));
                }

                if (filterDTO.getResolution() != null) {
                    predicates.add(criteriaBuilder.equal(root, filterDTO.getResolution()));
                }

                if (filterDTO.getExecutePath() != null) {
                    predicates.add(criteriaBuilder.equal(root, filterDTO.getExecutePath()));
                }

                if (filterDTO.getExecuteStatus() != null) {
                    predicates.add(criteriaBuilder.equal(root, filterDTO.getExecuteStatus()));
                }

                Date executeDateBegin = DateParser.TryParse(filterDTO.getExecuteDateBegin(), Common.uzbekistanDateFormat);
                Date executeDateEnd = DateParser.TryParse(filterDTO.getRegistrationDateEnd(), Common.uzbekistanDateFormat);
                if (executeDateBegin != null && executeDateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("registrationDate").as(Date.class), executeDateBegin));
                }
                if (executeDateEnd != null && executeDateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("registrationDate").as(Date.class), executeDateEnd));
                }
                if (executeDateBegin != null && executeDateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.get("registrationDate").as(Date.class), executeDateBegin, executeDateEnd));
                }

                if (filterDTO.getExecuteControlStatus() != null) {
                    predicates.add(criteriaBuilder.equal(root, filterDTO.getExecuteControlStatus()));
                }

                if (filterDTO.getInsidePurposeStatus() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("insicePurpose"), filterDTO.getInsidePurposeStatus()));
                }

                if (filterDTO.getCoExecutorStatus() != null) {
                    predicates.add(criteriaBuilder.equal(root, filterDTO.getCoExecutorStatus()));
                }

                if (filterDTO.getReplies() != null) {
                    predicates.add(criteriaBuilder.equal(root, filterDTO.getReplies()));
                }
            }


            if (deadlineDateBegin != null && deadlineDateEnd != null) {
                predicates.add(criteriaBuilder.between(root.get("dueDate").as(Date.class), deadlineDateBegin, deadlineDateEnd));
            }

            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
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