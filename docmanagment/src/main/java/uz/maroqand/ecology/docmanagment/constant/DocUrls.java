package uz.maroqand.ecology.docmanagment.constant;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz)
 * (ru)
 */
public class DocUrls {
    private static final String Prefix = "/doc";

    public static final String Dashboard = Prefix + "/office";

    private static final String IncomeMail = Prefix + "/income_mail";
    public static final String IncomeMailList = IncomeMail + "/list";
    public static final String IncomeMailListAjax = IncomeMail + "/list_ajax";
    public static final String IncomeMailView = IncomeMail + "/view";
    public static final String IncomeMailCreate = IncomeMail + "/create";
    public static final String IncomeMailNew = IncomeMail + "/new";
    public static final String IncomeMailEdit = IncomeMail + "/edit";
    public static final String IncomeMailExecuteEdit = IncomeMail + "/execute_edit";

    private static final String CommunicationTools = Prefix +  "/communication_tools";
    public static final String CommunicationToolsList = CommunicationTools + "/list";
    public static final String CommunicationToolsListAjax = CommunicationToolsList + "/ajax";
    public static final String CommunicationToolsNew = CommunicationTools + "/new";
    public static final String CommunicationToolsEdit = CommunicationTools + "/edit";
    public static final String CommunicationToolsEditStatus = CommunicationToolsEdit + "/status";
    public static final String CommunicationToolsEditSubmit = CommunicationToolsEdit + "/submit";

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
}
