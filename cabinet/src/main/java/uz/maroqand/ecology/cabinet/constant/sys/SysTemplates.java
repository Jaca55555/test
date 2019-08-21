package uz.maroqand.ecology.cabinet.constant.sys;

/**
 * Created by Utkirbek Boltaev on 24.06.2019.
 * (uz)
 * (ru)
 */
public class SysTemplates {
    private static final String Prefix = "sys";

    private static final String AppealAdmin = Prefix + "/appeal_admin";
    public static final String AppealAdminList = AppealAdmin + "/list";
    public static final String AppealAdminView = AppealAdmin + "/view";

    public static final String ErrorNotFound = "404";
    public static final String ErrorInternalServerError = "500";
    public static final String ErrorForbidden = "403";

    private static final String Notification = Prefix + "/notification";
    public static final String NotificationList = Notification + "/list";

}
