package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.AirPool;
import uz.maroqand.ecology.core.entity.expertise.DescriptionOfSources;

/**
 * Created by Sadullayev Akmal on 28.11.2020.
 * (uz)
 */
@Repository
public interface DescriptionOfSourcesRepository extends DataTablesRepository<DescriptionOfSources, Integer>, JpaRepository<DescriptionOfSources, Integer>, JpaSpecificationExecutor<DescriptionOfSources> {

    DescriptionOfSources findByIdAndDeletedFalse(Integer id);
}
