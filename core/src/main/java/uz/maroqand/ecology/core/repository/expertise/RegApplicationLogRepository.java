package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.service.expertise.RegApplicationLogService;

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
    List<RegApplicationLog> findByRegApplicationIdAndStatusAndTypeAndDeletedFalse(Integer regApplicationId,LogStatus status,LogType type);
    List<RegApplicationLog> findAllByStatusAndRegApplicationIdAndDeletedFalseOrderByIdAsc(LogStatus status,Integer regApplicationId);
    RegApplicationLog findTopByRegApplicationIdAndTypeAndIndexAndDeletedFalseOrderByIdDesc(Integer regApplicationId, LogType type, Integer index);

    RegApplicationLog findTop1ByRegApplicationIdAndDeletedFalseOrderByIdDesc(Integer regApplicationId);
    RegApplicationLog findTop1ByRegApplicationIdAndTypeAndDeletedFalseOrderByIdDesc(Integer regApplicationId,LogType type);
    RegApplicationLog findTop1ByStatusAndRegApplicationIdAndTypeAndDeletedFalseOrderByIdAsc(LogStatus status,Integer regApplicationId,LogType type);

    List<RegApplicationLog> findByRegApplicationIdAndTypeAndIndexAndDeletedFalseOrderByIdDesc(Integer regApplicationId, LogType type, Integer index);

    List<RegApplicationLog> findByRegApplicationIdAndTypeAndShowTrueAndDeletedFalseOrderByIdDesc(Integer regApplicationId, LogType type);

    List<RegApplicationLog> findByTypeAndDeletedFalseOrderByIdDesc(LogType logType);

    @Query("SELECT COUNT(d) FROM RegApplicationLog d LEFT JOIN RegApplication dt ON d.regApplicationId = dt.id WHERE d.type =:type AND d.deleted = FALSE AND  dt.reviewId=:organizationId AND dt.status in (:statuses)")
    Integer countByTypeAndOrganizationIdAndDeletedFalseAndStatus(@Param("type") LogType type,@Param("organizationId") Integer organizationId,@Param("statuses")Set<RegApplicationStatus>  statuses);


    @Query("SELECT COUNT(d) FROM RegApplicationLog d LEFT JOIN RegApplication dt ON d.regApplicationId = dt.id WHERE d.type =:type AND d.deleted = FALSE AND  dt.reviewId=:organizationId AND d.status in (:statuses) and d.show=:show")
    Integer countByTypeAndOrganizationIdAndDeletedFalse(@Param("type") LogType type,@Param("organizationId") Integer organizationId,@Param("statuses")Set<LogStatus>  statuses,@Param("show") Boolean show);


    @Query("SELECT COUNT(d) FROM RegApplicationLog d LEFT JOIN RegApplication dt ON d.regApplicationId = dt.id WHERE d.type =:type AND d.deleted = FALSE AND  dt.reviewId=:organizationId AND d.status in (:statuses) AND d.updateById=:userId and d.show=:show")
    Integer countByTypeAndOrganizationIdAndUpdatedByIdAndDeletedFalse(@Param("type") LogType type,@Param("organizationId") Integer organizationId,@Param("statuses")Set<LogStatus>  statuses,@Param("userId") Integer userId,@Param("show") Boolean show);

}