package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.entity.user.Position;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;


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

    Document getLastOutDocument(Integer organizationId);

    String createTree(Document document);

    String buildTree(Document document,List<Document> documentList);

    Document createDoc(Integer documentTypeId, Document document, User user);

    //saveOrNot=1 save positionLastnumber
    String getOutDocNumber(Integer positionId, Integer departmentId,Integer saveOrNot);

    Document createDoc2(Integer documentTypeId, DocumentStatus status, Document document, User user);
    Document createReference(Integer documentTypeId, Document document, User user);
    void update(Document document);

    Page<Document> findFiltered(DocFilterDTO filterDTO, Integer organizationId, Pageable pageable);

    Page<Document> getRegistrationNumber(String name,Integer organizationId, Pageable pageable);

    Long countAll(Integer documentTypeId, Integer organizationId);

    Long countAllByStatus(Integer typeId, DocumentStatus status, Integer organizationId);

    Long countAllTodaySDocuments(Integer docTypeId, Integer organizationId);

    Long countAllWhichHaveAdditionalDocuments(Integer documentTypeId, Integer organizationId);

    Long countAll(Integer documentTypeId, Integer organizationId, Integer departmentId, Integer performerId);

    Long countAllByStatus(Integer typeId, DocumentStatus status, Integer organizationId, Integer departmentId, Integer performerId);

    Long countAllTodaySDocuments(Integer docTypeId, Integer organizationId, Integer departmentId, Integer performerId);

    Long  countAllWhichHaveAdditionalDocuments(Integer documentTypeId, Integer organizationId, Integer departmentId, Integer performerId);

    Document updateAllParameters(Document document, Integer docSubId, Integer executeForm, Integer controlForm, Set<File> fileSet,Integer communicationToolId, Integer documentOrganizationId, Date docRegDate, User updateUser);

    Date getCastedDate();

    HashMap<String, Object> getCountersByType(Integer type);

    List<Document> getListByAddDocId(Integer addDocId);

    Document getTopParentId(Document docId);

    Page<Document> findAllByDocumentTypeIn(List<Integer> types, Pageable pageable);

    Page<Document> findFiltered(
            Integer organizationId,
            Integer documentTypeId,

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
            Boolean specialControll,
            Pageable pageable
    );
}
