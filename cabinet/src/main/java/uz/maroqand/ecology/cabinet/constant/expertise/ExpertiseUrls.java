package uz.maroqand.ecology.cabinet.constant.expertise;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
public class ExpertiseUrls {
    private static final String Prefix = "/expertise";

    //Birinchi tekshiruv
    private static final String Confirm = Prefix + "/confirm";
    public static final String ConfirmList = Confirm + "/list";
    public static final String ConfirmListAjax = Confirm + "/list_ajax";
    public static final String ConfirmView = Confirm + "/view";
    public static final String ConfirmApproved = Confirm + "/approved";
    public static final String ConfirmDenied = Confirm + "/denied";
    public static final String ConfirmFileDownload = Confirm + "/download";

    //Boshqaruv
    private static final String Forwarding = Prefix + "/forwarding";
    public static final String ForwardingList = Forwarding + "/list";
    public static final String ForwardingListAjax = Forwarding + "/list_ajax";
    public static final String ForwardingView = Forwarding + "/view";
    public static final String ForwardingAction = Forwarding + "/action";

    //Natijani kiritish
    private static final String Performer = Prefix + "/performer";
    public static final String PerformerList = Performer + "/list";
    public static final String PerformerListAjax = Performer + "/list_ajax";
    public static final String PerformerView = Performer + "/view";

    //Kelishish
    private static final String Agreement = Prefix + "/agreement";
    public static final String AgreementList = Agreement + "/list";
    public static final String AgreementListAjax = Agreement + "/list_ajax";
    public static final String AgreementView = Agreement + "/view";

    //Oxirgi kelishuv
    private static final String AgreementComplete = Prefix + "/agreement_complete";
    public static final String AgreementCompleteList = AgreementComplete + "/list";
    public static final String AgreementCompleteListAjax = AgreementComplete + "/list_ajax";
    public static final String AgreementCompleteView = AgreementComplete + "/view";

    //File
    public static final String FileUpload = Prefix + "/file/upload";
    public static final String FileDownload = Prefix + "/file/download";
    public static final String FileDelete = Prefix + "/file/delete";

    //Billing
    private static final String Billing = Prefix + "/billing";
    public static final String BillingList = Billing + "/list";
    public static final String BillingListAjax = Billing + "/list_ajax";
    public static final String BillingView = Billing + "/view";


    //Applicant
    private static final String Applicant = Prefix + "/applicant";
    public static final String ApplicantList = Applicant + "/list";
    public static final String ApplicantListAjax = Applicant + "/list_ajax";
    public static final String ApplicantView = Applicant + "/view";

}
