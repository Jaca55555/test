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

}
