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

    Long countAll(Integer documentTypeId, Integer organizationId);

    Long countAllByStatus(Integer typeId, DocumentStatus status, Integer organizationId);

    Long countAllTodaySDocuments(Integer docTypeId, Integer organizationId);

    Long countAllWhichHaveAdditionalDocuments(Integer documentTypeId, Integer organizationId);

    Long countAll(Integer documentTypeId, Integer organizationId, Integer departmentId);

    Long countAllByStatus(Integer typeId, DocumentStatus status, Integer organizationId, Integer departmentId);

    Long countAllTodaySDocuments(Integer docTypeId, Integer organizationId, Integer departmentId);

    Long  countAllWhichHaveAdditionalDocuments(Integer documentTypeId, Integer organizationId, Integer departmentId);
  //  Long countAllByDocumentTypeAndHasAdditionalDocument(Integer documentTypeId);

  //  Document updateAllparamert(Document document, Integer docSubId, Integer executeForm, Integer controlForm, Set<File> fileSet,Integer communicationToolId, Integer documentOrganizationId, Date docRegDate, User updateUser);

}
