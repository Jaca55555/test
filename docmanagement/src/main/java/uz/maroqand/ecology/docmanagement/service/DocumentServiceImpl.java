package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentType;
import uz.maroqand.ecology.docmanagement.repository.DocumentRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public Document getById(Integer id) {
        return documentRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public Document createDoc(Document document) {
        document.setCreatedAt(new Date());
        document.setDeleted(Boolean.FALSE);
        return documentRepository.save(document);
    }

    @Override
    public void update(Document document) {
        document.setUpdateAt(new Date());
        documentRepository.save(document);
    }

    @Override
    public Page<Document> findFiltered(
            DocFilterDTO filterDTO,
            Pageable pageable
    ) {
        return documentRepository.findAll(getSpesification(filterDTO), pageable);
    }

    private static Specification<Document> getSpesification(final DocFilterDTO filterDTO) {
        return (Specification<Document>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            if (filterDTO != null) {
                if (filterDTO.getDocumentId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), filterDTO.getDocumentId()));
                }

                if (filterDTO.getRegistrationNumber() != null) {
                    System.out.println(filterDTO.getRegistrationNumber());
                    predicates.add(criteriaBuilder.like(root.get("registrationNumber"), "%" + filterDTO.getRegistrationNumber() + "%"));
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

                predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
    }

    @Override
    public Page<Document> getRegistrationNumber(String name, Pageable pageable) {
        return documentRepository.findAll(getSpecification(name), pageable);
    }
    @Override
    public Long countAll(Integer documentTypeId, Integer organizationId){
        return documentRepository.countAllByDocumentTypeIdAndOrganizationId(documentTypeId, organizationId);
    }

    @Override
    public Long countAllByStatus(Integer typeId, DocumentStatus status, Integer organizationId){
        return documentRepository.countAllByDocumentTypeIdAndStatusAndOrganizationId(typeId, status, organizationId);
    }

    @Override
    public Long countAllTodaySDocuments(Integer docTypeId, Integer organizationId){
        return documentRepository.countAllByCreatedAtAfterAndDocumentTypeIdAndOrganizationId(getCastedDate(),docTypeId, organizationId);
    }
    @Override
    public Long countAllWhichHaveAdditionalDocuments(Integer documentTypeId, Integer organizationId){
        return documentRepository.countAllByDocumentTypeIdAndAdditionalDocumentIdNotNullAndOrganizationId(documentTypeId, organizationId);
    }

    @Override
    public Long countAll(Integer documentTypeId, Integer organizationId, Integer departmentId){
        return documentRepository.countAllByDocumentTypeIdAndOrganizationIdAndDepartmentId(documentTypeId, organizationId, departmentId);
    }

    @Override
    public Long countAllByStatus(Integer typeId, DocumentStatus status,Integer organizationId, Integer departmentId){
        return documentRepository.countAllByDocumentTypeIdAndStatusAndOrganizationIdAndDepartmentId(typeId, status, organizationId, departmentId);
    }

    @Override
    public Long countAllTodaySDocuments(Integer docTypeId, Integer organizationId, Integer departmentId){
        return documentRepository.countAllByCreatedAtAfterAndDocumentTypeIdAndOrganizationIdAndDepartmentId(getCastedDate(), docTypeId, organizationId, departmentId);
    }

    @Override
    public  Long countAllWhichHaveAdditionalDocuments(Integer documentTypeId, Integer organizationId, Integer departmentId){
        return documentRepository.countAllByDocumentTypeIdAndAdditionalDocumentIdNotNullAndOrganizationIdAndDepartmentId(documentTypeId, organizationId, departmentId);
    }

    private static Specification<Document> getSpecification(String registrationNumber) {
        return (Specification<Document>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if(registrationNumber != null){
                predicates.add(criteriaBuilder.like(root.get("registrationNumber"), "%" + registrationNumber + "%"));
            }
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Date getCastedDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        return calendar.getTime();
    }

}