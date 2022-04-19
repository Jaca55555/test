package uz.maroqand.ecology.cabinet.constant.expertise;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
public class ExpertiseUrls {
    private static final String Prefix = "/expertise";
    private static final String  ExpertiseReg = Prefix + "/reg";

    public static final String InvoiceModification = Prefix + "/invoice_modification";

    //API
    public static final String Api = Prefix + "/api";
    public static final String ApiView = Api + "/view";
    public static final String ApiList = Api + "/list";

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
    public static final String ForwardingChangePerformer = Forwarding + "/change_performer";
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
    public static final String PerformerConclusionIsOnline = Performer + "/conclusion_is_online";

    public static final String RegApplicationBoilerCharacteristicsEditType1 = Performer + "/boiler_characteristics_edit1";
    public static final String RegApplicationBoilerCharacteristicsEditType2 = Performer + "/boiler_characteristics_edit2";
    public static final String RegApplicationBoilerCharacteristicsEditType3 = Performer + "/boiler_characteristics_edit3";
    public static final String RegApplicationBoilerCharacteristicsConfirm = Performer + "/boiler_characteristics_confirm";
    public static final String RegApplicationBoilerCharacteristicsCheck = Performer + "/boiler_characteristics_check";
    public static final String RegApplicationBoilerCharacteristicsDelete = Performer + "/boiler_characteristics_delete";

    public static final String RegApplicationBoilerSave = Performer + "/boiler_characteristics_save";
    public static final String RegApplicationBoilerIsSave = Performer + "/boiler_characteristics_is_save";

    public static final String PerformerConclusionSave = Performer + "/conclusion_save";
    public static final String PerformerConclusionIsSave = Performer + "/conclusion_is_save";
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

    //rais tasdiqidan keyin xulosaga number va sana qo'yish
    private static final String ConclusionComplete = Prefix + "/conclusion_complete";
    public static final String ConclusionCompleteList = ConclusionComplete + "/list";
    public static final String ConclusionCompleteListAjax = ConclusionComplete + "/list_ajax";
    public static final String ConclusionCompleteView = ConclusionComplete + "/view";
    public static final String ConclusionCompleteAction = ConclusionComplete + "/action";


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
    public static final String BillingInvoiceIsNol = Billing + "/amount_is_nol";
    public static final String BillingDelete = Billing + "/delete";

    //Report
    private static final String Report = Prefix + "/report";
    public static final String ReportList = Report + "/list";
    public static final String ReportListAjax = Report + "/list_ajax";
    public static final String ReportSoato= Report+"/soato";
    public static final String ReportOrganization= Report+"/organization";

    //Applicant
    private static final String Applicant = Prefix + "/applicant";
    public static final String ApplicantList = Applicant + "/list";
    public static final String ApplicantListAjax = Applicant + "/list_ajax";
    public static final String ApplicantView = Applicant + "/view";
    public static final String ApplicantConclusionView = Applicant + "/conclusion_view";
    public static final String ApplicantUpdateTax = Applicant + "/update_tax";

    //Employee control
    private static final String EmployeeControl= Prefix + "/employee_control";
    public static final String EmployeeControls= EmployeeControl + "s";
    public static final String EmployeeControlList = EmployeeControl + "/list";
    public static final String EmployeeControlListForward = EmployeeControl + "/list_forward";

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
    public static final String ConclusionRegApplicationListAjax = Conclusion + "/reg_list_ajax"; // elektron yuklangan
    public static final String ConclusionRegApplicationView = Conclusion + "/reg_view";
    public static final String ConclusionNewList = Conclusion + "/new_list";
    public static final String ConclusionFileUpload = Conclusion + "/file_upload";
    public static final String ConclusionFileDownload = Conclusion + "/file_download";
    public static final String ConclusionFileDownloadForView = Conclusion + "/file_download_for_view";
    public static final String ConclusionRegApplicationConclusionIdFileDownloadForView = Conclusion + "/reg_file_download_for_view";
    public static final String ConclusionRegApplicationConclusionIdWordFileDownloadForView = Conclusion + "/reg_word_file_download_for_view";
    public static final String ConclusionFileAdd = Conclusion + "/file_add";


    private static final String ExpertiseRegApplication = ExpertiseReg + "/application";

    public static final String ExpertiseRegApplicationDashboard = ExpertiseRegApplication + "/dashboard";
    public static final String ExpertiseRegApplicationResume = ExpertiseRegApplication + "/resume";
    public static final String ExpertiseRegApplicationStart = ExpertiseRegApplication + "/start";
    public static final String ExpertiseRegApplicationApplicantCancel = ExpertiseRegApplication + "/cancel";
    public static final String ExpertiseRegApplicationApplicant = ExpertiseRegApplication + "/applicant";
    public static final String ExpertiseRegApplicationSendSMSCode = ExpertiseRegApplication + "/send_sms_code";
    public static final String ExpertiseRegApplicationGetSMSCode = ExpertiseRegApplication + "/get_sms_code";





