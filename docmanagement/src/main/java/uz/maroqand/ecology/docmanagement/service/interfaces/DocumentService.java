package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.core.entity.sys.File;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 */
public interface DocumentService {

    Document getById(Integer id);

    String createTree(Document document);

    String buildTree(Document document,List<Document> documentList);

    Document createDoc(Integer documentTypeId, Document document, User user);

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

    Document updateAllparamert(Document document, Integer docSubId, Integer executeForm, Integer controlForm, Set<File> fileSet,Integer communicationToolId, Integer documentOrganizationId, Date docRegDate, User updateUser);

    HashMap<String, Object> getCountersByType(Integer type);

    List<Document> getListByAddDocId(Integer addDocId);

    Document getTopParentId(Document docId);
}
