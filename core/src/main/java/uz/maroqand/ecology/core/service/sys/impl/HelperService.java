package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.config.DatabaseMessageSource;
import uz.maroqand.ecology.core.constant.sys.AppealType;
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

    private static DatabaseMessageSource databaseMessageSource;
    public static void setTranslationsSource(DatabaseMessageSource initializedDatabaseMessageSource) {
        databaseMessageSource = initializedDatabaseMessageSource;
    }

    public String getTranslation(String tag,String locale) {
        return databaseMessageSource.resolveCodeSimply(tag, locale);
    }

    @Cacheable(value = "getUserById", key = "#id",condition="#id != null",unless="#result == ''")
    public String getUserById(Integer id) {
        if(id==null) return "";
        User user = userService.findById(id);
        return user!=null? user.getUsername():"";
    }

    @Cacheable(value = "getAppealType", key = "#id",condition="#id != null",unless="#result == ''")
    public String getAppealType(Integer id, String locale) {
        if(id==null) return "";
        AppealType taskStep = AppealType.getAppealType(id);
        return databaseMessageSource.resolveCodeSimply(taskStep.getName(),locale);
    }

}
