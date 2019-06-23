package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.LogType;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.User;

public interface RegApplicationService {

    void update(RegApplication regApplication);

    RegApplication create(User user);

    RegApplication getById(Integer id);

    RegApplication getById(Integer id, Integer createdBy);

    Page<RegApplication> findFiltered(FilterDto filterDto, Integer reviewId, LogType logType, Integer userId, Pageable pageable);

}
