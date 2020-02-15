package uz.maroqand.ecology.docmanagement.service.interfaces;

import uz.maroqand.ecology.docmanagement.entity.DocumentSub;

import java.util.List;

public interface DocumentSubService {

    List<DocumentSub> findByDocumentId(Integer documentId);

    DocumentSub createDocumentSub(DocumentSub documentSub);

}
