package uz.maroqand.ecology.docmanagement.service.interfaces;

import uz.maroqand.ecology.docmanagement.entity.DocumentLog;

import java.util.List;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 14.02.2020
 */
public interface DocumentLogService {

    DocumentLog getById(Integer id);

    List<DocumentLog> getAllByDocId(Integer docId);

    DocumentLog create(DocumentLog documentLog);

    DocumentLog update(DocumentLog documentLog);

}
