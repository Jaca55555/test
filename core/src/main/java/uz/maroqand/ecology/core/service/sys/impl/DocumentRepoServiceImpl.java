package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.sys.DocumentRepoType;
import uz.maroqand.ecology.core.entity.sys.DocumentRepo;
import uz.maroqand.ecology.core.repository.sys.DocumentRepoRepository;
import uz.maroqand.ecology.core.service.sys.DocumentRepoService;
import uz.maroqand.ecology.core.util.Captcha;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Utkirbek Boltaev on 07.10.2019.
 * (uz)
 * (ru)
 */

@Service
public class DocumentRepoServiceImpl implements DocumentRepoService {

    private final DocumentRepoRepository documentRepoRepository;

    @Autowired
    public DocumentRepoServiceImpl(DocumentRepoRepository documentRepoRepository) {
        this.documentRepoRepository = documentRepoRepository;
    }

    public DocumentRepo getDocument(Integer id) {
        if(id == null) return null;
        return documentRepoRepository.getOne(id);
    }

    public DocumentRepo getDocumentByUuid(String uuid){
        return documentRepoRepository.findByUuid(uuid);
    }

    public DocumentRepo create(DocumentRepoType type, Integer applicationId) {
        DocumentRepo documentRepo = new DocumentRepo();
        documentRepo.setType(type);
        documentRepo.setApplicationId(applicationId);

        final String uuid = UUID.randomUUID().toString();
        documentRepo.setUuid(uuid);
        documentRepo.setCode(Captcha.getVerificationCodeString());

        documentRepo.setCreatedAt(new Date());
        return documentRepoRepository.save(documentRepo);
    }

}