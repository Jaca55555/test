package uz.maroqand.ecology.docmanagement.constant;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz)
 * (ru)
 */
public class DocTemplates {
    private static final String Prefix = "/doc";

    public static final String Dashboard = Prefix + "/office";

    private static final String IncomeMail = Prefix + "/income_mail";
    public static final String IncomeMailList = IncomeMail + "/list";
    public static final String IncomeMailView = IncomeMail + "/view";
    public static final String IncomeMailNew = IncomeMail + "/new";
    public static final String IncomeMailEdit = IncomeMail + "/new";

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

    private static final String DocType = Prefix + "/doc_type";
    public static final String DocTypeList = DocType + "/list";
    public static final String DocTypeView = DocType + "/view";
    public static final String DocTypeNew = DocType + "/new";
    public static final String DocTypeEdit = DocType + "/new";

    private static final String DocumentOrganization = Prefix + "/document_organization";
    public static final String DocumentOrganizationList = DocumentOrganization + "/list";
    public static final String DocumentOrganizationEdit = DocumentOrganization + "/edit";
    public static final String DocumentOrganizationNew = DocumentOrganization + "/new";

    private static final String Journal = Prefix + "/journal";
    public static final String JournalList = Journal + "/list";
    public static final String JournalView = Journal + "/view";
    public static final String JournalNew = Journal + "/new";
    public static final String JournalEdit = Journal + "/new";

    private static final String OutgoingMail = Prefix + "/outgoing_mail";
    public static final String OutgoingMailNew = OutgoingMail + "/new";

}
