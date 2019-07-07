package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.sys.Country;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    List<Country> findAllByOrderByShortNameEnAsc();

    List<Country> findAllByOrderByShortNameAsc();

    List<Country> findAllByOrderByShortNameUzAsc();

}
