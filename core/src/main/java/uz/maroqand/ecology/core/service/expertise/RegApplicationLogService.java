package uz.maroqand.ecology.core.service.expertise;

import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 22.06.2019.
 * (uz)
 * (ru)
 */
public interface RegApplicationLogService {

    List<RegApplicationLog> getByRegApplicationId(Integer regApplicationId);

    RegApplicationLog getById(Integer id);

    RegApplicationLog create(RegApplication regApplication, LogType logType, String comment, User user);

    RegApplicationLog update(RegApplicationLog regApplicationLog, LogStatus logStatus, String comment, User user);

    RegApplicationLog updateDocument(RegApplicationLog regApplicationLog, User user);

}
