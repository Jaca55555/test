package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.entity.Document;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentRepository extends DataTablesRepository<Document, Integer>,
                                            JpaRepository<Document, Integer>,
                                            JpaSpecificationExecutor<Document>
{
    Document findByIdAndDeletedFalse(Integer id);

    List<Document> findAllByDeletedFalse();
}
