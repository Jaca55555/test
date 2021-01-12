package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.DescriptionOfSources;
import uz.maroqand.ecology.core.entity.expertise.DescriptionOfSourcesAdditional;

/**
 * Created by Sadullayev Akmal on 28.11.2020.
 * (uz)
 */
@Repository
public interface DescriptionOfSourcesAdditionalRepository extends DataTablesRepository<DescriptionOfSourcesAdditional, Integer>, JpaRepository<DescriptionOfSourcesAdditional, Integer>, JpaSpecificationExecutor<DescriptionOfSourcesAdditional> {

    DescriptionOfSourcesAdditional findByIdAndDeletedFalse(Integer id);
}
