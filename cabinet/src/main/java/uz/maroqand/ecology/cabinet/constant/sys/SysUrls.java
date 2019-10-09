package uz.maroqand.ecology.cabinet.constant.sys;

/**
 * Created by Utkirbek Boltaev on 24.06.2019.
 * (uz)
 * (ru)
 */
public class SysUrls {
    private static final String Prefix = "/sys";

    public static final String Toastr = Prefix + "/toastr";

    private static final String Notification = Prefix + "/notification";
    public static final String NotificationShow = Notification + "/show";
    public static final String NotificationList = Notification + "/list";
    public static final String NotificationListAjax = Notification + "/list_ajax";
    public static final String NotificationShowAfter = Notification + "/show/after";

    private static final String AppealAdmin = Prefix + "/appeal_admin";
    public static final String AppealAdminList = AppealAdmin + "/list";
    public static final String AppealAdminListAjax = AppealAdmin + "/list_ajax";
    public static final String AppealAdminView = AppealAdmin + "/view";
    public static final String AppealAdminSubCreate = AppealAdmin + "/sub_create";

    public static final String ErrorNotFound = Prefix + "/404";
    public static final String ErrorInternalServerError = Prefix + "/500";
    public static final String ErrorForbidden = Prefix + "/403";

}
