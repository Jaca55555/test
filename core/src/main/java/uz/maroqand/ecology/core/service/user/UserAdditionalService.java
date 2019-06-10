package uz.maroqand.ecology.core.service.user;


import uz.maroqand.ecology.core.constant.user.LoginType;
import uz.maroqand.ecology.core.entity.user.User;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 */
public interface UserAdditionalService {

    Integer createUserAdditional(User user);

    Integer updateUserAdditional(User user, LoginType loginType, HttpServletRequest request);

}
