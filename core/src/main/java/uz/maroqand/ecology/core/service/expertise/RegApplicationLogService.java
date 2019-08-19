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

    List<RegApplicationLog> getByRegApplicationId(Integer regApplicationId);

    List<RegApplicationLog> getByIds(Set<Integer> ids);

    RegApplicationLog create(RegApplication regApplication, LogType logType, String comment, User createdBy);

    RegApplicationLog update(RegApplicationLog regApplicationLog, LogStatus logStatus, String comment, User updateBy);

    RegApplicationLog updateDocument(RegApplicationLog regApplicationLog, User user);

    Date getDeadlineDate(Integer deadline, Date beginDate);

    Page<RegApplicationLog> findFiltered(
            FilterDto filterDto,
            Integer createdById,
            Integer updateById,
            LogType type,
            LogStatus status,
            Pageable pageable
    );

}
