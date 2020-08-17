package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.entity.DocumentView;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.05.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentViewRepository extends DataTablesRepository<DocumentView, Integer>, JpaRepository<DocumentView, Integer> {

    DocumentView findByIdAndDeletedFalse(Integer id);

    List<DocumentView> findByStatusTrue();
    @Query("SELECT d FROM DocumentView d LEFT JOIN User dt ON d.createdById = dt.id WHERE d.status=true AND d.deleted = FALSE AND  dt.organizationId=?1 AND d.type=?2")
    List<DocumentView> findByStatusTrueAndType(Integer organizationId,String type);

}
