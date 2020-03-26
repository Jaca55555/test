package uz.maroqand.ecology.docmanagement.service;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.constant.TaskStatus;
import uz.maroqand.ecology.docmanagement.constant.TaskSubStatus;
import uz.maroqand.ecology.docmanagement.repository.DocumentRepository;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskSubRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;

import javax.swing.text.Document;

@Service
public class GetNotifacationService {
    private final DocumentTaskSubRepository documentTaskSubRepository;
    private final UserService userService;
    private final DocumentRepository documentRepository;
    private final DocumentService documentService;


    public GetNotifacationService(DocumentTaskSubRepository documentTaskSubRepository,UserService userService,DocumentRepository documentRepository, DocumentService documentService){
        this.documentTaskSubRepository = documentTaskSubRepository;
        this.userService = userService;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
    }
    public Integer countIncomingByStatus(){
        User user =userService.getCurrentUserFromContext();
        return documentTaskSubRepository.countByReceiverIdAndStatusAndType(user.getId(), TaskSubStatus.New.getId(),DocumentTypeEnum.IncomingDocuments.getId());
    }
    public Long countIncomingByStatus2(){
        User user = userService.getCurrentUserFromContext();
        return documentService.countAllByStatus(DocumentTypeEnum.IncomingDocuments.getId(), DocumentStatus.New, user.getOrganizationId());
    }


    public Integer countInnerByStatus(){
        User user =userService.getCurrentUserFromContext();
        return documentTaskSubRepository.countByReceiverIdAndStatusAndType(user.getId(), TaskSubStatus.New.getId(), DocumentTypeEnum.InnerDocuments.getId());
    }

    public Long countInnerByStatus2(){
        User user =userService.getCurrentUserFromContext();
        return documentService.countAllByStatus(DocumentTypeEnum.InnerDocuments.getId(), DocumentStatus.New, user.getOrganizationId(), user.getDepartmentId(), user.getId());
    }

    public Integer countOutgoingByStatus(){
        User user =userService.getCurrentUserFromContext();
        return documentTaskSubRepository.countByReceiverIdAndStatusAndType(user.getId(), TaskSubStatus.New.getId(), DocumentTypeEnum.OutgoingDocuments.getId());
    }

    public Long countOutgoingByStatus2(){
       User user = userService.getCurrentUserFromContext();
        return documentService.countAllByStatus(DocumentTypeEnum.OutgoingDocuments.getId(), DocumentStatus.InProgress, user.getOrganizationId(), user.getDepartmentId(), user.getId());
    }

    public Integer countIncomingNotByStatus(){

        return documentRepository.countByStatus(DocumentStatus.New);

    }

}
