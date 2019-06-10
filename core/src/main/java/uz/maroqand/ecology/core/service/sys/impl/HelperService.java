package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;

/**
 * Created by Utkirbek Boltaev on 26.05.2019.
 * (uz)
 * (ru)
 */
@Service
public class HelperService {

    private final UserService userService;

    public HelperService(UserService userService) {
        this.userService = userService;
    }

    @Cacheable(value = "getUserById", key = "#id",condition="#id != null",unless="#result == ''")
    public String getUserById(Integer id) {
        if(id==null) return "";
        User user = userService.findById(id);
        return user!=null? user.getUsername():"";
    }

}
