package uz.maroqand.ecology.docmanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.Notification;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentLog;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentLogRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentLogService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 14.02.2020
 */
@Service
public class DocumentLogServiceImpl implements DocumentLogService {

    private final DocumentLogRepository logRepository;
    private final FileService fileService;

    public DocumentLogServiceImpl(DocumentLogRepository logRepository, FileService fileService) {
        this.logRepository = logRepository;
        this.fileService = fileService;
    }

    @Override
    public DocumentLog getById(Integer id) {
        return logRepository.getOne(id);
    }

    @Override
    public List<DocumentLog> getAllByDocId(Integer docId) {
        return logRepository.findByDocumentIdOrderByIdDesc(docId);
    }

    @Override
    public List<DocumentLog> getAllByDocAndTaskSubId(Integer docId, Integer taskSubId) {
        return logRepository.findAllByDocumentIdAndTaskSubIdOrderByIdDesc(docId, taskSubId);
    }

    @Override
    public DocumentLog findFirstByDocumentIdOrderByIdDesc(Integer docId) {
        return logRepository.findFirstByDocumentIdOrderByIdDesc(docId);
    }

    @Override
    public List<DocumentLog> getAllByDocAndTaskId(Integer docId, Integer taskId) {
        return logRepository.findAllByDocumentIdAndTaskIdOrderByIdDesc(docId, taskId);
    }

    @Override
    public DocumentLog create(DocumentLog documentLog) {
        documentLog.setCreatedAt(new Date());
        return logRepository.save(documentLog);
    }

    @Override
    public DocumentLog update(DocumentLog documentLog) {
        return logRepository.save(documentLog);
    }

    @Override
    public DocumentLog createLog(DocumentLog documentLog,Integer logTypeId, List<Integer> file_ids, String beforeStatus, String beforeStatusColor, String afterStatus, String afterStatusColor, Integer createdById) {
        System.out.println("docId=" + documentLog.getDocumentId());
        System.out.println("task=" + documentLog.getTaskId());
        System.out.println("subId=" + documentLog.getTaskSubId());
        System.out.println("con=" + documentLog.getContent());
        DocumentLog documentLog1 = new DocumentLog();
        documentLog1.setContent(documentLog.getContent());
        documentLog1.setType(logTypeId);
        if (file_ids != null) {
            Set<File> files = new LinkedHashSet<>();
            for (Integer id : file_ids) {
                files.add(fileService.findById(id));
            }
            documentLog1.setContentFiles(files);
        }
        documentLog1.setDocumentId(documentLog.getDocumentId());
        documentLog1.setTaskId(documentLog.getTaskId());
        documentLog1.setTaskSubId(documentLog.getTaskSubId());
        documentLog1.setBeforeStatus(beforeStatus);
        documentLog1.setBeforeStatusColor(beforeStatusColor);
        documentLog1.setAfterStatus(afterStatus);
        documentLog1.setAfterStatusColor(afterStatusColor);
        documentLog1.setAttachedDocId(documentLog.getAttachedDocId());
        documentLog1.setCreatedAt(new Date());
        documentLog1.setCreatedById(createdById);
        return logRepository.save(documentLog1);
    }

    @Override
    public DocumentLog createComment(DocumentLog documentLog, Integer logTypeId, Date beforeDate, Date afterDate,String content, Integer createdById,Integer documentId) {
        DocumentLog documentLog1 = new DocumentLog();
        documentLog1.setContent(content);
        documentLog1.setDocumentId(documentId);
        documentLog1.setType(logTypeId);
        documentLog1.setTaskId(documentLog.getTaskId());
        documentLog1.setTaskSubId(documentLog.getTaskSubId());
        documentLog1.setAfterDate(afterDate);
        documentLog1.setBeforeDate(beforeDate);
        documentLog1.setCreatedAt(new Date());
        documentLog1.setCreatedById(createdById);
        return logRepository.save(documentLog1);

    }
    @Override
    public DocumentLog createUserComment(DocumentLog documentLog, Integer logTypeId, Integer beforeUserId, Integer afterUserId,String content, Integer createdById,Integer documentId) {
        DocumentLog documentLog1 = new DocumentLog();
        documentLog1.setContent(content);
        documentLog1.setDocumentId(documentId);
        documentLog1.setType(logTypeId);
        documentLog1.setTaskId(documentLog.getTaskId());
        documentLog1.setTaskSubId(documentLog.getTaskSubId());
        documentLog1.setBeforeUserId(beforeUserId);
        documentLog1.setAfterUserId(afterUserId);
        documentLog1.setCreatedAt(new Date());
        documentLog1.setCreatedById(createdById);
        return logRepository.save(documentLog1);

    }

    @Override
    public Page<DocumentLog> findFiltered(String dateBeginStr, String dateEndStr, Integer createdById, Integer type, Pageable pageable) {
        return logRepository.findAll(getFilteringSpecification(dateBeginStr,dateEndStr,createdById,type),pageable);
    }

    private static Specification<DocumentLog> getFilteringSpecification(
            final String dateBeginStr,
            final String dateEndStr,
            final Integer createdById,
            final Integer type
    ) {
        return new Specification<DocumentLog>() {
            @Override
            public Predicate toPredicate(Root<DocumentLog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                Date dateBegin = DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat);
                Date dateEnd = DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat);
                if (dateBegin != null && dateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt").as(Date.class), dateBegin));
                }
                if (dateEnd != null && dateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt").as(Date.class), dateEnd));
                }
                if (dateBegin != null && dateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.get("createdAt").as(Date.class), dateBegin, dateEnd));
                }


                if(createdById != null){
                    predicates.add(criteriaBuilder.equal(root.get("createdById"), createdById));
                }

                if(type!=null){
                    predicates.add(criteriaBuilder.equal(root.get("type"), type));
                }


                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
}
