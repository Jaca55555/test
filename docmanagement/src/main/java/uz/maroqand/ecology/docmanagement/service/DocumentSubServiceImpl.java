package uz.maroqand.ecology.docmanagement.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentSubRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentSubService;

import java.util.List;

@Service
public class DocumentSubServiceImpl implements DocumentSubService {

    private final DocumentSubRepository repository;

    @Autowired
    public DocumentSubServiceImpl(DocumentSubRepository repository){
        this.repository = repository;
    }

    @Override
    public List<DocumentSub> findByDocumentId(Integer documentId){
        DocumentSub doc = new DocumentSub();
        doc.setDocumentId(documentId);
        Example<DocumentSub> subExample = Example.of(doc);
        return repository.findAll(subExample);
    }

    @Override
    public DocumentSub createDocumentSub(DocumentSub sub){
        return repository.save(sub);
    }

}