    public static final String RegApplicationFourCategoryBoilerCharacteristicsCreate = ExpertiseRegApplication + "/boiler_characteristics_create";
    public static final String RegApplicationFourCategoryBoilerCharacteristicsEditType1 = ExpertiseRegApplication + "/boiler_characteristics_edit1";
    public static final String RegApplicationFourCategoryBoilerCharacteristicsEditType2 = ExpertiseRegApplication + "/boiler_characteristics_edit2";
    public static final String RegApplicationFourCategoryBoilerCharacteristicsEditType3 = ExpertiseRegApplication + "/boiler_characteristics_edit3";
    public static final String RegApplicationFourCategoryBoilerCharacteristicsDelete = ExpertiseRegApplication + "/boiler_characteristics_delete";

    public static final String RegApplicationFourCategoryBoilerSave = ExpertiseRegApplication + "/boiler_characteristics_save";
    public static final String RegApplicationFourCategoryBoilerIsSave = ExpertiseRegApplication + "/boiler_characteristics_is_save";










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


    // for category type 4
    private static final String RegApplicationFourCategory = ExpertiseRegApplication + "/category_four";
    public static final String ExpertiseRegApplicationFourCategoryResume = RegApplicationFourCategory + "/resume";
    public static final String ExpertiseRegApplicationFourCategoryStart = RegApplicationFourCategory + "/start";
    public static final String ExpertiseRegApplicationFourCategoryApplicant = RegApplicationFourCategory + "/applicant";
    public static final String ExpertiseRegApplicationFourCategorySendSMSCode = RegApplicationFourCategory + "/send_sms_code";
    public static final String ExpertiseRegApplicationFourCategoryGetSMSCode = RegApplicationFourCategory + "/get_sms_code";

    public static final String ExpertiseRegApplicationFourCategoryAbout = RegApplicationFourCategory + "/about";
    public static final String ExpertiseRegApplicationFourCategoryGetActivity = RegApplicationFourCategory + "/activity";
    public static final String ExpertiseRegApplicationFourCategoryGetMaterials = RegApplicationFourCategory + "/materials";
    public static final String ExpertiseRegApplicationFourCategoryGetMaterial = RegApplicationFourCategory + "/material";


    public static final String ExpertiseRegApplicationFourCategoryStep3 = RegApplicationFourCategory + "/step3";

    public static final String ExpertiseRegApplicationFourCategoryStep3FileUpload = RegApplicationFourCategory + "/step3_file_upload";
    public static final String ExpertiseRegApplicationFourCategoryStep3FileDownload = RegApplicationFourCategory + "/step3_file_download";
    public static final String ExpertiseRegApplicationFourCategoryStep3FileDelete = RegApplicationFourCategory + "/step3_file_delete";

    public static final String ExpertiseRegApplicationFourCategoryStep4 = RegApplicationFourCategory + "/step4";

    public static final String ExpertiseRegApplicationFourCategoryAirPoolCreate = RegApplicationFourCategory + "/air_pool_create";
    public static final String ExpertiseRegApplicationFourCategoryAirPoolEdit = RegApplicationFourCategory + "/air_pool_edit";
    public static final String ExpertiseRegApplicationFourCategoryAirPoolDelete = RegApplicationFourCategory + "/air_pool_delete";

    public static final String ExpertiseRegApplicationFourCategoryStep4_2 = RegApplicationFourCategory + "/step4_2";

    public static final String ExpertiseRegApplicationFourCategoryStep4_2FileUpload = ExpertiseRegApplication + "/step4_2_file_upload";
    public static final String ExpertiseRegApplicationFourCategoryStep4_2FileDownload = ExpertiseRegApplication + "/step4_2_file_download";
    public static final String ExpertiseRegApplicationFourCategoryStep4_2FileDelete = ExpertiseRegApplication + "/step4_2_file_delete";

    public static final String ExpertiseRegApplicationFourCategoryDescriptionOfSourcesAdditionalCreate = RegApplicationFourCategory + "/description_of_sources_additional_create";
    public static final String ExpertiseRegApplicationFourCategoryDescriptionOfSourcesAdditionalEdit = RegApplicationFourCategory + "/description_of_sources_additional_edit";
    public static final String ExpertiseRegApplicationFourCategoryDescriptionOfSourcesAdditionalDelete = RegApplicationFourCategory + "/description_of_sources_additional_delete";

    public static final String ExpertiseRegApplicationFourCategoryStep4_3 = RegApplicationFourCategory + "/step4_3";

    public static final String ExpertiseRegApplicationFourCategoryHarmfulSubstancesAmountCreate = RegApplicationFourCategory + "/harmful_substances_amount_create";
    public static final String ExpertiseRegApplicationFourCategoryHarmfulSubstancesAmountEdit = RegApplicationFourCategory + "/harmful_substances_amount_edit";
    public static final String ExpertiseRegApplicationFourCategoryHarmfulSubstancesAmountDelete = RegApplicationFourCategory + "/harmful_substances_amount_delete";

    public static final String ExpertiseRegApplicationFourCategoryStep4Submit = RegApplicationFourCategory + "/step4_submit";
    public static final String ExpertiseRegApplicationFourCategoryStep5 = RegApplicationFourCategory + "/step5";

