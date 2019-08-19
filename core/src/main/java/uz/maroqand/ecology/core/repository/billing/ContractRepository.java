package uz.maroqand.ecology.core.repository.billing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.billing.Contract;

/**
 * Created by Utkirbek Boltaev on 14.08.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {


}
