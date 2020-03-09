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

import javax.swing.text.Document;

@Service
public class GetNotifacationService {
    private final DocumentTaskSubRepository documentTaskSubRepository;
    private final UserService userService;
    private final DocumentRepository documentRepository;

    public GetNotifacationService(DocumentTaskSubRepository documentTaskSubRepository,UserService userService,DocumentRepository documentRepository){
        this.documentTaskSubRepository=documentTaskSubRepository;
        this.userService=userService;
        this.documentRepository=documentRepository;
    }
    public Integer countIncomingByStatus(){
        User user =userService.getCurrentUserFromContext();
        return documentTaskSubRepository.countByReceiverIdAndStatusAndType(user.getId(), TaskSubStatus.New.getId(),DocumentTypeEnum.IncomingDocuments.getId());
    }
    public Integer countInnerByStatus(){
        User user =userService.getCurrentUserFromContext();
        return documentTaskSubRepository.countByReceiverIdAndStatusAndType(user.getId(), TaskSubStatus.New.getId(), DocumentTypeEnum.InnerDocuments.getId());
    }
    public Integer countOutgoingByStatus(){
        User user =userService.getCurrentUserFromContext();
        return documentTaskSubRepository.countByReceiverIdAndStatusAndType(user.getId(), TaskSubStatus.New.getId(), DocumentTypeEnum.OutgoingDocuments.getId());
    }
    public Integer countIncomingNotByStatus(){

        return documentRepository.countByStatus(DocumentStatus.New);

    }

}
