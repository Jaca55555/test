package uz.maroqand.ecology.cabinet.constant.mgmt;

public class MgmtUrls {
    private static final String Prefix = "/mgmt";

    private static final String Translations = Prefix + "/translations";

    public static final String TranslationsList = Translations;
    public static final String TranslationsListAjax = Translations + "/ajax_list";
    public static final String TranslationsNew = Translations + "/new";
    public static final String TranslationsEdit = Translations + "/edit";
    public static final String TranslationsCreate = Translations + "/create";
    public static final String TranslationsUpdate = Translations + "/update";
    public static final String TranslationsSearchByTag = Translations + "/search";

    //ObjectExpertise
    private static final String ObjectExpertise = Prefix + "/object_expertise";

    public static final String ObjectExpertiseList = ObjectExpertise + "/list";
    public static final String ObjectExpertiseNew = ObjectExpertise + "/new";
    public static final String ObjectExpertiseEdit = ObjectExpertise + "/edit";
    public static final String ObjectExpertiseCreate = ObjectExpertise + "/create";
    public static final String ObjectExpertiseUpdate = ObjectExpertise + "/update";

    //ExpertiseRequirement
    private static final String ExpertiseRequirement = Prefix + "/expertise_requirement";

    public static final String ExpertiseRequirementList = ExpertiseRequirement + "/list";
    public static final String ExpertiseRequirementNew = ExpertiseRequirement + "/new";
    public static final String ExpertiseRequirementEdit = ExpertiseRequirement + "/edit";
    public static final String ExpertiseRequirementCreate = ExpertiseRequirement + "/create";
    public static final String ExpertiseRequirementUpdate = ExpertiseRequirement + "/update";


    private static final String Activity = Prefix + "/activity";

    public static final String ActivityList = Activity;
    public static final String ActivityListAjax = Activity + "/ajax_list";
    public static final String ActivityNew = Activity + "/new";
    public static final String ActivityEdit = Activity + "/edit";
    public static final String ActivityCreate = Activity + "/create";
    public static final String ActivityUpdate = Activity + "/update";


    private static final String MinWage = Prefix + "/min_wage";

    public static final String MinWageList = MinWage;
    public static final String MinWageListAjax = MinWage + "/ajax_list";
    public static final String MinWageNew = MinWage + "/new";
    public static final String MinWageEdit = MinWage + "/edit";
    public static final String MinWageCreate = MinWage + "/create";
    public static final String MinWageUpdate = MinWage + "/update";
}
