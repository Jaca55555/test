package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.FactureProduct;

@Repository
public interface FactureProductRepository extends JpaRepository<FactureProduct, Integer> {

}
