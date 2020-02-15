package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 11.02.2020
 */

@Service
public class DocumentTaskServiceImpl implements DocumentTaskService
{
    private final DocumentTaskRepository taskRepository;

    @Autowired
    public DocumentTaskServiceImpl(DocumentTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public DocumentTask getById(Integer id) {
        Optional<DocumentTask> task = taskRepository.findById(id);
        return task.orElse(null);
    }

    @Override
    public List<DocumentTask> getByDocumetId(Integer docId) {
        return taskRepository.findByDocumentId(docId);
    }

    @Override
    public List<DocumentTask> getByStatusNotInactive() {
        return null;
    }

    @Override
    public DocumentTask create(DocumentTask task) {
        return taskRepository.save(task);
    }

    @Override
    public DocumentTask createNewTask(Integer docId, Integer status, String context, Date dueDate, Integer chiefId, Integer createdById) {
        DocumentTask documentTask = new DocumentTask();
        documentTask.setDocumentId(docId);
        documentTask.setStatus(status);
        documentTask.setContent(context.trim());
        documentTask.setDueDate(dueDate);
        documentTask.setChiefId(chiefId);
        documentTask.setCreatedAt(new Date());
        documentTask.setCreatedById(createdById);
        return taskRepository.save(documentTask);
    }

    @Override
    public DocumentTask update(DocumentTask task) {
        return taskRepository.save(task);
    }
}
