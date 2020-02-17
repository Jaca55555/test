package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 */
public interface DocumentService {

    Document getById(Integer id);

    Document createDoc(Integer documentTypeId, Document document, User user);

    void update(Document document);

    Page<Document> findFiltered(DocFilterDTO filterDTO, Pageable pageable);

    Page<Document> getRegistrationNumber(String name, Pageable pageable);

}
