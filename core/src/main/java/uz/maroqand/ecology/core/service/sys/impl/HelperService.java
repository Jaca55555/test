package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.config.DatabaseMessageSource;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.constant.sys.AppealType;
import uz.maroqand.ecology.core.entity.client.Opf;
import uz.maroqand.ecology.core.entity.expertise.Activity;
import uz.maroqand.ecology.core.entity.expertise.Material;
import uz.maroqand.ecology.core.entity.expertise.ObjectExpertise;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.sys.Soato;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.entity.user.Position;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.client.OpfService;
import uz.maroqand.ecology.core.service.expertise.ActivityService;
import uz.maroqand.ecology.core.service.expertise.MaterialService;
import uz.maroqand.ecology.core.service.expertise.ObjectExpertiseService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.user.DepartmentService;
import uz.maroqand.ecology.core.service.user.PositionService;
import uz.maroqand.ecology.core.service.user.UserService;

import java.util.Set;

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
    private final OrganizationService organizationService;
    private final OpfService opfService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final FileService fileService;

    public HelperService(
            ObjectExpertiseService objectExpertiseService,
            OrganizationService organizationService,
            DepartmentService departmentService,
            ActivityService activityService,
            MaterialService materialService,
            PositionService positionService,
            SoatoService soatoService,
            FileService fileService,
            UserService userService,
            OpfService opfService
    ) {
        this.userService = userService;
        this.objectExpertiseService = objectExpertiseService;
        this.activityService = activityService;
        this.materialService = materialService;
        this.soatoService = soatoService;
        this.organizationService = organizationService;
        this.opfService = opfService;
        this.departmentService = departmentService;
        this.positionService = positionService;
        this.fileService = fileService;
    }

    private static DatabaseMessageSource databaseMessageSource;
    public static void setTranslationsSource(DatabaseMessageSource initializedDatabaseMessageSource) {
        databaseMessageSource = initializedDatabaseMessageSource;
    }

    public String getTranslation(String tag,String locale) {
        return databaseMessageSource.resolveCodeSimply(tag, locale);
    }

    /*  user    */
    @Cacheable(value = "getUserById", key = "#id",condition="#id != null",unless="#result == ''")
    public String getUserById(Integer id) {
        if(id==null) return "";
        User user = userService.findById(id);
        return user!=null? user.getUsername():"";
    }

    @Cacheable(value = "getUserFullNameById", key = "#id",condition="#id != null",unless="#result == ''")
    public String getUserFullNameById(Integer id) {
        if(id==null) return "";
        User user = userService.findById(id);
        return user!=null? user.getFullName():"";
    }

    @Cacheable(value = "getUserLastAndFirstShortById", key = "#id",condition="#id != null",unless="#result == ''")
    public String getUserLastAndFirstShortById(Integer id) {
        if(id==null) return "";
        User user = userService.findById(id);
        String fio_short = "  ";
        if (user!=null){
            fio_short=user.getFirstname()!=null?user.getFirstname().substring(0,1):" ";
            fio_short+=user.getLastname()!=null?user.getLastname().substring(0,1):" ";
        }
        return fio_short;
    }

    @Cacheable(value = "getOrganizationName", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getOrganizationName(Integer id, String locale) {
        if(id==null) return "";
        Organization organization = organizationService.getById(id);
        return organization!=null? organization.getNameTranslation(locale):"";
    }

    @Cacheable(value = "getDepartmentName", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getDepartmentName(Integer id, String locale) {
        if(id==null) return "";
        Department department = departmentService.getById(id);
        return department!=null? department.getNameTranslation(locale):"";
    }

    @Cacheable(value = "getPositionName", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getPositionName(Integer id, String locale) {
        if(id==null) return "";
        Position position = positionService.getById(id);
        return position!=null? position.getNameTranslation(locale):"";
    }


    /*  expertise   */
    @Cacheable(value = "getObjectExpertise", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getObjectExpertise(Integer id, String locale) {
        if(id==null) return "";
        ObjectExpertise objectExpertise = objectExpertiseService.getById(id);
        return objectExpertise !=null? objectExpertise.getNameTranslation(locale):"";
    }

    @Cacheable(value = "getActivity", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getActivity(Integer id, String locale) {
        if(id==null) return "";
        Activity activity = activityService.getById(id);
        return activity !=null? activity.getNameTranslation(locale):"";
    }

    @Cacheable(value = "getCategory", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getCategory(Integer id, String locale) {
        if(id==null) return "";
        Category category = Category.getCategory(id);
        return databaseMessageSource.resolveCodeSimply(category.getName(),locale);
    }

    @Cacheable(value = "getMaterial", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getMaterial(Integer id, String locale) {
        if(id==null) return "";
        Material material = materialService.getById(id);
        return material!=null? material.getNameTranslation(locale):"";
    }

    @Cacheable(value = "getMaterialShortName", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getMaterialShortName(Integer id, String locale) {
        if(id==null) return "";
        Material material = materialService.getById(id);
        return material!=null? material.getNameShortTranslation(locale):"";
    }

    public String getMaterials(Set<Integer> ids, String locale) {
        if(ids==null) return "";
        String result = "";
        for (Integer id:ids){
            result += getMaterial(id,locale)+", ";
        }
        return result.length()>2? result.substring(0,result.length()-2):result;
    }

    public String getMaterialShortNames(Set<Integer> ids, String locale) {
        if(ids==null) return "";
        String result = "";
        for (Integer id:ids){
            result += getMaterialShortName(id,locale)+", ";
        }
        return result.length()>2? result.substring(0,result.length()-2):result;
    }

    /*  common  */
    @Cacheable(value = "getSoatoName", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getSoatoName(Integer id, String locale) {
        if(id==null) return "";
        Soato soato = soatoService.getById(id);
        return soato!=null? soato.getNameTranslation(locale):"";
    }

    @Cacheable(value = "getOpfName", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getOpfName(Integer id, String locale) {
        if(id==null) return "";
        Opf opf = opfService.getById(id);
        return opf!=null? opf.getNameTranslation(locale):"";
    }

    @Cacheable(value = "getAppealType", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getAppealType(Integer id, String locale) {
        if(id==null) return "";
        AppealType appealType = AppealType.getAppealType(id);
        return databaseMessageSource.resolveCodeSimply(appealType.getName(),locale);
    }

    @Cacheable(value = "getFileName", key = "#id",condition="#id != null",unless="#result == ''")
    public String getFileName(Integer id) {
        if(id==null) return "";
        File file = fileService.findById(id);
        return file!=null?file.getName():"";
    }

    @Cacheable(value = "getRegApplicationStatus", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getRegApplicationStatus(Integer id, String locale) {
        if(id==null) return "";
        RegApplicationStatus regApplicationStatus = RegApplicationStatus.getRegApplicationStatus(id);
        System.out.println("regApplicationStatus="+regApplicationStatus);
        System.out.println("regApplicationStatus="+regApplicationStatus.getName());
        return databaseMessageSource.resolveCodeSimply(regApplicationStatus.getName(),locale);
    }


    @Cacheable(value = "getApplicantType", key = "{#id,#locale}",condition="#id != null",unless="#result == ''")
    public String getApplicantType(Integer id, String locale) {
        if(id==null) return "";
        ApplicantType applicantType = ApplicantType.getApplicantType(id);
        return databaseMessageSource.resolveCodeSimply(applicantType.getName(),locale);
    }

}
