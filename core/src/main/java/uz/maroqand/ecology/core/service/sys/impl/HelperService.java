package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.config.DatabaseMessageSource;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.constant.sys.AppealType;
import uz.maroqand.ecology.core.entity.expertise.Activity;
import uz.maroqand.ecology.core.entity.expertise.Material;
import uz.maroqand.ecology.core.entity.expertise.ObjectExpertise;
import uz.maroqand.ecology.core.entity.sys.Soato;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.ActivityService;
import uz.maroqand.ecology.core.service.expertise.MaterialService;
import uz.maroqand.ecology.core.service.expertise.ObjectExpertiseService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.user.UserService;

/**
 * Created by Utkirbek Boltaev on 26.05.2019.
 * (uz)
 * (ru)
 */
@Service
public class HelperService {

    private final UserService userService;
    private final ObjectExpertiseService objectExpertiseService;
    private final ActivityService activityService;
    private final MaterialService materialService;
    private final SoatoService soatoService;

    public HelperService(UserService userService, ObjectExpertiseService objectExpertiseService, ActivityService activityService, MaterialService materialService, SoatoService soatoService) {
        this.userService = userService;
        this.objectExpertiseService = objectExpertiseService;
        this.activityService = activityService;
        this.materialService = materialService;
        this.soatoService = soatoService;
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

    @Cacheable(value = "getSoatoName", key = "#id",condition="#id != null",unless="#result == ''")
    public String getSoatoName(Integer id, String locale) {
        if(id==null) return "";
        Soato soato = soatoService.getById(id);
        return soato!=null? soato.getNameTranslation(locale):"";
    }

    @Cacheable(value = "getAppealType", key = "#id",condition="#id != null",unless="#result == ''")
    public String getAppealType(Integer id, String locale) {
        if(id==null) return "";
        AppealType taskStep = AppealType.getAppealType(id);
        return databaseMessageSource.resolveCodeSimply(taskStep.getName(),locale);
    }

    @Cacheable(value = "getRegApplicationStatus", key = "#id",condition="#id != null",unless="#result == ''")
    public String getRegApplicationStatus(Integer id, String locale) {
        if(id==null) return "";
        RegApplicationStatus regApplicationStatus = RegApplicationStatus.getRegApplicationStatus(id);
        return databaseMessageSource.resolveCodeSimply(regApplicationStatus.getName(),locale);
    }


    @Cacheable(value = "getApplicantType", key = "#id",condition="#id != null",unless="#result == ''")
    public String getApplicantType(Integer id, String locale) {
        if(id==null) return "";
        ApplicantType applicantType = ApplicantType.getApplicantType(id);
        return databaseMessageSource.resolveCodeSimply(applicantType.getName(),locale);
    }

    /*  expertise   */
    @Cacheable(value = "getObjectExpertise", key = "#id",condition="#id != null",unless="#result == ''")
    public String getObjectExpertise(Integer id, String locale) {
        if(id==null) return "";
        ObjectExpertise objectExpertise = objectExpertiseService.getById(id);
        return objectExpertise !=null? objectExpertise.getNameTranslation(locale):"";
    }

    @Cacheable(value = "getActivity", key = "#id",condition="#id != null",unless="#result == ''")
    public String getActivity(Integer id, String locale) {
        if(id==null) return "";
        Activity activity = activityService.getById(id);
        return activity !=null? activity.getNameTranslation(locale):"";
    }

    @Cacheable(value = "getCategory", key = "#id",condition="#id != null",unless="#result == ''")
    public String getCategory(Integer id, String locale) {
        if(id==null) return "";
        Category category = Category.getCategory(id);
        return databaseMessageSource.resolveCodeSimply(category.getName(),locale);
    }

    @Cacheable(value = "getMaterial", key = "#id",condition="#id != null",unless="#result == ''")
    public String getMaterial(Integer id, String locale) {
        if(id==null) return "";
        Material material = materialService.getById(id);
        return material!=null? material.getNameTranslation(locale):"";
    }

}
