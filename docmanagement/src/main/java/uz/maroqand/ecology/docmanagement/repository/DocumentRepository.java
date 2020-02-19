package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.entity.Document;

import java.util.Date;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentRepository extends DataTablesRepository<Document, Integer>, JpaRepository<Document, Integer>, JpaSpecificationExecutor<Document> {

    Document findByIdAndDeletedFalse(Integer id);

    List<Document> findAllByDeletedFalse();

    Page<Document> findByRegistrationNumberLike(String number, Pageable pageable);

    Long countAllByDocumentTypeIdAndOrganizationId(Integer typeId, Integer organizationId);

    Long countAllByDocumentTypeIdAndStatusAndOrganizationId(Integer typeId, DocumentStatus status, Integer organizationId);

    Long countAllByCreatedAtAfterAndDocumentTypeIdAndOrganizationId(Date time, Integer documentTypeId, Integer organizationId);

    Long countAllByDocumentTypeIdAndAdditionalDocumentIdNotNullAndOrganizationId(Integer documentTypeId, Integer organizationId);

    Long countAllByDocumentTypeIdAndOrganizationIdAndDepartmentId(Integer documentTypeId, Integer organizationId, Integer departmentId);

    Long countAllByDocumentTypeIdAndStatusAndOrganizationIdAndDepartmentId(Integer typeId, DocumentStatus status, Integer organizationId, Integer departmentId);

    Long countAllByCreatedAtAfterAndDocumentTypeIdAndOrganizationIdAndDepartmentId(Date time, Integer documentTypeId, Integer organizationId, Integer departmentId);

    Long countAllByDocumentTypeIdAndAdditionalDocumentIdNotNullAndOrganizationIdAndDepartmentId(Integer documentTypeId, Integer organizationId, Integer departmentId);

}