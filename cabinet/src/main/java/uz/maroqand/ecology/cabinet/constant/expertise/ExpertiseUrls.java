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
    public static final String ConfirmApprovedEdit = Confirm + "/approved/edit";
    public static final String ConfirmDeniedEdit = Confirm + "/denied/edit";

    //Boshqaruv
    private static final String Forwarding = Prefix + "/forwarding";
    public static final String ForwardingList = Forwarding + "/list";
    public static final String ForwardingListAjax = Forwarding + "/list_ajax";
    public static final String ForwardingView = Forwarding + "/view";
    public static final String ForwardingAction = Forwarding + "/action";
    public static final String ForwardingAgreementAdd = Forwarding + "/agreement/add";
    public static final String ForwardingAgreementDelete = Forwarding + "/agreement/delete";

    //Natijani kiritish
    private static final String Performer = Prefix + "/performer";
    public static final String PerformerList = Performer + "/list";
    public static final String PerformerListAjax = Performer + "/list_ajax";
    public static final String PerformerView = Performer + "/view";
    public static final String PerformerAction = Performer + "/action";
    public static final String PerformerActionEdit = Performer + "/action/edit";
    public static final String PerformerChangeDeadlineDate = Performer + "/change_deadline_date";

    //Kelishish
    private static final String Agreement = Prefix + "/agreement";
    public static final String AgreementList = Agreement + "/list";
    public static final String AgreementListAjax = Agreement + "/list_ajax";
    public static final String AgreementView = Agreement + "/view";
    public static final String AgreementAction = Agreement + "/action";

    //Oxirgi kelishuv
    private static final String AgreementComplete = Prefix + "/agreement_complete";
    public static final String AgreementCompleteList = AgreementComplete + "/list";
    public static final String AgreementCompleteListAjax = AgreementComplete + "/list_ajax";
    public static final String AgreementCompleteView = AgreementComplete + "/view";
    public static final String AgreementCompleteAction = AgreementComplete + "/action";


    public static final String ExpertiseFileDownload = Prefix + "/document/download";


    //File
    public static final String FileUpload = Prefix + "/file/upload";
    public static final String FileDownload = Prefix + "/file/download";
    public static final String FileDelete = Prefix + "/file/delete";

    //Comment File
    public static final String CommentAdd = Prefix + "/comment/add";
    public static final String CommentFileUpload = Prefix + "/comment/file/upload";
    public static final String CommentFileDownload = Prefix + "/comment/file/download";
    public static final String CommentFileDelete = Prefix + "/comment/file/delete";

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

    //Employee control
    private static final String EmployeeControl= Prefix + "/employee_control";
    public static final String EmployeeControls= EmployeeControl + "s";
    public static final String EmployeeControlList = EmployeeControl + "/list";

    //Coordinate
    private static final String Coordinate = Prefix + "/coordinate";
    public static final String CoordinateList = Coordinate + "/list";
    public static final String CoordinateListAjax = Coordinate + "/list_ajax";
    public static final String CoordinateView = Coordinate + "/view";

    //Muddat uzaytirish uchun so'rovlar
    private static final String ChangeDeadlineDate = Prefix + "/change_deadline";
    public static final String ChangeDeadlineDateList = ChangeDeadlineDate + "/list";
    public static final String ChangeDeadlineDateListAjax = ChangeDeadlineDate + "/list_ajax";
    public static final String ChangeDeadlineDateView = ChangeDeadlineDate + "/view";
    public static final String ChangeDeadlineDateConfig= ChangeDeadlineDate + "/config";

    //Conclusion
    private static final String  Conclusion = Prefix + "/conclusion";
    public static final String ConclusionList = Conclusion + "/list";
    public static final String ConclusionListAjax = Conclusion + "/list_ajax";
    public static final String ConclusionView = Conclusion + "/view";

}
