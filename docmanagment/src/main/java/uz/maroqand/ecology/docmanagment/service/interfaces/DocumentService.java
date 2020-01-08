package uz.maroqand.ecology.docmanagment.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.docmanagment.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagment.entity.Document;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
public interface DocumentService {

    public Page<Document> findFiltered(DocFilterDTO filterDTO, Pageable pageable);

    public Document getById(Integer id);

    public Document createDoc(Document document);
}
