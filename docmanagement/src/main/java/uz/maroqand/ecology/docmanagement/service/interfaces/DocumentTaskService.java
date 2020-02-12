package uz.maroqand.ecology.docmanagement.service.interfaces;

import uz.maroqand.ecology.docmanagement.entity.DocumentTask;

import java.util.List;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 11.02.2020
 */
public interface DocumentTaskService
{
    DocumentTask getById(Integer id);

    List<DocumentTask> getByDocumetId(Integer docId);

    List<DocumentTask> getByStatusNotInactive();

    DocumentTask create(DocumentTask task);

    DocumentTask update(DocumentTask task);
}
