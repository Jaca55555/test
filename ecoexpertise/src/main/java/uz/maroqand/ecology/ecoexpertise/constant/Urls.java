package uz.maroqand.ecology.ecoexpertise.constant;

public class Urls {
    private static final String Prefix = "/reg";

    private static final String RegApplication = Prefix + "/application";

    public static final String RegApplicationDashboard = RegApplication + "/dashboard";
    public static final String RegApplicationResume = RegApplication + "/resume";
    public static final String RegApplicationStart = RegApplication + "/start";
    public static final String RegApplicationApplicant = RegApplication + "/applicant";
    public static final String RegApplicationAbout = RegApplication + "/about";
    public static final String RegApplicationWaiting = RegApplication + "/waiting";
    public static final String RegApplicationContract = RegApplication + "/contract";
    public static final String RegApplicationPrepayment = RegApplication + "/prepayment";
    public static final String RegApplicationPayment = RegApplication + "/payment";
    public static final String RegApplicationPaymentSendSms = RegApplication + "/payment/send_sms";
    public static final String RegApplicationPaymentConfirmSms = RegApplication + "/payment/confirm_sms";
    public static final String RegApplicationStatus = RegApplication + "/status";

    public static final String RegApplicationList = RegApplication + "/list";
    public static final String RegApplicationListAjax = RegApplication + "/list_ajax";


    private static final String Appeal = "/appeal";
    public static final String AppealUserList = Prefix + Appeal + "/list";
    public static final String AppealUserListAjax = Prefix + Appeal + "/list_ajax";

    public static final String AppealNew = Prefix + Appeal + "/new";
    public static final String AppealCreate = Prefix + Appeal + "/create";
    public static final String AppealEdit = Prefix + Appeal + "/edit";
    public static final String AppealUpdate = Prefix + Appeal + "/update";
    public static final String AppealDelete = Prefix + Appeal + "/delete";
    public static final String AppealUserView = Prefix + Appeal + "/view";
    public static final String AppealSubCreate = Prefix + Appeal + "/sub_create";

}
