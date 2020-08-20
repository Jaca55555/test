package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 13.02.2020.
 * (uz)
 * (ru)
 */
public interface DocumentDescriptionService {

    DocumentDescription getById(Integer id);

    List<DocumentDescription> getDescriptionList();
    List<DocumentDescription> findAllByOrganizationId(Integer organizationId);
    Page<DocumentDescription> getDescriptionFilterPage(String content,Integer organizationId, Pageable pageable);

    DocumentDescription save(DocumentDescription desc);

    void delete(DocumentDescription description);
}
