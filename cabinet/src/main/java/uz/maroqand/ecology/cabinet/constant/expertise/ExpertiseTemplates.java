package uz.maroqand.ecology.cabinet.constant.expertise;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
public class ExpertiseTemplates {
    private static final String Prefix = "expertise";

    //Birinchi tekshiruv
    private static final String Confirm = Prefix + "/confirm";
    public static final String ConfirmList = Confirm + "/list";
    public static final String ConfirmView = Confirm + "/view";

    //Boshqaruv
    private static final String Forwarding = Prefix + "/forwarding";
    public static final String ForwardingList = Forwarding + "/list";
    public static final String ForwardingView = Forwarding + "/view";
    public static final String ForwardingChecking = Forwarding + "/checking";

    //Natijani kiritish
    private static final String Performer = Prefix + "/performer";
    public static final String PerformerList = Performer + "/list";
    public static final String PerformerView = Performer + "/view";

    //Kelishish
    private static final String Agreement = Prefix + "/agreement";
    public static final String AgreementList = Agreement + "/list";
    public static final String AgreementView = Agreement + "/view";

    //Oxirgi kelishuv
    private static final String AgreementComplete = Prefix + "/agreement_complete";
    public static final String AgreementCompleteList = AgreementComplete + "/list";
    public static final String AgreementCompleteView = AgreementComplete + "/view";

    //Billing
    private static final String Billing = Prefix + "/billing";
    public static final String BillingList = Billing + "/list";

    //Applicant
    private static final String Applicant = Prefix + "/applicant";
    public static final String ApplicantList = Applicant + "/list";
    public static final String ApplicantView = Applicant + "/view";

    //Employee Control
    private static final String EmployeeControl= Prefix + "/employee_control";
    public static final String EmployeeControls= EmployeeControl + "/employes";
    public static final String EmployeeControlList = EmployeeControl + "/list";


    //Coordinate
    private static final String Coordinate = Prefix + "/coordinate";
    public static final String CoordinateList = Coordinate + "/list";
    public static final String CoordinateView = Coordinate + "/view";

    //Muddat uzaytirish uchun so'rovlar
    private static final String ChangeDeadlineDate = Prefix + "/change_deadline";
    public static final String ChangeDeadlineDateList = ChangeDeadlineDate + "/list";
    public static final String ChangeDeadlineDateView = ChangeDeadlineDate + "/view";


}
