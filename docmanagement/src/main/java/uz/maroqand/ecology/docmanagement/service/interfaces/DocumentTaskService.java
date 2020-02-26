package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.docmanagement.dto.IncomingRegFilter;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 11.02.2020
 */
public interface DocumentTaskService {

    DocumentTask getById(Integer id);

    DocumentTask getByIdAndDocumentId(Integer id,Integer docId);

    List<DocumentTask> getByDocumetId(Integer docId);

    List<DocumentTask> getByStatusNotInactive();

    Integer countNew();

    Integer countInProcess();

    Integer countNearDate();

    Integer countExpired();

    Integer countExecuted();

    Integer countTotal();

    DocumentTask create(DocumentTask task);

    DocumentTask createNewTask(Integer docId, Integer status, String context, Date dueDate,Integer chiefId, Integer createdById);

    DocumentTask update(DocumentTask task);

    DocumentTask getTaskByUser(Integer docId, Integer userId);

    Page<DocumentTask> findFiltered(
            Integer organizationId,
            Integer documentTypeId,
            IncomingRegFilter incomingRegFilter,
            Date deadlineDateBegin,
            Date deadlineDateEnd,
            Integer type,
            Set<Integer> status,
            Integer departmentId,
            Integer receiverId,
            Pageable pageable
    );

}