package uz.maroqand.ecology.cabinet.controller.mgmt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.expertise.Requirement;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.expertise.MaterialService;
import uz.maroqand.ecology.core.service.expertise.ObjectExpertiseService;
import uz.maroqand.ecology.core.service.expertise.RequirementService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class ExpertiseRequirementController {

    private final RequirementService requirementService;
    private final HelperService helperService;
    private final ObjectExpertiseService objectExpertiseService;
    private final MaterialService materialService;
    private final OrganizationService organizationService;
    private final TableHistoryService tableHistoryService;
    private final UserService userService;
    private final Gson gson;

    @Autowired
    public ExpertiseRequirementController(RequirementService requirementService, HelperService helperService, ObjectExpertiseService objectExpertiseService, MaterialService materialService, OrganizationService organizationService, TableHistoryService tableHistoryService, UserService userService, Gson gson) {
        this.requirementService = requirementService;
        this.helperService = helperService;
        this.objectExpertiseService = objectExpertiseService;
        this.materialService = materialService;
        this.organizationService = organizationService;
        this.tableHistoryService = tableHistoryService;
        this.userService = userService;
        this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    @RequestMapping(value = MgmtUrls.ExpertiseRequirementList,method = RequestMethod.GET)
    public String getList(Model model ){

        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("materialList",materialService.getList());
        model.addAttribute("categoryList",Category.getCategoryList());
        model.addAttribute("organizationList",organizationService.getList());
        return MgmtTemplates.ExpertiseRequirementList;}

    @RequestMapping(value = MgmtUrls.ExpertiseRequirementList,method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object>  getAjaxList(
            @RequestParam(name = "id",required = false) Integer id,
            @RequestParam(name = "objectExpertiseId",required = false) Integer objectExpertiseId,
            @RequestParam(name = "category",required = false) Category category,
            @RequestParam(name = "materialId",required = false) Integer materialId,
            @RequestParam(name = "reviewId",required = false) Integer reviewId,
            @RequestParam(name = "qty",required = false) Double qty,
            @RequestParam(name = "deadline",required = false) Integer deadline,
            Pageable pageable
    ){
        HashMap<String, Object> result = new HashMap<>();
        Page<Requirement> requirementPage = requirementService.findFiltered(id,objectExpertiseId,category,materialId,reviewId,qty,deadline,pageable);

        result.put("recordsTotal", requirementPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", requirementPage.getTotalElements()); //Filtered elements

        List<Requirement> requirementList = requirementPage.getContent();
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        List<Object[]> convenientForJSONArray = new ArrayList<>(requirementList.size());

        for(Requirement requirement: requirementList) {
            convenientForJSONArray.add(new Object[]{
                    requirement.getId(),
                    requirement.getObjectExpertiseId()!=null?helperService.getObjectExpertise(requirement.getObjectExpertiseId(),locale):"",
                    requirement.getCategory()!=null?helperService.getCategory(requirement.getCategory().getId(),locale):"",
                    requirement.getMaterialId()!=null?helperService.getMaterial(requirement.getMaterialId(),locale):"",
                    requirement.getReviewId()!=null?helperService.getOrganizationName(requirement.getReviewId(),locale):"",
                    requirement.getQty(),
                    requirement.getDeadline()
            });
        }
        result.put("data", convenientForJSONArray);

        return result;
    }

    @RequestMapping(value = MgmtUrls.ExpertiseRequirementNew)
    public String expertiseRequirementNew(Model model){
        Requirement requirement = new Requirement();

        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("materialList",materialService.getList());
        model.addAttribute("organizationList",organizationService.getList());
        model.addAttribute("categoryList",Category.getCategoryList());
        model.addAttribute("requirement",requirement);
        model.addAttribute("action_url",MgmtUrls.ExpertiseRequirementCreate);
        model.addAttribute("back_url",MgmtUrls.ExpertiseRequirementList);

        return MgmtTemplates.ExpertiseRequirementNew;
    }

    @RequestMapping(value = MgmtUrls.ExpertiseRequirementCreate)
    public String expertiseRequirementCreate(
            @RequestParam(name = "categoryId") Integer categoryId,
            Requirement requirement
    ){
        User user = userService.getCurrentUserFromContext();
        Requirement requirement1 = new Requirement();
        requirement1.setObjectExpertiseId(requirement.getObjectExpertiseId());
        requirement1.setCategory(Category.getCategory(categoryId));
        requirement1.setMaterialId(requirement.getMaterialId());
        requirement1.setReviewId(requirement.getReviewId());
        requirement1.setQty(requirement.getQty());
        requirement1.setDeadline(requirement.getDeadline());
        requirementService.save(requirement1);

        tableHistoryService.create(
                TableHistoryType.add,
                TableHistoryEntity.Requirement,
                requirement1.getId(),
                null,
                gson.toJson(requirement1),
                "",
                user.getId(),
                user.getUserAdditionalId()
                );

        return "redirect:" + MgmtUrls.ExpertiseRequirementList;
    }

    @RequestMapping(value = MgmtUrls.ExpertiseRequirementEdit)
    public String expertiseRequirementEdit(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        Requirement requirement = requirementService.getById(id);
        if (requirement==null){
            return "redirect:" + MgmtUrls.ExpertiseRequirementList;
        }
        model.addAttribute("objectExpertiseList",objectExpertiseService.getList());
        model.addAttribute("materialList",materialService.getList());
        model.addAttribute("organizationList",organizationService.getList());
        model.addAttribute("categoryList",Category.getCategoryList());
        model.addAttribute("requirement",requirement);
        model.addAttribute("action_url",MgmtUrls.ExpertiseRequirementUpdate);
        model.addAttribute("back_url",MgmtUrls.ExpertiseRequirementList);

        return MgmtTemplates.ExpertiseRequirementNew;
    }

    @RequestMapping(value = MgmtUrls.ExpertiseRequirementUpdate)
    public String objectExpertiseUpdate(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "categoryId") Integer categoryId,
            Requirement requirement
    ){
        User user = userService.getCurrentUserFromContext();
        Requirement requirement1 = requirementService.getById(id);
        if (requirement1==null){
            return "redirect:" + MgmtUrls.ExpertiseRequirementList;
        }
        String oldRequirement = gson.toJson(requirement1);
        System.out.println(categoryId);
        requirement1.setObjectExpertiseId(requirement.getObjectExpertiseId());
        requirement1.setCategory(Category.getCategory(categoryId));
        requirement1.setMaterialId(requirement.getMaterialId());
        requirement1.setReviewId(requirement.getReviewId());
        requirement1.setQty(requirement.getQty());
        requirement1.setDeadline(requirement.getDeadline());
        requirementService.save(requirement1);

        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.Requirement,
                requirement1.getId(),
                oldRequirement,
                gson.toJson(requirement1),
                "",
                user.getId(),
                user.getUserAdditionalId()
        );

        return "redirect:" + MgmtUrls.ExpertiseRequirementList;
    }

}
