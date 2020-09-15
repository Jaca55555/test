package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 14.02.2020.
 * (uz)
 */
public interface DocumentSubService {

    DocumentSub getByDocumentIdForIncoming(Integer documentId);

    DocumentSub create(Integer organizationId, DocumentSub documentSub, User user);

    DocumentSub update(DocumentSub documentSub, User user);
    List<DocumentSub> findAll();
    Specification<DocumentSub> getSpecificationOrganization( final Integer documentOrganizationId );

    List<DocumentSub> findByDocumentId(Integer documentId);

    DocumentSub createDocumentSub(DocumentSub documentSub);

    DocumentSub getById(Integer id);

    Page<DocumentSub> findFiltered(Integer documentTypeId, Integer organizationId, Integer documentStatusId, Integer documentOrganizationId, Set<Integer> documentOrganizationIds, String registrationNumber, Date dateBegin, Date dateEnd, Integer documentViewId, String content, Integer departmentId, Integer performerId, List<DocumentStatus> statuses, Boolean hasAdditionalDocument, Boolean findTodayS, Pageable pageable);

    DocumentSub findOneByDocumentId(Integer documentId);

    Specification<DocumentSub> filteringSpecificationForOutgoingForm(
            Integer documentTypeId,
            Integer organizationId,
            Integer documentStatusIdToExclude,
            Integer documentOrganizationId,
            Set<Integer> documentOrganizationIds,
            String registrationNumber,
            Date dateBegin,
            Date dateEnd,
            Integer documentViewId,
            String content,
            Integer departmentId,
            Integer performerId,
            List<DocumentStatus> statuses,
            Boolean hasAdditional,
            Boolean findTodayS
    );

    void defineFilterInputForOutgoingListTabs(Integer tabNumber, MutableBoolean hasAdditionalDocument, MutableBoolean findTodayS, List<DocumentStatus> statuses, MutableBoolean hasAdditionalNotRequired, MutableBoolean findTodaySNotRequired);

}
