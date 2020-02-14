package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentSubRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentSubService;

import java.util.Date;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 14.02.2020.
 * (uz)
 */
@Service
public class DocumentSubServiceImpl implements DocumentSubService {

    private final DocumentSubRepository documentSubRepository;

    @Autowired
    public DocumentSubServiceImpl(DocumentSubRepository documentSubRepository) {
        this.documentSubRepository = documentSubRepository;
    }

    @Override
    public DocumentSub getByDocumentIdForIncoming(Integer documentId){
        List<DocumentSub> documentSubList = documentSubRepository.findByDocumentIdAndDeletedFalse(documentId);
        if(documentSubList.size()>0){
            return documentSubList.get(0);
        }else {
            return null;
        }
    }

    @Override
    public DocumentSub create(Integer organizationId, DocumentSub documentSub, User user){
        documentSub.setDocumentId(organizationId);
        documentSub.setCreatedById(user.getId());
        documentSub.setCreatedAt(new Date());
        documentSub.setDeleted(false);
        return documentSubRepository.save(documentSub);
    }

    @Override
    public DocumentSub update(DocumentSub documentSub, User user){
        documentSub.setUpdateById(user.getId());
        documentSub.setUpdateAt(new Date());
        documentSub.setDeleted(false);
        return documentSubRepository.save(documentSub);
    }

}