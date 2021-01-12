package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.HarmfulSubstancesAmount;

/**
 * Created by Sadullayev Akmal on 28.11.2020.
 * (uz)
 */
@Repository
public interface HarmfulSubstancesAmountRepository extends DataTablesRepository<HarmfulSubstancesAmount, Integer>, JpaRepository<HarmfulSubstancesAmount, Integer>, JpaSpecificationExecutor<HarmfulSubstancesAmount> {

    HarmfulSubstancesAmount findByIdAndDeletedFalse(Integer id);
}
