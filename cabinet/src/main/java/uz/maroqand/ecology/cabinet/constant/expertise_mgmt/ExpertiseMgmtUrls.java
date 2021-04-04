package uz.maroqand.ecology.cabinet.constant.expertise_mgmt;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 */
public class ExpertiseMgmtUrls {
    private static final String Prefix = "/mgmt/expertise";

    //ObjectExpertise
    private static final String ObjectExpertise = Prefix + "/object_expertise";
    public static final String ObjectExpertiseList = ObjectExpertise + "/list";
    public static final String ObjectExpertiseNew = ObjectExpertise + "/new";
    public static final String ObjectExpertiseEdit = ObjectExpertise + "/edit";
    public static final String ObjectExpertiseCreate = ObjectExpertise + "/create";
    public static final String ObjectExpertiseUpdate = ObjectExpertise + "/update";
    public static final String ObjectExpertiseView = ObjectExpertise + "/view";
    public static final String ObjectExpertiseDelete = ObjectExpertise + "/delete";

    //Activity
    private static final String Activity = Prefix + "/activity";
    public static final String ActivityList = Activity + "/list";
    public static final String ActivityListAjax = Activity + "/ajax_list";
    public static final String ActivityNew = Activity + "/new";
    public static final String ActivityEdit = Activity + "/edit";
    public static final String ActivityCreate = Activity + "/create";
    public static final String ActivityUpdate = Activity + "/update";
    public static final String ActivityView = Activity + "/view";

    //Material
    private static final String Material = Prefix + "/material";
    public static final String MaterialList = Material + "/list";
    public static final String MaterialListAjax = Material + "/list_ajax";
    public static final String MaterialNew = Material + "/new";
    public static final String MaterialEdit = Material + "/edit";
    public static final String MaterialView = Material + "/view";
    public static final String MaterialDelete = Material + "/delete";

    //Substance
    private static final String Substance = Prefix + "/substance";
    public static final String SubstanceList = Substance + "/list";
    public static final String SubstanceListAjax = Substance + "/list_ajax";
    public static final String SubstanceNew = Substance + "/new";
    public static final String SubstanceEdit = Substance + "/edit";
    public static final String SubstanceView = Substance + "/view";
    public static final String SubstanceDelete = Substance + "/delete";

    //ExpertiseRequirement
    private static final String ExpertiseRequirement = Prefix + "/expertise_requirement";
    public static final String ExpertiseRequirementList = ExpertiseRequirement + "/list";
    public static final String ExpertiseRequirementNew = ExpertiseRequirement + "/new";
    public static final String ExpertiseRequirementEdit = ExpertiseRequirement + "/edit";
    public static final String ExpertiseRequirementCreate = ExpertiseRequirement + "/create";
    public static final String ExpertiseRequirementUpdate = ExpertiseRequirement + "/update";
    public static final String ExpertiseRequirementView = ExpertiseRequirement + "/view";

    //Offerta
    private static final String Offer = Prefix + "/offer";
    public static final String OfferList = Offer + "/list";
    public static final String OfferListAjax = Offer + "/list_ajax";
    public static final String OfferNew = Offer + "/new";
    public static final String OfferEdit = Offer + "/edit";
    public static final String OfferFileUpload = Offer + "/file_upload";
    public static final String OfferFileDownload = Offer + "/file_download";
    public static final String OfferView = Offer + "/view";

}
