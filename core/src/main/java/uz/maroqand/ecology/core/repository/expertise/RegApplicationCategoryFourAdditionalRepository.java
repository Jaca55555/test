package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationCategoryFourAdditional;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Repository
public interface RegApplicationCategoryFourAdditionalRepository extends DataTablesRepository<RegApplicationCategoryFourAdditional, Integer>, JpaRepository<RegApplicationCategoryFourAdditional, Integer>, JpaSpecificationExecutor<RegApplicationCategoryFourAdditional> {

    RegApplicationCategoryFourAdditional findByIdAndDeletedFalse(Integer id);

    RegApplicationCategoryFourAdditional findByRegApplicationIdAndDeletedFalse(Integer id);

}
