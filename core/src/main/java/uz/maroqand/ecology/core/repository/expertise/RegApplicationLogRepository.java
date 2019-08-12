package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 22.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface RegApplicationLogRepository extends JpaRepository<RegApplicationLog, Integer> {

    List<RegApplicationLog> findByRegApplicationIdAndDeletedFalseOrderByIdDesc(Integer regApplicationId);

}
