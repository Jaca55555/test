package uz.maroqand.ecology.cabinet.constant.expertise;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
public class ExpertiseUrls {
    private static final String Prefix = "/expertise";
    private static final String  ExpertiseReg = Prefix + "/reg";

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
    public static final String PerformerConclusionSave = Performer + "/conclusion_save";
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
    public static final String BillingEdit = Billing + "/edit";


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


    private static final String ExpertiseRegApplication = ExpertiseReg + "/application";

    public static final String ExpertiseRegApplicationDashboard = ExpertiseRegApplication + "/dashboard";
    public static final String ExpertiseRegApplicationResume = ExpertiseRegApplication + "/resume";
    public static final String ExpertiseRegApplicationStart = ExpertiseRegApplication + "/start";
    public static final String ExpertiseRegApplicationApplicantCancel = ExpertiseRegApplication + "/cancel";
    public static final String ExpertiseRegApplicationApplicant = ExpertiseRegApplication + "/applicant";
    public static final String ExpertiseRegApplicationSendSMSCode = ExpertiseRegApplication + "/send_sms_code";
    public static final String ExpertiseRegApplicationGetSMSCode = ExpertiseRegApplication + "/get_sms_code";

    public static final String ExpertiseRegApplicationAbout = ExpertiseRegApplication + "/about";
    public static final String ExpertiseRegApplicationClearCoordinates = ExpertiseRegApplication + "/clear_coordinates";
    public static final String ExpertiseRegApplicationGetActivity = ExpertiseRegApplication + "/activity";
    public static final String ExpertiseRegApplicationGetMaterials = ExpertiseRegApplication + "/materials";
    public static final String ExpertiseRegApplicationGetMaterial = ExpertiseRegApplication + "/material";
    public static final String ExpertiseRegApplicationFileUpload = ExpertiseRegApplication + "/file_upload";
    public static final String ExpertiseRegApplicationFileDownload = ExpertiseRegApplication + "/file_download";
    public static final String ExpertiseRegApplicationFileDelete = ExpertiseRegApplication + "/file_delete";

    public static final String ExpertiseRegApplicationContract = ExpertiseRegApplication + "/contract";
    public static final String ExpertiseRegApplicationContractSubmit = ExpertiseRegApplication + "/contract/submit";
    public static final String ExpertiseRegApplicationContractDownload = ExpertiseRegApplication + "/contract/download";
    public static final String ExpertiseRegApplicationContractUpload = ExpertiseRegApplication + "/contract/upload";
    public static final String ExpertiseRegApplicationContractDelete = ExpertiseRegApplication + "/contract/delete";

    public static final String ExpertiseRegApplicationPrepayment = ExpertiseRegApplication + "/prepayment";
    public static final String ExpertiseRegApplicationPaymentSendSms = ExpertiseRegApplication + "/payment/send_sms";
    public static final String ExpertiseRegApplicationPaymentConfirmSms = ExpertiseRegApplication + "/payment/confirm_sms";
    public static final String ExpertiseRegApplicationPaymentFree = ExpertiseRegApplication + "/payment/free";
    public static final String ExpertiseRegApplicationStatus = ExpertiseRegApplication + "/status";
    public static final String ExpertiseRegApplicationResend = ExpertiseRegApplication + "/resend";

    public static final String ExpertiseRegApplicationConclusionDownload = ExpertiseReg + "/conclusion_file/download";
    public static final String ExpertiseRegApplicationConfirmFacture = ExpertiseRegApplication + "/confirm/facture";
    public static final String ExpertiseRegApplicationCommentAdd = ExpertiseRegApplication + "/comment_add";
    public static final String ExpertiseRegApplicationCommentFileUpload = ExpertiseReg + "/comment/file/upload";
    public static final String ExpertiseRegApplicationCommentDownload = ExpertiseReg + "/comment/file/download";
    public static final String ExpertiseRegApplicationCommentDelete = ExpertiseReg + "/comment/file/delete";
    public static final String ExpertiseRegApplicationGetOkedName = ExpertiseRegApplication + "/get_oked_name";

    public static final String ExpertiseRegApplicationList = ExpertiseRegApplication + "/list";
    public static final String ExpertiseRegApplicationListAjax = ExpertiseRegApplication + "/list_ajax";

    private static final String ExpertiseRegApplicationMonitoring = ExpertiseReg + "/mntr/application";
    public static final String ExpertiseRegApplicationMonitoringList = ExpertiseRegApplicationMonitoring + "/list";
    public static final String ExpertiseRegApplicationMonitoringListAjax = ExpertiseRegApplicationMonitoring + "/list_ajax";
    public static final String ExpertiseRegApplicationMonitoringView = ExpertiseRegApplicationMonitoring + "/view";

    public static final String GetLegalEntityByTin = ExpertiseReg + "/get_legal_entity";
    public static final String GetIndividualByPinfl = ExpertiseReg + "/get_individual";

    //Facture
    private static final String Facture = Prefix + "/facture";
    public static final String FactureList = Facture + "/list";
    public static final String FactureView = Facture + "/view";
}
