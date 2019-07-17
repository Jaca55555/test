package uz.maroqand.ecology.ecoexpertise.constant;

public class Templates {
    private static final String Prefix = "reg";
    private static final String RegApplication = Prefix;

    public static final String RegApplicationDashboard = RegApplication + "/dashboard";
    public static final String RegApplicationApplicant = RegApplication + "/applicant";
    public static final String RegApplicationAbout = RegApplication + "/about";
    public static final String RegApplicationWaiting = RegApplication + "/waiting";
    public static final String RegApplicationContract = RegApplication + "/contract";
    public static final String RegApplicationPrepayment = RegApplication + "/prepayment";
    public static final String RegApplicationPayment = RegApplication + "/payment";
    public static final String RegApplicationStatus = RegApplication + "/status";

    public static final String RegApplicationList = RegApplication + "/list";


    private static final String Appeal = "/appeal";
    public static final String AppealUserList = Appeal + "/list";
    public static final String AppealNew = Appeal + "/new";
    public static final String AppealView = Appeal + "/view";

    public static final String ErrorNotFound = Prefix + "/404";
    public static final String ErrorInternalServerError = Prefix + "/500";
    public static final String ErrorForbidden = Prefix + "/403";

}
