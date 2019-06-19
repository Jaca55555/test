package uz.maroqand.ecology.core.service.billing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.billing.MinWage;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public interface MinWageService {

    MinWage getMinWage();

    MinWage getById(Integer id);

    Page<MinWage> findFiltered(Integer id, Pageable pageable);

    MinWage createMinWage(MinWage minWage);

    MinWage updateMinWage(MinWage minWage);

}
