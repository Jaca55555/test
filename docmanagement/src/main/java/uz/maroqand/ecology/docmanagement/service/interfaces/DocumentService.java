package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
public interface DocumentService {

    Page<Document> findFiltered(DocFilterDTO filterDTO, Pageable pageable);

   List<Document> findAllActive();

    List<DocumentDescription> getDescriptionsList();

    Document getById(Integer id);

    Document createDoc(Document document);

    void update(Document document);

    List<Document> getList();
}
