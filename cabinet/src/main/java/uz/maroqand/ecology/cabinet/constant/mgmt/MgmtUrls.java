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

    //Department
    private static final String Department = Prefix + "/department";
    public static final String DepartmentList = Department + "/list";
    public static final String DepartmentListAjax = Department + "/ajax_list";
    public static final String DepartmentNew = Department + "/new";
    public static final String DepartmentEdit = Department + "/edit";
    public static final String DepartmentCreate = Department + "/create";
    public static final String DepartmentUpdate = Department + "/update";
    public static final String DepartmentView = Department + "/view";
    public static final String DepartmentGetByOrganization = Department + "/get_department";


    //Roles
    private static final String Users = Prefix + "/users";
    public static final String UsersList = Users + "/list";
    public static final String UsersListAjax = Users + "/ajax_list";
    public static final String UsersNew = Users + "/new";
    public static final String UsersUsernameCheck = Users + "/check_username";
    public static final String UsersEdit = Users + "/edit";
    public static final String UsersCreate = Users + "/create";
    public static final String UsersPswEdit = Users + "/rsw_edit";
    public static final String UsersPswUpdate = Users + "/rsw_update";
    public static final String UsersUpdate = Users + "/update";
    public static final String UsersEditEnebled = Users + "/enebled";
    public static final String UsersView = Users + "/view";

}
