package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.sys.Soato;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 11.06.2019.
 * (uz)
 */
@Repository
public interface SoatoRepository extends JpaRepository<Soato, Integer> {

    List<Soato> findByLevelOrderByNameAsc(Integer level);

}
