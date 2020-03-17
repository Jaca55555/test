package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.docmanagement.dto.IncomingRegFilter;
import uz.maroqand.ecology.docmanagement.dto.ReferenceRegFilterDTO;
import uz.maroqand.ecology.docmanagement.dto.ResolutionDTO;
import uz.maroqand.ecology.docmanagement.dto.StaticInnerInTaskSubDto;
import uz.maroqand.ecology.docmanagement.entity.Document;
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

    Integer getCountTaskStatus(Set<Integer> taskStatusSet);

    DocumentTask create(DocumentTask task);

    DocumentTask createNewTask(Document doc, Integer status, String context, Date dueDate, Integer chiefId, Integer createdById);

    DocumentTask update(DocumentTask task);

    DocumentTask getTaskByUser(Integer docId, Integer userId);

    String getTreeByDocumentId(List<DocumentTask> documentTasks);

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
            Boolean specialControll,
            Pageable pageable
    );
    Page<DocumentTask> findFilteredReference(
            Integer organizationId,
            Integer documentTypeId,
            ReferenceRegFilterDTO referenceRegFilterDTO,
            Date deadlineDateBegin,
            Date deadlineDateEnd,
            Integer type,
            Set<Integer> status,
            Integer departmentId,
            Integer receiverId,
            Boolean specialControll,
            Pageable pageable
    );


    //taskOrsubTask==true  task
    //taskOrsubTask==false  taskSub
    List<String> getDueColor(Date date, boolean taskOrsubTask, Integer statusId, String locale);

    String getDueTranslateNameOrColor(Integer id,boolean taskOrsubTask, String nameOrColor,String locale);

    StaticInnerInTaskSubDto countAllInnerByReceiverId(Integer receiverId);

    ResolutionDTO resolutionCreateByTaskId(Integer taskId,String locale);

}