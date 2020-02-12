package uz.maroqand.ecology.docmanagement.constant;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz)
 * (ru)
 */
public class DocUrls {
    private static final String Prefix = "/doc";
    private static final String Registration = "/registration";
    private static final String Incoming = "/incoming"; //kiruvchi xujjatlar
    private static final String Outgoing = "/outgoing"; //chiquvchi xujjatlar
    private static final String Inner = "/inner"; //Ichki xujjatlar
    private static final String Appeal = "/appeal"; //Murojaatlar
    private static final String Orders = "/orders"; //Buyruqlar

    public static final String Dashboard = Prefix + "/office";
    public static final String Settings = Prefix + "/settings";
    public static final String FileDownload = Prefix + "/file/download";

    public static final String IncomingList = Prefix + Incoming + "/list"; //kiruvchi xatlar
    public static final String OutgoingList = Prefix + Outgoing + "/list"; //chiquvchi xatlar
    public static final String InnerList = Prefix + Inner + "/list"; //Ichki xujjatlar
    public static final String AppealList = Prefix + Appeal + "/list"; //Murojaatlar
    public static final String OrdersList = Prefix + Orders + "/list"; //Buyruqlar

    private static final String IncomeMail = Prefix + "/income_mail";
    public static final String IncomeMailList = IncomeMail + "/list";
    public static final String IncomeMailListAjax = IncomeMail + "/list_ajax";
    public static final String IncomeMailView = IncomeMail + "/view";
    public static final String IncomeMailNew = IncomeMail + "/new";
    public static final String IncomeMailEdit = IncomeMail + "/edit";
    public static final String IncomeMailFileUpload = IncomeMail + "/file";
    public static final String IncomeMailSpecial = IncomeMail + "/special";
    public static final String IncomeMailAddTask = IncomeMail + "/task";

    //Chiquvchi hujjatlarni ro'yhatga olish
    private static final String OutgoingRegistration = Prefix + Registration + Outgoing;
    public static final String OutgoingRegistrationList = OutgoingRegistration + "/list";

    //Ichki hujjatlarni ro'yhatga olish
    private static final String InnerRegistration = Prefix + Registration + Inner;
    public static final String InnerRegistrationList = InnerRegistration + "/list";

    //Murojaatlarni ro'yhatga olish
    private static final String AppealRegistration = Prefix + Registration + Appeal;
    public static final String AppealRegistrationList = AppealRegistration + "/list";

    private static final String CommunicationTools = Prefix +  "/communication_tools";
    public static final String CommunicationToolsList = CommunicationTools + "/list";
    public static final String CommunicationToolsListAjax = CommunicationToolsList + "/ajax";
    public static final String CommunicationToolsNew = CommunicationTools + "/new";
    public static final String CommunicationToolsEdit = CommunicationTools + "/edit";
    public static final String CommunicationToolsEditStatus = CommunicationToolsEdit + "/status";
    public static final String CommunicationToolsEditSubmit = CommunicationToolsEdit + "/submit";

    private static final String DocType = Prefix + "/doc_type";
    public static final String DocTypeList = DocType + "/list";
    public static final String DocTypeListAjax = DocType + "/list_ajax";
    public static final String DocTypeView = DocType + "/view";
    public static final String DocTypeNew = DocType + "/new";
    public static final String DocTypeEdit = DocType + "/edit";
    public static final String DocTypeDelete = DocType + "/delete";

    private static final String Journal = Prefix + "/journal";
    public static final String JournalList = Journal + "/list";
    public static final String JournalListAjax = Journal + "/list_ajax";
    public static final String JournalView = Journal + "/view";
    public static final String JournalNew = Journal + "/new";
    public static final String JournalEdit = Journal + "/edit";
    public static final String JournalDelete = Journal + "/delete";

    private static final String DocumentView = Prefix + "/document_view";
    public static final String DocumentViewList = DocumentView + "/list";
    public static final String DocumentViewListAjax = DocumentViewList + "/ajax";
    public static final String DocumentViewNew = DocumentView + "/new";
    public static final String DocumentViewEdit = DocumentView + "/edit";
    public static final String DocumentViewEditStatus = DocumentViewEdit + "/status";

    private static final String Folder = Prefix + "/folder";
    public static final String FolderList = Folder + "/list";
    public static final String FolderListAjax = Folder + "/ajax";
    public static final String FolderNew = Folder + "/new";
    public static final String FolderEdit = Folder + "/edit";

    private static final String DocumentOrganization = Prefix + "/document_organization";
    public static final String DocumentOrganizationList = DocumentOrganization + "/list";
    public static final String DocumentOrganizationListAjax = DocumentOrganizationList + "/ajax";
    public static final String DocumentOrganizationNew = DocumentOrganization + "/new";
    public static final String DocumentOrganizationEdit = DocumentOrganization + "/edit";
    public static final String DocumentOrganizationEditStatus = DocumentOrganizationEdit + "/status";
}
