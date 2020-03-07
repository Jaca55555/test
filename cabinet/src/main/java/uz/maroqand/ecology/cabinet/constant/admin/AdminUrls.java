package uz.maroqand.ecology.cabinet.constant.admin;

/**
 * Created by Utkirbek Boltaev on 07.03.2020.
 * (uz)
 * (ru)
 */
public class AdminUrls {
    private static final String Prefix = "/admin";

    private static final String Organization = "/organization";

    //Users
    private static final String Users = Prefix + "/users";
    public static final String UsersList = Users + "/list";
    public static final String UsersListAjax = Users + "/ajax_list";
    public static final String UsersNew = Users + "/new";
    public static final String UsersEvidenceFileUpload = Users + "/evindence_file_upload";
    public static final String UsersEvidenceFileDownload = Users + "/evindence_file_download";
    public static final String UsersEvidenceFileDelete = Users + "/evindence_file_delete";
    public static final String UsersUsernameCheck = Users + "/check_username";
    public static final String UsersEdit = Users + "/edit";
    public static final String UsersCreate = Users + "/create";
    public static final String UsersPswEdit = Users + "/rsw_edit";
    public static final String UsersPswUpdate = Users + "/rsw_update";
    public static final String UsersUpdate = Users + "/update";
    public static final String UsersEditEnebled = Users + "/enebled";
    public static final String UsersView = Users + "/view";

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

    //Position
    private static final String Position = Prefix + "/position";
    public static final String PositionList = Position + "/list";
    public static final String PositionGet = Position + "/get_ajax_position";
    public static final String PositionCreate = Position + "/create";
    public static final String PositionUpdate = Position + "/update";
    public static final String PositionStatusUpdate = Position + "/status_update";
    public static final String PositionDelete = Position + "/delete";
    public static final String PositionView = Position + "/view";

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