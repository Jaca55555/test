package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.Offer;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer> {

    Offer findTop1ByActiveTrueAndLanguageOrderByIdDesc(String language);

}
