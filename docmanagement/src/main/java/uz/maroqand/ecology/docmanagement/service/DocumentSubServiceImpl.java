package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.User;
import org.springframework.data.domain.Example;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentSubRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentSubService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Join;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 14.02.2020.
 * (uz)
 */
@Service
public class DocumentSubServiceImpl implements DocumentSubService {

    private final DocumentSubRepository documentSubRepository;

    @Autowired
    public DocumentSubServiceImpl(DocumentSubRepository documentSubRepository) {
        this.documentSubRepository = documentSubRepository;
    }

    @Override
    public DocumentSub getByDocumentIdForIncoming(Integer documentId){
        List<DocumentSub> documentSubList = documentSubRepository.findByDocumentIdAndDeletedFalse(documentId);
        if(documentSubList.size() > 0){
            return documentSubList.get(0);
        }else {
            return null;
        }
    }

    @Override
    public DocumentSub create(Integer organizationId, DocumentSub documentSub, User user){
        documentSub.setDocumentId(organizationId);
        documentSub.setCreatedById(user.getId());
        documentSub.setCreatedAt(new Date());
        documentSub.setDeleted(false);
        return documentSubRepository.save(documentSub);
    }

    @Override
    public List<DocumentSub> findAll() {
        return documentSubRepository.findAll();
    }

    @Override
    public DocumentSub update(DocumentSub documentSub, User user){
        documentSub.setUpdateById(user.getId());
        documentSub.setUpdateAt(new Date());
        documentSub.setDeleted(false);
        return documentSubRepository.save(documentSub);
    }

    public Specification<DocumentSub> getSpecificationOrganization(
            final Integer documentOrganizationId
    ) {
        return new Specification<DocumentSub>() {
            @Override
            public Predicate toPredicate(Root<DocumentSub> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if (documentOrganizationId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("organizationId"), documentOrganizationId));
                }

                predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    @Override
    public List<DocumentSub> findByDocumentId(Integer documentId){
        DocumentSub doc = new DocumentSub();
        doc.setDocumentId(documentId);
        Example<DocumentSub> subExample = Example.of(doc);
        return documentSubRepository.findAll(subExample);
    }

    @Override
    public DocumentSub createDocumentSub(DocumentSub sub){
        return documentSubRepository.save(sub);
    }

    @Override
    public DocumentSub getById(Integer id) {
        return documentSubRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public Page<DocumentSub> findFiltered(
            Integer documentTypeId,
            Integer documentStatusIdToExclude,
            Integer documentOrganizationId,
            String registrationNumber,
            Date dateBegin,
            Date dateEnd,
            Integer documentViewId,
            String content,
            Integer departmentId,
            Integer performerId,
            Pageable pageable
    ){
        return documentSubRepository.findAll(filteringSpecificationForOutgoingForm(
                documentTypeId,
                documentStatusIdToExclude,
                documentOrganizationId,
                registrationNumber,
                dateBegin, dateEnd,
                documentViewId,
                content,
                departmentId,
                performerId
                ),
                pageable);
    }

    Specification<DocumentSub> filteringSpecificationForOutgoingForm(
            Integer documentTypeId,
            Integer documentStatusIdToExclude,
            Integer documentOrganizationId,
            String registrationNumber,
            Date dateBegin,
            Date dateEnd,
            Integer documentViewId,
            String content,
            Integer departmentId,
            Integer performerId
    ){  return (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new LinkedList<>();

            Join<DocumentSub, Document> joinDocument = root.join("document");

            if(documentOrganizationId != null)
                predicates.add(criteriaBuilder.equal(root.get("organizationId"), documentOrganizationId));
            if(documentTypeId != null)
                predicates.add(criteriaBuilder.equal(joinDocument.get("documentTypeId"), documentTypeId));
            if(documentStatusIdToExclude != null)
                predicates.add(criteriaBuilder.notEqual(joinDocument.get("status"), documentStatusIdToExclude));
            if(registrationNumber != null)
                predicates.add(criteriaBuilder.equal(joinDocument.get("registrationNumber"), registrationNumber));
            if(dateEnd != null)
                predicates.add(criteriaBuilder.lessThanOrEqualTo(joinDocument.get("registrationDate"), dateEnd));
            if(dateBegin != null)
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(joinDocument.get("registrationDate"), dateBegin));
            if(documentViewId != null)
                predicates.add(criteriaBuilder.equal(joinDocument.get("documentViewId"), documentViewId));
            if(content != null)
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(joinDocument.get("content")), "%" + content.toLowerCase() + "%"));
            if(departmentId != null && performerId != null) {
                Predicate p = criteriaBuilder.or(criteriaBuilder.equal(joinDocument.get("departmentId"), departmentId), criteriaBuilder.equal(joinDocument.get("performerId"), performerId));
                predicates.add(p);
            }
            Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            return overAll;
        };
    }

    @Override
    public DocumentSub findOneByDocumentId(Integer documentId){
        return documentSubRepository.findOneByDocumentId(documentId);
    }

}
