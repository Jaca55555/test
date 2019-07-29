package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.Coordinate;

/**
 * Created by Utkirbek Boltaev on 09.07.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface CoordinateRepository extends JpaRepository<Coordinate, Integer>, JpaSpecificationExecutor<Coordinate> {


}
