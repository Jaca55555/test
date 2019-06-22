package uz.maroqand.ecology.cabinet.constant.expertise;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
public class ExpertiseUrls {
    private static final String Prefix = "/expertise";

    private static final String Accountant = Prefix + "/accountant";

    public static final String AccountantList = Accountant + "/list";
    public static final String AccountantListAjax = Accountant + "/list_ajax";

    public static final String AccountantChecking = Accountant + "/checking";
    public static final String AccountantConfirm = Accountant + "/confirm";
    public static final String AccountantNotConfirm = Accountant + "/not_confirm";
    public static final String AccountantFileDownload = Accountant + "/download";

    private static final String Forwarding = Prefix + "/forwarding";
    public static final String ForwardingList = Forwarding + "/list";
    public static final String ForwardingListAjax = Forwarding + "/list_ajax";
    public static final String ForwardingChecking = Forwarding + "/checking";
    public static final String ForwardingFileUpload = Forwarding + "/file_upload";
    public static final String ForwardingFileDownload = Forwarding + "/file_download";
    public static final String ForwardingFileDelete = Forwarding + "/file_delete";

    private static final String Performer = Prefix + "/performer";
    public static final String PerformerList = Performer + "/list";
    public static final String PerformerListAjax = Performer + "/list_ajax";

    private static final String Agreement = Prefix + "/agreement";
    public static final String AgreementList = Agreement + "/list";
    public static final String AgreementListAjax = Agreement + "/list_ajax";

    private static final String AgreementComplete = Prefix + "/agreement_complete";
    public static final String AgreementCompleteList = AgreementComplete + "/list";
    public static final String AgreementCompleteListAjax = AgreementComplete + "/list_ajax";

    private static final String Material = Prefix + "/material";
    public static final String MaterialList = Material + "/list";
    public static final String MaterialListAjax = Material + "/list_ajax";
    public static final String MaterialNew = Material + "/new";
    public static final String MaterialEdit = Material + "/edit";

    private static final String Offer = Prefix + "/offer";
    public static final String OfferList = Offer + "/list";
    public static final String OfferListAjax = Offer + "/list_ajax";
    public static final String OfferNew = Offer + "/new";
    public static final String OfferEdit = Offer + "/edit";

}
