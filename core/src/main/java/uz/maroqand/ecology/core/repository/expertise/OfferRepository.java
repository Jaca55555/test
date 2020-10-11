package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.Offer;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer> {

    Offer findTop1ByActiveTrueAndByudjetAndOrganizationIdOrderByIdDesc(Boolean budget,Integer organizationId);

    Page<Offer> findAllByActiveTrueAndDeletedFalse(Pageable pageable);

    List<Offer> findAllByActiveTrueAndDeletedFalse();

}
