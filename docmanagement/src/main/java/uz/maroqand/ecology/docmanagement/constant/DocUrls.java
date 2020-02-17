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
    public static final String AddComment = Prefix + "/add_comment";
    public static final String FileDownload = Prefix + "/file/download";

    public static final String IncomingList = Prefix + Incoming + "/list"; //kiruvchi xatlar
    public static final String IncomingView = Prefix + Incoming + "/view"; //kiruvchi xatni ko'rish
    public static final String OutgoingList = Prefix + Outgoing + "/list"; //chiquvchi xatlar
    public static final String OutgoingView = Prefix + Outgoing + "/view"; //chiquvchi xatni ko'rish

    public static final String InnerList = Prefix + Inner + "/list"; //Ichki xujjatlar
    public static final String InnerView = Prefix + Inner + "/view"; //Ichki ko'rish
    public static final String AppealList = Prefix + Appeal + "/list"; //Murojaatlar
    public static final String OrdersList = Prefix + Orders + "/list"; //Buyruqlar

    public static final String RegistrationAdditionalDocument = Prefix + Registration + "/add_document";
    public static final String RegistrationOrganization = Prefix + Registration + "/organization";

    private static final String IncomingRegistration = Prefix + Registration + Incoming;
    public static final String IncomingRegistrationList = IncomingRegistration + "/list";
    public static final String IncomingRegistrationNewList = IncomingRegistration + "/new_list";
    public static final String IncomingRegistrationView = IncomingRegistration + "/view";
    public static final String IncomingRegistrationNew = IncomingRegistration + "/new";
    public static final String IncomingRegistrationNewTask = IncomingRegistration + "/new_and_task";
    public static final String IncomingRegistrationEdit = IncomingRegistration + "/edit";
    public static final String IncomingRegistrationEditTask = IncomingRegistration + "/edit_and_task";
    public static final String IncomingRegistrationTask = IncomingRegistration + "/task";
    public static final String IncomingRegistrationTaskSubmit = IncomingRegistration + "/task_submit";
    public static final String IncomingRegistrationUserName = IncomingRegistration + "/user_name";

    public static final String IncomeMailFileUpload = IncomingRegistration + "/file";
    public static final String IncomingSpecialControll = IncomingRegistration + "/special";
//    public static final String IncomeMailAddTask = IncomingRegistration + "/task";

    //Chiquvchi hujjatlarni ro'yhatga olish
    private static final String OutgoingRegistration = Prefix + Registration + Outgoing;
    public static final String OutgoingRegistrationList = OutgoingRegistration + "/list";

    //Ichki hujjatlarni ro'yhatga olish
    private static final String InnerRegistration = Prefix + Registration + Inner;
    public static final String InnerRegistrationList = InnerRegistration + "/list";
    public static final String InnerRegistrationNew = InnerRegistration + "/new";
    public static final String InnerRegistrationView = InnerRegistration + "/view";
    public static final String InnerRegistrationEdit = InnerRegistration + "/edit";
    public static final String InnerRegistrationNewTask = InnerRegistration + "/new_and_task";
    public static final String InnerRegistrationEditTask = InnerRegistration + "/edit_and_task";
    public static final String InnerRegistrationFileUpload = InnerRegistration + "/file";
    public static final String InnerRegistrationTask = InnerRegistration + "/task";
    public static final String InnerRegistrationTaskSubmit = InnerRegistration + "/task_submit";
    public static final String InnerRegistrationUserName = InnerRegistration + "/user_name";

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

    private static final String UserManagement = Prefix + "/user_mngm";
    public static final String Chiefs = UserManagement + "/chiefs";
    public static final String Change = UserManagement + "/change";
    public static final String Controllers = UserManagement + "/controllers";

    private static final String DocumentOrganization = Prefix + "/document_organization";
    public static final String DocumentOrganizationList = DocumentOrganization + "/list";
    public static final String DocumentOrganizationListAjax = DocumentOrganizationList + "/ajax";
    public static final String DocumentOrganizationNew = DocumentOrganization + "/new";
    public static final String DocumentOrganizationEdit = DocumentOrganization + "/edit";
    public static final String DocumentOrganizationEditStatus = DocumentOrganizationEdit + "/status";

     private static final String OutgoingMail = Prefix + "/outgoing_mail";
     public static final String OutgoingMailNew = OutgoingMail + "/new";
     public static final String OutgoingMailList = OutgoingMail + "/list";
     public static final String OutgoingMailOrganizationList = OutgoingMail + "/organization_names";
     public static final String OutgoingMailListAjax = OutgoingMailList + "/ajax";
     public static final String OutgoingMailView = OutgoingMail + "/view";
     public static final String OutgoingMailEdit = OutgoingMail + "/edit";
     public static final String OutgoingMailFile = OutgoingMail + "/file";
     public static final String OutgoingMailFileUpload = OutgoingMailFile + "/upload";
     public static final String OutgoingMailFileDownload = OutgoingMailFile + "/download";
     public static final String OutgoingMailFileDelete = OutgoingMailFile + "/delete";
}
