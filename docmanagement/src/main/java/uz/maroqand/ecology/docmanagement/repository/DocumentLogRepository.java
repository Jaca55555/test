package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.entity.DocumentLog;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 16.04.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentLogRepository extends
        DataTablesRepository<DocumentLog, Integer>,
        JpaRepository<DocumentLog, Integer>,
        JpaSpecificationExecutor<DocumentLog>
{
    List<DocumentLog> findByDocumentIdOrderByIdDesc(Integer docId);

    List<DocumentLog> findAllByDocumentIdAndTaskSubIdOrderByIdDesc(Integer docId, Integer taskId);

    List<DocumentLog> findAllByDocumentIdAndTaskIdOrderByIdDesc(Integer docId, Integer taskId);
}
