package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.entity.Journal;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 02.05.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface JournalRepository extends DataTablesRepository<Journal, Integer>, JpaRepository<Journal, Integer> {
    @Query("SELECT d FROM Journal d LEFT JOIN User dt ON d.createdById = dt.id WHERE d.status=true AND d.deleted = FALSE AND  dt.organizationId=?1 AND d.documentTypeId=?2")
    List<Journal> findByStatusTrueAndDocumentType(Integer organizationId,Integer documentTypeId);

    List<Journal> findByStatusTrueAndDocumentTypeId(Integer documentTypeId);
    Journal findByIdAndDeletedFalse(Integer id);

}
