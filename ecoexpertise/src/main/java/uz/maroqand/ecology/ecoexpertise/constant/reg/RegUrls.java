package uz.maroqand.ecology.ecoexpertise.constant.reg;

import uz.maroqand.ecology.core.entity.expertise.RegApplication;

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

    // for category type 4
    private static final String RegApplicationFourCategory = RegApplication + "/category_four";
    public static final String RegApplicationFourCategoryResume = RegApplicationFourCategory + "/resume";
    public static final String RegApplicationFourCategoryStart = RegApplicationFourCategory + "/start";
    public static final String RegApplicationFourCategoryApplicant = RegApplicationFourCategory + "/applicant";
    public static final String RegApplicationFourCategorySendSMSCode = RegApplicationFourCategory + "/send_sms_code";
    public static final String RegApplicationFourCategoryGetSMSCode = RegApplicationFourCategory + "/get_sms_code";

    public static final String RegApplicationFourCategoryAbout = RegApplicationFourCategory + "/about";
    public static final String RegApplicationFourCategoryGetActivity = RegApplicationFourCategory + "/activity";
    public static final String RegApplicationFourCategoryGetMaterials = RegApplicationFourCategory + "/materials";
    public static final String RegApplicationFourCategoryGetMaterial = RegApplicationFourCategory + "/material";


    public static final String RegApplicationFourCategoryStep3 = RegApplicationFourCategory + "/step3";
    public static final String RegApplicationFourCategoryStep4 = RegApplicationFourCategory + "/step4";

    public static final String RegApplicationFourCategoryAirPoolCreate = RegApplicationFourCategory + "/air_pool_create";
    public static final String RegApplicationFourCategoryAirPoolEdit = RegApplicationFourCategory + "/air_pool_edit";
    public static final String RegApplicationFourCategoryAirPoolDelete = RegApplicationFourCategory + "/air_pool_delete";

    public static final String RegApplicationFourCategoryStep4_2 = RegApplicationFourCategory + "/step4_2";

    public static final String RegApplicationFourCategoryStep4_2FileUpload = RegApplication + "/step4_2_file_upload";
    public static final String RegApplicationFourCategoryStep4_2FileDownload = RegApplication + "/step4_2_file_download";
    public static final String RegApplicationFourCategoryStep4_2FileDelete = RegApplication + "/step4_2_file_delete";

    public static final String RegApplicationFourCategoryStep4_3 = RegApplicationFourCategory + "/step4_3";

    public static final String RegApplicationFourCategoryHarmfulSubstancesAmountCreate = RegApplicationFourCategory + "/harmful_substances_amount_create";
    public static final String RegApplicationFourCategoryHarmfulSubstancesAmountEdit = RegApplicationFourCategory + "/harmful_substances_amount_edit";
    public static final String RegApplicationFourCategoryHarmfulSubstancesAmountDelete = RegApplicationFourCategory + "/harmful_substances_amount_delete";

    public static final String RegApplicationFourCategoryStep4Submit = RegApplicationFourCategory + "/step4_submit";
    public static final String RegApplicationFourCategoryStep5 = RegApplicationFourCategory + "/step5";

    public static final String RegApplicationFourCategoryBoilerCharacteristicsCreate = RegApplicationFourCategory + "/boiler_characteristics_create";
    public static final String RegApplicationFourCategoryBoilerCharacteristicsEdit = RegApplicationFourCategory + "/boiler_characteristics_edit";
    public static final String RegApplicationFourCategoryBoilerCharacteristicsDelete = RegApplicationFourCategory + "/boiler_characteristics_delete";

    public static final String RegApplicationFourCategoryBoilerSave = RegApplicationFourCategory + "/boiler_characteristics_save";
    public static final String RegApplicationFourCategoryBoilerIsSave = RegApplicationFourCategory + "/boiler_characteristics_is_save";

    public static final String RegApplicationFourCategoryStep6 = RegApplicationFourCategory + "/step6";

    public static final String RegApplicationFourCategoryPollutionMeasuresCreate = RegApplicationFourCategory + "/pollution_measures_create";
    public static final String RegApplicationFourCategoryPollutionMeasuresEdit = RegApplicationFourCategory + "/pollution_measures_edit";
    public static final String RegApplicationFourCategoryPollutionMeasuresDelete = RegApplicationFourCategory + "/pollution_measures_delete";

    public static final String RegApplicationFourCategoryStep7 = RegApplicationFourCategory + "/step7";

    public static final String RegApplicationFourCategoryWaiting = RegApplicationFourCategory + "/waiting";
    public static final String RegApplicationFourCategoryContract = RegApplicationFourCategory + "/contract";
    public static final String RegApplicationFourCategoryContractConfirm = RegApplicationFourCategory + "/contract/confirm";

    public static final String RegApplicationFourCategoryPrepayment = RegApplicationFourCategory + "/prepayment";
    public static final String RegApplicationFourCategoryPaymentSendSms = RegApplicationFourCategory + "/payment/send_sms";
    public static final String RegApplicationFourCategoryPaymentConfirmSms = RegApplicationFourCategory + "/payment/confirm_sms";
    public static final String RegApplicationFourCategoryPaymentFree = RegApplicationFourCategory + "/payment/free";
    public static final String RegApplicationFourCategoryStatus = RegApplicationFourCategory + "/status";
    public static final String RegApplicationFourCategoryResend = RegApplicationFourCategory + "/resend";

    public static final String RegApplicationConclusionDownload = Prefix + "/conclusion_file/download";
    public static final String RegApplicationConfirmFacture = RegApplication + "/confirm/facture";
    public static final String RegApplicationCommentAdd = RegApplication + "/comment_add";
    public static final String RegApplicationCommentFileUpload = Prefix + "/comment/file/upload";
    public static final String RegApplicationCommentDownload = Prefix + "/comment/file/download";
    public static final String RegApplicationCommentDelete = Prefix + "/comment/file/delete";
    public static final String RegApplicationGetOkedName = RegApplication + "/get_oked_name";

    public static final String RegApplicationList = RegApplication + "/list";
    public static final String RegApplicationWaitingList = RegApplication + "/waiting_list";// tasdiqini kutayotgan shartnomalar ro'yxati
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
    public static final String AppealFileUpload = Appeal + "/file";
    public static final String AppealImages = Appeal + "/images";

    public static final String GetLegalEntityByTin = Prefix + "/get_legal_entity";
    public static final String GetIndividualByPinfl = Prefix + "/get_individual";

}
