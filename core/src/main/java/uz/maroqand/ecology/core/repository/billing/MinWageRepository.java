package uz.maroqand.ecology.core.repository.billing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.billing.MinWage;

import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface MinWageRepository extends JpaRepository<MinWage, Integer> {

    MinWage findFirst1ByBeginDateLessThanEqualOrderByBeginDateDesc(Date beginDate);

}
