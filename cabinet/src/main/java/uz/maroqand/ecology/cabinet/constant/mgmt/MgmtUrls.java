package uz.maroqand.ecology.cabinet.constant.mgmt;

public class MgmtUrls {
    private static final String Prefix = "/mgmt";

    //tarjimalar
    private static final String Translations = Prefix + "/translations";
    public static final String TranslationsList = Translations;
    public static final String TranslationsListAjax = Translations + "/ajax_list";
    public static final String TranslationsNew = Translations + "/new";
    public static final String TranslationsEdit = Translations + "/edit";
    public static final String TranslationsCreate = Translations + "/create";
    public static final String TranslationsUpdate = Translations + "/update";
    public static final String TranslationsSearchByTag = Translations + "/search";

    //Eng kam ish xaqqi
    private static final String MinWage = Prefix + "/min_wage";
    public static final String MinWageList = MinWage;
    public static final String MinWageListAjax = MinWage + "/ajax_list";
    public static final String MinWageNew = MinWage + "/new";
    public static final String MinWageEdit = MinWage + "/edit";
    public static final String MinWageCreate = MinWage + "/create";
    public static final String MinWageUpdate = MinWage + "/update";

    //Roles
    private static final String Roles = Prefix + "/roles";
    public static final String RolesList = Roles + "/list";
    public static final String RolesListAjax = Roles + "/ajax_list";
    public static final String RolesNew = Roles + "/new";
    public static final String RolesEdit = Roles + "/edit";
    public static final String RolesCreate = Roles + "/create";
    public static final String RolesUpdate = Roles + "/update";
    public static final String RolesView = Roles + "/view";

}
