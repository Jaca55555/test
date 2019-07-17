package uz.maroqand.ecology.core.service.sys;

import uz.maroqand.ecology.core.entity.sys.AppealSub;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.List;

public interface AppealSubService {

    List<AppealSub> getById(Integer id);

    AppealSub create(AppealSub appealSub, User user);
    AppealSub update(AppealSub appealSub, User user);
}
