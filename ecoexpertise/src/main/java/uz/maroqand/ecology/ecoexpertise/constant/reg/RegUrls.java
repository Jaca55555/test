package uz.maroqand.ecology.ecoexpertise.constant.reg;

public class RegUrls {
    private static final String Prefix = "/reg";

    private static final String RegApplication = Prefix + "/application";

    public static final String RegApplicationDashboard = RegApplication + "/dashboard";
    public static final String RegApplicationResume = RegApplication + "/resume";
    public static final String RegApplicationStart = RegApplication + "/start";
    public static final String RegApplicationApplicantCancel = RegApplication + "/cancel";
    public static final String RegApplicationApplicant = RegApplication + "/applicant";
    public static final String RegApplicationSendSMSCode = RegApplication + "/send_sms_code";
    public static final String RegApplicationGetSMSCode = RegApplication + "/get_sms_code";

    public static final String RegApplicationAbout = RegApplication + "/about";
    public static final String RegApplicationClearCoordinates = RegApplication + "/clear_coordinates";
    public static final String RegApplicationGetActivity = RegApplication + "/activity";
    public static final String RegApplicationGetMaterials = RegApplication + "/materials";
    public static final String RegApplicationGetMaterial = RegApplication + "/material";
    public static final String RegApplicationFileUpload = RegApplication + "/file_upload";
    public static final String RegApplicationFileDownload = RegApplication + "/file_download";
    public static final String RegApplicationFileDelete = RegApplication + "/file_delete";

    public static final String RegApplicationWaiting = RegApplication + "/waiting";
    public static final String RegApplicationContract = RegApplication + "/contract";
    public static final String RegApplicationContractConfirm = RegApplication + "/contract/confirm";
    public static final String RegApplicationContractOfferDownload = RegApplication + "/contract/offer_download";

    public static final String RegApplicationPrepayment = RegApplication + "/prepayment";
    public static final String RegApplicationPaymentSendSms = RegApplication + "/payment/send_sms";
    public static final String RegApplicationPaymentConfirmSms = RegApplication + "/payment/confirm_sms";
    public static final String RegApplicationPaymentFree = RegApplication + "/payment/free";
    public static final String RegApplicationStatus = RegApplication + "/status";
    public static final String RegApplicationResend = RegApplication + "/resend";

    public static final String RegApplicationConclusionDownload = Prefix + "/conclusion_file/download";
    public static final String RegApplicationConfirmFacture = RegApplication + "/confirm/facture";
    public static final String RegApplicationCommentAdd = RegApplication + "/comment_add";
    public static final String RegApplicationCommentFileUpload = Prefix + "/comment/file/upload";
    public static final String RegApplicationCommentDownload = Prefix + "/comment/file/download";
    public static final String RegApplicationCommentDelete = Prefix + "/comment/file/delete";
    public static final String RegApplicationGetOkedName = RegApplication + "/get_oked_name";

    public static final String RegApplicationList = RegApplication + "/list";
    public static final String RegApplicationListAjax = RegApplication + "/list_ajax";

    private static final String Appeal = Prefix + "/appeal";
    public static final String AppealUserList = Appeal + "/list";
    public static final String AppealUserListAjax = Appeal + "/list_ajax";
    public static final String AppealNew = Appeal + "/new";
    public static final String AppealCreate = Appeal + "/create";
    public static final String AppealEdit = Appeal + "/edit";
    public static final String AppealUpdate = Appeal + "/update";
    public static final String AppealDelete = Appeal + "/delete";
    public static final String AppealUserView = Appeal + "/view";
    public static final String AppealSubCreate = Appeal + "/sub_create";

    public static final String GetLegalEntityByTin = Prefix + "/get_legal_entity";
    public static final String GetIndividualByPinfl = Prefix + "/get_individual";

}
