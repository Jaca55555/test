package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.CoordinateLatLong;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 09.07.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface CoordinateLatLongRepository extends JpaRepository<CoordinateLatLong, Integer> {

    List<CoordinateLatLong> getByCoordinateIdAndDeletedFalse(Integer coordinateId);

}
