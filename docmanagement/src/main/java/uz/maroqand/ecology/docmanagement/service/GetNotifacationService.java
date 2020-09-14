package uz.maroqand.ecology.docmanagement.service;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.constant.TaskStatus;
import uz.maroqand.ecology.docmanagement.constant.TaskSubStatus;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentRepository;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskSubRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

import javax.swing.text.Document;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class GetNotifacationService {
    private final DocumentTaskSubRepository documentTaskSubRepository;
    private final UserService userService;
    private final DocumentRepository documentRepository;
    private final DocumentService documentService;
    private  final DocumentTaskService documentTaskService;
    private final DocumentTaskSubService documentTaskSubService;
    public GetNotifacationService(DocumentTaskSubService documentTaskSubService,DocumentTaskSubRepository documentTaskSubRepository,DocumentTaskService documentTaskService,UserService userService,DocumentRepository documentRepository, DocumentService documentService){
        this.documentTaskSubRepository = documentTaskSubRepository;
        this.userService = userService;
        this.documentTaskService=documentTaskService;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.documentTaskSubService=documentTaskSubService;
    }
    public Integer countIncomingByStatus(){
        User user =userService.getCurrentUserFromContext();
        Set<Integer> statuses = new LinkedHashSet<>();
        statuses.add(TaskSubStatus.New.getId());
        return documentTaskSubRepository.countByReceiverIdAndStatus(1,user.getId(), statuses);
    }
    public Long countIncomingByStatus2(){
        User user = userService.getCurrentUserFromContext();
        return documentService.countAllByStatus(DocumentTypeEnum.IncomingDocuments.getId(), DocumentStatus.New, user.getOrganizationId());
    }


    public Integer countInnerByStatus(){
        User user =userService.getCurrentUserFromContext();
        Set<Integer> statuses = new LinkedHashSet<>();
        statuses.add(TaskSubStatus.New.getId());
        return documentTaskSubRepository.countByReceiverIdAndStatus(3,user.getId(),statuses);
    }

    public Long countInnerByStatus2(){
        User user =userService.getCurrentUserFromContext();
        return documentService.countAllByStatus(DocumentTypeEnum.InnerDocuments.getId(), DocumentStatus.New, user.getOrganizationId(), user.getDepartmentId(), user.getId());
    }

    public Integer countOutgoingByStatus(){
        User user =userService.getCurrentUserFromContext();
        Set<Integer> statuses = new LinkedHashSet<>();
        statuses.add(TaskSubStatus.New.getId());
        return documentTaskSubRepository.countByReceiverIdAndStatus(2,user.getId(), statuses);
    }
    public Integer countAppealByStatus(){
        User user =userService.getCurrentUserFromContext();
        Set<Integer> statuses = new LinkedHashSet<>();
        statuses.add(TaskSubStatus.New.getId());
        return documentTaskSubRepository.countByReceiverIdAndStatus(4,user.getId(), statuses);
    }

    public Long countOutgoingByStatus2(){
       User user = userService.getCurrentUserFromContext();
        return documentService.countAllByStatus(DocumentTypeEnum.OutgoingDocuments.getId(), DocumentStatus.InProgress, user.getOrganizationId(), user.getDepartmentId(), user.getId());
    }
    public Integer countDocCheckByStatus(){
        User user =userService.getCurrentUserFromContext();
        Set<Integer> statuses = new LinkedHashSet<>();
        statuses.add(TaskStatus.Checking.getId());
        return documentTaskService.getCountTaskStatus(statuses,user.getId());
    }
    public Integer countPerformerByStatus(){
        return documentTaskSubService.getCountByStatus();
    }

    public Integer countIncomingNotByStatus(){
        User user =userService.getCurrentUserFromContext();
        return documentRepository.countByStatusAndCreatedById(DocumentStatus.New,user.getId());
    }

}
