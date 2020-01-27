package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.Facture;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Integer>, JpaSpecificationExecutor<Facture> {

}
