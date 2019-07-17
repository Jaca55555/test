package uz.maroqand.ecology.ecoexpertise.constant.sys;

public class SysUrls {
    private static final String Prefix = "/sys";

    public static final String IdGovUzLogin = Prefix +"/igu/login";
    public static final String IdGovUzLogout =Prefix + "/igu/logout";
    public static final String IdGovUzAccessToken = Prefix + "/igu/access_token";

    public static final String EDSLogin = Prefix + "/eds_login";

    public static final String Toastr = Prefix + "/toastr";

    private static final String Notification = Prefix + "/notification";
    private static final String NotificationShow = Notification + "/show";
    private static final String NotificationList = Notification + "/list";
    private static final String NotificationView = Notification + "/view";

    public static final String ErrorNotFound = Prefix + "/404";
    public static final String ErrorInternalServerError = Prefix + "/500";
    public static final String ErrorForbidden = Prefix + "/403";

}
