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

    public Page<Document> findFiltered(DocFilterDTO filterDTO, Pageable pageable);

    public List<Document> findAllActive();

    public List<DocumentDescription> getDescriptionsList();

    public Document getById(Integer id);

    public Document createDoc(Document document);

    public void update(Document document);
}
