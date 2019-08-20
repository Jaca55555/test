package uz.maroqand.ecology.core.service.sys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.sys.SmsSendStatus;
import uz.maroqand.ecology.core.entity.sys.SmsSend;

import java.util.Date;

public interface SmsSendService {

    SmsSend save(SmsSend smsSend);

    SmsSend getById(Integer id);

    SmsSend getRegApplicationId(Integer id);

    void update(SmsSend smsSend);

    Page<SmsSend> findFiltered(
            Date dateBegin,
            Date dateEnd,
            Integer id,
            String phone,
            String message,
            SmsSendStatus status,
            Pageable pageable
    );

    String getPhoneNumber(String phone);

}
