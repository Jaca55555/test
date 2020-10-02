package uz.maroqand.ecology.docmanagement.service;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentLog;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentLogRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentLogService;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
}
