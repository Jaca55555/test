package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.FactureProduct;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 27.01.2010.
 * (uz)
 */
@Repository
public interface FactureProductRepository extends JpaRepository<FactureProduct, Integer> {

    List<FactureProduct> findByFactureIdOrderByNumberAsc(Integer factureId);

}
