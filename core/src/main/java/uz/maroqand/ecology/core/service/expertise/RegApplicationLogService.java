package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 22.06.2019.
 * (uz)
 * (ru)
 */
public interface RegApplicationLogService {

    RegApplicationLog getById(Integer id);
    RegApplicationLog getByRegApplcationId(Integer id);

    List<RegApplicationLog> getByRegApplicationId(Integer regApplicationId);
    List<RegApplicationLog> findAll();
    List<RegApplicationLog> getByRegApplicationIdAndType(Integer regApplicationId, LogType type);

    List<RegApplicationLog> getByIds(Set<Integer> ids);

    RegApplicationLog create(RegApplication regApplication, LogType logType, String comment, User createdBy);

    RegApplicationLog update(RegApplicationLog regApplicationLog, LogStatus logStatus, String comment, Integer updateById);

    RegApplicationLog updateDocument(RegApplicationLog regApplicationLog);

    Date getDeadlineDate(Integer deadline, Date beginDate);

    RegApplicationLog getByIndex(Integer regApplicationId, LogType type, Integer index);

    List<RegApplicationLog> getAllByIndex(Integer regApplicationId, LogType type, Integer index);

    List<RegApplicationLog> getAllByLogType(LogType logType);

    Integer getLogCount(Integer id);
    Integer countbyLogType0AndDeletedFalseOrganizationId();
    Integer countbyLogType1AndDeletedFalseOrganizationId();
    Integer countbyLogType2AndDeletedFalseOrganizationId();
    Integer countbyLogType3AndDeletedFalseOrganizationId();
    Integer countbyLogType4AndDeletedFalseOrganizationId();
    Integer countbyLogType5AndDeletedFalseOrganizationId();
    Page<RegApplicationLog> findFiltered(
            FilterDto filterDto,
            Integer createdById,
            Integer updateById,
            LogType type,
            LogStatus status,
            Pageable pageable
    );

}
