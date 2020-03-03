package uz.maroqand.ecology.docmanagement.constant;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz)
 * (ru)
 */
public class DocTemplates {
    private static final String Prefix = "doc";
    private static final String Registration = "/registration";
    private static final String Incoming = "/incoming";
    private static final String Outgoing = "/outgoing";
    private static final String Inner = "/inner";
    private static final String DocumentCheck="/document_check";

    public static final String Dashboard = Prefix + "/office";

    //kiruvchi xatlar
    public static final String IncomingList = Prefix + Incoming + "/list";
    public static final String IncomingView = Prefix + Incoming + "/view";
    public static final String IncomingTask = Prefix + Incoming + "/task";


    //xatlarni tasdiqlash
    public static final String DocumentCheckList=Prefix + DocumentCheck+"/list";
    public static final String DocumentCheckTask = Prefix + DocumentCheck + "/task";
    public static final String DocumentCheckView = Prefix + DocumentCheck + "/view";

    //chiquvchi xatlar
    public static final String OutgoingList = Prefix + Outgoing + "/list";
    public static final String OutgoingView = Prefix + Outgoing + "/view";

    private static final String UserManagement = Prefix + "/user_mngm";
    public static final String Controllers = UserManagement + "/controllers";

    private static final String IncomingRegistration = Prefix + Registration + Incoming;
    public static final String IncomingRegistrationList = IncomingRegistration + "/list";
    public static final String IncomingRegistrationNewList = IncomingRegistration + "/new_list";
    public static final String IncomingRegistrationView = IncomingRegistration + "/view";
    public static final String IncomingRegistrationNew = IncomingRegistration + "/new";
    public static final String IncomingRegistrationTask = IncomingRegistration + "/task";

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

    private static final String LibraryCategory = Prefix + "/library_category";
    public static final String LibraryCategoryList = LibraryCategory + "/list";
    public static final String LibraryCategoryView = LibraryCategory + "/view";
    public static final String LibraryCategoryNew = LibraryCategory + "/new";
    public static final String LibraryCategoryEdit = LibraryCategory + "/new";

    private static final String Library = Prefix + "/library";
    public static final String LibraryList = Library + "/list";
    public static final String LibraryView = Library + "/view";
    public static final String LibraryNew = Library + "/new";
    public static final String LibraryEdit = Library + "/new";
    public static final String LibraryWindow = Library + "/library";

    private static final String DocumentOrganization = Prefix + "/document_organization";
    public static final String DocumentOrganizationList = DocumentOrganization + "/list";
    public static final String DocumentOrganizationEdit = DocumentOrganization + "/edit";
    public static final String DocumentOrganizationNew = DocumentOrganization + "/new";

    private static final String Journal = Prefix + "/journal";
    public static final String JournalList = Journal + "/list";
    public static final String JournalView = Journal + "/view";
    public static final String JournalNew = Journal + "/new";
    public static final String JournalEdit = Journal + "/new";

    private static final String DocDescription = Prefix + "/doc_description";
    public static final String DocDescriptionList = DocDescription + "/list";
    public static final String DocDescriptionNew = DocDescription + "/new";
    public static final String DocDescriptionEdit = DocDescription + "/new";
    public static final String DocDescriptionView = DocDescription + "/view";

    private static final String OutgoingMail = Prefix + "/outgoing_mail";
    public static final String OutgoingMailNew = OutgoingMail + "/new";
    public static final String OutgoingMailList = OutgoingMail + "/list";
    public static final String OutgoingMailView = OutgoingMail + "/view";
    public static final String OutgoingMailEdit = OutgoingMail + "/edit";

    public static final String InnerList = Prefix + Inner + "/list";
    public static final String InnerView = Prefix + Inner + "/view";

    //ichki xatlar
    public static final String InnerRegistrationNew = Prefix + Registration + Inner + "/new";
    public static final String InnerRegistrationList = Prefix + Registration + Inner + "/list";
    public static final String InnerRegistrationTask = Prefix + Registration + Inner + "/task";
    public static final String InnerRegistrationView = Prefix + Registration + Inner + "/view";
}
