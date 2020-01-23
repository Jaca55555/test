package uz.maroqand.ecology.docmanagment.constant;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz)
 * (ru)
 */
public class DocTemplates {
    private static final String Prefix = "doc";

    public static final String Dashboard = Prefix + "/office";

    private static final String IncomeMail = Prefix + "/income_mail";
    public static final String IncomeMailList = IncomeMail + "/list";
    public static final String IncomeMailListAjax = IncomeMail + "/list_ajax";
    public static final String IncomeMailView = IncomeMail + "/view";
    public static final String IncomeMailNew = IncomeMail + "/new";
    public static final String IncomeMailEdit = IncomeMail + "/new";
    public static final String IncomeMailExecuteEdit = IncomeMail + "/execute_edit";

    private static final String CommunicationToolsPrefix = Prefix + "/communication_tools";
    public static final String CommunicationToolsList = CommunicationToolsPrefix + "/list";
    public static final String CommunicationToolsNew = CommunicationToolsPrefix + "/new";
    public static final String CommunicationToolsEdit = CommunicationToolsPrefix + "/edit";

    private static final String DocumentViewPrefix = Prefix + "/document_view";
    public static final String DocumentViewList = DocumentViewPrefix + "/list";
    public static final String DocumentViewEdit = DocumentViewPrefix + "/edit";
    public static final String DocumentViewNew = DocumentViewPrefix + "/new";

    private static final String Folder = Prefix + "/folder";
    public static final String FolderList = Folder+ "/list";
    public static final String FolderEdit = Folder + "/edit";
    public static final String FolderNew = Folder + "/new";
}
