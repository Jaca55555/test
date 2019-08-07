package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.sys.GNKSoato;

/**
 * Created by Utkirbek Boltaev on 07.08.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface GNKSoatoRepository extends JpaRepository<GNKSoato, Integer> {

    GNKSoato findByGnkSoatoId(Integer gnkSoatoId);

}
