package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskContent;

import java.util.List;

public interface DocumentTaskContentService {

    DocumentTaskContent getById(Integer id);

    List<DocumentTaskContent> getTaskContentList();

    Page<DocumentTaskContent> getTaskContentFilterPage(String content, Pageable pageable);

    DocumentTaskContent save(DocumentTaskContent desc);

    void delete(DocumentTaskContent taskContent);
}
