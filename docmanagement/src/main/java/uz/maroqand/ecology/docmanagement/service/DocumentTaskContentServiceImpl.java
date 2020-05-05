package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskContent;
import uz.maroqand.ecology.docmanagement.repository.DocumentDescriptionRepository;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskContentRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentDescriptionService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskContentService;

import java.util.List;
import java.util.Optional;



@Service
public class DocumentTaskContentServiceImpl implements DocumentTaskContentService {

    private final DocumentTaskContentRepository documentTaskContentRepository;

    @Autowired
    public DocumentTaskContentServiceImpl(DocumentTaskContentRepository documentTaskContentRepository) {
        this.documentTaskContentRepository = documentTaskContentRepository;
    }

    @Override
    public DocumentTaskContent getById(Integer id) {
        Optional<DocumentTaskContent> response = documentTaskContentRepository.findById(id);
        return response.orElse(null);
    }

    @Override
    public List<DocumentTaskContent> getTaskContentList() {
        return documentTaskContentRepository.findAll();
    }

    @Override
    public Page<DocumentTaskContent> getTaskContentFilterPage(String content, Pageable pageable) {
        return documentTaskContentRepository.findAllByContentContainingOrderByIdDesc(content, pageable);
    }

    @Override
    public DocumentTaskContent save(DocumentTaskContent desc){
        return documentTaskContentRepository.save(desc);
    }

    @Override
    public void delete(DocumentTaskContent taskContent) {
        documentTaskContentRepository.delete(taskContent);
    }
}
