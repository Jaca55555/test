package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.entity.user.Notification;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentLog;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;

import java.util.Date;
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
    DocumentLog findFirstByDocumentIdOrderByIdDesc(Integer docId);
    List<DocumentLog> getAllByDocAndTaskId(Integer docId, Integer taskId);

    DocumentLog create(DocumentLog documentLog);

    DocumentLog update(DocumentLog documentLog);

    DocumentLog createLog(DocumentLog documentLog,Integer logTypeId, List<Integer> file_ids, String beforeStatus, String beforeStatusColor, String afterStatus, String afterStatusColor, Integer createdById);
    DocumentLog createComment(DocumentLog documentLog, Integer logTypeId, Date beforeDate,Date afterDate,String content ,Integer createdById,Integer documentId);
    DocumentLog createUserComment(DocumentLog documentLog, Integer logTypeId, Integer beforeUserId, Integer afterUserId, String content , Integer createdById,Integer documentId);
    Page<DocumentLog> findFiltered(
            String dateBeginStr,
            String dateEndStr,
            Integer createdById,
            Integer type,
            Pageable pageable
    );
}
