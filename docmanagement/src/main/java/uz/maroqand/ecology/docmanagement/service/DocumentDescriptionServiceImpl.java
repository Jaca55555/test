package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;
import uz.maroqand.ecology.docmanagement.repository.DocumentDescriptionRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentDescriptionService;

import java.util.List;

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
    public List<DocumentDescription> getDescriptionList() {
        return documentDescriptionRepository.findAll();
    }

}