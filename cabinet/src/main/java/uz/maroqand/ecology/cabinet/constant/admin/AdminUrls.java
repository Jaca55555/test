package uz.maroqand.ecology.cabinet.constant.admin;

/**
 * Created by Utkirbek Boltaev on 07.03.2020.
 * (uz)
 * (ru)
 */
public class AdminUrls {
    private static final String Prefix = "/admin";

    private static final String Organization = "/organization";

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


    private static final String Department = "/department";

    private static final String Position = "/position";

}