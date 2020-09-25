package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;

import java.util.List;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentOrganizationRepository extends DataTablesRepository<DocumentOrganization, Integer>, JpaRepository<DocumentOrganization, Integer> {

    DocumentOrganization findByIdAndDeletedFalse(Integer id);
    Set<DocumentOrganization> findByParentAndDeletedFalse(Integer parentId);
    @Query("SELECT d FROM DocumentOrganization d LEFT JOIN User dt ON d.createdById = dt.id WHERE d.status=true AND d.deleted = FALSE AND  dt.organizationId=?1 AND d.level=?2")
    List<DocumentOrganization> getAllByLevel(Integer organizationId,Integer id);
    DocumentOrganization findByNameContaining(String name);
    @Query("SELECT d.id FROM DocumentOrganization d LEFT JOIN User dt ON d.createdById = dt.id WHERE d.status=true AND d.deleted = FALSE AND  dt.organizationId=?1")
    Set<Integer> findByStatusTrueAndOrganizationId(Integer organizationId);
    List<DocumentOrganization> findByStatusTrue();

    DocumentOrganization getByName(String name);
}
