package uz.maroqand.ecology.docmanagement.service;

import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagement.entity.DocumentLog;
import uz.maroqand.ecology.docmanagement.repository.DocumentLogRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentLogService;

import java.util.Date;
import java.util.List;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 14.02.2020
 */
@Service
public class DocumentLogServiceImpl implements DocumentLogService {

    private final DocumentLogRepository logRepository;

    public DocumentLogServiceImpl(DocumentLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public DocumentLog getById(Integer id) {
        return logRepository.getOne(id);
    }

    @Override
    public List<DocumentLog> getAllByDocId(Integer docId) {
        return logRepository.findAllByDocumentIdOrderByIdDesc(docId);
    }

    @Override
    public List<DocumentLog> getAllByDocAndTaskId(Integer docId, Integer taskId) {
        return logRepository.findAllByDocumentIdAndTaskSubIdOrderByIdDesc(docId, taskId);
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
}
