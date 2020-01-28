package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.entity.DocumentType;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentTypeRepository extends DataTablesRepository<DocumentType, Integer>, JpaRepository<DocumentType, Integer> {

    List<DocumentType> findByStatusTrue();

}
