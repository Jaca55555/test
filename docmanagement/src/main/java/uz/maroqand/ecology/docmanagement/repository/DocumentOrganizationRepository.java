package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentOrganizationRepository extends DataTablesRepository<DocumentOrganization, Integer>, JpaRepository<DocumentOrganization, Integer> {

    DocumentOrganization findByIdAndDeletedFalse(Integer id);

    List<DocumentOrganization> findByStatusTrue();

}
