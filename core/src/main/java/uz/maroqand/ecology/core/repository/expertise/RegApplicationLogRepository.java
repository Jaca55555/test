package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;

import java.util.List;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 22.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface RegApplicationLogRepository extends JpaRepository<RegApplicationLog, Integer>,JpaSpecificationExecutor<RegApplicationLog> {

    List<RegApplicationLog> findByRegApplicationIdAndDeletedFalseOrderByIdDesc(Integer regApplicationId);

    List<RegApplicationLog> findByRegApplicationIdAndTypeAndDeletedFalseOrderByIdDesc(Integer regApplicationId, LogType type);

    List<RegApplicationLog> findByIdInOrderByIdDesc(Set<Integer> ids);

    RegApplicationLog findTopByRegApplicationIdAndTypeAndIndexAndDeletedFalseOrderByIdDesc(Integer regApplicationId, LogType type, Integer index);

    List<RegApplicationLog> findByRegApplicationIdAndTypeAndIndexAndDeletedFalseOrderByIdDesc(Integer regApplicationId, LogType type, Integer index);

    List<RegApplicationLog> findByRegApplicationIdAndTypeAndShowTrueAndDeletedFalseOrderByIdDesc(Integer regApplicationId, LogType type);

    List<RegApplicationLog> findByTypeAndDeletedFalseOrderByIdDesc(LogType logType);

}