    public static final String ExpertiseRegApplicationFourCategoryBoilerCharacteristicsCreate = RegApplicationFourCategory + "/boiler_characteristics_create";
    public static final String ExpertiseRegApplicationFourCategoryBoilerCharacteristicsEdit1 = RegApplicationFourCategory + "/boiler_characteristics_edit";
    public static final String ExpertiseRegApplicationFourCategoryBoilerCharacteristicsDelete = RegApplicationFourCategory + "/boiler_characteristics_delete";

    public static final String ExpertiseRegApplicationFourCategoryBoilerSave = RegApplicationFourCategory + "/boiler_characteristics_save";
    public static final String ExpertiseRegApplicationFourCategoryBoilerIsSave = RegApplicationFourCategory + "/boiler_characteristics_is_save";

    public static final String ExpertiseRegApplicationFourCategoryStep6 = RegApplicationFourCategory + "/step6";

    public static final String ExpertiseRegApplicationFourCategoryPollutionMeasuresCreate = RegApplicationFourCategory + "/pollution_measures_create";
    public static final String ExpertiseRegApplicationFourCategoryPollutionMeasuresEdit = RegApplicationFourCategory + "/pollution_measures_edit";
    public static final String ExpertiseRegApplicationFourCategoryPollutionMeasuresDelete = RegApplicationFourCategory + "/pollution_measures_delete";

    public static final String ExpertiseRegApplicationFourCategoryStep7 = RegApplicationFourCategory + "/step7";

    public static final String ExpertiseRegApplicationFourCategoryWaiting = RegApplicationFourCategory + "/waiting";
    public static final String ExpertiseRegApplicationFourCategoryContract = RegApplicationFourCategory + "/contract";
    public static final String ExpertiseRegApplicationFourCategoryContractConfirm = RegApplicationFourCategory + "/contract/confirm";

    public static final String ExpertiseRegApplicationFourCategoryPrepayment = RegApplicationFourCategory + "/prepayment";
    public static final String ExpertiseRegApplicationFourCategoryPaymentSendSms = RegApplicationFourCategory + "/payment/send_sms";
    public static final String ExpertiseRegApplicationFourCategoryPaymentConfirmSms = RegApplicationFourCategory + "/payment/confirm_sms";
    public static final String ExpertiseRegApplicationFourCategoryPaymentFree = RegApplicationFourCategory + "/payment/free";
    public static final String ExpertiseRegApplicationFourCategoryStatus = RegApplicationFourCategory + "/status";
    public static final String ExpertiseRegApplicationFourCategoryResend = RegApplicationFourCategory + "/resend";

    public static final String ExpertiseRegApplicationList = ExpertiseRegApplication + "/list";
    public static final String ExpertiseRegApplicationListAjax = ExpertiseRegApplication + "/list_ajax";
    public static final String ExpertiseGetByUserId = ExpertiseRegApplication+"get_user";

    private static final String ExpertiseRegApplicationMonitoring = ExpertiseReg + "/mntr/application";
    public static final String ExpertiseRegApplicationMonitoringList = ExpertiseRegApplicationMonitoring + "/list";
    public static final String ExpertiseRegApplicationMonitoringListAjax = ExpertiseRegApplicationMonitoring + "/list_ajax";
    public static final String ExpertiseRegApplicationMonitoringView = ExpertiseRegApplicationMonitoring + "/view";
    public static final String ExpertiseRegApplicationMonitoringEdit = ExpertiseRegApplicationMonitoring + "/edit";
    public static final String ExpertiseRegApplicationMonitoringChangePerformer = ExpertiseRegApplicationMonitoring + "/change_performer";
    public static final String ExpertiseRegApplicationMonitoringChangeConclusion = ExpertiseRegApplicationMonitoring + "/change_conclusion";
    public static final String ExpertiseRegApplicationMonitoringPerformerConclusionEdit = ExpertiseRegApplicationMonitoring + "/performer_conclusion_edit"; //agar ariza yopilgan yoki tasdiqlangan bo'lsa xulosani qayta edit qilish uchun
    public static final String ExpertiseRegApplicationMonitoringFileDelete = ExpertiseRegApplicationMonitoring + "/file/delete"; //agar ariza yopilgan yoki tasdiqlangan bo'lsa xulosani qayta edit qilish uchun
    public static final String ExpertiseRegApplicationMonitoringFileUpload = ExpertiseRegApplicationMonitoring + "/file/upload"; //agar ariza yopilgan yoki tasdiqlangan bo'lsa xulosani qayta edit qilish uchun

    public static final String GetLegalEntityByTin = ExpertiseReg + "/get_legal_entity";
    public static final String GetIndividualByPinfl = ExpertiseReg + "/get_individual";

    //Facture
    private static final String Facture = Prefix + "/facture";
    public static final String FactureList = Facture + "/list";
    public static final String FactureView = Facture + "/view";

    private static final String  Agree = Prefix + "/agree";
    public static final String AgreeList = Agree + "/list";
    public static final String AgreeListAjax = Agree + "/list_ajax";
    public static final String AgreeView = Agree + "/view";
}
