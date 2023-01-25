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
    public static final String MinWageDelete = MinWage + "/delete";

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


    //Roles
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

    //user performer and agreement
    private static final String UserControl = Prefix + "/user_control";
    public static final String UserControlList = UserControl + "/list";
    public static final String UserControlListAjax = UserControl + "/ajax_list";
    public static final String UserControlStatus = UserControl + "/status";

    //Position
    private static final String Position = Prefix + "/position";
    public static final String PositionList = Position + "/list";
    public static final String PositionGet = Position + "/get_ajax_position";
    public static final String PositionCreate = Position + "/create";
    public static final String PositionUpdate = Position + "/update";
    public static final String PositionStatusUpdate = Position + "/status_update";
    public static final String PositionDelete = Position + "/delete";
    public static final String PositionView = Position + "/view";

    //test html to Word
    private static final String Word = Prefix + "/word";
    public static final String WordEditor = Word;
    public static final String WordCreate = Word + "/create";

    //    event news
    private static final String Event = Prefix + "/event-news";

    public static final String EventNewsList = Event + "/list";
    public static final String EventNewsAjaxLIst = Event + "/ajax-list";
    public static final String EventNewsEdit = Event + "/edit";
    public static final String EventNewsView = Event + "/view";
    public static final String EventNewsDelete = Event + "/delete";
    public static final String EventNewsCreate = Event + "/create";
    public static final String EventNewsFileUpload = Event + "/upload-file";
    public static final String EventNewsFileDelete = Event + "/delete-file";


    public static final String SendingData = Prefix + "/sendings";
    public static final String Didox = Prefix + "/didox";
    public static final String SendingDataList = SendingData + "/list";
    public static final String SendingDataAjaxList = SendingData + "/ajax-list";
    public static final String DidoxList = Didox + "/list";
    public static final String DidoxAjaxList = Didox + "/ajax-list";
    public static final String DidoxStatucList = Didox + "/status-list";


}
