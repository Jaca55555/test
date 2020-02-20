package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;
import uz.maroqand.ecology.docmanagement.repository.DocumentDescriptionRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentDescriptionService;

import java.util.List;
import java.util.Optional;

/**
 * Created by Utkirbek Boltaev on 13.02.2020.
 * (uz)
 */

@Service
public class DocumentDescriptionServiceImpl implements DocumentDescriptionService {

    private final DocumentDescriptionRepository documentDescriptionRepository;

    @Autowired
    public DocumentDescriptionServiceImpl(DocumentDescriptionRepository documentDescriptionRepository) {
        this.documentDescriptionRepository = documentDescriptionRepository;
    }

    @Override
    public DocumentDescription getById(Integer id) {
        Optional<DocumentDescription> response = documentDescriptionRepository.findById(id);
        return response.orElse(null);
    }

    @Override
    public List<DocumentDescription> getDescriptionList() {
        return documentDescriptionRepository.findAll();
    }

    @Override
    public Page<DocumentDescription> getDescriptionFilterPage(String content, Pageable pageable) {
        return documentDescriptionRepository.findAllByContentContainingOrderByIdDesc(content, pageable);
    }

    @Override
    public DocumentDescription save(DocumentDescription desc){
        return documentDescriptionRepository.save(desc);
    }

    @Override
    public void delete(DocumentDescription description) {
        documentDescriptionRepository.delete(description);
    }
}
