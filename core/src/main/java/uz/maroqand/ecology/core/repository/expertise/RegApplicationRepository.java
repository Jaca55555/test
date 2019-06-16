package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;

import java.awt.print.Pageable;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Repository
public interface RegApplicationRepository extends DataTablesRepository<RegApplication, Integer>, JpaRepository<RegApplication, Integer>, JpaSpecificationExecutor<RegApplication> {

    RegApplication findByIdAndDeletedFalse(Integer id);

    RegApplication findByIdAndCreatedByIdAndDeletedFalse(Integer id, Integer createdBy);
}
