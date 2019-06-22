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
    public static final String ConfirmChecking = Confirm + "/checking";
    public static final String ConfirmConfirm = Confirm + "/confirm";
    public static final String ConfirmNotConfirm = Confirm + "/not_confirm";
    public static final String ConfirmFileDownload = Confirm + "/download";

    //Boshqaruv
    private static final String Forwarding = Prefix + "/forwarding";
    public static final String ForwardingList = Forwarding + "/list";
    public static final String ForwardingListAjax = Forwarding + "/list_ajax";
    public static final String ForwardingView = Forwarding + "/view";
    public static final String ForwardingChecking = Forwarding + "/checking";
    public static final String ForwardingFileUpload = Forwarding + "/file_upload";
    public static final String ForwardingFileDownload = Forwarding + "/file_download";
    public static final String ForwardingFileDelete = Forwarding + "/file_delete";

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


}
