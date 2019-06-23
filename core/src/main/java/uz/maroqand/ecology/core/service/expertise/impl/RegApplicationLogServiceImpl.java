package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.LogStatus;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationLog;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.expertise.RegApplicationLogRepository;
import uz.maroqand.ecology.core.service.expertise.RegApplicationLogService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 22.06.2019.
 * (uz)
 * (ru)
 */

@Service
public class RegApplicationLogServiceImpl implements RegApplicationLogService {

    private final RegApplicationLogRepository regApplicationLogRepository;

    @Autowired
    public RegApplicationLogServiceImpl(RegApplicationLogRepository regApplicationLogRepository) {
        this.regApplicationLogRepository = regApplicationLogRepository;
    }

    public List<RegApplicationLog> getByRegApplicationId(Integer regApplicationId){
        return regApplicationLogRepository.findByRegApplicationIdAndDeletedFalse(regApplicationId);
    }

    public RegApplicationLog getById(Integer id){
        return regApplicationLogRepository.getOne(id);
    }

    public RegApplicationLog create(
            RegApplication regApplication,
            LogType logType,
            String comment,
            User user
    ){
        RegApplicationLog regApplicationLog = new RegApplicationLog();
        regApplicationLog.setRegApplicationId(regApplication.getId());
        regApplicationLog.setComment(comment);

        regApplicationLog.setType(logType);
        regApplicationLog.setStatus(LogStatus.Initial);
        regApplicationLog.setCreatedAt(new Date());
        regApplicationLog.setCreatedById(user.getId());
        return regApplicationLogRepository.save(regApplicationLog);
    }

    public RegApplicationLog update(
            RegApplicationLog regApplicationLog,
            LogStatus logStatus,
            String comment,
            User user
    ){

        regApplicationLog.setStatus(logStatus);
        regApplicationLog.setComment(comment);

        regApplicationLog.setUpdateAt(new Date());
        regApplicationLog.setUpdateById(user.getId());
        return regApplicationLogRepository.save(regApplicationLog);
    }

    public RegApplicationLog updateDocument(RegApplicationLog regApplicationLog, User user){
        regApplicationLog.setUpdateAt(new Date());
        regApplicationLog.setUpdateById(user.getId());
        return regApplicationLogRepository.save(regApplicationLog);
    }

    public Date getDeadlineDate(Integer deadline,Date beginDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        calendar.add(Calendar.DAY_OF_MONTH, deadline);
        return calendar.getTime();
    }
}