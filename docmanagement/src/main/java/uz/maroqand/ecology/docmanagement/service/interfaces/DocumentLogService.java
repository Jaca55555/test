package uz.maroqand.ecology.docmanagement.service.interfaces;

import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentLog;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;

import java.util.List;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 14.02.2020
 */
public interface DocumentLogService {

    DocumentLog getById(Integer id);

    List<DocumentLog> getAllByDocId(Integer docId);

    List<DocumentLog> getAllByDocAndTaskSubId(Integer docId, Integer taskId);

    List<DocumentLog> getAllByDocAndTaskId(Integer docId, Integer taskId);

    DocumentLog create(DocumentLog documentLog);

    DocumentLog update(DocumentLog documentLog);

    DocumentLog createLog(DocumentLog documentLog,Integer logTypeId, List<Integer> file_ids, String beforeStatus, String beforeStatusColor, String afterStatus, String afterStatusColor, Integer createdById);

}
