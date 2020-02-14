package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;

import java.util.List;
import java.util.Optional;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 11.02.2020
 */
@Repository
public interface DocumentTaskRepository extends DataTablesRepository<DocumentTask, Integer>, JpaRepository<DocumentTask, Integer> {

    List<DocumentTask> findByDocumentId(Integer docId);

}