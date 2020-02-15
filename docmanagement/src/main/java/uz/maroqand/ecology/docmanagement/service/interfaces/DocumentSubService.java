package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.jpa.domain.Specification;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;

/**
 * Created by Utkirbek Boltaev on 14.02.2020.
 * (uz)
 */
public interface DocumentSubService {

    DocumentSub getByDocumentIdForIncoming(Integer documentId);

    DocumentSub create(Integer organizationId, DocumentSub documentSub, User user);

    DocumentSub update(DocumentSub documentSub, User user);

    Specification<DocumentSub> getSpecificationOrganization( final Integer documentOrganizationId );

}
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;

import java.util.List;

public interface DocumentSubService {

    List<DocumentSub> findByDocumentId(Integer documentId);

    DocumentSub createDocumentSub(DocumentSub documentSub);

}
