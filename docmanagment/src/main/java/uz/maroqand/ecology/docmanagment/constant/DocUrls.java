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
}
