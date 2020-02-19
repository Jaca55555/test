package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;
import uz.maroqand.ecology.docmanagement.entity.DocumentType;

import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 */
public interface DocumentService {

    Document getById(Integer id);

    Document createDoc(Document document);

    void update(Document document);

    Page<Document> findFiltered(DocFilterDTO filterDTO, Pageable pageable);

    Page<Document> getRegistrationNumber(String name, Pageable pageable);

    Long countTotalByDocumentType(Integer documentTypeId);

    Long countTotalByTypeAndStatus(Integer typeId, DocumentStatus status);

    Long countAllByCreatedAtAfterAndDocumentTypeId(Date time, Integer docTypeId);

    Long countAllByDocumentTypeAndHasAdditionalDocument(Integer documentTypeId);

    Long countTotalByDocumentTypeAndDepartmentId(Integer documentTypeId, Integer departmentId);

    Long countTotalByTypeAndStatusAndDepartmentId(Integer typeId, DocumentStatus status, Integer departmentId);

    Long countAllByCreatedAtAfterAndDocumentTypeIdAndDepartmentId(Date time, Integer docTypeId, Integer departmentId);

    Long countAllByDocumentTypeAndHasAdditionalDocumentAndDepartmentId(Integer documentTypeId, Integer departmentId);
}
